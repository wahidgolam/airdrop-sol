package com.zingit.user.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CashFreeToken {

    @SerializedName("cf_order_id")
    @Expose
    public String cf_order_id;
    @SerializedName("order_id")
    @Expose
    public String order_id;
    @SerializedName("order_status")
    @Expose
    public String order_status;
    @SerializedName("order_token")
    @Expose
    public String order_token;

    public CashFreeToken() {
    }

    public CashFreeToken(String cf_order_id, String order_id, String order_status, String order_token) {
        this.cf_order_id = cf_order_id;
        this.order_id = order_id;
        this.order_status = order_status;
        this.order_token = order_token;
    }

    public String getCf_order_id() {
        return cf_order_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_token() {
        return order_token;
    }
}
