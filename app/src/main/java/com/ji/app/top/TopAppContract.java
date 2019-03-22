package com.ji.app.top;

import com.ji.app.mvp.BasePresenter;
import com.ji.app.mvp.BaseView;
import com.ji.app.local.AppData;

import java.util.List;

public interface TopAppContract {
    interface View extends BaseView<Presenter> {
        void showMore(List<AppData> list, boolean more);
    }

    interface Presenter extends BasePresenter {
        void getMore();
    }
}
