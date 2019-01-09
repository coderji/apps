package com.ji.tree.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class WorkUtils {
    private static final String TAG = "WorkUtils";
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void workExecute(Runnable runnable) {
        AsyncTask.execute(runnable);
    }

    public static void uiExecute(Runnable runnable) {
        sHandler.post(runnable);
    }
}
