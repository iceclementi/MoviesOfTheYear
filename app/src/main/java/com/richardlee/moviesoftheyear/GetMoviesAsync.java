package com.richardlee.moviesoftheyear;

import android.os.AsyncTask;

import com.google.firebase.crash.FirebaseCrash;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbLists;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

enum MoviesListType {
    NOW_PLAYING, POPULAR, UPCOMING, SIMILAR, TOP_RATED, MARVEL_UNIVERSE, DC_UNIVERSE, TOP_GROSSING, PETER_JACKSON_MOVIES
}

class GetMoviesListAsync extends AsyncTask<MoviesListType, Void, List<Movie>> {

    private static final String TAGCURRENT = "TAGCURRENT";
    private static final String TAG = "GetMoviesListAsync";
    private OnMoviesAvailable mOnMoviesAvailableListener;
    private final String mLanguage;
    private TmdbMovies mMoviesAPI;

    public GetMoviesListAsync(OnMoviesAvailable callback, String language) {
        mOnMoviesAvailableListener = callback;//TODO: Test it later, by remving the WeakReference to callback
        mLanguage = language;
    }

    interface OnMoviesAvailable {
        void onMoviesAvailable(List<Movie> movies);
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        //Log.d(TAG, "onPostExecute: ends");

        if (mOnMoviesAvailableListener != null && !isCancelled()) {
            mOnMoviesAvailableListener.onMoviesAvailable(movies);
        }
    }

    @Override
    protected void onCancelled(List<Movie> movies) {
        super.onCancelled(movies);
        //Log.d(TAG, "onCancelled: starts");
    }

    @Override
    protected List<Movie> doInBackground(MoviesListType... moviesListTypes) {
        //Log.d(TAG, "doInBackground: starts");
        //Log.d(TAGCURRENT, "doInBackground: starts");

        List<Movie> movies = null;

        try {
            //TODO: Remove it later
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;//TODO: For tests ('cause o thread sleep above)

            mMoviesAPI = TmdbApiFactory.getTmdbApi().getMovies();//TODO:Deixar isso mais dinamico

            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;
            MoviesListType moviesType = moviesListTypes[0];
            MovieResultsPage moviesResults = null;

            switch (moviesType) {
                case NOW_PLAYING:
                    moviesResults = mMoviesAPI.getNowPlayingMovies(mLanguage, 1);
                    break;
                case TOP_RATED:
                    moviesResults = mMoviesAPI.getTopRatedMovies(mLanguage, 1);
                    break;
                case UPCOMING:
                    moviesResults = mMoviesAPI.getUpcoming(mLanguage, 1);
                    break;
                default:
                    moviesResults = null;
                    break;
            }

            //TODO: Remove it later
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;
            List<MovieDb> movieDbs = moviesResults.getResults();


            if (movieDbs != null && movieDbs.size() > 0) {
                movies = new ArrayList<Movie>();

                for (MovieDb movieDb : movieDbs) {
                    movies.add(new Movie(movieDb, mLanguage));
                }
            }

        } catch (Exception exc) {
            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
        //Log.d(TAGCURRENT, "doInBackground: ends");

        return movies;
    }
}

class GetMovieDetailsAsync extends AsyncTask<Integer, Void, Movie> {

    private static final String TAG = "GetMovieDetailsAsync";
    private final OnMovieDetailsAvailable mOnMovieDetailsAvailable;
    private final String mLanguage;
    private TmdbMovies mMoviesAPI;

