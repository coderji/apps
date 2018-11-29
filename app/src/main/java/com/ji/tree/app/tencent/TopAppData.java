package com.ji.tree.app.tencent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopAppData {
    @SerializedName("app")
    public List<App> app;

    public class App {
        @SerializedName("iconurl")
        public String iconUrl;

        @SerializedName("name")
        public String name;

        @SerializedName("pName")
        public String packageName;

        @SerializedName("url")
        public String apkUrl;

        @SerializedName("versionCode")
        public int versionCode;

        @Override
        public String toString() {
            return "App iconUrl:" + iconUrl
                    + " name:" + name
                    + " packageName:" + packageName
                    + " apkUrl:" + apkUrl
                    + " versionCode:" + versionCode;
        }
    }
}
