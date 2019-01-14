package com.ji.tree.utils;

import android.content.Context;
import android.os.Build;
import android.os.Process;

import com.ji.tree.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashUtils  {
    private static final String TAG = "CrashUtils";

    public static void initUncaughtExceptionHandler(Context context) {
        final Thread.UncaughtExceptionHandler defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                dumpException(e);
                if (defaultCrashHandler != null) {
                    defaultCrashHandler.uncaughtException(t, e);
                } else {
                    Process.killProcess(Process.myPid());
                }
            }
        });
    }

    private static void dumpException(Throwable e) {
        String time = new SimpleDateFormat("yyyy-MM-dd[HH:mm:ss]", Locale.getDefault()).format(new Date());
        File file = new File(StorageUtils.getCrashCacheDir() + File.separator + time);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.print(BuildConfig.VERSION_NAME);
            pw.print(" ");
            pw.println(BuildConfig.VERSION_CODE);

            pw.print(Build.VERSION.RELEASE);
            pw.print(" ");
            pw.println(Build.VERSION.SDK_INT);

            pw.println(Build.MANUFACTURER);
            pw.println(Build.MODEL);

            e.printStackTrace(pw);
            pw.close();
        } catch (IOException ex) {
            LogUtils.e(TAG, "dumpException", ex);
        }
    }
}
