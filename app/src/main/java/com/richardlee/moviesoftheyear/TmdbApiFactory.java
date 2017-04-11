package com.richardlee.moviesoftheyear;

import android.support.annotation.Nullable;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.tools.WebBrowser;

enum IMAGE_SIZE {
    W300, W500, W1000
}

public class TmdbApiFactory {

    static final String IMAGE_ROOT_PATH = "https://image.tmdb.org/t/p/";

    private static TmdbApi getTmdbApiUnderProxy() {
        WebBrowser browser = new WebBrowser();
        browser.setProxy(BuildConfig.PROXY_HOST, BuildConfig.PROXY_PORT, BuildConfig.PROXY_USERNAME, BuildConfig.PROXY_PASSWORD);

        return new TmdbApi(BuildConfig.TMDDBPI_KEY, browser, true);
    }

    private static TmdbApi getTmdbApiWithoutProxy() {
        return new TmdbApi(BuildConfig.TMDDBPI_KEY);
    }

    public static TmdbApi getTmdbApi() {

        if (BuildConfig.UNDER_PROXY) {
            return getTmdbApiUnderProxy();
        } else {
            return getTmdbApiWithoutProxy();
        }
    }

    public static String getImageRootPath(@Nullable IMAGE_SIZE imageSize) {
        String imagePathWithSize = IMAGE_ROOT_PATH;

        switch (imageSize) {
            case W300:
                imagePathWithSize += "w300";
                break;
            case W500:
                imagePathWithSize += "w500";
                break;
            case W1000:
                imagePathWithSize += "w1000";
                break;
            default:
                imagePathWithSize += "w300";
                break;
        }

        return imagePathWithSize;
    }
}
