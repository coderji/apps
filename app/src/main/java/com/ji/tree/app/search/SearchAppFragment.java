package com.ji.tree.app.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ji.tree.R;
import com.ji.tree.app.AppDownloadService;
import com.ji.tree.app.AppViewAdapter;
import com.ji.tree.app.local.AppData;
import com.ji.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAppFragment extends Fragment implements SearchAppContract.View {
    private static final String TAG = "SearchAppFragment";
    private SearchAppContract.Presenter mPresenter;
    private AppViewAdapter mAppViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.search_app_fragment, container, false);

        final EditText editText = parent.findViewById(R.id.search_et);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if (!string.isEmpty()) {
                    mPresenter.getMore(string);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = parent.findViewById(R.id.search_rv);
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
                        mPresenter.getMore(editText.getText().toString());
                    }
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
    public void setPresenter(SearchAppContract.Presenter presenter) {
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
