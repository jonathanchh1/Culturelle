package com.example.jonat.capstonestage1.services;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jonat on 1/9/2017.
 */

public class ApiClient {
        public static final String BASE_URL = "https://newsapi.org/v1/";
        private static Retrofit retrofit = null;


        public static Retrofit getClient() {
            if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }


