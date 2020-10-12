package com.example.diabetesretinopathy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
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
    private static final String mUserId = "userid";
    private String TAG;


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
        mLoading               = findViewById(R.id.loading_bar);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        if(mAuth.getCurrentUser() != null){
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
                final String email = mTextEmail.getText().toString().trim();
                final String username = mTextUsername.getText().toString().trim();
                String password = mTextPassword.getText().toString().trim();
                String confirm_password = mTextConfirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    mSignupBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextUsername.setError(getText(R.string.username_error));
                }
                else if(TextUtils.isEmpty(email)){
                    mSignupBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextEmail.setError(getText(R.string.email_error));
                }
                else if(password.length() < 8){
                    mSignupBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextPassword.setError(getText(R.string.password_error));
                }
                else if(!confirm_password.equals(password)){
                    mSignupBtn.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mTextConfirmPassword.setError(getText(R.string.confirm_password_error));
                }
                else{
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            String userid = mAuth.getCurrentUser().getUid();
                                            Map<String, Object> user = new HashMap<>();
                                            user.put(mUserId, userid);
                                            user.put(mUsername,username);
                                            user.put(mEmail, email);

                                            db.collection("users").document(userid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                            mTextUsername.setText("");
                                                            mTextEmail.setText("");
                                                            mTextPassword.setText("");
                                                            mTextConfirmPassword.setText("");
                                                            mCheckBoxAgree.setChecked(false);
                                                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                            mSignupBtn.setVisibility(View.VISIBLE);
                                                            mLoading.setVisibility(View.GONE);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding document", e);
                                                            mSignupBtn.setVisibility(View.VISIBLE);
                                                            mLoading.setVisibility(View.GONE);
                                                            Toast.makeText(getApplicationContext(),"Error creating user" , Toast.LENGTH_LONG).show();;
                                                        }
                                                    });
                                        }
                                    });

                                }
                                else{
                                    mSignupBtn.setVisibility(View.VISIBLE);
                                    mLoading.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();;
                                }
                            }
                        });
                    }
                  else{
                        mSignupBtn.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.GONE);
                        mTextUsername.setError(getText(R.string.email_error));
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