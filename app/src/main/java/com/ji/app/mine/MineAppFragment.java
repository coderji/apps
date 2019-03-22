package com.ji.app.mine;

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
import com.ji.app.tencent.TencentRepository;
import com.ji.utils.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MineAppFragment extends Fragment {
    private static final String TAG = "MineAppFragment";
    private AppViewAdapter mDownloadingAdapter, mUpdateAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.mine_app_fragment, container, false);

        RecyclerView downloadingRecyclerView = parent.findViewById(R.id.mine_install_rv);
        LinearLayoutManager downloadingLayoutManager = new LinearLayoutManager(getActivity());
        downloadingRecyclerView.setLayoutManager(downloadingLayoutManager);
        downloadingRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mDownloadingAdapter = new AppViewAdapter();
        downloadingRecyclerView.setAdapter(mDownloadingAdapter);

        RecyclerView updateRecyclerView = parent.findViewById(R.id.mine_update_rv);
        LinearLayoutManager updateLayoutManager = new LinearLayoutManager(getActivity());
        updateRecyclerView.setLayoutManager(updateLayoutManager);
        updateRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mUpdateAdapter = new AppViewAdapter();
        updateRecyclerView.setAdapter(mUpdateAdapter);

        TencentRepository.getInstance().getUpdateApps(getActivity(), new TencentRepository.UpdateCallback() {
            @Override
            public void onUpdate(List<AppData> list) {
                View update = getView().findViewById(R.id.mine_update_tv);
                if (list.isEmpty()) {
                    if (update.getVisibility() != View.GONE) {
                        update.setVisibility(View.GONE);
                    }
                } else {
                    if (update.getVisibility() != View.VISIBLE) {
                        update.setVisibility(View.VISIBLE);
                    }
                    getView().findViewById(R.id.mine_update_tv).setVisibility(View.VISIBLE);
                    mUpdateAdapter.setList(list);
                    mUpdateAdapter.notifyDataSetChanged();
                }
            }
        });

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), AppDownloadService.class);
        if (getActivity() != null) {
            getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (getActivity() != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.v(TAG, "onHiddenChanged hidden:" + hidden);
        if (!hidden) {
            mDownloadingAdapter.notifyDataSetChanged();
            mUpdateAdapter.notifyDataSetChanged();
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.v(TAG, "onServiceConnected");
            AppDownloadService.DownloadBinder downloadBinder = (AppDownloadService.DownloadBinder) service;
            mDownloadingAdapter.setDownloadBinder(downloadBinder);
            mUpdateAdapter.setDownloadBinder(downloadBinder);

            List<AppData> appList = downloadBinder.getAppList();
            View install = getView().findViewById(R.id.mine_install_tv);
            if (appList.isEmpty()) {
                if (install.getVisibility() != View.GONE) {
                    install.setVisibility(View.GONE);
                }
            } else {
                if (install.getVisibility() != View.VISIBLE) {
                    install.setVisibility(View.VISIBLE);
                }
                mDownloadingAdapter.setList(appList);
                mDownloadingAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.v(TAG, "onServiceDisconnected");
        }
    };

    public static void main(String[] args) {
        try {
            LogUtils.v(TAG, "> https://github.com/coderji/Tree/blob/master/app/release/output.json");
            Document output = Jsoup.connect("https://github.com/coderji/Tree/blob/master/app/release/output.json").get();
            LogUtils.v(TAG, "< https://github.com/coderji/Tree/blob/master/app/release/output.json");
            String json = output.getElementById("LC1").text();
            long versionCode = new com.ji.org.json.JSONArray(json).getJSONObject(0).getJSONObject("apkInfo").getLong("versionCode");
            LogUtils.v(TAG, "versionCode:" + versionCode + " json:" + json);
        } catch (Exception e) {
            LogUtils.e(TAG, "output.json", e);
        }
    }
}
