package com.ji.app.tencent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ji.app.local.AppData;
import com.ji.utils.HttpUtils;
import com.ji.utils.JsonUtils;
import com.ji.utils.LogUtils;
import com.ji.utils.ThreadUtils;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TencentRepository {
    private static String TAG = "TencentRepository";

    private TencentRepository() {
    }

    private static class SingletonInstance {
        private static final TencentRepository INSTANCE = new TencentRepository();
    }

    public static TencentRepository getInstance() {
        return SingletonInstance.INSTANCE;
    }

    // Top
    private List<AppData> mTopAppList = new ArrayList<>();
    private int mPageNo = 1;
    private final static int PAGE_SIZE = 10;

    private TopApps getTopApps() {
        LogUtils.v(TAG, "getTopApps > pageNo:" + mPageNo + " pageSize:" + PAGE_SIZE);
        String s = HttpUtils.getString(
                "https://mapp.qzone.qq.com/cgi-bin/mapp/mapp_applist?apptype=soft_top&platform=touch"
                        + "&pageNo=" + mPageNo++
                        + "&pageSize=" + PAGE_SIZE);
        LogUtils.v(TAG, "getTopApps < " + s);
        return (TopApps) JsonUtils.parse(s, TopApps.class);
    }

    public interface TopCallback {
        void onTop(List<AppData> list, boolean more);
    }

    public void getTop(final TopCallback callback) {
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                final TopApps topApps = getTopApps();
                if (topApps != null) {
                    List<AppData> list = topApps.getApps();
                    mTopAppList.addAll(list);
                    ThreadUtils.uiExecute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTop(mTopAppList, topApps.getNext());
                        }
                    });
                }
            }
        });
    }

    // Search
    private List<AppData> mSearchAppList = new ArrayList<>();
    private String mKW, mPNS, mSID;

    private SearchApps getSearchApps(String kw, int retry) {
        if (mKW == null || !mKW.equals(kw)) {
            mKW = kw;
            mPNS = "";
            mSID = "";
            mSearchAppList.clear();
        }
        LogUtils.v(TAG, "getSearchApps > kw:" + mKW + " pns:" + mPNS + " sid:" + mSID + " retry:" + retry);
        String s = HttpUtils.getString(
                "https://sj.qq.com/myapp/searchAjax.htm?"
                        + "&kw=" + mKW
                        + "&pns=" + mPNS
                        + "&sid=" + mSID);
        LogUtils.v(TAG, "getSearchApps < " + s);
        SearchApps searchApps = (SearchApps) JsonUtils.parse(s, SearchApps.class);
        if (searchApps != null) {
            if (searchApps.getSuccess()) {
                mPNS = searchApps.getPageNumberStack();
            } else {
                LogUtils.e(TAG, "getSearchApps retry:" + retry);
                if (retry > 0) {
                    return getSearchApps(kw, retry - 1);
                }
            }
        }
        return searchApps;
    }

    public interface SearchCallback {
        void onSearch(List<AppData> list, boolean more);
    }

    public void getSearch(final String kw, final SearchCallback callback) {
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                final SearchApps searchApps = getSearchApps(kw, 3);
                if (searchApps != null) {
                    List<AppData> list = searchApps.getApps();
                    if (list != null) {
                        mSearchAppList.addAll(list);
                        ThreadUtils.uiExecute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSearch(mSearchAppList, searchApps.getHasNext());
                            }
                        });
                    }
                }
            }
        });
    }

    // Update
    public interface UpdateCallback {
        void onUpdate(List<AppData> list);
    }

    public void getUpdateApps(final Context context, final UpdateCallback callback) {
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> infoList = pm.getInstalledPackages(0);
                List<PackageInfo> thirdList = new ArrayList<>();
                for (final PackageInfo info : infoList) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        thirdList.add(info);
                    }
                }

                final ArrayList<AppData> updateList = new ArrayList<>();
                final CountDownLatch countDownLatch = new CountDownLatch(thirdList.size());
                for (final PackageInfo info : thirdList) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        final String name = info.applicationInfo.loadLabel(pm).toString();
                        ThreadUtils.workExecute(new Runnable() {
                            @Override
                            public void run() {
                                SearchApps searchApps = getSearchApps(name, 3);
                                if (searchApps != null) {
                                    List<AppData> appList = searchApps.getApps();
                                    int size = appList.size();
                                    for (int i = 0; i < size; i++) {
                                        AppData appData = appList.get(i);
                                        if (appData.packageName.equals(info.packageName)) {
                                            if (appData.versionCode > info.versionCode) {
                                                updateList.add(appData);
                                            }
                                            break;
                                        }
                                    }
                                }
                                countDownLatch.countDown();
                                LogUtils.v(TAG, "countDownLatch countDown getCount:" + countDownLatch.getCount());
                            }
                        });
                    }
                }
                try {
                    LogUtils.v(TAG, "countDownLatch await begin");
                    countDownLatch.await();
                    LogUtils.v(TAG, "countDownLatch await end");
                } catch (InterruptedException e) {
                    LogUtils.e(TAG, "countDownLatch", e);
                }
                ThreadUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onUpdate");
                        callback.onUpdate(updateList);
                    }
                });
            }
        });
    }
}
