package com.richardlee.moviesoftheyear;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wooplr.spotlight.SpotlightConfig;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.people.PersonCast;

import static com.richardlee.moviesoftheyear.R.id.main_content;

public class MovieDetailsActivity extends AppCompatActivity implements GetMovieDetailsAsync.OnMovieDetailsAvailable,
        GetCreditsAsync.OnCreditsAvailable, GetVideosAsync.OnVideosAvailable {

    private static final String TAG = "MovieDetailsActivity";
    public static final String MOVIE_DETAILS_PARAM_ID = "MOVIE_DETAILS_PARAM_ID";
    public static final String MOVIE_DETAILS_SERIALIZABLE = "MOVIE_DETAILS_SERIALIZABLE";
    public static final String MOVIE_DETAILS_RELOAD_OVERVIEW = "MOVIE_DETAILS_RELOAD_OVERVIEW";
    private static final String CURRENT_MOVIE = "CURRENT_MOVIE";
    private static final String FAVORITED = "FAVORITED";
    private static final String CURRENT_MAIN_ACTORS = "CURRENT_MAIN_ACTORS";

    private static final String CURRENT_MAIN_TRAILER = "CURRENT_MAIN_TRAILER";
    private static final String CURRENT_OTHERS_TRAILERS = "CURRENT_OTHERS_TRAILERS";
    private static final String CURRENT_OTHERS_TRAILERS_NAMES = "CURRENT_OTHERS_TRAILERS_NAMES";
    private static final String CURRENT_MOVIE_FIRST_LOADED = "CURRENT_MOVIE_FIRST_LOADED ";

    private static final String TAG_MOVIES_ASYNC_TASKS_FRAGMENT = "TAG_MOVIES_ASYNC_TASKS_FRAGMENT";
    private static final java.lang.String HELP_WAS_SHOWN = "HELP_WAS_SHOWN";

    private Movie mMovie;
    private Movie mMovieFirstLoad;

    private boolean favorited = false;
    private String imageRootPath = TmdbApiFactory.getImageRootPath(IMAGE_SIZE.W500);
    private String imageFullPath;
    private Uri itemInsertedUri;
    private ContentResolver contentResolver;

    private Video mMainTrailer;
    private ArrayList<Video> mOtherTrailers;
    private ArrayList<String> mOtherTrailerNames;
    private ArrayAdapter<String> trailerAdapter;

    private ArrayList<String> mActorNames;
    private ArrayAdapter<String> actorAdapter;


    private ImageView imgvPhoto;
    private TextView txtvReleaseDate;
    private TextView txtvVoteAverage;
    private TextView txtvStatus;
    private TextView txtvOverview;
    private CollapsingToolbarLayout collapsingToolbar;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton btnAddToFavorites;
    private TextView txtv_movie_details_categories;
    private Button btnWatchTrailer;
    private ListView listTrailers;
    private CardView trailersCardContainer;

    private ListView listActors;
    private CardView actorsCardContainer;
    private MovieDetailsTasksFragment mMovieDetailsTasksFragment;
    private SpotlightConfig spotlightConfig;
    private boolean mHelpViewWasShown;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean imageLoaded;
    private boolean mReloadOverview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate: starts");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowReturnTransitionOverlap(false);
            getWindow().setSharedElementEnterTransition(enterTransition());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details2);

        imageLoaded = false;
        coordinatorLayout = (CoordinatorLayout) findViewById(main_content);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        imgvPhoto = (ImageView) findViewById(R.id.background_photo);

        txtv_movie_details_categories = (TextView) findViewById(R.id.txtv_movie_details_categories);
        txtvReleaseDate = (TextView) findViewById(R.id.release_date);
        txtvVoteAverage = (TextView) findViewById(R.id.vote_average);
        txtvOverview = (TextView) findViewById(R.id.overview);
        txtvStatus = (TextView) findViewById(R.id.status);
        btnAddToFavorites = (FloatingActionButton) findViewById(R.id.add_to_favorites);

        listTrailers = (ListView) findViewById(R.id.trailers);
        trailersCardContainer = (CardView) findViewById(R.id.trailers_card_container);
        trailersCardContainer.setVisibility(View.GONE);

        btnWatchTrailer = (Button) findViewById(R.id.watch_trailer);
        btnWatchTrailer.setVisibility(View.GONE);

        listActors = (ListView) findViewById(R.id.actors);
        actorsCardContainer = (CardView) findViewById(R.id.actors_card_container);
        actorsCardContainer.setVisibility(View.GONE);

        contentResolver = getContentResolver();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (savedInstanceState != null) {

            favorited = savedInstanceState.getBoolean(FAVORITED);
            mMovie = (Movie) savedInstanceState.getSerializable(CURRENT_MOVIE);
            mMovieFirstLoad = (Movie) savedInstanceState.getSerializable(CURRENT_MOVIE_FIRST_LOADED);
            mReloadOverview = savedInstanceState.getBoolean(MOVIE_DETAILS_RELOAD_OVERVIEW);

            if (favorited) {
                btnAddToFavorites.setImageResource(R.drawable.ic_star_yellow_24dp);
            }

            mActorNames = (ArrayList<String>) savedInstanceState.getSerializable(CURRENT_MAIN_ACTORS);

            mMainTrailer = (Video) savedInstanceState.getSerializable(CURRENT_MAIN_TRAILER);
            mOtherTrailerNames = savedInstanceState.getStringArrayList(CURRENT_OTHERS_TRAILERS_NAMES);
            mOtherTrailers = (ArrayList<Video>) savedInstanceState.getSerializable(CURRENT_OTHERS_TRAILERS);

            mHelpViewWasShown = savedInstanceState.getBoolean(HELP_WAS_SHOWN);

        } else {
            Intent intent = getIntent();

            mMovie = (Movie) intent.getSerializableExtra(MOVIE_DETAILS_SERIALIZABLE);
            mMovieFirstLoad = mMovie;

            mReloadOverview = intent.getBooleanExtra(MOVIE_DETAILS_RELOAD_OVERVIEW, false);
        }

        FragmentManager fm = getSupportFragmentManager();
        mMovieDetailsTasksFragment = (MovieDetailsTasksFragment) fm.findFragmentByTag(TAG_MOVIES_ASYNC_TASKS_FRAGMENT);

        if (mMovieDetailsTasksFragment == null) {

            Bundle bundle = new Bundle();

            bundle.putString(MovieDetailsTasksFragment.API_LANGUAGE, getString(R.string.api_language));
            bundle.putInt(MovieDetailsTasksFragment.TMDB_ID, mMovie.getTmdbID());

            mMovieDetailsTasksFragment = new MovieDetailsTasksFragment();
            mMovieDetailsTasksFragment.setArguments(bundle);

            fm.beginTransaction().add(mMovieDetailsTasksFragment, TAG_MOVIES_ASYNC_TASKS_FRAGMENT)
                    .commit();
        }

        setMovieViews(null);

        if (BuildConfig.ANALYTICS_ENABLED) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, "MovieDetailsActivity");
            mFirebaseAnalytics.logEvent("Access-MovieDetails", bundle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause: starts");
    }

    @Override
    protected void onResume() {
        //Log.d(TAG, "onResume: starts");

        mMovieDetailsTasksFragment.startNewMovieDetailsAsyncTask();

        if (mOtherTrailers == null) {
            mMovieDetailsTasksFragment.startNewVideosAsyncTask();
        } else {

            if (mMainTrailer != null &&
                    mOtherTrailerNames != null &&
                    mOtherTrailers != null)
                manageTrailerUIElements();

            if (mActorNames != null)
                manageCreditsUIElements();
        }

        if (!favorited) {
            //TODO: Move implementation to a background UI thread
            final String[] projection = new String[]{"count(" + FavoritesMoviesContract.Columns.TMDB_ID + ")"};

            Cursor cursor = contentResolver.query(FavoritesMoviesContract.buildMoviesUri(mMovie.getTmdbID()),
                    projection, null, null, null);

            cursor.moveToFirst();
            //Log.d(TAG, "onResume: cursor query count: " + cursor.getInt(0));

            favorited = cursor.getInt(0) > 0;

            if (favorited) {
                btnAddToFavorites.setImageResource(R.drawable.ic_star_yellow_24dp);
            }
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed: starts");

        //There is a current issue with sharedElementTransictions in androud M and Nougat (api 23+)
        //https://code.google.com/p/android/issues/detail?id=211678

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            this.btnAddToFavorites.setVisibility(View.INVISIBLE);

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Log.d(TAG, "onDestroy: starts! is finishing? " + isFinishing());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.d(TAG, "onSaveInstanceState: starts");
        super.onSaveInstanceState(outState);

        outState.putSerializable(CURRENT_MOVIE, mMovie);
        outState.putSerializable(CURRENT_MOVIE_FIRST_LOADED, mMovieFirstLoad);
        outState.putBoolean(FAVORITED, favorited);

        outState.putBoolean(MOVIE_DETAILS_RELOAD_OVERVIEW, mReloadOverview);

        outState.putSerializable(CURRENT_MAIN_TRAILER, mMainTrailer);
        outState.putSerializable(CURRENT_OTHERS_TRAILERS, mOtherTrailers);
        outState.putStringArrayList(CURRENT_OTHERS_TRAILERS_NAMES, mOtherTrailerNames);

        outState.putSerializable(CURRENT_MAIN_ACTORS, mActorNames);

        outState.putBoolean(HELP_WAS_SHOWN, mHelpViewWasShown);
    }

    public void addToFavorite(View view) {

        if (BuildConfig.ANALYTICS_ENABLED) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mMovie.getTmdbID().toString());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mMovie.getTitle());
            mFirebaseAnalytics.logEvent("addToFavorite", bundle);
        }

        Snackbar snackbar;

        if (favorited) {
            snackbar = Snackbar.make(coordinatorLayout, R.string.movie_already_added_to_favorites, Snackbar.LENGTH_LONG);

            snackbar.setAction(getString(R.string.action_remove), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    contentResolver.delete(FavoritesMoviesContract.buildMoviesUri(mMovie.getTmdbID()), null, null);

                    favorited = false;
                    btnAddToFavorites.setImageResource(R.drawable.ic_star_white_24dp);

                    Snackbar.make(coordinatorLayout, R.string.action_removed, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }).show();
            return;
        }

        ContentValues contentValues = extractValuesFromMovie();

        itemInsertedUri = contentResolver.insert(FavoritesMoviesContract.CONTENT_URI, contentValues);

        //Log.d(TAG, "btnFavorite onClick: insert mMovie done!");

        favorited = true;

        btnAddToFavorites.setImageResource(R.drawable.ic_star_yellow_24dp);
        snackbar = Snackbar.make(coordinatorLayout, R.string.action_saved_on_favorites, Snackbar.LENGTH_SHORT);

        snackbar.setAction(R.string.action_to_undone, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "snackbar onClick: trying to remove uri: " + itemInsertedUri);
                contentResolver.delete(itemInsertedUri, null, null);

                favorited = false;
                btnAddToFavorites.setImageResource(R.drawable.ic_star_white_24dp);

                Snackbar.make(coordinatorLayout, R.string.action_undoned, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        snackbar.show();
    }

    private void setMovieViews(@Nullable Boolean updateWithNewApiData) {
        //Log.d(TAG, "setMovieViews: starts");

        if (updateWithNewApiData != null && updateWithNewApiData) {
            txtvStatus.setText(mMovie.getStatus());
            txtv_movie_details_categories.setText(mMovie.getMainGenres());

            if (mReloadOverview) {
                if (mMovie.getOverview() != null && !StringUtils.isEmpty(mMovie.getOverview()))
                    txtvOverview.setText(mMovie.getOverview());
                else
                    txtvOverview.setText(mMovieFirstLoad.getOverview());
            }

            if (favorited) {
                updateFavoritedMovieTrailer();
            }
        } else {
            collapsingToolbar.setTitle(mMovieFirstLoad.getTitle());

            txtv_movie_details_categories.setText(mMovieFirstLoad.getMainGenres());
            txtvReleaseDate.setText(mMovieFirstLoad.getReleaseDate());
            txtvVoteAverage.setText(String.valueOf(mMovieFirstLoad.getVoteAverage()));
            txtvStatus.setText(mMovieFirstLoad.getStatus());

            if (!mReloadOverview) {
                txtvOverview.setText(mMovieFirstLoad.getOverview());
            }

            imageFullPath = imageRootPath + mMovieFirstLoad.getPosterUri();

            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(TAG, "[picasso] onBitmapLoaded: starts - " + mMovieFirstLoad.getTitle());
                    imgvPhoto.setImageBitmap(bitmap);
                    imageLoaded = true;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(TAG, "[picasso] onBitmapFailed: FAILED - " + mMovieFirstLoad.getTitle());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d(TAG, "[picasso] onPrepareLoad: preparing - " + mMovieFirstLoad.getTitle());
                }
            };

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(MovieDetailsActivity.this)
                            .load(imageFullPath)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .noFade()
                            .error(R.drawable.backgroundmaterial)
                            .into(target);
                }
            });

            // Operation to check if image was loaded by Picasso library:
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: [picasso check] - starts");

                    if (!imageLoaded) {
                        Log.d(TAG, "run: [picasso error] - trying load image again - " + mMovieFirstLoad.getTitle());
                        Picasso.with(MovieDetailsActivity.this)
                                .load(imageFullPath)
                                .noFade()
                                .error(R.drawable.backgroundmaterial)
                                .into(target);
                    }
                }
            }, 1300);

            /*Glide.with(MovieDetailsActivity.this)
                    .load(imageFullPath)
                    .asBitmap()
                    .imageDecoder(new StreamBitmapDecoder(MovieDetailsActivity.this, DecodeFormat.PREFER_ARGB_8888))
                    .error(R.drawable.backgroundmaterial)
                    .into(imgvPhoto);*/
        }

        txtv_movie_details_categories.post(new Runnable() {
            @Override
            public void run() {
                if (txtv_movie_details_categories.getLineCount() > 1)
                    collapsingToolbar.setExpandedTitleMarginBottom(90);//TODO: Does not working on some devices (e.g. Samsung Nexus S7 Edge) - update library later
            }
        });

        if (!mHelpViewWasShown) {
            mHelpViewWasShown = true;

            new SpotlightHelper(this, this, btnAddToFavorites, null, getString(R.string.help_add_fav_movies))
                    .show(SpotlightHelper.HELP_ITEM_MOVIE_DETAILS_ADD_TO_FAV_ID);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition enterTransition() {

        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(300);

        return bounds;
    }

    private void startYoutubePlayerIntent(String videoId) {

        if (!YouTubeIntents.isYouTubeInstalled(this)) {

            Snackbar.make(coordinatorLayout, getString(R.string.should_install_youtube_app), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        try {
            Intent intent = new Intent(MovieDetailsActivity.this, YoutubePlayVideoActivity.class);
            intent.putExtra(YoutubePlayVideoActivity.YOUTUBE_VIDEO_ID, videoId);

            startActivity(intent);
        } catch (Exception exc) {

            Snackbar.make(coordinatorLayout, R.string.user_general_error, Snackbar.LENGTH_LONG)
                    .show();

            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
    }

    public void getTotalHeightofListView(ListView listView) {
        //Retrieved from H N Sharma answer on: http://stackoverflow.com/questions/25197133/how-to-calculate-listview-height-of-different-height-listitem-in-android
        //TODO: Move to some 'helper' class

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;
        int listWidth = listView.getMeasuredWidth();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void updateFavoritedMovieTrailer() {
        ContentValues contentValues = extractValuesFromMovie();

        int updated = contentResolver.update(FavoritesMoviesContract.buildMoviesUri(mMovie.getTmdbID()), contentValues, null, null);
        //Log.d(TAG, "updateFavoritedMovieTrailer: " + updated);
    }

    private ContentValues extractValuesFromMovie() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FavoritesMoviesContract.Columns.TMDB_ID, mMovie.getTmdbID());

        if (!TextUtils.isEmpty(mMovie.getTitle())) {
            contentValues.put(FavoritesMoviesContract.Columns.TITLE, mMovie.getTitle());
        }

        if (!TextUtils.isEmpty(mMovie.getReleaseDate())) {
            contentValues.put(FavoritesMoviesContract.Columns.RELEASE_DATE, mMovie.getReleaseDate());
        }

        if (mMovie.getVoteAverage() > -1) {
            contentValues.put(FavoritesMoviesContract.Columns.AVERAGE, String.valueOf(mMovie.getVoteAverage()));
        }

        if (!TextUtils.isEmpty(mMovie.getPosterUri())) {
            contentValues.put(FavoritesMoviesContract.Columns.POSTER_URI, mMovie.getPosterUri());
        }

        if (!TextUtils.isEmpty(mMovie.getOverview())) {
            contentValues.put(FavoritesMoviesContract.Columns.OVERVIEW, mMovie.getOverview());
        }

        if (!TextUtils.isEmpty(mMovie.getStatus())) {
            contentValues.put(FavoritesMoviesContract.Columns.STATUS, mMovie.getStatus());
        }

        if (!TextUtils.isEmpty(mMovie.getGenres())) {
            contentValues.put(FavoritesMoviesContract.Columns.GENRES, mMovie.getGenres());
        }

        if (!TextUtils.isEmpty(mMovie.getMainTrailer())) {
            contentValues.put(FavoritesMoviesContract.Columns.MAIN_TRAILER, mMovie.getMainTrailer());
        }

        return contentValues;
    }

    @Override
    public void onMovieAvailable(Movie movie) {
        //Log.d(TAG, "onMovieAvailable: " + movie);

        try {
            if (movie != null) {
                mMovie = movie;
                setMovieViews(true);
            }
        } catch (Exception exc) {

            Snackbar.make(coordinatorLayout, R.string.user_general_error, Snackbar.LENGTH_LONG)
                    .show();

            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
    }

    @Override
    public void onCreditsAvailable(Credits credits) {
        //Log.d(TAG, "onCreditsAvailable: starts");

        try {
            if (credits != null) {
                mActorNames = new ArrayList<String>();
                List<PersonCast> personCasts = credits.getCast();

                if (personCasts != null && personCasts.size() > 0) {
                    for (int i = 0; i <= 5; i++) {

                        if (personCasts.get(i) != null)
                            mActorNames.add(personCasts.get(i).getName());
                    }

                    manageCreditsUIElements();
                }
            }
        } catch (Exception exc) {

            Snackbar.make(coordinatorLayout, R.string.user_general_error, Snackbar.LENGTH_LONG)
                    .show();

            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
    }

    private void manageCreditsUIElements() {
        //Log.d(TAG, "manageCreditsUIElements: starts");
        if (mActorNames.size() > 0) {
            actorsCardContainer.setVisibility(View.VISIBLE);
        }

        actorAdapter = new ArrayAdapter<String>(MovieDetailsActivity.this, R.layout.actor_item, R.id.actor_name, mActorNames);
        listActors.setAdapter(actorAdapter);
        listActors.post(new Runnable() {
            @Override
            public void run() {
                getTotalHeightofListView(listActors);
            }
        });
    }

    @Override
    public void onVideosAvailable(List<Video> videos) {
        //Log.d(TAG, "onVideosAvailable: starts");

        try {
            if (videos != null && videos.size() > 0) {
                mOtherTrailerNames = new ArrayList<String>();

                mOtherTrailers = new ArrayList<Video>();
                mMainTrailer = null;

                for (Video video : videos) {
                    if (StringUtils.equals(video.getSite().toLowerCase(), "youtube")) {
                        mOtherTrailers.add(video);

                        if (StringUtils.equals(video.getType().toLowerCase(), "trailer")) {
                            mMainTrailer = video;

                            mMovie.setMainTrailer(mMainTrailer.getKey());

                            if (favorited) {
                                updateFavoritedMovieTrailer();
                            }

                        } else {
                            if (mMainTrailer == null)
                                mMainTrailer = video;
                        }
                    }
                }

                mOtherTrailers.remove(mMainTrailer);
                for (Video video : mOtherTrailers) {
                    mOtherTrailerNames.add(video.getName());
                }

                manageTrailerUIElements();
            }

            if (mActorNames == null)
                mMovieDetailsTasksFragment.startNewCreditsAsyncTask();

        } catch (Exception exc) {

            //? Can i create a 'decorator' for its common behavior?
            Snackbar.make(coordinatorLayout, R.string.user_general_error, Snackbar.LENGTH_LONG)
                    .show();

            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
    }

    private void manageTrailerUIElements() {
        //Log.d(TAG, "manageTrailerUIElements: starts");
        if (mOtherTrailerNames.size() > 0) {
            trailersCardContainer.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> trailerAdapter = new ArrayAdapter<String>(MovieDetailsActivity.this, R.layout.trailer_item, R.id.trailer_name, mOtherTrailerNames);
        listTrailers.setAdapter(trailerAdapter);

        listTrailers.post(new Runnable() {
            @Override
            public void run() {
                getTotalHeightofListView(listTrailers);
            }
        });

        listTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String videoName = (String) listTrailers.getItemAtPosition(position);

                for (Video video : mOtherTrailers) {

                    if (StringUtils.equals(video.getName().toLowerCase(), videoName.toLowerCase())) {
                        startYoutubePlayerIntent(video.getKey());
                    }
                }
            }
        });

        if (mMainTrailer != null) {
            btnWatchTrailer.setVisibility(View.VISIBLE);


            btnWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (BuildConfig.ANALYTICS_ENABLED) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mMovie.getTmdbID().toString());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mMovie.getTitle());
                        mFirebaseAnalytics.logEvent("watchTrailerFromDetails", bundle);
                    }
                    startYoutubePlayerIntent(mMainTrailer.getKey());
                }
            });
        }
    }

    public static class MovieDetailsTasksFragment extends Fragment implements GetMovieDetailsAsync.OnMovieDetailsAvailable,
            GetCreditsAsync.OnCreditsAvailable, GetVideosAsync.OnVideosAvailable {

        private static final String TAG = "MovieDetailsTasksFragme";
        public static final String API_LANGUAGE = "API_LANGUAGE";
        private static final String TMDB_ID = "TMDB_ID";

        private GetMovieDetailsAsync.OnMovieDetailsAvailable mOnMovieDetailsAvailable;
        private GetVideosAsync.OnVideosAvailable mOnVideosAvailable;
        private GetCreditsAsync.OnCreditsAvailable mOnCreditsAvailable;

        private Integer mTmdbID;
        private String mApiLanguage;
        private AsyncTask<Integer, Void, Movie> mMovieAsyncTask;
        private AsyncTask<Integer, Void, List<Video>> mVideosAsyncTask;
        private AsyncTask<Integer, Void, Credits> mCreditsAsyncTask;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            //Log.d(TAG, "onAttach: starts");

            Activity activity = getActivity();

            if (!(activity instanceof GetMovieDetailsAsync.OnMovieDetailsAvailable)) {
                throw new ClassCastException(activity.getClass().getSimpleName()
                        + "must implement GetMovieDetailsAsync.OnMovieDetailsAvailableinterface");
            }

            if (!(activity instanceof GetVideosAsync.OnVideosAvailable)) {
                throw new ClassCastException(activity.getClass().getSimpleName()
                        + "must implement GetVideosAsync.OnVideosAvailable");
            }

            if (!(activity instanceof GetCreditsAsync.OnCreditsAvailable)) {
                throw new ClassCastException(activity.getClass().getSimpleName()
                        + "must implement GetCreditsAsync.OnCreditsAvailable");
            }

            mOnMovieDetailsAvailable = (GetMovieDetailsAsync.OnMovieDetailsAvailable) activity;
            mOnVideosAvailable = (GetVideosAsync.OnVideosAvailable) activity;
            mOnCreditsAvailable = (GetCreditsAsync.OnCreditsAvailable) activity;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Log.d(TAG, "onCreate: starts");

            Bundle arguments = getArguments();

            if (arguments == null)
                throw new NullPointerException("The arguments movie ID from Tmdb API and API Language should be informed to perform a async task");

            mApiLanguage = arguments.getString(API_LANGUAGE);
            mTmdbID = arguments.getInt(TMDB_ID);

            setRetainInstance(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            mOnMovieDetailsAvailable = null;
            mOnVideosAvailable = null;
            mOnCreditsAvailable = null;

            mMovieAsyncTask = null;
            mVideosAsyncTask = null;
            mCreditsAsyncTask = null;
        }

        public void startNewMovieDetailsAsyncTask() {

            mMovieAsyncTask = new GetMovieDetailsAsync(this, mApiLanguage).execute(mTmdbID);


            //Log.d(TAG, "StartNewMovieDetailsAsyncTask: ends, a new task was started");
        }

        public void startNewVideosAsyncTask() {

            mVideosAsyncTask = new GetVideosAsync(this, mApiLanguage).execute(mTmdbID);

            //Log.d(TAG, "startNewVideosAsyncTask: ends, a new task was started");
        }

        public void startNewCreditsAsyncTask() {

            mCreditsAsyncTask = new GetCreditsAsync(this).execute(mTmdbID);

            //Log.d(TAG, "startNewCreditsAsyncTask: ends, a new task was started");
        }

        public void cancellAllCurrentAsyncTasks() {

            if (mMovieAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                mMovieAsyncTask.cancel(true);

            if (mVideosAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                mVideosAsyncTask.cancel(true);

            if (mCreditsAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                mCreditsAsyncTask.cancel(true);
        }

        @Override
        public void onMovieAvailable(Movie movie) {

            if (mOnMovieDetailsAvailable != null && !mMovieAsyncTask.isCancelled())
                mOnMovieDetailsAvailable.onMovieAvailable(movie);
        }

        @Override
        public void onVideosAvailable(List<Video> videos) {

            if (mOnVideosAvailable != null && !mVideosAsyncTask.isCancelled())
                mOnVideosAvailable.onVideosAvailable(videos);
        }

        @Override
        public void onCreditsAvailable(Credits credits) {

            if (mOnCreditsAvailable != null && !mCreditsAsyncTask.isCancelled())
                mOnCreditsAvailable.onCreditsAvailable(credits);
        }
    }
}
