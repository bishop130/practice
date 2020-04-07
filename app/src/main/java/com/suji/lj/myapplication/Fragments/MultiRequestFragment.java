package com.suji.lj.myapplication.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.suji.lj.myapplication.InvitationInfoActivity;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MultiRequestFragment extends Fragment {

    RecyclerView recyclerView;
    Context context;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_multi_request, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progress_bar);



        loadMissionRequest();
        return view;
    }


    private void loadMissionRequest(){
        String user_id = Account.getUserId(context);
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        if(!TextUtils.isEmpty(user_id)) {
            mRootRef.child("user_data").child(user_id).child("invitation").orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<ItemForMultiModeRequest> list = new ArrayList<>();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {

                        String key = snapshot.getKey();
                        ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);

                        if(item!=null)
                        item.setMission_key(key);

                        list.add(item);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

/*
    private static class LoadingAsyncTask extends AsyncTask<String, Void, String> {

        String key;
        private WeakReference<MultiRequestFragment> weakReference;
        private LoadingAsyncTask(String key,MultiRequestFragment fragment) {
            this.key = key;
            weakReference = new WeakReference<>(fragment);
        }


        // only retain a weak reference to the activity


        @Override

        protected void onPreExecute() {

            weakReference.get().progressBar.setVisibility(View.VISIBLE);


            super.onPreExecute();

        }





        @Override

        protected String doInBackground(String... strings) {



            String abc = "Parsing & Download OK!!!";



            return abc;

        }



        @Override

        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            MultiRequestFragment fragment = weakReference.get();
            if (fragment == null || fragment.getActivity() == null || fragment.getActivity().isFinishing()) {
                return;
            }

            fragment.progressBar.setVisibility(View.GONE);


        }

    }

 */



}
