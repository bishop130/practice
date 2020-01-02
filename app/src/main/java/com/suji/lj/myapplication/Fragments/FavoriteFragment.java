package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import com.suji.lj.myapplication.Adapters.MainRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Decorators.EventDecorator;
import com.suji.lj.myapplication.Decorators.SelectorDecorator;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForServer;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.NewMissionActivity;
import com.suji.lj.myapplication.R;
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
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
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

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.database.paging.LoadingState.LOADED;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    private TextView date_display;
    private MaterialCalendarView materialCalendarView;
    DatePickerDialog datePickerDialog;
    int year, month, day;
    private Calendar calendar;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private FragmentActivity myContext;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private List<CalendarDay> dates = new ArrayList<>();
    private String response_result;
    private Context mContext;
    ProgressBar loading_panel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("홈프레그", "onCreateView");
        calendar = Calendar.getInstance();
        requestQueue = Volley.newRequestQueue(mContext);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        SharedPreferences Preferences = mContext.getSharedPreferences("timer_control", MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putBoolean("timer_switch", true);
        editor.apply();

        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        //volleyConnect(user_id);
        FloatingActionButton fab = view.findViewById(R.id.fab_new);

        loading_panel = view.findViewById(R.id.loadingPanel);
        recyclerView = view.findViewById(R.id.recycler_day);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new RecyclerViewDivider(36));


        materialCalendarView = view.findViewById(R.id.material_calendar_day);
        materialCalendarView.setTopbarVisible(true);

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                //onSortMission(response_result, calendarDay);
                Log.d("달력", calendarDay.getMonth() + "");
            }
        });

        setMaterialCalendarView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewMissionActivity.class);
                startActivity(intent);

            }
        });

        //checkResult();

        checkPaging();
        queryData();
        return view;
    }


    private void setMaterialCalendarView() {
        materialCalendarView.setBackgroundColor(Color.WHITE);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        final LocalDate calendar = LocalDate.now();
        materialCalendarView.setSelectedDate(calendar);
        //materialCalendarView.addDecorator(new SelectorDecorator(getActivity()));

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
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(getContext(), lstRecyclerItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mainRecyclerAdapter);

    }


    private void queryData() {
        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date").startAt(DateTimeUtils.getCurrentTime()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                    double lat = itemForMissionByDay.getLat();
                    double lng = itemForMissionByDay.getLat();
                    if (dataSnapshot.hasChildren()) {
                        Log.d("정렬", itemForMissionByDay.getDate()+" "+itemForMissionByDay.getTime());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void checkResult() {

        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        List<MissionInfoList> missionInfoLists = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference = databaseReference.child("mission_info_list").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Log.d("파베", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    MissionInfoList missionInfoList = post.getValue(MissionInfoList.class);


                    missionInfoLists.add(missionInfoList);
                    Log.d("파베", missionInfoList.getMission_title() + "");
                    Log.d("파베", missionInfoList.getMission_time());
                    Log.d("파베", missionInfoList.getLat() + "");
                    Log.d("파베", missionInfoList.getMission_info_root_id() + "");

                    for (String date : missionInfoList.getMission_dates().keySet()) {
                        Log.d("파베", date);
                    }
                }

                sortMissions(missionInfoLists);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkPaging() {

        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("user_data").child(user_id).child("mission_display");

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(2)
                .build();
        DatabasePagingOptions<ItemForMissionByDay> options = new DatabasePagingOptions.Builder<ItemForMissionByDay>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ItemForMissionByDay.class)
                .build();

        //MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(getContext(), lstRecyclerItem);


        FirebaseRecyclerPagingAdapter<ItemForMissionByDay, ItemViewHolder> adapter =
                new FirebaseRecyclerPagingAdapter<ItemForMissionByDay, ItemViewHolder>(options) {
                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create the ItemViewHolder
                        // ...
                        View view;
                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);
                        ItemViewHolder viewHolder = new ItemViewHolder(view);

                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder,
                                                    int position,
                                                    @NonNull ItemForMissionByDay model) {
                        // Bind the item to the view holder
                        // ...
                        holder.tv_missionTitle.setText(model.getTitle());
                        holder.tv_address.setText(model.getAddress());
                        holder.tv_date.setText(DateTimeUtils.makeDateForHuman(model.getDate()));
                        holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(model.getTime()));


                    }

                    @Override
                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
                        Log.d("페이징", state.toString());
                        switch (state) {
                            case LOADING_INITIAL:
                                // The initial load has begun
                                // ...
                                loading_panel.setVisibility(View.VISIBLE);
                                break;
                            case LOADING_MORE:
                                // The adapter has started to load an additional page
                                // ...
                                loading_panel.setVisibility(View.VISIBLE);
                                break;
                            case LOADED:
                                // The previous load (either initial or additional) completed
                                // ...
                                loading_panel.setVisibility(View.GONE);
                                break;
                            case ERROR:
                                // The previous load (either initial or additional) failed. Call
                                // the retry() method in order to retry the load operation.
                                // ...
                                //retry();
                                loading_panel.setVisibility(View.GONE);
                                break;
                            case FINISHED:

                                loading_panel.setVisibility(View.GONE);
                                break;
                        }
                    }

                };


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


    }

    private void sortMissions(List<MissionInfoList> missionInfoLists) {


        for (int i = 0; i < missionInfoLists.size(); i++) {
            for (String date : missionInfoLists.get(i).getMission_dates().keySet()) {
                Log.d("파베", date);
                RecyclerItem recyclerItem = new RecyclerItem();
                recyclerItem.setMissionTitle(missionInfoLists.get(i).getMission_title());
                recyclerItem.setMissionID("missionid");
                recyclerItem.setLatitude(missionInfoLists.get(i).getLat());
                recyclerItem.setLongitude(missionInfoLists.get(i).getLng());
                recyclerItem.setMissionTime(missionInfoLists.get(i).getMission_time());
                //recyclerItem.setDate_array(jsonObject.getString("date_array"));
                Log.d("파베", date + " " + missionInfoLists.get(i).getMission_time());
                recyclerItem.setDate_time(date + " " + missionInfoLists.get(i).getMission_time());
                //recyclerItem.setContact_json(jsonObject.getString("mission_contacts"));
                //recyclerItem.setCompleted(jsonObject.getString("completed_dates"));
                //recyclerItem.setCompleted_dates(jsonObject.getString("completed_dates_array"));
                //recyclerItem.setTotal_dates(jsonObject.getString("total_dates"));
                //recyclerItem.setIs_failed(jsonObject.getString("is_failed"));
                recyclerItem.setAddress(missionInfoLists.get(i).getAddress());
                recyclerItem.setDate(date);
                lstRecyclerItem.add(recyclerItem);
            }
        }


        Collections.sort(lstRecyclerItem);
        setupRecyclerView(lstRecyclerItem);

        //  }
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
                        //if (FORMATTER.format(calendarDay.getDate()).equals(items.get(j))) {
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
                        recyclerItem.setCompleted(jsonObject.getString("completed_dates"));
                        recyclerItem.setCompleted_dates(jsonObject.getString("completed_dates_array"));
                        recyclerItem.setTotal_dates(jsonObject.getString("total_dates"));
                        recyclerItem.setIs_failed(jsonObject.getString("is_failed"));
                        recyclerItem.setAddress(jsonObject.getString("address"));
                        recyclerItem.setDate(items.get(j));

                        lstRecyclerItem.add(recyclerItem);


                    }
                    //  }
                }
                //Collections.sort(lstRecyclerItem);
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


    @Override
    public void onPause() {
        super.onPause();
        Log.d("타이머", "onPause_SearchFragment");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timer_switch", false);
        editor.apply();

        boolean timer_switch = mContext.getSharedPreferences("timer_control", MODE_PRIVATE).getBoolean("timer_switch", true);


        Log.d("타이머", " " + timer_switch);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("홈프레그", "onResume_SearchFragment");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timer_switch", true);
        editor.apply();
        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        //volleyConnect(user_id);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("홈프레그", "onAttach_SearchFragment");
        mContext = context;

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_missionTitle;
        TextView tv_date;
        TextView tv_time;
        TextView tv_address;

        LinearLayout view_container;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.main_recycler_view_container);
            tv_missionTitle = itemView.findViewById(R.id.tv_mission_title);
            tv_date = itemView.findViewById(R.id.date_display);
            tv_time = itemView.findViewById(R.id.time_display);
            tv_address = itemView.findViewById(R.id.address_display);

        }
    }

    private void setDisplayDateTime(ItemViewHolder holder, MissionInfoList missionInfoList) {
        //String time = Utils.monthDayTime(missionInfoList.getMission_dates().,missionInfoList.getMission_time());


    }


    @Override
    public void onStart() {
        super.onStart();
        loading_panel.setVisibility(View.VISIBLE);
    }
}
