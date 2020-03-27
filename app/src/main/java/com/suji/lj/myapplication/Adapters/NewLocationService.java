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
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
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
            Log.d("서비스", "위치" + locationResult.getLastLocation().getLatitude() + "   " + locationResult.getLastLocation().getLongitude());

        }
    };
    int notif_id = 1;
    PreciseCountdown preciseCountdown;
    boolean isTimerRunning = false;
    FusedLocationProviderClient mFusedLocationClient;


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
        if (!Utils.isServiceRunningInForeground(this, NewLocationService.class)) {
            startForeground(notif_id, getMyActivityNotification("자동위치등록을 실행합니다", "처음"));
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("서비스", "onStartCommand");//notify 업데이트할 경우 처음 호출
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Utils.refreshDatabase(this,getSharedPreferences("Kakao",MODE_PRIVATE).getString("token",""));
            // makeContentForNotification();
            //startLocationUpdates();
            Log.d("서비스", "퍼미션 체크");


            String date_time = DateTimeUtils.getCurrentTime();
            queryData(date_time);


        } else {
            Log.d("서비스", "퍼미션 체크요망");
            //instantAlarm("위치정보에 대한 접근이 거부되었습니다.", "위치정보에 대한 권한을 허가해주세요.");

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }


    private void queryData(String date_time) {
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        SharedPreferences sharedPreferences = getSharedPreferences("Location", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (!Utils.isEmpty(user_id)) {
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().startAt(date_time).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            //Log.d("서비스",data.getChildrenCount()+"count");
                            //Log.d("서비스",dataSnapshot.getChildrenCount()+"datacount");

                            ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);

                            double lat = itemForMissionByDay.getLat();
                            double lng = itemForMissionByDay.getLng();
                            String title = itemForMissionByDay.getTitle();
                            String date = itemForMissionByDay.getDate();
                            String time = itemForMissionByDay.getTime();
                            boolean is_success = itemForMissionByDay.isSuccess();
                            Log.d("서비스", title);

                            if (is_success) {
                                Log.d("서비스", "성공");
                                String sample = date + (Integer.valueOf(time) + 1);
                                Log.d("서비스", "샘플" + sample);
                                queryData(sample);//현재 미션을 완료했다면 다음 미션으로 쿼

                                break;

                            } else {
                                editor.putString("date", date);
                                editor.putString("time", time);
                                editor.putLong("Lat", Double.doubleToRawLongBits(lat));
                                editor.putLong("Lng", Double.doubleToRawLongBits(lng));
                                editor.apply();

                                // Log.d("서비스", "가장가까운 날짜" + date);
                                //foregroundNotification();
                                Date current_date_time = new Date(System.currentTimeMillis());
                                Date mission_date_time = DateTimeFormatter.dateTimeParser(date + time);
                                long diff = mission_date_time.getTime() - current_date_time.getTime();
                                //Log.d("서비스", "diff  " + diff);
                                //Log.d("서비스", "title  " + title);
                                //Log.d("서비스", "currenttime  " + current_date_time.getTime());
                                //Log.d("서비스", "missiontime  " + mission_date_time.getTime());
//1,800,000  ,  3,173,130

                                if (diff - 1000 * 60 * 30 < 0) {

                                    //1,800,000
                                    Log.d("서비스", "30분안에 들어옴" + date);
                                    LocationUtils.startLocationUpdates(getApplicationContext(), locationCallback);
                                    updateNotification("자동위치등록이 실행중입니다.", "목표 : " + title + " - " + DateTimeUtils.makeDateForHumanNoYear(date) + " " + DateTimeUtils.makeTimeForHuman(time));
                                    break;

                                } else {
                                    //1,837,158
                                    Log.d("서비스", "30분밖에 나감" + date);

                                    updateNotification("다음 목표 : " + title + " - " + DateTimeUtils.makeDateForHumanNoYear(date) + " " + DateTimeUtils.makeTimeForHuman(time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
                                    timerAlarm(diff);
                                    break;


                                }


                            }

                        }
                    } else {
                        updateNotification("다음 약속이 없습니다.", "약속을 등록해주세요");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }


    private Notification getMyActivityNotification(String title, String text) {
        // The PendingIntent to launch our activity if the user selects
        // this notification

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "이불안은위험해";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "이불안은위험해", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            builder = new Notification.Builder(this, CHANNEL_ID);
            return builder.setSmallIcon(R.drawable.carrot_and_stick)
                    .setContentTitle(text)
                    .setContentText(title)
                    .setContentIntent(contentIntent).build();
        } else {
            builder = new Notification.Builder(this);
            return builder.setSmallIcon(R.drawable.carrot_and_stick)
                    .setContentTitle(text)
                    .setContentText(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent).build();
        }
    }

    private void updateNotification(String title, String text) {
        Log.d("서비스", "updateNotification");
        Notification notification = getMyActivityNotification(title, text);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notif_id, notification);
    }

    private void timerAlarm(long diff) {

        if (isTimerRunning) {
            preciseCountdown.cancel();
        } else {

            preciseCountdown = new PreciseCountdown(diff - 1000 * 60 * 30, 1000) {//30분전에 알람 diff - 1000 * 60 * 3
                @Override
                public void onTick(long timeLeft) {
                    //Log.d("서비스", "" + timeLeft);
                    isTimerRunning = true;
                }

                @Override
                public void onFinished() {
                    isTimerRunning = false;
                    Log.d("서비스", "" + "onfinished");
                    Utils.wakeDoze(getApplicationContext());

                }
            };
            preciseCountdown.start();
        }
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        SharedPreferences preferences = getSharedPreferences("Location", MODE_PRIVATE);
        double lat = Double.longBitsToDouble(preferences.getLong("Lat", 0));
        double lng = Double.longBitsToDouble(preferences.getLong("Lng", 0));
        //Log.d("데이트", " 위도경도" + lat + "    " + lng);
        if ((location.getLatitude() - lat) * (location.getLatitude() - lat) + (location.getLongitude() - lng) * (location.getLongitude() - lng) < 0.0007 * 0.0007) {

            Log.d("서비스", "  gps 위치확인");
            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
            String date = preferences.getString("date", "");
            String time = preferences.getString("time", "");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            ref.child("check_for_server").child(date + time).orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    String time_stamp = DateTimeUtils.getCurrentTime();
                    Log.d("서비스", dataSnapshot.getChildrenCount() + "  how_many");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String key = child.getKey();
                        ItemForServer object = child.getValue(ItemForServer.class);
                        if (!Utils.isEmpty(object)) {
                            object.setIs_success(true);
                            object.setTime_stamp(time_stamp);
                            String mission_id = object.getChildren_id();
                            Log.d("서비스","몇번이야");

                            if (!Utils.isEmpty(key))
                                ref.child("check_for_server").child(date + time).child(key).setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!Utils.isEmpty(user_id))
                                            ref.child("user_data").child(user_id).child("mission_display").child(date + time).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    ItemForMissionByDay item = dataSnapshot.getValue(ItemForMissionByDay.class);
                                                    if (item != null) {
                                                        item.setSuccess(true);
                                                        item.getMother_id();
                                                    }

                                                    ref.child("user_data").child(user_id).child("mission_display").child(date + time).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            getSharedPreferences("Location", MODE_PRIVATE).edit().remove("date").remove("time").apply();
                                                            Log.d("서비스", "위치등록성공");
                                                            ref.child("user_data").child(user_id).child("mission_info_list").child(mission_id).child("mission_dates").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    ItemForDateTimeByList item = dataSnapshot.getValue(ItemForDateTimeByList.class);
                                                                    Log.d("서비스", dataSnapshot.exists() + "");
                                                                    if (!Utils.isEmpty(item)) {
                                                                        item.setSuccess(true);
                                                                        item.setTime_stamp(time_stamp);
                                                                    }
                                                                    ref.child("user_data").child(user_id).child("mission_info_list").child(mission_id).child("mission_dates").child(date).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Log.d("서비스", "성공인가 = " + item.getDate());
                                                                            stopLocationUpdates();
                                                                            queryData(date + time);
                                                                        }
                                                                    });


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        //Log.d("서비스", mission_id + " , " + date);


                                    }
                                });

                            //Log.d("서비스", dataSnapshot.toString() + "  for_display");


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isTimerRunning) {
            preciseCountdown.cancel();
        }
        stopLocationUpdates();
        Log.d("서비스", "onDestroy");

    }

    private void stopLocationUpdates() {
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);


        Log.d("서비스4", "stopUpdate");
    }


}
