package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Adapters.FlexBoxAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;

import io.realm.Realm;
import io.realm.RealmResults;

public class TransferRespectivelyModeFragment extends Fragment implements FlexBoxAdapter.OnFriendsListListener {

    RecyclerView recyclerView;
    RecyclerTransferRespectivelyAdapter adapter;
    Realm realm;
    RealmResults<ContactItem> realmResults;
    Context context;

    public TransferRespectivelyModeFragment(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_respectively_mode, container, false);


        recyclerView = view.findViewById(R.id.recycler_transfer_respectively);
        

        realmResults = realm.where(ContactItem.class).findAll();
        Log.d("송금",realmResults.get(1).getDisplayName());
        FlexBoxAdapter flexBoxAdapter = new FlexBoxAdapter();




        setRecyclerView(realmResults);


        return view;
    }

    private void setRecyclerView(RealmResults<ContactItem> realmResults){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecyclerTransferRespectivelyAdapter(realmResults);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onFriendsList(RealmResults<ContactItem> realmResults) {
        adapter.notifyDataSetChanged();
    }
}
