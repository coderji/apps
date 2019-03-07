package com.ji.tree.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ji.tree.R;
import com.ji.tree.app.AppFragment;
import com.ji.tree.app.AppPresenter;
import com.ji.tree.app.tencent.TencentRepository;
import com.ji.utils.CrashUtils;
import com.ji.utils.DiskUtils;
import com.ji.utils.ImageUtils;
import com.ji.utils.LogUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private Fragment mTopFragment, mAppFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        LogUtils.v(TAG, "attachBaseContext");

        DiskUtils.initCacheDir(newBase);
        CrashUtils.initUncaughtExceptionHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "onCreate");
        ImageUtils.create();
        setContentView(R.layout.main_activity);

        setupFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy");
        ImageUtils.destroy();
    }

    private void setupFragment() {
        findViewById(R.id.main_btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTopFragment instanceof AppFragment) {
                    LogUtils.v(TAG, "mTopFragment is AppFragment");
                } else {
                    setAppFragment();
                }
            }
        });

        setAppFragment();
    }

    private void setAppFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        final String tag = "AppFragment";
        mAppFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (mAppFragment == null) {
            LogUtils.d(TAG, "new AppFragment");
            mAppFragment = new AppFragment();
            new AppPresenter((AppFragment) mAppFragment, new TencentRepository());
            fragmentTransaction.add(R.id.main_content, mAppFragment, tag);
            fragmentTransaction.commit();
        } else if (mAppFragment != mTopFragment) {
            fragmentTransaction.show(mAppFragment);
            fragmentTransaction.commit();
            mTopFragment = mAppFragment;
        }
    }
}
