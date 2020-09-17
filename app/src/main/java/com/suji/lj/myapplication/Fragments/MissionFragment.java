package com.suji.lj.myapplication.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.suji.lj.myapplication.Adapters.MissionPagerAdapter;
import com.suji.lj.myapplication.Adapters.WithDrawPagerAdapter;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.MissionDetailActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class MissionFragment extends Fragment {


    TabLayout tab_ly;
    String user_id;
    ArrayList<ItemForMissionByDay> list = new ArrayList<>();
    ViewPager pager;
    LinearLayout ly_add_mission;
    Activity activity;
    //AlertDialog loading_dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("데이", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        user_id = Account.getUserId(activity);

        tab_ly = view.findViewById(R.id.tab_ly);
        pager = view.findViewById(R.id.fragment_pager);
        ly_add_mission = view.findViewById(R.id.ly_add_mission);


        tab_ly.addTab(tab_ly.newTab().setText("약속"));
        tab_ly.addTab(tab_ly.newTab().setText("전체약속"));


        MissionPagerAdapter adapter = new MissionPagerAdapter(getChildFragmentManager(), tab_ly.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_ly));

        ly_add_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).showLoadingDialog();

                Intent intent = new Intent(activity, SingleModeActivity.class);
                activity.startActivity(intent);


            }
        });


        tab_ly.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        pager.setCurrentItem(tab.getPosition());
                        break;

                    case 1:
                        pager.setCurrentItem(tab.getPosition());
                        break;


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("홈프레그", "onPause_SearchFragment");


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("홈프레그", "onResume_SearchFragment");


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
            Log.d("데이", "favorite onAttach");
        }
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


}
