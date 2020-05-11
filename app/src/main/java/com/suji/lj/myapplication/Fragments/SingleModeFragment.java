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
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
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


    RecyclerView single_contact;
    Realm realm;
    RecyclerTransferRespectivelyAdapter adapter;
    EditText if_fail;
    EditText if_late;

    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    Context context;
    private boolean hasFractionalPart;
    RealmResults<ContactItem> realmResults2;
    int amount = 0;
    TextView amount_warning;
    TextView inform_select_friend;
    OnResetAmountFromSingleModeListener onResetAmountFromSingleModeListener;


    public SingleModeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_mode, container, false);

        single_contact = view.findViewById(R.id.single_contact);
        if_fail = view.findViewById(R.id.if_fail);
        amount_warning = view.findViewById(R.id.amount_warning);
        realm = Realm.getDefaultInstance();
        inform_select_friend = view.findViewById(R.id.inform_select_friend);

        onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

        long count = realm.where(MissionCartItem.class).count();

        if (count == 0) {
          if_fail.setText(0);
        }else{
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

            if_fail.setText(Utils.makeNumberComma(item.getSingle_amount()));
            if(item.getSingle_amount()>=1000){
                amount_warning.setVisibility(View.INVISIBLE);
            }else{
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

                            Log.d("부트", amount+"");
                            item.setSingle_amount(amount);

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
        if_fail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!if_fail.getText().toString().equals("")) {
                        amount = Integer.valueOf(if_fail.getText().toString().replaceAll(",", ""));
                    } else {
                        amount = 0;
                    }
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

                            item.setSingle_amount(amount);

                        }
                    });
                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                }
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
                if (s.toString().equals("")) {
                    amount = 0;
                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
                    amount_warning.setText("*최소금액 1,000원");
                    amount_warning.setVisibility(View.VISIBLE);
                    //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                } else {
                    amount = Integer.valueOf(s.toString().replaceAll(",", ""));
                    if (amount > 100000) {
                        amount_warning.setText("최대금액 100,000원");
                        amount_warning.setVisibility(View.VISIBLE);
                    } else {

                        if (amount < 1000) {
                            amount_warning.setText("*최소금액 1,000원");
                            amount_warning.setVisibility(View.VISIBLE);
                            //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                        } else {
                            amount_warning.setVisibility(View.INVISIBLE);

                        }
                    }

                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
                }


            }
        });
        realmResults2 = realm.where(ContactItem.class).findAll();
        setTransferRecyclerView(realmResults2);
        inform_select_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inform_select_friend.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getContext(), ContactActivity.class);
                intent.putExtra("amount", amount);
                startActivityForResult(intent, 2);
            }
        });
        SpannableString content = new SpannableString("+ 친구추가");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        inform_select_friend.setText(content);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("싱글", "onstart");

    }

    private void setTransferRecyclerView(RealmResults<ContactItem> realmResults) {

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        single_contact.setLayoutManager(layoutManager);
        adapter = new RecyclerTransferRespectivelyAdapter(getActivity(), realmResults, realm, this);
        single_contact.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Log.d("리절트", "들어가자");
            realmResults2 = realm.where(ContactItem.class).findAll();
            setTransferRecyclerView(realmResults2);
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

    public interface OnResetAmountFromSingleModeListener{
        void onResetAmountFromSingleMode();


    }
}
