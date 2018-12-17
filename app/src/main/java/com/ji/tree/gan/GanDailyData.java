package com.ji.tree.gan;

import java.util.List;

public class GanDailyData {
    public List<String> category;
    public boolean error;
    public Results results;

    @Override
    public String toString() {
        if (results == null) {
            return "GanDailyData:null";
        } else {
            return "GanDailyData category: " + category + " error:" + error
                    + " iData:" + (results.iData == null ? "null" : results.iData.size())
                    + " aData:" + (results.aData == null ? "null" : results.aData.size())
                    + " oData:" + (results.oData == null ? "null" : results.oData.size())
                    + " eData:" + (results.eData == null ? "null" : results.eData.size())
                    + " pData:" + (results.pData == null ? "null" : results.pData.size())
                    + " vData:" + (results.vData == null ? "null" : results.vData.size());
        }
    }

    public static class Results {
        // iOS
        public List<GanData> iData;
        // Android
        public List<GanData> aData;
        // 瞎推荐
        public List<GanData> oData;
        // 拓展资源
        public List<GanData> eData;
        // 福利
        public List<GanData> pData;
        // 休息视频
        public List<GanData> vData;
    }

    public static class GanData {
        public String _id;
        public String createdAt;
        public String desc;
        public String publishedAt;
        public String source;
        public String type;
        public String url;
        public boolean used;
        public String who;

        @Override
        public String toString() {
            return "GanData _id:" + _id
                    + " createdAt:" + createdAt
                    + " desc:" + desc
                    + " publishedAt:" + publishedAt
                    + " source:" + source
                    + " type:" + type
                    + " url:" + url
                    + " used:" + used
                    + " who:" + who;
        }
    }
}
