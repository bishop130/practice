package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ItemForFriendResponseForRequest implements Parcelable {
    String friend_id;
    String friend_uuid;
    String friend_name;
    String friend_fintech_num;
    String thumbnail;
    int accept;
    boolean mission_success;
    String mission_success_time;


    public ItemForFriendResponseForRequest(){}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.friend_id);
        dest.writeString(this.friend_uuid);
        dest.writeString(this.friend_name);
        dest.writeString(this.friend_fintech_num);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.accept);
        dest.writeByte((byte) (this.mission_success ? 1 : 0));
        dest.writeString(this.mission_success_time);

    }

    public ItemForFriendResponseForRequest(Parcel in) {
        this.friend_id = in.readString();
        this.friend_uuid = in.readString();
        this.friend_name = in.readString();
        this.friend_fintech_num = in.readString();
        this.thumbnail = in.readString();
        this.accept = in.readInt();
        this.mission_success = in.readByte() != 0;
        this.mission_success_time = in.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForFriendResponseForRequest createFromParcel(Parcel in) {
            return new ItemForFriendResponseForRequest(in);
        }

        @Override
        public ItemForFriendResponseForRequest[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForFriendResponseForRequest[size];
        }

    };


    public boolean isMission_success() {
        return mission_success;
    }

    public void setMission_success(boolean mission_success) {
        this.mission_success = mission_success;
    }

    public String getMission_success_time() {
        return mission_success_time;
    }

    public void setMission_success_time(String mission_success_time) {
        this.mission_success_time = mission_success_time;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_uuid() {
        return friend_uuid;
    }

    public void setFriend_uuid(String friend_uuid) {
        this.friend_uuid = friend_uuid;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getFriend_fintech_num() {
        return friend_fintech_num;
    }

    public void setFriend_fintech_num(String friend_fintech_num) {
        this.friend_fintech_num = friend_fintech_num;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }
}
