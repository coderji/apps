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

    public interface UpdateCallback {
        void onUpdate(List<AppData> list);
    }

    public void getUpdate(final Context context, final UpdateCallback callback) {
        WorkUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> infoList = pm.getInstalledPackages(0);
                final ArrayList<AppData> appList = new ArrayList<>(infoList.size());
                for (PackageInfo info : infoList) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        final AppData appData = new AppData();
                        appData.name = info.applicationInfo.loadLabel(pm).toString();
                        appData.packageName = info.packageName;
                        appData.versionCode = info.getLongVersionCode();

                        List<AppData> list = TencentApi.getAppList(appData.name, null, null);
                        if (list != null && list.get(0).packageName.equals(appData.packageName)) {
                            if (appData.versionCode < list.get(0).versionCode) {
                                appList.add(list.get(0));
                            }
                        }
                    }
                }
                WorkUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onUpdate(appList);
                    }
                });
            }
        });
    }

    public interface TopCallback {
        void onTop(List<AppData> list);
    }

    public void getTop(final TopCallback callback) {
        WorkUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                final List<AppData> list = TencentApi.getTopList(0, 3);
                WorkUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onTop(list);
                    }
                });
            }
        });
    }
}
