package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ItemForFriendResponseForRequest {
    String friendId;
    String friendUuid;
    String friendName;
    String friendImage;
    int accept;
    boolean missionSuccess;
    String missionSuccessTime;



    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendUuid() {
        return friendUuid;
    }

    public void setFriendUuid(String friendUuid) {
        this.friendUuid = friendUuid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(String friendImage) {
        this.friendImage = friendImage;
    }

    public boolean isMissionSuccess() {
        return missionSuccess;
    }

    public void setMissionSuccess(boolean missionSuccess) {
        this.missionSuccess = missionSuccess;
    }

    public String getMissionSuccessTime() {
        return missionSuccessTime;
    }

    public void setMissionSuccessTime(String missionSuccessTime) {
        this.missionSuccessTime = missionSuccessTime;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }
}
