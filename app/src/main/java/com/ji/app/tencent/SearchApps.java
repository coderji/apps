package com.ji.app.tencent;

import com.ji.app.local.AppData;
import com.ji.utils.Utils;
import com.ji.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchApps {
    @JsonUtils.FieldName("obj")
    private Obj obj;
    @JsonUtils.FieldName("success")
    private boolean success;

    public static class Obj {
        @JsonUtils.FieldName("appDetails")
        private List<AppDetail> appDetailList;
        @JsonUtils.FieldName("pageNumberStack")
        private String pageNumberStack;
        @JsonUtils.FieldName("hasNext")
        private int hasNext;
    }

    public static class AppDetail {
        @JsonUtils.FieldName("iconUrl")
        private String iconurl;
        @JsonUtils.FieldName("appName")
        private String name;
        @JsonUtils.FieldName("pkgName")
        private String packageName;
        @JsonUtils.FieldName("apkUrl")
        private String apkUrl;
        @JsonUtils.FieldName("versionCode")
        private long versionCode;

        @JsonUtils.FieldName("fileSize")
        private long fileSize;
    }

    @Override
    public String toString() {
        return "SearchApps obj:" + obj;
    }

    public List<AppData> getApps() {
        List<AppData> list = new ArrayList<>();
        if (obj != null) {
            for (AppDetail app : obj.appDetailList) {
                AppData appData = new AppData();
                appData.iconUrl = app.iconurl;
                appData.name = app.name;
                appData.detail = Utils.byte2FitMemorySize(app.fileSize);
                appData.packageName = app.packageName;
                appData.apkUrl = app.apkUrl;
                appData.versionCode = app.versionCode;
                list.add(appData);
            }
        }
        return list;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getPageNumberStack() {
        return obj != null ? obj.pageNumberStack : "";
    }

    public boolean getHasNext() {
        return obj != null && obj.hasNext == 1;
    }
}
