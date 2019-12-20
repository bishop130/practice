package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.AlarmService;
import com.suji.lj.myapplication.Adapters.BroadCastAlarm;
import com.suji.lj.myapplication.Adapters.DBHelper;
import com.suji.lj.myapplication.Adapters.LocationService;
import com.suji.lj.myapplication.Adapters.MainDB;
import com.suji.lj.myapplication.Items.AlarmItem;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar;
    DBHelper dbHelper;
    MainDB mainDB;
    private List<AlarmItem> alarmItem = new ArrayList<>();
    private String goURL = "http://bishop130.cafe24.com/transfer_test.php";
    StringRequest request;
    RequestQueue requestQueue;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //DatabaseReference conditionRef = mRootRef.child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Log.d("송금전",getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me",""));
        Button button = findViewById(R.id.firebase_test);

        Map<String, User> users = new HashMap<>();
        users.put("alanisawesome3", new User("June 23, 1912", "Alan Turing"));
        users.put("gracehop3", new User("December 9, 1906", "Grace Hopper"));



        volleyConnect();

        mRootRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //String text = dataSnapshot.getValue(String.class);
               // Log.d("실시간",text);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HashMap<String, Object> result = new HashMap<>();
        result.put("date_of_birth", "COMPLETED");

        button.setOnClickListener(new View.OnClickListener() {

            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token","");
            @Override
            public void onClick(View v) {
               //conditionRef.push().setValue(users);
                mRootRef.child("users").child(user_id).setValue(new User("123","345"));
                mRootRef.child("users_info").child(user_id).updateChildren(result);
            }
        });




/*
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
                String query = "SELECT * FROM main_table";
                String query2 = "SELECT * FROM alarm_table" ;
               alarmItem = dbHelper.setNextAlarm(query2);
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
        */

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


    private void volleyConnect() {
        request = new StringRequest(Request.Method.POST, goURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("송금",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_me",getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me",""));
                hashMap.put("transfer_amount",getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("transfer_amount",""));
                hashMap.put("bank_tran_id","T991596920U"+ Utils.getCurrentTimeForBankNum());

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public static class User {

        public String date_of_birth;
        public String full_name;
        public String nickname;

        public User(String dateOfBirth, String fullName) {
            date_of_birth=dateOfBirth;
            full_name=fullName;
        }

        public User(String dateOfBirth, String fullName, String nickname) {
            // ...
        }

    }
}
