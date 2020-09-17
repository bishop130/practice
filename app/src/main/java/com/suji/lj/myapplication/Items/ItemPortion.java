package com.suji.lj.myapplication.Items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class ItemPortion extends RealmObject {
    int portion;



    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }
}
