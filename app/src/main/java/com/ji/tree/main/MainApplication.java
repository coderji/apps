package com.ji.tree.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

public class MainApplication extends Application {
    private static ArrayList<Activity> sResumedList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                sResumedList.add(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                sResumedList.remove(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static boolean isResumed(Context context) {
        return context instanceof Activity && sResumedList.contains(context);
    }
}
