package com.ji.tree.app.search;

import com.ji.tree.app.local.AppData;
import com.ji.tree.mvp.BasePresenter;
import com.ji.tree.mvp.BaseView;

import java.util.List;

public interface SearchAppContract {
    interface View extends BaseView<Presenter> {
        void showMore(List<AppData> list, boolean more);
    }

    interface Presenter extends BasePresenter {
        void getMore(String kw);
    }
}
