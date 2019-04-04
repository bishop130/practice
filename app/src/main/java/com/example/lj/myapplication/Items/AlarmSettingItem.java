package com.example.lj.myapplication.Items;

public class AlarmSettingItem {

    String alarm_time;
    long time;
    private  boolean isSelected;

    public AlarmSettingItem(String alarm_time,long time, boolean isSelected){
        this.isSelected = isSelected;
        this.alarm_time = alarm_time;
        this.time = time;

    }

    public AlarmSettingItem(String alarm_time,long time){

        this.alarm_time = alarm_time;
        this.time = time;

    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
