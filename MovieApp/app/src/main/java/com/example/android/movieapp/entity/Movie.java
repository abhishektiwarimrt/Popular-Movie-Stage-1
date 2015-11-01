package com.example.android.movieapp.entity;

import android.net.Uri;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abhishek on 10/20/2015.
 */
public class Movie {

    public final long id;
    public final String title;
    public final String overview;
    public final String poster_path;
    public final double vote_average;
    public final long vote_count;
    public final String release_date;

    public static final String KEY_ID="id";
    public static final String KEY_TITLE="title";
    public static final String KEY_OVERVIEW="overview";
    public static final String KEY_POSTER_PATH="poster_path";
    public static final String KEY_VOTE_AVERAGE="vote_average";
    public static final String KEY_VOTE_COUNT="vote_count";
    public static final String KEY_RELEASE_DATE="release_date";
    public static final String EXTRA_MOVIE = "com.example.android.movieapp.EXTRA_MOVIE";


    public Movie(long id, String title, String overview, String poster_path, double vote_average, long vote_count, String release_date){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
    }

    public Movie(Bundle bundle){
        this(
                bundle.getLong(KEY_ID),
                bundle.getString(KEY_TITLE),
                bundle.getString(KEY_OVERVIEW),
                bundle.getString(KEY_POSTER_PATH),
                bundle.getDouble(KEY_VOTE_AVERAGE),
                bundle.getLong(KEY_VOTE_COUNT),
                bundle.getString(KEY_RELEASE_DATE)
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putString(KEY_POSTER_PATH, poster_path);
        bundle.putDouble(KEY_VOTE_AVERAGE, vote_average);
        bundle.putLong(KEY_VOTE_COUNT, vote_count);
        bundle.putString(KEY_RELEASE_DATE, release_date);


        return bundle;
    }

    public String getRating() {
        return "" + vote_average + " / 10";
    }

    public Uri posterUri(String size){
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath(size).appendEncodedPath(poster_path).build();

        return uri;
    }

    public static Movie getMovieFromJson(JSONObject movieJsonObject) throws JSONException {
        return new Movie(
                movieJsonObject.getLong(KEY_ID),
                movieJsonObject.getString(KEY_TITLE),
                movieJsonObject.getString(KEY_OVERVIEW),
                movieJsonObject.getString(KEY_POSTER_PATH),
                movieJsonObject.getDouble(KEY_VOTE_AVERAGE),
                movieJsonObject.getLong(KEY_VOTE_COUNT),
                movieJsonObject.getString(KEY_RELEASE_DATE)
        );
    }
}
