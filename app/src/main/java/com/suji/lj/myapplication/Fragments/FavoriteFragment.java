package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.suji.lj.myapplication.Adapters.MainRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.MissionRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionDayAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerPastMissionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, TabLayout.OnTabSelectedListener {

    private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private Context mContext;
    private ProgressBar loading_panel;
    private TextView tv_mission_title;
    private TextView date_display;
    private TextView time_display;
    private TextView address_display;
    private LinearLayout no_mission_ly;
    private LinearLayout ly_latest_mission;
    private TextView rest_time_display;
    CountDownTimer timer;
    private AppBarLayout appBar;
    LinearLayout address_display_ly;
    LinearLayout rest_time_display_ly;
    boolean isTimerRunning = false;

    private int expandedTopMargin;
    private int collapsedTopMargin;
    private static final float COLLAPSED_TOP_MARGIN_DP = 24f;
    private static final float MARGIN_SCROLLER_MULTIPLIER = 4f;
    boolean data_loaded = false;
    TabLayout tab_ly;
    List<MissionInfoList> infoLists = new ArrayList<>();
    String mother_id;
    String date;
    String time;
    String user_id;
    FirebaseRecyclerPagingAdapter<ItemForMissionByDay, ItemViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("홈프레그", "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        FloatingActionButton fab = view.findViewById(R.id.fab_new);
        tv_mission_title = view.findViewById(R.id.tv_mission_title);
        date_display = view.findViewById(R.id.date_display);
        time_display = view.findViewById(R.id.time_display);
        address_display = view.findViewById(R.id.address_display);
        no_mission_ly = view.findViewById(R.id.no_mission_layout);
        ly_latest_mission = view.findViewById(R.id.ly_latest_mission);
        rest_time_display = view.findViewById(R.id.rest_time_display);
        address_display_ly = view.findViewById(R.id.address_display_layout);
        rest_time_display_ly = view.findViewById(R.id.rest_time_display_layout);

        loading_panel = view.findViewById(R.id.loadingPanel);
        recyclerView = view.findViewById(R.id.recycler_day);
        recyclerView.setNestedScrollingEnabled(false);
        appBar = view.findViewById(R.id.app_bar_layout);
        tab_ly = view.findViewById(R.id.tab_ly);


        expandedTopMargin = ((ViewGroup.MarginLayoutParams) rest_time_display_ly.getLayoutParams()).topMargin;
        collapsedTopMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COLLAPSED_TOP_MARGIN_DP, getResources().getDisplayMetrics());
        tab_ly.addTab(tab_ly.newTab().setText("약속"));
        tab_ly.addTab(tab_ly.newTab().setText("지난약속"));
        tab_ly.addTab(tab_ly.newTab().setText("전체약속"));

        tab_ly.addOnTabSelectedListener(this);
        ly_latest_mission.setOnClickListener(this);


        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d("오프셋", "오프셋" + verticalOffset + "");
                Log.d("오프셋", "스크롤렌지" + appBarLayout.getTotalScrollRange() + "");

                //is_mission_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()))*2);
                address_display_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange())) * 2);
                rest_time_display_ly.setAlpha(1.0f - (Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange())) * 2);


            }
        });


        recyclerView.addItemDecoration(new RecyclerViewDivider(36));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FeedTemplate params = FeedTemplate
                        .newBuilder(ContentObject.newBuilder("디저트 사진",
                                "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                                LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com").build())
                                .setDescrption("아메리카노, 빵, 케익")
                                .build())
                        .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                                .setSharedCount(30).setViewCount(40).build())
                        .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                        .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                .setWebUrl("'https://developers.kakao.com")
                                .setMobileWebUrl("'https://developers.kakao.com")
                                .setAndroidExecutionParams("key1=value1")
                                .setIosExecutionParams("key1=value1")
                                .build()))
                        .build();

                KakaoTalkService.getInstance().requestSendMemo(new TalkResponseCallback<Boolean>() {
                    @Override
                    public void onNotSignedUp() {
                        //
                    }

                    @Override
                    public void onNotKakaoTalkUser() {
                        // 발신자가 카카오톡 유저가 아님
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 그 외 에러
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 액세스토큰 및 리프레시토큰이 만료됨. 재로그인 필요.
                    }

                    @Override
                    public void onSuccess(Boolean result) {

                        Log.d("나에게", result.toString());
                        // API  호출 성공.
                    }
                }, params);
            }
        });
        String key = DateTimeUtils.getCurrentTime();
        queryData(key);


        return view;
    }

    private void setRecyclerViewByMission(List<String> keyList, List<MissionInfoList> infoLists) {
        MissionRecyclerAdapter missionRecyclerAdapter = new MissionRecyclerAdapter(getContext(), keyList, infoLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(missionRecyclerAdapter);
    }

    private void queryData(String key) {

        //String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        SharedPreferences preferences = mContext.getSharedPreferences("FirebaseKey", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d("카카오세션", user_id + "  favorite");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("데이트타임", DateTimeUtils.getCurrentTime());
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().limitToFirst(2).startAt(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_mission_ly.setVisibility(View.GONE);
                        ly_latest_mission.setVisibility(View.VISIBLE);

                        List<ItemForMissionByDay> list = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                            list.add(itemForMissionByDay);
                        }

                        if(list.get(0).isSuccess()){
                            String key = list.get(1).getDate()+list.get(1).getTime();
                            queryData(key);
                        }else{
                            String title = list.get(0).getTitle();
                            String date = list.get(0).getDate();
                            String time = list.get(0).getTime();
                            String address = list.get(0).getAddress();
                            mother_id = list.get(0).getMother_id();

                            tv_mission_title.setText(title);
                            date_display.setText(DateTimeUtils.makeDateForHumanIfToday(date));
                            time_display.setText(DateTimeUtils.makeTimeForHuman(time));
                            address_display.setText(address);
                            setTimer(date + time);
                            data_loaded = true;
                            ly_latest_mission.setClickable(true);
                            String key = list.get(1).getDate()+list.get(1).getTime();
                            editor.putString("key",key);
                            editor.apply();
                            checkResult(key);

                        }

                    } else {
                        loading_panel.setVisibility(View.GONE);
                        no_mission_ly.setVisibility(View.VISIBLE);
                        ly_latest_mission.setVisibility(View.GONE);


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();

                }
            });
    }


    private void checkResult(String date_time) {

        Log.d("정렬", "이거확인" + date_time);
        //String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        if (!Utils.isEmpty(user_id)) {
            Query query = FirebaseDatabase.getInstance().getReference().child("user_data").child(user_id).child("mission_display").orderByKey().startAt(date_time);

            FirebaseRecyclerOptions<ItemForMissionByDay> options = new FirebaseRecyclerOptions.Builder<ItemForMissionByDay>()
                    .setLifecycleOwner(this)
                    .setQuery(query, ItemForMissionByDay.class)
                    .build();
            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ItemForMissionByDay, ItemViewHolder>(options) {
                @NonNull
                @Override
                public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    View view;
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    view = inflater.inflate(R.layout.main_recycler_view_item, parent, false);
                    return new ItemViewHolder(view);
                }


                @Override
                protected void onBindViewHolder(ItemViewHolder holder, int position, ItemForMissionByDay model) {
                    // Bind the Chat object to the ChatHolder
                    // ...
                    Log.d("정렬", model.getTitle());
                    holder.tv_missionTitle.setText(model.getTitle());
                    holder.tv_address.setText(model.getAddress());
                    holder.tv_date.setText(DateTimeUtils.makeDateForHuman(model.getDate()));
                    holder.tv_time.setText(DateTimeUtils.makeTimeForHuman(model.getTime()));
                    holder.view_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, MissionDetailActivity.class);
                            intent.putExtra("mission_id", model.getMother_id());
                            startActivity(intent);
                        }
                    });

                }


            };
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            loading_panel.setVisibility(View.GONE);
        }


    }

    private void queryPastData(){

       // String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        SharedPreferences preferences = mContext.getSharedPreferences("FirebaseKey", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String key = Utils.getCurrentTime();

        Log.d("카카오세션", user_id + "  favorite");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("데이트타임", DateTimeUtils.getCurrentTime());
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().endAt(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_mission_ly.setVisibility(View.GONE);
                        ly_latest_mission.setVisibility(View.VISIBLE);
                        List<ItemForMissionByDay> list = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                            list.add(itemForMissionByDay);
                        }
                        Collections.reverse(list);

                        RecyclerPastMissionAdapter adapter = new RecyclerPastMissionAdapter(mContext,list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(adapter);




                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }

    private void setTimer(String date_time) {
        Date endDate = DateTimeFormatter.dateTimeParser(date_time);
        Date curDate = new Date(System.currentTimeMillis());

        long diff = endDate.getTime() - curDate.getTime();
        if(isTimerRunning){
            timer.cancel();
        }


        if (diff >= 0) {

            timer = new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long l) {
                    Log.d("타이머","isrunning");
                    isTimerRunning = true;
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

                        rest_time_display.setText("남은시간: " + time);

                        if (l < 2 * 60 * 60 * 1000) { //2시간
                            //holder.is_status.setImageResource(R.drawable.ready_to_check);
                        }
                    } else {
                        String time = "+ " + days + "일 " + hours + "시간 " + minutes + "분";
                        rest_time_display.setText("남은시간: " + time);

                    }
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;


                }
            }.start();

        }


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


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("홈프레그", "onResume_SearchFragment");


    }

    @Override
    public void onAttach(@NonNull Context context) {
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
        if (isTimerRunning) {
            timer.cancel();
        }
        Log.d("홈프레그", "onStart");
        //adapter.startListening();

        //loading_panel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isTimerRunning) {
            timer.cancel();
        }
        Log.d("홈프레그", "onStop");
        //adapter.stopListening();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_latest_mission:
                Intent intent = new Intent(mContext, MissionDetailActivity.class);
                intent.putExtra("mission_id", mother_id);
                mContext.startActivity(intent);

                //loadMissionDetail();
                break;


        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        Log.d("홈프레그", "onDetach");
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        switch (pos) {
            case 0:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                String key = mContext.getSharedPreferences("FirebaseKey", MODE_PRIVATE).getString("key", "");
                queryData(key);

                break;
            case 1:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                queryPastData();

                break;
            case 2:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                queryMissionData();

                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        switch (pos) {
            case 0:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                String key = mContext.getSharedPreferences("FirebaseKey", MODE_PRIVATE).getString("key", "");
                queryData(key);

                break;
            case 1:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                queryPastData();

                break;
            case 2:
                loading_panel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                queryMissionData();

                break;
        }
    }

    private void queryMissionData() {

        //String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        if (!Utils.isEmpty(user_id))
            FirebaseDatabase.getInstance().getReference().child("user_data").child(user_id).child("mission_info_list").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    infoLists.clear();
                    List<String> keyList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MissionInfoList missionInfoList = data.getValue(MissionInfoList.class);
                        infoLists.add(missionInfoList);
                        Log.d("아이템바이", data.getKey());
                        keyList.add(data.getKey());
                    }
                    setRecyclerViewByMission(keyList, infoLists);
                    recyclerView.setVisibility(View.VISIBLE);
                    loading_panel.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }
}
