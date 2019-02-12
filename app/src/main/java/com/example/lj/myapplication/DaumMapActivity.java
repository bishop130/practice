package com.example.lj.myapplication;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;

import retrofit2.http.Query;

public class DaumMapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {

    private MapView mapView;
    private MapReverseGeoCoder mapReverseGeoCoder = null;
    private TextView address_result;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = true;
    private static final String TAG = "DaumMapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_map);


        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);

        address_result = (TextView) findViewById(R.id.address);


        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).
                baseUrl(GpsAddress.base).
                build();

        GpsAddress gpsAddress = retrofit.create(GpsAddress.class);
        Call<JsonObject> pro = gpsAddress.getAddress(GpsAddress.key, "127.0846157", "37.3101056");

        pro.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(), "JsonObject:" + response.body() , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }

        });

        Retrofit retrofit2 = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).
                baseUrl(KeywordSearch.base).
                build();

        KeywordSearch keywordSearch = retrofit2.create(KeywordSearch.class);
        Call<JsonObject> call = keywordSearch.keywordSearch(KeywordSearch.key,"순복음교회");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG,"response"+response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        Button button = (Button)findViewById(R.id.addressConfirmed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DaumMapActivity.this,TimePickerActivity.class);
                intent.putExtra("lat",String.valueOf(Latitude));
                intent.putExtra("lng",String.valueOf(Longitude));
                startActivity(intent);
            }
        });
    }

    //oncreate
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        //onFinishReverseGeoCoding("Fail");

    }

    //currentlocation
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

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


    private void onFinishReverseGeoCoding(String result) {


    }

    double Latitude;
    double Longitude;
    //onMap
    @Override
    public void onMapViewInitialized(MapView mapView) {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            Latitude = currentLocation.getLatitude();
                            Longitude = currentLocation.getLongitude();



                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


                        } else {
                            Toast.makeText(DaumMapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord( Latitude, Longitude), 4, true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), DaumMapActivity.this, DaumMapActivity.this);
        mapReverseGeoCoder.startFindingAddress();

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

        mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), DaumMapActivity.this, DaumMapActivity.this);
        mapReverseGeoCoder.startFindingAddress();
    }


    public interface GpsAddress {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";

        @GET("v2/local/geo/coord2address.json")
        Call<JsonObject> getAddress(@Header("Authorization") String key, @Query("x") String lon, @Query("y") String lat);
    }
    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";

        @GET("v2/local/search/keyword.json")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }


}
