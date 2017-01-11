package com.example.jonat.capstonestage1.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.jonat.capstonestage1.BuildConfig;
import com.example.jonat.capstonestage1.Data.ArticlesContract;
import com.example.jonat.capstonestage1.Model.NewsFeed;
import com.example.jonat.capstonestage1.Model.NewsReport;
import com.example.jonat.capstonestage1.widget.NewsArticlePreference;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.Observable;

/**
 * Created by jonat on 1/9/2017.
 */

public final class ArticleSyncService {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.example.jonat.capstonestage1.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;

    private static Subscription subscription;

    private ArticleSyncService() {
    }

    static void getQuotes(final Context context) {

        // detach an observer from its observable while the observable is still emitting data
        if (subscription != null) subscription.unsubscribe();
        // get data from api
        // update data inside content provider

        ApiClientInterface apiService = ApiClient.getClient().create(ApiClientInterface.class);
        Observable<NewsReport> mObservable =
                apiService.getArticles(new NewsArticlePreference(context).getSourceValue(),
                        BuildConfig.NEWS_API);

        subscription = mObservable
                .subscribeOn(Schedulers.newThread()) // Create a new Thread
                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread
                .subscribe(new Subscriber<NewsReport>() {
                    @Override
                    public void onCompleted() {
                        Log.d("task complete", "task complete successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("articles error", e.toString());
                    }

                    @Override
                    public void onNext(NewsReport response) {
                        Log.d("article result", response.getSource());
                        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();
                        ContentValues contentValues;

                        for (NewsFeed article: response.getArticles()) {
                            article.setSource(response.getSource());
                            contentValues = new ContentValues();
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_AUTHOR, article.getAuthor());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_DESCRIPTION, article.getDescription());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_PUBLISHEDAT, article.getmPublish());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_SOURCE, article.getSource());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_TITLE, article.getTitle());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_URL, article.getUrl());
                            contentValues.put(ArticlesContract.ArticleEntry.COLUMN_URLTOIMAGE, article.getThumbnail());
                            contentValuesArrayList.add(contentValues);
                        }

                        String selection = ArticlesContract.ArticleEntry.COLUMN_SOURCE + "=?";
                        String[] selectionArgs = {new NewsArticlePreference(context).getSourceValue()};

                        // delete old data
                        context.getContentResolver().delete(ArticlesContract.ArticleEntry.CONTENT_URI,
                                selection, selectionArgs);
                        // add new data
                        context.getContentResolver().bulkInsert(ArticlesContract.ArticleEntry.CONTENT_URI,
                                contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]));
                        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                        context.sendBroadcast(dataUpdatedIntent);

                    }
                });
    }

    private static void schedulePeriodic(Context context) {

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, NewsService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, NewsIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, NewsService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
