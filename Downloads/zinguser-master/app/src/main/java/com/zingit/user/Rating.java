package com.zingit.user;

public class Rating {
    String orderID;
    int ratingTime;
    int ratingFood;
    int ratingApp;

    public Rating(String orderID, int ratingTime, int ratingFood, int ratingApp) {
        this.orderID = orderID;
        this.ratingTime = ratingTime;
        this.ratingFood = ratingFood;
        this.ratingApp = ratingApp;
    }

    public Rating() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getRatingTime() {
        return ratingTime;
    }

    public void setRatingTime(int ratingTime) {
        this.ratingTime = ratingTime;
    }

    public int getRatingFood() {
        return ratingFood;
    }

    public void setRatingFood(int ratingFood) {
        this.ratingFood = ratingFood;
    }

    public int getRatingApp() {
        return ratingApp;
    }

    public void setRatingApp(int ratingApp) {
        this.ratingApp = ratingApp;
    }
}
