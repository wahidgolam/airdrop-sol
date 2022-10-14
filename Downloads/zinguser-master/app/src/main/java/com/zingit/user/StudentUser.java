package com.zingit.user;

import java.util.ArrayList;

public class StudentUser {
    String campusID;
    String email;
    String firstName;
    String fullName;
    String userID;
    String userImage;
    String phoneNumber;
    String FCMToken;

    public StudentUser(String campusID, String email, String firstName, String fullName, String userID, String userImage) {
        this.campusID = campusID;
        this.email = email;
        this.firstName = firstName;
        this.fullName = fullName;
        this.userID = userID;
        this.userImage = userImage;
    }

    public StudentUser() {
    }

    public String getCampusID() {
        return campusID;
    }

    public void setCampusID(String campusID) {
        this.campusID = campusID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }
}