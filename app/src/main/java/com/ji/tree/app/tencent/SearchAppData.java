package com.ji.tree.app.tencent;

import com.ji.tree.utils.JsonUtils;

import java.util.List;

public class SearchAppData {
    @JsonUtils.FieldName("obj")
    public Obj obj;

    public class Obj {
        public List<AppDetail> appDetails;
    }

    public class AppDetail {
        public String apkUrl;
        public String name;
        public String iconUrl;
        public String packageName;
        public int versionCode;
    }
}
