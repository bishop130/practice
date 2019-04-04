package com.example.lj.myapplication.Items;

public class ContactItem {

    private String displayName;
    private String phoneNumbers;
    private  boolean isSelected;

    public ContactItem(){

    }


    public ContactItem(String displayName, String phoneNumbers) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;
    }
    public ContactItem(String displayName, String phoneNumbers, boolean isSelected){
        this.displayName=displayName;
        this.phoneNumbers=phoneNumbers;
        this.isSelected=isSelected;
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
