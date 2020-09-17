package com.suji.lj.myapplication.Adapters;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.util.Log;

import com.suji.lj.myapplication.MainActivity;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BroadCastAlarm extends BroadcastReceiver {
    PowerManager pm;
    PowerManager.WakeLock wakeLock;

    private static final String TAG = "com.suji.lj.myapplication";


    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notifManager.createNotificationChannel(mChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("is_success");


        Log.d("확인좀", "들어왔나확인");


        builder.setContentTitle("title") // required
                .setContentText(content)  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentIntent(pendingIntent)
           .setGroup(TAG)
          .setGroupSummary(true);


        notifManager.notify(0, builder.build());
        wakeLock(context);
    }


    private void wakeLock(Context context){

        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        wakeLock = pm.newWakeLock( PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE,"myapp:TagforBroadCastAlarm");
        wakeLock.acquire(3000);


    }


}
