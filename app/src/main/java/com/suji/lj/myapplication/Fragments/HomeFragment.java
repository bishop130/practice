package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.suji.lj.myapplication.Adapters.DBHelper;
import com.suji.lj.myapplication.Adapters.DateTimeFormatter;
import com.suji.lj.myapplication.Adapters.PreciseTimer;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.NewMissionActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Adapters.RecyclerAdapter;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private CountDownTimer countDownTimer;
    private Context mContext;
    private AdView adView;
    private DateTimeFormatter dtf = new DateTimeFormatter();
    private SwipeRefreshLayout srl;
    RecyclerAdapter recyclerAdapter;
    PreciseTimer preciseTimer;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("홈프레그","onCreateView");
        requestQueue = Volley.newRequestQueue(mContext);
        SharedPreferences Preferences = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putBoolean("timer_switch",true);
        editor.apply();

        dbHelper = new DBHelper(mContext, "alarm_manager.db", null, 5);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("token","");
        volleyConnect(user_id);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        srl = view.findViewById(R.id.home_swipe_layout);
        srl.setOnRefreshListener(this);
        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544/6300978111");
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("광고", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d("광고", "onFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("광고", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("광고", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("광고", "onAdClosed");
            }
        });

        recyclerView.addItemDecoration(new RecyclerViewDivider(36));
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        // final TextView time_counter = (TextView) view.findViewById(R.id.timer);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewMissionActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private void setupRecyclerView(List<RecyclerItem> lstRecyclerItem) {

        recyclerAdapter = new RecyclerAdapter(getContext(), lstRecyclerItem);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);
    }


    private void volleyConnect(final String userId) {


        StringRequest request = new StringRequest(Request.Method.POST, Mission_List_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataFromServer(response);
                Log.d("HomeFragment", "volley connect");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("userId", userId);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void getDataFromServer(String response) {
        try {

            lstRecyclerItem.clear();

            Date cur_time = new Date(System.currentTimeMillis());

            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date_array = jsonObject.getString("date_array");
                String completed_dates_array = jsonObject.getString("completed_dates_array");
                String is_failed = jsonObject.getString("is_failed");

                Log.d("펄스"," "+is_failed);
               //아직 실패하지 않았다면
                if(!Boolean.valueOf(is_failed)) {
                    Log.d("펄스2"," "+!Boolean.valueOf(is_failed));


                    int last = completed_dates_array.length() - 1;
                    if (last > 0 && completed_dates_array.charAt(last) == ',') {
                        completed_dates_array = completed_dates_array.substring(0, last);
                    }

                    List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));
                    List<String> completed_dates_item = Arrays.asList(completed_dates_array.split("\\s*,\\s*"));


                    for (int j = 0; j < items.size(); j++) {

                        String date_time = items.get(j) + " " + jsonObject.getString("mission_time");

                        Date set_time = dtf.dateTimeParser(date_time);
                        if ((set_time.getTime() - cur_time.getTime() >= 0) && (set_time.getTime() - cur_time.getTime() <= 24 * 60 * 60 * 1000)) {//24시간
                            String check = jsonObject.getString("is_failed");
                            if(check.equals("1")) {

                            }else {

                                RecyclerItem recyclerItem = new RecyclerItem();

                                for (int k = 0; k < completed_dates_item.size(); k++) {
                                    if (items.get(j).equals(completed_dates_item.get(k))) {
                                        recyclerItem.setSuccess(true);
                                    } else {
                                        recyclerItem.setSuccess(false);
                                    }
                                }
                                recyclerItem.setMissionTitle(jsonObject.getString("mission_name"));
                                recyclerItem.setMissionID(jsonObject.getString("mission_id"));
                                recyclerItem.setLatitude(jsonObject.getDouble("mission_lat"));
                                recyclerItem.setLongitude(jsonObject.getDouble("mission_lng"));
                                recyclerItem.setDate_array(jsonObject.getString("date_array"));
                                recyclerItem.setMissionTime(jsonObject.getString("mission_time"));
                                recyclerItem.setDate_time(items.get(j) + " " + jsonObject.getString("mission_time"));
                                recyclerItem.setContact_json(jsonObject.getString("mission_contacts"));
                                recyclerItem.setCompleted(jsonObject.getString("completed_dates"));
                                recyclerItem.setTotal_dates(jsonObject.getString("total_dates"));
                                recyclerItem.setIs_failed(jsonObject.getString("is_failed"));
                                recyclerItem.setAddress(jsonObject.getString("address"));
                                recyclerItem.setCompleted_dates(jsonObject.getString("completed_dates_array"));
                                recyclerItem.setDate(items.get(j));
                                if (check.equals("1")) {
                                    deletePastDB(jsonObject.getString("mission_id"));
                                }
                                lstRecyclerItem.add(recyclerItem);
                            }

                        }
                    }

                }

            }
            Collections.sort(lstRecyclerItem);
            setupRecyclerView(lstRecyclerItem);
        } catch (JSONException e) {
            Log.d("살펴보자", e + "error");
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("홈프레그","onAttach");
        mContext = context;

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("홈프레그","onResume");
        //checkPermission();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timer_switch",true);
        editor.apply();
        SharedPreferences sharedPreferences2 = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE);
        String user_id = sharedPreferences2.getString("token","");
        volleyConnect(user_id);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("홈프레그","onDestroy");
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownTimer = null;
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("홈프레그","onPause");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();

        editor.putBoolean("timer_switch",false);

    }
    private void deletePastDB(String mission_id) {
        String sql = "DELETE FROM alarm_table WHERE mission_id = '"+mission_id+"'"; //실패한 정보 삭제
        dbHelper.delete(sql);

    }


    @Override
    public void onRefresh() {
        String user_id = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE).getString("token","");
        volleyConnect(user_id);
        srl.setRefreshing(false);
        Log.d("살펴보자", "새로고침 완료");
    }
}
