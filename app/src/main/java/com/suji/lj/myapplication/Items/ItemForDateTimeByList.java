package com.suji.lj.myapplication.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemForDateTimeByList implements Serializable {
    String time_stamp;
    boolean success;
    String date_time;
    List<ItemForFriendByDay> friendByDayList;

    public ItemForDateTimeByList(){

    }


    public List<ItemForFriendByDay> getFriendByDayList() {
        return friendByDayList;
    }

    public void setFriendByDayList(List<ItemForFriendByDay> friendByDayList) {
        this.friendByDayList = friendByDayList;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
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
