package com.example.lj.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.kakao.usermgmt.response.model.UserProfile;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerActivity extends AppCompatActivity {

    CalendarView calendarView;
    private int year, month, day;
    private String Year, Month, Day;
    private String hour,min;
    private String Lat,Lng;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        year = calendar.get(Calendar.YEAR);
        Year = String.valueOf(year);
        month = calendar.get(Calendar.MONTH) + 1;
        Month = String.valueOf(month);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Day = String.valueOf(day);

        Toast.makeText(getApplicationContext(),userId,Toast.LENGTH_LONG).show();



        Intent getintent = getIntent();
        hour = getintent.getStringExtra("hour");
        min = getintent.getStringExtra("min");
        Lat = getintent.getStringExtra("Lat");
        Lng = getintent.getStringExtra("Lng");
        userId = getintent.getStringExtra("userId");

        calendarView =(CalendarView)findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int years, int months, int days) {

                year=years;
                Year = String.valueOf(year);

                month=months+1;
                Month = String.valueOf(month);

                day=days;
                Day = String.valueOf(day);


            }
        });

        Button button = (Button)findViewById(R.id.Btn_Ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatePickerActivity.this,ResultActivity.class);
                intent.putExtra("year",Year);
                intent.putExtra("month",Month);
                intent.putExtra("day",Day);
                intent.putExtra("hour",String.valueOf(hour));
                intent.putExtra("min",String.valueOf(min));
                intent.putExtra("Lat",Lat);
                intent.putExtra("Lng",Lng);
                intent.putExtra("userId",userId);
                startActivity(intent);

            }
        });


        Toast.makeText(getApplicationContext(),hour+":"+min,Toast.LENGTH_LONG).show();


    }
}
