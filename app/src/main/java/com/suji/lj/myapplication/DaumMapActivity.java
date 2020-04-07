package com.suji.lj.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.Adapters.PlaceRecyclerAdapter;
import com.suji.lj.myapplication.Items.PlaceItem;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.suji.lj.myapplication.Utils.Utils;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

import retrofit2.http.Query;

public class DaumMapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, Button.OnClickListener {

    private MapView mapView;
    private MapReverseGeoCoder mapReverseGeoCoder = null;
    private TextView address_result;
    private Boolean mLocationPermissionsGranted = true;
    private static final String TAG = "DaumMapActivity";
    double Current_Latitude;
    double Current_Longitude;
    double Mission_Latitude;
    double Mission_Longitude;
    String foundedAddress;
    private List<PlaceItem> placeList;
    private RecyclerView recyclerView;
    PlaceRecyclerAdapter recyclerAdapter;
    public static Context mContext;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int address_founded;
    TextView location_loading;
    ImageView location_loaded;
    TextView address_confirmed_recycler;
    RelativeLayout map_layout;
    Bitmap bm;
    LinearLayout map_capture_layout;
    LinearLayout daum_map_loot_view;

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_map);
        placeList = new ArrayList<>();
        mContext = this;




        recyclerView = findViewById(R.id.place_recycler);
        mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        location_loading = findViewById(R.id.daum_map_location_loading);
        location_loaded = findViewById(R.id.daum_map_location_loaded);
        map_layout = findViewById(R.id.map_layout);
        map_capture_layout = findViewById(R.id.map_capture_layout);
        daum_map_loot_view = findViewById(R.id.daum_map_loot_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.daum_map_toolbar);
        toolbar.setTitle("장소 선택");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        onAddressSearch();

        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        CardView radius_button = findViewById(R.id.radius);
        radius_button.setOnClickListener(this);
        TextView select_completed = findViewById(R.id.address_confirmed_recycler);
        select_completed.setOnClickListener(this);
    }


    private void onAddressSearch() {



        SearchView searchView = findViewById(R.id.daum_map_search);
        //SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("장소,주소 검색..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mapView.removeAllPOIItems();
                placeList.clear();
                loadAddress(query);
                final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (300 * scale + 0.5f);
                map_layout.getLayoutParams().height = pixels;
                searchView.clearFocus();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return true;
            }
        });

    }


    public void moveSelectedPlace(double lat, double lng) {
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng)));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radius:

                if (mapView.isShowingCurrentLocationMarker()) {
                    if((Current_Latitude!=0.0)&&(Current_Longitude!=0.0)) {
                        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(Current_Latitude, Current_Longitude)));
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"위치정보를 수신중입니다.",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.address_confirmed_recycler:



                CheckTypesTask task2 = new CheckTypesTask();
                task2.execute();
                break;


        }
    }

    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";


        @GET("v2/local/search/keyword.json?")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }

    private void setupRecyclerView(List<PlaceItem> placeList) {

        //recyclerAdapter = new PlaceRecyclerAdapter(placeList, this);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();

    }


    //oncreate

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        Log.d("주소", "언제 실행되냐");

        onFinishReverseGeoCoding(s);

        Intent intent = new Intent(getApplicationContext(), SingleModeActivity.class);
        intent.putExtra("address",s );
        intent.putExtra("lat",Mission_Latitude);
        intent.putExtra("lng",Mission_Longitude);
        setResult(1,intent);

        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lat = String.format("%.6f", Mission_Latitude);
        String lng = String.format("%.6f", Mission_Longitude);
        editor.putString("lat", lat);
        editor.putString("lng", lng);
        editor.putString("address", s);
        editor.putInt("validCode", 777);
        editor.apply();
        editor.commit();

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
        address_founded = 666;

    }

    //currentlocation
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, v));
        Current_Latitude = mapPointGeo.latitude;
        Current_Longitude = mapPointGeo.longitude;
        location_loading.setVisibility(View.GONE);
        location_loaded.setVisibility(View.VISIBLE);

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
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

    }


    //onMap
    @Override
    public void onMapViewInitialized(MapView mapView) {


        //mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), DaumMapActivity.this, DaumMapActivity.this);
        //mapReverseGeoCoder.startFindingAddress();
        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        Mission_Latitude = geoCoordinate.latitude; // 위도
        Mission_Longitude = geoCoordinate.longitude; // 경도
        String lat = getSharedPreferences("sFile",MODE_PRIVATE).getString("lat",String.valueOf(geoCoordinate.latitude));
        String lng = getSharedPreferences("sFile",MODE_PRIVATE).getString("lng",String.valueOf(geoCoordinate.longitude));
        Mission_Latitude = Double.valueOf(lat);
        Mission_Longitude = Double.valueOf(lng);
        mapView.removeAllCircles();
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(Mission_Latitude, Mission_Longitude), // center
                100, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle.setTag(5678);//태그는 왜달지
        mapView.addCircle(circle);


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
        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        Mission_Latitude = geoCoordinate.latitude; // 위도
        Mission_Longitude = geoCoordinate.longitude; // 경도

        mapView.removeAllCircles();
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(Mission_Latitude, Mission_Longitude), // center
                100, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle.setTag(5678);//태그는 왜달지
        mapView.addCircle(circle);

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
                                ActivityCompat.requestPermissions(DaumMapActivity.this,
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
    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                DaumMapActivity.this);

        @Override
        protected void onPreExecute() {
            mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), DaumMapActivity.this, DaumMapActivity.this);
            mapReverseGeoCoder.startFindingAddress();



            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {


                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(1300);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            finish();
            super.onPostExecute(result);
        }
    }

    private void loadAddress(String query){
        final Retrofit retrofit2 = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create()).
                baseUrl(KeywordSearch.base).
                build();
        KeywordSearch keywordSearch = retrofit2.create(KeywordSearch.class);
        Call<JsonObject> call = keywordSearch.keywordSearch(KeywordSearch.key, query);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String con = new GsonBuilder().setPrettyPrinting().create().toJson(response.body());
                Log.d(TAG, "검색" + con);
                try {

                    JSONObject obj = new JSONObject(con);
                    String check_string = obj.getJSONObject("meta").getString("total_count");
                    if (Integer.valueOf(check_string) == 0) {
                        PlaceItem placeItem = new PlaceItem();
                        placeItem.setPlaceName(query + "(와)과 일치하는 검색결과가 없습니다");
                        placeList.add(placeItem);
                    } else {
                        JSONArray jsonArray = obj.getJSONArray("documents");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PlaceItem placeItem = new PlaceItem();
                            JSONObject object = (JSONObject) jsonArray.get(i);
                            if ((object.getString("road_address_name") == null)) {
                                placeItem.setRoadAddress(object.getString("address_name"));
                                placeItem.setOldAddress(" ");

                            } else {
                                placeItem.setRoadAddress(object.getString("road_address_name"));
                                placeItem.setOldAddress(object.getString("address_name"));
                            }
                            placeItem.setPlaceName(object.getString("place_name"));
                            placeItem.setLatitude(object.getDouble("y"));
                            placeItem.setLongitude(object.getDouble("x"));
                            MapPOIItem customMarker = new MapPOIItem();
                            customMarker.setItemName(object.getString("place_name"));
                            customMarker.setTag(1);
                            customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(object.getDouble("y"), object.getDouble("x")));
                            customMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 마커타입을 커스텀 마커로 지정.
                            customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);


                            mapView.addPOIItem(customMarker);
                            Log.d("장소", object.getString("place_name"));


                            placeList.add(placeItem);

                            //  String address = ((JSONObject) tempObj.get("address")).get("address_name").toString();



                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setupRecyclerView(placeList);


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getApplicationContext(),"주소를 불러올 수 없습니다.",Toast.LENGTH_LONG).show();

            }
        });

    }







    /*
        Retrofit retrofit = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create()).
                baseUrl(GpsAddress.base).
                build();
        GpsAddress gpsAddress = retrofit.create(GpsAddress.class);
        Call<JsonObject> pro = gpsAddress.getAddress(GpsAddress.key, String.valueOf(Mission_Longitude), String.valueOf(Mission_Latitude));

        pro.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    String result = response.body().toString();
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObj = (JSONObject) jsonParser.parse(result);

                    JSONArray memberArray = (JSONArray) jsonObj.get("documents");


                    for (int i = 0; i < memberArray.size(); i++) {
                        JSONObject tempObj = (JSONObject) memberArray.get(i);

                        String address = ((JSONObject) tempObj.get("address")).get("address_name").toString();
                        address_result.setText(address);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }

        });

    }

    public interface GpsAddress {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";

        @GET("v2/local/geo/coord2address.json")
        Call<JsonObject> getAddress(@Header("Authorization") String key, @Query("x") String lon, @Query("y") String lat);
    }
    */

}
