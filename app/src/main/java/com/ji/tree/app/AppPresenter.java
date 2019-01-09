package com.ji.tree.app;

import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.app.local.AppData;

import java.util.List;

public class AppPresenter implements AppContract.Presenter {
    private AppContract.View mView;
    private TencentRepository mRepository;

    public AppPresenter(AppContract.View view, TencentRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
    }

    @Override
    public void getTop() {
        mRepository.getTop(new TencentRepository.TopCallback() {
            @Override
            public void onTop(List<AppData> list) {
                mView.showTop(list);
            }
        });
    }

    @Override
    public void unsubscribe() {
    }
}
