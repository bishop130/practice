package com.suji.lj.myapplication.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsSelectedAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.FriendsActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiModeFragment extends Fragment implements RecyclerFriendsAdapter.OnFriendSelectListener, RecyclerFriendsAdapter.OnFriendUnSelectListener, RecyclerFriendsSelectedAdapter.OnRefreshSelectListener {

    RecyclerView recyclerView;
    Realm realm;

    RecyclerFriendsAdapter adapter;
    RecyclerFriendsSelectedAdapter selectedAdapter;
    List<ItemForFriends> listItem;
    List<ItemForFriends> selected_item;
    RealmResults<ItemForFriends> tempList;
    RealmResults<ItemForFriends> tempList2;
    Utils utils = new Utils();
    RecyclerView confirmed_recycler;
    TextView confirm_button;
    RealmResults<ItemForFriends> realmResults;
    Context mContext;


    public MultiModeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_multi_mode, container, false);

        realm = Realm.getDefaultInstance();
        recyclerView = view.findViewById(R.id.recycler_view_contact);
        confirmed_recycler = view.findViewById(R.id.recycler_view_confirmed);


        requestFriends();

        return view;
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
                Log.d("친구선택",item.getId()+"  아이디");
                Log.d("친구선택",item.getUuid()+"  유유아이디");
                Log.d("친구선택",item.getName()+" 이름");
                Log.d("친구선택",item.isSelected()+"  셀렉트");

            }
        });
        selected_item.add(item);


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
                ItemForFriends itemForFriends = realm.where(ItemForFriends.class).equalTo("position",position).findFirst();
                itemForFriends.deleteFromRealm();

            }
        });



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
                ItemForFriends itemForFriends = realm.where(ItemForFriends.class).equalTo("position",position).findFirst();
                itemForFriends.deleteFromRealm();
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("멀티","onattach");
    }
}
