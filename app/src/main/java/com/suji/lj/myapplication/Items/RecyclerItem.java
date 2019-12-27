package com.suji.lj.myapplication.Items;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecyclerItem implements Comparable<RecyclerItem> {

    private String missionTitle;
    private String missionID;
    private Double Latitude;
    private Double Longitude;
    private String MissionTime;
    private String date_time;
    private String date_array;
    private String contact_json;
    private String completed_dates;
    private String total_dates;
    private boolean success;
    private String is_failed;
    private String register_date;
    private String date;
    private String address;
    private String completed;


    public RecyclerItem(){

    }




    public RecyclerItem(String missionTitle, String missionID, Double Latitude, Double Longitude,
                        String MissionTime, String date_time, String date_array,String contact_json,String completed_dates,String total_dates, boolean success, String register_date, String date,String is_failed,String address,String completed) {
        this.missionTitle = missionTitle;
        this.missionID = missionID;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.MissionTime = MissionTime;
        this.date_time = date_time;
        this.date_array=date_array;
        this.contact_json=contact_json;
        this.completed_dates = completed_dates;
        this.total_dates = total_dates;
        this.success = success;
        this.register_date = register_date;
        this.date = date;
        this.is_failed =is_failed;
        this.address = address;
        this.completed = completed;


    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getIs_failed() {
        return is_failed;
    }

    public void setIs_failed(String is_failed) {
        this.is_failed = is_failed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTotal_dates() {
        return total_dates;
    }

    public void setTotal_dates(String total_dates) {
        this.total_dates = total_dates;
    }

    public String getContact_json() {
        return contact_json;
    }

    public void setContact_json(String contact_json) {
        this.contact_json = contact_json;
    }

    public String getDate_array() {
        return date_array;
    }

    public void setDate_array(String date_array) {
        this.date_array = date_array;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date) {
        date_time = date;
    }

    public String getMissionTitle() {
        return missionTitle;
    }

    public String getMissionID() {
        return missionID;
    }

    public Double getLatitude(){

        return Latitude;
    }

    public String getCompleted_dates() {
        return completed_dates;
    }

    public void setCompleted_dates(String completed_dates) {
        this.completed_dates = completed_dates;
    }

    public Double getLongitude(){

        return Longitude;
    }

    public String getMissionTime(){

        return MissionTime;
    }

    public void setMissionTitle(String missionTitle) {
        this.missionTitle = missionTitle;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public void setLatitude(Double Latitude){
        this.Latitude = Latitude;
    }

    public void setLongitude(Double Longitude){
        this.Longitude = Longitude;
    }

    public void setMissionTime(String MissionTime){
        this.MissionTime = MissionTime;

    }

    @Override
    public int compareTo(RecyclerItem o) {

        Date newDate = formatDateTime(o.getDate_time(), "yyyyMMdd HH:mm");
        Date inputDate = formatDateTime(getDate_time(), "yyyyMMdd HH:mm");

        return inputDate.compareTo(newDate);
    }
    public Date formatDateTime(String date, String fromFormat) {
      Date d = null;
        try {
            d = new SimpleDateFormat(fromFormat, Locale.KOREA).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
