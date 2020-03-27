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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerPastMissionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionByPastFragment extends Fragment {


    //List<ItemForMissionByDay> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout ly_swipe_refresh;
    private Context mContext;
    private ProgressBar loading_panel;
    private String user_id;

    public MissionByPastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mission_by_past, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        ly_swipe_refresh = view.findViewById(R.id.ly_swipe_refresh);
        loading_panel = view.findViewById(R.id.loadingPanel);
        user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        recyclerView.addItemDecoration(new RecyclerViewDivider(28));
        ly_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDatabase();
                ly_swipe_refresh.setRefreshing(false);
            }
        });
        getDatabase();
        // Inflate the layout for this fragment
        return view;
    }

    private void getDatabase() {
        String key = Utils.getCurrentTime();
        Double lll = Double.valueOf(key);
        long yyy = Math.round(lll) + 1000 * 60 * 30;


        //String key2 = String.valueOf(Integer.valueOf(Utils.getCurrentTime())+1000*60*30);

        Log.d("카카오세션", user_id + "  favorite");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("지난약속", Utils.getCustomTime() + "key1");
        Log.d("데이트타임", lll + "double");
        Log.d("데이트타임", yyy + 1000 * 60 * 30 + "long");
        if (!Utils.isEmpty(user_id))
            databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().endAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<ItemForMissionByDay> list = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);
                        list.add(itemForMissionByDay);

                    }

                    //no_mission_ly.setVisibility(View.GONE);
                    //ly_latest_mission.setVisibility(View.VISIBLE);
                    Log.d("지난약속",dataSnapshot.getChildrenCount()+"");


                    databaseReference.child("user_data").child(user_id).child("mission_display").orderByKey().startAt(key).endAt(Utils.getCustomTime()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                ItemForMissionByDay itemForMissionByDay = data.getValue(ItemForMissionByDay.class);


                                if(itemForMissionByDay.isSuccess()) {
                                    list.add(itemForMissionByDay);
                                    Log.d("지난약속",itemForMissionByDay.getTitle());
                                }
                            }
                            Log.d("과거", list.size() + "");
                            //Log.d("과거", list.size()+"");
                            Collections.reverse(list);

                            RecyclerPastMissionAdapter adapter = new RecyclerPastMissionAdapter(mContext, list);
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
