package com.example.lj.myapplication.Adapters;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.example.lj.myapplication.Items.AlarmItem;
import com.example.lj.myapplication.MainActivity;
import com.example.lj.myapplication.NewMissionActivity;
import com.example.lj.myapplication.NotificationActivity;
import com.example.lj.myapplication.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class BroadCastService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고

        Log.d("브로드", "들어왔나확인");
        Intent serviceIntent = new Intent(context, LocationService.class);
        context.stopService(serviceIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

    }
    public boolean isServiceRunning(Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (LocationService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


}

