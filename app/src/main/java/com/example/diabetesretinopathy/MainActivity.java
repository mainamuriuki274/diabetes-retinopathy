package com.example.diabetesretinopathy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {
    CardView mTakephoto,mUpload,mSettings;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    SharedPreferences sharedPreferences;
    TextView mName;
    String userId;
    public static final String MyPREFERENCES = "user" ;
    public static final String Name = "nameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth             = FirebaseAuth.getInstance();
        fstore            = FirebaseFirestore.getInstance();
        mTakephoto        = findViewById(R.id.take_photo);
        mUpload           = findViewById(R.id.upload_photo);
        mSettings         = findViewById(R.id.settings);
        mName             = findViewById(R.id.hello_there);

        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        userId              = mAuth.getCurrentUser().getUid();
        sharedPreferences   = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username     = sharedPreferences.getString(Name, "");
        if(username != null){
            mName.setText(username);
        }

        mTakephoto.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewPhotoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSettings.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
            }
        });

        mUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
    }

}