package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

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
    String password;
    String new_password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth                = FirebaseAuth.getInstance();
        user                 = mAuth.getCurrentUser();
        sharedPreferences    = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
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
                                        Toast.makeText(getApplicationContext(),"Password successfully updated",Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Failed! password change failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(),"Failed! password change failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}