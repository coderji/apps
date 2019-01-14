package com.ji.tree.utils;

import android.content.Context;

import java.io.File;

public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static String sCacheDir;

    public static String initCacheDir(Context context) {
        if (sCacheDir == null) {
            if (context.getExternalCacheDir() != null) {
                sCacheDir = context.getExternalCacheDir().getPath();
            } else {
                sCacheDir = context.getCacheDir().getPath();
            }
        }
        return sCacheDir;
    }

    public static String getImageCacheDir() {
        File dir = new File(sCacheDir + File.separator + "image");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                LogUtils.v(TAG, "getImageCacheDir mk dir success");
            } else {
                LogUtils.v(TAG, "getImageCacheDir mk dir fail");
            }

        }
        return dir.getPath();
    }

    public static String getCrashCacheDir() {
        File dir = new File(sCacheDir + File.separator + "crash");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                LogUtils.v(TAG, "getCrashCacheDir mk dir success");
            } else {
                LogUtils.v(TAG, "getCrashCacheDir mk dir fail");
            }

        }
        return dir.getPath();
    }
}
