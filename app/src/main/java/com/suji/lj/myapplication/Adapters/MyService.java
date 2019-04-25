package com.suji.lj.myapplication.Adapters;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int number = 0;

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
        number = 0;
    }

    public int getNumber(){
        return number++;
    }
}
