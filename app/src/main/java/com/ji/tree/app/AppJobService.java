package com.ji.tree.app;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ji.utils.LogUtils;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AppJobService extends JobService {
    private static final String TAG = "AppJobService";

    public static void startAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent().setAction("com.ji.tree.apps.AlarmManager");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, pendingIntent);
            }
        }
    }

    public static void startJob(Context context) {
        LogUtils.v(TAG, "startJob");
        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(context, AppJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setMinimumLatency(2 * 60 * 1000);
        builder.setOverrideDeadline(3 * 60 * 1000);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtils.v(TAG, "onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.v(TAG, "onStopJob");
        return false;
    }
}
