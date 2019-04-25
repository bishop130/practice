package com.suji.lj.myapplication.Items;

public class AlarmItem {
    private String time;
    private String lat;
    private String lng;
    private String mission_id;
    private String date;
    private String date_time;
    private String is_success;
    private String title;
    private String user_id;
    public AlarmItem(){

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public AlarmItem(String time, String lat, String lng, String mission_id, String date, String date_time, String is_success,String title, String user_id) {
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.mission_id = mission_id;
        this.date = date;
        this.date_time = date_time;
        this.is_success = is_success;
        this.title = title;
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dat) {
        this.date = dat;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getIs_success() {
        return is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
