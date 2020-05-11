package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Adapters.RecyclerDistributionAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsSelectedAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.FriendsActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiModeFragment extends Fragment implements RecyclerFriendsAdapter.OnFriendSelectListener, RecyclerFriendsAdapter.OnFriendUnSelectListener, RecyclerFriendsSelectedAdapter.OnRefreshSelectListener, RecyclerDistributionAdapter.OnCheckPortionListener {

    RecyclerView recyclerView;
    Realm realm;

    RecyclerFriendsAdapter adapter;
    RecyclerFriendsSelectedAdapter selectedAdapter;
    List<ItemForFriends> listItem;
    List<ItemForFriends> selected_item;
    Utils utils = new Utils();
    RecyclerView confirmed_recycler;
    RealmResults<ItemForFriends> realmResults;
    Context mContext;
    TextView custom_portion;
    Context context;
    RecyclerView recycler_distribution;
    RecyclerDistributionAdapter recyclerDistributionAdapter;
    BottomSheetDialog bottomSheetDialog;
    TextView total_portion;
    List<Integer> portionList = new ArrayList<>();
    int portion_id = 0;
    TextView equal_distribution;
    TextView rank_distribution;
    TextView confirm;
    EditText if_fail;
    int amount = 0;
    private boolean hasFractionalPart;
    TextView amount_warning;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");

    OnResetAmountFromMultiListener onResetAmountFromMultiListener;

    public MultiModeFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_multi_mode, container, false);

        realm = Realm.getDefaultInstance();
        recyclerView = view.findViewById(R.id.recycler_view_contact);
        confirmed_recycler = view.findViewById(R.id.recycler_friend_select);
        custom_portion = view.findViewById(R.id.custom_portion);
        if_fail = view.findViewById(R.id.if_fail);
        amount_warning = view.findViewById(R.id.amount_warning);



        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        if_fail.setText(Utils.makeNumberComma(item.getMulti_amount()));
        if_fail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());
                    if(!v.getText().toString().equals("")) {
                        amount = Integer.valueOf(v.getText().toString().replaceAll(",", ""));
                    }else{
                        amount = 0;
                    }




                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                            item.setMulti_amount(amount);
                        }
                    });

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    onResetAmountFromMultiListener.onResetAmountFromMulti();


                    return true;
                }
                return false;
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
                    onResetAmountFromMultiListener.onResetAmountFromMulti();
                    amount_warning.setText("최소금액 1,000원");
                    amount_warning.setVisibility(View.VISIBLE);
                    //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                } else {
                    amount = Integer.valueOf(s.toString().replaceAll(",", ""));
                    if(amount>100000){
                        amount_warning.setText("최대금액 100,000원");
                        amount_warning.setVisibility(View.VISIBLE);
                    }else {

                        if (amount < 1000) {
                            amount_warning.setText("최소금액 1,000원");
                            amount_warning.setVisibility(View.VISIBLE);
                            //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                        } else {
                            amount_warning.setVisibility(View.INVISIBLE);

                        }
                    }

                    onResetAmountFromMultiListener.onResetAmountFromMulti();
                }


            }
        });

        SpannableString content = new SpannableString("정해진 비율");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        custom_portion.setText(content);


        custom_portion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_item.size() > 0)
                    createBottomSheet();
            }
        });
        requestFriends();

        return view;
    }


    private void createBottomSheet() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_distribution, null);
        recycler_distribution = view.findViewById(R.id.recycler_distribution);
        total_portion = view.findViewById(R.id.total_portion);
        equal_distribution = view.findViewById(R.id.equal_distribution);
        rank_distribution = view.findViewById(R.id.rank_distribution);
        confirm = view.findViewById(R.id.confirm);

        equal_distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portion_id = 0;
                portionList = Utils.makeBalancePortion(selected_item.size() + 1);
                setRecyclerViewPortion(portionList);
            }
        });
        rank_distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portion_id = 1;
                portionList = Utils.makeRankPriorityPortion(selected_item.size() + 1);
                setRecyclerViewPortion(portionList);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.is100(portionList)) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                            RealmList<ItemPortion> realmList = new RealmList<>();
                            for(int i =0; i<portionList.size(); i++){
                                ItemPortion portion = realm.createObject(ItemPortion.class);
                                portion.setPortion(portionList.get(i));
                                realmList.add(portion);


                            }
                            item.setPortionList(realmList);
                            custom_portion.setTextColor(context.getResources().getColor(R.color.colorSuccess));

                        }
                    });
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(context, "배분비율이 맞지 않습니다.", Toast.LENGTH_LONG).show();

                }
            }
        });

        //String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");


        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        Log.d("포션",item.getPortionList().size()+"portion");
        Log.d("포션",selected_item.size()+"selected");
        if(item.getPortionList().size()==selected_item.size()+1) {
            portionList.clear();
            for (int i = 0; i < item.getPortionList().size(); i++) {
                int portion = item.getPortionList().get(i).getPortion();
                portionList.add(portion);
            }
        }else{
            portionList = Utils.makeRankPriorityPortion(selected_item.size() + 1);

        }
        setRecyclerViewPortion(portionList);


        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void setRecyclerViewPortion(List<Integer> portionList) {
            onCheckPortion(portionList);
            recyclerDistributionAdapter = new RecyclerDistributionAdapter(context, selected_item, this, portionList);
            recycler_distribution.setLayoutManager(new LinearLayoutManager(context));
            recycler_distribution.setAdapter(recyclerDistributionAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("멀티", "onstart");
        RealmResults<ItemForFriends> realmResults2 = realm.where(ItemForFriends.class).findAll();
        //setupRecyclerView(realmResults2);
    }


    public void requestFriends() {

        // offset = 0, limit = 100
        AppFriendContext friendContext = new AppFriendContext(true, 0, 100, "asc");

        KakaoTalkService.getInstance().requestAppFriends(friendContext,
                new TalkResponseCallback<AppFriendsResponse>() {
                    @Override
                    public void onNotKakaoTalkUser() {
                        Log.d("카카오친구 ", "카카오 유저가 아님");
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onNotSignedUp() {
                        Log.d("카카오친구 ", "onNotSinedup");
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onSuccess(AppFriendsResponse result) {
                        // 친구 목록
                        Log.d("친구목록 ", result.getFriends().toString());
                        List<ItemForFriends> itemForFriendsListList = new ArrayList<>();
                        for (int i = 0; i < result.getFriends().size(); i++) {
                            ItemForFriends item = new ItemForFriends();
                            item.setName(result.getFriends().get(i).getProfileNickname());
                            item.setImage(result.getFriends().get(i).getProfileThumbnailImage());
                            item.setId(result.getFriends().get(i).getId());
                            item.setUuid(result.getFriends().get(i).getUUID());
                            item.setFavorite(result.getFriends().get(i).isFavorite().getBoolean());
                            item.setSelected(false);
                            itemForFriendsListList.add(item);

                        }
                        listItem = itemForFriendsListList;


                        realmResults = realm.where(ItemForFriends.class).findAll();

                        selected_item = realm.copyFromRealm(realmResults);
                        //selected_item = realm.where(ItemForFriends.class).findAll();
                        for (int j = 0; j < realmResults.size(); j++) {

                            Log.d("멀티", listItem.get(realmResults.get(j).getPosition()).getPosition() + "전체");
                            Log.d("멀티", realmResults.get(j).getPosition() + "선택");
                            listItem.get(realmResults.get(j).getPosition()).setSelected(true);
                            //listItem.get(realmResults.get(j).getPosition()).setAmount(realmResults.get(j).getAmount());
                            listItem.get(realmResults.get(j).getPosition()).setPosition(realmResults.get(j).getPosition());

                        }


                        setupRecyclerView(listItem);
                        recyclerViewConfirmed(selected_item);


                        // context의 beforeUrl과 afterUrl이 업데이트 된 상태.
                    }
                });
    }

    private void setupRecyclerView(List<ItemForFriends> itemList) {

        adapter = new RecyclerFriendsAdapter(getActivity(), itemList, realm, this, this);
        //adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


    }

    public void recyclerViewConfirmed(List<ItemForFriends> itemList) {

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        confirmed_recycler.setLayoutManager(layoutManager);
        //confirmed_recycler.setItemAnimator(new DefaultItemAnimator());
        selectedAdapter = new RecyclerFriendsSelectedAdapter(getActivity(), itemList, realm, this);
        confirmed_recycler.setAdapter(selectedAdapter);
    }

    @Override
    public void onFriendSelect(ItemForFriends item, int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemForFriends itemForFriends = realm.createObject(ItemForFriends.class);
                itemForFriends.setId(item.getId());
                itemForFriends.setUuid(item.getUuid());
                itemForFriends.setName(item.getName());
                itemForFriends.setImage(item.getImage());
                itemForFriends.setSelected(item.isSelected());
                itemForFriends.setPosition(item.getPosition());
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.getPortionList().clear();

            }
        });
        selected_item.add(item);
        custom_portion.setTextColor(context.getResources().getColor(R.color.errorColor));

        onResetAmountFromMultiListener.onResetAmountFromMulti();


        Log.d("라니스터", "test호출");

        selectedAdapter.notifyItemChanged(position);
        adapter.notifyDataSetChanged();
        // confirm_button.setText("(" + selected_item.size() + ") 선택완료");
    }

    @Override
    public void onFriendUnSelect(int position) {
        for (int i = 0; i < selected_item.size(); i++) {
            if (position == selected_item.get(i).getPosition()) {
                // Log.d("리브", selected_item.get(i).getDisplayName() + "/ " + selected_item.get(i).getAmount() + "/ " + selected_item.get(i).getPosition() + "/ " + i);
                selected_item.remove(i);
                selectedAdapter.notifyItemRemoved(i);
                selectedAdapter.notifyItemRangeChanged(i, selected_item.size());
            }
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemForFriends itemForFriends = realm.where(ItemForFriends.class).equalTo("position", position).findFirst();
                itemForFriends.deleteFromRealm();

                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.getPortionList().clear();

            }
        });
        onResetAmountFromMultiListener.onResetAmountFromMulti();
        //portionList.remove(portionList.size());
        custom_portion.setTextColor(context.getResources().getColor(R.color.errorColor));


        Log.d("라니스터", "test호출");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshSelect(int position) {
        listItem.get(selected_item.get(position).getPosition()).setSelected(false);
        selected_item.remove(position);
        selectedAdapter.notifyItemRemoved(position);
        selectedAdapter.notifyItemRangeChanged(position, selected_item.size());
        adapter.notifyDataSetChanged();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemForFriends itemForFriends = realm.where(ItemForFriends.class).equalTo("position", position).findFirst();
                itemForFriends.deleteFromRealm();
            }
        });


    }

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
        try {
            onResetAmountFromMultiListener = (OnResetAmountFromMultiListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }
    @Override
    public void onDetach() {
        onResetAmountFromMultiListener = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }
    @Override
    public void onCheckPortion(List<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);

        }
        Log.d("에디트", sum + "표시");

        if (sum == 100) {
            total_portion.setTextColor(context.getResources().getColor(R.color.colorSuccess));

        } else {
            total_portion.setTextColor(context.getResources().getColor(R.color.errorColor));
        }
        total_portion.setText(sum + "%");
    }

    public interface OnResetAmountFromMultiListener{
        void onResetAmountFromMulti();
    }
}
