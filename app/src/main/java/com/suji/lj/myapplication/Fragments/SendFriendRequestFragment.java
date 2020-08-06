package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerSendFriendRequestAdapter;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.List;

public class SendFriendRequestFragment extends Fragment implements RecyclerSendFriendRequestAdapter.OnSendRequestListener {

    Activity activity;


    RecyclerView recyclerView;
    RelativeLayout rl_is_empty;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_send_friend_request, container, false);


        recyclerView = view.findViewById(R.id.rv_send_friend_request);
        rl_is_empty = view.findViewById(R.id.rl_is_empty);


        String user_id = Account.getUserId(activity);

        databaseReference.child("user_data").child(user_id).child("send_friend_request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<ItemForFriendsList> list = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForFriendsList item = snapshot.getValue(ItemForFriendsList.class);

                        list.add(item);

                    }

                    rl_is_empty.setVisibility(View.GONE);
                    setupRecyclerView(list);

                } else {
                    setupRecyclerView(list);
                    rl_is_empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    private void setupRecyclerView(List<ItemForFriendsList> list) {


        RecyclerSendFriendRequestAdapter adapter = new RecyclerSendFriendRequestAdapter(activity, list, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onSendRequest(ItemForFriendsList item) {
        //요청 취소를 누름
        String friend_id = item.getFriend_id();
        String me_id = Account.getUserId(activity);

        /** 친구 받은요청함 지우기**/
        databaseReference.child("user_data").child(friend_id).child("receive_friend_request").orderByChild("friend_id").equalTo(me_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /** 내 보낸요청함 지우기**/
        databaseReference.child("user_data").child(me_id).child("send_friend_request").orderByChild("friend_id").equalTo(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }
}
