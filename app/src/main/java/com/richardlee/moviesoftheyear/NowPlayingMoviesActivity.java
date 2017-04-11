package com.richardlee.moviesoftheyear;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import static com.richardlee.moviesoftheyear.SpecialListActivity.SPECIAL_LIST_TYPE;

//TODO: I have two layout files (only because of the elevation property wich does not work with api level under 21), but on genymotion emulation, even not setting elevation (favorites movies) the toolbar appears be 'elevated', install the app on a mobile 21+ and check this UI appearance, and remove elevation wheter its true
public class NowPlayingMoviesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MoviesListFragment.OnMovieClickListener {

    private static final String TAG = "NowPlayingMovAct";
    private static final String MOVIES_LOADED = "MOVIES_LOADED";

    private DrawerLayout drawer;
    private ArrayList<Movie> mMovies = null;

    private static final String TAG_NOW_PLAYING_MOVIES_FRAGMENT = "TAG_NOW_PLAYING_MOVIES_FRAGMENT";
    private FirebaseAnalytics mFirebaseAnalytics;
    private MoviesListFragment mMoviesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate: starts");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_now_playing_movies);
        setSupportActionBar(toolbar);

        // Boilterplate code for Drawer Layout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_now_playing_movies);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (BuildConfig.ANALYTICS_ENABLED) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, "NowPlayingMoviesActivity");
            mFirebaseAnalytics.logEvent("Access-NowPlayingMovies", bundle);
        }

        if (!DeviceStatusHandler.isOnline(this)) {
            Toast.makeText(this, R.string.device_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        mMoviesListFragment = (MoviesListFragment) fm.findFragmentByTag(TAG_NOW_PLAYING_MOVIES_FRAGMENT);

        if (mMoviesListFragment == null) {

            mMoviesListFragment = new MoviesListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(MoviesListFragment.MOVIE_LIST_TYPE, MoviesListType.NOW_PLAYING);

            mMoviesListFragment.setArguments(bundle);

            fm.beginTransaction().add(R.id.fragment_movies, mMoviesListFragment, TAG_NOW_PLAYING_MOVIES_FRAGMENT)
                    .commit();
        }

        //TODO: Research how to store state data from activity even after onDestroy call - This goal will reached using MVP?
        //MoviesListFragment mMoviesListFragment = new MoviesListFragment();
        /*if (savedInstanceState != null) {

            Serializable moviesSerializable = savedInstanceState.getSerializable(MOVIES_LOADED);
            if (moviesSerializable != null) {

                mMovies = (ArrayList<Movie>) moviesSerializable;

                Bundle bundle = new Bundle();
                bundle.putSerializable(MoviesListFragment.MOVIES_LOADED, mMovies);

                mMoviesListFragment.setArguments(bundle);
            }
        }*/

        //Log.d(TAG, "onCreate: ends");
    }

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.d(TAG, "onSaveInstanceState: starts");
        super.onSaveInstanceState(outState);

        if (mMovies != null) {
            outState.putSerializable(MOVIES_LOADED, mMovies);
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause: starts: " + isFinishing());
    }

    @Override
    protected void onDestroy() {
        //Log.d(TAG, "onDestroy: starts");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume: starts");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                Intent intent = new Intent(NowPlayingMoviesActivity.this, CreditsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_help:
                SpotlightHelper.resetUsageIds();

                mMoviesListFragment.showHelpFromOutside();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed: starts");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_now_playing_movies);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Log.d(TAG, "onNavigationItemSelected: starts");
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_favorited_movies:
                intent = new Intent(NowPlayingMoviesActivity.this, FavoritesMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_popular_movies:
                intent = new Intent(NowPlayingMoviesActivity.this, PopularMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_upcoming_movies:
                intent = new Intent(NowPlayingMoviesActivity.this, UpComingMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_list_marvel_universe:
                intent = new Intent(NowPlayingMoviesActivity.this, SpecialListActivity.class);
                startSpecialListTypeActivity(intent, SpecialListType.MARVEL_UNIVERSE);
                break;
            case R.id.nav_list_dc_universe:
                intent = new Intent(NowPlayingMoviesActivity.this, SpecialListActivity.class);
                startSpecialListTypeActivity(intent, SpecialListType.DC_UNIVERSE);
                break;
            case R.id.nav_list_top_grossing:
                intent = new Intent(NowPlayingMoviesActivity.this, SpecialListActivity.class);
                startSpecialListTypeActivity(intent, SpecialListType.TOP_GROSSING);
                break;
            case R.id.nav_list_peter_jackson:
                intent = new Intent(NowPlayingMoviesActivity.this, SpecialListActivity.class);
                startSpecialListTypeActivity(intent, SpecialListType.PETER_JACKSON_MOVIES);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMovieItemClick(View view, Movie movie) {

        try {
            if (movie != null) {
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(view, "movie_background_transiction");

                Bundle arguments = new Bundle();
                arguments.putSerializable(MovieDetailsActivity.MOVIE_DETAILS_SERIALIZABLE, movie);

                Intent intent = new Intent(this, MovieDetailsActivity.class);
                intent.putExtras(arguments);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
            }

        } catch (Exception exc) {
            //? Do i really need capture this exception at this point?

            Snackbar.make(this.getCurrentFocus(), R.string.user_general_error, Snackbar.LENGTH_LONG)
                    .show();

            exc.printStackTrace();
            FirebaseCrash.report(exc);
        }
    }

    private void startSpecialListTypeActivity(Intent intent, SpecialListType specialListType) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(SPECIAL_LIST_TYPE, specialListType);

        intent.putExtras(arguments);
        startActivity(intent);
    }


}