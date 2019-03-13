package com.ji.tree.app.tencent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.ji.tree.app.local.AppData;
import com.ji.utils.InternetUtils;
import com.ji.utils.JsonUtils;
import com.ji.utils.LogUtils;
import com.ji.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

public class TencentRepository {
    private static String TAG = "TencentRepository";

    // Top
    private List<AppData> mTopAppList = new ArrayList<>();
    private int mPageNo = 0;
    private final static int PAGE_SIZE = 10;

    public static List<AppData> getTopList(int pageNo, int pageSize) {
        String s = InternetUtils.getString(
                "https://mapp.qzone.qq.com/cgi-bin/mapp/mapp_applist?apptype=soft_top&platform=touch"
                        + "&pageNo=" + pageNo
                        + "&pageSize=" + pageSize);
        TopApps topAppData = (TopApps) JsonUtils.parse(s, TopApps.class);
        if (topAppData != null) {
            return topAppData.getApps();
        }
        LogUtils.e(TAG, "getTopList pageNo:" + pageNo + " pageSize:" + pageSize);
        return null;
    }

    public interface TopCallback {
        void onTop(List<AppData> list);
    }

    public void getTop(final TopCallback callback) {
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                List<AppData> list = getTopList(mPageNo++, PAGE_SIZE);
                if (list != null) {
                    mTopAppList.addAll(list);
                    ThreadUtils.uiExecute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTop(mTopAppList);
                        }
                    });
                }
            }
        });
    }

    public static List<AppData> getAppList(String kw, String pns, String sid) {
        String s = InternetUtils.getString(
                "https://sj.qq.com/myapp/searchAjax.htm?"
                        + "&kw=" + kw
                        + "&pns=" + pns
                        + "&sid=" + sid);
        SearchApps searchAppData = (SearchApps) JsonUtils.parse(s, SearchApps.class);
        if (searchAppData != null) {
            return searchAppData.getApps();
        }
        return null;
    }

    public interface UpdateCallback {
        void onUpdate(List<AppData> list);
    }

    public void getUpdate(final Context context, final UpdateCallback callback) {
        ThreadUtils.workExecute(new Runnable() {
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
                        if (Build.VERSION.SDK_INT >= 28) {
                            appData.versionCode = info.getLongVersionCode();
                        } else {
                            appData.versionCode = info.versionCode;
                        }

                        List<AppData> list = getAppList(appData.name, null, null);
                        if (list != null && list.get(0).packageName.equals(appData.packageName)) {
                            if (appData.versionCode < list.get(0).versionCode) {
                                appList.add(list.get(0));
                            }
                        }
                    }
                }
                ThreadUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onUpdate(appList);
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        LogUtils.v(TAG, "getTopList " + getTopList(1 , 2));
    }
}
