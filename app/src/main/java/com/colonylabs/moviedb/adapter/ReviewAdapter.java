package com.colonylabs.moviedb.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colonylabs.moviedb.R;
import com.colonylabs.moviedb.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter() {
        this.reviewList = new ArrayList<>();
    }

    public void addItem(Review item) {
        reviewList.add(item);
        notifyDataSetChanged();
    }

    public void clearItem() {
        reviewList.clear();
        notifyDataSetChanged();
    }

    public Review getSelectedItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        ReviewHolder holder = new ReviewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.txtName.setText(review.getAuthor());
        holder.txtReview.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public class ReviewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtReview;

        public ReviewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.name);
            txtReview = (TextView) itemView.findViewById(R.id.review);
        }

    }
}
