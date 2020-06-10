package com.suji.lj.myapplication.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionInfoList implements Serializable {

    public MissionInfoList() {

    }

    public String mission_title;
    public String address;
    public boolean success;
    public boolean valid;
    public double lat;
    public double lng;
    public List<ContactItem> friends_selected_list;
    public Map<String, ItemForDateTimeByList> mission_dates;
    String min_date;
    String max_date;
    String mission_id;
    String bank_name;
    String account_num;
    int penalty_amount;
    String account_holder;
    int mission_mode;


    public int getMission_mode() {
        return mission_mode;
    }

    public void setMission_mode(int mission_mode) {
        this.mission_mode = mission_mode;
    }

    public String getAccount_holder() {
        return account_holder;
    }

    public void setAccount_holder(String account_holder) {
        this.account_holder = account_holder;
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

    public int getPenalty_amount() {
        return penalty_amount;
    }

    public void setPenalty_amount(int penalty_amount) {
        this.penalty_amount = penalty_amount;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public List<ContactItem> getFriends_selected_list() {
        return friends_selected_list;
    }

    public void setFriends_selected_list(List<ContactItem> friends_selected_list) {
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

    public Map<String, ItemForDateTimeByList> getMission_dates() {
        return mission_dates;
    }

    public void setMission_dates(Map<String, ItemForDateTimeByList> mission_dates) {
        this.mission_dates = mission_dates;
    }


}