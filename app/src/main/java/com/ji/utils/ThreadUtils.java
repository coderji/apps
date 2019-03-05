package com.ji.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
    private static final String TAG = "ThreadUtils";
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void workExecute(Runnable runnable) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static void uiExecute(Runnable runnable) {
        sHandler.post(runnable);
    }
}
