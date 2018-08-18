package com.dentalcheck.themedapp;

import android.graphics.Bitmap;

public class getSetClass {
    String label;
    Bitmap image;
    String subtext;
    boolean status;
    String imageSrc;
    private String uid;
    int listItemPosition;
    boolean haveImage;

    public getSetClass() {

    }


    public void setUid(String uid) {
        this.uid = uid;
    }


    public int getListItemPosition() {
        return listItemPosition;
    }

    public void setListItemPosition(int listItemPosition) {
        this.listItemPosition = listItemPosition;
    }


    public boolean isHaveImage() {
        return haveImage;
    }

    public void setHaveImage(boolean haveImage) {
        this.haveImage = haveImage;
    }


    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}