package com.ji.tree;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ji.tree.gan.local.HistoryDao;
import com.ji.tree.gan.local.HistoryDate;

@android.arch.persistence.room.Database(entities = {HistoryDate.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract HistoryDao historyDao();

    private static Database INSTANCE;

    private static final Object sLock = new Object();

    public static Database getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        Database.class, "HistoryDate.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}
