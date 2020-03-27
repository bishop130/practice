package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.suji.lj.myapplication.Adapters.RecyclerInvitationAdapter;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MultiRequestFragment extends Fragment {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_multi_request, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);



        loadMissionRequest();
        return view;
    }


    private void loadMissionRequest(){
        String user_id = getContext().getSharedPreferences("Kakao", Context.MODE_PRIVATE).getString("token","");
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        if(!TextUtils.isEmpty(user_id)) {
            mRootRef.child("user_data").child(user_id).child("invitation").orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<ItemForMultiModeRequest> list = new ArrayList<>();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {

                        Log.d("요청1",snapshot.getKey());
                        ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);

                        Log.d("요청1",item.getAddress());
                        Log.d("요청1",item.getTitle());
                        Log.d("요청1",item.getManager_name());
                        //Log.d("요청1",snapshot.getKey());

                        list.add(item);
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Log.d("요청2",snap.getKey());


                            //

                        }



                    }

                    setupRecyclerViewRequest(list);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void setupRecyclerViewRequest(List<ItemForMultiModeRequest> list){
        RecyclerInvitationAdapter recyclerAdapter = new RecyclerInvitationAdapter(getContext(),list);
        //recyclerAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

    }
}
