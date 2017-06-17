package com.colonylabs.moviedb.controller;

import com.colonylabs.moviedb.utils.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dharmana on 6/17/17.
 */

public class RestManager {
    private DataService mDataService;

    public DataService getDataService() {
        if (mDataService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.MOVIE_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mDataService = retrofit.create(DataService.class);
        }
        return mDataService;
    }

}
