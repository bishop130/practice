package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerFriendAdapter;
import com.suji.lj.myapplication.FriendSearchActivity;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendFragment extends Fragment {


    private RecyclerView rv_friend;
    private Activity activity;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private LinearLayout ly_if_no_friend;
    private TextView tv_find_friend;
    String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_friend, container, false);



        rv_friend = view.findViewById(R.id.rv_friend);
        ly_if_no_friend = view.findViewById(R.id.lyIfNoFriend);
        tv_find_friend = view.findViewById(R.id.tv_find_friend);
        userId = Account.getUserId(activity);


        tv_find_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FriendSearchActivity.class);
                activity.startActivity(intent);
            }
        });


        databaseReference.child("user_data").child(userId).child("friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemForFriendsList> list = new ArrayList<>();
                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForFriendsList item = snapshot.getValue(ItemForFriendsList.class);


                        list.add(item);


                    }
                    ly_if_no_friend.setVisibility(View.GONE);


                    setupRecyclerView(list);


                } else {

                    ly_if_no_friend.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    private void setupRecyclerView(List<ItemForFriendsList> list) {

        RecyclerFriendAdapter adapter = new RecyclerFriendAdapter(activity,list);
        rv_friend.setLayoutManager(new LinearLayoutManager(activity));
        rv_friend.setAdapter(adapter);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }



}
