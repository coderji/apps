package com.ji.tree.app;

import com.ji.tree.app.tencent.TencentRepository;
import com.ji.tree.app.local.AppData;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AppPresenter implements AppContract.Presenter {
    private AppContract.View mView;
    private TencentRepository mRepository;
    private CompositeDisposable mCompositeDisposable;

    public AppPresenter(AppContract.View view, TencentRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void update() {
        mCompositeDisposable.add(mRepository.update(new TencentRepository.UpdateCallback() {
            @Override
            public void onPreUpdate(Disposable disposable) {
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onUpdate(List<AppData> list) {
                mView.show(list);
            }
        }));
    }

    @Override
    public void top() {
        mCompositeDisposable.add(mRepository.top(new TencentRepository.TopCallback() {
            @Override
            public void onTop(List<AppData> list) {
                mView.show(list);
            }
        }));
    }

    @Override
    public void install() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
