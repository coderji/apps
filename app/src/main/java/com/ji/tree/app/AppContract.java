package com.ji.tree.app;

import com.ji.tree.mvp.BasePresenter;
import com.ji.tree.mvp.BaseView;
import com.ji.tree.app.local.AppData;

import java.util.List;

public interface AppContract {
    interface View extends BaseView<Presenter> {
        void show(List<AppData> list);
    }

    interface Presenter extends BasePresenter {
        void update();
        void top();
        void install();
    }
}
