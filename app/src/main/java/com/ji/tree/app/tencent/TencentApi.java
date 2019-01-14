package com.ji.tree.app.tencent;

import com.ji.tree.app.local.AppData;
import com.ji.tree.utils.InternetUtils;
import com.ji.tree.utils.JsonUtils;

import java.util.List;

public class TencentApi {
    private static final String TAG = "TencentApi";

    public static List<AppData> getAppList(String kw, String pns, String sid) {
        String s = InternetUtils.getString(
                "https://sj.qq.com/myapp/searchAjax.htm?"
                        + "&kw=" + kw
                        + "&pns=" + pns
                        + "&sid=" + sid);
        SearchApps searchAppData = (SearchApps) JsonUtils.parse(s, SearchApps.class);
        if (searchAppData != null) {
            return searchAppData.getApps();
        }
        return null;
    }

    public static List<AppData> getTopList(int pageNo, int pageSize) {
        String s = InternetUtils.getString(
                "https://mapp.qzone.qq.com/cgi-bin/mapp/mapp_applist?apptype=soft_top&platform=touch"
                        + "&pageNo=" + pageNo
                        + "&pageSize" + pageSize);
        TopApps topAppData = (TopApps) JsonUtils.parse(s, TopApps.class);
        if (topAppData != null) {
            return topAppData.getApps();
        }
        return null;
    }
}
