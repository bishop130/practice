package com.suji.lj.myapplication.StepperForm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;


import com.suji.lj.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.Step;

public class MakeTimeStep extends Step<MakeTimeStep.TimeHolder> {

    private static final int DEFAULT_HOURS = 0;
    private static final int DEFAULT_MINUTES = 0;
    private TimePicker timePicker;
    private Switch no_limit_time_switch;
    private int alarmTimeHour;
    private int alarmTimeMinutes;
    private String min;
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    SimpleDateFormat date = new SimpleDateFormat("yyyy-M-dd", Locale.KOREA);
    SimpleDateFormat date_time = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.KOREA);
    private Date current_time = null;
    private Date target_time = null;
    LinearLayout no_limit_time_layout;
    boolean switch_state = true;

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
        View timeStepContent = inflater.inflate(R.layout.step_time_pick, null, false);
        timePicker = timeStepContent.findViewById(R.id.time_picker_spinner);
        no_limit_time_layout = timeStepContent.findViewById(R.id.time_picker_layout);
        no_limit_time_switch = timeStepContent.findViewById(R.id.no_limit_time_switch);
        no_limit_time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_state = isChecked;

                Log.d("타임","switch");
                if (isChecked) {
                    no_limit_time_layout.setVisibility(View.VISIBLE);


                } else {
                    no_limit_time_layout.setVisibility(View.GONE);
                }
                markAsCompletedOrUncompleted(true);
            }
        });
        setupAlarmTime();

        // timePicker.setOnTimeChangedListener();


        return timeStepContent;
    }


    private void setupAlarmTime() {
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("타임","timepickerchanger");
                alarmTimeHour = hourOfDay;
                alarmTimeMinutes = minute;
                markAsCompletedOrUncompleted(true);

            }
        });



    }


    @Override
    public TimeHolder getStepData() {
        Log.d("타임","getstepdata");
        //markAsCompletedOrUncompleted(true);
        return new TimeHolder(alarmTimeHour, alarmTimeMinutes);

    }

    @Override
    public String getStepDataAsHumanReadableString() {
        Log.d("타임","getstepdatahumanread");
        if (alarmTimeMinutes < 10) {
            min = "0" + String.valueOf(alarmTimeMinutes);
        } else {
            min = String.valueOf(alarmTimeMinutes);
        }


        if (!switch_state) {
            return "시간제한없음";
        } else {


            if (alarmTimeHour == 12) {
                return "오후 " + alarmTimeHour + ":" + min;
            } else if (alarmTimeHour == 0) {
                return "오전 12:" + min;
            } else {

                return ((alarmTimeHour >= 12) ? "오후" : "오전") + " " + alarmTimeHour % 12 + ":" + min;
            }
        }
    }

    @Override
    public void restoreStepData(TimeHolder data) {
        alarmTimeHour = data.hour;
        alarmTimeMinutes = data.minutes;
        Log.d("타임","restorestepdata");

        //alarmTimePicker.updateTime(alarmTimeHour, alarmTimeMinutes);
        //updatedAlarmTimeText(getStepDataAsHumanReadableString());
    }

    @Override
    protected IsDataValid isStepDataValid(TimeHolder stepData) {
        Log.d("타임","isStepDataValid");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("date_array", "");
        List<String> myList = new ArrayList<String>(Arrays.asList(s.split("\\s*,\\s*")));
        if(!switch_state){
            return new IsDataValid(true);
        }
        else {

            for (int i = 0; i < myList.size(); i++) {
                try {
                    Date current_date = date.parse(date.format(new Date(System.currentTimeMillis())));
                    Date myList_date = date.parse(myList.get(i));
                    if (current_date.equals(myList_date)) {

                        current_time = date_time.parse(date_time.format(new Date(System.currentTimeMillis())));
                        target_time = date_time.parse(myList.get(i) + " " + stepData.hour + ":" + stepData.minutes + ":0");
                        if (target_time.getTime() < current_time.getTime() + 1000 * 60 * 30) {  //+1800000
                            return new IsDataValid(false, "현재시간 기준, 30분뒤 부터 선택할 수 있습니다.");

                        } else {
                            return new IsDataValid(true);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return new IsDataValid(true);
        }

    }


    @Override
    protected void onStepOpened(boolean animated) {
        Log.d("타임","onstepopen");

    }

    @Override
    protected void onStepClosed(boolean animated) {
        Log.d("타임","onstepclosed");
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        Log.d("타임","onstepmarkedcomple");
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        Log.d("타임","onstepuncompleted");
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
