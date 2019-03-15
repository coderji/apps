package com.ji.utils;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";
    private static boolean sPhone = true;

    static {
        try {
            Class.forName("android.util.Log");
        } catch (Exception e) {
            sPhone = false;
        }
    }

    public static boolean isPhone() {
        return sPhone;
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            if (isPhone()) {
                android.util.Log.v(TAG, tag + " - " + msg);
            } else {
                System.out.println("V " + tag + " - " + msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (isPhone()) {
            android.util.Log.d(TAG, tag + " - " + msg);
        } else {
            System.out.println("D " + tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isPhone()) {
            android.util.Log.e(TAG, tag + " - " + msg);
        } else {
            System.err.println("E " + tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isPhone()) {
            android.util.Log.e(TAG, tag + " - " + msg, tr);
        } else {
            System.err.println("E " + msg);
            tr.printStackTrace();
        }
    }
}
