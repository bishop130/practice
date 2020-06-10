package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class ItemForDateTime extends RealmObject implements Parcelable {

    String date;
    String time;
    boolean select;
    int position;
    int year;
    int month;
    int day;
    int hour;
    int min;
    //List<ItemForFriendMissionCheck> friendMissionCheckList = new ArrayList<>();

    public ItemForDateTime(){

    }
    public ItemForDateTime(Parcel in) {
        this.date = in.readString();
        this.time = in.readString();
        this.select = in.readByte() != 0;
        this.position = in.readInt();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
        this.min = in.readInt();
        //in.readList(this.friendMissionCheckList, ItemForFriendMissionCheck.class.getClassLoader());



    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeByte((byte) (this.select ? 1 : 0));
        dest.writeInt(this.position);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.min);
        //dest.writeList(this.friendMissionCheckList);


    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForDateTime createFromParcel(Parcel in) {
            return new ItemForDateTime(in);
        }

        @Override
        public ItemForDateTime[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForDateTime[size];
        }

    };


    public ItemForDateTime(String date, String time, boolean select) {
        this.date = date;
        this.time = time;
        this.select = select;
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

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
}
