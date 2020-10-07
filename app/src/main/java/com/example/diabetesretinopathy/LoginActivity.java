package com.example.diabetesretinopathy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button mSigninBtn;
    TextView mSignUp, mForgotPassword;
    ImageView mLoading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth           = FirebaseAuth.getInstance();
        mLoading        = findViewById(R.id.loading_bar);
        mSigninBtn      = findViewById(R.id.sign_in_btn);
        mSignUp         = findViewById(R.id.signup_link);
        mForgotPassword = findViewById(R.id.forgot_password_link);

        if(mAuth != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);

        mSigninBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mSigninBtn.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);

                mSigninBtn.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }
}