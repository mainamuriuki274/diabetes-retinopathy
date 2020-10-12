package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "user" ;
    public static final String email = "emailKey";
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    FirebaseUser user;
    String password,new_password,confirm_password;
    TextView mForgotPassword;
    ImageView mLoading;
    EditText mOldPassword,mNewPassword,mConfirmPassword;
    Button mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth                = FirebaseAuth.getInstance();
        user                 = mAuth.getCurrentUser();
        sharedPreferences    = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        mForgotPassword      = findViewById(R.id.forgot_password_link);
        mChangePassword      = findViewById(R.id.change_password_btn);
        mOldPassword         = findViewById(R.id.old_password);
        mNewPassword         = findViewById(R.id.new_password);
        mConfirmPassword     = findViewById(R.id.confirm_new_password);
        mLoading             = findViewById(R.id.loading_bar);
        password             = mOldPassword.getText().toString().trim();
        new_password         = mNewPassword.getText().toString().trim();
        confirm_password     = mConfirmPassword.getText().toString().trim();



        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.length() < 8){
                    mChangePassword.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mOldPassword.setError(getText(R.string.password_error));
                }
                else if(new_password.length() < 8){
                    mChangePassword.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mNewPassword.setError(getText(R.string.password_error));
                }
                else if(!confirm_password.equals(password)){
                    mChangePassword.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                    mConfirmPassword.setError(getText(R.string.confirm_password_error));
                }
                else{
                    String email_address = sharedPreferences.getString(email, "");
                    AuthCredential credential = EmailAuthProvider.getCredential(email_address, password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mChangePassword.setVisibility(View.VISIBLE);
                                                    mLoading.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(),"Password successfully updated",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(ChangePasswordActivity.this, AccountActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    mChangePassword.setVisibility(View.VISIBLE);
                                                    mLoading.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(),"Failed! password change failed",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        mChangePassword.setVisibility(View.VISIBLE);
                                        mLoading.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(),"Failed to authenticate user",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(ChangePasswordActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

    }
}