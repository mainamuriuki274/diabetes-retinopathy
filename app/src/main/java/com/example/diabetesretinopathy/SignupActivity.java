package com.example.diabetesretinopathy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class SignupActivity extends AppCompatActivity{
    TextView mTerms,mSignin;
    Button mSignupBtn;
    EditText mTextUsername,mTextPassword,mTextConfirmPassword,mTextEmail;
    CheckBox mCheckBoxAgree;
    ImageView mLoading;
    private FirebaseAuth mAuth;
    private static final String mUsername = "username";
    private static final String mEmail = "email";
    private static final String mPassword = "password";


    @SuppressLint({"WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth                  = FirebaseAuth.getInstance();
        mLoading               = findViewById(R.id.loading_bar);
        mSignupBtn             = findViewById(R.id.sign_up_btn);
        mSignin                = findViewById(R.id.signin_link);
        mTerms                 = findViewById(R.id.terms);
        mTextUsername          = findViewById(R.id.username_signup);
        mTextEmail             = findViewById(R.id.email_signup);
        mTextPassword          = findViewById(R.id.password_signup);
        mTextConfirmPassword   = findViewById(R.id.confirm_password_signup);
        mCheckBoxAgree         = findViewById(R.id.checkBoxAgree);


        if(mAuth != null){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);


        mCheckBoxAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    mSignupBtn.setEnabled(true);
                } else {
                    mSignupBtn.setEnabled(false);
                }
            }
        });

        mTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms();
            }
        });

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignupBtn.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                String email = mTextEmail.getText().toString().trim();
                String username = mTextUsername.getText().toString().trim();
                String password = mTextPassword.getText().toString().trim();
                String confirm_password = mTextConfirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    mTextUsername.setError(getText(R.string.username_error));
                    return;
                }
                else if(TextUtils.isEmpty(email)){
                    mTextUsername.setError(getText(R.string.email_error));
                    return;
                }
                else if(password.length() < 8){
                    mTextUsername.setError(getText(R.string.password_error));
                    return;
                }
                else if(TextUtils.isEmpty(confirm_password)){
                    mTextUsername.setError(getText(R.string.confirm_password_error));
                    return;
                }
                else{
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Error: " + task.getException().getMessage(), Toast.LENGTH_LONG);
                                }
                            }
                        });
                    }
                  else{
                        mTextUsername.setError(getText(R.string.email_error));
                        return;
                    }

                }
            }
        });
    }
    private void terms(){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
    }
}