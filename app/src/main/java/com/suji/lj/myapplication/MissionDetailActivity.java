package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.suji.lj.myapplication.Adapters.RecyclerDetailFriendsAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionProgressAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewContactAdapter;
import com.suji.lj.myapplication.Decorators.OneDayDecorator;
import com.suji.lj.myapplication.Fragments.CalendarShowFragment;
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Decorators.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;


import java.nio.IntBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import io.realm.Realm;
import io.realm.RealmList;

public class MissionDetailActivity extends AppCompatActivity {

    //MaterialCalendarView materialCalendarView;

    private String mission_title;
    private double mission_latitude;
    private double mission_longitude;
    NestedScrollView mainScrollView;
    TextView tv_address_result;
    TextView tv_penalty_name_list;
    TextView tv_mission_date_start;
    TextView tv_mission_date_end;
    TextView tv_mission_time_start;
    TextView tv_mission_time_end;
    List<ItemForFriendByDay> friendsLists;
    Calendar calendar = Calendar.getInstance();

    int total_dates;
    //TextView tv_date_progress;
    //CircularProgressBar circularProgressBar;
    Toolbar toolbar;
    RecyclerView recyclerView_friends_list;

    DateTimeFormatter dtf = new DateTimeFormatter();
    Date initial_date;
    MaterialCalendarView materialCalendarView;
    Collection<CalendarDay> cal;
    List<ItemForDateTimeByList> itemForDateTimeByLists;
    TextView selected_date;
    TextView tv_penalty;
    TextView tv_penalty_total;
    TextView tv_current_penalty;
    TextView tv_failed_count;
    String min_date;
    String max_date;

    LinearLayout ly_start_date;
    LinearLayout ly_end_date;
    TextView tv_date_time_total;

