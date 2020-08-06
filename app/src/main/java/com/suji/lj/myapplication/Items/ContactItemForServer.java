package com.suji.lj.myapplication.Items;

import java.io.Serializable;

public class ContactItemForServer implements Serializable {

    private String friend_name;
    private String phone_num;
    String friend_image;
    String friend_uuid;
    String friend_id;
    private int amount;
    private int contact_or_friend;
    boolean success;

    public ContactItemForServer() {

    }

    public ContactItemForServer(String friend_name, String phone_num, int amount) {
        this.friend_name = friend_name;
        this.phone_num = phone_num;
        this.amount = amount;
    }


    public String getFriend_image() {
        return friend_image;
    }

    public void setFriend_image(String friend_image) {
        this.friend_image = friend_image;
    }

    public String getFriend_uuid() {
        return friend_uuid;
    }

    public void setFriend_uuid(String friend_uuid) {
        this.friend_uuid = friend_uuid;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public int getContact_or_friend() {
        return contact_or_friend;
    }

    public void setContact_or_friend(int contact_or_friend) {
        this.contact_or_friend = contact_or_friend;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
