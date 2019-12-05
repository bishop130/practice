package com.suji.lj.myapplication.Items;

public class ContactItem {

    private String displayName;
    private String phoneNumbers;
    private  boolean isSelected;
    private int contact_count;
    private int position;

    public ContactItem(){

    }


    public ContactItem(String displayName, String phoneNumbers) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;

    }
    public ContactItem(String displayName, String phoneNumbers, boolean isSelected, int contact_count,int position){
        this.displayName=displayName;
        this.phoneNumbers=phoneNumbers;
        this.isSelected=isSelected;
        this.contact_count=contact_count;
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getContact_count() {
        return contact_count;
    }

    public void setContact_count(int contact_count) {
        this.contact_count = contact_count;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
