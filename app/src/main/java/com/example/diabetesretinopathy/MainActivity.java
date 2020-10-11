package com.example.diabetesretinopathy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;

public class MainActivity extends Activity {
    CardView mTakephoto,mUpload,mSettings;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    TextView mName;
    String userId;
   // ImageView viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth       = FirebaseAuth.getInstance();
        fstore      = FirebaseFirestore.getInstance();
        mTakephoto  = findViewById(R.id.take_photo);
        mUpload     = findViewById(R.id.upload_photo);
        mSettings   = findViewById(R.id.settings);
        mName       = findViewById(R.id.hello_there);

        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        userId = mAuth.getCurrentUser().getUid();

//        final DocumentReference documentReference = fstore.collection("users").document(userId);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                mName.setText(value.getString("username"));
//            }
//        });


        mTakephoto.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivity(i);
            }
        });

        mSettings.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
            }
        });

        mUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
    }
    @SuppressLint("LongLogTag")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = null;
            if (selectedImage != null) {
                c = getContentResolver().query(selectedImage,filePath, null, null, null);
            }
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            Log.w("path of image from gallery", picturePath+"");
            //viewImage.setImageBitmap(thumbnail);
        }
    }
}