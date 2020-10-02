package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button sign_in;
    TextView sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
            sign_in = findViewById(R.id.sign_in_btn);
        sign_in.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,MainActivity.class);
               startActivity(i);
            }
        });
        sign_up = findViewById(R.id.signup_link);
        sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(i);
            }
        });
    }
}