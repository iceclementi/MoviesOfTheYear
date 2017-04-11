package com.richardlee.moviesoftheyear;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import info.movito.themoviedbapi.model.Video;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment extends BaseFragment
        implements RecyclerItemClickListener.OnRecylerClickListener {

    private static final String TAG = "MoviesListFragment";

    public static final String MOVIE_LIST_TYPE = "MOVIE_LIST_TYPE";
    public static final String MOVIES_LOADED = "MOVIES_LOADED";
    private static final String HELP_WAS_SHOWN = "HELP_WAS_SHOWN";

    private OnMovieClickListener mOnMovieClickListener;
    private OnLoadNewData mOnLoadNewDataListener;

    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private Context context;
    private ProgressBar progress;
    private RecyclerView movieRecycler;
    private LinearLayoutManager layoutManager;
    private Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);

    private ArrayList<Movie> moviesToFirstLoad;
    private GetMoviesListAsync.OnMoviesAvailable mOnMoviesAvailable;
    private AsyncTask<MoviesListType, Void, List<Movie>> mMoviesListTask;

    private GetSpecialMoviesListAsync.OnSpecialListMoviesAvailable mOnSpecialMoviesListAvailable;
    private AsyncTask<SpecialListType, Void, List<Movie>> mSpecialMoviesListTask;

    private ItemTouchHelper itemTouchHelper;
    private String mApiLanguage;
    private boolean mHelpViewWasShown;
    private ImageView gifloader;
    private MoviesListType mMovieListType;
    private FirebaseAnalytics mFirebaseAnalytics;


    interface OnMovieClickListener {
        void onMovieItemClick(View view, Movie movie);
    }

    interface OnLoadNewData {
        void onLoadMovies(List<Movie> movies);
    }

    public MoviesListFragment() {
        //Log.d(TAG, "MoviesListFragment: custom ctor starts");
    }


    @Override
    public void onAttach(Context context) {
        //Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        this.context = context;

        Activity activity = getActivity();

        if (!(activity instanceof OnMovieClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + "must implement MoviesListFragment.OnMovieClickListener interface");
        }
        mOnMovieClickListener = (OnMovieClickListener) activity;

        if ((activity instanceof OnLoadNewData))
            mOnLoadNewDataListener = (OnLoadNewData) activity;

        mOnMoviesAvailable = new GetMoviesListAsync.OnMoviesAvailable() {

            @Override
            public void onMoviesAvailable(List<Movie> movies) {
                //Log.d(TAG, "mOnMoviesAvailable: with " + movies.size());

                loadMovies(movies);
            }
        };

        mOnSpecialMoviesListAvailable = new GetSpecialMoviesListAsync.OnSpecialListMoviesAvailable() {

            @Override
            public void onMoviesAvailable(List<Movie> movies) {
                //Log.d(TAG, "mOnSpecialMoviesListAvailable: with " + movies.size());

                loadMovies(movies);
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.d(TAG, "onCreate: starts");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        setRetainInstance(true);

        //if (savedInstanceState != null) {
        //    mHelpViewWasShown = savedInstanceState.getBoolean(HELP_WAS_SHOWN);
        //}

        moviesToFirstLoad = new ArrayList<>();
        //TODO: Research how to store state data from activity even after onDestroy call - This goal will reached using MVP?
        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovieListType = (MoviesListType) arguments.getSerializable(MOVIE_LIST_TYPE);
        }

        mApiLanguage = getString(R.string.api_language);

        switch (mMovieListType) {
            case MARVEL_UNIVERSE:
                mSpecialMoviesListTask = new GetSpecialMoviesListAsync(mOnSpecialMoviesListAvailable, mApiLanguage)
                        .execute(SpecialListType.MARVEL_UNIVERSE);
                break;
            case DC_UNIVERSE:
                mSpecialMoviesListTask = new GetSpecialMoviesListAsync(mOnSpecialMoviesListAvailable, mApiLanguage)
                        .execute(SpecialListType.DC_UNIVERSE);
                break;
            case TOP_GROSSING:
                mSpecialMoviesListTask = new GetSpecialMoviesListAsync(mOnSpecialMoviesListAvailable, mApiLanguage)
                        .execute(SpecialListType.TOP_GROSSING);
                break;
            case PETER_JACKSON_MOVIES:
                mSpecialMoviesListTask = new GetSpecialMoviesListAsync(mOnSpecialMoviesListAvailable, mApiLanguage)
                        .execute(SpecialListType.PETER_JACKSON_MOVIES);
                break;
            default:

                mMoviesListTask = new GetMoviesListAsync(mOnMoviesAvailable, mApiLanguage)
                        .execute(mMovieListType);
                break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        gifloader = (ImageView) view.findViewById(R.id.gifloader);


        layoutManager = new LinearLayoutManager(context);

        movieRecycler = (RecyclerView) view.findViewById(R.id.rcv_movies);
        movieRecycler.setLayoutManager(layoutManager);

        // Listeners
        movieRecycler.addOnItemTouchListener(new RecyclerItemClickListener(context, movieRecycler, this));

        //Decorator
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(movieRecycler.getContext(),
                layoutManager.getOrientation());
        movieRecycler.addItemDecoration(dividerItemDecoration);

        // Adapter
        mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(context, moviesToFirstLoad);
        movieRecycler.setAdapter(mMoviesRecyclerViewAdapter);

        if (moviesToFirstLoad.size() == 0) {

            Random randomGenerator = new Random();

            ArrayList<Integer> items = new ArrayList<Integer>();
            items.add(R.drawable.hex);
            items.add(R.drawable.hex);
            items.add(R.drawable.animal);


            gifloader.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(R.drawable.animal)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(gifloader);

            int random = items.get(randomGenerator.nextInt(items.size()));

           /* if (random == R.drawable.animal) {

            } else {
                Glide.with(context)
                        .load(random)
                        .asGif().fitCenter()
                        .into(gifloader);
            }*/


        } else

        {
            gifloader.setVisibility(View.GONE);
            movieRecycler.setVisibility(View.VISIBLE);
        }

        initSwipe();

        //Log.d(TAG, "onCreateView: ends, with movies: " + moviesToFirstLoad.size());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(movieRecycler);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(HELP_WAS_SHOWN, mHelpViewWasShown);
        outState.putSerializable(MOVIE_LIST_TYPE, mMovieListType);
    }


    @Override
    public void onDetach() {
        //Log.d(TAG, "onDetach: starts");
        super.onDetach();

        context = null;
        mOnMovieClickListener = null;
        mOnLoadNewDataListener = null;

        mOnMoviesAvailable = null;
        //Clean up all references for view objects (that references the activity context) to prevent memory leak
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMoviesListTask != null)
            mMoviesListTask.cancel(true);

        mMoviesListTask = null;

        if (mSpecialMoviesListTask != null)
            mSpecialMoviesListTask.cancel(true);

        mSpecialMoviesListTask = null;
    }

    @Override
    public void onItemClick(View view, int position) {

        Movie movie = mMoviesRecyclerViewAdapter.getMovie(position);

        if (movie != null) {
            mOnMovieClickListener.onMovieItemClick(view, movie);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if (!YouTubeIntents.isYouTubeInstalled(getContext())) {

                    Snackbar.make(getView(), getString(R.string.should_install_youtube_app), Snackbar.LENGTH_LONG)
                            .show();

                    itemTouchHelper.attachToRecyclerView(null);
                    itemTouchHelper.attachToRecyclerView(movieRecycler);
                    return;
                }

                progress.setVisibility(View.VISIBLE);
                progress.bringToFront();

                final Movie movie = mMoviesRecyclerViewAdapter.getMovie(position);

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

                            try {
                                Intent intent = new Intent(getActivity(), YoutubePlayVideoActivity.class);
                                intent.putExtra(YoutubePlayVideoActivity.YOUTUBE_VIDEO_ID, videos.get(0).getKey());

                                startActivity(intent);

                            } catch (Exception exc) {

                                Snackbar.make(getView(), R.string.user_general_error, Snackbar.LENGTH_LONG)
                                        .show();

                                exc.printStackTrace();
                                FirebaseCrash.report(exc);
                            }
                        } else {
                            Snackbar.make(getView(), getString(R.string.no_trailer_associated), Snackbar.LENGTH_LONG)
                                    .show();

                            itemTouchHelper.attachToRecyclerView(null);
                            itemTouchHelper.attachToRecyclerView(movieRecycler);
                        }
                    }
                }, mApiLanguage).execute(movie.getTmdbID());

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
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(movieRecycler);
    }

    private void loadMovies(List<Movie> movies) {
        Collections.sort(movies, Collections.<Movie>reverseOrder());

        moviesToFirstLoad = (ArrayList<Movie>) movies;

        mMoviesRecyclerViewAdapter.loadNewData(moviesToFirstLoad);

        if (mOnLoadNewDataListener != null)
            mOnLoadNewDataListener.onLoadMovies(moviesToFirstLoad);

        gifloader.setVisibility(View.GONE);

        if (moviesToFirstLoad != null && moviesToFirstLoad.size() > 0) {
            movieRecycler.setVisibility(View.VISIBLE);

            if (!mHelpViewWasShown) {
                mHelpViewWasShown = true;

                buildHelperInstance(true)
                        .show(SpotlightHelper.HELP_ITEM_MOVIES_LIST_FIRST_CONTACT_ID);
            }
        }
    }

    private SpotlightHelper buildHelperInstance(boolean showTitle) {

        String title = (showTitle) ? getString(R.string.help_first_access) : "";

        return new SpotlightHelper(
                getActivity(),
                getContext(),
                movieRecycler.getChildAt(0).findViewById(R.id.imgv_movie_photo),
                title,
                getString(R.string.help_first_access_subtitle));
    }

    public void showHelpFromOutside() {

        buildHelperInstance(false)
                .show(SpotlightHelper.HELP_ITEM_MOVIES_LIST_FIRST_CONTACT_ID, 0);
    }
}
