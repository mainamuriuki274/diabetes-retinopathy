package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CompareImagesActivity extends AppCompatActivity {
    TextView mSsim,mSsimReport,mPreviousScan,mCurrentScan,mEye;
    ImageView mComparison,mLoading;
    CardView mImageCard;
    LinearLayout mInfoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_images);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mSsimReport = findViewById(R.id.ssim_report);
        mComparison = findViewById(R.id.comparison);
        mLoading = findViewById(R.id.loading_bar);
        mImageCard = findViewById(R.id.image_card);
        mInfoCard = findViewById(R.id.info_card);
        mSsim = findViewById(R.id.ssim);
        mPreviousScan = findViewById(R.id.previous_scan);
        mCurrentScan = findViewById(R.id.current_scan);
        mEye = findViewById(R.id.eye);


        String eye = getIntent().getStringExtra("eye");
        String image = getIntent().getStringExtra("image");
        String ssim = getIntent().getStringExtra("ssim");
        String previous_date = getIntent().getStringExtra("previous_date");
        String previous_scan = getIntent().getStringExtra("previous_scan");
        String prediction = getIntent().getStringExtra("prediction");
        String ssim_report = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput(image));
            mComparison.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        float ssim_int = Float.parseFloat(ssim);
        if(ssim_int >= 0.96){
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has not changed and no new masses have formed since the last scan taken on: " + previous_date + ". The image shows new masses(if any) areas in blue marks.";
        }
        else if(ssim_int >= 0.57 && ssim_int <=0.95){
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has slightly regressed and a few new masses have formed since the last scan taken on: " + previous_date + ". The image shows new masses areas in blue marks.";
        }
        else {
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has gotten significantly worse and a number new masses have formed since the last scan taken on: " + previous_date + ". Kindly make a point to see a doctor as soon as possible. The image shows new masses areas in blue marks.";

        }

        mSsim.setText(ssim);
        mSsimReport.setText(ssim_report);
        mCurrentScan.setText(prediction);
        mPreviousScan.setText(previous_scan);
        mEye.setText(eye);
        mImageCard.setVisibility(View.VISIBLE);
        mInfoCard.setVisibility(View.VISIBLE);


    }
}