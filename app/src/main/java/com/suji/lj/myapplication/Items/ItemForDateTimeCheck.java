package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ItemForDateTimeCheck implements Parcelable {


    String date;
    String time;
    int year;
    int month;
    int day;
    int hour;
    int min;
    List<ItemForFriendMissionCheck> friendMissionCheckList = new ArrayList<>();

    public ItemForDateTimeCheck() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ItemForDateTimeCheck(Parcel in) {
        this.date = in.readString();
        this.time = in.readString();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
        this.min = in.readInt();
        in.readList(this.friendMissionCheckList, ItemForFriendMissionCheck.class.getClassLoader());



    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.min);
        dest.writeList(this.friendMissionCheckList);

    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForDateTimeCheck createFromParcel(Parcel in) {
            return new ItemForDateTimeCheck(in);
        }

        @Override
        public ItemForDateTimeCheck[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForDateTimeCheck[size];
        }

    };

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

    public List<ItemForFriendMissionCheck> getFriendMissionCheckList() {
        return friendMissionCheckList;
    }

    public void setFriendMissionCheckList(List<ItemForFriendMissionCheck> friendMissionCheckList) {
        this.friendMissionCheckList = friendMissionCheckList;
    }
}
