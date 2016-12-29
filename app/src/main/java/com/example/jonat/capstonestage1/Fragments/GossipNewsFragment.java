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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Activities.GossipDetailActivity;
import com.example.jonat.capstonestage1.Adapters.GossipNewsAdapter;
import com.example.jonat.capstonestage1.BuildConfig;
import com.example.jonat.capstonestage1.ContentProviders.NewsContract;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.model.GossipFeedItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jonat on 12/9/2016.
 */

public class GossipNewsFragment extends Fragment {

    public static final String LOG_TAG = GossipNewsFragment.class.getSimpleName();
    private static final String BOOKS_KEY = "books";
    private GossipNewsAdapter mAdapter;
    private ArrayList<GossipFeedItems> feedList;
    private static final String LATEST = "latest";
    private String sortOrder = LATEST;
    private RecyclerView recyclerView;
    private CoordinatorLayout mcoordinatorlayout;
    private ProgressBar mProgressbar;
    private static final String[] MOVIE_COLUMNS = {
            NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_NEWS_TITLE,
            NewsContract.NewsEntry.COLUMN_AUTHOR,
            NewsContract.NewsEntry.COLUMN_DESCRIPTION,
            NewsContract.NewsEntry.COLUMN_POSTER_PATH,
            NewsContract.NewsEntry.COLUMN_URL,
            NewsContract.NewsEntry.COLUMN_PUBLISHED,
    };

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.recyclerview, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.mrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.grid_column)));
        mProgressbar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> implements GossipNewsAdapter.Callbacks {


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
                URL url = new URL("https://newsapi.org/v1/articles?source=mtv-news&sortBy=" + choice + "&apiKey=" +
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
                mAdapter = new GossipNewsAdapter(getActivity(),
                        feedList, this);
                recyclerView.setAdapter(mAdapter);
                ;
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCompleted(GossipFeedItems items, int position) {
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
                GossipFeedItems item = new GossipFeedItems();
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
    public static class FetchFav extends AsyncTask<String, Void, List<GossipFeedItems>> {
        private Callbacks mCallbacks;

        private Context mContext;


        public FetchFav(Callbacks callbacks){
            this.mCallbacks = callbacks;
        }
        //constructor
        public FetchFav(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<GossipFeedItems> doInBackground(String... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    NewsContract.NewsEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<GossipFeedItems> items) {
            //we got Fav movies so let's show them
            if (items != null) {
                mCallbacks.onTaskCompleted(items);
            }
        }

        private List<GossipFeedItems> getFavMoviesFromCursor(Cursor cursor) {
            List<GossipFeedItems> results = new ArrayList<>();
            //if we have data in database for Fav. movies.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    GossipFeedItems items = new GossipFeedItems(cursor);
                    results.add(items);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);

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

        public interface Callbacks{
            void onTaskCompleted(List<GossipFeedItems> items);
        }
    }


