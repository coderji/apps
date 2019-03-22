package com.ji.app.search;

import com.ji.app.local.AppData;
import com.ji.app.tencent.TencentRepository;

import java.util.List;

public class SearchAppPresenter implements SearchAppContract.Presenter {
    private SearchAppContract.View mView;
    private TencentRepository mRepository;

    public SearchAppPresenter(SearchAppContract.View view, TencentRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
    }

    @Override
    public void getMore(String kw) {
        mRepository.getSearch(kw, new TencentRepository.SearchCallback() {
            @Override
            public void onSearch(List<AppData> list, boolean more) {
                mView.showMore(list, more);
            }
        });
    }

    @Override
    public void unsubscribe() {
    }
}
