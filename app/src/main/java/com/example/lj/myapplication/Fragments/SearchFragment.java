package com.example.lj.myapplication.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.myapplication.Adapters.KakaoFriends;
import com.example.lj.myapplication.Adapters.RecyclerAdapter;
import com.example.lj.myapplication.Adapters.RecyclerForMissionAdapter;
import com.example.lj.myapplication.Adapters.RecyclerViewDivider;
import com.example.lj.myapplication.Items.RecyclerItem;
import com.example.lj.myapplication.R;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private Request request;
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private KakaoFriends kakaoFriends = new KakaoFriends();
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        requestQueue = Volley.newRequestQueue(mContext);
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_total);
        recyclerView.addItemDecoration(new RecyclerViewDivider(36));

        String user_id = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE).getString("token","");
        volleyConnect(user_id);
        kakaoFriends.requestFriends();

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void setupRecyclerView(List<RecyclerItem> lstRecyclerItem) {

        RecyclerForMissionAdapter recyclerAdapter = new RecyclerForMissionAdapter(getContext(), lstRecyclerItem);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void volleyConnect(final String userId) {


        request = new StringRequest(Request.Method.POST, Mission_List_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("HomeFragment", "volley connect");
                CalendarDay calendarDay = CalendarDay.today();
                onSortMission(response, calendarDay);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("userId", userId);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    private void onSortMission(String resultForDate, CalendarDay calendarDay) {

        if (resultForDate != null) {
            try {
                lstRecyclerItem.clear();
                JSONArray jsonArray = new JSONArray(resultForDate);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecyclerItem recyclerItem = new RecyclerItem();
                    String register_date = jsonObject.getString("register_date_time");

                    recyclerItem.setMissionTitle(jsonObject.getString("mission_name"));
                    recyclerItem.setMissionID(jsonObject.getString("mission_id"));
                    recyclerItem.setLatitude(jsonObject.getDouble("mission_lat"));
                    recyclerItem.setLongitude(jsonObject.getDouble("mission_lng"));
                    recyclerItem.setMissionTime(jsonObject.getString("mission_time"));
                    recyclerItem.setContact_json(jsonObject.getString("mission_contacts"));
                    recyclerItem.setCompleted_dates(jsonObject.getString("completed_dates"));
                    recyclerItem.setTotal_dates(jsonObject.getString("total_dates"));
                    recyclerItem.setRegister_date(jsonObject.getString("register_date_time"));
                    recyclerItem.setDate_time(jsonObject.getString("register_date_time"));
                    recyclerItem.setDate_array(jsonObject.getString("date_array"));

                    lstRecyclerItem.add(recyclerItem);


                }
                Collections.sort(lstRecyclerItem);
                setupRecyclerView(lstRecyclerItem);


            } catch (JSONException e) {
                Log.d("살펴보자", e + "error");
            }
        }


    }
}
