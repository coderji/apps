package com.ji.app.top;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ji.app.R;
import com.ji.app.AppDownloadService;
import com.ji.app.AppViewAdapter;
import com.ji.app.local.AppData;
import com.ji.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TopAppFragment extends Fragment implements TopAppContract.View {
    private static final String TAG = "TopAppFragment";
    private TopAppContract.Presenter mPresenter;
    private AppViewAdapter mAppViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.top_app_fragment, container, false);

        RecyclerView recyclerView = parent.findViewById(R.id.top_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAppViewAdapter = new AppViewAdapter();
        mAppViewAdapter.setBottomType(AppViewAdapter.TYPE_LOADING);
        recyclerView.setAdapter(mAppViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mAppViewAdapter.getBottomType() == AppViewAdapter.TYPE_LOADING && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int itemCount = layoutManager.getItemCount();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastPosition == itemCount - 1) {
                        mPresenter.getMore();
                    }
                }
            }
        });

        mPresenter.getMore();

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.v(TAG, "onStart");

        Intent intent = new Intent(getActivity(), AppDownloadService.class);
        if (getActivity() != null) {
            getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.v(TAG, "onStop");

        mPresenter.unsubscribe();
        if (getActivity() != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.v(TAG, "onHiddenChanged hidden:" + hidden);
        if (!hidden) {
            mAppViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(TopAppContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMore(List<AppData> list, boolean more) {
        mAppViewAdapter.setList(list);
        if (!more) {
            mAppViewAdapter.setBottomType(AppViewAdapter.TYPE_NO_MORE);
        }
        mAppViewAdapter.notifyDataSetChanged();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.v(TAG, "onServiceConnected");
            mAppViewAdapter.setDownloadBinder((AppDownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.v(TAG, "onServiceDisconnected");
        }
    };
}
