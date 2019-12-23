package com.suji.lj.myapplication.Items;

import java.util.ArrayList;
import java.util.Map;

public class MissionInfoList {

    public MissionInfoList() {

    }

    public String mission_title;
    public String mission_time;
    public String address;
    public boolean is_success;
    public double lat;
    public double lng;
    public ArrayList<String> arrayList;
    public Map<String, Object> mission_dates;
    public String mission_info_root_id;

    public MissionInfoList(String mission_title, String mission_time, String address, boolean is_success, double lat, double lng, ArrayList<String> arrayList, Map<String, Object> mission_dates, String mission_info_root_id) {
        this.mission_title = mission_title;
        this.mission_time = mission_time;
        this.address = address;
        this.is_success = is_success;
        this.lat = lat;
        this.lng = lng;
        this.arrayList = arrayList;
        this.mission_dates = mission_dates;
        this.mission_info_root_id = mission_info_root_id;
    }

    public String getMission_title() {
        return mission_title;
    }

    public void setMission_title(String mission_title) {
        this.mission_title = mission_title;
    }

    public String getMission_time() {
        return mission_time;
    }

    public void setMission_time(String mission_time) {
        this.mission_time = mission_time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isIs_success() {
        return is_success;
    }

    public void setIs_success(boolean is_success) {
        this.is_success = is_success;
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

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public Map<String, Object> getMission_dates() {
        return mission_dates;
    }

    public void setMission_dates(Map<String, Object> mission_dates) {
        this.mission_dates = mission_dates;
    }


    public String getMission_info_root_id() {
        return mission_info_root_id;
    }

    public void setMission_info_root_id(String mission_info_root_id) {
        this.mission_info_root_id = mission_info_root_id;
    }
}