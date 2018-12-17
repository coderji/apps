package com.ji.tree.gan;

import com.ji.tree.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GanApi {
    public static GanHistoryDate getHistory() {
        GanHistoryDate ganHistoryDate = new GanHistoryDate();
        try {
            String s = NetUtils.get("https://gank.io/api/day/history");
            JSONObject jsonObject = new JSONObject(s);
            ganHistoryDate.error = jsonObject.getBoolean("error");
            if (!ganHistoryDate.error) {
                ganHistoryDate.results = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    ganHistoryDate.results.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ganHistoryDate;
    }

    public static GanDailyData getDailyData(String year, String month, String day) {
        GanDailyData ganDailyData = new GanDailyData();
        try {
            String s = NetUtils.get("https://gank.io/api/day/" + year + "/" + month + "/" + day);
            JSONObject jsonObject = new JSONObject(s);
            ganDailyData.error = jsonObject.getBoolean("error");
            if (!ganDailyData.error) {
                ganDailyData.results = new GanDailyData.Results();
                ganDailyData.results.pData = new ArrayList<>();
                JSONArray pArray = jsonObject.getJSONObject("results").getJSONArray("福利");
                int length = pArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject p = pArray.getJSONObject(i);
                    GanDailyData.GanData pData = new GanDailyData.GanData();
                    pData._id = p.getString("_id");
                    pData.createdAt = p.getString("createdAt");
                    pData.desc = p.getString("desc");
                    pData.publishedAt = p.getString("publishedAt");
                    pData.source = p.getString("source");
                    pData.type = p.getString("type");
                    pData.url = p.getString("url");
                    pData.used = p.getBoolean("used");
                    pData.who = p.getString("who");
                    ganDailyData.results.pData.add(pData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ganDailyData;
    }
}
