package com.suji.lj.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.suji.lj.myapplication.Items.AlarmItem;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.PreciseCountdown;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationService extends Service {

    LocationRequest mLocationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DBHelper dbHelper;
    MainDB mainDB;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());

        }
    };
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 10 * 1000; /* 2 sec */
    String lat;
    String lng;
    private StringRequest request;
    private static final String goURL = "http://bishop130.cafe24.com/Mission_Control.php";
    private static final String getURL = "http://bishop130.cafe24.com/Mission_List.php";
    private RequestQueue requestQueue;

    List<AlarmItem> alarmItemList = new ArrayList<>();
    String service_time;
    String service_date;
    String service_title;
    StringBuffer stringBuffer = new StringBuffer();
    boolean is_volley_success;
    PreciseCountdown preciseCountdown;
    private SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat date_time_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    DateTimeFormatter dtf = new DateTimeFormatter();
    int notif_id = 1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("서비스", "onCreate");


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestQueue = Volley.newRequestQueue(this);


            dbHelper = new DBHelper(LocationService.this, "alarm_manager.db", null, 5);
            mainDB = new MainDB(LocationService.this, "main_manager.db", null, 1);
            foregroundNotification();

            getLatestMission();
        } else {
            instantAlarm("위치정보에 대한 접근이 거부되었습니다.", "위치정보에 대한 권한을 허가해주세요.");

            stopForeground(true);
            stopSelf();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("서비스", "onStartCommand");//notify 업데이트할 경우 처음 호출
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



            if(Utils.isServiceRunningInForeground(this, LocationService.class)){
                Utils.refreshDatabase(this,getSharedPreferences("Kakao",MODE_PRIVATE).getString("token",""));
                makeContentForNotification();

            }else{


            }

            stringBuffer.append("start\n");
        } else {
            instantAlarm("위치정보에 대한 접근이 거부되었습니다.", "위치정보에 대한 권한을 허가해주세요.");

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }



    private void makeContentForNotification() {
        Log.d("서비스", "makeContentForNotification");
//제일 가까운 30분 내 날짜 뽑기
        String query = "SELECT * FROM alarm_table WHERE strftime('%s', date_time) - strftime('%s', datetime('now','localtime'))<60 " +
                "AND strftime('%s', date_time) - strftime('%s', datetime('now','localtime'))>0 ORDER BY date_time ASC LIMIT 1";
        alarmItemList.clear();
        alarmItemList = dbHelper.setNextAlarm(query);
        if (alarmItemList.size() > 0) {

            startLocationUpdates();
            stringBuffer = new StringBuffer();

            updateNotification("다음 목표 : " + alarmItemList.get(0).getTitle() + " - " + DateTimeUtils.makeDateTimeForHuman(alarmItemList.get(0).getDate(), alarmItemList.get(0).getTime()), "자동위치 등록이 실행중입니다.");
            //foregroundNotification("다음 목표 : " + alarmItemList.get(0).getTitle() + " - " + Utils.monthDayTime(alarmItemList.get(0).getDate(), alarmItemList.get(0).getTime()), "자동위치 등록이 실행중입니다.");


        } else {
//시간 내에 약속이 없을경우 모든시간내에서 가장 가까운 하나 알림
            Log.d("서비스", "else로 빠짐");

            String query2 = "SELECT * FROM alarm_table ORDER BY date_time ASC LIMIT 1";
            alarmItemList.clear();
            alarmItemList = dbHelper.setNextAlarm(query2);
            if (alarmItemList.size() > 0) {
                for (int i = 0; i < alarmItemList.size(); i++) {
                    service_time = alarmItemList.get(i).getTime();
                    service_date = alarmItemList.get(i).getDate();
                    service_title = alarmItemList.get(i).getTitle();
                    updateNotification("다음 목표 : " + service_title + " - " + DateTimeUtils.makeDateTimeForHuman(service_date, service_time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
                    //foregroundNotification("다음 목표 : " + service_title + " - " + Utils.monthDayTime(service_date, service_time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
                }

            }
            else{
                updateNotification("다음 목표 : 없음", "새로운 목표를 등록해주세요!");
            }

        }

    }


    private void foregroundNotification() {

        startForeground(notif_id, getMyActivityNotification("처음","처음"));
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = date_time_sdf.format(new Date()) + " ; " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        stringBuffer.append(msg + "\n");
        Log.d("서비스3", msg);

        //연속으로 약속이 있을때는 else로 넘어가지 않는다. 그러므로 다음 약속을위해 실행되는 onLocationChanged메소드를 활용하여 is_success를 추적한다.
        String select_is_success = "SELECT * FROM alarm_table WHERE strftime('%s', date_time) < strftime('%s', datetime('now','localtime'))";
        alarmItemList.clear();
        alarmItemList = dbHelper.setNextAlarm(select_is_success);
        if (alarmItemList.size() > 0) {
            for (int i = 0; i < alarmItemList.size(); i++) {

                String mission_id = alarmItemList.get(i).getMission_id();
                String date_time = alarmItemList.get(i).getDate_time();
                String title = alarmItemList.get(i).getTitle();
                String user_id = alarmItemList.get(i).getUser_id();
                String date = alarmItemList.get(i).getDate();

                Log.d("서비스", "남아 있다면 실패");
                String is_failed = "1";
                //volleyConnect(user_id, mission_id, date_time, title, date, is_failed,goURL);

                //위치등록 로그 분석용
                stopLocationUpdates();

                instantAlarm(title, "위치등록에 실패했습니다.");
                Toast.makeText(getApplicationContext(), "위치등록 실패", Toast.LENGTH_LONG).show();
                Log.d("서비스", "위치등록 실패");
                String sql = "INSERT INTO main_table (is_success,title,mission_id,date_time) VALUES ('" + stringBuffer.toString() + "','" + title + "','" + mission_id + "','" + date_time + "')";
                mainDB.update(sql);


                //deleteFailedMission(mission_id);
                //deletePastDB();
                //getDBData();
            }
        }

        //연속으로 약속이 있을시에 다음 목표를 Foreground에 표시한다. 연속적이라는것은 onLocationChanged가 실행되고있는 동안에 다음 목표시간이 존재할 경우를 말한다.
        String query = "SELECT * FROM alarm_table WHERE strftime('%s', date_time) - strftime('%s', datetime('now','localtime'))<60 " +
                "AND strftime('%s', date_time) - strftime('%s', datetime('now','localtime'))>0 "; //30분
        alarmItemList.clear();
        alarmItemList = dbHelper.setNextAlarm(query);
        if (alarmItemList.size() > 0) {
            for (int i = 0; i < alarmItemList.size(); i++) {
                String time = alarmItemList.get(i).getTime();
                String lat = alarmItemList.get(i).getLat();
                String lng = alarmItemList.get(i).getLng();
                String mission_id = alarmItemList.get(i).getMission_id();
                String date = alarmItemList.get(i).getDate();
                String date_time = alarmItemList.get(i).getDate_time();
                String title = alarmItemList.get(i).getTitle();
                String user_id = alarmItemList.get(i).getUser_id();



                if ((location.getLatitude() - Double.valueOf(lat)) * (location.getLatitude() - Double.valueOf(lat)) + (location.getLongitude() - Double.valueOf(lng)) * (location.getLongitude() - Double.valueOf(lng)) < 0.0007 * 0.0007) {

                    Log.d("서비스", "여기 두번 콜되면 안돼");

                    String is_failed = "0";
                    volleyConnect(user_id, mission_id, date_time, title, date, is_failed,goURL);


                    //updateDB(mission_id, date_time);
                    instantAlarm(title, "위치등록에 성공했습니다.");
                    Toast.makeText(getApplicationContext(), "위치등록 성공", Toast.LENGTH_LONG).show();
                    Log.d("서비스", "위치등록 성공");
                    //getLatestMission();

                    String sql2 = "INSERT INTO main_table (is_success,title,mission_id,date_time) VALUES ('" + stringBuffer.toString() + "','" + title + "','" + mission_id + "','" + date_time + "')";
                    //String sql = "UPDATE alarm_table SET is_success = 'true' WHERE mission_id = '" + mission_id + "' AND date_time = '" + date_time + "'";
                    //dbHelper.update(sql);
                    mainDB.update(sql2);
                    stopLocationUpdates();

                }
            }
            //foregroundNotification(sb.toString());

        } else {
            //지정시간에 추적할 약속이 없을 경우 stop한다.
            Log.d("서비스", "list가 null임  ");
            //sb.append(date_time);
            stopLocationUpdates();


            //지정된 시간 외에 설정한 약속이 하나라도 남으면 가장최근 정보 불러오기
            String query2 = "SELECT * FROM alarm_table ORDER BY date_time ASC LIMIT 1";
            alarmItemList.clear();
            alarmItemList = dbHelper.setNextAlarm(query2);
            if (alarmItemList.size() > 0) {//

                getLatestMission();
                updateNotification("다음 목표 : " + alarmItemList.get(0).getTitle() + " - " + DateTimeUtils.makeDateTimeForHuman(alarmItemList.get(0).getDate(), alarmItemList.get(0).getTime()), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
                //foregroundNotification("다음 목표 : " + alarmItemList.get(0).getTitle() + " - " + Utils.monthDayTime(alarmItemList.get(0).getDate(), alarmItemList.get(0).getTime()), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
            } else {//진짜 아무것도 없으면
                updateNotification("다음 목표 : 없음", "새로운 목표를 등록해주세요!");
                //foregroundNotification("다음 목표 : 없음", "새로운 목표를 등록해주세요!");

            }
        }
    }

    private void volleyConnect(final String userId, final String mission_id, final String date_time, final String title, final String date, final String is_failed,String url) {


        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("서비스", "volley response  ");
                Utils.refreshDatabase(getApplicationContext(),userId);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LocationService.this, "네트워크 연결이 되지않습니다." + error, Toast.LENGTH_LONG).show();
                is_volley_success = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", userId);
                hashMap.put("mission_id", mission_id);
                hashMap.put("mission_date", date);
                hashMap.put("is_failed", is_failed);


                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    // You can now create a LatLng Object for use with maps
    private void stopLocationUpdates() {
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        Log.d("서비스4", "stopUpdate");
    }

    private void getLatestMission() {
        String query = "SELECT * FROM alarm_table ORDER BY date_time ASC LIMIT 1";
        alarmItemList.clear();
        alarmItemList = dbHelper.setNextAlarm(query);
        if (alarmItemList.size() > 0) {
            for (int i = 0; i < alarmItemList.size(); i++) {

                String title = alarmItemList.get(i).getTitle();
                String time = alarmItemList.get(i).getTime();
                String mission_id = alarmItemList.get(i).getMission_id();
                String date = alarmItemList.get(i).getDate();
                String date_time = alarmItemList.get(i).getDate_time();
                Log.d("서비스", "getDBData   size:" + alarmItemList.size() + "다음목표" + title);

                //makeAlarm(time, date, mission_id);
                timerAlarm(date_time,date,title,time);

            }
        } else {
            updateNotification("다음 목표 : 없음", "새로운 목표를 등록해주세요!");
            //foregroundNotification("다음 목표 : 없음", "새로운 목표를 등록해주세요!");
        }
    }

    private void timerAlarm(final String date_time, final String date, final String title, final String time) {

        try{
            preciseCountdown.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        preciseCountdown = null;

        updateNotification("다음 목표 : " + title + " - " + DateTimeUtils.makeDateTimeForHuman(date, time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");
        //foregroundNotification("다음 목표 : " + title + " - " + Utils.monthDayTime(date, time), "목표시간 30분 전에 자동위치등록이 시작됩니다.");

        Date current_date_time = new Date(System.currentTimeMillis());
        Date mission_date_time = dtf.dateTimeParser(date_time);
        long diff = mission_date_time.getTime() - current_date_time.getTime();
        //countDownTimer.cancel();

         preciseCountdown= new PreciseCountdown(diff - 1000 * 60 * 3,1000) {
            @Override
            public void onTick(long timeLeft) {
                Log.d("서비스", "" + timeLeft);
            }

            @Override
            public void onFinished() {
                Log.d("서비스", "" + "onfinished");
                wakeDoze();

            }
        };
        preciseCountdown.start();
    }

    private void wakeDoze(){
        Log.d("서비스", "wakeDoze");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //PendingIntent foregroundService_sender = PendingIntent.getForegroundService(this, 123, new Intent(this, LocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,3000, foregroundService_sender);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//26
            //PendingIntent foregroundService_sender = PendingIntent.getForegroundService(this, 123, new Intent(this, LocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent foregroundService_sender = PendingIntent.getForegroundService(this,123,new Intent(this,LocationService.class),PendingIntent.FLAG_UPDATE_CURRENT );

            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(0,foregroundService_sender),foregroundService_sender);
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //21
            PendingIntent foregroundService_sender = PendingIntent.getService(this,123,new Intent(this,LocationService.class),PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.setExact(AlarmManager.RTC, 0, foregroundService_sender);
        }
        else {
            PendingIntent foregroundService_sender = PendingIntent.getService(this, 123, new Intent(this, LocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, 0, foregroundService_sender);
        }


    }


    private void instantAlarm(String title, String content) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BroadCastAlarm.class);
        intent.putExtra("title", title);
        intent.putExtra("is_success", content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (new Date()).getTime() + 5000, pendingIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        // 음악 종료
        //cancelAlarm();
        try{
            preciseCountdown.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        preciseCountdown=null;


        stopLocationUpdates();
        Log.d("서비스", "onDestroy");

    }








    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

        //locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    private Notification getMyActivityNotification(String title, String text){
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

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(String title, String text) {
        Log.d("서비스","updateNotification");
        Notification notification = getMyActivityNotification(title,text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notif_id, notification);
    }



}
