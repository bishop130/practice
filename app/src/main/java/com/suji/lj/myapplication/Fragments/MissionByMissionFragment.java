package com.suji.lj.myapplication.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionByMissionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionDayAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiKey;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionByMissionFragment extends Fragment {


    private String user_id;
    private RecyclerView recyclerView;
    private ProgressBar loading_panel;
    private SwipeRefreshLayout ly_swipe_refresh;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Activity activity;
    int numOfData = 0;
    AlertDialog loading_dialog;

    List<MissionInfoList> singleList = new ArrayList<>();
    RelativeLayout rl_is_empty;
    TextView tv_add_mission;
    boolean isFetched = false;
    Query query;
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            loading_panel.setVisibility(View.GONE);
            if (dataSnapshot.exists()) {
                if (!isFetched) {
                    isFetched = true;
                    singleList = new ArrayList<>();
                    numOfData = (int) dataSnapshot.getChildrenCount();
                    rl_is_empty.setVisibility(View.GONE);
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Boolean isSingle = data.child("singleMode").getValue(Boolean.class);
                        if (!isSingle) {
                            String key = data.child("missionId").getValue(String.class);
                            databaseReference.child("multi_data").orderByChild("missionId").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                            MissionInfoList missionInfoList = shot.child("missionInfoList").getValue(MissionInfoList.class);
                                            singleList.add(missionInfoList);


                                        }


                                        update();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            MissionInfoList missionInfoList = data.getValue(MissionInfoList.class);
                            singleList.add(missionInfoList);


                        }


                    }
                    //setRecyclerViewByMission(keyList, list);
                    update();

                } else {

                    rl_is_empty.setVisibility(View.VISIBLE);
                    update();


                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mission_by_mission, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        loading_panel = view.findViewById(R.id.loadingPanel);
        ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        rl_is_empty = view.findViewById(R.id.rl_is_empty);
        tv_add_mission = view.findViewById(R.id.tv_add_mission);


        user_id = Account.getUserId(activity);

        tv_add_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SingleModeActivity.class);
                startActivity(intent);

            }
        });

        Utils.drawRecyclerViewDivider(activity, recyclerView);
        //recyclerView.addItemDecoration(new RecyclerViewDivider(28));
        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                query = databaseReference.child("user_data").child(user_id).child("missionInfoList");
                query.addValueEventListener(listener);
                loading_panel.setVisibility(View.VISIBLE);
                ly_swipe_refresh.setRefreshing(false);

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        query = databaseReference.child("user_data").child(user_id).child("missionInfoList");
        query.addValueEventListener(listener);


        return view;
    }

    private void update() {

        if (numOfData == singleList.size()) {

            isFetched = false;
            //query.removeEventListener(listener);
            //rl_is_empty.setVisibility(View.GONE);
            ly_swipe_refresh.setRefreshing(false);
            Collections.sort(singleList, new Comparator<MissionInfoList>() {
                @Override
                public int compare(MissionInfoList o1, MissionInfoList o2) {
                    return o1.getMinDate().compareTo(o2.getMinDate());
                }
            });
            Collections.reverse(singleList);
            Iterator<MissionInfoList> i = singleList.iterator();
            while (i.hasNext()) {
                if (!i.next().isActivation()) {
                    // Remove the last thing returned by next()
                    i.remove();
                }
            }

            if (singleList.size() == 0) {
                rl_is_empty.setVisibility(View.VISIBLE);

            }


            Log.d("들어와", "업데이트");
            RecyclerMissionByMissionAdapter adapter = new RecyclerMissionByMissionAdapter(activity, singleList, loading_dialog);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
            Log.d("들어와", "여기요");
            recyclerView.setVisibility(View.VISIBLE);
            loading_panel.setVisibility(View.GONE);
        }


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("로딩", "onpause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("로딩", "onresume");
        if (loading_dialog.isShowing()) {
            loading_dialog.dismiss();
        }
    }
}
