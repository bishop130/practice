package com.example.lj.myapplication.Items;

public class ContactItemForServer {

    private String name;
    private String num;
    public ContactItemForServer() {

    }

    public ContactItemForServer(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
