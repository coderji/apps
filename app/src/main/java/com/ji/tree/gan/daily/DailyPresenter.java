package com.ji.tree.gan.daily;

import com.ji.tree.gan.GanDailyData;
import com.ji.tree.gan.GanRepository;

import java.util.List;

public class DailyPresenter implements DailyContract.Presenter {
    private DailyContract.View mView;
    private GanRepository mRepository;

    public DailyPresenter(DailyContract.View view, GanRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void history() {
        mRepository.history();

        more();
    }

    @Override
    public void more() {
        mRepository.more(new GanRepository.LoadMoreCallback() {

            @Override
            public void onLoadMore(List<GanDailyData.GanData> list) {
                mView.show(list);
            }
        });
    }
}
