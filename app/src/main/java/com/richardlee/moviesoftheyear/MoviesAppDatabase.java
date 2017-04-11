package com.richardlee.moviesoftheyear;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class MoviesAppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "MoviesAppDatabase";
    public static final String DATABASE_NAME = "MoviesApp.db";
    public static final int DATABASE_VERSION = 1;

    private static MoviesAppDatabase instance = null;

    private MoviesAppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //Log.d(TAG, "MoviesAppDatabase: constructor");
    }

    static MoviesAppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new MoviesAppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Log.d(TAG, "onCreate: starts");

        String sSQL = "CREATE TABLE " + FavoritesMoviesContract.TABLE_NAME + "("
                + FavoritesMoviesContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + FavoritesMoviesContract.Columns.TMDB_ID + " INTEGER NOT NULL, "
                + FavoritesMoviesContract.Columns.TITLE + " TEXT NOT NULL, "
                + FavoritesMoviesContract.Columns.RELEASE_DATE + " TEXT, "
                + FavoritesMoviesContract.Columns.AVERAGE + " INTEGER, "
                + FavoritesMoviesContract.Columns.POSTER_URI + " TEXT, "
                + FavoritesMoviesContract.Columns.OVERVIEW + " TEXT, "
                + FavoritesMoviesContract.Columns.STATUS + " TEXT, "
                + FavoritesMoviesContract.Columns.GENRES + " TEXT, "
                + FavoritesMoviesContract.Columns.MAIN_TRAILER + " TEXT"
                + ");";

        sqLiteDatabase.execSQL(sSQL);

        //Log.d(TAG, "onCreate: ends with sql executed: " + sSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            default:
                throw new IllegalStateException("onUpgrade invoked with unknow oldVersion:" + oldVersion);
        }
    }
}
