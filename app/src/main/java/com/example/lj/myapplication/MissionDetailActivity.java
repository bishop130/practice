package com.example.lj.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.example.lj.myapplication.Decorators.EventDecorator;
import com.example.lj.myapplication.Decorators.SelectorDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    private String mission_date_array;
    private double current_latitude;
    private double current_longitude;
    ScrollView mainScrollView;
    ImageView transparentImageView;
    ArrayList<CalendarDay> dates = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        getIntents();

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view_detail);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        drawMissionRadius();

        Toolbar toolbar = findViewById(R.id.mission_detail_toolbar);
        toolbar.setTitle(mission_title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        materialCalendarView = findViewById(R.id.detail_calendar);
        setMaterialCalendarView();


        button_location = findViewById(R.id.myLocation_detail);
        button_mission_location = findViewById(R.id.mission_location_detail);


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
        mainScrollView = (ScrollView) findViewById(R.id.main_scroll_view);
        transparentImageView = (ImageView) findViewById(R.id.transparent_image);


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


    private void getIntents() {
        mission_title = getIntent().getExtras().getString("MissionTitle");
        mission_id = getIntent().getExtras().getString("MissionID");
        mission_latitude = getIntent().getExtras().getDouble("Latitude");
        mission_longitude = getIntent().getExtras().getDouble("Longitude");
        mission_time = getIntent().getExtras().getString("mission_time");
        mission_date_array = getIntent().getExtras().getString("mission_date_array");
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

    private void setMaterialCalendarView() {

        materialCalendarView.setBackgroundColor(Color.WHITE);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        final LocalDate localDate = LocalDate.now();
        materialCalendarView.setSelectedDate(localDate);
        materialCalendarView.addDecorator(new SelectorDecorator(this));

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
        }
        catch (ParseException e){
            e.printStackTrace();
        }
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));


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


}
