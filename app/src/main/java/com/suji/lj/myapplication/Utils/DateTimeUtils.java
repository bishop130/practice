package com.suji.lj.myapplication.Utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {


    public static String makeDateTimeForHuman(String date, String time) {

        String mission_time;

        String minutes;

        String month = "";
        String day = "";


        int hour = Integer.valueOf(time.substring(0, 1));
        int min = Integer.valueOf(time.substring(2, 3));
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


        if (DateUtils.isToday(DateTimeFormatter.dateParser(date, "yyyyMMdd").getTime())) {
            return "오늘 " + mission_time;

        } else if (isTomorrow(DateTimeFormatter.dateParser(date, "yyyyMMdd"))) {
            return "내일 " + mission_time;

        } else {
            return month + "월 " + day + "일 " + mission_time;
        }

    }

    public static String makeDateForHumanIfToday(String date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeFormatter.dateParser(date, "yyyyMMdd"));
        Log.d("날짜이상", date);
        int mission_year = Integer.valueOf(date.substring(2, 4));
        int mission_month = Integer.valueOf(date.substring(4, 6));
        int mission_day = Integer.valueOf(date.substring(6));

        Log.d("날짜이상", mission_month + "   " + mission_day);


        if (DateUtils.isToday(DateTimeFormatter.dateParser(date, "yyyyMMdd").getTime())) {
            return "오늘 ";

        } else if (isTomorrow(DateTimeFormatter.dateParser(date, "yyyyMMdd"))) {
            return "내일 ";

        } else {
            return mission_year + "년 " + mission_month + "월 " + mission_day + "일 (" + makeDayOfWeek(date) + ")";
        }


    }

    public static String makeDateForHuman(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeFormatter.dateParser(date, "yyyyMMdd"));

        int mission_month = cal.get(Calendar.MONTH) + 1;
        int mission_day = cal.get(Calendar.DAY_OF_MONTH);

        Log.d("날짜이상", mission_month + "   " + mission_day);


        return mission_month + "월 " + mission_day + "일 (" + makeDayOfWeek(date) + ")";


    }

    public static String makeDateForHumanNoYear(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeFormatter.dateParser(date, "yyyyMMdd"));
        Log.d("날짜이상", date);
        int mission_year = Integer.valueOf(date.substring(2, 4));
        int mission_month = Integer.valueOf(date.substring(4, 6));
        int mission_day = Integer.valueOf(date.substring(6));

        Log.d("날짜이상", mission_month + "   " + mission_day);


        return mission_month + "월 " + mission_day + "일 (" + makeDayOfWeek(date) + ")";


    }

    public static String makeTimeForHuman(String date_time, String pattern) {

        String minutes;
        Date date = DateTimeFormatter.dateParser(date_time, pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        if (min < 10) {
            minutes = "0" + min;
        } else {

            minutes = String.valueOf(min);
        }

        Log.d("오전오후", hour + "  hour");
        Log.d("오전오후", min + "  min");


        if (hour == 12) {
            if (min == 0) {
                return "오후 " + hour + "시 ";
            } else {
                return "오후 " + hour + "시 " + minutes + "분";
            }

        } else if (hour == 0) {

            if (min == 0) {
                return "오전 12시 ";
            } else {
                return "오전 12시 " + minutes + "분";
            }
        } else {

            if (min == 0) {
                return ((hour >= 12) ? "오후 " : "오전 ") + hour % 12 + "시 ";
            } else {
                return ((hour >= 12) ? "오후 " : "오전 ") + hour % 12 + "시 " + minutes + "분";
            }
        }


    }

    public static String makeTimeForHumanInt(int hour, int min) {
        if (hour == 12) {
            if (min == 0) {
                return "오후 " + hour + "시 ";
            } else {
                return "오후 " + hour + "시 " + min + "분";
            }

        } else if (hour == 0) {

            if (min == 0) {
                return "오전 12시 ";
            } else {
                return "오전 12시 " + min + "분";
            }
        } else {

            if (min == 0) {
                return ((hour >= 12) ? "오후 " : "오전 ") + hour % 12 + "시 ";
            } else {
                return ((hour >= 12) ? "오후 " : "오전 ") + hour % 12 + "시 " + min + "분";
            }
        }


    }

    public static boolean isTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }

    public static String makeDateSimple(String date) {
        String year = date.substring(2, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);

        return year + "." + month + "." + day + " (" + makeDayOfWeek(date) + ")";

    }

    public static String makeTimeSimple(String time) {
        String hour = time.substring(0, 2);
        String min = time.substring(2);

        return hour + ":" + min;


    }


    public static String makeDateForServer(int year, int month, int day) {
        String Year;
        String Month;
        String Day;

        Year = String.valueOf(year);

        if (month < 10) {

            Month = "0" + month;
        } else {
            Month = String.valueOf(month);

        }
        if (day < 10) {
            Day = "0" + day;
        } else {
            Day = String.valueOf(day);
        }

        return Year + Month + Day;

    }

    public static String makeDateForHuman(int year, int month, int day) {
        String Year;
        String Month;
        String Day;

        Year = String.valueOf(year);

        if (month < 10) {

            Month = "0" + month;
        } else {
            Month = String.valueOf(month);

        }
        if (day < 10) {
            Day = "0" + day;
        } else {
            Day = String.valueOf(day);
        }

        return Year + "-" + Month + "-" + Day;
    }

    public static String makeTimeForServer(int gethour, int getmin) {

        String hour;
        String min;
        String result;

        if (gethour < 10) {
            hour = "0" + gethour;
        } else {
            hour = gethour + "";
        }
        if (getmin < 10) {
            min = "0" + getmin;
        } else {
            min = getmin + "";
        }

        result = hour + min;


        return result;
    }

    public static String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);


        return simpleDate.format(date);
    }

    public static String getCurrentTimePattern(String pattern){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat(pattern, Locale.KOREA);


        return simpleDate.format(date);


    }

    public static String getCurrentDateTimeForKey() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);


        return simpleDate.format(date);
    }

    public static String getCurrentHourMin() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("HHmm", Locale.KOREA);
        String getTime = simpleDate.format(date);


        return getTime;
    }

    public static String getToday() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);


        return simpleDate.format(date);

    }

    public static boolean compareIsFuture(String input_date_time, String pattern) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
        Date date = dateTimeFormatter.dateTimeParser(input_date_time, pattern);
        long input = date.getTime();
        long now = System.currentTimeMillis();
        if (input > now) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean compareIsFuture30min(String input_date_time,String pattern) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
        Date date = dateTimeFormatter.dateTimeParser(input_date_time,pattern);
        long input = date.getTime();
        long now = System.currentTimeMillis();
        if ((input - now) >= 1000 * 60 * 30) {
            return true;
        } else {
            return false;
        }
    }

    public static String makeDayOfWeek(String input_date) {

        Date date = DateTimeFormatter.dateParser(input_date, "yyyyMMdd");


        Calendar calendar = Calendar.getInstance();


        calendar.setTime(date);
        String day_of_week = "?";

        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case 1:
                day_of_week = "일";
                break;
            case 2:
                day_of_week = "월";
                break;
            case 3:
                day_of_week = "화";
                break;
            case 4:
                day_of_week = "수";
                break;
            case 5:
                day_of_week = "목";
                break;
            case 6:
                day_of_week = "금";
                break;
            case 7:
                day_of_week = "토";
                break;
        }

        return day_of_week;
    }


}
