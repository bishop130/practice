package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemForFriendMissionCheck implements Parcelable {

    String user_id;
    String user_name;
    String user_image;
    String user_uuid;
    boolean success;
    String success_time;

    @Override
    public int describeContents() {
        return 0;
    }
    public ItemForFriendMissionCheck(){}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_id);
        dest.writeString(this.user_name);
        dest.writeString(this.user_image);
        dest.writeString(this.user_uuid);
        dest.writeByte((byte) (this.success ? 1 : 0));
        dest.writeString(this.success_time);

    }

    public ItemForFriendMissionCheck(Parcel in) {
        this.user_id = in.readString();
        this.user_name = in.readString();
        this.user_image = in.readString();
        this.user_uuid = in.readString();
        this.success = in.readByte() != 0;
        this.success_time = in.readString();


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemForFriendMissionCheck createFromParcel(Parcel in) {
            return new ItemForFriendMissionCheck(in);
        }

        @Override
        public ItemForFriendMissionCheck[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemForFriendMissionCheck[size];
        }

    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSuccess_time() {
        return success_time;
    }

    public void setSuccess_time(String success_time) {
        this.success_time = success_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(String user_uuid) {
        this.user_uuid = user_uuid;
    }
}
