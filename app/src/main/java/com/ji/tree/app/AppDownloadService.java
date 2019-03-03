package com.ji.tree.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.ArrayMap;
import android.widget.Button;

import com.ji.tree.R;
import com.ji.tree.app.local.AppData;
import com.ji.tree.utils.InternetUtils;
import com.ji.tree.utils.LogUtils;
import com.ji.tree.utils.StorageUtils;
import com.ji.tree.utils.WorkUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppDownloadService extends Service {
    private static final String TAG = "AppDownloadService";
    public static final int STATE_START = 1, STATE_STOP = 2, STATE_FINISH = 3, STATE_WAIT = 4;
    private ArrayMap<String, AppData> mDownloadingMap = new ArrayMap<>();
    private ArrayMap<String, AppData> mWaitingMap = new ArrayMap<>();
    private ArrayMap<String, Button> mButtonMap = new ArrayMap<>();

    public class DownloadBinder extends Binder {
        public void click(final AppData data) {
            LogUtils.v(TAG, "click data:" + data);
            WorkUtils.workExecute(new Runnable() {
                @Override
                public void run() {
                    AppData appData;
                    if (mDownloadingMap.containsKey(data.apkUrl)) {
                        appData = mDownloadingMap.get(data.apkUrl);
                        appData.state = STATE_STOP;
                        mDownloadingMap.remove(appData.apkUrl);

                        if (!mWaitingMap.isEmpty()) {
                            AppData nextData = mWaitingMap.removeAt(0);
                            nextData.state = STATE_START;
                            mDownloadingMap.put(nextData.apkUrl, nextData);
                            downloadApp(nextData);
                        }
                    } else {
                        appData = data;
                        if (mDownloadingMap.size() >= 2) {
                            if (mWaitingMap.containsKey(appData.apkUrl)) {
                                mWaitingMap.remove(appData.apkUrl);
                            } else {
                                mWaitingMap.put(appData.apkUrl, appData);
                            }
                        } else {
                            appData.state = STATE_START;
                            mDownloadingMap.put(appData.apkUrl, appData);
                            downloadApp(appData);
                        }
                    }
                }
            });
        }

        public void longClick(final AppData data) {

        }

        public void registerUrl(AppData data, Button button) {
            mButtonMap.put(data.apkUrl, button);
        }

        public void unregisterUrl(AppData data) {
            mButtonMap.remove(data.apkUrl);
        }

        public boolean isDownloading() {
            return !mDownloadingMap.isEmpty();
        }

        public String getDisplayString(final AppData data) {
            AppData appData = mDownloadingMap.get(data.apkUrl);
            if (appData != null) {
                if (appData.state == STATE_START) {
                    return String.valueOf(appData.downloadSize);
                }
            }
            return getString(R.string.download);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "onCreate " + this + " mDownloadingMap:" + mDownloadingMap);
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

    private void downloadApp(final AppData data) {
        try {
            RandomAccessFile raf = new RandomAccessFile(
                    StorageUtils.getAppCacheDir() + File.separator + InternetUtils.format(data.apkUrl),
                    "rwd");
            LogUtils.v(TAG, "raf.length:" + raf.length());
            URL url = new URL(data.apkUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int contentLength = connection.getHeaderFieldInt("Content-Length", 0);
                LogUtils.v(TAG, "downloadApp contentLength:" + contentLength);
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int length;
                raf.seek(data.downloadSize);
                while (data.state == STATE_START && (length = is.read(buffer)) != -1) {
                    raf.write(buffer, 0, length);
                    data.downloadSize += length;
                    Button button = mButtonMap.get(data.apkUrl);
//                    if (button != null && MainApplication.isResumed(button.getContext())) {
//                        WorkUtils.uiExecute(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mButtonMap.get(data.apkUrl) != null) {
//                                    mButtonMap.get(data.apkUrl).setText(String.valueOf(data.downloadSize));
//                                }
//                            }
//                        });
//                    }
                }
                raf.close();
                is.close();
                mDownloadingMap.remove(data.apkUrl);
                if (!mWaitingMap.isEmpty()) {
                    AppData nextData = mWaitingMap.removeAt(0);
                    nextData.state = STATE_START;
                    mDownloadingMap.put(nextData.apkUrl, nextData);
                    downloadApp(nextData);
                }
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "getApp apkUrl:" + data.apkUrl, e);
        }
    }
}
