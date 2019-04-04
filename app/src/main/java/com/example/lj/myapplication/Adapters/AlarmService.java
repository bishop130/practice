package com.example.lj.myapplication.Adapters;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.lj.myapplication.MainActivity;
import com.example.lj.myapplication.NotificationActivity;
import com.example.lj.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AlarmService extends Service {

    private SimpleDateFormat date_time = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.KOREA);
    DBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("알람서비스", "onCreate");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("알람서비스", "onStartCommand");

        String mission_time = intent.getStringExtra("mission_time");
        String date_array = intent.getStringExtra("date_array");
        String mission_id = intent.getStringExtra("mission_id");
        String lat = intent.getStringExtra("lat");
        String lng = intent.getStringExtra("lng");
        String title = intent.getStringExtra("title");

        makeForeGround(title);
        makeAlarm(mission_time, date_array, mission_id, lat, lng, title);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        // 음악 종료
        Log.d("알람서비스", "onDestroy");

    }

    private void makeAlarm(String mission_time, String date_array, String mission_id, String lat, String lng, String title) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long[] interval = {1000 * 60 * 5, 1000 * 60 * 4, 1000 * 60 * 3};
        long time = 1000 * 60 * 60;


        List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));

        for (int i = 0; i < items.size(); i++) {

            int requestID = String.valueOf(mission_id + items.get(i) + time).hashCode();
            try {
                Log.d("확인좀", String.valueOf(requestID));

                Date cur_time = date_time.parse(date_time.format(new Date(System.currentTimeMillis())));
                Date mission_datetime = date_time.parse(items.get(i) + " " + mission_time);
                Intent intent = new Intent(this, BroadCastService.class);
                long days = TimeUnit.MILLISECONDS.toDays(time);
                long remainingHoursInMillis = time - TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                intent.putExtra("hour", hours);
                intent.putExtra("min", minutes);
                intent.putExtra("interval", time);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("mission_id", mission_id);
                intent.putExtra("mission_time", mission_time);
                intent.putExtra("mission_date", items.get(i));
                intent.putExtra("title", title);


                PendingIntent sender = PendingIntent.getBroadcast(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent foregroundService_sender = PendingIntent.getBroadcast(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - time, foregroundService_sender);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - time, sender);
                else
                    alarmManager.set(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - time, sender);

            } catch (ParseException e) {

            }

        }

    }

    private void makeForeGround(String title) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.drawer_header);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "Alarm_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Alarm", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Application")
                .setContentText(title)
                .setContentIntent(pendingIntent);

        startForeground(2, builder.build());

    }

    private void setNextAlarm() {
        dbHelper = new DBHelper(AlarmService.this, "alarm_manager.db", null, 4);
        String query = "SELECT * FROM alarm_table ORDER BY ABS(`date_time` - 'now') LIMIT 1 WHERE is_success = 'false";
        dbHelper.setNextAlarm(query);
        /*
        if(result!=null) {
            while (result.moveToNext()) {
                int id = result.getInt(0);
                String time = result.getString(1);
                String lat = result.getString(2);
                String lng = result.getString(3);
                String mission_id = result.getString(4);
                String date = result.getString(5);
                String date_time = result.getString(6);
                String is_success = result.getString(7);

                Log.d("디바2", "id:" + id + "   lat:" + String.valueOf(lat) + "   " +
                        "lng:" + String.valueOf(lng) + "     time:" + time + "     mission_id:" + mission_id + "   " +
                        "  date:" + date + "     date+time:" + date_time + "      is_success:" + is_success);
                Intent service = new Intent(this, AlarmService.class);
                service.putExtra("mission_time", time);
                service.putExtra("date_array", date);
                service.putExtra("mission_id", mission_id);
                service.putExtra("lat", lat);
                service.putExtra("lng", lng);
                //service.putExtra("title",title);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, service);
                } else {
                    startService(service);
                }

            }
        }
        else{

        }
*/

    }
}
