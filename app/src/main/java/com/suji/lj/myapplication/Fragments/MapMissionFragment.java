package com.suji.lj.myapplication.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.suji.lj.myapplication.Adapters.PlaceRecyclerAdapter;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static android.content.Context.MODE_PRIVATE;

public class MapMissionFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, View.OnClickListener, PlaceRecyclerAdapter.OnMoveSelectedPlaceListener {


    MapView mapView;
    PlaceRecyclerAdapter placeRecyclerAdapter;
    ImageView imageView;
    ScrollView mainScrollView;
    TextView location_loading;
    ImageView location_loaded;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //Context context;

    Activity activity;
    RelativeLayout map_layout;
    CardView current_location;
    SearchView searchView;

    double current_lat;
    double current_lng;
    NestedScrollView scrollView;
    private final int radius = 50;
    LinearLayout zoom_in;
    LinearLayout zoom_out;
    Switch no_time_switch;

    BottomSheetDialog dialog;
    ViewGroup mapViewContainer;


    Realm realm;

    private FusedLocationProviderClient fusedLocationClient;

    public MapMissionFragment(NestedScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map_mission, container, false);


        realm = Realm.getDefaultInstance();


        imageView = view.findViewById(R.id.transparent_image);
        location_loading = view.findViewById(R.id.location_loading);
        location_loaded = view.findViewById(R.id.location_loaded);
        current_location = view.findViewById(R.id.current_location);
        mapViewContainer = view.findViewById(R.id.map_view);
        map_layout = view.findViewById(R.id.map_layout);

        searchView = view.findViewById(R.id.search_view);
        zoom_in = view.findViewById(R.id.zoom_in);
        zoom_out = view.findViewById(R.id.zoom_out);
        no_time_switch = view.findViewById(R.id.no_time_switch);
        mapView = new MapView(activity);

        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(MapMissionFragment.this);


        mapView.setCurrentLocationEventListener(this);
        current_location.setOnClickListener(this);
        zoom_in.setOnClickListener(this);
        zoom_out.setOnClickListener(this);


        onAddressSearch();

        Utils.fixMapScroll(imageView, scrollView);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_location:
                moveCurrentLocation();
                break;
            case R.id.zoom_in:
                mapView.zoomIn(true);

                break;
            case R.id.zoom_out:
                mapView.zoomOut(true);

                break;


        }

    }

    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";


        @GET("v2/local/search/keyword.json?")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {


    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        current_lat = mapPointGeo.latitude;
        current_lng = mapPointGeo.longitude;


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

    @Override
    public void onMapViewInitialized(MapView mapView) {
        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();


        Log.d("위치", "초기화됨");
        boolean isLocationEnable = Utils.isLocationEnabled(activity);

        if (isLocationEnable) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            fusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        current_lat = location.getLatitude();
                        current_lng = location.getLongitude();

                        Log.d("위치", current_lat + "라스트");
                        Log.d("위치", current_lng + "라스트");
                        // Logic to handle location object


                    } else {
                        SharedPreferences preferences = activity.getSharedPreferences("location_setting", MODE_PRIVATE);
                        current_lat = Double.longBitsToDouble(preferences.getLong("lat", 0));
                        current_lng = Double.longBitsToDouble(preferences.getLong("lng", 0));
                    }
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(current_lat, current_lng), true);
                    mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(current_lat, current_lng)));
                    mapView.setZoomLevel(1, true);
                    drawCircle(current_lat, current_lng, radius);

                }
            });
        } else {/** 위치 꺼져있음**/

            Toast.makeText(activity, "위치가 꺼져있습니다. 위치설정을 켜주세요", Toast.LENGTH_SHORT).show();
            mapView.setZoomLevel(1, true);
            drawCircle(geoCoordinate.latitude, geoCoordinate.longitude, radius);

        }


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                missionCartItem.setLat(geoCoordinate.latitude);
                missionCartItem.setLng(geoCoordinate.longitude);
            }
        });


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
        drawCircle(geoCoordinate.latitude, geoCoordinate.longitude, radius);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("location_setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lat", Double.doubleToRawLongBits(geoCoordinate.latitude));
        editor.putLong("lng", Double.doubleToRawLongBits(geoCoordinate.longitude));
        editor.apply();

        if (!realm.isClosed()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                    missionCartItem.setLat(geoCoordinate.latitude);
                    missionCartItem.setLng(geoCoordinate.longitude);
                }
            });
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    private void onAddressSearch() {


        //SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("장소,주소 검색..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mapView.removeAllPOIItems();

                //createBottomSheetForAddress();

                loadAddress(query);
                final float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (300 * scale + 0.5f);
                //map_layout.getLayoutParams().height = pixels;
                searchView.clearFocus();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return true;
            }
        });

    }

    private void moveSelectedPlace(double mission_lat, double mission_lng) {

        if ((mission_lat != 0.0) && (mission_lng != 0.0)) {
            mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(mission_lat, mission_lng)));
        }

    }

    private void moveCurrentLocation() {
        if (mapView.isShowingCurrentLocationMarker()) {
            if ((current_lat != 0.0) && (current_lng != 0.0)) {
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(current_lat, current_lng)));
            }
        } else {
            Toast.makeText(activity, "위치정보를 수신중입니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void drawCircle(double lat, double lng, int radius) {
        mapView.removeAllCircles();
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(lat, lng), // center
                radius, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle.setTag(5678);//태그는 왜달지
        mapView.addCircle(circle);


    }

    private void createBottomSheetForAddress(List<PlaceItem> placeList) {
        View view = View.inflate(activity, R.layout.dialog_address, null);

        LinearLayout ly_dialog_address = view.findViewById(R.id.ly_dialog_address);
        RecyclerView recycler_address = view.findViewById(R.id.recycler_address);


        dialog = new BottomSheetDialog(activity);
        //String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int maxHeight = (int) (height * 0.70);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);

        ly_dialog_address.setLayoutParams(lp);


        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.line_divider);
        if (drawable != null) {
            DividerItemDecoration divider = new DividerItemDecoration(recycler_address.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(drawable);
            recycler_address.addItemDecoration(divider);
        }


        placeRecyclerAdapter = new PlaceRecyclerAdapter(placeList, activity, this);
        recycler_address.setLayoutManager(new LinearLayoutManager(activity));
        recycler_address.setAdapter(placeRecyclerAdapter);


        dialog.setContentView(view);
        dialog.show();


    }

    private void loadAddress(String query) {
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
                List<PlaceItem> placeList = new ArrayList<>();
                try {

                    JSONObject obj = new JSONObject(con);
                    String check_string = obj.getJSONObject("meta").getString("total_count");
                    if (Integer.valueOf(check_string) == 0) { //결과가 없을 때

                        Toast.makeText(activity, query + "(와)과 일치하는 검색결과가 없습니다", Toast.LENGTH_LONG).show();
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
                        createBottomSheetForAddress(placeList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // setupRecyclerView(placeList);


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(activity, "주소를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onMoveSelectedPlace(double lat, double lng) {
        dialog.dismiss();
        moveSelectedPlace(lat, lng);


    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");

            getCurrentLocationAndInitiateMap();
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(activity)
                        .setTitle("위치정보에대한 권한이 필요합니다.")
                        .setMessage("서비스 이용을 위해 위치정보에 대한 접근이 필요합니다.")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void getCurrentLocationAndInitiateMap() {


    }
}
