package com.ji.tree.app.tencent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ji.tree.app.local.AppData;
import com.ji.tree.utils.WorkUtils;

import java.util.ArrayList;
import java.util.List;

public class TencentRepository {
    private String TAG = "TencentRepository";
    private Context mAppContext;

    public TencentRepository(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public interface UpdateCallback {
        void onUpdate(List<AppData> list);
    }

    public void update(final UpdateCallback callback) {
        PackageManager pm = mAppContext.getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        final ArrayList<AppData> appList = new ArrayList<>(infoList.size());
        for (PackageInfo info : infoList) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                final AppData appData = new AppData();
                appData.name = info.applicationInfo.loadLabel(pm).toString();
                appData.packageName = info.packageName;
                appData.versionCode = info.versionCode;

                SearchAppData searchAppData = TencentApi.search(appData.name);
                        for (int i = 0; i < 5; i++) {
                            SearchAppData.AppDetail appDetail = searchAppData.obj.appDetails.get(i);
                            if (appData.packageName.equals(appDetail.packageName)) {
                                appData.iconUrl = appDetail.iconUrl;
                                if (appData.versionCode < appDetail.versionCode) {
                                    appList.add(appData);
                                }
                                break;
                            }
                        }

            }
        }
        callback.onUpdate(appList);
    }

    public interface TopCallback {
        void onTop(List<AppData> list);
    }

    public void getTop(final TopCallback callback) {
        WorkUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                TopAppData topAppData = TencentApi.softTop(0, 10);
                final ArrayList<AppData> appList = new ArrayList<>(topAppData.app.size());
                for (TopAppData.App app : topAppData.app) {
                    final AppData appData = new AppData();
                    appData.iconUrl = app.iconUrl;
                    appData.name = app.name;
                    appList.add(appData);
                }
                WorkUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onTop(appList);
                    }
                });
            }
        });
    }
}
