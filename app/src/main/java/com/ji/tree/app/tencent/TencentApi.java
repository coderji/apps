package com.ji.tree.app.tencent;

import com.ji.tree.utils.NetUtils;

public class TencentApi {
    public static SearchAppData search(String kw) {
        SearchAppData searchAppData = new SearchAppData();
        String s = NetUtils.get("https://sj.qq.com/myapp/searchAjax.htm?"
                + "&kw=" + kw);
        return searchAppData;
    }

    public static TopAppData softTop(int pageNo, int pageSize) {
        TopAppData topAppData = new TopAppData();
        String s = NetUtils.get("https://mapp.qzone.qq.com/cgi-bin/mapp/mapp_applist?apptype=soft_top&platform=touch"
                + "&pageNo=" + pageNo + "&pageSize" + pageSize);
        return topAppData;
    }
}
