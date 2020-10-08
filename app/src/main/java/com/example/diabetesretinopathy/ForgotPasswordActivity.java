package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView mSignUp;
    EditText mEmailForgot;
    Button mForgotBtn;
    ImageView mLoading;
    private FirebaseAuth mAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mSignUp      = findViewById(R.id.signup_link);
        mEmailForgot = findViewById(R.id.email_forgot_password);
        mForgotBtn   = findViewById(R.id.forgot_password_btn);
        mLoading     = findViewById(R.id.loading_progress);
        mAuth        = FirebaseAuth.getInstance();

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);
        mForgotBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mForgotBtn.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                String email = mEmailForgot.getText().toString().trim();
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mEmailForgot.setText("");
                            mLoading.setVisibility(View.GONE);
                            mForgotBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Please Check your email for the reset link",Toast.LENGTH_LONG).show();
                            Intent i=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mLoading.setVisibility(View.GONE);
                            mForgotBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Error occured reset link was not sent",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    mLoading.setVisibility(View.GONE);
                    mForgotBtn.setVisibility(View.VISIBLE);
                    mEmailForgot.setError(getText(R.string.email_error));
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(ForgotPasswordActivity.this,SignupActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}