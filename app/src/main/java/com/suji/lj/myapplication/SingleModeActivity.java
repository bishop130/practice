package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.suji.lj.myapplication.Fragments.DOWCalendarFragment;
import com.suji.lj.myapplication.Fragments.NormalCalendarFragment;
import com.suji.lj.myapplication.Fragments.PeriodCalendarFragment;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SingleModeActivity extends AppCompatActivity implements TextWatcher, NormalCalendarFragment.OnDateChangedListener,
        TimePicker.OnTimeChangedListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

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
    Switch no_time_switch;
    TextView no_time_limit_tv;
    TextView wrong_time_tv;


    int selected_year = calendar.get(Calendar.YEAR);
    int selected_month = calendar.get(Calendar.MONTH)+1;
    int selected_day = calendar.get(Calendar.DATE);
    int hour;
    int min;
    String min_date=selected_year+"-"+selected_month+"-"+selected_day;

    boolean is_today = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);
        Log.d("목장","onCreate");

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
        no_time_switch = findViewById(R.id.no_time_switch);
        no_time_limit_tv = findViewById(R.id.no_time_limit_tv);
        wrong_time_tv = findViewById(R.id.wrong_time_tv);


        textInputEditText.addTextChangedListener(this);
        calendar_radio_group.setOnCheckedChangeListener(this);
        search_map.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        timePicker.setOnTimeChangedListener(this);
        no_time_switch.setOnCheckedChangeListener(this);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_calendar_container, new NormalCalendarFragment())
                .commit();

        //textInputEditText.setText("수정제목");
        selected_address.setText("수정주소");
        if(Build.VERSION.SDK_INT > 23){
            timePicker.setHour(1);
            timePicker.setMinute(11);
        }else{
            timePicker.setCurrentHour(1);
            timePicker.setCurrentMinute(11);
        }


        realmInit();
        checkDateTime();

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

        if (title.length() > 1) {
            missionCartItem.setTitle(title);
        } else {
            //제목이 너무 짧습니다. 최소 두 글자부터 입력이 가능합니다.
        }


    }

    @Override
    public void onDateChanged(RealmList<DateItem> dateItemList,String min_date,String max_date) {

        int min_position =0;

        if (dateItemList.size() == 0) {

        } else {//선택한 날짜가 오늘과 같다면

            missionCartItem.setCalendarDayList(dateItemList);
            missionCartItem.setMin_date(min_date);
            missionCartItem.setMax_date(max_date);
            checkDateTime();


        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        hour = hourOfDay;
        min = minute;
        checkDateTime();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.single_mode_search_map:

                startActivityForResult(new Intent(this, DaumMapActivity.class), 1);
                break;
            case R.id.mission_basic_send:
                dataCheck();
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
        Log.d("목장","onActivityResult");
        Log.d("목장",realm.isInTransaction()+"isInTransaction");
        data.getExtras().getString("key");
        String address = data.getExtras().getString("address");
        double lat = data.getExtras().getDouble("lat");
        double lng = data.getExtras().getDouble("lng");
        selected_address.setText(address);

        missionCartItem.setAddress(address);
        missionCartItem.setLat(lat);
        missionCartItem.setLng(lng);




        //Log.d("리절트",map_preview);

    }

    private void realmInit() {

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        if(!realm.isInTransaction()) {

            realm.beginTransaction();
            missionCartItem = realm.createObject(MissionCartItem.class);
            missionCartItem.setTitle("");
            missionCartItem.setAddress("");
            missionCartItem.setNo_time_limit(false);
        }
        Log.d("목장",realm.isInTransaction()+"isInTransaction");

    }

    private void dataCheck() {

        List<String> list = new ArrayList<>();
        if (missionCartItem.getTitle().length() < 2) {//제목이 두글자 미만일때
            list.add("제목을 입력해주세요");
        }
        if (missionCartItem.getAddress().length() == 0) {
            list.add("장소를 선택해주세요");
        }
        if (missionCartItem.getCalendarDayList().size() == 0) {
            list.add("날짜를 선택해주세요");

        }
        for (int i = 0; i < list.size(); i++) {

            Toast.makeText(this, list.get(i).toString(), Toast.LENGTH_LONG).show();


        }
        if (list.size() == 0) {

            realm.commitTransaction();
            Intent intent = new Intent();
            setResult(1, intent);

            finish();
        }


    }

    @Override//스위치 온오프
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            timePicker.setVisibility(View.VISIBLE);
            no_time_limit_tv.setVisibility(View.GONE);
            missionCartItem.setNo_time_limit(false);
            checkDateTime();


        } else {

            timePicker.setVisibility(View.GONE);
            no_time_limit_tv.setVisibility(View.VISIBLE);
            wrong_time_tv.setVisibility(View.GONE);
            missionCartItem.setNo_time_limit(true);


        }
    }

    private void checkDateTime() {
        long diff = 0;
        long now = System.currentTimeMillis();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        String input_time = min_date + " " + hour + ":" + min;
        Log.d("싱글", input_time + "  inputdate");
        try {
            Date input_date = sdf.parse(input_time);
            diff = (input_date.getTime() - now) / 60000;
            Log.d("싱글", now + "\n" + input_date.getTime() + "\n" + diff);
        } catch (ParseException e) {
            e.getErrorOffset();
        }

        if (diff >= 30) {

            wrong_time_tv.setVisibility(View.GONE);
            missionCartItem.setHour(hour);
            missionCartItem.setMin(min);
        } else {

            if (!no_time_switch.isChecked()) {

                wrong_time_tv.setVisibility(View.GONE);
            } else {

                wrong_time_tv.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("목장","onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //realm.beginTransaction();
        Log.d("목장","onResume");

    }
    @Override
    public void onBackPressed() {
        realm.cancelTransaction();
        finish();
        super.onBackPressed();
    }



}
