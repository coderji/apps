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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AppDownloadService extends Service {
    private static final String TAG = "AppDownloadService";
    private static final int START_STATE = 1, NONE_STATE = 2;
    private static final int START_MAX = 1;
    private ArrayList<AppData> mStartList = new ArrayList<>();
    private ArrayList<AppData> mWaitingList = new ArrayList<>();
    private HashMap<String, Button> mButtonMap = new HashMap<>();

    public class DownloadBinder extends Binder {
        public void with(final Button button, final AppData data) {
            if (mButtonMap.containsKey(data.packageName)) {
                mButtonMap.put(data.packageName, button);
            }
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

                                downloadApp(button, waiting);
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

                        downloadApp(button, data);
                    } else {
                        mWaitingList.add(data);
                        button.setText(R.string.waiting);
                    }
                }
            });
        }

        private String getStateString(final AppData data) {
            for (AppData wait : mWaitingList) {
                if (wait.equals(data)) {
                    return getString(R.string.waiting);
                }
            }
            return getString(R.string.download);
        }

        private void downloadApp(final Button button, final AppData data) {
            ThreadUtils.workExecute(new Runnable() {
                @Override
                public void run() {
                    LogUtils.v(TAG, "downloadApp begin button:" + Integer.toHexString(System.identityHashCode(button)) + " data:" + data.packageName);
                    mButtonMap.put(data.packageName, button);

                    try {
                        RandomAccessFile raf = new RandomAccessFile(
                                DiskUtils.getAppCacheDir() + File.separator + DiskUtils.addressToPath(data.apkUrl),
                                "rwd");
                        LogUtils.v(TAG, "raf.length:" + raf.length());
                        data.downloadSize = raf.length();
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
                            while (data.state == START_STATE && (length = is.read(buffer)) != -1) {
try {Thread.sleep(3000); } catch (Exception e) {}
                                raf.write(buffer, 0, length);
                                data.downloadSize += length;

                                final Button b = mButtonMap.get(data.packageName);
                                LogUtils.v(TAG, "--" + data.packageName + " " + b);
                                if (b != null) {
                                    if (data.packageName.equals(b.getTag())) {
                                        ThreadUtils.uiExecute(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (data.packageName.equals(b.getTag())) {
                                                    b.setText(CommonUtils.byte2FitMemorySize(data.downloadSize));
                                                }
                                            }
                                        });
                                    } else {
                                        mButtonMap.remove(data.packageName);
                                    }
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

                                        downloadApp(button, waiting);
                                    }

                                    return;
                                }
                            }
                        }
                    } catch (IOException e) {
                        LogUtils.e(TAG, "downloadApp apkUrl:" + data.apkUrl, e);
                    }

                    mButtonMap.remove(data.packageName);
                    LogUtils.v(TAG, "downloadApp end button:" + Integer.toHexString(System.identityHashCode(button)) + " data:" + data.packageName);
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
}
