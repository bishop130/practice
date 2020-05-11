package com.suji.lj.myapplication.Items;

import java.io.Serializable;

public class ContactItemForServer implements Serializable {

    private String friend_name;
    private String phone_num;
    private int amount;
    private int contact_or_friend;
    public ContactItemForServer() {

    }

    public ContactItemForServer(String friend_name, String phone_num, int amount) {
        this.friend_name = friend_name;
        this.phone_num = phone_num;
        this.amount = amount;
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
