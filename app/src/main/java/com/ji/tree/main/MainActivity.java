package com.ji.tree.main;

import android.os.Bundle;
import android.view.View;

import com.ji.tree.R;
import com.ji.tree.app.AppFragment;
import com.ji.tree.app.AppPresenter;
import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.gan.daily.DailyFragment;
import com.ji.tree.gan.daily.DailyPresenter;
import com.ji.tree.gan.GanRepository;
import com.ji.tree.utils.CrashUtils;
import com.ji.tree.utils.InternetUtils;
import com.ji.tree.utils.LogUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private Fragment mTopFragment, mDailyFragment, mAppFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        InternetUtils.init(this);
        CrashUtils.getInstance().init(this);
        setupFragment();
    }

    private void setupFragment() {
        findViewById(R.id.main_btn_gan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTopFragment instanceof DailyFragment) {
                    LogUtils.v(TAG, "mTopFragment is DailyFragment");
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    final String tag = "DailyFragment";
                    mDailyFragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (mDailyFragment == null) {
                        LogUtils.d(TAG, "new DailyFragment");
                        mDailyFragment = new DailyFragment();
                        new DailyPresenter((DailyFragment) mDailyFragment, new GanRepository(getApplicationContext()));
                        fragmentTransaction.add(R.id.main_content, mDailyFragment, tag);
                    } else {
                        fragmentTransaction.show(mDailyFragment);
                    }
                    if (mTopFragment != null) {
                        fragmentTransaction.hide(mTopFragment);
                    }
                    fragmentTransaction.commit();
                    mTopFragment = mDailyFragment;
                }
            }
        });

        findViewById(R.id.main_btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTopFragment instanceof AppFragment) {
                    LogUtils.v(TAG, "mTopFragment is AppFragment");
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    final String tag = "AppFragment";
                    mAppFragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (mAppFragment == null) {
                        LogUtils.d(TAG, "new AppFragment");
                        mAppFragment = new AppFragment();
                        new AppPresenter((AppFragment) mAppFragment, new TencentRepository(getApplicationContext()));
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
        });
    }
}
