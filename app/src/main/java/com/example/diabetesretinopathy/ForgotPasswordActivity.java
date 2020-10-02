package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sign_up = findViewById(R.id.signup_link);
        sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(ForgotPasswordActivity.this,SignupActivity.class);
                startActivity(i);
            }
        });
    }
}