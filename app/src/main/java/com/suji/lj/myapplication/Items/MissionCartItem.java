package com.suji.lj.myapplication.Items;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;


public class MissionCartItem extends RealmObject {

    String title;
    String date;
    String address;
    RealmList<DateItem> calendarDayList = new RealmList<>();
    int hour;
    int min;
    boolean no_time_limit;

    public MissionCartItem(){}



    public MissionCartItem(String title, String date, String address, RealmList<DateItem> calendarDayList, int hour, int min, boolean no_time_limit) {
        this.title = title;
        this.date = date;
        this.address = address;
        this.calendarDayList = calendarDayList;
        this.hour = hour;
        this.min = min;
        this.no_time_limit = no_time_limit;
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

    public RealmList<DateItem> getCalendarDayList() {
        return calendarDayList;
    }

    public void setCalendarDayList(RealmList<DateItem> calendarDayList) {
        this.calendarDayList = calendarDayList;
    }

    public boolean isNo_time_limit() {
        return no_time_limit;
    }

    public void setNo_time_limit(boolean no_time_limit) {
        this.no_time_limit = no_time_limit;
    }
}
