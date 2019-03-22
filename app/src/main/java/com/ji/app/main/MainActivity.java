package com.ji.app.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ji.app.R;
import com.ji.app.AppJobService;
import com.ji.app.mine.MineAppFragment;
import com.ji.app.search.SearchAppFragment;
import com.ji.app.search.SearchAppPresenter;
import com.ji.app.top.TopAppFragment;
import com.ji.app.top.TopAppPresenter;
import com.ji.app.tencent.TencentRepository;
import com.ji.utils.CrashUtils;
import com.ji.utils.DiskUtils;
import com.ji.utils.ImageUtils;
import com.ji.utils.LogUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private Fragment mShowFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        LogUtils.v(TAG, "attachBaseContext");

        DiskUtils.initCacheDir(newBase);
        CrashUtils.initUncaughtExceptionHandler();
        AppJobService.startJob(newBase);
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
                if (mShowFragment instanceof TopAppFragment) {
                    LogUtils.v(TAG, "mShowFragment is TopAppFragment");
                } else {
                    showTopAppFragment();
                }
            }
        });
        findViewById(R.id.main_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowFragment instanceof SearchAppFragment) {
                    LogUtils.v(TAG, "mShowFragment is SearchAppFragment");
                } else {
                    showSearchAppFragment();
                }
            }
        });
        findViewById(R.id.main_btn_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowFragment instanceof MineAppFragment) {
                    LogUtils.v(TAG, "mShowFragment is MineAppFragment");
                } else {
                    showMineAppFragment();
                }
            }
        });

        showTopAppFragment();
    }

    private void showTopAppFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        final String tag = "TopAppFragment";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            LogUtils.d(TAG, "new TopAppFragment");
            fragment = new TopAppFragment();
            new TopAppPresenter((TopAppFragment) fragment, TencentRepository.getInstance());
            fragmentTransaction.add(R.id.main_content, fragment, tag);
            if (mShowFragment != null) {
                fragmentTransaction.hide(mShowFragment);
            }
            fragmentTransaction.commit();
            mShowFragment = fragment;
        } else if (fragment != mShowFragment) {
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(mShowFragment);
            fragmentTransaction.commit();
            mShowFragment = fragment;
        }
    }

    private void showSearchAppFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        final String tag = "SearchAppFragment";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            LogUtils.d(TAG, "new SearchAppFragment");
            fragment = new SearchAppFragment();
            new SearchAppPresenter((SearchAppFragment) fragment, TencentRepository.getInstance());
            fragmentTransaction.add(R.id.main_content, fragment, tag);
            if (mShowFragment != null) {
                fragmentTransaction.hide(mShowFragment);
            }
            fragmentTransaction.commit();
            mShowFragment = fragment;
        } else if (fragment != mShowFragment) {
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(mShowFragment);
            fragmentTransaction.commit();
            mShowFragment = fragment;
        }
    }

    private void showMineAppFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        final String tag = "MineAppFragment";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            LogUtils.d(TAG, "new SearchAppFragment");
            fragment = new MineAppFragment();
            fragmentTransaction.add(R.id.main_content, fragment, tag);
            if (mShowFragment != null) {
                fragmentTransaction.hide(mShowFragment);
            }
            fragmentTransaction.commit();
            mShowFragment = fragment;
        } else if (fragment != mShowFragment) {
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(mShowFragment);
            fragmentTransaction.commit();
            mShowFragment = fragment;
        }
    }
}
