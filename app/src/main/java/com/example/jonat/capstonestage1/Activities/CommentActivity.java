package com.example.jonat.capstonestage1.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Adapters.CommentAdapter;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Model.Comment;
import com.example.jonat.capstonestage1.Model.NewsFeed;
import com.example.jonat.capstonestage1.Model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.jonat.capstonestage1.Fragments.GossipNewsFragment.LOG_TAG;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mCommentsReference;
    private FirebaseDatabase mFirebaseInstance;
    private CommentAdapter mAdapter;
    private ImageView userprofile;
    private TextView mAuthorView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private GoogleSignInResult mGoogleSignResult;
    private ValueEventListener mPostListener;
    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_comment);




        // Initialize Database
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mCommentsReference = mFirebaseInstance.getReference("user_favorites");
        // store app title to 'app_title' node
        mFirebaseInstance.getReference("user_favorites").setValue("post_comments");

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("user_favorites").child("post_comments");
        // Initialize Views
        userprofile = (ImageView) findViewById(R.id.post_author_photo);
        mAuthorView = (TextView) findViewById(R.id.post_author);
        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);


        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        if(mGoogleSignResult != null){
            userInfo(getIntent());
        }

    }

    private void userInfo(Intent data){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        GoogleSignInAccount acct = result.getSignInAccount();
        Comment comment = new Comment();
        String userid = acct.getId();
        String author = acct.getGivenName();
        comment.setAuthor(author);
        comment.setUid(userid);

        String username = acct.getDisplayName();
        String email = acct.getEmail();
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        Uri userPhoto = acct.getPhotoUrl();

        Picasso.with(getApplicationContext())
                .load(userPhoto)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(userprofile);

        mAuthorView.setText(username);

    }


    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the comment
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Comment items = dataSnapshot.getValue(Comment.class);
                // [START_EXCLUDE]
            //    mAuthorView.setText(items.getAuthor());
                // [END_EXCLUDE]

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getBaseContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        // [END post_value_event_listener];/
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;
        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
    super.onStop();

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    private String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.getUsername();
                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

