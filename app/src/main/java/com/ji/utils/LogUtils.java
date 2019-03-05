package com.ji.utils;

import com.ji.android.util.Log;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";
    private static boolean sPhone = System.getProperty("os.name") == null;

    public static boolean isPhone() {
        return sPhone;
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            if (isPhone()) {
                Log.v(TAG, tag + " - " + msg);
            } else {
                com.ji.android.util.Log.v(TAG, tag + " - " + msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (isPhone()) {
            Log.d(TAG, tag + " - " + msg);
        } else {
            com.ji.android.util.Log.d(TAG, tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isPhone()) {
            Log.e(TAG, tag + " - " + msg);
        } else {
            com.ji.android.util.Log.e(TAG, tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isPhone()) {
            Log.e(TAG, tag + " - " + msg, tr);
        } else {
            com.ji.android.util.Log.e(TAG, tag + " - " + msg, tr);
        }
    }
}
