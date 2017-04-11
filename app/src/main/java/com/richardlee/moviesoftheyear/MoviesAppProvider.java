package com.richardlee.moviesoftheyear;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MoviesAppProvider extends ContentProvider {
    private static final String TAG = "MoviesAppProvider";

    private MoviesAppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int FAVORITES_MOVIES = 100;
    private static final int FAVORITES_MOVIES_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://com.richardlee.moviesoftheyear.provider/NowPlayingMovies
        matcher.addURI(CONTENT_AUTHORITY, FavoritesMoviesContract.TABLE_NAME, FAVORITES_MOVIES);

        // content://com.richardlee.moviesoftheyear.provider/NowPlayingMovies/1
        matcher.addURI(CONTENT_AUTHORITY, FavoritesMoviesContract.TABLE_NAME + "/#", FAVORITES_MOVIES_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = MoviesAppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Log.d(TAG, "query: URI = " + uri);
        final int match = sUriMatcher.match(uri);
        //Log.d(TAG, "query: math = " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {
            case FAVORITES_MOVIES:
                queryBuilder.setTables(FavoritesMoviesContract.TABLE_NAME);
                break;

            case FAVORITES_MOVIES_ID:
                queryBuilder.setTables(FavoritesMoviesContract.TABLE_NAME);
                long moviesID = FavoritesMoviesContract.getMovieID(uri);

                queryBuilder.appendWhere(FavoritesMoviesContract.Columns.TMDB_ID + " = " + moviesID);
                break;
            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }

        SQLiteDatabase sqliteDb = mOpenHelper.getReadableDatabase();

        Cursor cursor = queryBuilder.query(sqliteDb, projection, selection, selectionArgs, null, null, sortOrder);
        //Log.d(TAG, "query: rows lenght returned for cursor = " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES_MOVIES:
                return FavoritesMoviesContract.CONTENT_TYPE;
            case FAVORITES_MOVIES_ID:
                return FavoritesMoviesContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        //Log.d(TAG, "insert: match = " + match);

        final SQLiteDatabase db;
        Uri returnUri = null;
        long recordID;

        switch (match) {
            case FAVORITES_MOVIES:
                db = mOpenHelper.getWritableDatabase();
                recordID = db.insert(FavoritesMoviesContract.TABLE_NAME, null, contentValues);

                if (recordID <= 0) {
                    throw new SQLException("Error on inserting new data into " + uri.toString());
                }

                returnUri = FavoritesMoviesContract.buildMoviesUri(recordID);
                break;
            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }

        if (recordID > 0) {
            //Log.d(TAG, "insert: notifyChange, uri = " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Log.d(TAG, "delete called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        //Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case FAVORITES_MOVIES_ID:
                db = mOpenHelper.getWritableDatabase();
                long movieID = FavoritesMoviesContract.getMovieID(uri);

                selectionCriteria = FavoritesMoviesContract.Columns.TMDB_ID + " = " + movieID;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(FavoritesMoviesContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }

        if (count > 0) {
            //Notify about delete operation
            //Log.d(TAG, "delete: Uri: " + uri + " was removed.");
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        //Log.d(TAG, "update: called with uri: " + uri);
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case FAVORITES_MOVIES_ID:
                db = mOpenHelper.getWritableDatabase();
                long movieID = FavoritesMoviesContract.getMovieID(uri);

                selectionCriteria = FavoritesMoviesContract.Columns.TMDB_ID + " = " + movieID;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(FavoritesMoviesContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }

        if (count > 0) {
            // something was updated
            //Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            //Log.d(TAG, "update: nothing updated");
        }

        return count;
    }
}
