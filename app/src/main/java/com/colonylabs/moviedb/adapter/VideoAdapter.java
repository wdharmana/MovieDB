package com.colonylabs.moviedb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colonylabs.moviedb.R;
import com.colonylabs.moviedb.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MovieHolder> {

    private List<Video> movieList;
    private ClickListener mListener;

    public VideoAdapter(ClickListener listener) {
        this.movieList = new ArrayList<>();
        this.mListener = listener;
    }

    public void addItem(Video item) {
        movieList.add(item);
        notifyDataSetChanged();
    }

    public void clearItem() {
        movieList.clear();
        notifyDataSetChanged();
    }

    public Video getSelectedItem(int position) {
        return movieList.get(position);
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row, parent, false);
        MovieHolder holder = new MovieHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        holder.txtName.setText(movieList.get(position).getName());

        Context context = holder.imgThumb.getContext();
        Picasso.with(context).load("https://img.youtube.com/vi/" + movieList.get(position).getKey() + "/hqdefault.jpg").into(holder
                .imgThumb);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public interface ClickListener {
        void onVideoClick(int position);
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName;
        ImageView imgThumb;

        public MovieHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.name);
            imgThumb = (ImageView) itemView.findViewById(R.id.imgThumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mListener.onVideoClick(getLayoutPosition());
        }
    }
}
