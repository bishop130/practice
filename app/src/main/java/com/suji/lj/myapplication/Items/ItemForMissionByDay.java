package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ItemForMissionByDay extends RealmObject implements Parcelable {


    String title;
    String address;
    String date;
    String time;
    double lat;
    double lng;
    boolean success;
    String mother_id;
    String date_time;

    public ItemForMissionByDay() {
    }

    public ItemForMissionByDay(Parcel in) {
        this.title = in.readString();
        this.address = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.success = in.readByte() != 0;
        this.mother_id = in.readString();
        this.date_time = in.readString();


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeString(this.date);
        dest.writeString(this.time);

        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeByte((byte) (this.success ? 1 : 0));
        dest.writeString(this.mother_id);
        dest.writeString(this.date_time);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForMissionByDay createFromParcel(Parcel in) {
            return new ItemForMissionByDay(in);
        }

        @Override
        public ItemForMissionByDay[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForMissionByDay[size];
        }

    };

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getMother_id() {
        return mother_id;
    }

    public void setMother_id(String mother_id) {
        this.mother_id = mother_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
