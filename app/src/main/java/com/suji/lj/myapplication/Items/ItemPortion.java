package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ItemPortion extends RealmObject implements Parcelable {
    int portion;


    public ItemPortion() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.portion);

    }
    public ItemPortion(Parcel in) {
        this.portion = in.readInt();

    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public ItemPortion createFromParcel(Parcel in) {
            return new ItemPortion(in);
        }

        @Override
        public ItemPortion[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ItemPortion[size];
        }

    };

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }
}
