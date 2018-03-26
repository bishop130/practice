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
    private String hour,min;
    private String Lat,Lng;
    UserProfile userProfile;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        userId = String.valueOf(userProfile.getId());
        Toast.makeText(getApplicationContext(),userId,Toast.LENGTH_LONG).show();



        Intent getintent = getIntent();
        hour = getintent.getStringExtra("hour");
        min = getintent.getStringExtra("min");
        Lat = getintent.getStringExtra("Lat");
        Lng = getintent.getStringExtra("Lng");

        calendarView =(CalendarView)findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int years, int months, int days) {

                year=years;
                month=months+1;
                day=days;

                Toast.makeText(getApplicationContext(),year+"/"+month+"/"+day,Toast.LENGTH_LONG).show();


            }
        });

        Button button = (Button)findViewById(R.id.Btn_Ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatePickerActivity.this,ResultActivity.class);
                intent.putExtra("date",String.valueOf(year+"/"+month+"/"+day));
                intent.putExtra("time",String.valueOf(hour+"/"+min));
                intent.putExtra("Lat",Lat);
                intent.putExtra("Lng",Lng);
                startActivity(intent);

            }
        });


        Toast.makeText(getApplicationContext(),hour+":"+min+"/"+year+month+day,Toast.LENGTH_LONG).show();


    }
}
