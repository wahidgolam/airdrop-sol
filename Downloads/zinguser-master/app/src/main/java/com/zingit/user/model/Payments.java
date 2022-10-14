package com.zingit.user.model;

import com.google.firebase.Timestamp;

public class Payments {

    String id;
    String couponID;
    String paymentOrderID;
    String userID;
    double  totalAmountPaid;
    double taxesAndCharges;
    double couponDiscount;
    double basePrice;
    Timestamp timestamp;

    public Payments(String id, String couponID, String paymentOrderID, String userID, double totalAmountPaid, double taxesAndCharges, double couponDiscount, double basePrice, Timestamp timestamp) {
        this.id = id;
        this.couponID = couponID;
        this.paymentOrderID = paymentOrderID;
        this.userID = userID;
        this.totalAmountPaid = totalAmountPaid;
        this.taxesAndCharges = taxesAndCharges;
        this.couponDiscount = couponDiscount;
        this.basePrice = basePrice;
        this.timestamp = timestamp;
    }

    public Payments() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponID() {
        return couponID;
    }

    public void setCouponID(String couponID) {
        this.couponID = couponID;
    }

    public String getPaymentOrderID() {
        return paymentOrderID;
    }

    public void setPaymentOrderID(String paymentOrderID) {
        this.paymentOrderID = paymentOrderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(int totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public double getTaxesAndCharges() {
        return taxesAndCharges;
    }

    public void setTaxesAndCharges(int taxesAndCharges) {
        this.taxesAndCharges = taxesAndCharges;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(int couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }




}
