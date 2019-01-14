package com.ji.tree.app.local;

public class AppData {
    public String iconUrl;
    public String name;
    public String packageName;
    public long versionCode;

    @Override
    public String toString() {
        return "AppData iconUrl:" + iconUrl
                + " name:" + name
                + " packageName:" + packageName
                + " versionCode:" + versionCode;
    }
}
