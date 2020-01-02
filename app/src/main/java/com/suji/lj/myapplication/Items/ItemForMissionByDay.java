package com.suji.lj.myapplication.Items;

public class ItemForMissionByDay {


    String title;
    String address;
    String date;
    String time;
    double lat;
    double lng;
    boolean success;
    public ItemForMissionByDay(){}

    public ItemForMissionByDay(String title, String address, String date, String time, double lat, double lng,boolean success) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
