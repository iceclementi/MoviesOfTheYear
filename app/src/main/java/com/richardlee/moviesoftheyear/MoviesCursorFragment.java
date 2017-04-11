package com.richardlee.moviesoftheyear;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.security.InvalidParameterException;
import java.util.List;

import info.movito.themoviedbapi.model.Video;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesCursorFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerItemClickListener.OnRecylerClickListener {

    private static final String TAG = "MoviesCursorFragment";

    public static final int LOADER_ID = 0;// ID of current cursor of this fragment
    private static final java.lang.String HELP_WAS_SHOWN = "HELP_WAS_SHOWN";

    private MoviesCursorFragment.OnMovieClickListener mMovieClickListener;
    private MoviesRecyclerViewCursorAdapter mMoviesRecyclerViewAdapter;

    private ProgressBar progress;
    private RecyclerView mMovieRecycler;
    private Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
    private ContentResolver contentResolver;
    private Cursor mMoviesCursor;
    private ItemTouchHelper itemTouchHelper;
    private String mApiLanguage;
    private boolean mHelpViewWasShown;

    private FirebaseAnalytics mFirebaseAnalytics;

    interface OnMovieClickListener {
        void onMovieItemClick(View view, Movie movie);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Context context) {
        //Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        Activity activity = getActivity();

        if (!(activity instanceof OnMovieClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + "must implement MoviesListFragment.MoviesListFragment interface");
        }
        mMovieClickListener = (OnMovieClickListener) activity;

        contentResolver = activity.getContentResolver();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        mApiLanguage = getString(R.string.api_language);

        if (savedInstanceState != null) {
            mHelpViewWasShown = savedInstanceState.getBoolean(HELP_WAS_SHOWN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mMovieRecycler = (RecyclerView) view.findViewById(R.id.rcv_movies);
        mMovieRecycler.setLayoutManager(layoutManager);

        // Listeners
        mMovieRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mMovieRecycler, this));

        //Decorator
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mMovieRecycler.getContext(),
                layoutManager.getOrientation());
        mMovieRecycler.addItemDecoration(dividerItemDecoration);

        mMoviesRecyclerViewAdapter = new MoviesRecyclerViewCursorAdapter(getContext(), null);
        mMovieRecycler.setAdapter(mMoviesRecyclerViewAdapter);

        initSwipe();

        //Log.d(TAG, "onCreateView: ends");
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(HELP_WAS_SHOWN, mHelpViewWasShown);
    }

    @Override
    public void onDetach() {
        //Log.d(TAG, "onDetach: starts");
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(mMovieRecycler);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMovieClickListener = null;
    }

    // LOADER Overrides ---

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.d(TAG, "onCreateLoader: with id: " + id);

        String[] projection = {FavoritesMoviesContract.Columns._ID,
                FavoritesMoviesContract.Columns.TMDB_ID,
                FavoritesMoviesContract.Columns.TITLE,
                FavoritesMoviesContract.Columns.RELEASE_DATE,
                FavoritesMoviesContract.Columns.AVERAGE,
                FavoritesMoviesContract.Columns.POSTER_URI,
                FavoritesMoviesContract.Columns.OVERVIEW,
                FavoritesMoviesContract.Columns.STATUS,
                FavoritesMoviesContract.Columns.GENRES,
                FavoritesMoviesContract.Columns.MAIN_TRAILER
        };

        String sortOrder = FavoritesMoviesContract.Columns.AVERAGE + " DESC";

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        FavoritesMoviesContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id" + id);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Log.d(TAG, "onLoadFinished: starts");

        mMoviesCursor = data;
        mMoviesRecyclerViewAdapter.swapCursosr(mMoviesCursor);

        progress.setVisibility(ProgressBar.GONE);

        mMovieRecycler.setVisibility(((mMoviesCursor.getCount() > 0)) ? View.VISIBLE : View.GONE);

        if (mMoviesCursor.getCount() > 0) {

            if (!mHelpViewWasShown) {
                mHelpViewWasShown = true;

                buildHelperInstance()
                        .show(SpotlightHelper.HELP_ITEM_FAVORITE_MOVIES_ID);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Log.d(TAG, "onLoaderReset: starts");
        mMoviesCursor = null;
        mMoviesRecyclerViewAdapter.swapCursosr(null);
    }

    // RecyclerLister Overrides ---

    @Override
    public void onItemClick(View view, int position) {
        Movie movie = mMoviesRecyclerViewAdapter.getMovie(position);

        if (movie != null) {
            mMovieClickListener.onMovieItemClick(view, movie);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    //Log.d(TAG, "onSwiped: left");

                    Movie movie = mMoviesRecyclerViewAdapter.getMovie(position);
                    long movieId = movie.getTmdbID();

                    mMoviesRecyclerViewAdapter.getMovie(position);
                    Uri movieUri = FavoritesMoviesContract.buildMoviesUri(movieId);
                    //Log.d(TAG, "onSwiped: movieUri: " + movieUri);

                    mMoviesCursor.setNotificationUri(getContext().getContentResolver(), movieUri);
                    contentResolver.delete(movieUri, null, null);

                    if (BuildConfig.ANALYTICS_ENABLED) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movie.getTmdbID().toString());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                        mFirebaseAnalytics.logEvent("removeFavoritedMovie", bundle);
                    }
                    //Log.d(TAG, "onSwiped: ends");
                } else {

                    if (!YouTubeIntents.isYouTubeInstalled(getContext())) {

                        Snackbar.make(getView(), getString(R.string.should_install_youtube_app), Snackbar.LENGTH_LONG)
                                .show();

                        itemTouchHelper.attachToRecyclerView(null);
                        itemTouchHelper.attachToRecyclerView(mMovieRecycler);
                        return;
                    }

                    final Movie movie = mMoviesRecyclerViewAdapter.getMovie(position);
                    String trailerKey = movie.getMainTrailer();

                    if (trailerKey != null) {

                        if (BuildConfig.ANALYTICS_ENABLED) {
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movie.getTmdbID().toString());
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                            mFirebaseAnalytics.logEvent("watchFavoritedTrailer", bundle);
                        }

                        try {
                            Intent intent = new Intent(getContext(), YoutubePlayVideoActivity.class);
                            intent.putExtra(YoutubePlayVideoActivity.YOUTUBE_VIDEO_ID, trailerKey);

                            startActivity(intent);
                        } catch (Exception exc) {

                            Snackbar.make(getView(), R.string.user_general_error, Snackbar.LENGTH_LONG)
                                    .show();

                            exc.printStackTrace();
                            FirebaseCrash.report(exc);
                        }

                    } else {

                        progress.setVisibility(View.VISIBLE);
                        progress.bringToFront();

                        new GetVideosAsync(new GetVideosAsync.OnVideosAvailable() {
                            @Override
                            public void onVideosAvailable(List<Video> videos) {

                                progress.setVisibility(View.GONE);

                                if (videos != null && videos.size() > 0) {

                                    if (BuildConfig.ANALYTICS_ENABLED) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movie.getTmdbID().toString());
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                                        mFirebaseAnalytics.logEvent("watchTrailer", bundle);
                                    }

                                    Intent intent = new Intent(getContext(), YoutubePlayVideoActivity.class);
                                    intent.putExtra(YoutubePlayVideoActivity.YOUTUBE_VIDEO_ID, videos.get(0).getKey());

                                    startActivity(intent);
                                } else {
                                    Snackbar.make(getView(), getString(R.string.no_trailer_associated), Snackbar.LENGTH_LONG)
                                            .show();

                                    itemTouchHelper.attachToRecyclerView(null);
                                    itemTouchHelper.attachToRecyclerView(mMovieRecycler);
                                }
                            }
                        }, mApiLanguage).execute(movie.getTmdbID());
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;


                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getContext(), R.color.accent_dark));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);

                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_circle_outline_white_48dp, options);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        c.clipRect(background);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear_white_48dp, options);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mMovieRecycler);
    }

    private SpotlightHelper buildHelperInstance() {
        return new SpotlightHelper(
                getActivity(),
                getContext(),
                mMovieRecycler.getChildAt(0).findViewById(R.id.imgv_movie_photo),
                null,
                getString(R.string.help_favorites_subtitle)
        );
    }

    public void showHelpFromOutside() {
        buildHelperInstance()
                .show(SpotlightHelper.HELP_ITEM_FAVORITE_MOVIES_ID, 0);
    }
}
