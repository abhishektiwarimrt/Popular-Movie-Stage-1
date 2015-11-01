package com.example.android.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.entity.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieImageAdapter movieImages;
    private TextView textViewLoading;
    public static final int MAX_PAGES = 100;
    private boolean isLoading = false;
    private byte pageLoaded = 0;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        movieImages = new MovieImageAdapter(getActivity());
        textViewLoading=(TextView)view.findViewById(R.id.textview_loading);

        initializeGridView(view);

        loadPages();

        return view;
    }

    private void initializeGridView(View view){
        GridView gridView = (GridView)view.findViewById(R.id.gridview_movies);

        if(gridView == null){
            return;
        }

        gridView.setAdapter(movieImages);

        gridView.setOnScrollListener(
                new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == totalItemCount) {
                            loadPages();
                        }
                    }
                }
        );

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieImageAdapter movieImageAdapter = (MovieImageAdapter)parent.getAdapter();
                        Movie movie = movieImageAdapter.getItem(position);

                        if(movie == null){
                            return;
                        }

                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
                        getActivity().startActivity(intent);
                    }
                }
        );

    }

    private void loadPages() {
        if (isLoading) {
            return;
        }

        if (pageLoaded >= MAX_PAGES) {
            return;
        }

        isLoading = true;
        if (textViewLoading != null) {
            textViewLoading.setVisibility(View.VISIBLE);
        }

        new FetchMovieTask().execute(pageLoaded + 1);
    }

    private void stopPageLoading(){
        if(!isLoading){
            return;
        }
        isLoading = false;

        if(textViewLoading != null){
            textViewLoading.setVisibility(View.GONE);
        }
    }


    private class FetchMovieTask extends AsyncTask<Integer, Void, Collection<Movie>>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Collection<Movie> doInBackground(Integer... params) {

            if(params.length == 0){
                return null;
            }

            int page = params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReadereader = null;
            String movieJsonStr = null;

            try {
                final String BASE_API_URL = "http://api.themoviedb.org/3/movie";
                final String PARAM_API_PAGE = "page";
                final String PARAM_API_KEY = "api_key";
                final String SORTING_API_PARAMETER = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sorting", "popular");

                Uri uriMovie = Uri.parse(BASE_API_URL).buildUpon()
                        .appendPath(SORTING_API_PARAMETER)
                        .appendQueryParameter(PARAM_API_PAGE, String.valueOf(page))
                        .appendQueryParameter(PARAM_API_KEY, getString(R.string.api_key))
                        .build();

                URL url = new URL(uriMovie.toString());

                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if(inputStream == null){
                    return null;
                }

                bufferedReadereader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReadereader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = stringBuffer.toString();
                Log.i(LOG_TAG, movieJsonStr);
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReadereader != null) {
                    try {
                        bufferedReadereader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return getMovieDataFromJson(movieJsonStr);
            }
            catch (JSONException ex){
                Log.e(LOG_TAG,"MovieJSON String Parse Failed: "+ movieJsonStr, ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Collection<Movie> movies) {
            if(movies == null){
                Toast.makeText(
                        getActivity(),
                        "Server Error",
                        Toast.LENGTH_SHORT
                ).show();

                stopPageLoading();
                return;
            }

            pageLoaded++;

            stopPageLoading();

            movieImages.addMovies(movies);

        }

        private Collection<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {
            final String KEY_MOVIE_ARRAY = "results";

            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray moviesArray = jsonObject.getJSONArray(KEY_MOVIE_ARRAY);

            List<Movie> movieList = new ArrayList<Movie>();


            for (int index = 0; index < moviesArray.length(); index++) {
                JSONObject movieObject = moviesArray.getJSONObject(index);
                movieList.add(Movie.getMovieFromJson(movieObject));
            }

            return movieList;
        }


    }
}
