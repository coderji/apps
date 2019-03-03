package com.ji.tree.app;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.ji.tree.utils.LogUtils;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AppsJobService extends JobService {
    private static final String TAG = "AppsJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtils.v(TAG, "onStartJob");

        AppsReceiver.startJob(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.v(TAG, "onStopJob");
        return false;
    }
}
