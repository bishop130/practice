package com.suji.lj.myapplication.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.suji.lj.myapplication.AutoLocationSetting;

public class LocationReceiver extends BroadcastReceiver {
    boolean isGpsEnabled;
    boolean isNetworkEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Intent i = new Intent(context, NewLocationService.class);
            if (isGpsEnabled || isNetworkEnabled) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //ContextCompat.startForegroundService(context, i);
                } else {
                    //context.startService(i);
                }
            } else {
                Toast.makeText(context, "위치설정 꺼짐",
                        Toast.LENGTH_SHORT).show();
                context.stopService(i);
            }

        }
    }
}
