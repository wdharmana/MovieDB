package com.colonylabs.moviedb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.colonylabs.moviedb.adapter.ReviewAdapter;
import com.colonylabs.moviedb.adapter.VideoAdapter;
import com.colonylabs.moviedb.controller.RestManager;
import com.colonylabs.moviedb.models.BaseReview;
import com.colonylabs.moviedb.models.BaseVideo;
import com.colonylabs.moviedb.models.Movie;
import com.colonylabs.moviedb.models.Review;
import com.colonylabs.moviedb.models.Video;
import com.colonylabs.moviedb.utils.Constant;
import com.colonylabs.moviedb.utils.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements VideoAdapter.ClickListener {

    String posterPath;
    ImageView imgPoster;
    TextView txtTitle, txtRelease, txtOverview, txtRate;
    LinearLayout llMain, llProgressBar;
    private RestManager mManager;
    private ProgressDialog mDialog;
    private Movie selectedMovie;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;

    private RecyclerView rvVideo, rvReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        imgPoster = (ImageView) findViewById(R.id.imgPoster);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtRelease = (TextView) findViewById(R.id.txtRelease);
        txtRate = (TextView) findViewById(R.id.txtRate);
        txtOverview = (TextView) findViewById(R.id.txtOverview);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);

        rvVideo = (RecyclerView) findViewById(R.id.rvVideo);
        rvReview = (RecyclerView) findViewById(R.id.rvReview);

        llMain.setVisibility(View.INVISIBLE);

        mManager = new RestManager();

        videoAdapter = new VideoAdapter(this);
        reviewAdapter = new ReviewAdapter();

        Intent curIntent = getIntent();

        if (curIntent.hasExtra(Intent.EXTRA_TEXT)) {
            fetchMovie(curIntent.getStringExtra(Intent.EXTRA_TEXT));
        }
        LinearLayoutManager videoLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvVideo.setLayoutManager(videoLayoutManager);
        rvVideo.setAdapter(videoAdapter);
        rvReview.setLayoutManager(reviewLayoutManager);
        rvReview.setAdapter(reviewAdapter);


    }

    private void fetchMovie(final String movieId) {

        if (getNetworkAvailability()) {

            llMain.setVisibility(View.INVISIBLE);
            llProgressBar.setVisibility(View.VISIBLE);

            Call<Movie> listCall = mManager.getDataService().movieDetail(movieId, Constant.MOVIE_API_KEY);

            listCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {

                    if (response.isSuccessful()) {
                        selectedMovie = response.body();
                        setMovieDetail();
                        fetchVideos(movieId);
                    } else {

                        int sc = response.code();
                        Toast.makeText(getApplicationContext(), "Error code: " + sc, Toast.LENGTH_LONG)
                                .show();

                    }

                    llMain.setVisibility(View.VISIBLE);
                    llProgressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    llMain.setVisibility(View.VISIBLE);
                    llProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

        } else {

            llMain.setVisibility(View.VISIBLE);
            llProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

    }


    private void fetchVideos(final String movieId) {

        Call<BaseVideo> listCall = mManager.getDataService().movieVideos(movieId, Constant.MOVIE_API_KEY);

        listCall.enqueue(new Callback<BaseVideo>() {
            @Override
            public void onResponse(Call<BaseVideo> call, Response<BaseVideo> response) {

                if (response.isSuccessful()) {
                    BaseVideo baseVideo = response.body();
                    if (baseVideo != null) {
                        List<Video> videoList = baseVideo.getResults();
                        Log.e("movie", new Gson().toJson(response.body().getResults()));
                        for (int i = 0; i < videoList.size(); i++) {
                            Video video = videoList.get(i);
                            videoAdapter.addItem(video);
                        }
                        videoAdapter.notifyDataSetChanged();
                    }
                    fetchReviews(movieId);
                } else {

                    int sc = response.code();
                    Toast.makeText(getApplicationContext(), "Error code: " + sc, Toast.LENGTH_LONG)
                            .show();

                }

                llMain.setVisibility(View.VISIBLE);
                llProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<BaseVideo> call, Throwable t) {
                llMain.setVisibility(View.VISIBLE);
                llProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }


        });

    }

    private void fetchReviews(final String movieId) {

        Call<BaseReview> listCall = mManager.getDataService().movieReviews(movieId, Constant.MOVIE_API_KEY);

        listCall.enqueue(new Callback<BaseReview>() {
            @Override
            public void onResponse(Call<BaseReview> call, Response<BaseReview> response) {

                if (response.isSuccessful()) {
                    BaseReview baseReview = response.body();
                    if (baseReview != null) {
                        List<Review> reviewList = baseReview.getResults();
                        Log.e("jum", new Gson().toJson(response.body().getResults()));
                        Log.e("jum", String.valueOf(reviewList.size()));
                        for (int i = 0; i < reviewList.size(); i++) {
                            Review review = reviewList.get(i);
                            reviewAdapter.addItem(review);
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }
                } else {

                    int sc = response.code();
                    Toast.makeText(getApplicationContext(), "Error code: " + sc, Toast.LENGTH_LONG)
                            .show();

                }


            }

            @Override
            public void onFailure(Call<BaseReview> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }


        });

    }

    private void setMovieDetail() {

        NumberFormat numberFormat = new DecimalFormat("#,###.0");

        posterPath = Constant.MOVIE_IMAGE_BASE_URL + selectedMovie.getPosterPath();
        Picasso.with(DetailActivity.this).load(posterPath).into(imgPoster);

        txtTitle.setText(selectedMovie.getTitle());

        String releaseText = "Release Date: " + selectedMovie.getReleaseDate();
        txtRelease.setText(releaseText);

        String rateText = numberFormat.format(selectedMovie.getVoteAverage()) + " / 10";
        txtRate.setText(rateText);

        txtOverview.setText(selectedMovie.getOverview());

    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    @Override
    public void onVideoClick(int position) {
        try {
            String videoUrl = "https://www.youtube.com/watch?v=" + videoAdapter.getSelectedItem(position).getKey();
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(myIntent);
        } catch (Exception e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
