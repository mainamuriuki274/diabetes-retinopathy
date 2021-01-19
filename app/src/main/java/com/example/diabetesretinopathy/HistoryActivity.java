package com.example.diabetesretinopathy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    ImageView mLoading;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mLoading  = findViewById(R.id.loading_bar);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .into(mLoading);
        mAuth = FirebaseAuth.getInstance();
        final String userId = mAuth.getCurrentUser().getUid();
        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default

        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("/images/"+userId);
    }
    //load data into recycler view onStart
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                        mLoading.setVisibility(View.VISIBLE);
                        viewHolder.setDetails(getApplicationContext(), model.getDate_of_scan(), model.getRight_prediction(), model.getLeft_prediction(),model.getImage_right(),model.getImage_left(),model.getRight_ssim(),model.getLeft_ssim(),model.getLast_scan_date());
                        mLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
//                                //Views
//                                TextView mScanDate = view.findViewById(R.id.date_of_scan);
//                                TextView mScanDate2 = view.findViewById(R.id.date_of_scan2);
//                                TextView mPredictionRight = view.findViewById(R.id.right_prediction);
//                                TextView mPredictionLeft = view.findViewById(R.id.left_prediction);
//                                ImageView mImageRight = view.findViewById(R.id.right_image);
//                                ImageView mImageLeft = view.findViewById(R.id.left_image);
//                                TextView mPredictionRightAll = view.findViewById(R.id.right_prediction_all);
//                                TextView mPredictionLeftAll = view.findViewById(R.id.left_prediction_all);
//                                //get data from views
//                                String mScan_Date = mScanDate.getText().toString();
//                                String mScan_Date2 = mScanDate2.getText().toString();
//                                String mRight_Prediction = mPredictionRight.getText().toString();
//                                String mLeft_Prediction = mPredictionLeft.getText().toString();
//                                Drawable mDrawableRight = mImageRight.getDrawable();
//                                byte[] right_bytes = drawabletoBytes(mDrawableRight);
//                                Drawable mDrawableLeft = mImageLeft.getDrawable();
//                                byte[] left_bytes = drawabletoBytes(mDrawableLeft);
//
//                                //pass this data to new activity
//                                Intent intent = new Intent(view.getContext(), HistoryDetailsActivity.class);
//                                    intent.putExtra("right_image", right_bytes); //put bitmap image as array of bytes
//                                    intent.putExtra("left_image", left_bytes); //put bitmap image as array of bytes
//                                    intent.putExtra("right_prediction", mRight_Prediction); // put title
//                                    intent.putExtra("left_prediction", mLeft_Prediction); // put title
//                                    intent.putExtra("scan_date", mScan_Date);
//                                    intent.putExtra("scan_date2", mScan_Date2);//put description//put description
//                                    startActivity(intent); //start activity


                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do your own implementaion on long item click
                            }
                        });

                        return viewHolder;
                    }

                };

        //set adapter to recyclerview
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
private byte[] drawabletoBytes(Drawable drawable){
    Bitmap mBitmap = ((BitmapDrawable) drawable).getBitmap();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();
    return  bytes;
}

}