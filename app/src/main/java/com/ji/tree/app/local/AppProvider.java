package com.ji.tree.app.local;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;

public class AppProvider extends ContentProvider {
    public static final String AUTHORITY = "com.ji.tree.app.local";
    public static String TABLE_DATA = "data";
    public static class Columns {
        public static String DATA_ICON_URL = "iconUrl";
        public static String DATA_NAME = "name";
        public static String DATA_PACKAGE_NAME = "packageName";
        public static String DATA_VERSION_CODE = "versionCode";
        public static String DATA_APK_URL = "apkUrl";
        public static String DATA_FILE_SIZE = "fileSize";
        public static String DATA_DOWNLOAD_SIZE = "downloadSize";
        public static String DATA_STATE = "state";
    }

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_DATA = 0;
    static {
        sMatcher.addURI(AUTHORITY, TABLE_DATA, CODE_DATA);
    }
    private AppSQLiteOpenHelper mAppSQLiteOpenHelper;

    @Override
    public boolean onCreate() {
        mAppSQLiteOpenHelper = new AppSQLiteOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mAppSQLiteOpenHelper.getWritableDatabase();
        if (sMatcher.match(uri) == CODE_DATA) {
            return database.query(TABLE_DATA, projection, selection, selectionArgs, null, null, null);
        }
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase database = mAppSQLiteOpenHelper.getWritableDatabase();
        if (sMatcher.match(uri) == CODE_DATA) {
            long rowId = database.insert(TABLE_DATA, null, values);
            if (rowId > 0 && getContext() != null) {
                Uri newUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            }
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mAppSQLiteOpenHelper.getWritableDatabase();
        if (sMatcher.match(uri) == CODE_DATA) {
            return database.delete(TABLE_DATA, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mAppSQLiteOpenHelper.getWritableDatabase();
        if (sMatcher.match(uri) == CODE_DATA) {
            return database.update(TABLE_DATA, values, selection, selectionArgs);
        }
        return 0;
    }

    private static class AppSQLiteOpenHelper extends SQLiteOpenHelper {
        private static String DATABASE_NAME = "app";
        private static int DATABASE_VERSION = 1;

        AppSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DATA + "("
                    + Columns.DATA_ICON_URL + " TEXT NOT NULL PRIMARY KEY, "
                    + Columns.DATA_NAME + " TEXT NOT NULL, "
                    + Columns.DATA_PACKAGE_NAME + " TEXT NOT NULL, "
                    + Columns.DATA_VERSION_CODE + " INTEGER, "
                    + Columns.DATA_APK_URL + " TEXT NOT NULL, "
                    + Columns.DATA_FILE_SIZE + " INTEGER, "
                    + Columns.DATA_DOWNLOAD_SIZE + " INTEGER, "
                    + Columns.DATA_STATE + " INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
