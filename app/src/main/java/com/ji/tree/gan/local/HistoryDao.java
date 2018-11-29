package com.ji.tree.gan.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistoryDate date);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<HistoryDate> dateList);

    @Query("SELECT * FROM HistoryDate LIMIT :start, :offset")
    List<HistoryDate> getDateList(int start, int offset);
}
