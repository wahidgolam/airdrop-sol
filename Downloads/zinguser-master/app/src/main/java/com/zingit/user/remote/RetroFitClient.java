package com.zingit.user.remote;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient {

    public static Retrofit instance;

    public static Retrofit getInstance(){
        return instance == null ? new Retrofit.Builder()
                .baseUrl("https://us-central1-zing-user.cloudfunctions.net/widgets/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build(): instance;
    }
}
