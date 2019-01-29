package com.ji.tree.utils;

import android.util.Log;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, tag + " - " + msg);
        }
    }

    public static void d(String tag, String msg) {
        Log.d(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        Log.e(TAG, tag + " - " + msg, e);
    }
}
