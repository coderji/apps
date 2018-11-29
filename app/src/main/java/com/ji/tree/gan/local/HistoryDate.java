package com.ji.tree.gan.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "HistoryDate")
public class HistoryDate {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    public String date;
}
