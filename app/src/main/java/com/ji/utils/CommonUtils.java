package com.ji.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.Locale;

import androidx.core.content.FileProvider;

// https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/README-CN.md
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    // ConvertUtils
    public static String byte2FitMemorySize(final long byteSize) {
        if (byteSize < 0) {
            return "shouldn't be less than zero!";
        } else if (byteSize < MemoryConstants.KB) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteSize);
        } else if (byteSize < MemoryConstants.MB) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteSize / MemoryConstants.KB);
        } else if (byteSize < MemoryConstants.GB) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteSize / MemoryConstants.MB);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteSize / MemoryConstants.GB);
        }
    }

    private final class MemoryConstants {
        static final int KB = 1024;
        static final int MB = 1048576;
        static final int GB = 1073741824;
    }

    // AppUtils
    public static void installApp(final Context context, final String filePath) {
        installApp(context, getFileByPath(filePath));
    }

    private static void installApp(final Context context, final File file) {
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                if (!isFileExists(file)) return;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(getInstallAppIntent(context, file, true));
                    }
                });
            }
        });
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // IntentUtils
    private static Intent getInstallAppIntent(final Context context, final File file, final boolean isNewTask) {
        if (file == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = "com.ji.apps.fileProvider";
            data = FileProvider.getUriForFile(context, authority, file);
        }
        intent.setDataAndType(data, type);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }
}
