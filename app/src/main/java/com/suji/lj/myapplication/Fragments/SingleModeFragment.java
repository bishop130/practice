package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SelectKakaoFriendActivity;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleModeFragment extends Fragment implements RecyclerTransferRespectivelyAdapter.OnResetAmountDisplayListener {


    Realm realm;
    EditText if_fail;
    EditText if_late;

    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    Context context;
    private boolean hasFractionalPart;
    RealmResults<ItemForFriends> realmResults2;
    int amount = 0;
    TextView amount_warning;

    OnResetAmountFromSingleModeListener onResetAmountFromSingleModeListener;


    public SingleModeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_mode, container, false);

        if_fail = view.findViewById(R.id.if_fail);
        amount_warning = view.findViewById(R.id.amount_warning);
        realm = Realm.getDefaultInstance();

        onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

        long count = realm.where(MissionCartItem.class).count();

        if (count == 0) {
            if_fail.setText(0);
        } else {
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

            if_fail.setText(Utils.makeNumberComma(item.getSinglePenaltyPerDay()));
            if (item.getSinglePenaltyPerDay() >= 1000) {
                amount_warning.setVisibility(View.INVISIBLE);
            } else {
                amount_warning.setVisibility(View.VISIBLE);
            }
        }


        if_fail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());
                    if (!v.getText().toString().equals("")) {
                        amount = Integer.valueOf(v.getText().toString().replaceAll(",", ""));
                    } else {
                        amount = 0;
                    }
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

                            Log.d("부트", amount + "");
                            item.setSinglePenaltyPerDay(amount);

                        }
                    });
                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    return true;
                }
                return false;
            }
        });





        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("싱글", "onstart");

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Log.d("리절트", "들어가자");
            realmResults2 = realm.where(ItemForFriends.class).findAll();
            onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onResetAmountFromSingleModeListener = (OnResetAmountFromSingleModeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        onResetAmountFromSingleModeListener = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }


    @Override
    public void onResetAmountDisplay() {
        onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
    }

    public interface OnResetAmountFromSingleModeListener {
        void onResetAmountFromSingleMode();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();


    }
}
