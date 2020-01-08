package com.ji.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.Locale;

import androidx.core.content.FileProvider;

public class Utils {
    private static final String TAG = "Utils";

    // https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/README-CN.md
    // ConvertUtils
    public static String byte2FitMemorySize(final long byteSize) {
        final int KB = 1024, MB = 1048576, GB = 1073741824;
        if (byteSize < 0) {
            return "shouldn't be less than zero!";
        } else if (byteSize < KB) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteSize);
        } else if (byteSize < MB) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteSize / KB);
        } else if (byteSize < GB) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteSize / MB);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteSize / GB);
        }
    }

    // AppUtils
    public static void installApp(final Context context, final File file) {
        LogUtils.v(TAG, "installApp " + file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        String type = "application/vnd.android.package-archive";
        String authority = "com.ji.app.FileProvider";
        data = FileProvider.getUriForFile(context, authority, file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.grantUriPermission(context.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, type);
        context.startActivity(intent);
    }
}
