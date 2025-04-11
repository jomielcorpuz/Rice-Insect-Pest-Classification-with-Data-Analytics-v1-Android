package com.example.riceinsectpest;

import android.graphics.Bitmap;

public class SuperClass {

    private String ID;
    private String Image_Name;
    private Bitmap image;
    private String Image_Path;
    private String Date_Taken;

    public SuperClass(String ID, String image_Name, Bitmap image, String image_Path, String date_Taken) {
        this.ID = ID;
        Image_Name = image_Name;
        this.image = image;
        Image_Path = image_Path;
        Date_Taken = date_Taken;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImage_Name() {
        return Image_Name;
    }

    public void setImage_Name(String image_Name) {
        Image_Name = image_Name;
    }

    public String getImage_Path() {
        return Image_Path;
    }

    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }

    public String getDate_Taken() {
        return Date_Taken;
    }

    public void setDate_Taken(String date_Taken) {
        Date_Taken = date_Taken;
    }

    public SuperClass(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
