package com.ji.tree.utils;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, tag + " - " + msg);
        }
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        android.util.Log.e(TAG, tag + " - " + msg, e);
    }
}
