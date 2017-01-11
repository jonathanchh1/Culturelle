package com.example.jonat.capstonestage1.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private TextInputEditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ContentLoadingProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (TextInputEditText) findViewById(R.id.email);
        inputPassword = (TextInputEditText) findViewById(R.id.password);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.mProgressbar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);



        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                       }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), getString(R.string.prompt_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), getString(R.string.prompt_password), Toast.LENGTH_SHORT).show();;
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), getString(R.string.password_short), Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                if(!validateForm()){
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.createUserEmail) + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, getString(R.string.mAuth_failed) + task.getException(), Toast.LENGTH_SHORT).show();
                                    Log.d(LOG_TAG, getString(R.string.auth_failed) + task.getException());
                                }else {
                                    onAuthSuccess(task.getResult().getUser());
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    private void onAuthSuccess(FirebaseUser user){
        String username = usernameFromEmail(user.getEmail());
        //write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        //go to mainActivity
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();

    }


    private void writeNewUser(String userId, String name, String email){
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private String usernameFromEmail(String email) {
        if(email.contains("@")){
            return email.split("@")[0];
        }else{
            return email;
        }
    }

    private boolean validateForm(){
        boolean result = true;
        if(TextUtils.isEmpty(inputEmail.getText().toString())){
            inputEmail.setError("Required");
            result = false;
        }else{
            inputEmail.setError(null);
        }

        if(TextUtils.isEmpty(inputPassword.getText().toString())){
            inputPassword.setError("Required");
            result = false;
        }else{
            inputPassword.setError(null);
        }

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
