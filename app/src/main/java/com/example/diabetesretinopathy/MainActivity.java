package com.example.diabetesretinopathy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Activity {
    CardView mTakephoto,mUpload,mSettings;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    SharedPreferences sharedPreferences;
    TextView mName;
    String userId;
    public static final String MyPREFERENCES = "user" ;
    public static final String Name = "nameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth             = FirebaseAuth.getInstance();
        fstore            = FirebaseFirestore.getInstance();
        mTakephoto        = findViewById(R.id.take_photo);
        mUpload           = findViewById(R.id.upload_photo);
        mSettings         = findViewById(R.id.settings);
        mName             = findViewById(R.id.hello_there);

        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        userId              = mAuth.getCurrentUser().getUid();
        sharedPreferences   = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username     = sharedPreferences.getString(Name, "");
        if(username != null){
            mName.setText(username);
        }


        mTakephoto.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, UploadPhotoActivity.class);
                startActivity(intent);
            }
        });
    }

}