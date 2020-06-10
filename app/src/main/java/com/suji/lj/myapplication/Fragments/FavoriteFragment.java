package com.suji.lj.myapplication.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TabLayout.OnTabSelectedListener {

    //private RecyclerView recyclerView;
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private Context mContext;
    //private ProgressBar loading_panel;
    //private TextView tv_mission_title;
    //private TextView date_display;
    //private TextView time_display;
    //private TextView address_display;
    CountDownTimer timer;
    LinearLayout address_display_ly;
    LinearLayout rest_time_display_ly;
    boolean isTimerRunning = false;

    boolean data_loaded = false;
    TabLayout tab_ly;
    String mother_id;
    String user_id;
    //SwipeRefreshLayout ly_swipe_refresh;
    Fragment fb, fc;
    String key_for_day;
    ArrayList<ItemForMissionByDay> list = new ArrayList<>();
    LinearLayout ly_latest;
    MissionByDayFragment missionByDayFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("홈프레그", "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");


        //ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        //loading_panel = view.findViewById(R.id.loadingPanel);
        //recyclerView = view.findViewById(R.id.recycler_day);
        //recyclerView.setNestedScrollingEnabled(false);
        tab_ly = view.findViewById(R.id.tab_ly);


        tab_ly.addTab(tab_ly.newTab().setText("약속"));
        tab_ly.addTab(tab_ly.newTab().setText("지난약속"));
        tab_ly.addTab(tab_ly.newTab().setText("전체약속"));

        tab_ly.addOnTabSelectedListener(this);


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
        Activity activity = getActivity();

        if (isAdded() && activity != null) {
            Log.d("favorite", "isAdd");
            String key = DateTimeUtils.getCurrentDateTimeForKey();
            missionByDayFragment = new MissionByDayFragment();
            addFragment(missionByDayFragment, "day");
            if (missionByDayFragment != null) {
                Log.d("favorite", "널이 아니야");
                //newQueryData(key, missionByDayFragment);
            }
            //missionByDayFragment = (MissionByDayFragment) getChildFragmentManager().findFragmentByTag("day");

        }


        //queryData(key);

        return view;
    }
/*
    private void newQueryData(String key, MissionByDayFragment fragment) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Log.d("데이트타임", DateTimeUtils.getCurrentTime());
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByChild("date_time").startAt(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForMissionByDay item = snapshot.getValue(ItemForMissionByDay.class);
                        if (item != null) {
                            if (!item.isSuccess()) {
                                list.add(item);
                            }
                        }
                    }

                    //single인지 multi인지 구분하기


                    if (fragment != null) {
                        Log.d("favorite", "favorite update");

                        fragment.update(list);
                    }


                    Log.d("들어와", "파이어베이스");
                    //addFragment(MissionByDayFragment.newInstance(list));
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // missionByDayFragment = new MissionByDayFragment();
                    //addFragment(missionByDayFragment, "day");

                }
            });
    }

 */

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
                            //rest_time_display.setText("남은시간: " + time2);
                        } else {
                            String time = "+ " + hours + "시간 " + minutes + "분";

                            //rest_time_display.setText("남은시간: " + time);
                        }


                    } else {
                        String time = "+ " + days + "일 " + hours + "시간 " + minutes + "분";
                        //rest_time_display.setText("남은시간: " + time);

                    }
                }

                @Override
                public void onFinish() {
                    //newQueryData(Utils.getCurrentTime(), new MissionByDayFragment());
                    isTimerRunning = false;


                }
            }.start();

        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        String setText = year + "년 " + monthOfYear + "월 " + dayOfMonth + "일";
        //date_display.setText(setText);
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

        missionByDayFragment = null;
        fb = null;
        fc = null;
        Log.d("홈프레그", "TabPosition" + tab_ly.getSelectedTabPosition());
        Log.d("홈프레그", "onStart");
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

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment, tag).commit();
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
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    private void tabFragment(int pos) {
        switch (pos) {
            case 0:
                Log.d("리스트", list.size() + "favorite");
                if (missionByDayFragment == null) {
                    missionByDayFragment = new MissionByDayFragment();
                    addFragment(missionByDayFragment, "day");
                    String key = DateTimeUtils.getCurrentTime();
                    //newQueryData(key, missionByDayFragment);
                    Log.d("홈프레그", "missionByMissionFragment is null");
                }

                if (missionByDayFragment != null) {
                    //   missionByDayFragment.update(list);
                    // String key = DateTimeUtils.getCurrentTime();
                    //newQueryData(key);
                    showFragment(missionByDayFragment);
                    Log.d("홈프레그", "missionByMissionFragment is showing");
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
                    addFragment(fb, "past");
                    Log.d("홈프레그", "fb is null");
                }

                if (missionByDayFragment != null) {
                    hideFragment(missionByDayFragment);
                    Log.d("홈프레그", "missionByMissionFragment is hiding");
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

                    addFragment(fc, "mission");
                    Log.d("홈프레그", "fc is null");
                }

                if (missionByDayFragment != null) {
                    hideFragment(missionByDayFragment);
                    Log.d("홈프레그", "missionByMissionFragment is hiding");
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

    public void refresh() {
        String key = DateTimeUtils.getCurrentTime();
        //newQueryData(key, new MissionByDayFragment());
        Log.d("들어와", "리프레시");

    }
}
