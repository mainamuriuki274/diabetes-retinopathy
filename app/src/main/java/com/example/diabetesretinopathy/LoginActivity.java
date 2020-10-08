package com.example.diabetesretinopathy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button mSigninBtn;
    TextView mSignUp, mForgotPassword;
    ImageView mLoading;
    EditText mTextEmail, mTextPassword;
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
        mTextEmail      = findViewById(R.id.email_signin);
        mTextPassword   = findViewById(R.id.password_signin);

        if(mAuth.getCurrentUser() != null){
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

                String email    = mTextEmail.getText().toString().trim();
                String password = mTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mSigninBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextEmail.setError(getText(R.string.email_error));
                }
                else if(TextUtils.isEmpty(password)){
                    mSigninBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextPassword.setError(getText(R.string.empty_password_error));
                }
                else{
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                    mTextEmail.setText("");
                                    mTextPassword.setText("");
                                    mSigninBtn.setVisibility(View.VISIBLE);
                                    mLoading.setVisibility(View.GONE);
                                } else {
                                    mSigninBtn.setVisibility(View.VISIBLE);
                                    mLoading.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();;
                                }
                            }
                        });
                    }
                    else{
                            mTextEmail.setError(getText(R.string.email_error));
                            mSigninBtn.setVisibility(View.VISIBLE);
                            mLoading.setVisibility(View.GONE);
                    }

                }
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