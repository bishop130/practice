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
    String mission_id;
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
    String min_date;
    String max_date;
    String bank_name;
    String account_num;
    String account_holder;
    int mission_mode;

    int single_point;
    int single_penalty;
    int single_total;

    int multi_point;
    int multi_penalty;
    int multi_total;
    int pay_method;




    public MissionCartItem(){}


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
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

    public int getMission_mode() {
        return mission_mode;
    }


    public int getSingle_point() {
        return single_point;
    }

    public void setSingle_point(int single_point) {
        this.single_point = single_point;
    }

    public int getSingle_penalty() {
        return single_penalty;
    }

    public void setSingle_penalty(int single_penalty) {
        this.single_penalty = single_penalty;
    }

    public int getMulti_point() {
        return multi_point;
    }

    public void setMulti_point(int multi_point) {
        this.multi_point = multi_point;
    }


    public int getSingle_total() {
        return single_total;
    }

    public void setSingle_total(int single_total) {
        this.single_total = single_total;
    }

    public int getMulti_penalty() {
        return multi_penalty;
    }

    public void setMulti_penalty(int multi_penalty) {
        this.multi_penalty = multi_penalty;
    }

    public int getMulti_total() {
        return multi_total;
    }

    public void setMulti_total(int multi_total) {
        this.multi_total = multi_total;
    }

    public void setMission_mode(int mission_mode) {
        this.mission_mode = mission_mode;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_num() {
        return account_num;
    }

    public void setAccount_num(String account_num) {
        this.account_num = account_num;
    }

    public String getAccount_holder() {
        return account_holder;
    }

    public void setAccount_holder(String account_holder) {
        this.account_holder = account_holder;
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
