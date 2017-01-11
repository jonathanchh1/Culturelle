package com.example.jonat.capstonestage1.services;

import com.example.jonat.capstonestage1.Model.NewsReport;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jonat on 1/9/2017.
 */

public interface ApiClientInterface {
        @GET("articles")
        rx.Observable<NewsReport> getArticles(@Query("source") String source, @Query("apiKey") String apiKey);
    }

