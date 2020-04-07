package com.suji.lj.myapplication.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemForMultiModeRequest {

    String mission_key;
    String manager_name;
    String manager_id;
    String manager_uuid;
    double lat;
    double lng;
    String address;
    int late_penalty;
    int fail_penalty;
    String title;
    List<ItemForDateTime> calendarDayList = new ArrayList<>();
    List<ItemForFriendResponseForRequest> friendRequestList = new ArrayList<>();
    String manager_thumbnail;



    public String getMission_key() {
        return mission_key;
    }

    public void setMission_key(String mission_key) {
        this.mission_key = mission_key;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getManager_uuid() {
        return manager_uuid;
    }

    public void setManager_uuid(String manager_uuid) {
        this.manager_uuid = manager_uuid;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLate_penalty() {
        return late_penalty;
    }

    public void setLate_penalty(int late_penalty) {
        this.late_penalty = late_penalty;
    }

    public int getFail_penalty() {
        return fail_penalty;
    }

    public void setFail_penalty(int fail_penalty) {
        this.fail_penalty = fail_penalty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemForDateTime> getCalendarDayList() {
        return calendarDayList;
    }

    public void setCalendarDayList(List<ItemForDateTime> calendarDayList) {
        this.calendarDayList = calendarDayList;
    }

    public List<ItemForFriendResponseForRequest> getFriendRequestList() {
        return friendRequestList;
    }

    public void setFriendRequestList(List<ItemForFriendResponseForRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    public String getManager_thumbnail() {
        return manager_thumbnail;
    }

    public void setManager_thumbnail(String manager_thumbnail) {
        this.manager_thumbnail = manager_thumbnail;
    }
}
