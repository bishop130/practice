package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.suji.lj.myapplication.Fragments.DOWCalendarFragment;
import com.suji.lj.myapplication.Fragments.NormalCalendarFragment;
import com.suji.lj.myapplication.Fragments.PeriodCalendarFragment;

public class SingleModeActivity extends AppCompatActivity {

    RadioButton normal_calendar_radio_button;
    RadioButton DOW_calendar_radio_button;
    RadioButton period_calendar_radio_button;
    RadioGroup calendar_radio_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);

        calendar_radio_group = findViewById(R.id.calendar_radio_group);
        normal_calendar_radio_button = findViewById(R.id.normal_calendar_radio_button);
        DOW_calendar_radio_button = findViewById(R.id.DOW_calendar_radio_button);
        period_calendar_radio_button = findViewById(R.id.period_calendar_radio_button);





        calendar_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.normal_calendar_radio_button:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_calendar_container, new NormalCalendarFragment())
                                .commit();
                        break;
                    case R.id.DOW_calendar_radio_button:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_calendar_container, new DOWCalendarFragment())
                                .commit();
                        break;
                    case R.id.period_calendar_radio_button:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_calendar_container,new PeriodCalendarFragment())
                                .commit();
                        break;
                }

            }
        });

    }
}
