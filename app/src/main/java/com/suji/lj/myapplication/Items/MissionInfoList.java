package com.suji.lj.myapplication.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionInfoList {

    public MissionInfoList() {

    }

    public String mission_title;
    public String address;
    public boolean success;
    public boolean valid;
    public double lat;
    public double lng;
    public ArrayList<String> arrayList;
    public List<ContactItemForServer> friends_selected_list;
    public Map<String, ItemForDateTimeByList> mission_dates;
    String min_date;
    String max_date;
    String mission_id;

    public MissionInfoList(String mission_title, String address, boolean success, double lat, double lng, ArrayList<String> arrayList, Map<String, ItemForDateTimeByList> mission_dates,String max_date,String min_date) {
        this.mission_title = mission_title;
        this.address = address;
        this.success = success;
        this.lat = lat;
        this.lng = lng;
        this.arrayList = arrayList;
        this.mission_dates = mission_dates;
        this.min_date = min_date;
        this.max_date = max_date;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public List<ContactItemForServer> getFriends_selected_list() {
        return friends_selected_list;
    }

    public void setFriends_selected_list(List<ContactItemForServer> friends_selected_list) {
        this.friends_selected_list = friends_selected_list;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMin_date() {
        return min_date;
    }

    public void setMin_date(String min_date) {
        this.min_date = min_date;
    }

    public String getMax_date() {
        return max_date;
    }

    public void setMax_date(String max_date) {
        this.max_date = max_date;
    }

    public String getMission_title() {
        return mission_title;
    }

    public void setMission_title(String mission_title) {
        this.mission_title = mission_title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public Map<String, ItemForDateTimeByList> getMission_dates() {
        return mission_dates;
    }

    public void setMission_dates(Map<String, ItemForDateTimeByList> mission_dates) {
        this.mission_dates = mission_dates;
    }


}