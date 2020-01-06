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

import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import com.suji.lj.myapplication.Adapters.MainRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.MissionRecyclerAdapter;
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
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

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
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.database.paging.LoadingState.LOADED;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private Context mContext;
    private ProgressBar loading_panel;
    private TextView tv_mission_title;
    private TextView date_display;
    private TextView time_display;
    private TextView address_display;
    //private LinearLayout no_mission_ly;
    private LinearLayout is_mission_ly;
    private TextView rest_time_display;
    CountDownTimer timer;
    DateTimeFormatter dtf = new DateTimeFormatter();
    AppBarLayout appBar;
    LinearLayout address_display_ly;
    LinearLayout rest_time_display_ly;

    private int expandedTopMargin;
    private int collapsedTopMargin;
    private static final float COLLAPSED_TOP_MARGIN_DP = 24f;
    private static final float MARGIN_SCROLLER_MULTIPLIER = 4f;
    TabLayout tab_ly;
    List<MissionInfoList> infoLists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("홈프레그", "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        SharedPreferences Preferences = mContext.getSharedPreferences("timer_control", MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putBoolean("timer_switch", true);
        editor.apply();

        FloatingActionButton fab = view.findViewById(R.id.fab_new);
        tv_mission_title = view.findViewById(R.id.tv_mission_title);
        date_display = view.findViewById(R.id.date_display);
        time_display = view.findViewById(R.id.time_display);
        address_display = view.findViewById(R.id.address_display);
        //no_mission_ly = view.findViewById(R.id.no_mission_layout);
        is_mission_ly = view.findViewById(R.id.is_mission_layout);
        rest_time_display = view.findViewById(R.id.rest_time_display);
        address_display_ly=view.findViewById(R.id.address_display_layout);
        rest_time_display_ly=view.findViewById(R.id.rest_time_display_layout);

        loading_panel = view.findViewById(R.id.loadingPanel);
        recyclerView = view.findViewById(R.id.recycler_day);
        recyclerView.setNestedScrollingEnabled(false);
        appBar = view.findViewById(R.id.app_bar_layout);
        tab_ly = view.findViewById(R.id.tab_ly);


        expandedTopMargin = ((ViewGroup.MarginLayoutParams) rest_time_display_ly.getLayoutParams()).topMargin;
        collapsedTopMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COLLAPSED_TOP_MARGIN_DP, getResources().getDisplayMetrics());
        tab_ly.addTab(tab_ly.newTab().setText("TAB-3")) ;
        tab_ly.addTab(tab_ly.newTab().setText("TAB-4")) ;

        tab_ly.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition() ;
                switch (pos) {
                    case 0 :
                        queryData();

                        break ;
                    case 1 :
                        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

                        FirebaseDatabase.getInstance().getReference().child("mission_info_list").child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                infoLists.clear();
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    MissionInfoList missionInfoList = data.getValue(MissionInfoList.class);
                                    infoLists.add(missionInfoList);


                                   for(TreeMap.Entry<String, Boolean> map: new TreeMap<>(missionInfoList.getMission_dates()).entrySet()){


                                   }


                                }
                                setRecyclerViewByMission(infoLists);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });






                        break ;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d("오프셋", "오프셋" + verticalOffset + "");
                Log.d("오프셋", "스크롤렌지" + appBarLayout.getTotalScrollRange() + "");

                //is_mission_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()))*2);
                address_display_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()))*2);
                rest_time_display_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()))*2);


            }
        });



        recyclerView.addItemDecoration(new RecyclerViewDivider(36));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewMissionActivity.class);
                startActivity(intent);

            }
        });

        //checkResult();

        //checkPaging();
        queryData();


        return view;
    }
    private void setRecyclerViewByMission(List<MissionInfoList> infoLists){


        MissionRecyclerAdapter missionRecyclerAdapter= new MissionRecyclerAdapter(getContext(),infoLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(missionRecyclerAdapter);

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
        Log.d("데이트타임",DateTimeUtils.getCurrentTime());
        databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().startAt(DateTimeUtils.getCurrentTime()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //no_mission_ly.setVisibility(View.GONE);
                    is_mission_ly.setVisibility(View.VISIBLE);
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                        double lat = itemForMissionByDay.getLat();
                        double lng = itemForMissionByDay.getLat();
                        String title = itemForMissionByDay.getTitle();
                        String date = itemForMissionByDay.getDate();
                        String time = itemForMissionByDay.getTime();
                        String address = itemForMissionByDay.getAddress();
                        tv_mission_title.setText(title);
                        date_display.setText(DateTimeUtils.makeDateForHuman(date));
                        time_display.setText(DateTimeUtils.makeTimeForHuman(time));
                        address_display.setText(address);
                        setTimer(date+time);
                        checkResult(date + (Integer.valueOf(time) + 1));
                        if (dataSnapshot.hasChildren()) {
                            Log.d("정렬", itemForMissionByDay.getDate() + " " + itemForMissionByDay.getTime());
                        }
                    }
                }
                else{
                   // no_mission_ly.setVisibility(View.VISIBLE);
                    is_mission_ly.setVisibility(View.GONE);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.toString(),Toast.LENGTH_LONG).show();

            }
        });
    }


    private void checkResult(String date_time) {

        String dateTime = String.valueOf(Double.valueOf(date_time)+1);
        Log.d("정렬","이거확인"+date_time);
        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        Query query = FirebaseDatabase.getInstance().getReference().child("user_data").child(user_id).child("mission_display").orderByChild("date").startAt(date_time);

        FirebaseRecyclerOptions<ItemForMissionByDay> options =
                new FirebaseRecyclerOptions.Builder<ItemForMissionByDay>()
                        .setLifecycleOwner(this)
                        .setQuery(query, new SnapshotParser<ItemForMissionByDay>() {
                            @NonNull
                            @Override
                            public ItemForMissionByDay parseSnapshot(@NonNull DataSnapshot snapshot) {

                                ItemForMissionByDay item =snapshot.getValue(ItemForMissionByDay.class);
                                return item;
                            }
                        })
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ItemForMissionByDay, ItemViewHolder>(options) {
            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);
                ItemViewHolder viewHolder = new ItemViewHolder(view);

                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemForMissionByDay model) {
                // Bind the Chat object to the ChatHolder
                // ...
                Log.d("정렬",model.getTitle());
                holder.tv_missionTitle.setText(model.getTitle());
                holder.tv_address.setText(model.getAddress());
                holder.tv_date.setText(DateTimeUtils.makeDateForHuman(model.getDate()));
                holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(model.getTime()));
            }


        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loading_panel.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);


    }

    private void setTimer(String date_time){
        Date endDate = dtf.dateTimeParser(date_time);
        Date curDate = new Date(System.currentTimeMillis());

        long diff = endDate.getTime() - curDate.getTime();
        if (diff >= 0) {

            timer = new CountDownTimer(diff,1000) {
                @Override
                public void onTick(long l) {
                    long days = TimeUnit.MILLISECONDS.toDays(l);
                    long remainingHoursInMillis = l - TimeUnit.DAYS.toMillis(days);
                    long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                    long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                    long remainingSecondsInMillis = remainingMinutesInMillis - TimeUnit.MINUTES.toMillis(minutes);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingSecondsInMillis);
                    //Log.d("타이머", "position" + position + "   " + holder.getAdapterPosition() + "time:" + l + " " + timer_switch);
                    if (days == 0) {
                        String time = "+ " + hours + "시간 " + minutes + "분";

                        rest_time_display.setText("남은시간: "+time);

                        if (l < 2 * 60 * 60 * 1000) { //2시간
                            //holder.is_status.setImageResource(R.drawable.ready_to_check);
                        }
                    } else {
                        String time = "+ " + days + "일 " + hours + "시간 " + minutes + "분";
                        rest_time_display.setText("남은시간"+time);

                    }
                }

                @Override
                public void onFinish() {


                }
            }.start();

        }


    }
