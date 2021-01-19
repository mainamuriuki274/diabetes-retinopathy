package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Objects;

public class NoPreviousActivity extends AppCompatActivity {
    ImageView mImageLeft,mImageRight;
    TextView mPredictionRight,mPredictionLeft;
    Button mOpthamologist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_previous);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mImageRight = findViewById(R.id.right_eye);
        mImageLeft = findViewById(R.id.left_eye);
        mPredictionRight = findViewById(R.id.right_scan_results);
        mPredictionLeft = findViewById(R.id.left_scan_results);
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
        try {
            Bitmap right_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_right_gaussian"));
            Bitmap left_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_left_gaussian"));
            mImageRight.setImageBitmap(right_bitmap);
            mImageLeft.setImageBitmap(left_bitmap);
            mPredictionLeft.setText(prediction_left);
            mPredictionRight.setText(prediction_right);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}