package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UploadPhotoActivity extends AppCompatActivity {
ImageView mRghtImage,mLeftImage;
TextView mRightPrediction, mLeftPrediction,mRightSsim,mLeftSsim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mLeftImage = findViewById(R.id.left_image);
        mRghtImage = findViewById(R.id.left_image);
        mRightPrediction = findViewById(R.id.right_results);
        mLeftPrediction = findViewById(R.id.left_results);
        mRightSsim = findViewById(R.id.right_ssim);
        mLeftSsim = findViewById(R.id.left_ssim);

        String current_left = getIntent().getStringExtra("current_left");
        String current_right = getIntent().getStringExtra("current_right");
        String previous_left = getIntent().getStringExtra("previous_left");
        String previous_right = getIntent().getStringExtra("previous_right");
        String previous_date = getIntent().getStringExtra("previous_date");
        String prediction_right = getIntent().getStringExtra("prediction_right");
        String prediction_left = getIntent().getStringExtra("prediction_left");

        Glide.with(getApplicationContext()).load(current_left).into(mLeftImage);
        Glide.with(getApplicationContext()).load(current_right).into(mRghtImage);
        mRightPrediction.setText(prediction_right);
        mLeftPrediction.setText(prediction_left);

    }
}