package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button sign_in;
    TextView sign_up,forgot_password;
    Handler handler;
    ImageView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
            loading = findViewById(R.id.loading_bar);
            sign_in = findViewById(R.id.sign_in_btn);
            Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(loading);
        sign_in.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sign_in.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        sign_in.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        finish();

                    }
                },3000);
            }
        });
        sign_up = findViewById(R.id.signup_link);
        sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
            }
        });
        forgot_password = findViewById(R.id.forgot_password_link);
        forgot_password.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }
}