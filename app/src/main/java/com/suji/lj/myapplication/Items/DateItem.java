package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class DateItem extends RealmObject {

    int year;
    int month;
    int day;
    String min_date;
    String max_date;


    public DateItem(){}

    public DateItem(int year, int month, int day, String min_date, String max_date) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.min_date = min_date;
        this.max_date = max_date;
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
}
