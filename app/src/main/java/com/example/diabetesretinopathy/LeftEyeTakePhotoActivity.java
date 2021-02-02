package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LeftEyeTakePhotoActivity extends AppCompatActivity {
    ImageView mImage,mLoading;
    Button mReUploadImage,mSubmit;
    TextView mNothing,mFundusInstructions;
    CardView mButtonsCard;
    LinearLayout mLinearLayout;
    private EyeTypeClassifier eyeTypeClassifier;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2000;
    private static final int CAMERA_REQUEST_CODE = 20001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_eye_take_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initializeUI();
        try {
            eyeTypeClassifier = new EyeTypeClassifier(this);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Image Classifier Error",Toast.LENGTH_LONG);
        }
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LeftEyeTakePhotoActivity.this);
                pd.setMessage("Please wait processing images...");
                pd.show();
                getImageBitmap();
                pd.dismiss();

            }
        });
        mReUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // if this is the result of our camera image request
        if (requestCode == CAMERA_REQUEST_CODE) {
            // getting bitmap of the image
            Bitmap photo = null;
            //photo = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            try {
                photo = (Bitmap) data.getExtras().get("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resultCode == RESULT_OK) {
                mNothing.setVisibility(View.GONE);
                mImage.setImageBitmap(photo);
                mSubmit.setEnabled(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * checks whether all the needed permissions have been granted or not
     *
     * @param grantResults the permission grant results
     * @return true if all the reqested permission has been granted,
     * otherwise returns false
     */
    private boolean hasAllPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    /**
     * Method requests for permission if the android version is marshmallow or above
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // whether permission can be requested or on not
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Intent i=new Intent(LeftEyeTakePhotoActivity.this,TurnOnCameraActivity.class);
                startActivity(i);
            }
            // request the camera permission permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * checks whether camera permission is available or not
     *
     * @return true if android version is less than marshmallow,
     * otherwise returns whether camera permission has been granted or not
     */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if this is the result of our camera permission request
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (hasAllPermissions(grantResults)) {
                openCamera();
            } else {
                requestPermission();
            }
        }
    }
    /**
     * creates and starts an intent to get a picture from camera
     */
    private void openCamera() {
        if (hasPermission()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermission();
        }
    }
    private void getImageBitmap(){
        if(mImage.getDrawable() != null) {
            ProgressDialog  dialog = new ProgressDialog(LeftEyeTakePhotoActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();
            Drawable mDrawable = mImage.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            String fileName = "current_left_image";//no .png or .jpg needed
            try {
                FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(bytes);
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
                fileName = null;
            }

            try {
                Bitmap left_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_left_image"));
                String eye = checkEye(left_bitmap);
                if(eye.equals("right")) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please Upload an Image of the Left Eye",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), AnalyseImagesActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "Whooops! an error occurred please try again",Toast.LENGTH_LONG).show();
        }
    }
    private String checkEye(Bitmap bitmap){
        List<EyeTypeClassifier.Recognition> prediction = eyeTypeClassifier.recognizeImage(bitmap, 0);
        String name = prediction.get(0).getName();
        return name;
    }
    private void initializeUI(){
        mImage   = findViewById(R.id.imageView);
        mFundusInstructions   = findViewById(R.id.fundus_information);
        mNothing   = findViewById(R.id.nothing);
        mLoading   = findViewById(R.id.searching);
        mSubmit = findViewById(R.id.submit_right);
        mButtonsCard   = findViewById(R.id.buttons_card);
        mLinearLayout   = findViewById(R.id.linear_layout);
        mReUploadImage = findViewById(R.id.reupload_image);
        mSubmit.setEnabled(false);
    }

    }