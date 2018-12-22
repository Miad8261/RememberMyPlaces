package com.johnabbottcollege.androidteamproject;

public class Parking {


    private String address;
    private String currentDate;
    private String id;

    public Parking() {
    }

    public Parking(String id, String currentDate,String address) {
        this.id = id;
        this.address = address;
        this.currentDate = currentDate;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
