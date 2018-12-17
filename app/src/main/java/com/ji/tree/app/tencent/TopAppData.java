package com.ji.tree.app.tencent;

import java.util.List;

public class TopAppData {
    public List<App> app;

    public class App {
        public String iconUrl;
        public String name;
        public String packageName;
        public String apkUrl;
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
