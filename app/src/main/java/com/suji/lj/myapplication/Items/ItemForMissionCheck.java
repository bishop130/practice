package com.suji.lj.myapplication.Items;

public class ItemForMissionCheck {

    public String userId;
    public boolean success;
    public String missionId;
    public String timeStamp;
    boolean serverCheck;
    boolean single;
    String title;

    public ItemForMissionCheck() {

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isServerCheck() {
        return serverCheck;
    }

    public void setServerCheck(boolean serverCheck) {
        this.serverCheck = serverCheck;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
