package com.colonylabs.moviedb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.colonylabs.moviedb.database.FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW;
import static com.colonylabs.moviedb.database.FavoriteContract.FavoriteEntry.COLUMN_POSTER;
import static com.colonylabs.moviedb.database.FavoriteContract.FavoriteEntry.COLUMN_RATING;
import static com.colonylabs.moviedb.database.FavoriteContract.FavoriteEntry.COLUMN_RELEASE;
import static com.colonylabs.moviedb.database.FavoriteContract.FavoriteEntry.COLUMN_TITLE;

public class FavoriteDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + "(" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_OVERVIEW + " TEXT, " +
                COLUMN_RELEASE + " TEXT, " +
                COLUMN_POSTER + " TEXT, " +
                COLUMN_RATING + " );";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
