package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsSelectedAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.OnSingleClickListener;

import java.text.DecimalFormat;
import java.text.ParseException;

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
    EditText if_fail;
    EditText if_late;
    TextView total_amount;

    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    Context context;
    private boolean hasFractionalPart;
    RealmResults<ContactItem> realmResults2;

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
        total_amount = view.findViewById(R.id.total_amount);
        if_fail = view.findViewById(R.id.if_fail);
        if_late = view.findViewById(R.id.if_late);
        realm = Realm.getDefaultInstance();
        ly_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ContactActivity.class), 2);
            }
        });



        if_fail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
                    hasFractionalPart = true;
                } else {
                    hasFractionalPart = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("오픈뱅킹", "after_changed");
                if_fail.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = if_fail.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = if_fail.getSelectionStart();
                    if (hasFractionalPart) {
                        if_fail.setText(df.format(n));
                    } else {
                        if_fail.setText(dfnd.format(n));
                    }
                    endlen = if_fail.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= if_fail.getText().length()) {
                        if_fail.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        if_fail.setSelection(if_fail.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }
                if_fail.addTextChangedListener(this);

                amountDisplay(Integer.valueOf(s.toString().replaceAll(",","")));


            }
        });




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("싱글","onstart");
        realmResults2 = realm.where(ContactItem.class).findAll();
        setTransferRecyclerView(realmResults2);
    }

    private void setTransferRecyclerView(RealmResults<ContactItem> realmResults) {

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        single_contact.setLayoutManager(layoutManager);

        adapter = new RecyclerTransferRespectivelyAdapter(getActivity(), realmResults, realm);
        single_contact.setAdapter(adapter);

    }
    public void amountDisplay(int amount) {
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        int total = realmResults2.size()*amount;
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        Log.d("옵티마", decimalFormat.format(total));
        total_amount.setText(decimalFormat.format(total));

    }

}
