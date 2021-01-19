package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RightEyeUploadActivity extends AppCompatActivity {
    ImageView mImage;
    Button mReUploadImage,mSubmit;
    TextView mNothing,mFundusInstructions;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2000;
    private static final int GALLERY_REQUEST_CODE = 20001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_eye_upload);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initializeUI();
        mSubmit.setEnabled(false);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(RightEyeUploadActivity.this);
                pd.setMessage("Please wait processing images...");
                pd.show();
               getImageBitmap();
                pd.dismiss();
            }
        });
        mReUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // if this is the result of our camera image request
        Bitmap photo = null;
        try {
            photo = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (resultCode == RESULT_OK && data.getData()!=null) {
            // getting bitmap of the image
            // displaying this bitmap in imageview
            mNothing.setVisibility(View.GONE);
            mImage.setImageBitmap(photo);
            mSubmit.setEnabled(true);

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
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(),"Please grant permission",Toast.LENGTH_LONG).show();
            }
            // request the camera permission permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
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
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if this is the result of our camera permission request
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (hasAllPermissions(grantResults)) {
                openGallery();
            } else {
                requestPermission();
            }
        }
    }
    /**
     * creates and starts an intent to get a picture from camera
     */
    private void openGallery() {
        if (hasPermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            try {
                startActivityForResult(Intent.createChooser(intent, "Select File"),GALLERY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
                Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermission();
        }
    }
    private void getImageBitmap(){
        if(mImage.getDrawable() != null) {

            Drawable mDrawable = mImage.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
            //pass this data to new activity
            Intent intent = new Intent(getApplicationContext(), LeftEyeUploadActivity.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            //intent.putExtra("right_image", bytes); //put bitmap image as array of bytes
            //intent.putExtra("Image", mBitmap);
            String fileName = "current_right_image";//no .png or .jpg needed
            try {
                FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(bytes);
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
                fileName = null;
            }
            startActivity(intent); //start activity
        }
        else{
            Toast.makeText(getApplicationContext(), "Whooops! an error occurred please try again",Toast.LENGTH_LONG).show();
        }
    }
    private void initializeUI(){
        mImage   = findViewById(R.id.imageView);
        mFundusInstructions   = findViewById(R.id.fundus_information);
        mNothing   = findViewById(R.id.nothing);
        mSubmit = findViewById(R.id.submit_right);
        mReUploadImage = findViewById(R.id.reupload_image);
    }
    private Bitmap gaussianBlur(String image){
        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("gaussian_blur");
        PyObject obj = pyobj.callAttr("gaussian_blur",image);
        String result = obj.toString();
        byte[] bytes = android.util.Base64.decode(result, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
}