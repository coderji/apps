package com.ji.tree.app.top;

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
import com.ji.tree.app.AppDownloadService;
import com.ji.tree.app.local.AppData;
import com.ji.utils.ImageUtils;
import com.ji.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TopAppFragment extends Fragment implements TopAppContract.View {
    private static final String TAG = "TopAppFragment";
    private TopAppContract.Presenter mPresenter;
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int itemCount = layoutManager.getItemCount();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastPosition == itemCount - 1) {
                        mPresenter.getTop();
                    }
                }
            }
        });

        mPresenter.getTop();

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

        mPresenter.unsubscribe();
        if (getActivity() != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    @Override
    public void setPresenter(TopAppContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showTop(List<AppData> list) {
        mAppAdapter.setList(list);
        mAppAdapter.notifyDataSetChanged();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.v(TAG, "onServiceConnected");
            mAppAdapter.setDownloadBinder((AppDownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.v(TAG, "onServiceDisconnected");
        }
    };

    private final static class AppAdapter extends RecyclerView.Adapter<AppAdapter.Holder> {
        private Context mContext;
        private List<AppData> mList;
        private AppDownloadService.DownloadBinder mDownloadBinder;
        private final static int TYPE_NORMAL = 0, TYPE_WAITING = 1;

        AppAdapter(Context context) {
            mContext = context;
        }

        public void setDownloadBinder(AppDownloadService.DownloadBinder binder) {
            mDownloadBinder = binder;
        }

        public void setList(List<AppData> list) {
            mList = list;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_WAITING;
            }
            return TYPE_NORMAL;
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_WAITING) {
                return new Holder(LayoutInflater.from(mContext).inflate(R.layout.app_rv_item_waiting, parent, false));
            }
            return new Holder(LayoutInflater.from(mContext).inflate(R.layout.app_rv_item_normal, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            LogUtils.v(TAG, "onBindViewHolder " + holder);
            if (getItemViewType(position) != TYPE_WAITING) {
                final AppData data = mList.get(position);
                holder.number.setText(String.valueOf(position + 1));
                ImageUtils.with(holder.icon, data.iconUrl);
                holder.name.setText(data.name);
                holder.detail.setText(data.detail);
                if (mDownloadBinder != null) {
                    mDownloadBinder.with(holder.btn, data);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size() + 1;
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
    }
}
