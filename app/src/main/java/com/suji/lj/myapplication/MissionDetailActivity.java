package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.suji.lj.myapplication.Adapters.DateTimeFormatter;
import com.suji.lj.myapplication.Decorators.EventDecorator;
import com.suji.lj.myapplication.Decorators.SelectorDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MissionDetailActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    MapView mapView;
    MaterialCalendarView materialCalendarView;
    CardView button_location;
    CardView button_mission_location;
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
    DateTimeFormatter dtf = new DateTimeFormatter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        getIntents();

        mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view_detail);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);


        Toolbar toolbar = findViewById(R.id.mission_detail_toolbar);
        toolbar.setTitle(mission_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        materialCalendarView = findViewById(R.id.detail_calendar);
        address_result = findViewById(R.id.address_detail);
        address_result.setText(address);
        button_location = findViewById(R.id.myLocation_detail);
        button_mission_location = findViewById(R.id.mission_location_detail);
        mainScrollView = findViewById(R.id.main_scroll_view);
        transparentImageView = findViewById(R.id.transparent_image);
        penalty_name_list = findViewById(R.id.penalty_name_list);
        mission_date_start = findViewById(R.id.mission_date_start);
        mission_date_end = findViewById(R.id.mission_date_end);
        penalty_content = findViewById(R.id.penalty_content);


        setMaterialCalendarView();
        drawMissionRadius();
        displayContactList();
        displayPenaltyContent();
        setMinMaxDate();
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapView.isShowingCurrentLocationMarker()) {
                    if ((current_latitude != 0.0) && (current_longitude != 0.0)) {
                        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude)));
                    }
                }

            }
        });
        button_mission_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(mission_latitude, mission_longitude)));
            }
        });


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


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayPenaltyContent(){
        String user_name = getSharedPreferences("kakao_profile", MODE_PRIVATE).getString("name", "");
        String content = user_name+"님이 '"+mission_title+"' 미션에 실패하셨습니다. 테스트중입니다.";
        penalty_content.setText(content);

    }

    private void setMinMaxDate() {

        List<String> date_array = Arrays.asList(mission_date_array.split("\\s*,\\s*"));
        int date_count = 0;
        Date max_date = dtf.dateParser(date_array.get(0));
        Date min_date = dtf.dateParser(date_array.get(0));

        for (int i = 0; i < date_array.size(); i++) {
            String date = date_array.get(i);
            Log.d("date_check", date);
            date_count++;

            Date date_arr = dtf.dateParser(date);
            if (date_arr.getTime() >= max_date.getTime()) {
                max_date = date_arr;
            }
            if (date_arr.getTime() <= min_date.getTime()) {
                min_date = date_arr;
            }
        }

        Log.d("date_check_count", String.valueOf(date_count));
        mission_date_start.setText(dtf.dateReadable(min_date));
        mission_date_end.setText(dtf.dateReadable(max_date));

        //holder.range_date.setText(sdf_array.format(min_date) + "~" + sdf_array.format(max_date));


    }



    private void getIntents() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mission_title = extras.getString("MissionTitle");
            mission_id = extras.getString("MissionID");
            mission_latitude = extras.getDouble("Latitude");
            mission_longitude = extras.getDouble("Longitude");
            mission_time = extras.getString("mission_time");
            mission_date_array = extras.getString("mission_date_array");
            address = extras.getString("address");
            contact_list = extras.getString("contact_list");
            completed_dates = extras.getString("completed_dates");
            is_failed = extras.getString("is_failed");
        }
    }

    private void drawMissionRadius() {
        MapCircle circle2 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(mission_latitude, mission_longitude), // center
                100, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle2.setTag(5678);
        mapView.addCircle(circle2);

    }

    private void displayContactList() {
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(contact_list);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                sb.append(jsonObject.getString("name")+" : "+jsonObject.getString("num")+"\n");
                //sb.append(",");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        String result = sb.toString();
        int last = result.length() - 1;
        if (last > 0 && result.charAt(last) == ',') {
            result = result.substring(0, last);
        }
        */
        penalty_name_list.setText(sb.toString());
    }

    private void setMaterialCalendarView() {
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
        materialCalendarView.setBackgroundColor(Color.WHITE);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        final LocalDate localDate = LocalDate.now();
        materialCalendarView.setSelectedDate(localDate);
        materialCalendarView.addDecorator(new SelectorDecorator(this));



        try {

            List<String> date_array = Arrays.asList(mission_date_array.split("\\s*,\\s*"));
            for (int i = 0; i < date_array.size(); i++) {

                Date date = simpleDateFormat.parse(date_array.get(i));


                String day = (String) DateFormat.format("d", date); // 20
                String monthNumber = (String) DateFormat.format("M", date); // 6
                String year = (String) DateFormat.format("yyyy", date); // 2013
                CalendarDay eventDay2 = CalendarDay.from(Integer.valueOf(year), Integer.valueOf(monthNumber), Integer.valueOf(day));
                dates.add(eventDay2);
            }
            List<String> completed_array = Arrays.asList(completed_dates.split("\\s*,\\s*"));
            for (int i = 0; i < completed_array.size(); i++) {

                Date date = simpleDateFormat.parse(completed_array.get(i));


                String day = (String) DateFormat.format("d", date); // 20
                String monthNumber = (String) DateFormat.format("M", date); // 6
                String year = (String) DateFormat.format("yyyy", date); // 2013
                CalendarDay eventDay3 = CalendarDay.from(Integer.valueOf(year), Integer.valueOf(monthNumber), Integer.valueOf(day));
                completed_date_array.add(eventDay3);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(is_failed.equals("1")){
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
        }else{
            materialCalendarView.addDecorator(new EventDecorator(Color.YELLOW, dates));
        }
        materialCalendarView.addDecorator(new EventDecorator(Color.BLUE, completed_date_array));



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


}
