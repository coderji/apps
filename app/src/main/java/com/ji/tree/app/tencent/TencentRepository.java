package com.ji.tree.app.tencent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.ji.tree.app.local.AppData;
import com.ji.tree.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TencentRepository {
    private String TAG = "TencentRepository";
    private TencentApi mTencentApi;
    private Context mAppContext;

    public TencentRepository(Context context) {
        mTencentApi = new Retrofit.Builder()
                .baseUrl("https://www.tencent.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(TencentApi.class);
        mAppContext = context.getApplicationContext();
    }

    public interface UpdateCallback {
        void onPreUpdate(Disposable disposable);
        void onUpdate(List<AppData> list);
    }

    public Disposable update(final UpdateCallback callback) {
        return Single.create(new SingleOnSubscribe<ArrayList<AppData>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<AppData>> emitter) throws Exception {
                PackageManager pm = mAppContext.getPackageManager();
                List<PackageInfo> infoList = pm.getInstalledPackages(0);
                final ArrayList<AppData> appList = new ArrayList<>(infoList.size());
                for (PackageInfo info : infoList) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        final AppData appData = new AppData();
                        appData.name = info.applicationInfo.loadLabel(pm).toString();
                        appData.packageName = info.packageName;
                        appData.versionCode = info.versionCode;

                        Disposable disposable = mTencentApi.search(appData.name).subscribe(new Consumer<SearchAppData>() {
                            @Override
                            public void accept(SearchAppData searchAppData) throws Exception {
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
                        });
                        callback.onPreUpdate(disposable);
                    }
                }
                emitter.onSuccess(appList);
            }
        }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<AppData>>() {
                    @Override
                    public void accept(ArrayList<AppData> appList) throws Exception {
                        callback.onUpdate(appList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(TAG, "update", throwable);
                    }
                });
    }

    public interface TopCallback {
        void onTop(List<AppData> list);
    }

    public Disposable top(final TopCallback callback) {
        return mTencentApi.softTop(0, 10)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        TopAppData topAppData = new Gson().fromJson(responseBody.string().split(";")[0], TopAppData.class);
                        final ArrayList<AppData> appList = new ArrayList<>(topAppData.app.size());
                        for (TopAppData.App app : topAppData.app) {
                            final AppData appData = new AppData();
                            appData.iconUrl = app.iconUrl;
                            appData.name = app.name;
                            appList.add(appData);
                        }
                        callback.onTop(appList);
                    }
                });
    }
}
