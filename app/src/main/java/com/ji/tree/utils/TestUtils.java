package com.ji.tree.utils;

import com.ji.tree.gan.GanHistoryDate;

public class TestUtils {
    public static void main(String[] args) {
        String history = NetUtils.get("https://gank.io/api/day/history");
        System.out.print(history);
    }

    private static void testNet() {
        String history = NetUtils.get("https://gank.io/api/day/history");
        System.out.print(history);
    }

    private static void testJson() {
        String history = NetUtils.get("https://gank.io/api/day/history");
//        JsonUtils.parse(history, GanHistoryDate.class);
    }
}
