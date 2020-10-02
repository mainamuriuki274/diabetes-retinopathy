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
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

public class MainActivity extends Activity {
    CardView takephoto,upload;
    ImageView viewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takephoto = findViewById(R.id.take_photo);
        takephoto.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivity(i);
            }
        });

        upload = findViewById(R.id.upload_photo);
        upload.setOnClickListener(new OnClickListener() {
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