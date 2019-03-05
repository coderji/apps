package com.ji.tree.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ji.tree.R;
import com.ji.tree.app.local.AppData;
import com.ji.utils.ImageUtils;
import com.ji.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppFragment extends Fragment implements AppContract.View {
    private static final String TAG = "AppFragment";
    private AppContract.Presenter mPresenter;
    private AppAdapter mAppAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.app_fragment, container, false);

        RecyclerView recyclerView = parent.findViewById(R.id.app_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAppAdapter = new AppAdapter(getActivity());
        recyclerView.setAdapter(mAppAdapter);

        mPresenter.getTop();

        return parent;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAppAdapter.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        mAppAdapter.stop();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(AppContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showTop(List<AppData> list) {
        mAppAdapter.setList(list);
        mAppAdapter.notifyDataSetChanged();
    }

    private final static class AppAdapter extends RecyclerView.Adapter<AppAdapter.Holder> {
        private Context mContext;
        private List<AppData> mList;
        private AppDownloadService.DownloadBinder mAppDownloadBinder;

        public void start() {
            LogUtils.v(TAG, "AppAdapter start");
            if (mAppDownloadBinder == null) {
                Intent intent = new Intent(mContext, AppDownloadService.class);
                mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

        public void stop() {
            LogUtils.v(TAG, "AppAdapter stop");
            if (mAppDownloadBinder != null && !mAppDownloadBinder.isDownloading()) {
                mAppDownloadBinder = null;
                mContext.unbindService(mServiceConnection);
            }
        }

        AppAdapter(Context context) {
            mContext = context;
        }

        public void setList(List<AppData> list) {
            mList = list;
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(mContext).inflate(R.layout.app_rv_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            LogUtils.v(TAG, "onBindViewHolder " + holder);
            final AppData data = mList.get(position);
            holder.number.setText(String.valueOf(position + 1));
            ImageUtils.with(holder.icon, data.iconUrl);
            holder.name.setText(data.name);
            holder.detail.setText(data.detail);
            if (mAppDownloadBinder != null) {
                mAppDownloadBinder.with(holder.btn, data);
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView number;
            ImageView icon;
            TextView name;
            TextView detail;
            Button btn;

            private Holder(View itemView) {
                super(itemView);

                number = itemView.findViewById(R.id.app_iv_number);
                icon = itemView.findViewById(R.id.app_iv_icon);
                name = itemView.findViewById(R.id.app_iv_name);
                detail = itemView.findViewById(R.id.app_iv_detail);
                btn = itemView.findViewById(R.id.app_iv_btn);
            }
        }

        private ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.v(TAG, "onServiceConnected");
                mAppDownloadBinder = (AppDownloadService.DownloadBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtils.v(TAG, "onServiceDisconnected");
                mAppDownloadBinder = null;
            }
        };
    }
}
