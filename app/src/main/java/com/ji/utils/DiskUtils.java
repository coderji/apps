package com.ji.utils;

import android.content.Context;

import java.io.File;

public class DiskUtils {
    private static final String TAG = "DiskUtils";
    private static String sExternalCacheDir;
    private static String sImageCacheDir;
    private static String sCrashCacheDir;
    private static String sAppCacheDir;

    public static void initCacheDir(Context context) {
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

    public static String addressToPath(String s) {
        return s.replace(':', '[').replace('/', ']');
    }

    public static File getFile(String address, String dir) {
        File cacheFile = new File(dir + File.separator + addressToPath(address));
        if (cacheFile.exists()) {
            return cacheFile;
        }
        return null;
    }
}
