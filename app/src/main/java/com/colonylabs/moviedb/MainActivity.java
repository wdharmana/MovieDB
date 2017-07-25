package com.colonylabs.moviedb;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.colonylabs.moviedb.adapter.MovieAdapter;
import com.colonylabs.moviedb.controller.RestManager;
import com.colonylabs.moviedb.database.FavoriteContract;
import com.colonylabs.moviedb.models.Base;
import com.colonylabs.moviedb.models.Result;
import com.colonylabs.moviedb.utils.Constant;
import com.colonylabs.moviedb.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_LAYOUT_MANAGER = "EXTRA_LAYOUT_MANAGER";

    private RecyclerView rv;
    private String sortBy = "popular";
    private RestManager mManager;
    private MovieAdapter mAdapter;
    private LinearLayout llProgressBar;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<Result> movies = new ArrayList<>();
    private Parcelable layoutManagerSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.rv);
        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);

        //rv.setVisibility(View.INVISIBLE);
        llProgressBar.setVisibility(View.INVISIBLE);
        mManager = new RestManager();

        mAdapter = new MovieAdapter(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        rv.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_DATA)) {
                List<Result> movieList = savedInstanceState.getParcelableArrayList(EXTRA_DATA);
                mAdapter.setData(movieList);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            fetchMovie(sortBy);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_SORT_BY, sortBy);
        outState.putParcelableArrayList(EXTRA_DATA, movies);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        movies = savedInstanceState.getParcelableArrayList(EXTRA_DATA);
        sortBy = savedInstanceState.getString(EXTRA_SORT_BY);
        mAdapter.clearItem();
        mAdapter.setData(movies);
        mAdapter.notifyDataSetChanged();
        setLayoutManager();
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(mAdapter);

    }

    public void setLayoutManager() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4);
        }
    }

    private void fetchMovie(String sortBy) {
        if (sortBy.equals("favorite")) {
            loadFavorite();
            rv.setLayoutManager(gridLayoutManager);
        } else {
            loadMovie(sortBy);
            rv.setLayoutManager(gridLayoutManager);
        }

    }

    private void loadMovie(String sortBy) {

        if (getNetworkAvailability()) {

            rv.setVisibility(View.INVISIBLE);
            llProgressBar.setVisibility(View.VISIBLE);

            Call<Base> listCall = null;

            if (sortBy.equals("popular")) {
                listCall = mManager.getDataService().moviePopular(Constant.MOVIE_API_KEY);
            } else {
                listCall = mManager.getDataService().movieTopRated(Constant.MOVIE_API_KEY);
            }


            listCall.enqueue(new Callback<Base>() {
                @Override
                public void onResponse(Call<Base> call, Response<Base> response) {

                    if (response.isSuccessful()) {
                        Base resp = response.body();
                        List<Result> results = resp.getResults();
                        mAdapter.clearItem();
                        for (int i = 0; i < results.size(); i++) {
                            mAdapter.addItem(results.get(i));
                            movies.add(results.get(i));
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {

                        int sc = response.code();
                        Toast.makeText(getApplicationContext(), "Error code: " + sc, Toast.LENGTH_LONG)
                                .show();

                    }


                    rv.setVisibility(View.VISIBLE);
                    llProgressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onFailure(Call<Base> call, Throwable t) {

                    rv.setVisibility(View.VISIBLE);
                    llProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

        } else {

            rv.setVisibility(View.VISIBLE);
            llProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

    }


    private Cursor getFavorite() {
        Cursor favoriteCursor = getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        return favoriteCursor;
    }

    private void loadFavorite() {

        rv.setVisibility(View.INVISIBLE);
        llProgressBar.setVisibility(View.VISIBLE);

        mAdapter.clearItem();
        movies.clear();
        Cursor favData = getFavorite();

        while (favData.moveToNext()) {
            Integer movieId = favData.getInt(favData.getColumnIndex(FavoriteContract.FavoriteEntry._ID));
            String posterPath = favData.getString(favData.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER));

            Result movie = new Result();
            movie.setId(movieId);
            movie.setPosterPath(posterPath);

            mAdapter.addItem(movie);
            movies.add(movie);
        }

        mAdapter.notifyDataSetChanged();

        rv.setVisibility(View.VISIBLE);
        llProgressBar.setVisibility(View.GONE);

    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    @Override
    public void onClick(int position) {
        Result result = mAdapter.getSelectedItem(position);
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(result.getId()));
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            sortBy = "popular";
            fetchMovie(sortBy);
        } else if (id == R.id.action_rate) {
            sortBy = "rate";
            fetchMovie(sortBy);
        } else if (id == R.id.action_favorite) {
            sortBy = "favorite";
            fetchMovie(sortBy);
        }
        return super.onOptionsItemSelected(item);
    }


}
