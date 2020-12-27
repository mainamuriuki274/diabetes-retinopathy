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
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LeftEyeTakePhotoActivity extends AppCompatActivity {
    ImageView mImage;
    Button mReUploadImage,mSubmit;
    TextView mNothing,mFundusInstructions,mPreviousLeft,mPreviousRight,mCurrentLeft,mCurrentRight,mPreviousDate;
    TextView mPredictionRight,mPredictionLeft,mPreviousScanRight,mPreviousScanLeft;
    ProgressDialog dialog;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2000;
    private static final int CAMERA_REQUEST_CODE = 20001;
    private ImageClassifier imageClassifier;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_eye_take_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        final byte[] right_bytes = getIntent().getByteArrayExtra("image");
        mImage   = findViewById(R.id.imageView);
        mFundusInstructions   = findViewById(R.id.fundus_information);
        mNothing   = findViewById(R.id.nothing);
        mSubmit = findViewById(R.id.submit_right);
        mReUploadImage = findViewById(R.id.reupload_image);
        mCurrentRight = findViewById(R.id.current_right);
        mPreviousDate = findViewById(R.id.previous_date);
        mCurrentLeft = findViewById(R.id.current_left);
        mPreviousLeft = findViewById(R.id.previous_left);
        mPreviousRight = findViewById(R.id.previous_right);
        mPreviousScanRight = findViewById(R.id.previous_scan_right);
        mPreviousScanLeft = findViewById(R.id.previous_scan_left);
        mPredictionRight = findViewById(R.id.right_prediction);
        mPredictionLeft = findViewById(R.id.left_prediction);
        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Image Classifier Error",Toast.LENGTH_LONG);
        }

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LeftEyeTakePhotoActivity.this);
                pd.setMessage("Please wait saving images...");
                pd.show();
                Drawable mDrawable = mImage.getDrawable();
                final Bitmap left_bmp = ((BitmapDrawable) mDrawable).getBitmap();

                byte[] bytes = getIntent().getByteArrayExtra("right_image");
                final Bitmap right_bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                List<ImageClassifier.Recognition> right_prediciton = imageClassifier.recognizeImage(right_bmp, 0);
                List<ImageClassifier.Recognition> left_prediciton = imageClassifier.recognizeImage(left_bmp, 0);
                String right_prediction_value = right_prediciton.get(0).toString();
                String left_prediction_value = left_prediciton.get(0).toString();



                savetoCloud(right_bmp, left_bmp, right_prediction_value,left_prediction_value);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), UploadPhotoActivity.class);
                        intent.putExtra("current_left", mCurrentLeft.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("current_right", mCurrentRight.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("prediction_right", mPredictionRight.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("prediction_left", mPredictionLeft.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("previous_date", mPreviousDate.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("previous_left", mPreviousLeft.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("previous_right", mPreviousRight.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("previous_scan_left", mPreviousScanLeft.getText().toString()); //put bitmap image as array of bytes
                        intent.putExtra("previous_scan_right", mPreviousScanRight.getText().toString()); //put bitmap image as array of bytes
                        pd.dismiss();
                        startActivity(intent);
                        finish();
                    }
                },7000);




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
        Bitmap photo = null;
        photo = (Bitmap) data.getExtras().get("data");
        if (resultCode == RESULT_OK && data.getData()!=null) {
            mNothing.setVisibility(View.GONE);
            mImage.setImageBitmap(photo);

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
        private void getPreviousImage(){
            String userId = mAuth.getCurrentUser().getUid();
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/images/"+userId);
            myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data : snapshot.getChildren()) {
                        String last_date_scan = data.child("date_of_scan").getValue().toString();
                        String previous_image_left = data.child("image_left").getValue().toString();
                        String previous_image_right = data.child("image_right").getValue().toString();
                        String previous_scan_right = data.child("right_prediction").getValue().toString();
                        String previous_scan_left = data.child("left_prediction").getValue().toString();
                        mPreviousLeft.setText(previous_image_left);
                        mPreviousRight.setText(previous_image_right);
                        mPreviousScanLeft.setText(previous_scan_left);
                        mPreviousScanRight.setText(previous_scan_right);
                        mPreviousDate.setText(last_date_scan);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),"An error occured",Toast.LENGTH_LONG).show();
                }
            });

        }
        private void savetoCloud(final Bitmap right_bitmap, final Bitmap left_bitmap,final String right_prediction, final String left_prediction){
            mPredictionRight.setText(right_prediction);
            mPredictionLeft.setText(left_prediction);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            Date currentTime = Calendar.getInstance().getTime();
            final String userId = mAuth.getCurrentUser().getUid();
            final String model_id = "1";
            final String date_of_scan = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            final String right_image = "right-" + userId +"-"+ currentTime;
            final String left_image = "left-" + userId +"-"+ currentTime;
            final String image =  userId + currentTime;
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/images/"+userId);
            final StorageReference storageRefRight = mStorageRef.child("images/"+right_image+".jpg");
            final StorageReference storageRefLeft = mStorageRef.child("images/"+left_image+".jpg");


            getPreviousImage();

            ByteArrayOutputStream right_baos = new ByteArrayOutputStream();
            right_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, right_baos);
            byte[] right_data = right_baos.toByteArray();

            ByteArrayOutputStream left_baos = new ByteArrayOutputStream();
            left_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, left_baos);
            final byte[] left_data = left_baos.toByteArray();

            UploadTask uploadTaskRight = storageRefRight.putBytes(right_data);
            uploadTaskRight.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(),"Failed uploaded image",Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    //Toast.makeText(getApplicationContext(),"Successfully uploaded image",Toast.LENGTH_LONG).show();
                    UploadTask uploadTaskLeft= storageRefLeft.putBytes(left_data);
                    uploadTaskLeft.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(),"Failed uploaded image",Toast.LENGTH_LONG).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            myRef.child("/" + image).setValue(image);
                            myRef.child("/" + image+ "/date_of_scan").setValue(date_of_scan);

                            storageRefRight.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    String right_url = downloadUrl.toString();
                                    myRef.child("/" + image+ "/image_right").setValue(right_url);
                                    mCurrentRight.setText(right_url);
                                    //Toast.makeText(getApplicationContext(),imageRef.toString(),Toast.LENGTH_LONG).show();
                                    storageRefLeft.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri downloadUrl)
                                        {
                                            String left_url = downloadUrl.toString();
                                            myRef.child("/" + image+ "/image_left").setValue(left_url);
                                            mCurrentLeft.setText(left_url);


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"An error occured! " + e.toString(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    myRef.child("/" + image+ "/image_id").setValue(image);
                                    myRef.child("/" + image+ "/model_id").setValue(model_id);
                                    myRef.child("/" + image+ "/right_prediction").setValue(right_prediction);
                                    myRef.child("/" + image+ "/left_prediction").setValue(left_prediction);
                                    myRef.child("/" + image+ "/user_id").setValue(userId);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"An error occured! " + e.toString(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });
                }
            });
        }
    }