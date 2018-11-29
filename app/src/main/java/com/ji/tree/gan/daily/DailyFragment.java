package com.ji.tree.gan.daily;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ji.tree.R;
import com.ji.tree.gan.GanDailyData;

import java.util.List;

public class DailyFragment extends Fragment implements DailyContract.View {
    private String TAG = "DailyFragment";
    private DailyContract.Presenter mPresenter;
    private DailyAdapter mDailyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.daily_fragment, container, false);

        RecyclerView recyclerView = parent.findViewById(R.id.daily_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mDailyAdapter = new DailyAdapter(getActivity());
        recyclerView.setAdapter(mDailyAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int itemCount = layoutManager.getItemCount();
                        int lastPosition = layoutManager.findLastVisibleItemPosition();
                        if (itemCount != 0 && itemCount == lastPosition + 1) {
                            mPresenter.more();
                        }
                    }
                }
            }
        });
        mPresenter.history();

        return parent;
    }

    @Override
    public void onStop() {
        super.onStop();

        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(DailyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void show(List<GanDailyData.GanData> list) {
        if (mDailyAdapter.getList() == null) {
            mDailyAdapter.setList(list);
        }
        mDailyAdapter.notifyItemChanged(0);
    }

    private final static class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.Holder> {
        private Context mContext;
        private List<GanDailyData.GanData> mList;
        private static final int TYPE_NORMAL = 0, TYPE_FOOT = 1;

        DailyAdapter(Context context) {
            mContext = context;
        }

        public void setList(List<GanDailyData.GanData> list) {
            mList = list;
        }

        public List<GanDailyData.GanData> getList() {
            return mList;
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOT) {
                return new Holder(LayoutInflater.from(mContext).inflate(R.layout.daily_rv_load, parent, false));
            } else {
                return new Holder(LayoutInflater.from(mContext).inflate(R.layout.daily_rv_item, parent, false));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_FOOT;
            }
            return TYPE_NORMAL;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                GanDailyData.GanData data = mList.get(position);
                if (data.type.equals("福利")) {
                    Glide.with(mContext).load(data.url).into(holder.picture);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 1 : mList.size() + 1;
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView picture;
            TextView load;

            private Holder(View itemView) {
                super(itemView);

                picture = itemView.findViewById(R.id.daily_iv_picture);
                load = itemView.findViewById(R.id.daily_tv_loading);
            }
        }
    }
}
