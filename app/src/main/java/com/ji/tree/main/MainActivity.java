package com.ji.tree.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ji.tree.R;
import com.ji.tree.app.AppFragment;
import com.ji.tree.app.AppPresenter;
import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.utils.CrashUtils;
import com.ji.tree.utils.LogUtils;
import com.ji.tree.utils.StorageUtils;

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

        StorageUtils.initCacheDir(newBase);
        CrashUtils.initUncaughtExceptionHandler(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "onCreate");
        setContentView(R.layout.main_activity);

        setupFragment();
    }

    private void setupFragment() {
        setAppFragment();

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
        } else {
            fragmentTransaction.show(mAppFragment);
        }
        if (mTopFragment != null) {
            fragmentTransaction.hide(mTopFragment);
        }
        fragmentTransaction.commit();
        mTopFragment = mAppFragment;
    }
}
