package com.example.jonat.capstonestage1.Activities;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jonat.capstonestage1.Fragments.GossipDetailFragment;
import com.example.jonat.capstonestage1.R;

public class GossipDetailActivity extends AppCompatActivity {
    public static final String ARG_NEWS = "arguments";
    private FragmentManager fragmentManager = getFragmentManager();
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mToolbar= (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .add(R.id.gossip_detail_container, new GossipDetailFragment())
                    .commit();
        }

    }

}
