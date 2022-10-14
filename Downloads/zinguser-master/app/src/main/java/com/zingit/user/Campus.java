package com.zingit.user;

import com.google.firebase.firestore.GeoPoint;

public class Campus {
    private String address;
    private GeoPoint location;
    //to be changed to geoPoint
    private String name;
    private String subName;
    private String contactNumber;
    private String id;

    /* only fetching allowed. immutable */

    public Campus(String address, GeoPoint location, String name, String subName) {
        this.address = address;
        this.location = location;
        this.name = name;
        this.subName = subName;
    }

    public Campus(String address, GeoPoint location, String name, String subName, String contactNumber, String id) {
        this.address = address;
        this.location = location;
        this.name = name;
        this.subName = subName;
        this.contactNumber = contactNumber;
        this.id = id;
    }

    public Campus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
