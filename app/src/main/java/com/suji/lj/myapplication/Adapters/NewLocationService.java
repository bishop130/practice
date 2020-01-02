package com.suji.lj.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForServer;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.LocationUtils;
import com.suji.lj.myapplication.Utils.PreciseCountdown;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.Date;

import io.realm.Realm;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class NewLocationService extends Service {


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
            Log.d("서비", "위치");

        }
    };


    int notif_id = 1;
    PreciseCountdown preciseCountdown;
    DateTimeFormatter dtf = new DateTimeFormatter();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("서비스", "onCreate");

        //Realm realm = Realm.getDefaultInstance();


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            foregroundNotification();
            queryData();


            SharedPreferences pref = getSharedPreferences("Location", MODE_PRIVATE);
            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
            String date = pref.getString("date", "");
            String time = pref.getString("time", "");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
/*
            ref.child("check_for_server").child(date + time).orderByChild("user_id").equalTo(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d("서비스", dataSnapshot.toString() + "  for_server");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String key = child.getKey();
                        ItemForServer object = child.getValue(ItemForServer.class);
                        object.setIs_success(true);
                        ref.child("check_for_server").child(date + time).child(key).setValue(object);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

 */
            ref.child("user_data").child(user_id).child("mission_display").child(date + time).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d("서비스", dataSnapshot.toString() + "  display");

                    ItemForMissionByDay item = dataSnapshot.getValue(ItemForMissionByDay.class);
                    item.setSuccess(true);
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Log.d("서비스", data.toString() + "  display");
                        //ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                        //itemForMissionByDay.setSuccess(true);
                        //ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay(data.getValue(ItemForMissionByDay.class));

                        /*

                        ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();
                        itemForMissionByDay.setTitle(data.child("title").getValue(String.class));
                        itemForMissionByDay.setDate(data.child("date").getValue(String.class));
                        itemForMissionByDay.setTime(data.child("time").getValue(String.class));
                        itemForMissionByDay.setAddress(data.child("address").getValue(String.class));
                        itemForMissionByDay.setSuccess(true);
                        itemForMissionByDay.setLat(data.child("lat").getValue(Double.class));
                        itemForMissionByDay.setLng(data.child("lng").getValue(Double.class));

                         */
                       //
                    }
                    ref.child("user_data").child(user_id).child("mission_display").child(date + time).setValue(item);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //LocationUtils.startLocationUpdates(this, locationCallback);


        } else {
            stopForeground(true);
            stopSelf();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("서비스", "onStartCommand");//notify 업데이트할 경우 처음 호출
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Utils.isServiceRunningInForeground(this, NewLocationService.class)) {
                //Utils.refreshDatabase(this,getSharedPreferences("Kakao",MODE_PRIVATE).getString("token",""));
                // makeContentForNotification();
                //startLocationUpdates();
                Log.d("서비스", startId + "  start_id");
                Log.d("서비스", flags + "  flags");

                //updateNotification();
            } else {


            }
        } else {
            //instantAlarm("위치정보에 대한 접근이 거부되었습니다.", "위치정보에 대한 권한을 허가해주세요.");

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }


    private void queryData() {
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        SharedPreferences sharedPreferences = getSharedPreferences("Location", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date").startAt(DateTimeUtils.getCurrentTime()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                    double lat = itemForMissionByDay.getLat();
                    double lng = itemForMissionByDay.getLat();
                    String title = itemForMissionByDay.getTitle();
                    String date = itemForMissionByDay.getDate();
                    String time = itemForMissionByDay.getTime();
                    editor.putString("date", date);
                    editor.putString("time", time);
                    editor.putLong("Lat", Double.doubleToRawLongBits(lat));
                    editor.putLong("Lng", Double.doubleToRawLongBits(lng));
                    editor.apply();


                    //foregroundNotification();
                    Date current_date_time = new Date(System.currentTimeMillis());
                    Date mission_date_time = dtf.dateTimeParser(date + time);
                    long diff = mission_date_time.getTime() - current_date_time.getTime();

                    if (true) {
                        LocationUtils.startLocationUpdates(getApplicationContext(), locationCallback);
                    } else {
                        updateNotification("다음 목표 : " + title + " - " + DateTimeUtils.makeDateForHuman(date) + " " + DateTimeUtils.makeTimeForHuman(time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
                        timerAlarm(diff);
                    }


                    if (dataSnapshot.hasChildren()) {
                        Log.d("정렬", itemForMissionByDay.getDate() + " " + itemForMissionByDay.getTime());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void foregroundNotification() {

        startForeground(notif_id, getMyActivityNotification("처음", "처음"));
    }


    private Notification getMyActivityNotification(String title, String text) {
        // The PendingIntent to launch our activity if the user selects
        // this notification

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snow_deer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SnowDeer Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        return builder.setSmallIcon(R.drawable.carrot_and_stick)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent).build();
    }

    private void updateNotification(String title, String text) {
        Log.d("서비스", "updateNotification");
        Notification notification = getMyActivityNotification(title, text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notif_id, notification);
    }

    private void timerAlarm(long diff) {

        try {
            preciseCountdown.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        preciseCountdown = null;

        //updateNotification("다음 목표 : " + title + " - " + DateTimeUtils.makeDateTimeForHuman(date, time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
        //foregroundNotification("다음 목표 : " + title + " - " + Utils.monthDayTime(date, time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
        //countDownTimer.cancel();

        preciseCountdown = new PreciseCountdown(5000, 1000) {//30분전에 알람 diff - 1000 * 60 * 3
            @Override
            public void onTick(long timeLeft) {
                Log.d("서비스", "" + timeLeft);
            }

            @Override
            public void onFinished() {
                Log.d("서비스", "" + "onfinished");
                Utils.wakeDoze(getApplicationContext());

            }
        };
        preciseCountdown.start();
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        SharedPreferences preferences = getSharedPreferences("Location", MODE_PRIVATE);
        double lat = Double.longBitsToDouble(preferences.getLong("Lat", 0));
        double lng = Double.longBitsToDouble(preferences.getLong("Lng", 0));
        if ((location.getLatitude() - lat) * (location.getLatitude() - lat) + (location.getLongitude() - lng) * (location.getLongitude() - lng) < 0.0007 * 0.0007) {


            SharedPreferences pref = getSharedPreferences("Location", MODE_PRIVATE);
            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
            String date = pref.getString("date", "");
            String time = pref.getString("time", "");


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        //cancelAlarm();
        try {
            preciseCountdown.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        preciseCountdown = null;


        stopLocationUpdates();
        Log.d("서비스", "onDestroy");

    }

    private void stopLocationUpdates() {
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        Log.d("서비스4", "stopUpdate");
    }


}
