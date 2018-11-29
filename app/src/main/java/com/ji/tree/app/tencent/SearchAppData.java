package com.ji.tree.app.tencent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchAppData {
    @SerializedName("obj")
    public Obj obj;

    public class Obj {
        @SerializedName("appDetails")
        public List<AppDetail> appDetails;
    }

    public class AppDetail {
        @SerializedName("apkUrl")
        public String apkUrl;

        @SerializedName("name")
        public String name;

        @SerializedName("iconUrl")
        public String iconUrl;

        @SerializedName("packageName")
        public String packageName;

        @SerializedName("versionCode")
        public int versionCode;
    }
}
