package com.ji.tree.app.top;

import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.app.local.AppData;

import java.util.List;

public class TopAppPresenter implements TopAppContract.Presenter {
    private TopAppContract.View mView;
    private TencentRepository mRepository;

    public TopAppPresenter(TopAppContract.View view, TencentRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
    }

    @Override
    public void getMore() {
        mRepository.getTop(new TencentRepository.TopCallback() {
            @Override
            public void onTop(List<AppData> list, boolean more) {
                mView.showMore(list, more);
            }
        });
    }

    @Override
    public void unsubscribe() {
    }
}
