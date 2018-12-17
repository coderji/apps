package com.ji.tree.gan.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

import com.ji.tree.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryDateHelper {
    private String TAG = "HistoryDateHelper";
    private SQLiteOpenHelperImpl mSQLiteOpenHelperImpl;

    public HistoryDateHelper(Context context) {
        if (mSQLiteOpenHelperImpl == null) {
            mSQLiteOpenHelperImpl = new SQLiteOpenHelperImpl(context.getApplicationContext(),
                    "gan.db", null, 1);
        }
    }

    public long insert(HistoryDate date) {
        SQLiteDatabase db = mSQLiteOpenHelperImpl.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteOpenHelperImpl.DATE_COLUMN0, date.date);
        return db.insert(SQLiteOpenHelperImpl.DATE_TABLE, null, contentValues);
    }

    public long[] insert(List<HistoryDate> dateList) {
        long[] ids = new long[dateList.size()];
        SQLiteDatabase db = mSQLiteOpenHelperImpl.getWritableDatabase();
        int i = 0;
        for (HistoryDate date : dateList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLiteOpenHelperImpl.DATE_COLUMN0, date.date);
            ids[i++] = db.insert(SQLiteOpenHelperImpl.DATE_TABLE, null, contentValues);
        }
        return ids;
    }

    public List<HistoryDate> getDateList(int start, int offset) {
        List<HistoryDate> dateList = new ArrayList<>();
        SQLiteDatabase db = mSQLiteOpenHelperImpl.getReadableDatabase();
        Cursor cursor = db.query(SQLiteOpenHelperImpl.DATE_TABLE, new String[]{SQLiteOpenHelperImpl.DATE_COLUMN0},
                SQLiteOpenHelperImpl.DATE_COLUMN0 + " IS NOT NULL LIMIT " + start + "," + offset, null,
                null, null, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex(SQLiteOpenHelperImpl.DATE_COLUMN0));
                HistoryDate historyDate  = new HistoryDate();
                historyDate.date = date;
                dateList.add(historyDate);
            }
            cursor.close();
        }
        return dateList;
    }

    private static class SQLiteOpenHelperImpl extends SQLiteOpenHelper {
        String TAG = "SQLiteOpenHelperImpl";
        static String DATE_TABLE = "history_date";
        static String DATE_COLUMN0 = "date";

        private SQLiteOpenHelperImpl(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            LogUtils.v(TAG, "SQLiteOpenHelperImpl");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtils.e(TAG, "onCreate");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DATE_TABLE + "(" + DATE_COLUMN0 + " TEXT NOT NULL PRIMARY KEY);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LogUtils.v(TAG, "onUpgrade oldVersion:" + oldVersion + " newVersion:" + newVersion);
        }
    }
}
