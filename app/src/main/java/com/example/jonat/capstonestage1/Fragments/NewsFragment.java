package com.example.jonat.capstonestage1.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Activities.GossipDetailActivity;
import com.example.jonat.capstonestage1.Adapters.WorldNewsAdapter;
import com.example.jonat.capstonestage1.BuildConfig;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.services.ArticleSyncService;
import com.example.jonat.capstonestage1.Model.NewsFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jonat on 12/9/2016.
 */

public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = NewsFragment.class.getSimpleName();
    private ArrayList<NewsFeed> feedList;
    private WorldNewsAdapter mAdapter;
    RecyclerView mRecyclerView;
    private static final String LATEST = "latest";
    private String sortOrder = LATEST;
    private CoordinatorLayout mcoordinatorlayout;
    private ProgressBar mProgressbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View rootView = inflater.inflate(
                R.layout.recyclerview, container, false);

        ArticleSyncService.initialize(getContext());
        getLoaderManager().initLoader(0, null, this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mrecyclerView);
        mcoordinatorlayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        mProgressbar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView.setHasFixedSize(true);
        //Set padding for News;
        int  padding  =  getResources().getDimensionPixelSize(R.dimen.tile_padding);
        mRecyclerView.setPadding(padding, padding, padding, padding);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.grid_column)));
        updateNews(sortOrder);

       return rootView;
    }
    private void updateNews(String choice) {
        if (isNetworkAvailable(getActivity())) {
            new DownloadTask().execute(choice);
        } else {
            Snackbar.make(mcoordinatorlayout, getString(R.string.noNetwork), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> implements WorldNewsAdapter.Callbacks {

        @Override
        protected void onPreExecute() {
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                String choice = params[0];
                URL url = new URL("https://newsapi.org/v1/articles?source=business-insider&sortBy" + choice + "&apiKey=" +
                        BuildConfig.NEWS_API);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            mProgressbar.setVisibility(View.GONE);
            if (result == 1) {
                mAdapter = new WorldNewsAdapter(getContext(), feedList, this);
                mRecyclerView.setAdapter(mAdapter);
                ;
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(NewsFeed items, int position) {
            Intent intent = new Intent(getActivity(), GossipDetailActivity.class);
            intent.putExtra(GossipDetailActivity.ARG_NEWS, items);
            startActivity(intent);
        }
    }
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("articles");
            feedList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                NewsFeed item = new NewsFeed();
                item.setTitle(post.optString("title"));
                item.setThumbnail(post.optString("urlToImage"));
                item.setmPublish(post.optString("publishedAt"));
                item.setUrl(post.optString("url"));
                item.setAuthor(post.optString("author"));
                item.setDescription(post.optString("description"));

                feedList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWorkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetWorkInfo == null) {
            Toast.makeText(getActivity(), "there no internet connection", Toast.LENGTH_SHORT).show();
        }
        return activeNetWorkInfo != null && activeNetWorkInfo.isConnected();
    }
}
