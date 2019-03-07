package com.ji.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.Locale;

import androidx.core.content.FileProvider;

public class CommonUtils {
    private static final String TAG = "CommonUtils";

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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            String authority = "com.ji.tree.FileProvider";
            data = FileProvider.getUriForFile(context, authority, file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.grantUriPermission(context.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, type);
        context.startActivity(intent);
    }
}
