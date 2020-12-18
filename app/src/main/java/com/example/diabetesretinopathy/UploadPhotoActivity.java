package com.example.diabetesretinopathy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadPhotoActivity extends AppCompatActivity {
    ImageView mImage,imageView;
    Button mReUploadImage;
    ListView listView;
    TextView mNothing;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2000;
    private static final int GALLERY_REQUEST_CODE = 20001;
    private ImageClassifier imageClassifier;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mImage   = findViewById(R.id.imageView);
        mNothing   = findViewById(R.id.nothing);
        listView = findViewById(R.id.lv_probabilities);
        mReUploadImage = findViewById(R.id.reupload_image);
        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Image Classifier Error",Toast.LENGTH_LONG);
        }
        openGallery();
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
            savetoCloud(photo);
            // pass this bitmap to classifier to make prediction
            List<ImageClassifier.Recognition> predicitons = imageClassifier.recognizeImage(
                    photo, 0);

            // creating a list of string to display in list view
            final List<String> predicitonsList = new ArrayList<>();
            for (ImageClassifier.Recognition recog : predicitons) {
                predicitonsList.add(recog.getName() + "  ::::::::::  " + recog.getConfidence()*100);
            }

            // creating an array adapter to display the classification result in list view
            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(
                    this, R.layout.support_simple_spinner_dropdown_item, predicitonsList);
            listView.setAdapter(predictionsAdapter);

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
    private void savetoCloud(Bitmap bitmap){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // Create a storage reference from our app


// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = mStorageRef.child("mountains.jpg");

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = mStorageRef.child("images/mountains.jpg");

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(),"Failed uploaded image",Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(getApplicationContext(),"Successfully uploaded image",Toast.LENGTH_LONG).show();
            }
        });
    }
}