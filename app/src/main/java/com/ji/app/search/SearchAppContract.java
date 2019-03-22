package com.ji.app.search;

import com.ji.app.local.AppData;
import com.ji.app.mvp.BasePresenter;
import com.ji.app.mvp.BaseView;

import java.util.List;

public interface SearchAppContract {
    interface View extends BaseView<Presenter> {
        void showMore(List<AppData> list, boolean more);
    }

    interface Presenter extends BasePresenter {
        void getMore(String kw);
    }
}
