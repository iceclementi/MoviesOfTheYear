package com.richardlee.moviesoftheyear;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.richardlee.moviesoftheyear.MoviesAppProvider.CONTENT_AUTHORITY;
import static com.richardlee.moviesoftheyear.MoviesAppProvider.CONTENT_AUTHORITY_URI;

public class FavoritesMoviesContract {

    static final String TABLE_NAME = "FavoritesMovies";

    public static final class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TMDB_ID = "TmdbID";
        public static final String TITLE = "Title";
        public static final String RELEASE_DATE = "ReleaseDate";
        public static final String AVERAGE = "AverageScore";
        public static final String POSTER_URI = "PosterUri";
        public static final String OVERVIEW = "Overview";
        public static final String STATUS = "Status";
        public static final String GENRES = "Genres";
        public static final String MAIN_TRAILER = "MainTrailer";

        private Columns() {
        }
    }


    /**
     * Uris to access the FavoritesMovies table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    //TODO Give a review on it
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    static Uri buildMoviesUri(long movieID) {
        return ContentUris.withAppendedId(CONTENT_URI, movieID);
    }

    static long getMovieID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
