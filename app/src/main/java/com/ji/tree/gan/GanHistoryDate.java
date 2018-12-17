package com.ji.tree.gan;

import java.util.List;

public class GanHistoryDate {
    public boolean error;
    public List<String> results;

    @Override
    public String toString() {
        return "GanHistoryDate results:" + results;
    }
}
