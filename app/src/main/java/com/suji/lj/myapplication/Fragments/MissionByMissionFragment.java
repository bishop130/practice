package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.MissionRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerMissionProgressAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionByMissionFragment extends Fragment {


    private String user_id;
    private Context mContext;
    private RecyclerView recyclerView;
    private ProgressBar loading_panel;
    private SwipeRefreshLayout ly_swipe_refresh;

    public MissionByMissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mission_by_mission, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        loading_panel = view.findViewById(R.id.loadingPanel);
        ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        recyclerView.addItemDecoration(new RecyclerViewDivider(28));
        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMissionData();
                ly_swipe_refresh.setRefreshing(false);
            }
        });


        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        getMissionData();


        return view;
    }

    private void getMissionData(){
        FirebaseDatabase.getInstance().getReference().child("user_data").child(user_id).child("mission_info_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //infoLists.clear();
                List<MissionInfoList> list = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MissionInfoList missionInfoList = data.getValue(MissionInfoList.class);
                    list.add(missionInfoList);
                    Log.d("아이템바이", data.getKey());
                }
                //setRecyclerViewByMission(keyList, list);
                Collections.sort(list, new Comparator<MissionInfoList>() {
                    @Override
                    public int compare(MissionInfoList o1, MissionInfoList o2) {
                        return o1.getMin_date().compareTo(o2.getMin_date());
                    }
                });
                Collections.reverse(list);
                MissionRecyclerAdapter adapter = new MissionRecyclerAdapter(mContext,list);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(adapter);

                recyclerView.setVisibility(View.VISIBLE);
                loading_panel.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d("홈프레그", "onAttach_SearchFragment");
        mContext = context;

    }

}
