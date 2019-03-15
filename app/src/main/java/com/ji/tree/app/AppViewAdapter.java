package com.ji.tree.app;

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
import androidx.recyclerview.widget.RecyclerView;

public class AppViewAdapter extends RecyclerView.Adapter<AppViewAdapter.Holder> {
    private String TAG = "AppViewAdapter";
    private List<AppData> mList;
    private AppDownloadService.DownloadBinder mDownloadBinder;
    public static final int TYPE_NORMAL = 0, TYPE_LOADING = 1, TYPE_NO_MORE = 2;
    private int mBottomType = TYPE_NORMAL;

    public AppViewAdapter() {
    }

    public void setList(List<AppData> list) {
        mList = list;
    }

    public void setDownloadBinder(AppDownloadService.DownloadBinder binder) {
        mDownloadBinder = binder;
    }

    public void setBottomType(int type) {
        mBottomType = type;
    }

    public int getBottomType() {
        return mBottomType;
    }

    @Override
    public int getItemViewType(int position) {
        if (getBottomType() == TYPE_LOADING && position == getItemCount() - 1) {
            return TYPE_LOADING;
        } else if (getBottomType() == TYPE_NO_MORE && position == getItemCount() - 1) {
            return TYPE_NO_MORE;
        }
        return TYPE_NORMAL;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_rv_item_loading, parent, false));
        } else if (viewType == TYPE_NO_MORE) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_rv_item_no_more, parent, false));
        }
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_rv_item_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        LogUtils.v(TAG, "onBindViewHolder " + holder);
        if (getItemViewType(position) == TYPE_NORMAL) {
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
        return mList == null ? 0 : mList.size() + (getBottomType() == TYPE_NORMAL ? 0 : 1);
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
