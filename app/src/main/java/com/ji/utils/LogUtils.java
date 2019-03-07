package com.ji.utils;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";
    public static boolean sPhone = true;
    static {
        try {
            android.util.Log.v(TAG, "isPhone");
        } catch (Exception e) {
            sPhone = false;
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            if (sPhone) {
                android.util.Log.v(TAG, tag + " - " + msg);
            } else {
                com.ji.android.util.Log.v(TAG, tag + " - " + msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (sPhone) {
            android.util.Log.d(TAG, tag + " - " + msg);
        } else {
            com.ji.android.util.Log.d(TAG, tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (sPhone) {
            android.util.Log.e(TAG, tag + " - " + msg);
        } else {
            com.ji.android.util.Log.e(TAG, tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sPhone) {
            android.util.Log.e(TAG, tag + " - " + msg, tr);
        } else {
            com.ji.android.util.Log.e(TAG, tag + " - " + msg, tr);
        }
    }
}
