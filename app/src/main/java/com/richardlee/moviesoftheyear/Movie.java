package com.richardlee.moviesoftheyear;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;

public class Movie implements Serializable, Comparable<Movie> {

    public static final long serialVersionUID = 20170103L;

    private Integer mID;
    private Integer mTmdbID;
    private String mTitle;
    private String mReleaseDate;
    private float mVoteAverage;
    private String mPosterUri;
    private String mOverview;
    private String mStatus;
    private String mGenres;
    private String mMainTrailer;

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM, yyyy", Locale.getDefault());
    private static SimpleDateFormat comparableFormatter = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

    public Movie() {
    }

    public Movie(MovieDb movieDb, String language) {
        mTmdbID = movieDb.getId();
        mTitle = movieDb.getTitle();

        try {
            mReleaseDate = dateFormatter.format(new SimpleDateFormat("yyyy-MM-dd").parse(movieDb.getReleaseDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }

        mVoteAverage = movieDb.getVoteAverage();
        mPosterUri = movieDb.getBackdropPath();
        mOverview = movieDb.getOverview();
        mStatus = movieDb.getStatus();

        if (movieDb.getGenres() != null)
            mGenres = formatGenres(movieDb.getGenres());

        //Tricky to solve specific pt-BR translates issues not solved by the Api
        if (StringUtils.equals(language, "pt-BR")) {
            if (mStatus != null) {

                //TODO: Move it to another class, it should be inject in it by DI
                switch (mStatus.toLowerCase()) {
                    case "released":
                        mStatus = "Em Cartaz";
                        break;
                    case "in production":
                        mStatus = "Em Produção";
                        break;
                    case "post production":
                        mStatus = "Pós-Produção";
                        break;
                    case "planned":
                        mStatus = "Planejado";
                        break;
                    default:
                        break;

                }
            }
        }
    }

    private String formatGenres(List<Genre> genres) {

        String formattedGenre = "";
        for (Genre genre :
                genres) {
            formattedGenre = formattedGenre + genre.getName() + " | ";
        }
        formattedGenre = StringUtils.removeEnd(formattedGenre, " | ");
        return formattedGenre;
    }

    private String formatGenresConcat(List<String> genresText) {

        String formattedGenre = "";
        for (String genre :
                genresText) {
            formattedGenre = formattedGenre + genre + " | ";
        }
        formattedGenre = StringUtils.removeEnd(formattedGenre, " | ");
        return formattedGenre;
    }

    public Integer getID() {
        return mID;
    }

    public Integer getTmdbID() {
        return mTmdbID;
    }

    public void setTmdbID(Integer tmdbID) {
        mTmdbID = tmdbID;
    }

    public void setID(Integer ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getPosterUri() {
        return mPosterUri;
    }

    public void setPosterUri(String posterUri) {
        mPosterUri = posterUri;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getGenres() {
        return mGenres;
    }

    public String getMainGenres() {

        String mainGenres = this.getGenres();

        if (mainGenres != null) {
            String[] genres = StringUtils.split(this.getGenres(), "|");

            if (genres.length > 3) {
                ArrayList<String> firstGenres = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    firstGenres.add(genres[i]);
                }
                mainGenres = formatGenresConcat(firstGenres);
            }
        }
        return mainGenres;
    }

    public void setGenres(String genres) {
        mGenres = genres;
    }

    public String getMainTrailer() {
        return mMainTrailer;
    }

    public void setMainTrailer(String mainTrailer) {
        mMainTrailer = mainTrailer;
    }

    @Override
    public int compareTo(Movie movie) {

        int comparisonResult = 0;

        try {
            SimpleDateFormat comparableFormatter = new SimpleDateFormat("MMM, yyyy", Locale.getDefault());

            Date instanceItemDate = comparableFormatter.parse(this.getReleaseDate());
            Date incomingItemDate = comparableFormatter.parse(movie.getReleaseDate());

            if (instanceItemDate.after(incomingItemDate)) {
                comparisonResult = 1;
            } else if (incomingItemDate.after(instanceItemDate)) {
                comparisonResult = -1;
            } else {
                comparisonResult = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }

        return comparisonResult;
    }
}