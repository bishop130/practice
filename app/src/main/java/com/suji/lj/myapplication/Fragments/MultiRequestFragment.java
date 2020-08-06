package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.suji.lj.myapplication.Items.ItemForInvitationPreview;
import com.suji.lj.myapplication.Items.ItemForMultiInvitationKey;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiRequestFragment extends Fragment {

    RecyclerView recyclerView;
    Context context;
    ProgressBar progressBar;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<ItemForMultiModeRequest> list = new ArrayList<>();
    List<ItemForInvitationPreview> previewList = new ArrayList<>();
    RecyclerInvitationAdapter recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_multi_request, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progress_bar);

        setupRecyclerViewRequest();

        loadMissionRequest();
        return view;
    }


    private void loadMissionRequest() {

        String user_id = Account.getUserId(context);


        mRootRef.child("user_data").child(user_id).child("invitation").child("preview").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                previewList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForInvitationPreview preview = snapshot.getValue(ItemForInvitationPreview.class);
                        if (preview != null) {

                            Log.d("리퀘스트","preview");

                            previewList.add(preview);

                        }

                    }
                    setupRecyclerViewRequest();

                }
                recyclerAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setupRecyclerViewRequest() {
        recyclerAdapter = new RecyclerInvitationAdapter(getContext(), list, previewList);
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
