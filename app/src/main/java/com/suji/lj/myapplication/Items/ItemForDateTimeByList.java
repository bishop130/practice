package com.suji.lj.myapplication.Items;

import java.io.Serializable;

public class ItemForDateTimeByList implements Serializable {
    String date;
    String time;
    boolean success;
    String time_stamp;
    int year;
    int month;
    int day;
    int hour;
    int min;

    public ItemForDateTimeByList(){

    }


    public ItemForDateTimeByList(String date, String time, boolean success) {
        this.date = date;
        this.time = time;
        this.success = success;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
/*
    @Override
    public int compareTo(ItemForDateTimeByList o) {
        if (Integer.valueOf(this.getDate()+this.getTime()) < Integer.valueOf(o.getDate()+o.getTime())) {
            return -1;
        } else if (Integer.valueOf(this.getDate()+this.getTime()) > Integer.valueOf(o.getDate()+o.getTime())) {
            return 1;
        }
        return 0;
    }

 */

}
