package com.ji.android.util;

public final class Log {
    public static final int VERBOSE = 2;

    public static boolean isLoggable(String tag, int level) {
        return true;
    }

    public static void v(String tag, String msg) {
        System.out.println("V " + msg);
    }

    public static void d(String tag, String msg) {
        System.out.println("D " + msg);
    }

    public static void e(String tag, String msg) {
        System.err.println("E " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        System.err.println("E " + msg);
        tr.printStackTrace();
    }
}
