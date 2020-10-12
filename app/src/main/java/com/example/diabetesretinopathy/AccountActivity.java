package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {
CardView mChangePassword,mUserCard,mUserIcon;
ImageView mLoading;
EditText mUserName,mUserEmail;
private FirebaseAuth mAuth;
FirebaseFirestore fstore;
String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mChangePassword      = findViewById(R.id.change_password_card);
        mLoading             = findViewById(R.id.loading_bar);
        mUserCard            = findViewById(R.id.user_card);
        mUserIcon            = findViewById(R.id.user_icon);
        mUserName            = findViewById(R.id.user_name);
        mUserEmail           = findViewById(R.id.user_email);
        mAuth                = FirebaseAuth.getInstance();
        fstore               = FirebaseFirestore.getInstance();


        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);

        userId = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = fstore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   if (task.isSuccessful()) {
                                                       DocumentSnapshot document = task.getResult();
                                                       if (document.exists()) {
                                                           mUserName.setText(document.getString("username"));
                                                           mUserEmail.setText(document.getString("email"));
                                                           mLoading.setVisibility(View.GONE);
                                                           mUserIcon.setVisibility(View.VISIBLE);
                                                           mUserCard.setVisibility(View.VISIBLE);
                                                       }
                                                   }
                                               }
                                           });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}