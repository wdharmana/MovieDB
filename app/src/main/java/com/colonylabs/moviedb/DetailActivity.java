package com.colonylabs.moviedb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.colonylabs.moviedb.controller.RestManager;
import com.colonylabs.moviedb.models.Movie;
import com.colonylabs.moviedb.utils.Constant;
import com.colonylabs.moviedb.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    String posterPath;
    ImageView imgPoster;
    TextView txtTitle, txtRelease, txtOverview, txtRate;
    LinearLayout llMain, llProgressBar;
    private RestManager mManager;
    private ProgressDialog mDialog;
    private Movie selectedMovie;

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

        llMain.setVisibility(View.INVISIBLE);

        mManager = new RestManager();

        Intent curIntent = getIntent();

        if (curIntent.hasExtra(Intent.EXTRA_TEXT)) {
            fetchMovie(curIntent.getStringExtra(Intent.EXTRA_TEXT));
        }


    }

    private void fetchMovie(String movieId) {

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
}
