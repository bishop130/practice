package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationUtils {



    public static void startLocationUpdates(Context context, LocationCallback locationCallback) {
        long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
        long FASTEST_INTERVAL = 10 * 1000; /* 2 sec */

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

        //locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

}
