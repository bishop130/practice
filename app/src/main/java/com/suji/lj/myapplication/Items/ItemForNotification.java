package com.suji.lj.myapplication.Items;

import java.util.List;

public class ItemForNotification {

    //알림종류

    //지난약속 알림

    String key;
    String title;
    String mission_id;
    boolean single;
    boolean success;
    boolean read;
    String date_time;
    String content;

    //새약속 등록 알림
    int penalty;


    //친구추가 알림
    String friend_name;
    String friend_image;
    String friend_id;

    List<ItemForFriendByDay> forFriendByDayList;



    int notification_code;


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

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
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

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
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

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getFriend_image() {
        return friend_image;
    }

    public void setFriend_image(String friend_image) {
        this.friend_image = friend_image;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public int getNotification_code() {
        return notification_code;
    }

    public void setNotification_code(int notification_code) {
        this.notification_code = notification_code;
    }
}
