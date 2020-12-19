package com.example.diabetesretinopathy;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
    public void setDetails(Context ctx, String scan_date, String prediction, String scanned_image, List<String> predicitons_list,String pred_value){
        //Views
        TextView mScanDate = mView.findViewById(R.id.date_of_scan);
        TextView mPrediction = mView.findViewById(R.id.prediction);
        ImageView mScannedImage = mView.findViewById(R.id.scanned_image);
        TextView mPredictionValue = mView.findViewById(R.id.pred_value);
        ListView mPredList = mView.findViewById(R.id.predictions_list);
        //set data to views
        mScanDate.setText(scan_date);
        mPrediction.setText(prediction);

        // creating an array adapter to display the classification result in list view
        ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(
                ctx, R.layout.support_simple_spinner_dropdown_item, predicitons_list);
        mPredList.setAdapter(predictionsAdapter);
        mPredictionValue.setText(pred_value);
        Glide.with(ctx).load(scanned_image).into(mScannedImage);
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
}