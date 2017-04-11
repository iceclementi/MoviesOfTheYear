package com.richardlee.moviesoftheyear;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbGenre;
import info.movito.themoviedbapi.TmdbLists;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonPeople;

import static org.junit.Assert.assertFalse;

public class TmdbIntegrationTest {

    String mDefaultAPILanguge = "en-US";

    @Test
    public void onListMovies_should_bring_common_fields() {
        TmdbMovies moviesAPI = TmdbApiFactory.getTmdbApi().getMovies();

        MovieResultsPage moviesResults = moviesAPI.getNowPlayingMovies(mDefaultAPILanguge, 1);
        List<MovieDb> movies = moviesResults.getResults();

        MovieDb movieSample = movies.get(0);

        assertFalse(isAnyEmpty(
                String.valueOf(movieSample.getId()),
                movieSample.getTitle(),
                movieSample.getBackdropPath(),
                movieSample.getOverview(),
                movieSample.getReleaseDate(),
                String.valueOf(movieSample.getVoteAverage())));
    }

    @Test
    public void OnGetMovie_should_bring_common_fields() {
        TmdbMovies moviesAPI = TmdbApiFactory.getTmdbApi().getMovies();

        MovieDb movieDetails = moviesAPI.getMovie(346672, mDefaultAPILanguge);

        assertFalse(isAnyEmpty(
                String.valueOf(movieDetails.getId()),
                movieDetails.getTitle(),
                movieDetails.getBackdropPath(),
                movieDetails.getOverview(),
                movieDetails.getReleaseDate(),
                String.valueOf(movieDetails.getVoteAverage()),

                movieDetails.getPosterPath(),
                movieDetails.getStatus()
        ));
    }

    @Test
    public void OnListGenres_should_bring_common_fields() {
        TmdbGenre genreAPI = TmdbApiFactory.getTmdbApi().getGenre();

        Genre genre = genreAPI.getGenreList(mDefaultAPILanguge).get(0);

        assertFalse(isAnyEmpty(
                String.valueOf(genre.getId()),
                genre.getName()
        ));
    }

    @Test
    public void OnListMoviesOfActionGenre_should_not_be_empty() {
        TmdbGenre genreAPI = TmdbApiFactory.getTmdbApi().getGenre();

        MovieResultsPage genreMoviesResult = genreAPI.getGenreMovies(28, mDefaultAPILanguge, 1, false);
        List<MovieDb> moviesFromGenre = genreMoviesResult.getResults();

        assertFalse(moviesFromGenre.isEmpty());
    }

    @Test
    public void OnListPeople_should_bring_common_fields() {
        TmdbPeople peopleAPI = TmdbApiFactory.getTmdbApi().getPeople();

        TmdbPeople.PersonResultsPage personResults = peopleAPI.getPersonPopular(1);
        List<Person> persons = personResults.getResults();
        Person personSample = persons.get(0);

        assertFalse(isAnyEmpty(
                String.valueOf(personSample.getId()),
                personSample.getName(),
                personSample.getProfilePath()
        ));
    }

    @Test
    public void OnGetPeople_should_bring_common_fields() {
        TmdbPeople peopleAPI = TmdbApiFactory.getTmdbApi().getPeople();

        PersonPeople personInfo = peopleAPI.getPersonInfo(8784);

        assertFalse(isAnyEmpty(
                String.valueOf(personInfo.getId()),
                personInfo.getName(),
                personInfo.getProfilePath()
        ));
    }

    // Tests of Customs Lists
    @Test
    public void OnListDCComicsUniverse_should_not_be_empty() {
        TmdbLists listAPI = TmdbApiFactory.getTmdbApi().getLists();

        List<MovieDb> movies = listAPI.getList("3").getItems();

        assertFalse(movies.isEmpty());
    }

    @Test
    public void OnListMarvelUniverse_should_not_be_empty() {
        TmdbLists listAPI = TmdbApiFactory.getTmdbApi().getLists();

        List<MovieDb> movies = listAPI.getList("1").getItems();

        assertFalse(movies.isEmpty());
    }

    @Test
    public void OnListTop50GrossingFilms_should_not_be_empty() {
        TmdbLists listAPI = TmdbApiFactory.getTmdbApi().getLists();

        List<MovieDb> movies = listAPI.getList("10").getItems();

        assertFalse(movies.isEmpty());
    }

    @Test
    public void OnListPeterJacksonFilms_should_not_be_empty() {
        TmdbLists listAPI = TmdbApiFactory.getTmdbApi().getLists();

        List<MovieDb> movies = listAPI.getList("34").getItems();

        assertFalse(movies.isEmpty());
    }


    private boolean isAnyEmpty(String... values) {

        for (int i = 0; i < values.length; i++) {

            if (StringUtils.isEmpty(values[i])) {
                return true;
            }
        }
        return false;
    }

}
