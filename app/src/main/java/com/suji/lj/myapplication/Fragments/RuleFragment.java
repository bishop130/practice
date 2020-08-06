package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suji.lj.myapplication.Adapters.RecyclerDistributionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SelectKakaoFriendActivity;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RuleFragment extends Fragment implements View.OnClickListener, RecyclerTransferRespectivelyAdapter.OnResetAmountDisplayListener, RecyclerDistributionAdapter.OnCheckPortionListener {

    RecyclerView rvFriend;
    Activity activity;
    Realm realm;
    TextView tvSelectFriend;
    EditText etPenaltySingle;
    EditText etPenaltyMulti;
    int amount = 0;
    int missionMode = 0;
    OnResetAmountFromSingleModeListener onResetAmountFromSingleModeListener;
    TextView tvSingle, tvMulti;
    LinearLayout lySingle;
    LinearLayout lyMulti;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    private boolean hasFractionalPart;
    TextView tvAmountWarning;
    TextView tvTotalPortion;
    RealmResults<ItemForFriends> friendsRealmResults;
    List<Integer> portionList = new ArrayList<>();
    TextView tvCustomPortion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_rule, container, false);

        rvFriend = view.findViewById(R.id.rvFriend);
        realm = Realm.getDefaultInstance();
        tvSelectFriend = view.findViewById(R.id.tvSelectFriend);
        etPenaltySingle = view.findViewById(R.id.etPenaltySingle);
        tvSingle = view.findViewById(R.id.tvSingle);
        tvMulti = view.findViewById(R.id.tvMulti);
        etPenaltyMulti = view.findViewById(R.id.etPenaltyMulti);
        lySingle = view.findViewById(R.id.lySingle);
        lyMulti = view.findViewById(R.id.lyMulti);
        tvAmountWarning = view.findViewById(R.id.tvAmountWarning);
        tvCustomPortion = view.findViewById(R.id.tvCustomPortion);


        tvSelectFriend.setOnClickListener(this);
        tvSingle.setOnClickListener(this);
        tvMulti.setOnClickListener(this);
        tvCustomPortion.setOnClickListener(this);


        friendsRealmResults = realm.where(ItemForFriends.class).findAll();
        tvSingle.performClick();

        etPenaltyManage(etPenaltySingle);
        etPenaltyManage(etPenaltyMulti);
        //RecyclerDistributionAdapter recyclerDistributionAdapter = new RecyclerDistributionAdapter(activity, 2,this, portionList);


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            Log.d("디스플레이", "onresult");

            friendsRealmResults = realm.where(ItemForFriends.class).findAll();
            setTransferRecyclerView(friendsRealmResults);
            onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

        }

    }

    private void setTransferRecyclerView(RealmResults<ItemForFriends> realmResults) {

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(activity);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        rvFriend.setLayoutManager(layoutManager);
        RecyclerTransferRespectivelyAdapter adapter = new RecyclerTransferRespectivelyAdapter(activity, realmResults, realm, this);
        rvFriend.setAdapter(adapter);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        try {
            onResetAmountFromSingleModeListener = (OnResetAmountFromSingleModeListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelectFriend:
                Log.d("여기요", "tvSelectFried");
                //tvSelectFriend.setVisibility(View.VISIBLE);
                Intent intent = new Intent(activity, SelectKakaoFriendActivity.class);
                startActivityForResult(intent, 2);
                break;

            case R.id.tvSingle:
                lySingle.setVisibility(View.VISIBLE);
                lyMulti.setVisibility(View.GONE);
                tvSingle.setBackground(activity.getResources().getDrawable(R.drawable.rounded_filled));
                tvSingle.setTextColor(activity.getResources().getColor(R.color.White));
                tvMulti.setBackground(activity.getResources().getDrawable(R.drawable.rounded_stroke));
                tvMulti.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                etPenaltyMulti.getText().clear();
                amount = 0;
                missionMode = 0;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                        item.setSingle_penalty(amount);
                        item.setMission_mode(missionMode);
                        RealmResults<ItemPortion> itemPortions = realm.where(ItemPortion.class).findAll();
                        itemPortions.deleteAllFromRealm();
                    }
                });
                onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();


                break;
            case R.id.tvMulti:
                lySingle.setVisibility(View.GONE);
                lyMulti.setVisibility(View.VISIBLE);
                tvMulti.setBackground(activity.getResources().getDrawable(R.drawable.rounded_filled));
                tvMulti.setTextColor(activity.getResources().getColor(R.color.White));
                tvSingle.setBackground(activity.getResources().getDrawable(R.drawable.rounded_stroke));
                tvSingle.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                etPenaltySingle.getText().clear();
                amount = 0;
                missionMode = 1;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                        item.setMulti_penalty(amount);
                        item.setMission_mode(missionMode);
                    }
                });
                onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

                break;

            case R.id.tvCustomPortion:

                createBottomSheet();
                break;


        }
    }

    @Override
    public void onResetAmountDisplay() {

        onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

    }


    public interface OnResetAmountFromSingleModeListener {
        void onResetAmountFromSingleMode();


    }

    private void etPenaltyManage(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
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
                editText.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = editText.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = editText.getSelectionStart();
                    if (hasFractionalPart) {
                        editText.setText(df.format(n));
                    } else {
                        editText.setText(dfnd.format(n));
                    }
                    endlen = editText.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= editText.getText().length()) {
                        editText.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        editText.setSelection(editText.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }
                editText.addTextChangedListener(this);
                if (s.toString().equals("")) {
                    amount = 0;
                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
                    tvAmountWarning.setText("*최소금액 1,000원");
                    tvAmountWarning.setVisibility(View.VISIBLE);
                    //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                } else {
                    amount = Integer.valueOf(s.toString().replaceAll(",", ""));
                    if (amount > 100000) {
                        tvAmountWarning.setText("최대금액 100,000원");
                        tvAmountWarning.setVisibility(View.VISIBLE);
                    } else {

                        if (amount < 1000) {
                            tvAmountWarning.setText("*최소금액 1,000원");
                            tvAmountWarning.setVisibility(View.VISIBLE);
                            //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                        } else {
                            tvAmountWarning.setVisibility(View.INVISIBLE);

                        }
                    }

                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();
                }


            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

                            if (editText.getId() == R.id.etPenaltySingle) {
                                //amount = Integer.valueOf(editText.getText().toString());
                                item.setMission_mode(0);
                                item.setSingle_penalty(amount);

                            } else {

                                item.setMission_mode(1);
                                item.setMulti_penalty(amount);

                            }


                        }
                    });

                    onResetAmountFromSingleModeListener.onResetAmountFromSingleMode();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    editText.clearFocus();


                    return true;
                }

                return false;
            }
        });


    }

    private void createBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_distribution, null);
        RecyclerView rvDistribution = view.findViewById(R.id.rvDistribution);
        tvTotalPortion = view.findViewById(R.id.tvTotalPortion);
        TextView tvEqualDistribution = view.findViewById(R.id.tvEqualDistribution);
        TextView tvRankDistribution = view.findViewById(R.id.tvRankDistribution);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);

        int numOfPeople = friendsRealmResults.size() + 1;
        portionList = Utils.makeRankPriorityPortion(numOfPeople);
        onCheckPortion(portionList);
        RecyclerDistributionAdapter recyclerDistributionAdapter = new RecyclerDistributionAdapter(activity, numOfPeople, RuleFragment.this, portionList);
        rvDistribution.setLayoutManager(new LinearLayoutManager(activity));
        rvDistribution.setAdapter(recyclerDistributionAdapter);


        tvEqualDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portionList = Utils.makeBalancePortion(numOfPeople);

                RecyclerDistributionAdapter recyclerDistributionAdapter = new RecyclerDistributionAdapter(activity, numOfPeople, RuleFragment.this, portionList);
                rvDistribution.setLayoutManager(new LinearLayoutManager(activity));
                rvDistribution.setAdapter(recyclerDistributionAdapter);
            }
        });
        tvRankDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portionList = Utils.makeRankPriorityPortion(numOfPeople);
                RecyclerDistributionAdapter recyclerDistributionAdapter = new RecyclerDistributionAdapter(activity, numOfPeople, RuleFragment.this, portionList);
                rvDistribution.setLayoutManager(new LinearLayoutManager(activity));
                rvDistribution.setAdapter(recyclerDistributionAdapter);
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.is100(portionList)) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                            RealmList<ItemPortion> realmList = new RealmList<>();
                            for (int i = 0; i < portionList.size(); i++) {
                                ItemPortion portion = realm.createObject(ItemPortion.class);
                                portion.setPortion(portionList.get(i));
                                realmList.add(portion);


                            }
                            item.setPortionList(realmList);
                            //tv.setTextColor(activity.getResources().getColor(R.color.colorSuccess));

                        }
                    });
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(activity, "배분비율이 맞지 않습니다.", Toast.LENGTH_LONG).show();

                }
            }
        });

        //String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");


        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        if (item.getPortionList().size() == numOfPeople) {
            portionList.clear();
            for (int i = 0; i < item.getPortionList().size(); i++) {
                int portion = item.getPortionList().get(i).getPortion();
                portionList.add(portion);
            }
        } else {
            portionList = Utils.makeRankPriorityPortion(numOfPeople);

        }
        //setRecyclerViewPortion(portionList);


        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }


    @Override
    public void onCheckPortion(List<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);

        }
        Log.d("에디트", sum + "표시");

        if (sum == 100) {
            tvTotalPortion.setTextColor(activity.getResources().getColor(R.color.colorSuccess));

        } else {
            tvTotalPortion.setTextColor(activity.getResources().getColor(R.color.errorColor));
        }
        tvTotalPortion.setText(sum + "%");
    }

}
