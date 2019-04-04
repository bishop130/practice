package com.example.lj.myapplication.Adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {


    public  DateTimeFormatter(){


    }
    public Date timeParser(String time){
        SimpleDateFormat date_sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        Date result = new Date();
        try {
            result = date_sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;

    }

    public Date dateParser(String date) {
        SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date result = new Date();
        try {
            result = date_sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Date dateTimeParser(String date_time) {
        SimpleDateFormat date_time_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        Date result = new Date();
        try {
            result = date_time_sdf.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
