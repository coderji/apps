package com.ji.tree.gan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GanHistoryDate {
    @SerializedName("error")
    public boolean error;

    @SerializedName("results")
    public List<String> results;

    @Override
    public String toString() {
        return "GanHistoryDate results:" + results;
    }
}
