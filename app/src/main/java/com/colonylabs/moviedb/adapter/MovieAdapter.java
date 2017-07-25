package com.colonylabs.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.colonylabs.moviedb.R;
import com.colonylabs.moviedb.models.Result;
import com.colonylabs.moviedb.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmana on 6/17/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private List<Result> movieList;
    private ClickListener mListener;

    public MovieAdapter(ClickListener listener) {
        this.movieList = new ArrayList<>();
        this.mListener = listener;
    }

    public void addItem(Result item) {
        movieList.add(item);
        notifyDataSetChanged();
    }

    public void clearItem() {
        movieList.clear();
        notifyDataSetChanged();
    }

    public Result getSelectedItem(int position) {
        return movieList.get(position);
    }

    public List<Result> showList() {
        return movieList;
    }

    public void setData(List<Result> data) {
        for (int i = 0; i < data.size(); i++) {
            movieList.add(data.get(i));
        }

        notifyDataSetChanged();
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row, parent, false);
        MovieHolder holder = new MovieHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        String imageBaseUrl = Constant.MOVIE_IMAGE_BASE_URL;
        Context context = holder.imgPoster.getContext();
        Picasso.with(context)
                .load(imageBaseUrl + movieList.get(position).getPosterPath())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public interface ClickListener {
        void onClick(int position);
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgPoster;

        public MovieHolder(View itemView) {
            super(itemView);
            imgPoster = (ImageView) itemView.findViewById(R.id.imgPoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mListener.onClick(getLayoutPosition());
        }
    }
}
