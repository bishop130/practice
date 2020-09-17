package com.suji.lj.myapplication.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemForDateTimeByList implements Serializable {
    String timeStamp;
    boolean success;
    String dateTime;
    List<ItemForFriendByDay> friendByDayList;

    public ItemForDateTimeByList(){

    }


    public List<ItemForFriendByDay> getFriendByDayList() {
        return friendByDayList;
    }

    public void setFriendByDayList(List<ItemForFriendByDay> friendByDayList) {
        this.friendByDayList = friendByDayList;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    /*
    @Override
    public int compareTo(ItemForDateTimeByList o) {
        if (Integer.valueOf(this.getDate()+this.getTime()) < Integer.valueOf(o.getDate()+o.getTime())) {
            return -1;
        } else if (Integer.valueOf(this.getDate()+this.getTime()) > Integer.valueOf(o.getDate()+o.getTime())) {
            return 1;
        }
        return 0;
    }

 */

}
