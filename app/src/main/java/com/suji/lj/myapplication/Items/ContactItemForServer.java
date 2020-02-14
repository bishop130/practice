package com.suji.lj.myapplication.Items;

public class ContactItemForServer {

    private String friend_name;
    private String phone_num;
    private int amount;
    public ContactItemForServer() {

    }

    public ContactItemForServer(String friend_name, String phone_num, int amount) {
        this.friend_name = friend_name;
        this.phone_num = phone_num;
        this.amount = amount;
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
