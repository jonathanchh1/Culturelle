package com.example.jonat.capstonestage1.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Activities.CommentActivity;
import com.example.jonat.capstonestage1.Activities.GossipDetailActivity;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Model.NewsFeed;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by jonat on 12/23/2016.
 */

public class GossipDetailFragment extends Fragment {

    public static final String LOG_TAG = GossipDetailFragment.class.getSimpleName();
    private NewsFeed items;
    View rootView;
    private MenuItem favorite;
    private LayoutInflater mLayoutInflater;
    ShareActionProvider mShareActionProvider;
    private DatabaseReference mDatabase;
    TextView mTitle;
    TextView mAuthor;
    ImageView thumbnail;
    TextView Description;
    TextView published;
    Button mbuttonList;


    public GossipDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof GossipDetailActivity) {
            appBarLayout.setTitle(items.getTitle());
        }

        ImageView backdrop = ((ImageView) activity.findViewById(R.id.detail_backdrop));
        if (backdrop != null) {
            String mbackdrop = items.getThumbnail();
            Picasso.with(getActivity()).load(mbackdrop).into(backdrop);
        }


    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mLayoutInflater = inflater;

        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();

        if (arguments != null || intent != null && intent.hasExtra(GossipDetailActivity.ARG_NEWS)) {

            rootView = mLayoutInflater.inflate(R.layout.detail_fragment, container, false);
            if (arguments != null) {
                items = getArguments().getParcelable(GossipDetailActivity.ARG_NEWS);
            } else {
                items = intent.getParcelableExtra(GossipDetailActivity.ARG_NEWS);
            }

            mTitle = (TextView) rootView.findViewById(R.id.title_detail);
            mAuthor = (TextView) rootView.findViewById(R.id.author_detail);
            Description = (TextView) rootView.findViewById(R.id.descriptionm);
            published = (TextView) rootView.findViewById(R.id.published);
            mbuttonList = (Button) rootView.findViewById(R.id.read_more_b);
            thumbnail = (ImageView) rootView.findViewById(R.id.detail_poster);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mTitle.setText(items.getTitle());
            mAuthor.setText(items.getAuthor());
            Description.setText(items.getDescription());
            published.setText(items.getmPublish());

            mbuttonList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(items.getUrl())));
                }
            });

            mbuttonList.setEnabled(!items.getUrl().isEmpty());

            DisplayInfo(rootView);
        }
        return rootView;
    }

    private void DisplayInfo(View v) {
        if (items != null) {
            String posterUrl = items.getThumbnail();
            Picasso.with(getActivity()).load(posterUrl).into(thumbnail);
        }

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
        Log.d(LOG_TAG, "detail Menu created");

        favorite = menu.findItem(R.id.comment);
        MenuItem action_share = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.comment:
                if(items != null) {
                    Intent intent = new Intent(getActivity(), CommentActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.action_share:
                //share NEWS
                updateShareActionProvider(items);


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void updateShareActionProvider(NewsFeed items) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, items.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, items.getUrl());
        startActivity(Intent.createChooser(sharingIntent, "sharing Option"));
    }

}

