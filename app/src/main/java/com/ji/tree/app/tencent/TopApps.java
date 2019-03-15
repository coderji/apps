package com.ji.tree.app.tencent;

import com.ji.tree.app.local.AppData;
import com.ji.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class TopApps {
    @JsonUtils.FieldName("app")
    private List<App> appList;
    @JsonUtils.FieldName("next")
    private boolean next;

    public static class App {
        @JsonUtils.FieldName("iconurl")
        private String iconUrl;
        @JsonUtils.FieldName("name")
        private String name;
        @JsonUtils.FieldName("pName")
        private String packageName;
        @JsonUtils.FieldName("url")
        private String apkUrl;
        @JsonUtils.FieldName("versionCode")
        private long versionCode;

        @JsonUtils.FieldName("size")
        private String size;
    }

    @Override
    public String toString() {
        return "TopApps next:" + next
                + " appList.size:" + appList.size();
    }

    public List<AppData> getApps() {
        List<AppData> list = new ArrayList<>();
        for (App app : appList) {
            AppData appData = new AppData();
            appData.iconUrl = app.iconUrl;
            appData.name = app.name;
            appData.detail = app.size;
            appData.packageName = app.packageName;
            appData.apkUrl = app.apkUrl;
            appData.versionCode = app.versionCode;
            list.add(appData);
        }
        return list;
    }

    public boolean getNext() {
        return next;
    }
}
