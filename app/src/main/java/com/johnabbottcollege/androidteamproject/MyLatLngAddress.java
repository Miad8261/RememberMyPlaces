package com.johnabbottcollege.androidteamproject;

import java.util.Date;

public class MyLatLngAddress {

    private  double latitude;
    private  double longtitude;
    private  String address;
    Date date;


    public MyLatLngAddress() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;

    }

    public MyLatLngAddress(double latitude, double longtitude, String address) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.address = address;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
