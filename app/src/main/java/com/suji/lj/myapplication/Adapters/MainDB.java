package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.suji.lj.myapplication.Items.AlarmItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class MainDB  extends SQLiteOpenHelper {
    Context context;
    private List<AlarmItem> alarmItem = new ArrayList<>();

    public MainDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE main_table ( ");
        sb.append(" time TEXT, ");
        sb.append(" lat TEXT, ");
        sb.append(" lng TEXT, ");
        sb.append(" mission_id TEXT, ");
        sb.append(" date TEXT, ");
        sb.append("date_time DATETIME,");
        sb.append("is_success TEXT,");
        sb.append("title TEXT,");
        sb.append("user_id TEXT)");

        db.execSQL(sb.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            StringBuffer sb = new StringBuffer();
            sb.append(" CREATE TABLE main_table ( ");
            sb.append(" time TEXT, ");
            sb.append(" lat TEXT, ");
            sb.append(" lng TEXT, ");
            sb.append(" mission_id TEXT, ");
            sb.append(" date TEXT, ");
            sb.append("date_time DATETIME,");
            sb.append("is_success TEXT,");
            sb.append("title TEXT,");
            sb.append("user_id TEXT)");

        }
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }
    public void select(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void selectData(String query){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery(query, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        while (result.moveToNext()){
            int id = result.getInt(0);
            String time = result.getString(1);
            String lat = result.getString(2);
            String lng = result.getString(3);
            String mission_id = result.getString(4);
            String date = result.getString(5);
            String date_time = result.getString(6);
            String is_success = result.getString(7);
            String title = result.getString(8);
            String user_id = result.getString(9);

            Log.d("디바","id:"+id+"   lat:"+String.valueOf(lat)+"   " +
                    "lng:"+String.valueOf(lng)+"     time:"+time+"     mission_id:"+mission_id+"   " +
                    "  date:"+date+"     date+time:"+date_time+"      is_success:"+is_success+"      title:"+title+"       user_id:"+user_id);

        }
        result.close();
        db.close();
    }
    public List<AlarmItem> setNextAlarm(String query) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery(query, null);
        //db.close();



        // result(Cursor 객체)가 비어 있으면 false 리턴
        for(result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            AlarmItem alarm = new AlarmItem();

            alarm.setTime(result.getString(0));
            alarm.setLat(result.getString(1));
            alarm.setLng(result.getString(2));

            alarm.setMission_id(result.getString(3));

            alarm.setDate(result.getString(4));

            alarm.setDate_time(result.getString(5));

            alarm.setIs_success(result.getString(6));

            alarm.setTitle(result.getString(7));
            alarm.setUser_id(result.getString(8));



            alarmItem.add(alarm);
        }
        result.close();
        db.close();
        return alarmItem;


    }


}
