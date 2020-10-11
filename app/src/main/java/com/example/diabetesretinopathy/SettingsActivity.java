package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    Spinner mSpinner;
    CardView mNotification,mNotificationBtn,mHelpBtn,mHelp,mAbout,mAboutBtn;
    ImageView mNotificationImage,mHelpImage,mAboutImage;
    Switch mNotificationSwitch;
    LinearLayout mLastReminder,mNextReminder;
    TextView mTextNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mSpinner            = findViewById(R.id.timeline);
        mHelp               = findViewById(R.id.help_settings);
        mHelpBtn            = findViewById(R.id.help_card);
        mHelpImage          = findViewById(R.id.imageViewHelp);
        mAbout               = findViewById(R.id.about_settings);
        mAboutBtn            = findViewById(R.id.about_card);
        mAboutImage          = findViewById(R.id.imageViewAbout);
        mNotification       = findViewById(R.id.notification_settings);
        mNotificationBtn    = findViewById(R.id.notification_card);
        mNotificationImage  = findViewById(R.id.imageViewNotification);
        mNotificationSwitch = findViewById(R.id.notification_switch);
        mLastReminder       = findViewById(R.id.LastReminder);
        mNextReminder       = findViewById(R.id.NextReminder);
        mTextNotification   = findViewById(R.id.text_notification);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);


        mNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNotification.getVisibility() == View.VISIBLE){
                    mNotification.setVisibility(View.GONE);
                    mNotificationImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_arrow));
                }
                else{
                    mNotification.setVisibility(View.VISIBLE);
                    mNotificationImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow));
                }
            }
        });

        mHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHelp.getVisibility() == View.VISIBLE){
                    mHelp.setVisibility(View.GONE);
                    mHelpImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_arrow));
                }
                else{
                    mHelp.setVisibility(View.VISIBLE);
                    mHelpImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow));
                }
            }
        });

        mAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAbout.getVisibility() == View.VISIBLE){
                    mAbout.setVisibility(View.GONE);
                    mAboutImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_arrow));
                }
                else{
                    mAbout.setVisibility(View.VISIBLE);
                    mAboutImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow));
                }
            }
        });




        mNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mTextNotification.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.VISIBLE);
                    mNextReminder.setVisibility(View.VISIBLE);
                    mLastReminder.setVisibility(View.VISIBLE);

                } else {
                    mTextNotification.setVisibility(View.GONE);
                    mSpinner.setVisibility(View.GONE);
                    mNextReminder.setVisibility(View.GONE);
                    mLastReminder.setVisibility(View.GONE);
                }
            }
        });
    }
}
