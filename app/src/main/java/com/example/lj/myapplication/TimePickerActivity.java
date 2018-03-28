package com.example.lj.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

public class TimePickerActivity extends AppCompatActivity {

    TimePicker mTimePicker;
    private String hour,minute;
    String lat,lng;
    private String location;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");
        double Lat = Double.parseDouble(lat);
        double Lng = Double.parseDouble(lng);
        lat = String.format("%.6f",Lat);
        lng = String.format("%.6f",Lng);
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                userId = String.valueOf(userProfile.getId());
            }

            @Override
            public void onNotSignedUp() {
            }
        });





        location = lat+"/"+lng;

        mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mTimePicker.getHour() + "";
            minute = mTimePicker.getMinute() + "";
        } else {
            hour = mTimePicker.getCurrentHour() + "";
            minute = mTimePicker.getCurrentMinute() + "";
        }
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hours, int mins) {

                hour = String.format("%02d",hours);
                hour = String.valueOf(hour);

                minute=String.format("%02d",mins);
                minute=String.valueOf(minute);
                Toast.makeText(getApplicationContext(), "hour : " + hour + ", min : " + minute+"Location = "+location, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), userId, Toast.LENGTH_LONG).show();
            }
        });


        Button button = (Button)findViewById(R.id.Btn_Ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimePickerActivity.this,DatePickerActivity.class);
                intent.putExtra("hour",hour);
                intent.putExtra("min",minute);
                intent.putExtra("userId",userId);
                intent.putExtra("Lat",lat);
                intent.putExtra("Lng",lng);
                startActivity(intent);
            }
        });
    }
    private void requestMe() {

    }

}
