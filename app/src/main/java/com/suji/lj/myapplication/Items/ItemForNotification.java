package com.suji.lj.myapplication.Items;

import java.util.List;

public class ItemForNotification {

    //알림종류

    //지난약속 알림

    String key;
    String title;
    String missionId;
    boolean single;
    boolean success;
    boolean read;
    String dateTime;
    String content;


    //친구추가 알림
    String friendName;
    String friendImage;
    String friendId;

    List<ItemForFriendByDay> forFriendByDayList;


    int code;


    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public List<ItemForFriendByDay> getForFriendByDayList() {
        return forFriendByDayList;
    }

    public void setForFriendByDayList(List<ItemForFriendByDay> forFriendByDayList) {
        this.forFriendByDayList = forFriendByDayList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}


