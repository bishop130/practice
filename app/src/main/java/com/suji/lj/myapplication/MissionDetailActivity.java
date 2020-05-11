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
import android.text.format.DateFormat;
import android.util.Log;
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
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
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

public class MissionDetailActivity extends AppCompatActivity implements View.OnClickListener {

    //MaterialCalendarView materialCalendarView;

    private String mission_title;
    private double mission_latitude;
    private double mission_longitude;
    NestedScrollView mainScrollView;
    TextView address_result;
    TextView penalty_name_list;
    TextView mission_date_start;
    TextView mission_date_end;
    TextView penalty_content;
    TextView mission_time_start;
    TextView mission_time_end;
    List<ContactItemForServer> friendsLists;
    String mission_time;
    String min_date;
    String max_date;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Bitmap snapshotBitmap;
    ImageView some_image;
    LinearLayout test;
    ViewGroup mapViewContainer;
    RecyclerView recyclerView;
    int total_dates;
    TextView tv_date_progress;
    CircularProgressBar circularProgressBar;
    Toolbar toolbar;
    RecyclerView recyclerView_friends_list;

    DateTimeFormatter dtf = new DateTimeFormatter();
    Date initial_date;
    MaterialCalendarView materialCalendarView;
    Collection<CalendarDay> cal;
    List<ItemForDateTimeByList> itemForDateTimeByLists;
    TextView selected_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);


        toolbar = findViewById(R.id.mission_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        address_result = findViewById(R.id.address_detail);


        mainScrollView = findViewById(R.id.main_scroll_view);

        penalty_name_list = findViewById(R.id.penalty_name_list);
        mission_date_start = findViewById(R.id.mission_date_start);
        mission_date_end = findViewById(R.id.mission_date_end);
        penalty_content = findViewById(R.id.penalty_content);
        mission_time_start = findViewById(R.id.mission_time_start);
        mission_time_end = findViewById(R.id.mission_time_end);
        recyclerView = findViewById(R.id.recycler_progress_detail);
        tv_date_progress = findViewById(R.id.tv_date_progress);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        recyclerView_friends_list = findViewById(R.id.recycler_friends_list);
        selected_date = findViewById(R.id.selected_date);

        getIntents();
        //some_image.setImageBitmap(getBitmapFromView(test));

        //Log.d("안될듯",viewToBitmap().toString());


        //query();

        materialCalendarView = findViewById(R.id.material_calendarView);
        //recycler_date_time = findViewById(R.id.recycler_date_time);

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();


        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년  ").append(monthOne).append("월");
                return buffer;
            }
        });

        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //widget.removeDecorators();
                widget.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));
                if (cal.contains(date)) {
                    OneDayDecorator one = new OneDayDecorator(getResources().getColor(R.color.White));
                    one.setDate(date);
                    widget.addDecorator(one);
                    for (int i = 0; i < itemForDateTimeByLists.size(); i++) {
                        int year = itemForDateTimeByLists.get(i).getYear();
                        int month = itemForDateTimeByLists.get(i).getMonth();
                        int day = itemForDateTimeByLists.get(i).getDay();
                        if (year == date.getYear()) {
                            if (month == date.getMonth()) {
                                if (day == date.getDay()) {
                                    int hour = itemForDateTimeByLists.get(i).getHour();
                                    int min = itemForDateTimeByLists.get(i).getMin();
                                    selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));

                                }
                            }
                        }
                    }
                }else{
                    selected_date.setText("");
                }
            }

        });


    }

    private void setRecyclerView(List<ItemForDateTimeByList> list) {
        RecyclerMissionProgressAdapter adapter = new RecyclerMissionProgressAdapter(this, list);
        //adapter.notifyDataSetChanged();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


    }

    private void makeCircularProgressBar(int date, int total_dates) {


// Set Progress
        // circularProgressBar.setProgress(1f);
// or with animation
        if (date == 0) {
            circularProgressBar.setProgressWithAnimation(total_dates * 0.02f, (long) 1000);
        } else {

            circularProgressBar.setProgressWithAnimation(date, (long) 1000); // =1s
        }

// Set Progress Max
        circularProgressBar.setProgressMax(total_dates);

// Set ProgressBar Color
        circularProgressBar.setProgressBarColor(getResources().getColor(R.color.colorPrimary));

// or with gradient
        //circularProgressBar.setProgressBarColorStart(Color.GRAY);
        //circularProgressBar.setProgressBarColorEnd(Color.RED);
        //circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set background ProgressBar Color
        circularProgressBar.setBackgroundProgressBarColor(getResources().getColor(R.color.wall_paper2));
// or with gradient
        //circularProgressBar.setBackgroundProgressBarColorStart(Color.WHITE);
        //circularProgressBar.setBackgroundProgressBarColorEnd(Color.RED);
        //circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set Width
        circularProgressBar.setProgressBarWidth(14f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(10f); // in DP

// Other
        circularProgressBar.setRoundBorder(true);
        //circularProgressBar.setStartAngle(180f);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


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


    private void displayPenaltyContent() {
        String user_name = getSharedPreferences("kakao_profile", MODE_PRIVATE).getString("name", "");
        String content = user_name + "님이 '" + mission_title + "' 미션에 실패하셨습니다. 테스트중입니다.";
        penalty_content.setText(content);


    }


    private void getIntents() {
        itemForDateTimeByLists = new ArrayList<>();

        Intent intent = getIntent();

        MissionInfoList item = (MissionInfoList) intent.getSerializableExtra("mission_info_list");

        if (item != null) {

            String address = item.getAddress();
            mission_latitude = item.getLat();
            mission_longitude = item.getLng();
            String min_date = item.getMin_date();
            String max_date = item.getMax_date();
            String mission_title = item.getMission_title();
            //total_dates = item.getMission_dates().size();
            friendsLists = item.getFriends_selected_list();
            for (int i = 0; i < friendsLists.size(); i++) {
                Log.d("친구", friendsLists.get(i).getFriend_name());
                Log.d("친구", friendsLists.get(i).getPhone_num());

            }

            setRecyclerViewFriendsList(friendsLists);


            mission_date_start.setText(DateTimeUtils.makeDateSimple(min_date));
            mission_date_end.setText(DateTimeUtils.makeDateSimple(max_date));
            address_result.setText(address);
            toolbar.setTitle(mission_title);
            setSupportActionBar(toolbar);


            Map<String, ItemForDateTimeByList> map = item.getMission_dates();

            for (ItemForDateTimeByList object : map.values()) {

                Collections.addAll(itemForDateTimeByLists, object);
                Collections.sort(itemForDateTimeByLists, new Comparator<ItemForDateTimeByList>() {
                    @Override
                    public int compare(ItemForDateTimeByList o1, ItemForDateTimeByList o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                //Collections.add
                //itemForDateTimeByLists.add(object);
            }

            mission_time_start.setText(DateTimeUtils.makeTimeSimple(itemForDateTimeByLists.get(0).getTime()));
            mission_time_end.setText(DateTimeUtils.makeTimeSimple(itemForDateTimeByLists.get(itemForDateTimeByLists.size() - 1).getTime()));


            int count = 0;
            cal = new HashSet<CalendarDay>();
            for (int i = 0; i < itemForDateTimeByLists.size(); i++) {


                int year = itemForDateTimeByLists.get(i).getYear();
                int month = itemForDateTimeByLists.get(i).getMonth();
                int day = itemForDateTimeByLists.get(i).getDay();

                String date = itemForDateTimeByLists.get(i).getDate();
                String time = itemForDateTimeByLists.get(i).getTime();
                cal.add(CalendarDay.from(year, month, day));
                Log.d("아이템바이", itemForDateTimeByLists.get(i).getDate() + "/" + itemForDateTimeByLists.get(i).getTime());
                if (!DateTimeUtils.compareIsFuture(date + time)) {
                    count++;
                }


            }
            tv_date_progress.setText(count + "/" + item.getMission_dates().size() + "일");
            makeCircularProgressBar(count, itemForDateTimeByLists.size());
            setRecyclerView(itemForDateTimeByLists);
            addMapFragment(new MapFragment(mainScrollView, mission_latitude, mission_longitude));
        }
        /*

        Bundle extras = getIntent().getExtras();
        if (extras != null) {





            mission_time = extras.getString("time");
            String mission_id = extras.getString("mission_id");
            Log.d("아이템바이", mission_id);
            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            if (!Utils.isEmpty(mission_id) && !Utils.isEmpty(user_id))
                databaseReference.child("user_data").child(user_id).child("mission_info_lit").child(mission_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ItemForDateTimeByList> itemForDateTimeByLists = new ArrayList<>();
                        Log.d("아이템바이", dataSnapshot.exists() + "");
                        if (dataSnapshot.exists()) {


                            MissionInfoList item = dataSnapshot.getValue(MissionInfoList.class);

                            String address = item.getAddress();
                            mission_latitude = item.getLat();
                            mission_longitude = item.getLng();
                            String min_date = item.getMin_date();
                            String max_date = item.getMax_date();
                            String mission_title = item.getMission_title();
                            //total_dates = item.getMission_dates().size();
                            friendsLists = item.getFriends_selected_list();
                            for (int i = 0; i < friendsLists.size(); i++) {
                                Log.d("친구", friendsLists.get(i).getFriend_name());
                                Log.d("친구", friendsLists.get(i).getPhone_num());

                            }

                            setRecyclerViewFriendsList(friendsLists);


                            mission_date_start.setText(DateTimeUtils.makeDateSimple(min_date));
                            mission_date_end.setText(DateTimeUtils.makeDateSimple(max_date));
                            address_result.setText(address);
                            toolbar.setTitle(mission_title);
                            setSupportActionBar(toolbar);


                            Map<String, ItemForDateTimeByList> map = item.getMission_dates();

                            for (ItemForDateTimeByList object : map.values()) {

                                Collections.addAll(itemForDateTimeByLists, object);
                                Collections.sort(itemForDateTimeByLists, new Comparator<ItemForDateTimeByList>() {
                                    @Override
                                    public int compare(ItemForDateTimeByList o1, ItemForDateTimeByList o2) {
                                        return o1.getDate().compareTo(o2.getDate());
                                    }
                                });
                                //Collections.add
                                //itemForDateTimeByLists.add(object);
                            }

                            mission_time_start.setText(DateTimeUtils.makeTimeSimple(itemForDateTimeByLists.get(0).getTime()));
                            mission_time_end.setText(DateTimeUtils.makeTimeSimple(itemForDateTimeByLists.get(itemForDateTimeByLists.size() - 1).getTime()));

                            int count = 0;
                            for (int i = 0; i < itemForDateTimeByLists.size(); i++) {
                                String date = itemForDateTimeByLists.get(i).getDate();
                                String time = itemForDateTimeByLists.get(i).getTime();
                                Log.d("아이템바이", itemForDateTimeByLists.get(i).getDate() + "/" + itemForDateTimeByLists.get(i).getTime());
                                if (!DateTimeUtils.compareIsFuture(date + time)) {
                                    count++;
                                }


                            }
                            tv_date_progress.setText(count + "/" + item.getMission_dates().size() + "일");
                            makeCircularProgressBar(count, itemForDateTimeByLists.size());
                            setRecyclerView(itemForDateTimeByLists);
                            addMapFragment(new MapFragment(mainScrollView,mission_latitude,mission_longitude));
                        }else{
                            Toast.makeText(getApplicationContext(),"데이터를 불러오는데 실패했습니다.",Toast.LENGTH_LONG).show();
                            finish();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        finish();
                    }
                });




        }

         */
    }


    private void setRecyclerViewFriendsList(List<ContactItemForServer> friendsLists) {
        RecyclerDetailFriendsAdapter adapter = new RecyclerDetailFriendsAdapter(friendsLists);
        recyclerView_friends_list.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_friends_list.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }


    private void addMapFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_layout, fragment).commit();
    }
}
