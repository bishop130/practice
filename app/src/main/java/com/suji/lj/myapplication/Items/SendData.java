package com.suji.lj.myapplication.Items;

public class SendData{
    String name;
    String number;
    public SendData(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public SendData(String name, String number) {
        this.name = name;
        this.number = number;

    }
}