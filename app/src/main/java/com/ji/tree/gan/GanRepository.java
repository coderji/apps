package com.ji.tree.gan;

import android.content.Context;

import com.ji.tree.Database;
import com.ji.tree.gan.local.HistoryDao;
import com.ji.tree.gan.local.HistoryDate;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GanRepository {
    private String TAG = "GanRepository";
    private GanApi mGanApi;
    private HistoryDao mHistoryDao;
    private List<GanDailyData.GanData> mGanDataList;

    public GanRepository(Context context) {
        mGanApi = new Retrofit.Builder()
                .baseUrl("https://gank.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(GanApi.class);

        mHistoryDao = Database.getInstance(context).historyDao();
        mGanDataList = new ArrayList<>();
    }

    public Disposable history() {
        return mGanApi.getHistory().subscribeOn(Schedulers.single()).subscribe(new Consumer<GanHistoryDate>() {
            @Override
            public void accept(GanHistoryDate ganHistoryDate) throws Exception {
                LogUtils.v(TAG, "getHistory results:" + ganHistoryDate.results);
                List<HistoryDate> historyDateList = new ArrayList<>(ganHistoryDate.results.size());
                for (String result : ganHistoryDate.results) {
                    HistoryDate historyDate = new HistoryDate();
                    historyDate.date = result;
                    historyDateList.add(historyDate);
                }
                mHistoryDao.insert(historyDateList);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e(TAG, "getHistory results:", throwable);
            }
        });
    }

    public interface LoadMoreCallback {
        void onPreLoadMore(Disposable disposable);
        void onLoadMore(List<GanDailyData.GanData> list);
    }

    public Disposable more(final LoadMoreCallback callback) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                int offset = 5;
                List<HistoryDate> dateList = mHistoryDao.getDateList(mGanDataList.size(), offset);
                for (int i = 0; i < offset; i++) {
                    String[] date = dateList.get(i).date.split("-");
                    Disposable disposable = mGanApi.getDailyData(date[0], date[1], date[2])
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<GanDailyData>() {
                                @Override
                                public void accept(GanDailyData ganDailyData) throws Exception {
                                    mGanDataList.addAll(ganDailyData.results.pData);
                                    callback.onLoadMore(mGanDataList);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    LogUtils.e(TAG, "accept", throwable);
                                }
                            });
                    callback.onPreLoadMore(disposable);
                }
                emitter.onSuccess(true);
            }
        }).subscribeOn(Schedulers.single()).subscribe();
    }
}
