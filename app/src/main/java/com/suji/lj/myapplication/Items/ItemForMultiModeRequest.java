package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ItemForMultiModeRequest implements Parcelable {
    String title;
    String mission_key;
    String manager_name;
    String manager_id;
    String manager_uuid;
    String manager_thumbnail;
    String address;
    String mission_id;
    double lat;
    double lng;

    int late_penalty;
    int fail_penalty;

    List<ItemForDateTime> calendarDayList = new ArrayList<>();
    List<ItemForFriendResponseForRequest> friendRequestList = new ArrayList<>();
    List<ItemPortion> itemPortionList = new ArrayList<>();


    public ItemForMultiModeRequest(){}

    public ItemForMultiModeRequest(Parcel in) {
        this.title = in.readString();
        this.mission_key = in.readString();
        this.manager_name = in.readString();
        this.manager_id = in.readString();
        this.manager_uuid = in.readString();
        this.manager_thumbnail = in.readString();
        this.address = in.readString();
        this.mission_id = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.late_penalty = in.readInt();
        this.fail_penalty = in.readInt();
        in.readList(this.calendarDayList, ItemForDateTime.class.getClassLoader());
        in.readList(this.friendRequestList,ItemForFriendResponseForRequest.class.getClassLoader());
        in.readList(this.itemPortionList,ItemPortion.class.getClassLoader());



    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForMultiModeRequest createFromParcel(Parcel in) {
            return new ItemForMultiModeRequest(in);
        }

        @Override
        public ItemForMultiModeRequest[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForMultiModeRequest[size];
        }

    };




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.title);
        dest.writeString(this.mission_key);
        dest.writeString(this.manager_name);
        dest.writeString(this.manager_id);
        dest.writeString(this.manager_uuid);
        dest.writeString(this.manager_thumbnail);
        dest.writeString(this.address);
        dest.writeString(this.mission_id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.late_penalty);
        dest.writeInt(this.fail_penalty);
        dest.writeList(this.calendarDayList);
        dest.writeList(this.friendRequestList);
        dest.writeList(this.itemPortionList);

    }


    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public List<ItemPortion> getItemPortionList() {
        return itemPortionList;
    }

    public void setItemPortionList(List<ItemPortion> itemPortionList) {
        this.itemPortionList = itemPortionList;
    }

    public String getMission_key() {
        return mission_key;
    }

    public void setMission_key(String mission_key) {
        this.mission_key = mission_key;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getManager_uuid() {
        return manager_uuid;
    }

    public void setManager_uuid(String manager_uuid) {
        this.manager_uuid = manager_uuid;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLate_penalty() {
        return late_penalty;
    }

    public void setLate_penalty(int late_penalty) {
        this.late_penalty = late_penalty;
    }

    public int getFail_penalty() {
        return fail_penalty;
    }

    public void setFail_penalty(int fail_penalty) {
        this.fail_penalty = fail_penalty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemForDateTime> getCalendarDayList() {
        return calendarDayList;
    }

    public void setCalendarDayList(List<ItemForDateTime> calendarDayList) {
        this.calendarDayList = calendarDayList;
    }

    public List<ItemForFriendResponseForRequest> getFriendRequestList() {
        return friendRequestList;
    }

    public void setFriendRequestList(List<ItemForFriendResponseForRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    public String getManager_thumbnail() {
        return manager_thumbnail;
    }

    public void setManager_thumbnail(String manager_thumbnail) {
        this.manager_thumbnail = manager_thumbnail;
    }
}
