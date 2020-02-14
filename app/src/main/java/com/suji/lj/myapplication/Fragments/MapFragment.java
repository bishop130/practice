package com.suji.lj.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.suji.lj.myapplication.Adapters.PlaceRecyclerAdapter;
import com.suji.lj.myapplication.DaumMapActivity;
import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;


public class MapFragment extends Fragment implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public MapFragment() {
        // Required empty public constructor
    }

    SearchView searchView;
    MapView mapView;
    RecyclerView recyclerView;
    PlaceRecyclerAdapter placeRecyclerAdapter;
    ImageView imageView;
    ScrollView mainScrollView;

    RelativeLayout map_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = new MapView(getContext());
        ViewGroup mapViewContainer = view.findViewById(R.id.map_view);
        map_layout = view.findViewById(R.id.map_layout);
        searchView = view.findViewById(R.id.daum_map_search);
        recyclerView = view.findViewById(R.id.place_recycler);
        imageView = view.findViewById(R.id.transparent_image);

        recyclerView.setNestedScrollingEnabled(false);
        mapViewContainer.addView(mapView);
        onAddressSearch();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

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

    @Override
    public void onMapViewInitialized(MapView mapView) {

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

    private void onAddressSearch() {


        //SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("장소,주소 검색..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mapView.removeAllPOIItems();
                loadAddress(query);
                final float scale = getContext().getResources().getDisplayMetrics().density;
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

                Toast.makeText(getContext(), "주소를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void setupRecyclerView(List<PlaceItem> placeList) {

        placeRecyclerAdapter = new PlaceRecyclerAdapter(placeList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(placeRecyclerAdapter);

    }

    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";


        @GET("v2/local/search/keyword.json?")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }

}
