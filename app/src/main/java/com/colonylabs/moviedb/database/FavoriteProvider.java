package com.colonylabs.moviedb.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by dharmana on 7/25/17.
 */

public class FavoriteProvider extends ContentProvider {


    public static final int FAVORITES = 10;
    public static final int FAVORITES_WITH_ID = 20;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final String TAG = FavoriteProvider.class.getSimpleName();
    private FavoriteDBHelper dbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITE, FAVORITES);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITE + "/#", FAVORITES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteDBHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                result = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                result = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                Log.w(TAG, "Unknown URI: " + uri);
        }


        return result;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri result = null;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    result = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);

                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    Log.e(TAG, "Insert data failed to: " + uri);
                }
                break;
            default:
                Log.w(TAG, "Unknown URI: " + uri);
        }


        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int result = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case FAVORITES_WITH_ID:
                String whereClause = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String id = uri.getPathSegments().get(1);
                result = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, whereClause, new String[]{id});
                break;
            default:
                Log.w(TAG, "Unknown URI: " + uri);
        }

        if (result > 0) getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}