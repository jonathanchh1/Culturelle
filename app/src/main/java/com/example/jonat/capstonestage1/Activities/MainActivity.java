package com.example.jonat.capstonestage1.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jonat.capstonestage1.Adapters.GossipNewsAdapter;
import com.example.jonat.capstonestage1.Adapters.TabAdapter;
import com.example.jonat.capstonestage1.Fragments.GamesFragment;
import com.example.jonat.capstonestage1.Fragments.GossipNewsFragment;
import com.example.jonat.capstonestage1.Fragments.NewsFragment;
import com.example.jonat.capstonestage1.Fragments.TechFragment;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.model.GossipFeedItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.jonat.capstonestage1.R.id.action_share;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;

    TabAdapter mTabAdapter;
    GossipFeedItems items = new GossipFeedItems();
    DrawerLayout mDrawlayout;
    private List<GossipFeedItems> feedList;
    GossipNewsAdapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
   final int[] TabsIcon = new int[]{
            R.drawable.ic_library_books_black_24dp,
            R.drawable.ic_bubble_chart_black_24dp,
            R.drawable.ic_videogame_asset_black_24dp,
           R.drawable.ic_computer_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adding Toolbar to Main screen
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupWithViewPager(viewPager);

        //Set Tabs inside Toolbar
        TabLayout mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(viewPager);


        if(TabsIcon.length > 0) {
            mTabs.getTabAt(0).setIcon(TabsIcon[0]);
            mTabs.getTabAt(1).setIcon(TabsIcon[1]);
            mTabs.getTabAt(2).setIcon(TabsIcon[2]);
            mTabs.getTabAt(3).setIcon(TabsIcon[3]);
        }
        //Create navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawlayout = (DrawerLayout) findViewById(R.id.drawer);

        //Adding menu icon to Toolbar;
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null ){
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_black_24dp, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Set item in checked state
                int id  = item.getItemId();
                switch (id){
                    case R.id.favorites:
                        Log.d(LOG_TAG, "favorite");
                        GossipNewsFragment.FetchFav fetchFav =
                                new GossipNewsFragment.FetchFav(new GossipNewsFragment.Callbacks() {
                                    @Override
                                    public void onTaskCompleted(List<GossipFeedItems> items) {
                                        if (items != null) {
                                        if (mAdapter != null) {
                                            mAdapter.updateList(items);
                                        }
                                        feedList = new ArrayList<>();
                                        feedList.addAll(items);
                                    }
                                    }
                                });
                        fetchFav.execute();
                }
                item.setChecked(true);

                //Closing drawer on ItemClick
                mDrawlayout.closeDrawers();
                return true;
            }
        });


        //Adding Floating Action button to bottom right of main view;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add code for share here....
                updateShareActionProvider(items);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(LOG_TAG, "log in" + user.getUid());
                }else{
                    Log.d(LOG_TAG, "user logged out");
                }
            }
        };



    }

    private void setupWithViewPager(ViewPager viewPager) {
        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        mTabAdapter.addFragment(new GossipNewsFragment(), getString(R.string.Gossip));
        mTabAdapter.addFragment(new NewsFragment(), getString(R.string.news));
        mTabAdapter.addFragment(new GamesFragment(), getString(R.string.games));
        mTabAdapter.addFragment(new TechFragment(), getString(R.string.tech));
        viewPager.setAdapter(mTabAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem action_share = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

        return true;
    }

    private void updateShareActionProvider(GossipFeedItems items) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, items.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, items.getUrl());
        startActivity(Intent.createChooser(sharingIntent, "sharing Option"));
    }

            @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            return true;
        }else if(id == android.R.id.home){
            mDrawlayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
