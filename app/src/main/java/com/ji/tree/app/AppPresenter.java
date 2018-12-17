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
    public void update() {
        mRepository.update(new TencentRepository.UpdateCallback() {

            @Override
            public void onUpdate(List<AppData> list) {
                mView.show(list);
            }
        });
    }

    @Override
    public void top() {
        mRepository.top(new TencentRepository.TopCallback() {
            @Override
            public void onTop(List<AppData> list) {
                mView.show(list);
            }
        });
    }

    @Override
    public void install() {

    }

    @Override
    public void unsubscribe() {
    }
}
