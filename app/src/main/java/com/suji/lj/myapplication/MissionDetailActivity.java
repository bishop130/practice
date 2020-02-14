package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class MissionDetailActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener, View.OnClickListener {
    MapView mapView;
    //MaterialCalendarView materialCalendarView;
    CardView btn_current_location;
    CardView btn_mission_location;
    private String mission_title;
    private double mission_latitude;
    private double mission_longitude;
    private String mission_id;
    private String mission_time;
    private String address;
    private String contact_list;
    private String mission_date_array;
    private String completed_dates;
    private double current_latitude;
    private double current_longitude;
    private String is_failed;
    ScrollView mainScrollView;
    ImageView transparentImageView;
    ArrayList<CalendarDay> dates = new ArrayList<>();
    ArrayList<CalendarDay> completed_date_array = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.KOREA);
    TextView address_result;
    TextView penalty_name_list;
    TextView mission_date_start;
    TextView mission_date_end;
    TextView penalty_content;
    TextView mission_time_start;
    TextView mission_time_end;
    List<ContactItemForServer> friendsLists;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        getIntents();

        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view_detail);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);


        toolbar = findViewById(R.id.mission_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        address_result = findViewById(R.id.address_detail);

        btn_current_location = findViewById(R.id.btn_current_location);
        btn_mission_location = findViewById(R.id.btn_mission_location);
        mainScrollView = findViewById(R.id.main_scroll_view);
        transparentImageView = findViewById(R.id.transparent_image);
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
        checkLocationPermission();

        //some_image.setImageBitmap(getBitmapFromView(test));

        //Log.d("안될듯",viewToBitmap().toString());
        btn_current_location.setOnClickListener(this);
        btn_mission_location.setOnClickListener(this);


        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }

        });


        //query();


    }

    private void setRecyclerView(List<ItemForDateTimeByList> list) {
        RecyclerMissionProgressAdapter adapter = new RecyclerMissionProgressAdapter(list);
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


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mission_time = extras.getString("time");
            String mission_id = extras.getString("mission_id");
            Log.d("아이템바이", mission_id);
            String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            if (!Utils.isEmpty(mission_id) && !Utils.isEmpty(user_id))
                databaseReference.child("user_data").child(user_id).child("mission_info_list").child(mission_id).addValueEventListener(new ValueEventListener() {
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
                            for(int i =0; i<friendsLists.size();i++){
                                Log.d("친구",friendsLists.get(i).getFriend_name());
                                Log.d("친구",friendsLists.get(i).getPhone_num());

                            }

                            setRecyclerViewFriendsList(friendsLists);


                            mission_date_start.setText(DateTimeUtils.makeDateForHuman(min_date));
                            mission_date_end.setText(DateTimeUtils.makeDateForHuman(max_date));
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

                            mission_time_start.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(0).getTime()));
                            mission_time_end.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(itemForDateTimeByLists.size() - 1).getTime()));

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
                            drawMissionRadius(mission_latitude, mission_longitude);
                            moveSelectedPlace(mission_latitude,mission_longitude);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        }
    }

    private void drawMissionRadius(double lat, double lng) {
        MapCircle circle2 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(lat, lng), // center
                100, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle2.setTag(5678);
        mapView.addCircle(circle2);

    }
    private void setRecyclerViewFriendsList(List<ContactItemForServer> friendsLists){
        RecyclerDetailFriendsAdapter adapter = new RecyclerDetailFriendsAdapter(friendsLists);
        recyclerView_friends_list.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_friends_list.setAdapter(adapter);


    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        current_latitude = mapPointGeo.latitude;
        current_longitude = mapPointGeo.longitude;

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mission_latitude, mission_longitude), 2, true);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void moveSelectedPlace(double lat, double lng) {
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng)));

    }

    private void moveCurrentLocation() {
        if (mapView.isShowingCurrentLocationMarker()) {
            if ((current_latitude != 0.0) && (current_longitude != 0.0)) {
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude)));
            }
        } else {
            Toast.makeText(getApplicationContext(), "위치정보를 수신중입니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_current_location:
                moveCurrentLocation();
                break;
            case R.id.btn_mission_location:
                moveSelectedPlace(mission_latitude, mission_longitude);
                break;


        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");

            mapView.setCurrentLocationEventListener(this);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MissionDetailActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d("퍼미션", "여기는 왜 못들어와");

                        mapView.setCurrentLocationEventListener(this);
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
