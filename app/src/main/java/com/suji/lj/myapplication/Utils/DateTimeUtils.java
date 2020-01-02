package com.suji.lj.myapplication.Utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {



    public static String makeDateTimeForHuman(String date, String time) {

        String mission_time;
        DateTimeFormatter dtf = new DateTimeFormatter();
        String minutes;

        String month = "";
        String day = "";


        int hour = Integer.valueOf(time.substring(0,1));
        int min = Integer.valueOf(time.substring(2,3));
        if (min < 10) {
            minutes = "0" + String.valueOf(min);
        } else {

            minutes = String.valueOf(min);
        }


        if (hour == 12) {
            mission_time = "오후\n" + hour + "시 " + minutes + "분";
            if (min == 0) {
                mission_time = "오후\n" + hour + "시 ";
            }

        } else if (hour == 0) {
            mission_time = "오전\n 12시 " + minutes + "분";
            if (min == 0) {
                mission_time = "오후\n" + hour + "시 ";
            }
        } else {
            mission_time = ((hour >= 12) ? "오후\n" : "오전\n") + hour % 12 + "시 " + minutes + "분";
            if (min == 0) {
                mission_time = ((hour >= 12) ? "오후\n" : "오전\n") + hour % 12 + "시 ";
            }
        }


        if (DateUtils.isToday(dtf.dateParser(date).getTime())) {
            return "오늘 " + mission_time;

        } else if (isTomorrow(dtf.dateParser(date))) {
            return "내일 " + mission_time;

        } else {
            return month + "월 " + day + "일 " + mission_time;
        }

    }

    public static String makeDateForHuman(String date){
        DateTimeFormatter dtf = new DateTimeFormatter();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtf.dateParser(date));
        Log.d("날짜이상",date);
        int mission_month = Integer.valueOf(date.substring(4,6));
        int mission_day = Integer.valueOf(date.substring(6));

        Log.d("날짜이상",mission_month+"   "+mission_day);



        if (DateUtils.isToday(dtf.dateParser(date).getTime())) {
            return "오늘 ";

        } else if (isTomorrow(dtf.dateParser(date))) {
            return "내일 ";

        } else {
            return mission_month+"월 "+mission_day+"일";
        }




    }
    public static String makeTimeForHuman(String time){

        String minutes;
        int hour = Integer.valueOf(time.substring(0,2));
        int min = Integer.valueOf(time.substring(2));
        if (min < 10) {
            minutes = "0" + min;
        } else {

            minutes = String.valueOf(min);
        }


        if (hour == 12) {
            if (min == 0) {
                return "오후\n" + hour + "시 ";
            }else{
                return "오후\n" + hour + "시 " + minutes + "분";
            }

        }
        else if (hour == 0) {

            if (min == 0) {
                return "오후\n" + hour + "시 ";
            }else{
                return "오전\n 12시 " + minutes + "분";
            }
        } else {

            if (min == 0) {
                return ((hour >= 12) ? "오후\n" : "오전\n") + hour % 12 + "시 ";
            }else{
                return ((hour >= 12) ? "오후\n" : "오전\n") + hour % 12 + "시 " + minutes + "분";
            }
        }



    }
    public static boolean isTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }


    public static String makeDateForServer(int year, int month, int day){
        String Year;
        String Month;
        String Day;

        Year = String.valueOf(year);

        if(month<10){

            Month = "0"+month;
        }else{
            Month = String.valueOf(month);

        }
        if(day<10){
            Day = "0"+day;
        }else{
            Day = String.valueOf(day);
        }

        return Year+Month+Day;

    }

    public static String makeDateForHuman(int year, int month, int day){
        String Year;
        String Month;
        String Day;

        Year = String.valueOf(year);

        if(month<10){

            Month = "0"+month;
        }else{
            Month = String.valueOf(month);

        }
        if(day<10){
            Day = "0"+day;
        }else{
            Day = String.valueOf(day);
        }

        return Year+"-"+Month+"-"+Day;
    }

    public static String makeTimeForServer(int gethour, int getmin){

        String hour;
        String min;
        String result;

        if(gethour<10){
            hour = "0"+gethour;
        }else{
            hour = gethour+"";
        }if(getmin<10){
            min = "0"+getmin;
        }else{
            min = getmin+"";
        }

        result = hour+min;


        return result;
    }


}
