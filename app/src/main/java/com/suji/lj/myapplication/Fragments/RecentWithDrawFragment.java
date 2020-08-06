package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerPointTransactionAdapter;
import com.suji.lj.myapplication.Items.ItemForPointTransaction;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RecentWithDrawFragment extends Fragment {


    RecyclerView recyclerView;
    Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_withdraw, container, false);


        recyclerView = view.findViewById(R.id.recycler_transaction);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String user_id = Account.getUserId(activity);
        databaseReference.child("user_data").child(user_id).child("point_transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    List<ItemForPointTransaction> list = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForPointTransaction item = snapshot.getValue(ItemForPointTransaction.class);
                        list.add(item);

                    }

                    setRecyclerView(list);

                } else {

                    //이체내역이 없습니다.

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Utils.drawRecyclerViewDivider(activity, recyclerView);


        return view;
    }


    private void setRecyclerView(List<ItemForPointTransaction> list) {


        RecyclerPointTransactionAdapter adapter = new RecyclerPointTransactionAdapter(list, activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

}
