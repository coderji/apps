package com.ji.tree.run;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ji.tree.R;
import com.ji.tree.utils.LogUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RunFragment extends Fragment {
    private String TAG = "RunFragment";
    private TextView mTextView;
    private StringBuilder mStringBuilder = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.run_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView = view.findViewById(R.id.run_tv);

        startSafe("com.pa.health");
        startSafe("com.pingan.lifeinsurance");
        startSafe("com.eg.android.AlipayGphone");
    }

    private void startSafe(@NonNull String packageName) {
        mStringBuilder.append(packageName).append("\n");
        Intent queryIntent = new Intent();
        queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        queryIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = getActivity().getPackageManager().queryIntentActivities(queryIntent, 0);
        if (resolveInfoList != null && !resolveInfoList.isEmpty()) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            String activityName = resolveInfo.activityInfo.name;
            mStringBuilder.append(activityName).append("\n");
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName(packageName, activityName);
                startActivity(intent);
                mStringBuilder.append("----->success").append("\n\n");
            } catch (ActivityNotFoundException e) {
                LogUtils.e(TAG, "startSafe", e);
                mStringBuilder.append("----->fail").append("\n\n");
            }
        }
        mTextView.setText(mStringBuilder);
    }
}
