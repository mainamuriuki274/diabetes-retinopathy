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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CompareImagesActivity extends AppCompatActivity {
    TextView mSsim,mSsimReport;
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
        if(!Python.isStarted())
        {
            Python.start(new AndroidPlatform(this));
        }
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);
        byte[] bytesA = getIntent().getByteArrayExtra("imageA");
        byte[] bytesB = getIntent().getByteArrayExtra("imageB");
        String last_scan_date = getIntent().getStringExtra("last_scan_date");
        Bitmap bmpA = BitmapFactory.decodeByteArray(bytesA, 0, bytesA.length);
        Bitmap bmpB = BitmapFactory.decodeByteArray(bytesB, 0, bytesB.length);

        String image1 = getStringImage(bmpA);
        String image2 = getStringImage(bmpB);

        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("script");
        PyObject obj = pyobj.callAttr("main",image1,image2);
        String rslt = obj.toString();
        String replace = rslt.replace("[","");
        String replace1 = replace.replace("]","");
        List<String> result = new ArrayList<String>(Arrays.asList(replace1.split(",")));

        String str = result.get(0);
        String ssim = result.get(1);
        String ssim_report = null;
        byte[] data = android.util.Base64.decode(str,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);

        int ssim_int = Integer.parseInt(ssim);
        if(ssim_int >= 0.96){
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has not changed and no new masses have formed since the last scan taken on: " + last_scan_date;
        }
        else if(ssim_int >= 0.57 && ssim_int <=0.95){
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has slightly regressed and a few new masses have formed since the last scan taken on: " + last_scan_date;
        }
        else {
            ssim_report = "The Image analysis indicates that the Diabetes Retinopathy has gotten significantly worse and a number new masses have formed since the last scan taken on: " + last_scan_date;

        }
        mSsim.setText(ssim);
        mComparison.setImageBitmap(bmp);
        mSsimReport.setText(ssim_report);
        mLoading.setVisibility(View.GONE);
        mImageCard.setVisibility(View.VISIBLE);
        mInfoCard.setVisibility(View.VISIBLE);


    }
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}