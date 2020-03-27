package com.suji.lj.myapplication.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;

import java.text.DecimalFormat;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleModeFragment extends Fragment {


    LinearLayout ly_add_contact;
    RecyclerView single_contact;
    Realm realm;
    RecyclerTransferRespectivelyAdapter adapter;

    public SingleModeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_mode, container, false);

        ly_add_contact = view.findViewById(R.id.ly_add_contact);
        single_contact = view.findViewById(R.id.single_contact);
        realm = Realm.getDefaultInstance();
        ly_add_contact.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                startActivityForResult(new Intent(getContext(), ContactActivity.class), 2);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("싱글","onstart");
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        setTransferRecyclerView(realmResults2);
    }

    private void setTransferRecyclerView(RealmResults<ContactItem> realmResults) {
        single_contact.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerTransferRespectivelyAdapter(getContext(), realmResults, realm);

        single_contact.setAdapter(adapter);

    }
    public void amountDisplay() {
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        int amount = 0;
        for (int i = 0; i < realmResults2.size(); i++) {
            amount = amount + realmResults2.get(i).getAmount();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        Log.d("옵티마", decimalFormat.format(amount));
        //total_amount.setText(decimalFormat.format(amount));

    }

}
