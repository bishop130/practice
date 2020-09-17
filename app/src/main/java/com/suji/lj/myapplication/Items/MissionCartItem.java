package com.suji.lj.myapplication.Items;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;


public class MissionCartItem extends RealmObject {

    String id;

    String title;
    String date;
    String address;
    String missionId;
    int radius;

    RealmList<ItemForDateTime> calendarDayList = new RealmList<>();
    RealmList<ItemPortion> portionList = new RealmList<>();
    RealmList<ContactItem> contactList = new RealmList<>();
    RealmList<ItemForFriends> friendsList = new RealmList<>();
    int hour;
    int min;
    boolean no_time_limit;
    double lat;
    double lng;
    String minDate;
    String maxDate;
    int missionMode;

    int singlePenaltyPerDay;
    int singlePenaltyTotal;

    int multiPenaltyPerDay;
    int multiPenaltyTotal;
    int pay_method;




    public MissionCartItem(){}


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public RealmList<ContactItem> getContactList() {
        return contactList;
    }

    public void setContactList(RealmList<ContactItem> contactList) {
        this.contactList = contactList;
    }

    public RealmList<ItemForFriends> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(RealmList<ItemForFriends> friendsList) {
        this.friendsList = friendsList;
    }

    public int getPay_method() {
        return pay_method;
    }

    public void setPay_method(int pay_method) {
        this.pay_method = pay_method;
    }



    public int getMultiPenaltyPerDay() {
        return multiPenaltyPerDay;
    }

    public void setMultiPenaltyPerDay(int multiPenaltyPerDay) {
        this.multiPenaltyPerDay = multiPenaltyPerDay;
    }


    public int getMultiPenaltyTotal() {
        return multiPenaltyTotal;
    }

    public void setMultiPenaltyTotal(int multiPenaltyTotal) {
        this.multiPenaltyTotal = multiPenaltyTotal;
    }


    public RealmList<ItemPortion> getPortionList() {
        return portionList;
    }

    public void setPortionList(RealmList<ItemPortion> portionList) {
        this.portionList = portionList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMinDate() {
        return minDate;
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

    public int getMissionMode() {
        return missionMode;
    }

    public void setMissionMode(int missionMode) {
        this.missionMode = missionMode;
    }

    public int getSinglePenaltyPerDay() {
        return singlePenaltyPerDay;
    }

    public void setSinglePenaltyPerDay(int singlePenaltyPerDay) {
        this.singlePenaltyPerDay = singlePenaltyPerDay;
    }

    public int getSinglePenaltyTotal() {
        return singlePenaltyTotal;
    }

    public void setSinglePenaltyTotal(int singlePenaltyTotal) {
        this.singlePenaltyTotal = singlePenaltyTotal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public RealmList<ItemForDateTime> getCalendarDayList() {
        return calendarDayList;
    }

    public void setCalendarDayList(RealmList<ItemForDateTime> calendarDayList) {
        this.calendarDayList = calendarDayList;
    }

    public boolean isNo_time_limit() {
        return no_time_limit;
    }

    public void setNo_time_limit(boolean no_time_limit) {
        this.no_time_limit = no_time_limit;
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
