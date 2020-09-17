package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemForMultiModeRequest {
    String title;

    String managerName;
    String managerId;
    String managerUuid;
    String managerImage;
    String address;
    String missionId;
    String registeredTime;

    int accept;
    double lat;
    double lng;

    int penaltyTotal;
    int penaltyPerDay;

    List<ItemForDateTime> calendarDayList = new ArrayList<>();
    List<ItemForFriendResponseForRequest> friendRequestList = new ArrayList<>();
    List<ItemPortion> portionList = new ArrayList<>();
    List<ItemForDateTimeCheck> dateTimeCheckList = new ArrayList<>();
    public Map<String, ItemForDateTimeByList> missionDates;


    public ItemForMultiModeRequest() {
    }


    public Map<String, ItemForDateTimeByList> getMissionDates() {
        return missionDates;
    }

    public void setMissionDates(Map<String, ItemForDateTimeByList> missionDates) {
        this.missionDates = missionDates;
    }

    public List<ItemForDateTimeCheck> getDateTimeCheckList() {
        return dateTimeCheckList;
    }

    public void setDateTimeCheckList(List<ItemForDateTimeCheck> dateTimeCheckList) {
        this.dateTimeCheckList = dateTimeCheckList;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }


    public List<ItemPortion> getPortionList() {
        return portionList;
    }

    public void setPortionList(List<ItemPortion> portionList) {
        this.portionList = portionList;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerUuid() {
        return managerUuid;
    }

    public void setManagerUuid(String managerUuid) {
        this.managerUuid = managerUuid;
    }

    public String getManagerImage() {
        return managerImage;
    }

    public void setManagerImage(String managerImage) {
        this.managerImage = managerImage;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(String registeredTime) {
        this.registeredTime = registeredTime;
    }

    public int getPenaltyTotal() {
        return penaltyTotal;
    }

    public void setPenaltyTotal(int penaltyTotal) {
        this.penaltyTotal = penaltyTotal;
    }

    public int getPenaltyPerDay() {
        return penaltyPerDay;
    }

    public void setPenaltyPerDay(int penaltyPerDay) {
        this.penaltyPerDay = penaltyPerDay;
    }
}
