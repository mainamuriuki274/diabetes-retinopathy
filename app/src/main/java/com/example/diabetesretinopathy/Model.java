package com.example.diabetesretinopathy;

import java.util.List;

public class Model {
    String date_of_scan;
    String image_right;
    String image_left;
    String right_prediction;
    String left_prediction;
    String last_scan_date;
    //constructor
    public Model(){}
    public String getLast_scan_date() {
        return last_scan_date;
    }

    public void setLast_scan_date(String last_scan_date) {
        this.last_scan_date = last_scan_date;
    }

    public String getRight_ssim() {
        return right_ssim;
    }

    public void setRight_ssim(String right_ssim) {
        this.right_ssim = right_ssim;
    }

    public String getLeft_ssim() {
        return left_ssim;
    }

    public void setLeft_ssim(String left_ssim) {
        this.left_ssim = left_ssim;
    }

    String right_ssim;
    String left_ssim;

    public String getDate_of_scan() {
        return date_of_scan;
    }

    public void setDate_of_scan(String date_of_scan) {
        this.date_of_scan = date_of_scan;
    }

    public String getImage_right() {
        return image_right;
    }

    public void setImage_right(String image_right) {
        this.image_right = image_right;
    }

    public String getImage_left() {
        return image_left;
    }

    public void setImage_left(String image_left) {
        this.image_left = image_left;
    }

    public String getRight_prediction() {
        return right_prediction;
    }

    public void setRight_prediction(String right_prediction) {
        this.right_prediction = right_prediction;
    }

    public String getLeft_prediction() {
        return left_prediction;
    }

    public void setLeft_prediction(String left_prediction) {
        this.left_prediction = left_prediction;
    }


}