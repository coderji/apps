package com.ji.tree.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.ji.tree.R;
import com.ji.tree.app.local.AppData;
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

import androidx.collection.ArrayMap;

public class AppDownloadService extends Service {
    private static final String TAG = "AppDownloadService";
    private static final int START_STATE = 1, NONE_STATE = 2;
    private static final int START_MAX = 2;
    private ArrayList<AppData> mStartList = new ArrayList<>();
    private ArrayList<AppData> mWaitingList = new ArrayList<>();
    private ArrayMap<String, WeakReference<Button>> mButtonMap = new ArrayMap<>();

    public class DownloadBinder extends Binder {
        public void with(final Button button, final AppData data) {
            button.setTag(data.packageName);
            button.setText(getStateString(data));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (AppData start : mStartList) {
                        if (start.equals(data)) {
                            start.state = NONE_STATE;
                            mStartList.remove(start);

                            if (!mWaitingList.isEmpty()) {
                                AppData waiting = mWaitingList.remove(0);
                                waiting.state = START_STATE;
                                mStartList.add(waiting);
                                mButtonMap.put(data.packageName, new WeakReference<>(button));
                                downloadApp(waiting);
                            }

                            return;
                        }
                    }

                    for (AppData waiting : mWaitingList) {
                        if (waiting.equals(data)) {
                            waiting.state = NONE_STATE;
                            mWaitingList.remove(waiting);
                            button.setText(R.string.download);

                            return;
                        }
                    }

                    if (mStartList.size() < START_MAX) {
                        data.state = START_STATE;
                        mStartList.add(data);
                        mButtonMap.put(data.packageName, new WeakReference<>(button));
                        downloadApp(data);
                    } else {
                        mWaitingList.add(data);
                        mButtonMap.put(data.packageName, new WeakReference<>(button));
                        button.setText(R.string.waiting);
                    }
                }
            });

            for (AppData start : mStartList) {
                if (start.equals(data)) {
                    mButtonMap.put(data.packageName, new WeakReference<>(button));
                    break;
                }
            }
        }

        private String getStateString(final AppData data) {
            for (AppData wait : mWaitingList) {
                if (wait.equals(data)) {
                    return getString(R.string.waiting);
                }
            }
            try {
                String apkPath = DiskUtils.getAppCacheDir() + File.separator + data.packageName + "_" + data.versionCode + ".apk";
                RandomAccessFile raf = new RandomAccessFile(apkPath, "rwd");
                long size = raf.length();
                if (size > 0) {
                    return CommonUtils.byte2FitMemorySize(size);
                }
            } catch (IOException e) {
                LogUtils.e(TAG, "getStateString data.packageName:" + data.packageName, e);
            }
            return getString(R.string.download);
        }

        private void downloadApp(final AppData data) {
            ThreadUtils.workExecute(new Runnable() {
                @Override
                public void run() {
                    LogUtils.v(TAG, "downloadApp begin " + data.packageName);
                    Intent intent = new Intent(getApplicationContext(), AppDownloadService.class);
                    if (mStartList.size() == 1) {
                        startService(intent);
                    }
                    try {
                        String apkPath = DiskUtils.getAppCacheDir() + File.separator + data.packageName + "_" + data.versionCode + ".apk";
                        RandomAccessFile raf = new RandomAccessFile(apkPath, "rwd");
                        LogUtils.v(TAG, "raf.length:" + raf.length());
                        data.downloadSize = raf.length();
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

                            for (AppData start : mStartList) {
                                if (start.equals(data)) {
                                    mStartList.remove(start);

                                    if (!mWaitingList.isEmpty()) {
                                        AppData waiting = mWaitingList.remove(0);
                                        waiting.state = START_STATE;
                                        mStartList.add(waiting);

                                        downloadApp(waiting);
                                    }

                                    return;
                                }
                            }

                            if (data.state == START_STATE) {
                                CommonUtils.installApp(getApplicationContext(), new File(apkPath));
                            }
                        }
                    } catch (IOException e) {
                        LogUtils.e(TAG, "downloadApp apkUrl:" + data.apkUrl, e);
                    }
                    mButtonMap.remove(data.packageName);
                    if (mStartList.isEmpty()) {
                        stopSelf();
                    }
                    LogUtils.v(TAG, "downloadApp end " + data.packageName);

                }
            });
        }

        public boolean isDownloading() {
            return !mStartList.isEmpty();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "onCreate " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy " + this);
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
}
