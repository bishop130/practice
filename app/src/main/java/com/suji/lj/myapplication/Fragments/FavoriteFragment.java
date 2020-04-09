package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.text.TextUtils;
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
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.Main2Activity;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.MultiModeActivity;
import com.suji.lj.myapplication.R;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TabLayout.OnTabSelectedListener {

    //private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private Context mContext;
    //private ProgressBar loading_panel;
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
    String mother_id;
    String user_id;
    //SwipeRefreshLayout ly_swipe_refresh;
    Fragment fa, fb, fc;
    FragmentTransaction ft;
    String key_for_day;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("홈프레그", "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        tv_mission_title = view.findViewById(R.id.tv_mission_title);
        date_display = view.findViewById(R.id.date_display);
        time_display = view.findViewById(R.id.time_display);
        address_display = view.findViewById(R.id.address_display);
        no_mission_ly = view.findViewById(R.id.no_mission_layout);
        ly_latest_mission = view.findViewById(R.id.ly_latest_mission);
        rest_time_display = view.findViewById(R.id.rest_time_display);
        address_display_ly = view.findViewById(R.id.address_display_layout);
        rest_time_display_ly = view.findViewById(R.id.rest_time_display_layout);
        //ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);

        //loading_panel = view.findViewById(R.id.loadingPanel);
        //recyclerView = view.findViewById(R.id.recycler_day);
        //recyclerView.setNestedScrollingEnabled(false);
        appBar = view.findViewById(R.id.app_bar_layout);
        tab_ly = view.findViewById(R.id.tab_ly);


        expandedTopMargin = ((ViewGroup.MarginLayoutParams) rest_time_display_ly.getLayoutParams()).topMargin;
        collapsedTopMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COLLAPSED_TOP_MARGIN_DP, getResources().getDisplayMetrics());
        tab_ly.addTab(tab_ly.newTab().setText("약속"));
        tab_ly.addTab(tab_ly.newTab().setText("지난약속"));
        tab_ly.addTab(tab_ly.newTab().setText("전체약속"));

        ft = getChildFragmentManager().beginTransaction();
        tab_ly.addOnTabSelectedListener(this);
        ly_latest_mission.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(mContext, MissionDetailActivity.class);
                intent.putExtra("mission_id", mother_id);
                mContext.startActivity(intent);
            }
        });
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

        FloatingActionButton fab = view.findViewById(R.id.fab_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SingleModeActivity.class));
            }
        });



        //recyclerView.addItemDecoration(new RecyclerViewDivider(36));
/*
        fabs.setOnClickListener(new View.OnClickListener() {
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

 */
        String key = DateTimeUtils.getCurrentTime();
        queryData(key);

        return view;
    }

    private void queryData(String key) {
        Log.d("카카오세션", user_id + "  favorite");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("데이트타임", DateTimeUtils.getCurrentTime());
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().limitToFirst(2).startAt(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    no_mission_ly.setVisibility(View.GONE);
                    ly_latest_mission.setVisibility(View.VISIBLE);

                    List<ItemForMissionByDay> list = new ArrayList<>();
                    Log.d("수정", list.size() + "사이즈");
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                        list.add(itemForMissionByDay);
                    }
                    if (list.size() == 1) {
                        if (list.get(0).isSuccess()) {
                            //String key = list.get(1).getDate() + list.get(1).getTime();
                            //queryData(key);
                            Log.d("수정", "사이즈 1 성공이라면");
                        } else {
                            Log.d("수정", "사이즈 1 실패라면");
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

                            //replaceFragment(new MissionByDayFragment(key_for_day));
                            //checkResult(key);


                        }

                    }
                    if (list.size() > 1) {

                        if (list.get(0).isSuccess()) {
                            String key = list.get(1).getDate() + list.get(1).getTime();
                            queryData(key);
                        } else {
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
                            if (list.size() != 1) {
                                key_for_day = list.get(1).getDate() + list.get(1).getTime();
                                if (!isAdded()) return;
                                replaceFragment(new MissionByDayFragment(key_for_day));
                                //checkResult(key);

                            }

                        }

                    } else {
                        Log.d("수정", "사이즈 0 없음");
                        //loading_panel.setVisibility(View.GONE);
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

    private void setTimer(String date_time) {
        Date endDate = DateTimeFormatter.dateTimeParser(date_time);
        Date curDate = new Date(System.currentTimeMillis());

        long diff = endDate.getTime() - curDate.getTime();
        if (isTimerRunning) {
            timer.cancel();
        }

        if (diff >= 0) {

            timer = new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long l) {
                    Log.d("타이머", "isrunning");
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
                        if (l < 10 * 60 * 1000) { //10분
                            String time2 = "+ " + minutes + "분 " + seconds + "초";
                            rest_time_display.setText("남은시간: " + time2);
                        } else {
                            String time = "+ " + hours + "시간 " + minutes + "분";

                            rest_time_display.setText("남은시간: " + time);
                        }


                    } else {
                        String time = "+ " + days + "일 " + hours + "시간 " + minutes + "분";
                        rest_time_display.setText("남은시간: " + time);

                    }
                }

                @Override
                public void onFinish() {
                    queryData(Utils.getCurrentTime());
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


    @Override
    public void onStart() {
        super.onStart();

        fa = null;
        fb = null;
        fc = null;
        Log.d("홈프레그", "TabPosition" + tab_ly.getSelectedTabPosition());
        //tabFragment(tab_ly.getSelectedTabPosition());


        //addFragment(new MissionByDayFragment());


        //getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.fragment_container)).commit();
        //getChildFragmentManager().beginTransaction().remove(new MissionByMissionFragment()).commit();

        Log.d("홈프레그", "onStart");
        //adapter.startListening();

        //loading_panel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("홈프레그", "onStop");
        //adapter.stopListening();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("홈프레그", "onDetach");
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        tabFragment(pos);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        tabFragment(pos);
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment).commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment).commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
    }

    private void tabFragment(int pos) {
        switch (pos) {
            case 0:
                if (fa == null) {
                        fa = new MissionByDayFragment(key_for_day);
                        addFragment(fa);

                    Log.d("홈프레그", "fa is null");
                }

                if (fa != null) {
                    showFragment(fa);
                    Log.d("홈프레그", "fa is showing");
                }
                if (fb != null) {
                    hideFragment(fb);
                    Log.d("홈프레그", "fb is hiding");
                }
                if (fc != null) {
                    hideFragment(fc);
                    Log.d("홈프레그", "fc is hiding");
                }

                break;
            case 1:

                if (fb == null) {
                    fb = new MissionByPastFragment();
                    addFragment(fb);
                    Log.d("홈프레그", "fb is null");
                }

                if (fa != null) {
                    hideFragment(fa);
                    Log.d("홈프레그", "fa is hiding");
                }
                if (fb != null) {
                    showFragment(fb);
                    Log.d("홈프레그", "fb is showing");
                }
                if (fc != null) {
                    hideFragment(fc);
                    Log.d("홈프레그", "fc is hiding");
                }

                break;
            case 2:
                if (fc == null) {
                    fc = new MissionByMissionFragment();

                    addFragment(fc);
                    Log.d("홈프레그", "fc is null");
                }

                if (fa != null) {
                    hideFragment(fa);
                    Log.d("홈프레그", "fa is hiding");
                }
                if (fb != null) {
                    hideFragment(fb);
                    Log.d("홈프레그", "fb is hiding");
                }
                if (fc != null) {
                    showFragment(fc);
                    Log.d("홈프레그", "fc is showing");
                }


                break;
        }

    }
}
