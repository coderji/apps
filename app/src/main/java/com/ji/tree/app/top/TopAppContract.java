package com.ji.tree.app.top;

import com.ji.tree.mvp.BasePresenter;
import com.ji.tree.mvp.BaseView;
import com.ji.tree.app.local.AppData;

import java.util.List;

public interface TopAppContract {
    interface View extends BaseView<Presenter> {
        void showTop(List<AppData> list);
    }

    interface Presenter extends BasePresenter {
        void getTop();
    }
}
