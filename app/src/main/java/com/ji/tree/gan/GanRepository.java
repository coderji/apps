package com.ji.tree.gan;

import android.content.Context;

import com.ji.tree.gan.local.HistoryDate;
import com.ji.tree.gan.local.HistoryDateHelper;
import com.ji.tree.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class GanRepository {
    private String TAG = "GanRepository";
    private HistoryDateHelper mHistoryDateHelper;
    private List<GanDailyData.GanData> mGanDataList;

    public GanRepository(Context context) {
        mHistoryDateHelper = new HistoryDateHelper(context);
        mGanDataList = new ArrayList<>();
    }

    public void history() {
        GanHistoryDate ganHistoryDate =  GanApi.getHistory();
        LogUtils.v(TAG, "getHistory results:" + ganHistoryDate.results);
        List<HistoryDate> historyDateList = new ArrayList<>(ganHistoryDate.results.size());
        for (String result : ganHistoryDate.results) {
            HistoryDate historyDate = new HistoryDate();
            historyDate.date = result;
            historyDateList.add(historyDate);
        }
        mHistoryDateHelper.insert(historyDateList);
    }

    public interface LoadMoreCallback {
        void onLoadMore(List<GanDailyData.GanData> list);
    }

    public void more(final LoadMoreCallback callback) {
        int offset = 5;
        List<HistoryDate> dateList = mHistoryDateHelper.getDateList(mGanDataList.size(), offset);
        for (int i = 0; i < offset; i++) {
            String[] date = dateList.get(i).date.split("-");
            GanDailyData ganDailyData = GanApi.getDailyData(date[0], date[1], date[2]);
            mGanDataList.addAll(ganDailyData.results.pData);
            callback.onLoadMore(mGanDataList);
        }
    }
}
