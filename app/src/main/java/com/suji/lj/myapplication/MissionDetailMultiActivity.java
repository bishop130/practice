package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationFriendAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionByMissionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionProgressAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerPortionInvitationAdapter;
import com.suji.lj.myapplication.Decorators.EventDecorator;
import com.suji.lj.myapplication.Decorators.OneDayDecorator;
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.internal.Util;

public class MissionDetailMultiActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    List<MissionInfoList> list;
    NestedScrollView mainScrollView;

    TextView tv_title;
    TextView tv_address;
    Double lat;
    Double lng;
    TextView tv_date_start;
    TextView tv_date_end;
    TextView tv_time_start;
    TextView tv_time_end;
    Toolbar toolbar;
    TextView tv_penalty;
    TextView tv_penalty_total;
    RecyclerView rv_friend;
    RecyclerView rv_portion;
    MaterialCalendarView materialCalendarView;
    Collection<CalendarDay> cal;
    List<ItemForDateTimeByList> itemForDateTimeByLists;
    LinearLayout ly_start_date;
    LinearLayout ly_end_date;
    String user_id;
    RecyclerView rv_friend_by_day;
    TextView tv_selected_date;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail_multi);


        tv_title = findViewById(R.id.tv_title);
        tv_address = findViewById(R.id.tv_address);
        toolbar = findViewById(R.id.toolbar);
        tv_date_start = findViewById(R.id.tv_date_start);
        tv_date_end = findViewById(R.id.tv_date_end);
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_time_end = findViewById(R.id.tv_time_end);
        tv_penalty = findViewById(R.id.tv_penalty);
        tv_penalty_total = findViewById(R.id.tv_penalty_amount);
        mainScrollView = findViewById(R.id.main_scroll_view);
        rv_friend = findViewById(R.id.rv_friend_list);
        rv_portion = findViewById(R.id.rv_portion);
        materialCalendarView = findViewById(R.id.material_calendarView);
        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년 ").append(monthOne).append("월");
                return buffer;
            }
        });

        ly_start_date = findViewById(R.id.ly_start_date);
        ly_end_date = findViewById(R.id.ly_end_date);
        rv_friend_by_day = findViewById(R.id.rv_friend_by_day);
        tv_selected_date = findViewById(R.id.tv_selected_date);

        user_id = Account.getUserId(this);

        Utils.drawRecyclerViewDivider(this, rv_friend_by_day);
        cal = new HashSet<CalendarDay>();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        ly_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialCalendarView.clearSelection();

                int index = 0;
                maintainCalendarState(index);


            }
        });

        ly_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = itemForDateTimeByLists.size() - 1;
                maintainCalendarState(index);
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mission_id = bundle.getString("mission_id");
            boolean isSingle = bundle.getBoolean("mission_mode", false);
            if (!isSingle) {
                Log.d("초대", mission_id + "");

                databaseReference.child("multi_data").orderByChild("mission_id").equalTo(mission_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                MissionInfoList missionInfoList = snapshot.child("mission_info_list").getValue(MissionInfoList.class);

                                if (missionInfoList != null) {

                                    Log.d("초대", missionInfoList.getMission_title() + "");

                                    displayData(missionInfoList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }


    }

    private void maintainCalendarState(int index) {

        materialCalendarView.clearSelection();

        String date_time = itemForDateTimeByLists.get(index).getDate_time();
        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        Log.d("캘린더", year + "  " + month + "   " + day);


        materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);

        tv_selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));


        materialCalendarView.setCurrentDate(CalendarDay.from(year, month, day));

        //selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));
        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));
        OneDayDecorator one = new OneDayDecorator(getResources().getColor(R.color.White));
        one.setDate(CalendarDay.from(year, month, day));
        materialCalendarView.addDecorator(one);
        setupRecyclerViewFriendByDay(itemForDateTimeByLists.get(index).getFriendByDayList());


    }


    private void displayData(MissionInfoList item) {

        String title = item.getMission_title();
        String address = item.getAddress();
        String start_date = DateTimeUtils.makeDateForHuman(item.getMin_date());
        String end_date = DateTimeUtils.makeDateForHuman(item.getMax_date());
        double lat = item.getLat();
        double lng = item.getLng();
        itemForDateTimeByLists = new ArrayList<>();
        Map<String, ItemForDateTimeByList> map = item.getMission_dates();
        addMapFragment(new MapFragment(mainScrollView, lat, lng));


        for (ItemForDateTimeByList object : map.values()) {

            Collections.addAll(itemForDateTimeByLists, object);
            Collections.sort(itemForDateTimeByLists, new Comparator<ItemForDateTimeByList>() {
                @Override
                public int compare(ItemForDateTimeByList o1, ItemForDateTimeByList o2) {
                    return o1.getDate_time().compareTo(o2.getDate_time());
                }
            });

            //Collections.add
            //itemForDateTimeByLists.add(object);
        }


        List<ItemForFriendByDay> friendByDayList = item.getFriendByDayList();
        List<ItemPortion> portionList = item.getItemPortionList();

        toolbar.setTitle(title);
        tv_address.setText(address);
        tv_date_start.setText(start_date);
        tv_date_end.setText(end_date);
        tv_time_start.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(0).getDate_time(), "yyyyMMddHHmm"));
        tv_time_end.setText(DateTimeUtils.makeTimeForHuman(itemForDateTimeByLists.get(itemForDateTimeByLists.size() - 1).getDate_time(), "yyyyMMddHHmm"));
        tv_penalty.setText(Utils.makeNumberCommaWon(item.getPenalty()));
        tv_penalty_total.setText(Utils.makeNumberCommaWon(item.getPenalty_amount()));
        //tv_current_penalty.setText(Utils.makeNumberCommaWon(item.getPenalty() * item.getFailed_count()));
        //tv_failed_count.setText(item.getFailed_count() + " 번");

        cal = new HashSet<CalendarDay>();
        for (int i = 0; i < itemForDateTimeByLists.size(); i++) {

            String date_time = itemForDateTimeByLists.get(i).getDate_time();
            Date date = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
            //Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Log.d("캘린더", year + "  " + month + "   " + day);


            cal.add(CalendarDay.from(year, month, day));


        }

        setupRecyclerView(friendByDayList);
        setUpRecyclerViewPortion(portionList);
        createCalendar(itemForDateTimeByLists);

    }

    private void createCalendar(List<ItemForDateTimeByList> itemForDateTimeByLists) {


        String date_time = itemForDateTimeByLists.get(0).getDate_time();
        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
        //Calendar calendar = Calendar.getInstance();
        //calendar.clear();
        calendar.setTime(temp);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        materialCalendarView.state().edit().setMinimumDate(CalendarDay.from(year, month, day)).commit();


        //materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);
        materialCalendarView.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                Log.d("달력", selected + " 선택");
                //widget.removeDecorators();
                widget.addDecorator(new EventDecorator(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), cal));
                if (cal.contains(date)) {


                    OneDayDecorator one = new OneDayDecorator(getResources().getColor(R.color.White));
                    one.setDate(date);
                    widget.addDecorator(one);
                    for (int i = 0; i < itemForDateTimeByLists.size(); i++) {
                        String date_time = itemForDateTimeByLists.get(i).getDate_time();
                        Date temp = DateTimeFormatter.dateParser(date_time, "yyyyMMddHHmm");
                        // calendar.clear();
                        calendar.setTime(temp);

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);


                        if (year == date.getYear()) {
                            if (month == date.getMonth()) {
                                if (day == date.getDay()) {

                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int min = calendar.get(Calendar.MINUTE);

                                    setupRecyclerViewFriendByDay(itemForDateTimeByLists.get(i).getFriendByDayList());
                                    tv_selected_date.setText(DateTimeUtils.makeTimeForHumanInt(hour, min));

                                }
                            }
                        }
                    }
                } else {
                    List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
                    setupRecyclerViewFriendByDay(friendByDayList);
                    tv_selected_date.setText("");

                }
            }

        });


    }


    private void addMapFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_layout, fragment).commit();
    }

    private void setupRecyclerView(List<ItemForFriendByDay> list) {
        FriendAdapter adapter = new FriendAdapter(list);
        rv_friend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_friend.setAdapter(adapter);


    }

    private void setUpRecyclerViewPortion(List<ItemPortion> list) {
        RecyclerPortionInvitationAdapter adapter = new RecyclerPortionInvitationAdapter(list);
        rv_portion.setLayoutManager(new LinearLayoutManager(this));
        rv_portion.setAdapter(adapter);


    }

    private void setupRecyclerViewFriendByDay(List<ItemForFriendByDay> friendByDayList) {
        FriendByDayAdapter adapter = new FriendByDayAdapter(friendByDayList);
        rv_friend_by_day.setLayoutManager(new LinearLayoutManager(this));
        rv_friend_by_day.setAdapter(adapter);


    }


    private class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

        List<ItemForFriendByDay> list;

        private FriendAdapter(List<ItemForFriendByDay> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_friend_image, parent, false);


            return new FriendAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String friend_name = list.get(position).getFriend_name();
            String friend_image = list.get(position).getFriend_image();

            holder.tv_friend_name.setText(friend_name);

            holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_friend_image.setClipToOutline(true);

            if (friend_image != null && !friend_image.isEmpty()) {

                Picasso.with(getApplicationContext())
                        .load(friend_image)
                        .fit()
                        .into(holder.iv_friend_image);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_friend_name;
            ImageView iv_friend_image;


            private ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_friend_name = itemView.findViewById(R.id.tv_friend_name);
                iv_friend_image = itemView.findViewById(R.id.iv_friend_image);


            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private class FriendByDayAdapter extends RecyclerView.Adapter<FriendByDayAdapter.ViewHolder> {


        List<ItemForFriendByDay> friendByDayList;
        int hour;
        int min;

        public FriendByDayAdapter(List<ItemForFriendByDay> friendByDayList) {
            this.friendByDayList = friendByDayList;
            this.hour = hour;
            this.min = min;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_friend_by_day, parent, false);


            return new FriendByDayAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String friend_name = friendByDayList.get(position).getFriend_name();
            String friend_image = friendByDayList.get(position).getFriend_image();
            String timeStamp = friendByDayList.get(position).getTime_stamp();
            if (timeStamp != null && !timeStamp.isEmpty()) {
                Log.d("멀티", timeStamp + "타임");
                String checkTime = DateTimeUtils.makeTimeForHuman(timeStamp, "yyyyMMddHHmmss");
                holder.tv_time.setText(checkTime);
                holder.tv_time.setTextColor(ContextCompat.getColor(MissionDetailMultiActivity.this, R.color.successColor));
            } else {


                holder.tv_time.setText("도착정보없음");

            }
            holder.tv_friend_name.setText(friend_name);
            Log.d("멀티", friend_image);


            holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_friend_image.setClipToOutline(true);

            if (friend_image != null && !friend_image.isEmpty()) {
                Picasso.with(getApplicationContext())
                        .load(friend_image)
                        .fit()
                        .into(holder.iv_friend_image);
            }


        }

        @Override
        public int getItemCount() {
            return friendByDayList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_friend_name;
            ImageView iv_friend_image;
            TextView tv_time;


            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_friend_name = itemView.findViewById(R.id.tv_friend_name);
                iv_friend_image = itemView.findViewById(R.id.iv_friend_image);
                tv_time = itemView.findViewById(R.id.tv_time);

            }
        }
    }


}
