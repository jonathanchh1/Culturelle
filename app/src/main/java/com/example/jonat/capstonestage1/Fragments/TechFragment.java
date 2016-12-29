package com.example.jonat.capstonestage1.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Adapters.TechAdapter;
import com.example.jonat.capstonestage1.BuildConfig;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.model.GamesItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jonat on 12/23/2016.
 */

public class TechFragment extends Fragment {
    public static final String LOG_TAG = GamesFragment.class.getSimpleName();
    private static final String BOOKS_KEY = "books";
    private TechAdapter mAdapter;
    private ArrayList<GamesItems> feedList;
    private static final String LATEST = "latest";
    private String sortOrder = LATEST;
    private RecyclerView recyclerView;
    private CoordinatorLayout mcoordinatorlayout;
    private ProgressBar mProgressbar;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(
                R.layout.recyclerview, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.mrecyclerView);
        recyclerView.setHasFixedSize(true);
        mProgressbar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.grid_column)));
        mcoordinatorlayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        updateNews(sortOrder);
        return  rootView;

    }

    private void updateNews(String choice) {
        if (isNetworkAvailable(getActivity())) {
            new DownloadTask().execute(choice);
        } else {
            Snackbar.make(mcoordinatorlayout, getString(R.string.noNetwork), Snackbar.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> {


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
                String Api = "&apiKey=";
                URL url = new URL("https://newsapi.org/v1/articles?source=engadget&sortBy=" + choice + Api +
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
                mAdapter = new TechAdapter(getContext(), feedList);
                recyclerView.setAdapter(mAdapter);
                ;
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("articles");
            feedList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                GamesItems item = new GamesItems();
                item.setTitle(post.optString("title"));
                item.setmPublish("publishedAt");
                item.setUrl("url");
                item.setThumbnail(post.optString("urlToImage"));
                item.setAuthor("author");
                item.setDescription("description");

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
            Toast.makeText(getActivity(), getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
        }
        return activeNetWorkInfo != null && activeNetWorkInfo.isConnected();
    }
}
