package com.dentalcheck.themedapp;

public class user {
    private  String username, email, gender, DOB, image,speciality;
    private String DeviceToken;

    public user() {
    }

    public user(String username, String email, String gender, String DOB, String image, String speciality,String DeviceToken) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.DOB = DOB;
        this.image = image;
        this.speciality = speciality;
        this.DeviceToken = DeviceToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

}
