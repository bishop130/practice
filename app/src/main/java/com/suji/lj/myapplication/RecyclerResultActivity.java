package com.suji.lj.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;

import com.suji.lj.myapplication.Adapters.BroadCastAlarm;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;

import com.suji.lj.myapplication.Items.AlarmItem;

import net.daum.android.map.location.MapViewLocationManager;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;

import net.daum.mf.map.api.MapPoint;

import net.daum.mf.map.api.MapView;


import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Objects;


public class RecyclerResultActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    private StringRequest request;
    private static final String goURL = "http://bishop130.cafe24.com/Mission_Control.php";
    private RequestQueue requestQueue;

    private MapView mapView;
    private static final String TAG = "RecyclerResultActivity";
    double Current_Latitude = 0.0;
    double Current_Longitude = 0.0;

    String user_id;
    String mission_title;
    String mission_id;
    Double Latitude;
    Double Longitude;
    String mission_time;
    String mission_date;
    String mission_date_time;
    boolean update_location_code = false;
    TextView check_time;
    TextView check_location;
    TextView check_date;
    TextView text_if_success;
    TextView location_loading;
    ImageView unchecked_time_icon;
    ImageView unchecked_location_icon;
    ImageView unchecked_date_icon;
    ImageView location_founded;
    DateTimeFormatter dtf;
    CountDownTimer date_time_timer;
    CountDownTimer time_timer;
    String is_failed;

    Boolean mission_success;
    LinearLayout layout_if_success;
    LinearLayout layout_if_success_not;
    private MapViewLocationManager mapViewLocationManager;
    List<AlarmItem> alarmItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mission_title = extras.getString("MissionTitle");
            mission_id = extras.getString("MissionID");
            Latitude = extras.getDouble("Latitude");
            Longitude = extras.getDouble("Longitude");
            mission_time = extras.getString("mission_time");
            mission_date = extras.getString("mission_date");
            mission_success = extras.getBoolean("mission_success");
            mission_date_time = extras.getString("mission_date_time");
            is_failed = extras.getString("is_failed");
        }


        mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view2);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);

        requestQueue = Volley.newRequestQueue(this);
        //dbHelper = new DBHelper(this, "alarm_manager.db", null, 5);
        dtf = new DateTimeFormatter();

        //setMyLocation();

        Toolbar toolbar = findViewById(R.id.recycler_result_toolbar);
        toolbar.setTitle("위치등록");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        check_time = findViewById(R.id.check_time);
        check_location = findViewById(R.id.check_location);
        check_date = findViewById(R.id.check_date);
        unchecked_time_icon = findViewById(R.id.unchecked_time_icon);
        unchecked_location_icon = findViewById(R.id.unchecked_location_icon);
        unchecked_date_icon = findViewById(R.id.unchecked_date_icon);
        unchecked_location_icon.setImageResource(R.drawable.unknown_icon);
        text_if_success = findViewById(R.id.text_if_success);
        layout_if_success = findViewById(R.id.layout_if_success);
        layout_if_success_not = findViewById(R.id.check_layout);
        location_loading = findViewById(R.id.location_loading);
        location_founded = findViewById(R.id.location_founded);

        CardView btn_current_location = findViewById(R.id.btn_current_location);
        CardView btn_mission_location = findViewById(R.id.btn_mission_location);
        Button register_location = findViewById(R.id.register_location);

        //mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true);


        locationLogAnalystic();
        drawMissionRadius();
        mapViewLocationManager = MapViewLocationManager.getInstance();


        boolean failed = Boolean.valueOf(is_failed);
        Log.d("까보자", ""+is_failed);
        Log.d("까보자", ""+failed);
        if(is_failed.equals("1")) {

            layout_if_success.setVisibility(View.VISIBLE);
            text_if_success.setText("실패");
            register_location.setVisibility(View.INVISIBLE);
            layout_if_success_not.setVisibility(View.INVISIBLE);

        }
        else {
            if (mission_success) {
                layout_if_success.setVisibility(View.VISIBLE);
                text_if_success.setText("성공");
                layout_if_success_not.setVisibility(View.INVISIBLE);
                register_location.setVisibility(View.INVISIBLE);


            } else {
                Date is_success_date_time = dtf.dateTimeParser(mission_date_time,"yyyyMMddHHmm");

                if (is_success_date_time.after(new Date(System.currentTimeMillis()))) {
                    register_location.setVisibility(View.VISIBLE);
                    onCheckTimer();
                }
            }
        }

        // SharedPreferences sharedPreferences = getSharedPreferences("location",MODE_PRIVATE);
        //Current_Latitude = Double.valueOf(sharedPreferences.getString("latitude",""));
        //Current_Longitude = Double.valueOf(sharedPreferences.getString("longitude",""));


        btn_mission_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(Latitude, Longitude)));
                mapViewLocationManager.requestLocationUpdate();
            }
        });

        btn_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("isShowingCurrent", String.valueOf(mapView.isShowingCurrentLocationMarker()));
                if (mapView.isShowingCurrentLocationMarker()) {
                    if ((Current_Latitude != 0.0) && (Current_Longitude != 0.0)) {
                        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(Current_Latitude, Current_Longitude)));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "위치정보를 수신중입니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

        register_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapView.isShowingCurrentLocationMarker() && update_location_code) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecyclerResultActivity.this);
                    builder.setTitle("AlertDialog Title");
                    builder.setMessage("AlertDialog Content");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();
                                    CheckTypesTask task = new CheckTypesTask();
                                    task.execute();

                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();


                }


            }
        });
    }

    private void locationLogAnalystic() {
        alarmItemList.clear();

        String sql = "SELECT * FROM main_table WHERE mission_id = '" + mission_id + "' AND date_time = '" + mission_date_time + "'";
        for (int i = 0; i < alarmItemList.size(); i++) {

            String result = alarmItemList.get(i).getIs_success();
            String title = alarmItemList.get(i).getTitle();

            Log.d("위치로그분석", result + "\n" + title);
        }


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


    private void drawMissionRadius() {
        MapCircle circle2 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(Latitude, Longitude), // center
                100, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle2.setTag(5678);
        mapView.addCircle(circle2);

    }

    private void getIntents() {


    }


    @Override
    public void onMapViewInitialized(MapView mapView) {

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true);

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
        Log.d(TAG, "onMapViewMoveFinished");


    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        Current_Latitude = mapPointGeo.latitude;
        Current_Longitude = mapPointGeo.longitude;
        update_location_code = true;
        location_loading.setVisibility(View.GONE);
        location_founded.setVisibility(View.VISIBLE);

        if ((Current_Latitude - Latitude) * (Current_Latitude - Latitude) + (Current_Longitude - Longitude) * (Current_Longitude - Longitude) < 0.0007 * 0.0007) {
            unchecked_location_icon.setImageResource(R.drawable.checked_icon);
            check_location.setText("위치 확인");

        } else {
            unchecked_location_icon.setImageResource(R.drawable.unchecked_icon);
            check_location.setText("지정된 장소가 아닙니다.");
        }

        //Toast.makeText(getApplicationContext(), String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters), Toast.LENGTH_LONG).show();


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

    private void onCheckTimer() {

        Date cur_date_time = new Date(System.currentTimeMillis());
        Date set_mission_date = DateTimeFormatter.dateParser(mission_date,"yyyyMMdd");
        Date set_mission_date_time = dtf.dateTimeParser(mission_date_time,"yyyyMMddHHmm");

        date_time_timer = new CountDownTimer((set_mission_date.getTime() + 86400 * 1000) - cur_date_time.getTime(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished > 0) && (millisUntilFinished < 86400 * 1000)) {
                    unchecked_date_icon.setImageResource(R.drawable.checked_icon);
                } else {
                    unchecked_date_icon.setImageResource(R.drawable.unchecked_icon);
                    check_date.setText("지정된 날이 아닙니다.");
                }
            }

            @Override
            public void onFinish() {
                unchecked_date_icon.setImageResource(R.drawable.unchecked_icon);
            }
        };
        date_time_timer.start();

        time_timer = new CountDownTimer((set_mission_date_time.getTime() - cur_date_time.getTime()), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished >= 0) && (millisUntilFinished < 7200 * 1000)) {//2시간 제한

                    unchecked_time_icon.setImageResource(R.drawable.checked_icon);
                } else {
                    unchecked_time_icon.setImageResource(R.drawable.unchecked_icon);
                    check_time.setText("지정된 시간이 아닙니다.");
                }
            }

            @Override
            public void onFinish() {
                unchecked_time_icon.setImageResource(R.drawable.unchecked_icon);
                check_time.setText("지정된 시간이 아닙니다.");
            }
        };
        time_timer.start();


    }

    private void checkMission() {
        SharedPreferences sharedPreferences = getSharedPreferences("Kakao", MODE_PRIVATE);
        final String user_id = sharedPreferences.getString("token", "");


        Date set_mission_date = DateTimeFormatter.dateParser(mission_date,"yyyyMMdd");
        Date set_mission_time = dtf.timeParser(mission_time);

        try {
            Date cur_time = dtf.timeFormatter(new Date(System.currentTimeMillis()));
            Date cur_date = dtf.dateFormatter(new Date(System.currentTimeMillis()));


            if ((set_mission_date.getTime() - cur_date.getTime()) == 0) {//당일 확인 현재날짜 - 설정날짜
                long diff = set_mission_time.getTime() - cur_time.getTime();//시간확인 설정시간 - 현재시간
                if (((diff >= 0) && (diff < 1000 * 60 * 60 * 2)) && ((Current_Latitude - Latitude) * (Current_Latitude - Latitude) + (Current_Longitude - Longitude) * (Current_Longitude - Longitude) < 0.0005 * 0.0005)) {


                    request = new StringRequest(Request.Method.POST, goURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //cancelAlarm();
                            instantAlarm(mission_title, "위치등록에 성공했습니다.");

                            Intent intent = new Intent(RecyclerResultActivity.this, MainActivity.class);
                            startActivity(intent);


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("mission_id", mission_id);
                            hashMap.put("mission_date", mission_date);
                            hashMap.put("user_id", user_id);

                            return hashMap;
                        }
                    };
                    requestQueue.add(request);

                } else if (diff > 1000 * 60 * 60 * 2) {
                    check_time.setText("지정된 시간이 아닙니다.");
                    unchecked_time_icon.setImageResource(R.drawable.unchecked_icon);


                } else if ((Current_Latitude - Latitude) * (Current_Latitude - Latitude) + (Current_Longitude - Longitude) * (Current_Longitude - Longitude) > 0.0005 * 0.0005) {
                    Toast.makeText(getApplicationContext(), "위치실패", Toast.LENGTH_LONG).show();
                    check_location.setText("지정된 장소가 아닙니다.");
                    unchecked_location_icon.setImageResource(R.drawable.unchecked_icon);
                }
            } else {
                Toast.makeText(getApplicationContext(), "날짜가 다릅니다", Toast.LENGTH_LONG).show();
                check_date.setText("지정된 날짜가 아닙니다.");
                unchecked_date_icon.setImageResource(R.drawable.unchecked_icon);

            }

        } catch (
                Exception e) {
            Log.e("RecyclerResultActivity", "Error!" + e);

        }


    }

    @Override
    protected void onResume() {
        checkLocationPermission();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy()");
       // mapView.setShowCurrentLocationMarker(false);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "onPause()");

        SharedPreferences.Editor editor = getSharedPreferences("location", MODE_PRIVATE).edit();
        editor.putString("latitude", String.valueOf(Current_Latitude));
        editor.putString("longitude", String.valueOf(Current_Longitude));
        editor.apply();

    }

    private void instantAlarm(String title, String content) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BroadCastAlarm.class);
        intent.putExtra("title", title);
        intent.putExtra("is_success", content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (new Date()).getTime() + 5000, pendingIntent);

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");

            mapView.setCurrentLocationEventListener(this);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

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
                                ActivityCompat.requestPermissions(RecyclerResultActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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

                        Toast.makeText(getApplicationContext(),"requestPermission",Toast.LENGTH_LONG).show();
                        mapView.setCurrentLocationEventListener(this);
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                RecyclerResultActivity.this);

        @Override
        protected void onPreExecute() {
            checkMission();
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                for (int i = 0; i < 5; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }

    }

}
