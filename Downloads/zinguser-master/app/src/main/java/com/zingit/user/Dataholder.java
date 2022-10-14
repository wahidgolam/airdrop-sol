package com.zingit.user;

import java.util.ArrayList;

public class Dataholder {
    public static ArrayList<Outlet> outletList;
    public static Outlet currentOutlet;
    public static StudentUser studentUser;
    public static Cart cart;
    public static String contactNumber;
    public static ArrayList<Order> orderList = new ArrayList<>();
    public static String userId="";
    public static Boolean newUser=false;
    public static ArrayList<Order> orderHistoryList = new ArrayList<>();
    public static Boolean firstTimeLogin = false;
    public static double taxPercentage;
    //For bottom navigation bar 1 = history is clicked
    // 2 = home is clicked
    // 3 = cart is clicked

    public static int path=1;
}
