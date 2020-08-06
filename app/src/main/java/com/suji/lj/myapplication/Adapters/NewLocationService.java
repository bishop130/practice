package com.suji.lj.myapplication.Adapters;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForNotification;
import com.suji.lj.myapplication.Items.ItemForMissionCheck;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FCM;
import com.suji.lj.myapplication.Utils.LocationUtils;
import com.suji.lj.myapplication.Utils.PreciseCountdown;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    PreciseCountdown timeOutAlarm;
    boolean isWakeUpTimerRunning = false;
    boolean isTimeOutTimerRunning = false;
    FusedLocationProviderClient mFusedLocationClient;
    String mission_id;
    String user_id;
    String title;

    double lat, lng;
    int count = 0;
    int key_count = 0;

    String date;
    String time;
    boolean isSingle;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<ItemForMissionByDay> singleList;
    List<ItemForFriendByDay> friendByDayList = new ArrayList<>();


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

        startForeground(1, getMyActivityNotification("다음약속이 없습니다.", "새 약속을 등록해주세요."));


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("서비스", "onStartCommand");//notify 업데이트할 경우 처음 호출
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Utils.refreshDatabase(this,getSharedPreferences("Kakao",MODE_PRIVATE).getString("token",""));
            // makeContentForNotification();
            //startLocationUpdates();
            Log.d("서비스", "퍼미션 체크");


            user_id = Account.getUserId(this);

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

        singleList = new ArrayList<>();


        databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date_time").startAt(date_time).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                        if (itemForMissionByDay != null) {
                            locationOrNot(itemForMissionByDay);

                        }

                    }
                } else {

                    updateNotification("다음약속이 없습니다.", "새 약속을 등록해주세요.");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void locationOrNot(ItemForMissionByDay item) {

        lat = item.getLat();
        lng = item.getLng();
        title = item.getTitle();
        date = item.getDate();
        mission_id = item.getMission_id();
        time = item.getTime();
        isSingle = item.isSingle_mode();
        if (!isSingle) {
            friendByDayList = item.getFriendByDayList();
        }


        Date current_date_time = new Date(System.currentTimeMillis());
        Date mission_date_time = DateTimeFormatter.dateTimeParser(date + time);
        long diff = mission_date_time.getTime() - current_date_time.getTime();


        /** 미션시작 30분 전 이내로 들어올경우 gps on**/
        if (diff - 1000 * 60 * 30 < 0) {

            //1,800,000
            Log.d("서비스", "30분안에 들어옴" + date);
            LocationUtils.startLocationUpdates(getApplicationContext(), locationCallback);
            updateNotification("자동위치등록이 실행중입니다.", "목표 : " + title + " - " + DateTimeUtils.makeDateForHumanNoYear(date) + " " + DateTimeUtils.makeTimeForHuman(time, "HHmm"));
            timeOutAlarm();


        } else {
            //1,837,158
            Log.d("서비스", "30분밖에 나감" + date);

            updateNotification("다음 목표 : " + title + " - " + DateTimeUtils.makeDateForHumanNoYear(date) + " " + DateTimeUtils.makeTimeForHuman(time, "HHmm"), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
            wakeUpAlarm(diff);


        }


    }

    private Notification getMyActivityNotification(String title, String text) {
        // The PendingIntent to launch our activity if the user selects
        // this notification

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "WindWalk";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "WindWalk", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);

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

    private void wakeUpAlarm(long diff) {

        if (isWakeUpTimerRunning) {
            preciseCountdown.cancel();
        } else {

            preciseCountdown = new PreciseCountdown(diff - 1000 * 60 * 30, 1000) {//30분전에 알람 diff - 1000 * 60 * 3
                @Override
                public void onTick(long timeLeft) {
                    //Log.d("서비스", "" + timeLeft);
                    isWakeUpTimerRunning = true;
                }

                @Override
                public void onFinished() {
                    isWakeUpTimerRunning = false;
                    Log.d("서비스", "" + "onfinished");
                    Utils.wakeDoze(getApplicationContext());

                }
            };
            preciseCountdown.start();
        }
    }


    public void onLocationChanged(Location location) {
        String time_stamp = DateTimeUtils.getCurrentTime();
        String my_name = Account.getUserName(this);
        String my_image = Account.getUserThumbnail(this);
        if (isTimeOutTimerRunning) {

            if ((location.getLatitude() - lat) * (location.getLatitude() - lat) + (location.getLongitude() - lng) * (location.getLongitude() - lng) < 0.0007 * 0.0007) {


                /** 위치업데이트 종료**/
                stopLocationUpdates();
                Log.d("서비스", lat + "  " + lng);

                Log.d("서비스", "  gps 위치확인");


                /** 서버에 성공여부 기록**/
                databaseReference.child("check_mission").child(date + time).orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists()) {
                            Log.d("서비스", dataSnapshot.getChildrenCount() + "  how_many");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ItemForMissionCheck object = snapshot.getValue(ItemForMissionCheck.class);
                                String userId = snapshot.child("user_id").getValue(String.class);
                                if (userId != null && object != null) {
                                    if (userId.equals(user_id)) {
                                        object.setSuccess(true);
                                        object.setTime_stamp(time_stamp);
                                        snapshot.getRef().setValue(object);


                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });

                if (isSingle) {

                    /** 싱글미션은 개인db에 기록**/

                    databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String key = snapshot.getKey();

                                ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);

                                if (item != null && key != null) {
                                    String mission_id2 = item.getMission_id();
                                    item.setSuccess(true);
                                    if (mission_id.equals(mission_id2)) { //날짜와 시간 미션id가 모두 일치한다

                                        //databaseReference.child("user_data").child(user_id).child("notification").push().setValue(item);
                                        /** 성공한 약속은 display DB에서 삭제**/
                                        databaseReference.child("user_data").child(user_id).child("mission_display").child(key).removeValue();


                                        /** 성공여부를 mission_info_list에 기록**/
                                        databaseReference.child("user_data").child(user_id).child("mission_info_list").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    String key = snapshot.getKey();
                                                    ItemForDateTimeByList item = snapshot.child("mission_dates").getValue(ItemForDateTimeByList.class);
                                                    Log.d("서비스", dataSnapshot.exists() + "");
                                                    if (item != null) {
                                                        item.setSuccess(true);
                                                        item.setTime_stamp(time_stamp);
                                                        item.setDate_time(item.getDate_time());

                                                        databaseReference.child("user_data").child(user_id).child("mission_info_list").child(key).child("mission_dates").child(date).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //Log.d("서비스", "성공인가 = " + item.getDate());


                                                            }
                                                        });
                                                    }
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        /** 미션 성공여부 알림보관함에 기록(싱글성공 코드400)**/
                                        ItemForNotification notification = new ItemForNotification();
                                        notification.setMission_id(mission_id);
                                        notification.setTitle(title);
                                        notification.setFriend_name(my_name);
                                        notification.setSingle(true);
                                        notification.setDate_time(time_stamp);
                                        notification.setNotification_code(100);


                                        databaseReference.child("user_data").child(user_id).child("notification").push().setValue(notification);
                                        FCM.fcmPushAlarm(user_id, title, "도착 성공!");

                                    }
                                }


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {


                    /** 공유데이터 mission_display에 내 도착시간 기록**/

                    databaseReference.child("multi_data").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("mission_display").orderByChild("date_time").equalTo(date + time).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot shot : snapshot.getChildren()) {

                                                    Log.d("상태", "123");

                                                    GenericTypeIndicator<List<ItemForFriendByDay>> t = new GenericTypeIndicator<List<ItemForFriendByDay>>() {
                                                    };
                                                    List<ItemForFriendByDay> friendByDayArrayList = shot.child("friendByDayList").getValue(t);

                                                    for (int i = 0; i < friendByDayArrayList.size(); i++) {
                                                        String friend_id = friendByDayArrayList.get(i).getUser_id();

                                                        if (friend_id.equals(user_id)) {

                                                            friendByDayArrayList.get(i).setSuccess(true);
                                                            friendByDayArrayList.get(i).setTime_stamp(time_stamp);
                                                            shot.getRef().child("friendByDayList").setValue(friendByDayArrayList);
                                                        }


                                                    }


                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /** 공유데이터 mission_info_list에 내 도착시간 기록**/

                    databaseReference.child("multi_data").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    GenericTypeIndicator<List<ItemForFriendByDay>> t = new GenericTypeIndicator<List<ItemForFriendByDay>>() {
                                    };
                                    List<ItemForFriendByDay> friendByDayArrayList = shot.child("mission_info_list").child("mission_dates").child(date).child("friendByDayList").getValue(t);
                                    for (int i = 0; i < friendByDayArrayList.size(); i++) {
                                        String friend_id = friendByDayArrayList.get(i).getUser_id();

                                        if (friend_id.equals(user_id)) {

                                            friendByDayArrayList.get(i).setSuccess(true);
                                            friendByDayArrayList.get(i).setTime_stamp(time_stamp);
                                            shot.getRef().child("mission_info_list").child("mission_dates").child(date).child("friendByDayList").setValue(friendByDayArrayList);
                                        }


                                    }


                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /** 개인데이터에 성공했으면 삭제**/
                    databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date_time").equalTo(date + time).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    shot.getRef().removeValue();
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /** 친구들  일간데이터에 display 기록 **/


                    /** missionInfoList에 기록**/


                    /**  의미없는 타임스탬프를 찍어서 데이터갱신**/

                    for (int i = 0; i < friendByDayList.size(); i++) {
                        String friend_id = friendByDayList.get(i).getUser_id();
                        databaseReference.child("user_data").child(friend_id).child("mission_display").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ItemForMissionByDay itemForMissionByDay = snapshot.getValue(ItemForMissionByDay.class);
                                        if (itemForMissionByDay != null) {
                                            if (itemForMissionByDay.getDate_time().equals(date + time)) {

                                                snapshot.getRef().child("time_stamp").setValue(time_stamp);
                                            }


                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        databaseReference.child("user_data").child(friend_id).child("mission_info_list").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        shot.getRef().child("time_stamp").setValue(time_stamp);


                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        if (!friend_id.equals(user_id)) {
                            ItemForNotification item = new ItemForNotification();
                            item.setDate_time(time_stamp);
                            item.setFriend_image(my_image);
                            item.setFriend_name(my_name);
                            item.setTitle(title);
                            item.setSingle(false);
                            item.setMission_id(mission_id);
                            item.setNotification_code(100);
                            item.setRead(false);


                            databaseReference.child("user_data").child(friend_id).child("notification").push().setValue(item);
                        } else {
                            /** 나에게 **/

                            ItemForNotification item = new ItemForNotification();
                            item.setDate_time(time_stamp);
                            item.setFriend_image(my_image);
                            item.setFriend_name(my_name);
                            item.setSingle(false);
                            item.setMission_id(mission_id);
                            item.setTitle(title);
                            item.setRead(false);
                            item.setNotification_code(100);
                            databaseReference.child("user_data").child(user_id).child("notification").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //pushAlarm(friend_id);
                                    FCM.fcmPushAlarm(user_id, title, "도착 성공!");
                                    String date_time = DateTimeUtils.getCurrentTime();
                                    queryData(date_time);
                                }
                            });


                        }
                    }


                }

            }

        } else {//타임아웃 푸시보내기?
            ItemForNotification item = new ItemForNotification();
            item.setDate_time(time_stamp);
            item.setFriend_image(my_image);
            item.setFriend_name(my_name);
            item.setSingle(false);
            item.setMission_id(mission_id);
            item.setTitle(title);
            item.setRead(false);
            item.setNotification_code(100);
            databaseReference.child("user_data").child(user_id).child("notification").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //pushAlarm(friend_id);

                    FCM.fcmPushAlarm(user_id, title, "실패하셨습니다..");
                    stopLocationUpdates();

                    String date_time = DateTimeUtils.getCurrentTime();
                    queryData(date_time);
                }
            });


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isWakeUpTimerRunning) {
            preciseCountdown.cancel();
        }
        if (isTimeOutTimerRunning) {
            timeOutAlarm.cancel();
        }
        stopLocationUpdates();

        Log.d("서비스", "onDestroy");

    }

    private void stopLocationUpdates() {
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);


        Log.d("서비스4", "stopUpdate");
    }

    private void timeOutAlarm() {
        if (isTimeOutTimerRunning) {
            timeOutAlarm.cancel();
        } else {

            timeOutAlarm = new PreciseCountdown(1000 * 60 * 30, 1000) {//30분전에 알람 diff - 1000 * 60 * 3
                @Override
                public void onTick(long timeLeft) {
                    //Log.d("서비스", "" + timeLeft);
                    isTimeOutTimerRunning = true;
                }

                @Override
                public void onFinished() {
                    isTimeOutTimerRunning = false;
                    Log.d("서비스", "" + "onfinished");


                    /** 30분 지나면 mission_display 삭제**/


                    //Utils.wakeDoze(getApplicationContext());


                }
            };
            timeOutAlarm.start();
        }


    }


}


