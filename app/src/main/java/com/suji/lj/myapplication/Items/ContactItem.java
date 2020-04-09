package com.suji.lj.myapplication.Items;

import android.app.Person;
import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ContactItem extends RealmObject implements Parcelable {


    private String displayName;
    private String phoneNumbers;
    private boolean isSelected;
    private int position;
    private int amount;
    private boolean friend_selected;
    private int friend_position;
    private int contact_or_friend;


    public ContactItem() {

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
        dest.writeInt(this.amount);
        dest.writeByte((byte) (this.friend_selected ? 1 : 0));
        dest.writeInt(this.friend_position);
        dest.writeInt(this.contact_or_friend);


    }

    public ContactItem(String displayName, String phoneNumbers) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;

    }

    public ContactItem(String displayName, String phoneNumbers, boolean isSelected, int position, int amount) {
        this.displayName = displayName;
        this.phoneNumbers = phoneNumbers;
        this.isSelected = isSelected;
        this.position = position;
        this.amount = amount;

    }

    public ContactItem(Parcel in) {
        this.displayName = in.readString();
        this.phoneNumbers = in.readString();
        this.isSelected = in.readByte() != 0;
        this.position = in.readInt();
        this.amount = in.readInt();
        this.friend_selected = in.readByte() !=0;
        this.friend_position = in.readInt();
        this.contact_or_friend = in.readInt();



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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isFriend_selected() {
        return friend_selected;
    }

    public void setFriend_selected(boolean friend_selected) {
        this.friend_selected = friend_selected;
    }

    public int getFriend_position() {
        return friend_position;
    }

    public void setFriend_position(int friend_position) {
        this.friend_position = friend_position;
    }

    public int getContact_or_friend() {
        return contact_or_friend;
    }

    public void setContact_or_friend(int contact_or_friend) {
        this.contact_or_friend = contact_or_friend;
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
