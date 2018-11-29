package com.ji.tree.gan.daily;

import com.ji.tree.gan.GanDailyData;
import com.ji.tree.gan.GanRepository;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DailyPresenter implements DailyContract.Presenter {
    private DailyContract.View mView;
    private GanRepository mRepository;
    private CompositeDisposable mCompositeDisposable;

    public DailyPresenter(DailyContract.View view, GanRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void history() {
        mCompositeDisposable.add(mRepository.history());

        more();
    }

    @Override
    public void more() {
        mCompositeDisposable.add(mRepository.more(new GanRepository.LoadMoreCallback() {
            @Override
            public void onPreLoadMore(Disposable disposable) {
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onLoadMore(List<GanDailyData.GanData> list) {
                mView.show(list);
            }
        }));
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
