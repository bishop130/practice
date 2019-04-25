package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.suji.lj.myapplication.Adapters.LocationService;

public class AutoLocationSetting extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Switch auto_register_location_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_location_setting);
        auto_register_location_switch = findViewById(R.id.auto_register_location_switch);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Log.d("power","power" +powerManager.isPowerSaveMode());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && powerManager.isPowerSaveMode()) {
            Intent batterySaverIntent=new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
            startActivity(batterySaverIntent);
        }


        if(!checkLocationPermission()){
            auto_register_location_switch.setChecked(false);
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("auto_register_location", false);
            editor.apply();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        auto_register_location_switch.setChecked(sharedPreferences.getBoolean("auto_register_location", false));
        if (isServiceRunning()) {

            auto_register_location_switch.setChecked(true);
        } else {
            auto_register_location_switch.setChecked(false);
        }

        auto_register_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(checkLocationPermission()) {
                        Intent service = new Intent(AutoLocationSetting.this, LocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(AutoLocationSetting.this, service);
                        } else {
                            startService(service);
                        }


                        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                        editor.putBoolean("auto_register_location", isChecked);
                        editor.apply();
                    }
                    else{
                        requestPermission();
                    }

                    // The toggle is enabled
                } else {
                    Intent serviceIntent = new Intent(AutoLocationSetting.this, LocationService.class);
                    stopService(serviceIntent);
                    cancelAlarm();
                    SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_register_location", isChecked);
                    editor.apply();

                    // The toggle is disabled
                }
            }
        });


    }
    private void cancelAlarm(){
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("서비스","설정전"+(PendingIntent.getForegroundService(this, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
            PendingIntent pendingIntent = PendingIntent.getForegroundService(this,123,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pendingIntent);
            Log.d("서비스","설정후"+(PendingIntent.getForegroundService(this, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
        }



    }


    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    private Boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");
            return true;
        } else {
            // Should we show an explanation?
            return false;
        }
    }
    private void requestPermission(){


        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(AutoLocationSetting.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent service = new Intent(AutoLocationSetting.this, LocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(AutoLocationSetting.this, service);
                        } else {
                            startService(service);
                        }
                        Toast.makeText(this, "위치권한이 허가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "위치권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
