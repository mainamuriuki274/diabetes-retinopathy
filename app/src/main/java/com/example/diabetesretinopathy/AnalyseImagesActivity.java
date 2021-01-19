package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AnalyseImagesActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    ImageView mLoading;
    TextView mCurrentProcess;
    private String last_date_scan,previous_scan_right,previous_scan_left;
    private String right_prediction_value,left_prediction_value;
    private byte[] previous_right_bytes =null;
    private byte[] previous_left_bytes =null;
    private byte[] current_right_bytes =null;
    private byte[] current_left_bytes =null;
    private ImageClassifier imageClassifier;
    final String[] ssim_image_left = new String[1];
    final String[] ssim_image_right = new String[1];
    final String[] ssim_left = new String[1];
    final String[] ssim_right = new String[1];
    private Bitmap right_bitmap = null;
    private Bitmap left_bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_images);
        Objects.requireNonNull(getSupportActionBar()).hide();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initializeUI();
        if(!Python.isStarted())
        {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        try {
            right_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_right_image"));
            left_bitmap = BitmapFactory.decodeStream(getApplicationContext().openFileInput("current_left_image"));
            current_right_bytes = BitmaptoByte(right_bitmap);
            current_left_bytes = BitmaptoByte(left_bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Image Classifier Error",Toast.LENGTH_LONG);
        }
        if(isNetworkConnected()){
            if(internetIsConnected()){
                getPreviousImages();
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                if(previous_left_bytes==null){
                    last_date_scan = "none";
                    ssim_left[0] = "0.00";
                    ssim_right[0] = "0.00";
                    right_prediction_value = predictImage(current_right_bytes,"right");
                    left_prediction_value = predictImage(current_left_bytes,"left");
                    savetoCloud();
                    mCurrentProcess.setText("Successfully completed analysing images...");
                    Intent intent = new Intent(getApplicationContext(), NoPreviousActivity.class);
                    intent.putExtra("prediction_right", right_prediction_value);
                    intent.putExtra("prediction_left", left_prediction_value);
                    startActivity(intent);
                    finish();
                        }
                else {
                    right_prediction_value = predictImage(current_right_bytes,"right");
                    left_prediction_value = predictImage(current_left_bytes,"left");
                    compareImages();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            savetoCloud();
                        }
                    }, 5000);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentProcess.setText("Successfully completed analysing images...");
                            Intent intent = new Intent(getApplicationContext(), UploadPhotoActivity.class);
                            intent.putExtra("prediction_right", right_prediction_value);
                            intent.putExtra("prediction_left", left_prediction_value);
                            intent.putExtra("previous_date", last_date_scan);
                            intent.putExtra("previous_scan_left", previous_scan_left);
                            intent.putExtra("previous_scan_right", previous_scan_right);
                            intent.putExtra("ssim_left", ssim_left[0]);
                            intent.putExtra("ssim_right", ssim_right[0]);
                            startActivity(intent);
                            finish();
                        }
                    }, 5000);
                }
            }
                }, 5000);
            }

            else{
                right_prediction_value = predictImage(current_right_bytes,"right");
                left_prediction_value = predictImage(current_left_bytes,"left");
                mCurrentProcess.setText("Successfully completed analysing images...");
                Intent intent = new Intent(getApplicationContext(), NoPreviousActivity.class);
                intent.putExtra("prediction_right", right_prediction_value);
                intent.putExtra("prediction_left", left_prediction_value);
                startActivity(intent);
                finish();
            }
        }
        else{
            right_prediction_value = predictImage(current_right_bytes,"right");
            left_prediction_value = predictImage(current_left_bytes,"left");
            mCurrentProcess.setText("Successfully completed analysing images...");
            Intent intent = new Intent(getApplicationContext(), NoPreviousActivity.class);
            intent.putExtra("prediction_right", right_prediction_value);
            intent.putExtra("prediction_left", left_prediction_value);
            startActivity(intent);
            finish();
        }


    }
    private void getPreviousImages(){
        mCurrentProcess.setText("Retrieving last scanned images...");
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/images/"+userId);
        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    last_date_scan = data.child("date_of_scan").getValue().toString();
                    String previous_image_left = data.child("image_left").getValue().toString();
                    String previous_image_right = data.child("image_right").getValue().toString();
                    previous_scan_right = data.child("right_prediction").getValue().toString();
                    previous_scan_left = data.child("left_prediction").getValue().toString();
                    previous_right_bytes = getImageBytes(previous_image_right);
                    previous_left_bytes = getImageBytes(previous_image_left);
                    String fileName1 = "previous_right_image";//no .png or .jpg needed
                    try {
                        FileOutputStream fo = openFileOutput(fileName1, Context.MODE_PRIVATE);
                        fo.write(previous_right_bytes);
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fileName1 = null;
                    }
                    String fileName2 = "previous_left_image";//no .png or .jpg needed
                    try {
                        FileOutputStream fo = openFileOutput(fileName2, Context.MODE_PRIVATE);
                        fo.write(previous_left_bytes);
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fileName2 = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"An error occured",Toast.LENGTH_LONG).show();
            }
        });

    }
    private byte[] getImageBytes(String image) {
        Bitmap bitmap =null;
        byte[] bytes = null;
        try {
            URL url = new URL(image);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bytes = BitmaptoByte(bitmap);
            return bytes;
        } catch(IOException e) {
            System.out.println(e);
        }
        return bytes;
    }
    private byte[] BitmaptoByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }
    private void savetoCloud(){
        mCurrentProcess.setText("Scanning submitted images...");
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


        mCurrentProcess.setText("Saving submitted images...");
        UploadTask uploadTaskRight = storageRefRight.putBytes(current_right_bytes);
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
                UploadTask uploadTaskLeft= storageRefLeft.putBytes(current_left_bytes);
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
                                //Toast.makeText(getApplicationContext(),imageRef.toString(),Toast.LENGTH_LONG).show();
                                storageRefLeft.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri downloadUrl)
                                    {
                                        String left_url = downloadUrl.toString();
                                        myRef.child("/" + image+ "/image_left").setValue(left_url);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"An error occured! " + e.toString(),Toast.LENGTH_LONG).show();
                                    }
                                });
                                myRef.child("/" + image+ "/image_id").setValue(image);
                                myRef.child("/" + image+ "/model_id").setValue(model_id);
                                myRef.child("/" + image+ "/right_prediction").setValue(right_prediction_value);
                                myRef.child("/" + image+ "/left_prediction").setValue(left_prediction_value);
                                myRef.child("/" + image+ "/right_ssim").setValue(ssim_right[0]);
                                myRef.child("/" + image+ "/left_ssim").setValue(ssim_left[0]);
                                myRef.child("/" + image+ "/last_scan_date").setValue(last_date_scan);
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
    private String getStringImage(byte[] imageBytes) {
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    private Bitmap gaussianBlur(String image){
        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("gaussian_blur");
        PyObject obj = pyobj.callAttr("gaussian_blur",image);
        String result = obj.toString();
        byte[] bytes = android.util.Base64.decode(result,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
    private void initializeUI() {
        mLoading = findViewById(R.id.searching);
        mCurrentProcess = findViewById(R.id.current_process);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.searching)
                .into(mLoading);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
    private void storeImageLocally(Bitmap bitmap,String eye){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String fileName = null;
        if(eye=="right"){
            fileName = "current_right_gaussian";
        }
        else if(eye=="left"){
            fileName = "current_left_gaussian";
        }
        //no .png or .jpg needed
        try {
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes);
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
    }
    private String predictImage(byte[] bytes,String eye){
        mCurrentProcess.setText("Predicting submitted images...");
        String image_string = getStringImage(current_right_bytes);

        Bitmap gaussian_image = gaussianBlur(image_string);
        storeImageLocally(gaussian_image,eye);


        List<ImageClassifier.Recognition> prediction = imageClassifier.recognizeImage(gaussian_image, 0);
       //String prediction_value = prediction.get(0).toString();
        float confidence = prediction.get(0).getConfidence();
        String name = prediction.get(0).getName();
        confidence = confidence*100;
        String formattedconfidence = String.format("%.02f", confidence);
        String prediction_value = name + " confidence: " + formattedconfidence + "%";
        return  prediction_value;
    }
    private void compareImages(){
        mCurrentProcess.setText("Comparing previous and current images...");
        String RightimageA = getStringImage(current_right_bytes);
        String RightimageB = getStringImage(previous_right_bytes);

        String LeftimageA = getStringImage(current_left_bytes);
        String LeftimageB = getStringImage(previous_left_bytes);

        List<String> result_right = compareImage(RightimageA, RightimageB);
        ssim_image_right[0] = result_right.get(0);
        ssim_right[0] = result_right.get(1);
        ssim_right[0] = ssim_right[0].replace("'", "");
        ssim_right[0] = ssim_right[0].replace("'", "");
        byte[] right_bytes = android.util.Base64.decode(ssim_image_right[0], Base64.DEFAULT);
        String fileName = "ssim_image_right";//no .png or .jpg needed
        try {
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(right_bytes);
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }

        List<String> result_left = compareImage(LeftimageA, LeftimageB);
        ssim_image_left[0] = result_left.get(0);
        ssim_left[0] = result_left.get(1);
        ssim_left[0] = ssim_left[0].replace("'", "");
        ssim_left[0] = ssim_left[0].replace("'", "");
        byte[] left_bytes = android.util.Base64.decode(ssim_image_left[0], Base64.DEFAULT);
        fileName = "ssim_image_left";//no .png or .jpg needed
        try {
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(left_bytes);
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
    }
}

