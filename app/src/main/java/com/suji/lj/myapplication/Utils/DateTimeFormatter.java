package com.suji.lj.myapplication.Utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {


    public DateTimeFormatter() {


    }

    public static Date timeFormatter(Date date) {
        SimpleDateFormat date_sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        String d1 = date_sdf.format(date);
        Date result = new Date();
        try {
            result = date_sdf.parse(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;


    }

    public static Date dateFormatter(Date date) {
        SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String d1 = date_sdf.format(date);
        Date result = new Date();
        try {
            result = date_sdf.parse(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;


    }

    public static Date timeParser(String time) {
        SimpleDateFormat date_sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        Date result = new Date();
        try {
            result = date_sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static Date dateParser(String date, String pattern) {
        SimpleDateFormat date_sdf = new SimpleDateFormat(pattern, Locale.KOREA);
        Date result = new Date();
        try {
            result = date_sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date dateTimeParser(String date_time, String pattern) {
        SimpleDateFormat date_time_sdf = new SimpleDateFormat(pattern, Locale.KOREA);
        Date result = new Date();
        try {
            result = date_time_sdf.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String dateReadable(Date date) {
        String day = (String) DateFormat.format("d", date); // 20
        String monthNumber = (String) DateFormat.format("M", date); // 6
        String year = (String) DateFormat.format("yyyy", date); // 2013
        return year + "년 " + monthNumber + "월 " + day + "일";
    }
}
