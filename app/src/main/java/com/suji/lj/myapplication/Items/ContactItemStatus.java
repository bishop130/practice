package com.suji.lj.myapplication.Items;

import io.realm.RealmObject;

public class ContactItemStatus extends RealmObject {

    private String displayName;
    private String phoneNumbers;
    private  boolean isSelected;
    private int position;

    public ContactItemStatus(){

    }

    public ContactItemStatus(String displayName, String phoneNumbers, boolean isSelected, int position) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;
        this.isSelected = isSelected;
        this.position = position;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
