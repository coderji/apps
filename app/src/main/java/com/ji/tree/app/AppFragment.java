package com.ji.tree.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ji.tree.R;
import com.ji.tree.app.local.AppData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppFragment extends Fragment implements AppContract.View {
    private String TAG = "AppFragment";
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
        mPresenter.top();

        return parent;
    }

    @Override
    public void onStop() {
        super.onStop();

        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(AppContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void show(List<AppData> list) {
        if (mAppAdapter.getList() == null) {
            mAppAdapter.setList(list);
        }
        mAppAdapter.notifyItemChanged(0);
    }

    private final static class AppAdapter extends RecyclerView.Adapter<AppAdapter.Holder> {
        private Context mContext;
        private List<AppData> mList;

        AppAdapter(Context context) {
            mContext = context;
        }

        public void setList(List<AppData> list) {
            mList = list;
        }

        public List<AppData> getList() {
            return mList;
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(mContext).inflate(R.layout.app_rv_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AppData data = mList.get(position);
            holder.number.setText(String.valueOf(position + 1));

            holder.name.setText(data.name);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView number;
            ImageView icon;
            TextView name;
            Button btn;

            private Holder(View itemView) {
                super(itemView);

                number = itemView.findViewById(R.id.app_iv_number);
                icon = itemView.findViewById(R.id.app_iv_icon);
                name = itemView.findViewById(R.id.app_iv_name);
                btn = itemView.findViewById(R.id.app_iv_btn);
            }
        }
    }
}
