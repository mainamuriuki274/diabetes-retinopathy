package com.example.diabetesretinopathy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Activity {
    CardView mTakephoto,mUpload,mSettings,mHistory;
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
        mHistory          = findViewById(R.id.history);
        mSettings         = findViewById(R.id.settings);
        mName             = findViewById(R.id.hello_there);

        userId              = mAuth.getCurrentUser().getUid();
        sharedPreferences   = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username     = sharedPreferences.getString(Name, "");

        if(mAuth.getCurrentUser() == null && username == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if(username != null){
            mName.setText(username);
        }
        else{
            Toast.makeText(getApplicationContext(),username,Toast.LENGTH_LONG).show();
        }


        mTakephoto.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RightEyeTakePhotoActivity.class);
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
                Intent intent = new Intent(MainActivity.this, RightEyeUploadActivity.class);
                startActivity(intent);
            }
        });
        mHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

    }

}