package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UploadPhotoActivity extends AppCompatActivity {
ImageView mRightImage,mLeftImage;
TextView mRightPrediction, mLeftPrediction,mRightSsim,mLeftSsim;
CardView mLeft,mRight;
Button mOpthamologist;
    private String[] ssim_left = new String[1];
    private String[] ssim_right = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mLeftImage = findViewById(R.id.left_image);
        mRightImage = findViewById(R.id.right_image);
        mRight = findViewById(R.id.right);
        mLeft = findViewById(R.id.left);
        mRightPrediction = findViewById(R.id.right_results);
        mLeftPrediction = findViewById(R.id.left_results);
        mRightSsim = findViewById(R.id.right_ssim);
        mLeftSsim = findViewById(R.id.left_ssim);
        mOpthamologist =  findViewById(R.id.ophthalmologist_near_me_btn);

        mOpthamologist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.google.com/search?q=ophthalmologist+near+me&oq=opthamologist+nea&aqs=chrome.1.69i57j0i10i19l3j0i19j0i10i19l2.7423j0j1&sourceid=chrome&ie=UTF-8";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        final String prediction_right = getIntent().getStringExtra("prediction_right");
        final String prediction_left = getIntent().getStringExtra("prediction_left");
        final String previous_date = getIntent().getStringExtra("previous_date");;
        final String previous_scan_right = getIntent().getStringExtra("previous_scan_right");
        final String previous_scan_left = getIntent().getStringExtra("previous_scan_left");
        ssim_left[0] = getIntent().getStringExtra("ssim_left");
        ssim_right[0] = getIntent().getStringExtra("ssim_right");
        mRightSsim.setText(ssim_right[0]);
        mLeftSsim.setText(ssim_left[0]);



        try {
            Bitmap right_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_right_gaussian"));
            Bitmap left_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_left_gaussian"));
            mRightImage.setImageBitmap(right_bitmap);
            mLeftImage.setImageBitmap(left_bitmap);
            mRightPrediction.setText(prediction_right);
            mLeftPrediction.setText(prediction_left);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompareImagesActivity.class);
                intent.putExtra("eye", "(Right Eye)"); //put bitmap image as array of bytes
                intent.putExtra("image", "ssim_image_right"); //put bitmap image as array of bytes
                intent.putExtra("prediction", prediction_right); //put bitmap image as array of bytes
                intent.putExtra("ssim", ssim_right[0]);
                intent.putExtra("previous_date", previous_date);
                intent.putExtra("previous_scan", previous_scan_right);
                startActivity(intent);
            }
        });
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompareImagesActivity.class);
                intent.putExtra("eye", "(Left Eye)"); //put bitmap image as array of bytes
                intent.putExtra("image", "ssim_image_left"); //put bitmap image as array of bytes
                intent.putExtra("prediction", prediction_left); //put bitmap image as array of bytes
                intent.putExtra("ssim", ssim_left[0]);
                intent.putExtra("previous_date", previous_date);
                intent.putExtra("previous_scan", previous_scan_left);
                startActivity(intent);
            }
        });

    }
}