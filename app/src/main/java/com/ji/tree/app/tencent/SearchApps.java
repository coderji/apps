package com.ji.tree.app.tencent;

import com.ji.tree.app.local.AppData;
import com.ji.tree.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchApps {
    @JsonUtils.FieldName("obj")
    private Obj obj;

    public static class Obj {
        @JsonUtils.FieldName("appDetails")
        private List<AppDetail> appDetailList;
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
    }

    public List<AppData> getApps() {
        List<AppData> list = new ArrayList<>();
        for (AppDetail app : obj.appDetailList) {
            AppData appData = new AppData();
            appData.iconUrl = app.iconurl;
            appData.name = app.name;
            appData.packageName = app.packageName;
            appData.versionCode = app.versionCode;
            list.add(appData);
        }
        return list;
    }
}