    Handler mHandler;
    Runnable runnable;
    RecyclerView recycler_date_time;
    BottomSheetDialog bottomSheetDialog;
    String user_id;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);


        toolbar = findViewById(R.id.mission_detail_toolbar);
        user_id = Account.getUserId(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tv_address_result = findViewById(R.id.address_detail);


        mainScrollView = findViewById(R.id.main_scroll_view);

        tv_penalty_name_list = findViewById(R.id.penalty_name_list);
        tv_mission_date_start = findViewById(R.id.mission_date_start);
        tv_mission_date_end = findViewById(R.id.mission_date_end);
        tv_mission_time_start = findViewById(R.id.mission_time_start);
        tv_mission_time_end = findViewById(R.id.mission_time_end);
        recyclerView_friends_list = findViewById(R.id.recycler_friend_list);
        selected_date = findViewById(R.id.selected_date);
        ly_start_date = findViewById(R.id.ly_start_date);
        ly_end_date = findViewById(R.id.ly_end_date);
        tv_penalty = findViewById(R.id.penalty);
        tv_penalty_total = findViewById(R.id.penalty_amount);
        tv_current_penalty = findViewById(R.id.current_penalty);
        tv_failed_count = findViewById(R.id.failed_count);
        tv_date_time_total = findViewById(R.id.tv_date_time_total);
        materialCalendarView = findViewById(R.id.material_calendarView);
        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년 ").append(monthOne).append("월");
                return buffer;
            }
        });


        tv_date_time_total.setClickable(false);
        ly_start_date.setClickable(false);
        ly_end_date.setClickable(false);


        getIntents();


        tv_date_time_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createBottomSheet();


            }
        });

        ly_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialCalendarView.clearSelection();

                int index = 0;
                maintainCalendarState(index);
            }
        });

        ly_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = itemForDateTimeByLists.size() - 1;
                maintainCalendarState(index);
            }
        });


    }

    private void maintainCalendarState(int index) {

        materialCalendarView.clearSelection();

        String date_time = itemForDateTimeByLists.get(index).getDate_time();
        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        Log.d("캘린더", year + "  " + month + "   " + day);


        materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);

        //.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));


        materialCalendarView.setCurrentDate(CalendarDay.from(year, month, day));

        //selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));
        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));
        OneDayDecorator one = new OneDayDecorator(getResources().getColor(R.color.White));
        one.setDate(CalendarDay.from(year, month, day));
        materialCalendarView.addDecorator(one);
        //setupRecyclerViewFriendByDay(itemForDateTimeByLists.get(index).getFriendByDayList());


    }


    private void setRecyclerView(List<ItemForDateTimeByList> list) {
        RecyclerMissionProgressAdapter adapter = new RecyclerMissionProgressAdapter(this, list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_date_time.setLayoutManager(mLayoutManager);
        recycler_date_time.setAdapter(adapter);


    }

    private void createCalendar() {

        String date_time = itemForDateTimeByLists.get(0).getDate_time();
        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
        //Calendar calendar = Calendar.getInstance();
        //calendar.clear();
        calendar.setTime(temp);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        materialCalendarView.state().edit().setMinimumDate(CalendarDay.from(year, month, day)).commit();
        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //widget.removeDecorators();

                Log.d("달력", selected + " 선택");
                //widget.removeDecorators();
                widget.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));
                if (cal.contains(date)) {


                    OneDayDecorator one = new OneDayDecorator(getResources().getColor(R.color.White));
                    one.setDate(date);
                    widget.addDecorator(one);
                    for (int i = 0; i < itemForDateTimeByLists.size(); i++) {
                        String date_time = itemForDateTimeByLists.get(i).getDate_time();
                        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
                        // calendar.clear();
                        calendar.setTime(temp);

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);


                        if (year == date.getYear()) {
                            if (month == date.getMonth()) {
                                if (day == date.getDay()) {

                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int min = calendar.get(Calendar.MINUTE);


                                    selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));

                                }
                            }
                        }
                    }
                } else {
                    List<ItemForFriendByDay> friendByDayList = new ArrayList<>();

                    selected_date.setText("");

                }
            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void getIntents() {


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String mission_id = bundle.getString("mission_id");
            boolean isSingle = bundle.getBoolean("mission_mode");

            Log.d("싱글", mission_id);
            Log.d("싱글", isSingle + "");


            if (isSingle) {
                databaseReference.child("user_data").child(user_id).child("mission_info_list").orderByChild("mission_id").equalTo(mission_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MissionInfoList item = snapshot.getValue(MissionInfoList.class);
                            if (item != null) {


                                displaySingleItem(item);


                                Log.d("싱글", item.getMission_title());

                            } else {
                                //미션정보 없음
                                finish();


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //서버문제발생
                        finish();


                    }
                });
            }

        }
    }

    private void displaySingleItem(MissionInfoList item) {
        String address = item.getAddress();
        mission_latitude = item.getLat();
        mission_longitude = item.getLng();
        min_date = item.getMin_date();
        max_date = item.getMax_date();
        String mission_title = item.getMission_title();
        itemForDateTimeByLists = new ArrayList<>();

        //total_dates = item.getMission_dates().size();
        Map<String, ItemForDateTimeByList> map = item.getMission_dates();

        for (ItemForDateTimeByList object : map.values()) {

            Collections.addAll(itemForDateTimeByLists, object);
            Collections.sort(itemForDateTimeByLists, new Comparator<ItemForDateTimeByList>() {
                @Override
                public int compare(ItemForDateTimeByList o1, ItemForDateTimeByList o2) {
                    return o1.getDate_time().compareTo(o2.getDate_time());
                }
            });

            //Collections.add
            //itemForDateTimeByLists.add(object);
        }

        Log.d("싱글", "제목 " + mission_title);
        Log.d("싱글", min_date);
        Log.d("싱글", max_date);
        Log.d("싱글", item.getFriendByDayList().size() + "명");
        Log.d("싱글", itemForDateTimeByLists.get(0).getDate_time() + "날짜");

        friendsLists = item.getFriendByDayList();

        addMapFragment(new MapFragment(mainScrollView, mission_latitude, mission_longitude));


        setRecyclerViewFriendsList(friendsLists);


        tv_mission_date_start.setText(DateTimeUtils.makeDateForHuman(min_date));
        tv_mission_date_end.setText(DateTimeUtils.makeDateForHuman(max_date));
        tv_address_result.setText(address);

        toolbar.setTitle(mission_title);
        setSupportActionBar(toolbar);





        tv_mission_time_start.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(0).getDate_time(),"yyyyMMddHHmm"));
        tv_mission_time_end.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(itemForDateTimeByLists.size() - 1).getDate_time(),"yyyyMMddHHmm"));
        tv_penalty.setText(Utils.makeNumberCommaWon(item.getPenalty()));
        tv_penalty_total.setText(Utils.makeNumberCommaWon(item.getPenalty_amount()));
        tv_current_penalty.setText(Utils.makeNumberCommaWon(item.getPenalty() * item.getFailed_count()));
        tv_failed_count.setText(item.getFailed_count() + " 번");


        //Calendar calendar = Calendar.getInstance();

        cal = new HashSet<CalendarDay>();
        for (int i = 0; i < itemForDateTimeByLists.size(); i++) {

            String date_time = itemForDateTimeByLists.get(i).getDate_time();
            Date date = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
            //Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Log.d("캘린더", year + "  " + month + "   " + day);


            cal.add(CalendarDay.from(year, month, day));


        }

        createCalendar();


        tv_date_time_total.setClickable(true);
        ly_start_date.setClickable(true);
        ly_end_date.setClickable(true);




    }


    private void setRecyclerViewFriendsList(List<ItemForFriendByDay> friendsLists) {

        recyclerView_friends_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        RecyclerDetailFriendsAdapter adapter = new RecyclerDetailFriendsAdapter(friendsLists, this);
        recyclerView_friends_list.setAdapter(adapter);


    }

    private void createBottomSheet() {

        View view = View.inflate(this, R.layout.dialog_date_time_check, null);
        recycler_date_time = view.findViewById(R.id.recycler_progress_detail);
        LinearLayout ly_date_time = view.findViewById(R.id.ly_date_time_detail);

        //String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.d("높이", height + "");

        int maxHeight = (int) (height * 0.70);
        Log.d("높이", maxHeight + "max");
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
        //recycler_date_time.setLayoutParams(lp);
        ly_date_time.setLayoutParams(lp);
        setRecyclerView(itemForDateTimeByLists);


        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void addMapFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_layout, fragment).commit();
    }


}
