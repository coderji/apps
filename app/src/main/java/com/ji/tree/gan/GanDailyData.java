package com.ji.tree.gan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GanDailyData {
    @SerializedName("category")
    public List<String> category;

    @SerializedName("error")
    public boolean error;

    @SerializedName("results")
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

    public class Results {
        @SerializedName("iOS")
        public List<GanData> iData;

        @SerializedName("Android")
        public List<GanData> aData;

        @SerializedName("瞎推荐")
        public List<GanData> oData;

        @SerializedName("拓展资源")
        public List<GanData> eData;

        @SerializedName("福利")
        public List<GanData> pData;

        @SerializedName("休息视频")
        public List<GanData> vData;
    }

    public class GanData {
        @SerializedName("_id")
        public String _id;

        @SerializedName("createdAt")
        public String createdAt;

        @SerializedName("desc")
        public String desc;

        @SerializedName("publishedAt")
        public String publishedAt;

        @SerializedName("source")
        public String source;

        @SerializedName("type")
        public String type;

        @SerializedName("url")
        public String url;

        @SerializedName("used")
        public boolean used;

        @SerializedName("who")
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
