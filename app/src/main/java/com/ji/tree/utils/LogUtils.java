package com.ji.tree.utils;

import android.util.Log;

import com.ji.tree.BuildConfig;

public class LogUtils {
    private static final String TAG = "Tree";
    private static boolean sPhone = !"Windows_NT".equals(System.getenv("OS"));

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            if (sPhone) {
                Log.v(TAG, tag + " - " + msg);
            } else {
                System.out.println(TAG + " " + tag + " - " + msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (sPhone) {
            Log.d(TAG, tag + " - " + msg);
        } else {
            System.out.println(TAG + " " + tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (sPhone) {
            Log.e(TAG, tag + " - " + msg);
        } else {
            System.out.println(TAG + " " + tag + " - " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (sPhone) {
            Log.e(TAG, tag + " - " + msg, e);
        } else {
            System.out.println(TAG + " " + tag + " - " + msg);
            e.printStackTrace();
        }
    }
}
