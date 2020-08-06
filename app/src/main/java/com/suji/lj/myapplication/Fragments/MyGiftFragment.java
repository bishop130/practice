package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerContactFriendAdapter;
import com.suji.lj.myapplication.Items.ItemMyGift;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.List;

public class MyGiftFragment extends Fragment {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    RecyclerView recyclerView;
    String userId;
    List<ItemMyGift> giftList = new ArrayList<>();
    Activity activity;
    LinearLayout lyEmptyGift;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mygift, container, false);

        recyclerView = view.findViewById(R.id.rvMyGift);
        lyEmptyGift = view.findViewById(R.id.lyEmptyGift);
        userId = Account.getUserId(activity);

        databaseReference.child("user_data").child(userId).child("gift").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                giftList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        ItemMyGift itemMyGift = shot.getValue(ItemMyGift.class);
                        giftList.add(itemMyGift);
                    }

                    MyGiftAdapter adapter = new MyGiftAdapter();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);


                } else {

                    lyEmptyGift.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    private class MyGiftAdapter extends RecyclerView.Adapter<MyGiftAdapter.ItemViewHolder> {


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_mygift, parent, false);
            final MyGiftAdapter.ItemViewHolder viewHolder = new MyGiftAdapter.ItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return giftList.size();
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {


            ImageView ivGift;
            TextView tvGiftName;
            TextView tvExpire;


            public ItemViewHolder(View itemView) {
                super(itemView);

                ivGift = itemView.findViewById(R.id.ivGift);
                tvGiftName = itemView.findViewById(R.id.tvGiftName);
                tvExpire = itemView.findViewById(R.id.tvGiftExpire);


            }
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


}
