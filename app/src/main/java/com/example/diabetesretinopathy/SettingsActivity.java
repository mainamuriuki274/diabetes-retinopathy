package com.example.diabetesretinopathy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    Spinner mSpinner;
    CardView mNotification,mNotificationBtn,mHelpBtn,mHelp,mAbout,mAboutBtn,mAccount;
    ImageView mNotificationImage,mHelpImage,mAboutImage;
    Switch mNotificationSwitch;
    LinearLayout mLastReminder,mNextReminder;
    TextView mLast_Reminder,mNext_Reminder;
    TextView mTextNotification,mUserName,mUserEmail;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "user" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Notification = "notificationKey";
    public static final String Timeline = "timelineKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mSpinner            = findViewById(R.id.timeline);
        mAccount            = findViewById(R.id.account);
        mHelp               = findViewById(R.id.help_settings);
        mHelpBtn            = findViewById(R.id.help_card);
        mHelpImage          = findViewById(R.id.imageViewHelp);
        mAbout              = findViewById(R.id.about_settings);
        mAboutBtn           = findViewById(R.id.about_card);
        mAboutImage         = findViewById(R.id.imageViewAbout);
        mNotification       = findViewById(R.id.notification_settings);
        mNotificationBtn    = findViewById(R.id.notification_card);
        mNotificationImage  = findViewById(R.id.imageViewNotification);
        mNotificationSwitch = findViewById(R.id.notification_switch);
        mLast_Reminder      = findViewById(R.id.last_reminder);
        mNext_Reminder       = findViewById(R.id.next_reminder);
        mLastReminder       = findViewById(R.id.LastReminder);
        mNextReminder       = findViewById(R.id.NextReminder);
        mTextNotification   = findViewById(R.id.text_notification);
        mUserName           = findViewById(R.id.user_name);
        mUserEmail          = findViewById(R.id.user_email);

        mTextNotification.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
        mNextReminder.setVisibility(View.GONE);
        mLastReminder.setVisibility(View.GONE);

        sharedPreferences      = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String username        = sharedPreferences.getString(Name, "");
        String email           = sharedPreferences.getString(Email, "");
        String timeline        = sharedPreferences.getString(Timeline, "");
        String notification   = sharedPreferences.getString(Notification,"");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        if(username != null && email != null && timeline != null ){
            mUserName.setText(username);
            mUserEmail.setText(email);
            mSpinner.setSelection(adapter.getPosition(timeline));
            mNotificationSwitch.setChecked(Boolean.parseBoolean(notification));
        }

       if(mNotificationSwitch.isChecked()){
           mTextNotification.setVisibility(View.VISIBLE);
           mSpinner.setVisibility(View.VISIBLE);
           mNextReminder.setVisibility(View.VISIBLE);
           mLastReminder.setVisibility(View.VISIBLE);
       }

        mNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isChecked) {
                    editor.putString(Notification,"true");
                    if(editor.commit()) {
                        mTextNotification.setVisibility(View.VISIBLE);
                        mSpinner.setVisibility(View.VISIBLE);
                        mNextReminder.setVisibility(View.VISIBLE);
                        mLastReminder.setVisibility(View.VISIBLE);
                    }

                } else {
                    editor.putString(Notification,"false");
                    if(editor.commit()) {
                        mTextNotification.setVisibility(View.GONE);
                        mSpinner.setVisibility(View.GONE);
                        mNextReminder.setVisibility(View.GONE);
                        mLastReminder.setVisibility(View.GONE);
                    }
                }
            }
        });



        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String notification_timeline = mSpinner.getSelectedItem().toString().trim();
                editor.putString(Timeline,notification_timeline);
                if(editor.commit()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar c = Calendar.getInstance();
                    String date = mLast_Reminder.getText().toString();
                    Date date2= null;
                    try {
                        date2 = sdf.parse(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(notification_timeline.equals("1 month")) {
                        c.setTime(date2);
                        c.add(Calendar.DATE, 30);
                        String output = sdf.format(c.getTime());
                        mNext_Reminder.setText(output);
                    }
                    else if(notification_timeline.equals("2 months")) {
                        c.setTime(date2);
                        c.add(Calendar.DATE, 60);
                        String output = sdf.format(c.getTime());
                        mNext_Reminder.setText(output);
                    }
                    else if(notification_timeline.equals("3 months")) {
                        c.setTime(date2);
                        c.add(Calendar.DATE, 90);
                        String output = sdf.format(c.getTime());
                        mNext_Reminder.setText(output);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
    }
}
