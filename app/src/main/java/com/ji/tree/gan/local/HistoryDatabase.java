package com.ji.tree.gan.local;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@android.arch.persistence.room.Database(entities = {HistoryDate.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();

    private static HistoryDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static HistoryDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        HistoryDatabase.class, "gan.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}
