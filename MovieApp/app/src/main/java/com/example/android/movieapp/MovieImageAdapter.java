package com.example.android.movieapp;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.movieapp.entity.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Abhishek on 10/20/2015.
 */
public class MovieImageAdapter extends BaseAdapter {

    private final ArrayList<Movie> movies;
    private final int hight;
    private final int width;
    private Context context;

    public MovieImageAdapter(Context context){
        movies = new ArrayList<Movie>();
        hight = Math.round(context.getResources().getDimension(R.dimen.dp240));
        width = Math.round(context.getResources().getDimension(R.dimen.dp250));
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        if(position <0 || position >= movies.size()){
            return null;
        }
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        Movie movie = getItem(position);

        if(movie == null){
            return  -1L;
        }
        return movie.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        if(movie == null){
            return  null;
        }

        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(width, hight));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else{
            imageView = (ImageView) convertView;
        }

        Uri posterUri = movie.posterUri("w185");
        Picasso.with(context).load(posterUri).into(imageView);


        return imageView;
    }

    public void addMovies(Collection<Movie> movieList){
        movies.addAll(movieList);
        notifyDataSetChanged();
    }
}
