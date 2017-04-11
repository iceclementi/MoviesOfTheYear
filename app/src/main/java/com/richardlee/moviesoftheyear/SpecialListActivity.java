package com.richardlee.moviesoftheyear;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.UUID;

public class SpecialListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MoviesListFragment.OnMovieClickListener {

    private static final String TAG = "SpecialListActivity";

    private DrawerLayout drawer;

    public static final String SPECIAL_LIST_TYPE = "SPECIAL_LIST_TYPE";

    private FirebaseAnalytics mFirebaseAnalytics;
    private SpecialListType currentListType;
    private String mTitle;
    private MoviesListType mMovieListType;
    private MoviesListFragment mMoviesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate: starts");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_list);

        //ToolBar title e fragment tag

        if (savedInstanceState != null) {
            currentListType = (SpecialListType) savedInstanceState.getSerializable(SPECIAL_LIST_TYPE);

        } else {
            Intent intent = getIntent();
            currentListType = (SpecialListType) intent.getSerializableExtra(SPECIAL_LIST_TYPE);

            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if (BuildConfig.ANALYTICS_ENABLED) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ORIGIN, "SpecialListActivity");
                mFirebaseAnalytics.logEvent("Access-" + mTitle, bundle);
            }
        }

        switch (currentListType) {
            case MARVEL_UNIVERSE:
                mTitle = getString(R.string.marvel_universe_title);
                mMovieListType = MoviesListType.MARVEL_UNIVERSE;
                break;
            case DC_UNIVERSE:
                mTitle = getString(R.string.dc_universe_title);
                mMovieListType = MoviesListType.DC_UNIVERSE;
                break;
            case TOP_GROSSING:
                mTitle = getString(R.string.top_grossing_movies_title);
                mMovieListType = MoviesListType.TOP_GROSSING;
                break;
            case PETER_JACKSON_MOVIES:
                mTitle = getString(R.string.peter_jackson_movies_title);
                mMovieListType = MoviesListType.PETER_JACKSON_MOVIES;
                break;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        // Boilterplate code for Drawer Layout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_special_movies);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!DeviceStatusHandler.isOnline(this)) {
            Toast.makeText(this, R.string.device_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        mMoviesListFragment = (MoviesListFragment) fm.findFragmentByTag(mTitle);

        if (mMoviesListFragment == null) {

            mMoviesListFragment = new MoviesListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(MoviesListFragment.MOVIE_LIST_TYPE, mMovieListType);

            mMoviesListFragment.setArguments(bundle);

            fm.beginTransaction().add(R.id.fragment_movies, mMoviesListFragment, mTitle)
                    .commit();
        }

        //Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SPECIAL_LIST_TYPE, currentListType);
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
                Intent intent = new Intent(SpecialListActivity.this, CreditsActivity.class);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_special_movies);
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
                intent = new Intent(SpecialListActivity.this, FavoritesMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_now_playing_movies:
                intent = new Intent(SpecialListActivity.this, NowPlayingMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_popular_movies:
                intent = new Intent(SpecialListActivity.this, PopularMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_upcoming_movies:
                intent = new Intent(SpecialListActivity.this, UpComingMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_list_marvel_universe:
                if (mMovieListType != MoviesListType.MARVEL_UNIVERSE) {
                    intent = new Intent(SpecialListActivity.this, SpecialListActivity.class);
                    startSpecialListTypeActivity(intent, SpecialListType.MARVEL_UNIVERSE);
                }
                break;
            case R.id.nav_list_dc_universe:
                if (mMovieListType != MoviesListType.DC_UNIVERSE) {
                    intent = new Intent(SpecialListActivity.this, SpecialListActivity.class);
                    startSpecialListTypeActivity(intent, SpecialListType.DC_UNIVERSE);
                }
                break;
            case R.id.nav_list_top_grossing:
                if (mMovieListType != MoviesListType.TOP_GROSSING) {
                    intent = new Intent(SpecialListActivity.this, SpecialListActivity.class);
                    startSpecialListTypeActivity(intent, SpecialListType.TOP_GROSSING);
                }
                break;
            case R.id.nav_list_peter_jackson:
                if (mMovieListType != MoviesListType.PETER_JACKSON_MOVIES) {
                    intent = new Intent(SpecialListActivity.this, SpecialListActivity.class);
                    startSpecialListTypeActivity(intent, SpecialListType.PETER_JACKSON_MOVIES);
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMovieItemClick(View view, Movie movie) {

        if (movie != null) {
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(view, "movie_background_transiction");

            Bundle arguments = new Bundle();
            arguments.putSerializable(MovieDetailsActivity.MOVIE_DETAILS_SERIALIZABLE, movie);
            arguments.putBoolean(MovieDetailsActivity.MOVIE_DETAILS_RELOAD_OVERVIEW, true);

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtras(arguments);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
                startActivity(intent, activityOptions.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }

    private void startSpecialListTypeActivity(Intent intent, SpecialListType specialListType) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(SPECIAL_LIST_TYPE, specialListType);

        intent.putExtras(arguments);
        startActivity(intent);
    }


}
