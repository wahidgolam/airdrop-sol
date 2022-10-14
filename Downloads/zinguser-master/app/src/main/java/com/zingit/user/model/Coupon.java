package com.zingit.user.model;

import java.util.ArrayList;

public class Coupon {
    String id;
    String code;
    int applyOn;
    //0 -> user
    //1 -> items
    //2 -> outlets
    //3 -> campus
    ArrayList<String> applyID = new ArrayList<>();
    int discountType;
    //0 -> discount %
    //1 -> flat
    int discount;
    int preReqOrderValue;
    //-1 -> no such requirement
    int couponLimitPerUser;
    //
    int totalLimit;

    public int getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(int totalLimit) {
        this.totalLimit = totalLimit;
    }


    //no. of times a coupon can be used by each applyID

    public Coupon() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getApplyOn() {
        return applyOn;
    }

    public void setApplyOn(int applyOn) {
        this.applyOn = applyOn;
    }

    public ArrayList<String> getApplyID() {
        return applyID;
    }

    public void setApplyID(ArrayList<String> applyID) {
        this.applyID = applyID;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public int getPreReqOrderValue() {
        return preReqOrderValue;
    }

    public void setPreReqOrderValue(int preReqOrderValue) {
        this.preReqOrderValue = preReqOrderValue;
    }

    public int getCouponLimitPerUser() {
        return couponLimitPerUser;
    }

    public void setCouponLimitPerUser(int couponLimitPerUser) {
        this.couponLimitPerUser = couponLimitPerUser;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
