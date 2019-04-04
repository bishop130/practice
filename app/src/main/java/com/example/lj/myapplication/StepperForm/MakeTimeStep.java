package com.example.lj.myapplication.StepperForm;

import android.app.Activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;


import com.example.lj.myapplication.NewMissionActivity;
import com.example.lj.myapplication.R;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ernestoyaquello.com.verticalstepperform.Step;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MakeTimeStep extends Step<MakeTimeStep.TimeHolder> {

    private static final int DEFAULT_HOURS = 0;
    private static final int DEFAULT_MINUTES = 0;
    private TextView alarmTimeTextView;
    private TimePickerDialog alarmTimePicker;
    private int alarmTimeHour;
    private int alarmTimeMinutes;
    private String min;
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    SimpleDateFormat date = new SimpleDateFormat("yyyy-M-dd", Locale.KOREA);
    SimpleDateFormat date_time = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.KOREA);
    private Date current_time = null;
    private Date target_time = null;
    public MakeTimeStep(String title) {
        this(title, "");
    }

    public MakeTimeStep(String title, String subtitle) {

        super(title, subtitle);
        alarmTimeHour = DEFAULT_HOURS;
        alarmTimeMinutes = DEFAULT_MINUTES;
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View timeStepContent = inflater.inflate(R.layout.step_time_layout, null, false);
        alarmTimeTextView = timeStepContent.findViewById(R.id.time_pick);
        setupAlarmTime();

        // timePicker.setOnTimeChangedListener();


        return timeStepContent;
    }


    private void setupAlarmTime() {
        if (alarmTimePicker == null) {
            alarmTimePicker = new TimePickerDialog(getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            alarmTimeHour = hourOfDay;
                            alarmTimeMinutes = minute;

                            updatedAlarmTimeText();
                            markAsCompletedOrUncompleted(true);
                            //isStepDataValid();
                        }
                    }, alarmTimeHour, alarmTimeMinutes, false);
        } else {
            alarmTimePicker.updateTime(alarmTimeHour, alarmTimeMinutes);
        }

        if (alarmTimeTextView != null) {
            alarmTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmTimePicker.show();
                }
            });
        }


    }


    @Override
    public TimeHolder getStepData() {
        return new TimeHolder(alarmTimeHour, alarmTimeMinutes);
    }

    @Override
    public String getStepDataAsHumanReadableString() {

        if (alarmTimeMinutes < 10) {
            min = "0" + String.valueOf(alarmTimeMinutes);
        } else {
            min = String.valueOf(alarmTimeMinutes);
        }


        if (alarmTimeHour == 12) {
            return "오후 " + alarmTimeHour + ":" + min;
        } else if (alarmTimeHour == 0) {
            return "오전 12:" + min;
        } else {

            return ((alarmTimeHour >= 12) ? "오후" : "오전") + " " + alarmTimeHour % 12 + ":" + min;
        }
    }

    @Override
    public void restoreStepData(TimeHolder data) {
        alarmTimeHour = data.hour;
        alarmTimeMinutes = data.minutes;

        alarmTimePicker.updateTime(alarmTimeHour, alarmTimeMinutes);
        updatedAlarmTimeText();
    }

    @Override
    protected IsDataValid isStepDataValid(TimeHolder stepData) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("date_array", "");
        List<String> myList = new ArrayList<String>(Arrays.asList(s.split("\\s*,\\s*")));

        for (int i = 0; i < myList.size(); i++) {
            try {
                Date current_date = date.parse(date.format(new Date(System.currentTimeMillis())));
                Date myList_date = date.parse(myList.get(i));
                if(current_date.equals(myList_date)) {

                    current_time = date_time.parse(date_time.format(new Date(System.currentTimeMillis())));
                    target_time = date_time.parse(myList.get(i) + " " + stepData.hour + ":" + stepData.minutes + ":0");
                    if(target_time.getTime()<current_time.getTime()){  //+1800000
                        return new IsDataValid(false,"현재시간 기준, 30분뒤 부터 선택할 수 있습니다.");

                    }
                    else{
                        return new IsDataValid(true);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new IsDataValid(true);

    }


    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    private void updatedAlarmTimeText() {
        alarmTimeTextView.setText(getStepDataAsHumanReadableString());
    }

    public static class TimeHolder {

        public int hour;
        public int minutes;

        public TimeHolder(int hour, int minutes) {
            this.hour = hour;
            this.minutes = minutes;
        }
    }
}
