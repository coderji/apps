package com.ji.tree.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ji.tree.R;
import com.ji.tree.app.AppFragment;
import com.ji.tree.app.AppPresenter;
import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.gan.daily.DailyFragment;
import com.ji.tree.gan.daily.DailyPresenter;
import com.ji.tree.gan.GanRepository;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setupFragment();
    }

    private void setupFragment() {
        DailyFragment dailyFragment = (DailyFragment) getSupportFragmentManager().findFragmentByTag("DailyFragment");
        if (dailyFragment == null) {
            dailyFragment = new DailyFragment();
        }
        new DailyPresenter(dailyFragment, new GanRepository(getApplicationContext()));

        final DailyFragment finalDailyFragment = dailyFragment;
        findViewById(R.id.main_btn_gan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, finalDailyFragment, "DailyFragment")
                        .commit();
            }
        });

        AppFragment appFragment = (AppFragment) getSupportFragmentManager().findFragmentByTag("AppFragment");
        if (appFragment == null) {
            appFragment = new AppFragment();
        }
        new AppPresenter(appFragment, new TencentRepository(getApplicationContext()));

        final AppFragment finalAppFragment = appFragment;
        findViewById(R.id.main_btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, finalAppFragment, "AppFragment")
                        .commit();
            }
        });
    }
}
