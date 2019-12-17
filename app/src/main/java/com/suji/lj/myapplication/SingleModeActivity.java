package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.suji.lj.myapplication.Fragments.DOWCalendarFragment;
import com.suji.lj.myapplication.Fragments.NormalCalendarFragment;
import com.suji.lj.myapplication.Fragments.PeriodCalendarFragment;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Utils.Utils;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SingleModeActivity extends AppCompatActivity implements TextWatcher, NormalCalendarFragment.OnDateChangedListener,
        TimePicker.OnTimeChangedListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    RadioButton normal_calendar_radio_button;
    RadioButton DOW_calendar_radio_button;
    RadioButton period_calendar_radio_button;
    RadioGroup calendar_radio_group;
    TextView next_btn;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    Intent intent;
    TimePicker timePicker;
    TextView search_map;
    Realm realm;
    MissionCartItem missionCartItem;
    Calendar calendar = Calendar.getInstance();
    TextView selected_address;
    int hour;
    int min;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);

        calendar_radio_group = findViewById(R.id.calendar_radio_group);
        normal_calendar_radio_button = findViewById(R.id.normal_calendar_radio_button);
        DOW_calendar_radio_button = findViewById(R.id.DOW_calendar_radio_button);
        period_calendar_radio_button = findViewById(R.id.period_calendar_radio_button);
        next_btn = findViewById(R.id.mission_basic_send);
        textInputEditText = findViewById(R.id.mission_basic_editText);
        textInputLayout = findViewById(R.id.mission_basic_inputLayout);
        timePicker = findViewById(R.id.time_picker_spinner);
        search_map = findViewById(R.id.single_mode_search_map);
        selected_address = findViewById(R.id.selected_address);



        textInputEditText.addTextChangedListener(this);
        calendar_radio_group.setOnCheckedChangeListener(this);
        search_map.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        timePicker.setOnTimeChangedListener(this);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);


        realmInit();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        isTitleValid(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void isTitleValid(String title) {

        if (title.length() > 2) {
            missionCartItem.setTitle(title);
        }


    }

    @Override
    public void onDateChanged(RealmList<DateItem> dateItemList) {

        if(dateItemList.size()==0){

        }else {

            missionCartItem.setCalendarDayList(dateItemList);
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        hour = hourOfDay;
        min = minute;
        missionCartItem.setHour(hour);
        missionCartItem.setMin(min);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.single_mode_search_map:

                startActivityForResult(new Intent(this, DaumMapActivity.class), 1);
                break;
            case R.id.mission_basic_send:
                realm.commitTransaction();
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                break;


        }
    }

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
                        .replace(R.id.fragment_calendar_container, new PeriodCalendarFragment())
                        .commit();
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getExtras().getString("key");
        selected_address.setText(data.getExtras().getString("address"));





        //Log.d("리절트",map_preview);

    }

    private void realmInit(){

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        missionCartItem = realm.createObject(MissionCartItem.class);

    }




}
