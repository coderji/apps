package com.ji.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ji.app.local.AppData;
import com.ji.app.local.AppProvider;
import com.ji.utils.CommonUtils;
import com.ji.utils.DiskUtils;
import com.ji.utils.LogUtils;
import com.ji.utils.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.collection.ArrayMap;

public class AppDownloadService extends Service {
    private static final String TAG = "AppDownloadService";
    private static final int START_STATE = 1, NONE_STATE = 2;
    private static final int START_MAX = 2;
    private ArrayMap<String, AppData> mStartMap = new ArrayMap<>();
    private ArrayMap<String, AppData> mWaitingMap = new ArrayMap<>();
    private ArrayMap<String, WeakReference<Button>> mButtonMap = new ArrayMap<>();
    private static final int NOTIFICATION_ID = 1;
    private ArrayMap<String, AppData> mInstallMap = new ArrayMap<>();

    public class DownloadBinder extends Binder {
        public void with(final Button button, final AppData data) {
            button.setTag(data.packageName);
            button.setText(getStateString(data));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mStartMap.containsKey(data.packageName)) {
                        AppData start = mStartMap.get(data.packageName);
                        start.state = NONE_STATE; // stop download
                        mStartMap.remove(start);

                        if (!mWaitingMap.isEmpty()) {
                            AppData waiting = mWaitingMap.removeAt(0);
                            waiting.state = START_STATE;
                            mStartMap.put(waiting.packageName, waiting);
                            mButtonMap.put(data.packageName, new WeakReference<>(button));
                            downloadApp(waiting);
                        }
                    } else if (mWaitingMap.containsKey(data.packageName)) {
                        AppData waiting = mWaitingMap.get(data.packageName);
                        mWaitingMap.remove(waiting);
                        button.setText(R.string.install);
                    } else if (mInstallMap.containsKey(data.packageName)
                            && data.versionCode <= mInstallMap.get(data.packageName).versionCode) {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(data.packageName);
                        startActivity(intent);
                    } else if (mStartMap.size() < START_MAX) {
                        data.state = START_STATE;
                        mStartMap.put(data.packageName, data);
                        mButtonMap.put(data.packageName, new WeakReference<>(button));
                        downloadApp(data);
                    } else {
                        mWaitingMap.put(data.packageName, data);
                        mButtonMap.put(data.packageName, new WeakReference<>(button));
                        button.setText(R.string.waiting);
                    }
                }
            });

            if (mStartMap.containsKey(data.packageName)) {
                mButtonMap.put(data.packageName, new WeakReference<>(button));
            }
        }

        private String getStateString(final AppData data) {
            if (mWaitingMap.containsKey(data.packageName)) {
                return getString(R.string.waiting);
            } else if (mInstallMap.containsKey(data.packageName)) {
                if (data.versionCode > mInstallMap.get(data.packageName).versionCode) {
                    return getString(R.string.update);
                } else {
                    return getString(R.string.open);
                }
            } else {
                String apkPath = DiskUtils.getAppCacheDir() + File.separator + data.packageName + "_" + data.versionCode + ".apk";
                File file = new File(apkPath);
                if (file.exists()) {
                    data.downloadSize = file.length();
                    return CommonUtils.byte2FitMemorySize(data.downloadSize);
                }
                return getString(R.string.install);
            }
        }

        private void downloadApp(final AppData data) {
            ThreadUtils.workExecute(new Runnable() {
                @Override
                public void run() {
                    LogUtils.v(TAG, "downloadApp begin " + data.packageName);
                    onStartDownload(data);

                    try {
                        String apkPath = DiskUtils.getAppCacheDir() + File.separator + data.packageName + "_" + data.versionCode + ".apk";
                        RandomAccessFile raf = new RandomAccessFile(apkPath, "rwd");
                        URL url = new URL(data.apkUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Range", "bytes=" + data.downloadSize + "-");
                        int responseCode = connection.getResponseCode();
                        LogUtils.v(TAG, "responseCode:" + responseCode);
                        if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                            int contentLength = connection.getHeaderFieldInt("Content-Length", 0);
                            LogUtils.v(TAG, "downloadApp contentLength:" + contentLength);
                            data.fileSize = contentLength;
                            InputStream is = connection.getInputStream();
                            byte[] buffer = new byte[1024];
                            int length;
                            raf.seek(data.downloadSize);
                            while (data.state == START_STATE && (length = is.read(buffer)) != -1) {
                                raf.write(buffer, 0, length);
                                data.downloadSize += length;

                                WeakReference<Button> button = mButtonMap.get(data.packageName);
                                final Button b = button != null ? button.get() : null;
                                if (b != null && data.packageName.equals(b.getTag())) {
                                    ThreadUtils.uiExecute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ((data.packageName.equals(b.getTag()))) {
                                                b.setText(CommonUtils.byte2FitMemorySize(data.downloadSize));
                                            }
                                        }
                                    });
                                }
                            }
                            raf.close();
                            is.close();

                            mStartMap.remove(data.packageName);
                            if (!mWaitingMap.isEmpty()) {
                                AppData waiting = mWaitingMap.removeAt(0);
                                waiting.state = START_STATE;
                                mStartMap.put(waiting.packageName, waiting);

                                downloadApp(waiting);
                            }
                            if (data.state == START_STATE) {
                                CommonUtils.installApp(getApplicationContext(), new File(apkPath));
                            }
                            mButtonMap.remove(data.packageName);
                        } else if (responseCode == 416) { // 416
                            ThreadUtils.uiExecute(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "responseCode 416", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        LogUtils.e(TAG, "downloadApp apkUrl:" + data.apkUrl, e);
                    }

                    onStopDownload(data);
                    LogUtils.v(TAG, "downloadApp end " + data.packageName);
                }
            });
        }

        public List<AppData> getAppList() {
            ArrayList<AppData> appList = new ArrayList<>();
            appList.addAll(mStartMap.values());
            appList.addAll(mWaitingMap.values());
            return appList;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "onCreate " + this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initInstallApps();
            LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
            if (launcherApps != null) {
                launcherApps.registerCallback(mAppsCallback);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy " + this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
            if (launcherApps != null) {
                launcherApps.unregisterCallback(mAppsCallback);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.v(TAG, "onBind " + this);
        return new DownloadBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LogUtils.v(TAG, "onRebind " + this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.v(TAG, "onUnbind " + this);
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.v(TAG, "onStartCommand " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    private void onStartDownload(AppData data) {
        Intent intent = new Intent(getApplicationContext(), AppDownloadService.class);
        if (mStartMap.size() == 1) {
            startService(intent);
        }

        sendNotify(data.name);

        ContentValues contentValues = new ContentValues();
        contentValues.put(AppProvider.Columns.DATA_PACKAGE_NAME, data.packageName);
        Cursor cursor = getApplicationContext().getContentResolver().query(AppProvider.TABLE_URI,
                new String[]{AppProvider.Columns.DATA_PACKAGE_NAME},
                AppProvider.Columns.DATA_PACKAGE_NAME + " = ?",
                new String[]{data.packageName},
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                LogUtils.v(TAG, "not need insert");
            } else {
                getApplicationContext().getContentResolver().insert(AppProvider.TABLE_URI, contentValues);
            }
            cursor.close();
        }
    }

    private void onStopDownload(AppData data) {
        getApplicationContext().getContentResolver().delete(AppProvider.TABLE_URI,
                AppProvider.Columns.DATA_PACKAGE_NAME + " = ?",
                new String[]{data.packageName});

        if (mStartMap.isEmpty()) {
            cancelNotify();

            stopSelf();
        }
    }

    private void sendNotify(String name) {
        LogUtils.v(TAG, "sendNotify");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setContentText(getString(R.string.download) + " " + name);
                builder.setSmallIcon(R.mipmap.ic_launcher);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel =
                            new NotificationChannel(getPackageName(), getString(R.string.download), NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                    builder.setChannelId(notificationChannel.getId());
                }
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } else {
                Notification notification = new Notification();
                notification.tickerText = getString(R.string.download) + " " + name;
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        }
    }

    private void cancelNotify() {
        LogUtils.v(TAG, "cancelNotify");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public void initInstallApps() {
        LogUtils.v(TAG, "initInstallApps begin");
        List<PackageInfo> infoList = getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : infoList) {
            AppData appData = new AppData();
            if (Build.VERSION.SDK_INT >= 28) {
                appData.versionCode = info.getLongVersionCode();
            } else {
                appData.versionCode = info.versionCode;
            }
            mInstallMap.put(info.packageName, appData);
        }
        LogUtils.v(TAG, "initInstallApps end mInstallMap:" + mInstallMap);
    }

    LauncherApps.Callback mAppsCallback = new LauncherApps.Callback() {
        @Override
        public void onPackageRemoved(String packageName, UserHandle user) {
            mInstallMap.remove(packageName);
        }

        @Override
        public void onPackageAdded(String packageName, UserHandle user) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                AppData appData = new AppData();
                if (Build.VERSION.SDK_INT >= 28) {
                    appData.versionCode = packageInfo.getLongVersionCode();
                } else {
                    appData.versionCode = packageInfo.versionCode;
                }
                mInstallMap.put(packageName, appData);
            } catch (PackageManager.NameNotFoundException e) {
                LogUtils.e(TAG, "onPackageAdded packageName:" + packageName, e);
            }
        }

        @Override
        public void onPackageChanged(String packageName, UserHandle user) {

        }

        @Override
        public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {

        }

        @Override
        public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {

        }
    };
}
