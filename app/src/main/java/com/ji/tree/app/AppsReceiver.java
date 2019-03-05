package com.ji.tree.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ji.utils.LogUtils;

public class AppsReceiver extends BroadcastReceiver {
    private static final String TAG = "AppsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.v(TAG, "onReceive intent:" + intent);
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) && intent.getData() != null) {
            String pkg = intent.getData().getSchemeSpecificPart();
//            AppDownload.packageAdd(pkg);
        } else if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction()) && intent.getData() != null) {
            String pkg = intent.getData().getSchemeSpecificPart();
//            AppDownload.packageReplaced(pkg);
        } else if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction()) && intent.getData() != null) {
            String pkg = intent.getData().getSchemeSpecificPart();
//            AppDownload.packageRemoved(pkg);
        } else if ("com.ji.apps.AlarmManager".equals(intent.getAction())) {
            startAlarm(context);
        }
    }

    public static void startAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent(context, AppsReceiver.class).setAction("com.ji.apps.AlarmManager");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, pendingIntent);
            }
        }
    }


    public static void startJob(Context context) {
        JobInfo.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new JobInfo.Builder(0, new ComponentName(context, AppsJobService.class));
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setRequiresCharging(false);
            builder.setRequiresDeviceIdle(false);
            builder.setMinimumLatency(4 * 60 * 1000);
            builder.setOverrideDeadline(5 * 60 * 1000);

            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.schedule(builder.build());
            }
        }
    }
}
