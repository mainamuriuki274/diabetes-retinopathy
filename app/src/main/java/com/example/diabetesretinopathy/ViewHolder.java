package com.example.diabetesretinopathy;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewHolder  extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    //set details to recycler view row
    public void setDetails(Context ctx, String scan_date, String right_prediction, String left_prediction, String right_image, String left_image, String ssim_right,String ssim_left,String previous_date){
        //Views
        TextView mScanDate = mView.findViewById(R.id.date_of_scan);
        TextView mScanDate2 = mView.findViewById(R.id.date_of_scan2);
        TextView mPredictionRight = mView.findViewById(R.id.right_prediction);
        TextView mPredictionLeft = mView.findViewById(R.id.left_prediction);
        ImageView mImageRight = mView.findViewById(R.id.right_image);
        ImageView mImageLeft = mView.findViewById(R.id.left_image);
        TextView mSsimLeft = mView.findViewById(R.id.comparison_left);
        TextView mSsimRight = mView.findViewById(R.id.comparison_right);
        //set data to views
        String left_ssim = ssimReport(ssim_left,previous_date);
        String right_ssim = ssimReport(ssim_right,previous_date);
        mScanDate.setText(scan_date);
        mScanDate2.setText(scan_date);
        mSsimLeft.setText(left_ssim);
        mSsimRight.setText(right_ssim);
        mPredictionRight.setText(right_prediction);
        mPredictionLeft.setText(left_prediction);
        Glide.with(ctx).load(right_image).into(mImageRight);
        Glide.with(ctx).load(left_image).into(mImageLeft);
    }

    private ViewHolder.ClickListener mClickListener;

    //interface to send callbacks
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View  view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
    private String ssimReport(String ssim, String previous_date){
        float ssim_float = Float.parseFloat(ssim);
        String ssim_report;
        if(ssim_float >= 0.96){
            ssim_report = "Diabetes Retinopathy did not change and no new masses formed since the last scan taken on: " + previous_date;
        }
        else if(ssim_float >= 0.57 && ssim_float <=0.95){
            ssim_report = "Diabetes Retinopathy slightly regressed and a few new masses formed since the last scan taken on: " + previous_date;
        }
        else if(ssim_float > 0.00 && ssim_float < 0.57){
            ssim_report = "Diabetes Retinopathy got significantly worse and a number new masses formed since the last scan taken on: " + previous_date;
        }
        else{
            ssim_report = "There are no images to compare with. This is the first image";
        }
        return ssim_report;
    }
}