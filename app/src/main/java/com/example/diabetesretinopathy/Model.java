package com.example.diabetesretinopathy;

import java.util.List;

public class Model {
    String date_of_scan;
    String image;
    String prediction;
    String prediction_value;


    //constructor
    public Model(){}

    //getter and setters press Alt+Insert


    public String getDate_of_scan() {
        return date_of_scan;
    }

    public void setDate_of_scan(String date_of_scan) {
        this.date_of_scan = date_of_scan;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getPrediction_value() {
        return prediction_value;
    }

    public void setPrediction_value(String prediction_value) {
        this.prediction_value = prediction_value;
    }






}