package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class HistoryDetailsActivity extends AppCompatActivity {

    TextView mPrediction, mScanDate;
    ImageView mImageIv;
    ListView mPredictionLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        Objects.requireNonNull(getSupportActionBar()).hide();
        //initialize views
        mPrediction = findViewById(R.id.prediction);
        mScanDate = findViewById(R.id.date_of_scan);
        mImageIv = findViewById(R.id.imageView);
        mPredictionLists = findViewById(R.id.lv_probabilities);

        //get data from intent
        byte[] bytes = getIntent().getByteArrayExtra("image");
        String prediction = getIntent().getStringExtra("prediction");
        String scan_date = getIntent().getStringExtra("scan_date");
        //List<String> predicitonsList = getIntent().getStringExtra("predicitons_list");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //set data to views
        mPrediction.setText(prediction);
        mScanDate.setText(scan_date);
//        ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(
//                this, R.layout.support_simple_spinner_dropdown_item, predicitonsList);
//        mPredictionLists.setAdapter(predictionsAdapter);
        mImageIv.setImageBitmap(bmp);


    }
}