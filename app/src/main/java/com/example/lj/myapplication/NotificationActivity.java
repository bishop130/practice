package com.example.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.lj.myapplication.Adapters.AlarmService;
import com.example.lj.myapplication.Adapters.BroadCastAlarm;
import com.example.lj.myapplication.Adapters.DBHelper;
import com.example.lj.myapplication.Adapters.LocationService;
import com.example.lj.myapplication.Adapters.MainDB;
import com.example.lj.myapplication.Fragments.HomeFragment;
import com.example.lj.myapplication.Items.AlarmItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar;
    DBHelper dbHelper;
    MainDB mainDB;
    private List<AlarmItem> alarmItem = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        dbHelper = new DBHelper(NotificationActivity.this,"alarm_manager.db",null,5);
        mainDB= new MainDB(NotificationActivity.this,"main_manager.db",null,1);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Button service_start = findViewById(R.id.service_start);
        Button service_stop = findViewById(R.id.service_stop);
        Button service_check = findViewById(R.id.service_check);
        Button service_alarm = findViewById(R.id.service_alarm);
        Button service_alarm_stop = findViewById(R.id.service_alarm_stop);
        Button check_db = findViewById(R.id.check_db);
        Button check_db2 = findViewById(R.id.check_db2);
        Button broadcast_notification = findViewById(R.id.broadcast_notification);

        service_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent service = new Intent(NotificationActivity.this, LocationService.class);
                service.putExtra("service_flag",false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(NotificationActivity.this,service);
                    }else{
                        startService(service);
                    }
                    //am.cancel(pendingIntent);
                    //pendingIntent.cancel();

                    //boolean isWorking = (PendingIntent.getBroadcast(this, 13, intet, PendingIntent.FLAG_NO_CREATE) != null);
                   // Log.d("열열", date_time.format(cur_time)+"alarm is " + (isWorking ? "" : "not") + " working...");
            }
        });
        service_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(NotificationActivity.this, LocationService.class);
                stopService(serviceIntent);
                Intent AlarmIntent = new Intent(NotificationActivity.this, AlarmService.class);
                stopService(AlarmIntent);
                Log.d("서비스", "Service Stop");
            }
        });
        service_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("서비스", "서비스 체크"+isServiceRunning());
            }
        });

        service_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(NotificationActivity.this, AlarmService.class);
                startService(serviceIntent);
            }
        });
        service_alarm_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("서비스", "서비스 체크"+isAlarmServiceRunning());
            }
        });
        check_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String drop = "DROP TABLE alarm_table";
                 //dbHelper.drop(drop);
                //String sql = "select * from alarm_table";
                String sql = "DELETE FROM alarm_table";
                dbHelper.selectData(sql);

            }
        });
        check_db2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query2 = "SELECT * FROM main_table";
                String query = "SELECT * FROM alarm_table" ;
               alarmItem = mainDB.setNextAlarm(query2);
               for(int i = 0; i<alarmItem.size(); i++){
                   String time = alarmItem.get(i).getTime();
                   String lat = alarmItem.get(i).getLat();
                   String lng = alarmItem.get(i).getLng();
                   String mission_id = alarmItem.get(i).getMission_id();
                   String date = alarmItem.get(i).getDate();
                   String date_time = alarmItem.get(i).getDate_time();
                   String is_success = alarmItem.get(i).getIs_success();
                   String title = alarmItem.get(i).getTitle();
                   String user_id = alarmItem.get(i).getUser_id();
                   Log.d("디바all", "   lat:" + String.valueOf(lat) + "   " +
                           "lng:" + String.valueOf(lng) + "     time:" + time + "     mission_id:" + mission_id + "   " +
                           "  date:" + date + "     date+time:" + date_time + "      is_success:" + is_success + "      title:" + title + "       user_id:" + user_id);

               }





                }
        });

        broadcast_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(NotificationActivity.this, BroadCastAlarm.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O)
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (new Date()).getTime()+5000, pendingIntent);
            }
        });

    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (LocationService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }
    public boolean isAlarmServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (AlarmService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

}
