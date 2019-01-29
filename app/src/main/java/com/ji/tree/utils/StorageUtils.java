package com.ji.tree.utils;

import android.content.Context;

import java.io.File;

public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static String sExternalCacheDir;
    private static String sImageCacheDir;
    private static String sCrashCacheDir;
    private static String sAppCacheDir;

    public static String initCacheDir(Context context) {
        if (sExternalCacheDir == null) {
            if (context.getExternalCacheDir() != null) {
                sExternalCacheDir = context.getExternalCacheDir().getPath();

                File dir;
                sImageCacheDir = sExternalCacheDir + File.separator + "image";
                dir = new File(sImageCacheDir);
                if (dir.exists() || dir.mkdir()) {
                    LogUtils.v(TAG, "sImageCacheDir");
                }

                sCrashCacheDir = sExternalCacheDir + File.separator + "crash";
                dir = new File(sCrashCacheDir);
                if (dir.exists() || dir.mkdir()) {
                    LogUtils.v(TAG, "sCrashCacheDir");
                }

                sAppCacheDir = sExternalCacheDir + File.separator + "app";
                dir = new File(sAppCacheDir);
                if (dir.exists() || dir.mkdir()) {
                    LogUtils.v(TAG, "sAppCacheDir");
                }
            }
        }
        return sExternalCacheDir;
    }

    public static String getImageCacheDir() {
        return sImageCacheDir;
    }

    public static String getCrashCacheDir() {
        return sCrashCacheDir;
    }

    public static String getAppCacheDir() {
        return sAppCacheDir;
    }
}
