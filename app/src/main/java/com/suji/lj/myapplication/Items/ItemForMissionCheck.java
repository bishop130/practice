package com.suji.lj.myapplication.Items;

public class ItemForMissionCheck {

    public String user_id;
    public boolean success;
    public String root_id;
    public String mission_id;
    public String time_stamp;
    boolean server_check;
    boolean single;

    public ItemForMissionCheck() {

    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isServer_check() {
        return server_check;
    }

    public void setServer_check(boolean server_check) {
        this.server_check = server_check;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public String getRoot_id() {
        return root_id;
    }

    public void setRoot_id(String root_id) {
        this.root_id = root_id;
    }


    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
