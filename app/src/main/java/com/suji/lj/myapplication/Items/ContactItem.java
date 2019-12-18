package com.suji.lj.myapplication.Items;

import android.app.Person;
import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ContactItem extends RealmObject implements Parcelable {

    private String displayName;
    private String phoneNumbers;
    private  boolean isSelected;
    private int position;

    public ContactItem(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(this.displayName);
        dest.writeString(this.phoneNumbers);
        dest.writeByte((byte) (this.isSelected ? 1 : 0));
        dest.writeInt(this.position);


    }

    public ContactItem(String displayName, String phoneNumbers) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;

    }
    public ContactItem(String displayName, String phoneNumbers, boolean isSelected, int position){
        this.displayName=displayName;
        this.phoneNumbers=phoneNumbers;
        this.isSelected=isSelected;
        this.position = position;

    }
    public ContactItem(Parcel in) {
        this.displayName = in.readString();
        this.phoneNumbers = in.readString();
        this.isSelected = in.readByte() !=0;
        this.position = in.readInt();
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ContactItem createFromParcel(Parcel in) {
            return new ContactItem(in);
        }

        @Override
        public ContactItem[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ContactItem[size];
        }

    };

}
