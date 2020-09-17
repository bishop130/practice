package com.suji.lj.myapplication.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionInfoList implements Serializable {

    public MissionInfoList() {

    }

    public String title;
    public String address;
    public boolean success;
    public double lat;
    public double lng;
    public List<ContactItem> friends_selected_list;
    List<ItemForFriendByDay> friendByDayList;
    public Map<String, ItemForDateTimeByList> missionDates;
    public Map<String, Object> successCount;
    List<ItemPortion> portionList;
    String minDate;
    String maxDate;
    String minTime;
    String maxTime;
    String missionId;
    String bankName;
    String accountNum;
    int penaltyTotal;
    int penaltyPerDay;
    String accountHolder;
    boolean singleMode;
    int failedCount;
    boolean activation;


    public Map<String, Object> getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Map<String, Object> successCount) {
        this.successCount = successCount;
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

    public List<ContactItem> getFriends_selected_list() {
        return friends_selected_list;
    }

    public void setFriends_selected_list(List<ContactItem> friends_selected_list) {
        this.friends_selected_list = friends_selected_list;
    }

    public List<ItemForFriendByDay> getFriendByDayList() {
        return friendByDayList;
    }

    public void setFriendByDayList(List<ItemForFriendByDay> friendByDayList) {
        this.friendByDayList = friendByDayList;
    }

    public Map<String, ItemForDateTimeByList> getMissionDates() {
        return missionDates;
    }

    public void setMissionDates(Map<String, ItemForDateTimeByList> missionDates) {
        this.missionDates = missionDates;
    }

    public List<ItemPortion> getPortionList() {
        return portionList;
    }

    public void setPortionList(List<ItemPortion> portionList) {
        this.portionList = portionList;
    }

    public String getMinDate() {
        return minDate;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
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

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public boolean isSingleMode() {
        return singleMode;
    }

    public void setSingleMode(boolean singleMode) {
        this.singleMode = singleMode;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public boolean isActivation() {
        return activation;
    }

    public void setActivation(boolean activation) {
        this.activation = activation;
    }
}