/*
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
                .setQuery(query, config,    new SnapshotParser<ItemForMissionByDay>() {
                    @NonNull
                    @Override
                    public ItemForMissionByDay parseSnapshot(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()){

                            Log.d("파베","스냅샷"+data.toString());
                        }

                        ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);
                        if(DateTimeUtils.compareIsFuture(item.getDate()+item.getTime())){

                            return item;
                        }
                        else {
                            return item;
                        }
                    }
                })
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

                        if (DateTimeUtils.compareIsFuture(model.getDate() + model.getTime())) {

                            if (position == 0) {
                                tv_mission_title.setText(model.getTitle());
                                time_display.setText(model.getTime());
                                date_display.setText(model.getDate());
                                address_display.setText(model.getAddress());

                               // notifyItemRemoved(position);
                                //notifyItemRangeChanged(position,.size());

                            } else {
                                holder.tv_missionTitle.setText(model.getTitle());
                                holder.tv_address.setText(model.getAddress());
                                holder.tv_date.setText(DateTimeUtils.makeDateForHuman(model.getDate()));
                                holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(model.getTime()));
                                holder.view_container.setVisibility(View.GONE);
                                holder.tv_date.setVisibility(View.GONE);
                                holder.tv_missionTitle.setVisibility(View.GONE);
                            }

                        }


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

 */

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
        loading_panel.setVisibility(View.GONE);
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


    @Override
    public void onStart() {
        super.onStart();
        loading_panel.setVisibility(View.VISIBLE);
    }
}
