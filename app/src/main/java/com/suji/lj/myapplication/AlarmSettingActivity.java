package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.suji.lj.myapplication.Adapters.AlarmSettingAdapter;
import com.suji.lj.myapplication.Items.AlarmSettingItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlarmSettingActivity extends AppCompatActivity {

    boolean is_alarm_on = true;
    String TAG = "AlarmSettingActivity";
    private SimpleDateFormat date_time = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.KOREA);
    RecyclerView recyclerView;
    AlarmSettingAdapter alarmSettingAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        Toolbar toolbar =  findViewById(R.id.toolbar_alarm_setting);
        toolbar.setTitle("알람설정");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Switch sw =  findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Switch On :" + isChecked);

                    SharedPreferences.Editor editor = getSharedPreferences("alarm_setting", MODE_PRIVATE).edit();
                    editor.putBoolean("alarm_setting", isChecked);
                    editor.apply();


                    // The toggle is enabled
                } else {
                    Log.d(TAG, "Switch Off :" + isChecked);
                    SharedPreferences.Editor editor = getSharedPreferences("alarm_setting", MODE_PRIVATE).edit();
                    editor.putBoolean("alarm_setting", isChecked);
                    editor.apply();


                    // The toggle is disabled
                }
            }
        });


        recyclerView = findViewById(R.id.alarm_setting_list);
        recyclerView.setLayoutManager(layoutManager);

        List<AlarmSettingItem> itemList = new ArrayList<>();
        itemList.add( new AlarmSettingItem("5분 전",1000*60*5));
        itemList.add( new AlarmSettingItem("10분 전",1000*60*10));
        itemList.add( new AlarmSettingItem("15분 전",1000*60*15));
        itemList.add( new AlarmSettingItem("30분 전",1000*60*30));
        itemList.add( new AlarmSettingItem("1시간 전",1000*60*60));
        itemList.add( new AlarmSettingItem("2시간 전",1000*60*60*2));
        itemList.add( new AlarmSettingItem("3시간 전",1000*60*60*3));
        itemList.add( new AlarmSettingItem("4시간 전",1000*60*60*4));
        itemList.add( new AlarmSettingItem("6시간 전",1000*60*60*6));
        itemList.add( new AlarmSettingItem("12시간 전",1000*60*60*12));
        itemList.add( new AlarmSettingItem("24시간 전",1000*60*60*24));


        alarmSettingAdapter = new AlarmSettingAdapter(this, itemList);
        List<AlarmSettingItem> ctList = ((AlarmSettingAdapter) alarmSettingAdapter).getStudentist();

        for(int i = 0; i<ctList.size(); i++){
            AlarmSettingItem alarmSettingItem = ctList.get(i);

            alarmSettingItem.isSelected();


        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(alarmSettingAdapter);


    }
}
