package com.ji.tree.utils;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class DiskCacheUtils {
    private static final String TAG = "DiskCacheUtils";

    static File getFile(Context context, String path) {
        File cacheFile = new File(context.getExternalCacheDir() + File.separator + CommonUtils.encryptMD5ToString(path));
        if (cacheFile.exists()) {
            return cacheFile;
        }
        return null;
    }

    static InputStream getInputStream(Context context, String path) {
        File cacheFile = new File(context.getExternalCacheDir() + File.separator + CommonUtils.encryptMD5ToString(path));
        if (cacheFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(cacheFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                InputStream is = new ByteArrayInputStream(byteArray);
                byteArrayOutputStream.close();
                fis.close();
                return is;
            } catch (IOException e) {
                LogUtils.e(TAG, "get path:" + path, e);
            }
        }
        return null;
    }

    static void put(Context context, String path, InputStream is) {
        File cacheFile = new File(context.getExternalCacheDir() + File.separator + CommonUtils.encryptMD5ToString(path));
        if (!cacheFile.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cacheFile);
                byte buffer[] = new byte[1024 * 4];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                fos.getFD().sync();
                fos.close();
            } catch (IOException e) {
                LogUtils.e(TAG, "set path:" + path, e);
            }
        }
    }
}
