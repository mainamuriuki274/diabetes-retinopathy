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
ImageView mRightImage,mLeftImage,mPreviousRight,mPreviousLeft,right,left;
TextView mRightPrediction, mLeftPrediction,mRightSsim,mLeftSsim;
CardView mLeft,mRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mLeftImage = findViewById(R.id.left_image);
        mRightImage = findViewById(R.id.right_image);
        mRight = findViewById(R.id.right);
        mLeft = findViewById(R.id.left);
        mPreviousRight = findViewById(R.id.previous_right);
        mPreviousLeft = findViewById(R.id.previous_left);
        mRightPrediction = findViewById(R.id.right_results);
        mLeftPrediction = findViewById(R.id.left_results);
        mRightSsim = findViewById(R.id.right_ssim);
        mLeftSsim = findViewById(R.id.left_ssim);
        final String[] ssim_image_left = new String[1];
        final String[] ssim_image_right = new String[1];
        final String[] ssim_left = new String[1];
        final String[] ssim_right = new String[1];

        if(!Python.isStarted())
        {
            Python.start(new AndroidPlatform(this));
        }
        final ProgressDialog pd = new ProgressDialog(UploadPhotoActivity.this);
        pd.setMessage("Please wait processing images...");
        pd.show();
        final String current_left = getIntent().getStringExtra("current_left");
        final String current_right = getIntent().getStringExtra("current_right");
        final String prediction_right = getIntent().getStringExtra("prediction_right");
        final String prediction_left = getIntent().getStringExtra("prediction_left");
        final String previous_date = getIntent().getStringExtra("previous_date");
        final String previous_left = getIntent().getStringExtra("previous_left");
        final String previous_right = getIntent().getStringExtra("previous_right");
        final String previous_scan_right = getIntent().getStringExtra("previous_scan_right");
        final String previous_scan_left = getIntent().getStringExtra("previous_scan_left");



        Toast.makeText(getApplicationContext(),previous_right,Toast.LENGTH_LONG).show();
        System.out.println(previous_right);

        Glide.with(getApplicationContext()).load(current_right).into(mRightImage);
        Glide.with(getApplicationContext()).load(previous_right).into(mPreviousRight);
        Glide.with(getApplicationContext()).load(previous_left).into(mPreviousLeft);
        Glide.with(getApplicationContext()).load(current_left).into(mLeftImage);

        final Bitmap RightbmpA =  getBitmapImage(current_right);
        final Bitmap RightbmpB =  getBitmapImage(previous_right);

        final Bitmap LeftbmpA =  getBitmapImage(current_left);
        final Bitmap LeftbmpB =  getBitmapImage(previous_left);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String RightimageA = getStringImage(RightbmpA);
                String RightimageB = getStringImage(RightbmpB);

                String LeftimageA = getStringImage(LeftbmpA);
                String LeftimageB = getStringImage(LeftbmpB);
                List<String> result_right = compareImage(RightimageA,RightimageB);
                ssim_image_right[0] = result_right.get(0);
                ssim_right[0] = result_right.get(1);
                ssim_right[0] = ssim_right[0].replace("'","");
                ssim_right[0] = ssim_right[0].replace("'","");

                List<String> result_left = compareImage(LeftimageA,LeftimageB);
                ssim_image_left[0] = result_left.get(0);
                ssim_left[0] = result_left.get(1);
                ssim_left[0] = ssim_left[0].replace("'","");
                ssim_left[0] = ssim_left[0].replace("'","");



                mLeftSsim.setText(ssim_left[0]);
                mRightSsim.setText(ssim_right[0]);
                mRightPrediction.setText(prediction_right);
                mLeftPrediction.setText(prediction_left);
                pd.dismiss();

            }
        },5000);
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = android.util.Base64.decode(ssim_image_right[0],Base64.DEFAULT);
                Intent intent = new Intent(getApplicationContext(), CompareImagesActivity.class);
                intent.putExtra("eye", "(Right Eye)"); //put bitmap image as array of bytes
                intent.putExtra("image",  ssim_image_right[0]); //put bitmap image as array of bytes
                intent.putExtra("prediction", prediction_right); //put bitmap image as array of bytes
                intent.putExtra("ssim", ssim_right[0]);
                intent.putExtra("previous_date", previous_date);
                intent.putExtra("previous_scan", previous_scan_right);
                startActivity(intent);
                finish();
            }
        });
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = android.util.Base64.decode(ssim_image_left[0],Base64.DEFAULT);
                Intent intent = new Intent(getApplicationContext(), CompareImagesActivity.class);
                intent.putExtra("eye", "(Left Eye)"); //put bitmap image as array of bytes
                intent.putExtra("image",  ssim_image_left[0]); //put bitmap image as array of bytes
                intent.putExtra("prediction", prediction_left); //put bitmap image as array of bytes
                intent.putExtra("ssim", ssim_left[0]);
                intent.putExtra("previous_date", previous_date);
                intent.putExtra("previous_scan", previous_scan_left);
                startActivity(intent);
                finish();
            }
        });

    }
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private Bitmap getBitmapImage(String image) {
        Bitmap bitmap =null;
        try {
            URL url = new URL(image);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch(IOException e) {
            System.out.println(e);
        }
        return bitmap;
    }
    private List<String> compareImage(String image1,String image2){
        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("script");
        PyObject obj = pyobj.callAttr("main",image1,image2);
        String result = obj.toString();
        result = result.replace("[","");
        result = result.replace("]","");
        List<String> array_result = new ArrayList<String>(Arrays.asList(result.split(",")));
        return array_result;
    }
}