    public GetMovieDetailsAsync(OnMovieDetailsAvailable onMovieDetailsAvailable, String language) {
        mOnMovieDetailsAvailable = onMovieDetailsAvailable;
        mLanguage = language;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        //Log.d(TAG, "onPostExecute: with " + movie);

        if (mOnMovieDetailsAvailable != null && !isCancelled()) {
            mOnMovieDetailsAvailable.onMovieAvailable(movie);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        //Log.d(TAG, "onCancelled: starts");
    }

    @Override
    protected Movie doInBackground(Integer... moviesID) {

        Movie movie = null;

        try {
            if (isCancelled()) return null;
            mMoviesAPI = TmdbApiFactory.getTmdbApi().getMovies();

            //TODO: Remove it later
            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (isCancelled()) return null;
            MovieDb movieDb = mMoviesAPI.getMovie(moviesID[0], mLanguage);

            if (movieDb != null) {
                movie = new Movie(movieDb, mLanguage);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }

        return movie;
    }

    interface OnMovieDetailsAvailable {
        void onMovieAvailable(Movie movie);
    }
}

class GetVideosAsync extends AsyncTask<Integer, Void, List<Video>> {

    private static final String TAG = "GetVideosAsync";
    private TmdbMovies mMoviesAPI;
    private final String mLanguage;
    private OnVideosAvailable mOnVideosAvailable = null;
    private WeakReference<OnVideosAvailable> mOnVideosAvailableWeakRef = null;


    public GetVideosAsync(OnVideosAvailable callback, String language) {
        mOnVideosAvailable = callback;
        mLanguage = language;
    }

    public GetVideosAsync(WeakReference<OnVideosAvailable> callback, String language) {
        mOnVideosAvailableWeakRef = callback;
        mLanguage = language;
    }

    @Override
    protected void onPostExecute(List<Video> videos) {
        //Log.d(TAG, "onPostExecute: with " + videos);

        if (mOnVideosAvailable != null && !isCancelled())
            mOnVideosAvailable.onVideosAvailable(videos);

        if (mOnVideosAvailableWeakRef != null && !isCancelled())
            mOnVideosAvailableWeakRef.get().onVideosAvailable(videos);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        //Log.d(TAG, "onCancelled: starts");
    }

    @Override
    protected List<Video> doInBackground(Integer... movieID) {

        List<Video> videos = null;
        try {
            if (isCancelled()) return null;

            mMoviesAPI = TmdbApiFactory.getTmdbApi().getMovies();

            //TODO: Remove it later
           /* try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            if (isCancelled()) return null;
            if (mMoviesAPI != null)
                videos = mMoviesAPI.getVideos(movieID[0], mLanguage);

        } catch (Exception exc) {
            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
        return videos;
    }

    interface OnVideosAvailable {
        void onVideosAvailable(List<Video> videos);
    }
}

class GetCreditsAsync extends AsyncTask<Integer, Void, Credits> {
    private static final String TAG = "GetCreditsAsync";
    private final OnCreditsAvailable mOnCreditsAvailableListener;
    private TmdbMovies mMoviesAPI;

    public GetCreditsAsync(OnCreditsAvailable callback) {
        mOnCreditsAvailableListener = callback;
    }

    @Override
    protected void onPostExecute(Credits credits) {

        if (mOnCreditsAvailableListener != null && !isCancelled()) {
            mOnCreditsAvailableListener.onCreditsAvailable(credits);
        }
    }

    @Override
    protected Credits doInBackground(Integer... moviesID) {

        Credits credits = null;
        try {
            if (isCancelled()) return null;

            mMoviesAPI = TmdbApiFactory.getTmdbApi().getMovies();

            //TODO: Remove it later
            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (isCancelled()) return null;
            credits = mMoviesAPI.getCredits(moviesID[0]);

        } catch (Exception exc) {
            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
        return credits;
    }

    interface OnCreditsAvailable {
        void onCreditsAvailable(Credits credits);
    }
}

enum SpecialListType {
    MARVEL_UNIVERSE, DC_UNIVERSE, TOP_GROSSING, PETER_JACKSON_MOVIES
}

class GetSpecialMoviesListAsync extends AsyncTask<SpecialListType, Void, List<Movie>> {

    private static final String TAG = "GetSpecialMoviesListAsync";
    private OnSpecialListMoviesAvailable mOnMoviesAvailableListener;
    private final String mLanguage;
    private TmdbLists mSpecialListAPI;

    public GetSpecialMoviesListAsync(OnSpecialListMoviesAvailable callback, String language) {
        mOnMoviesAvailableListener = callback;
        mLanguage = language;
    }

    interface OnSpecialListMoviesAvailable {
        void onMoviesAvailable(List<Movie> movies);
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        //Log.d(TAG, "onPostExecute: ends");

        if (mOnMoviesAvailableListener != null && !isCancelled()) {
            mOnMoviesAvailableListener.onMoviesAvailable(movies);
        }
    }

    @Override
    protected void onCancelled(List<Movie> movies) {
        super.onCancelled(movies);
        //Log.d(TAG, "onCancelled: starts");
    }

    @Override
    protected List<Movie> doInBackground(SpecialListType... specialListTypes) {
        //Log.d(TAG, "doInBackground: starts");
        //Log.d(TAGCURRENT, "doInBackground: starts");

        List<Movie> movies = null;

        try {
            //TODO: Remove it later
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;//TODO: For tests ('cause o thread sleep above)

            mSpecialListAPI = TmdbApiFactory.getTmdbApi().getLists();

            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;
            SpecialListType listType = specialListTypes[0];

            List<MovieDb> movieDbs;
            switch (listType) {
                case MARVEL_UNIVERSE:
                    movieDbs = mSpecialListAPI.getList("1").getItems();
                    break;
                case DC_UNIVERSE:
                    movieDbs = mSpecialListAPI.getList("3").getItems();
                    break;
                case TOP_GROSSING:
                    movieDbs = mSpecialListAPI.getList("10").getItems();
                    break;
                case PETER_JACKSON_MOVIES:
                    movieDbs = mSpecialListAPI.getList("34").getItems();
                    break;
                default:
                    movieDbs = null;
                    break;
            }

            //TODO: Remove it later
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

            //Log.d(TAGCURRENT, "was cancelled? : " + isCancelled());
            if (isCancelled()) return null;


            if (movieDbs != null && movieDbs.size() > 0) {
                movies = new ArrayList<Movie>();

                for (MovieDb movieDb : movieDbs) {
                    movies.add(new Movie(movieDb, mLanguage));
                }
            }

        } catch (Exception exc) {
            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
        //Log.d(TAGCURRENT, "doInBackground: ends");

        return movies;
    }
}