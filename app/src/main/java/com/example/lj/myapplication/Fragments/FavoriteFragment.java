package com.example.lj.myapplication.Fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.lj.myapplication.Adapters.RecyclerAdapter;
import com.example.lj.myapplication.Adapters.RecyclerViewDivider;
import com.example.lj.myapplication.Decorators.EventDecorator;
import com.example.lj.myapplication.Decorators.SelectorDecorator;
import com.example.lj.myapplication.Items.RecyclerItem;
import com.example.lj.myapplication.MainActivity;
import com.example.lj.myapplication.R;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    private TextView date_display;
    private MaterialCalendarView materialCalendarView;
    DatePickerDialog datePickerDialog;
    int year, month, day;
    private Calendar calendar;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private FragmentActivity myContext;
    private RequestQueue requestQueue;
    private StringRequest request;
    private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private List<CalendarDay> dates = new ArrayList<>();
    private String response_result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        requestQueue = Volley.newRequestQueue(getContext());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        final Calendar now = Calendar.getInstance();

        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        requestAccessTokenInfo();
        recyclerView = view.findViewById(R.id.recycler_day);
        recyclerView.addItemDecoration(new RecyclerViewDivider(36));


        materialCalendarView = view.findViewById(R.id.material_calendar_day);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                onSortMission(response_result, calendarDay);
            }
        });

        setMaterialCalendarView();

        return view;
    }

    private void setMaterialCalendarView() {
        materialCalendarView.setBackgroundColor(Color.WHITE);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        final LocalDate calendar = LocalDate.now();
        materialCalendarView.setSelectedDate(calendar);
        materialCalendarView.addDecorator(new SelectorDecorator(getActivity()));

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
    }


    private void setupRecyclerView(List<RecyclerItem> lstRecyclerItem) {

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(), lstRecyclerItem);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void volleyConnect(final String userId) {


        request = new StringRequest(Request.Method.POST, Mission_List_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                displayDotinCalendar(response);
                response_result = response;
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

    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                // redirectLoginActivity(self);
                Toast.makeText(getContext(), "세션닫힘", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getContext(), "로그인해주세요", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                String userId = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("토큰확인", String.valueOf(userId));
                //requestProfile();

                volleyConnect(userId);
                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Log.d("토큰 만료 됨 ", expiresInMilis + "이후에");
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        String setText = year + "년 " + monthOfYear + "월 " + dayOfMonth + "일";
        date_display.setText(setText);
        Log.d("결과 보기", date);
    }

    private void displayDotinCalendar(String response) {

        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String date_array = jsonObject.getString("date_array");
                List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));
                for (int j = 0; j < items.size(); j++) {
                    Date date = dateParser(items.get(j));
                    String day = (String) DateFormat.format("d", date); // 20
                    String monthNumber = (String) DateFormat.format("M", date); // 6
                    String year = (String) DateFormat.format("yyyy", date); // 2013
                    CalendarDay eventDay2 = CalendarDay.from(Integer.valueOf(year), Integer.valueOf(monthNumber), Integer.valueOf(day));
                    dates.add(eventDay2);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));

    }


    private void onSortMission(String resultForDate, CalendarDay calendarDay) {

        if (resultForDate != null) {
            try {
                lstRecyclerItem.clear();
                JSONArray jsonArray = new JSONArray(resultForDate);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String date_array = jsonObject.getString("date_array");
                    String completed_dates_array = jsonObject.getString("completed_dates_array");
                    int last = completed_dates_array.length() - 1;
                    if (last > 0 && completed_dates_array.charAt(last) == ',') {
                        completed_dates_array = completed_dates_array.substring(0, last);
                    }

                    List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));
                    List<String> completed_dates_item = Arrays.asList(completed_dates_array.split("\\s*,\\s*"));

                    for (int j = 0; j < items.size(); j++) {
                        if (FORMATTER.format(calendarDay.getDate()).equals(items.get(j))) {
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
                            recyclerItem.setMissionTime(jsonObject.getString("mission_time"));
                            recyclerItem.setDate_array(jsonObject.getString("date_array"));
                            recyclerItem.setDate_time(items.get(j) + " " + jsonObject.getString("mission_time"));
                            recyclerItem.setContact_json(jsonObject.getString("mission_contacts"));
                            recyclerItem.setCompleted_dates(jsonObject.getString("completed_dates"));
                            recyclerItem.setTotal_dates(jsonObject.getString("total_dates"));
                            recyclerItem.setDate(items.get(j));

                            lstRecyclerItem.add(recyclerItem);


                        }
                    }
                }
                Collections.sort(lstRecyclerItem);
                setupRecyclerView(lstRecyclerItem);


            } catch (JSONException e) {
                Log.d("살펴보자", e + "error");
            }
        }

    }

    private Date dateParser(String date) {
        Date result = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.KOREA);
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
