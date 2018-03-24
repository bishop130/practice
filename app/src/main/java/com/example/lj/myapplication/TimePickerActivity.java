package com.example.lj.myapplication;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimePickerActivity extends AppCompatActivity {

    TimePicker mTimePicker;
    private String hour,minute;
    String lat,lng;
    private String location;

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
                Toast.makeText(getApplicationContext(), "hour : " + hour + ", min : " + mins+"Location = "+location, Toast.LENGTH_LONG).show();
                hour = String.valueOf(hours);
                minute=String.valueOf(mins);
            }
        });
        Button button = (Button)findViewById(R.id.Btn_Ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimePickerActivity.this,DatePickerActivity.class);
                intent.putExtra("hour",hour);
                intent.putExtra("min",minute);
                intent.putExtra("Location",location);
                startActivity(intent);
            }
        });
    }
}
