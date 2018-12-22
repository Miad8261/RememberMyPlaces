package com.johnabbottcollege.androidteamproject;

class Locations {

    private String id;
    private float lat;
    private float lng;
    private String address;
    private String currentDate;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Locations() {
    }

    public Locations(String id, String currentDate, String address, String category,float lat,float lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.currentDate = currentDate;
        this.category = category;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
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
