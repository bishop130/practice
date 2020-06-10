package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class FriendsActivity extends AppCompatActivity implements RecyclerFriendsSelectedAdapter.OnFriendsCountListener{
    RecyclerFriendsAdapter adapter;
    RecyclerFriendsSelectedAdapter selectedAdapter;
    List<ItemForFriends> listItem;
    List<ItemForFriends> selected_item = new ArrayList<>();
    RealmResults<ItemForFriends> tempList;
    RealmResults<ItemForFriends> tempList2;
    Utils utils = new Utils();
    RecyclerView recyclerView;
    RecyclerView confirmed_recycler;
    TextView confirm_button;
    Realm realm;
    RealmResults<ItemForFriends> realmResults;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.recycler_view_contact);
        confirmed_recycler = findViewById(R.id.recycler_view_confirmed);
        SearchView searchView = findViewById(R.id.contact_search);
        confirm_button = findViewById(R.id.contact_confirmed);
        realm = Realm.getDefaultInstance();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactSorting();

            }
        });
        //tempList2 = realm.where(ContactItem.class).equalTo("isSelected", true).findAll();

        requestFriends();
       // long count = realm.where(ItemForFriends.class).count();





    }
    private void setupRecyclerView(List<ItemForFriends> itemList) {

        //adapter = new RecyclerFriendsAdapter(this, itemList, realm,this,this);
        //adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }
    public void recyclerViewConfirmed(List<ItemForFriends> itemList) {
/*
        recyclerContactFriendSelectAdapter = new RecyclerContactFriendSelectAdapter(this,itemList);
        confirmed_recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        confirmed_recycler.setAdapter(recyclerContactFriendSelectAdapter);

 */
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        confirmed_recycler.setLayoutManager(layoutManager);
       // selectedAdapter= new RecyclerFriendsSelectedAdapter(getApplicationContext(), itemList, realm);
        confirmed_recycler.setAdapter(selectedAdapter);
    }
    public void refreshSelection(int position) {
        //Log.d("라니스터","포지션 activity"+ position+"   "+listItem.get(position).isSelected());


        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        listItem.get(selected_item.get(position).getPosition()).setSelected(false);
        selected_item.remove(position);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        selectedAdapter.notifyItemRemoved(position);
        selectedAdapter.notifyItemRangeChanged(position, selected_item.size());

        //listItem.remove(position);
        //recyclerContactFriendSelectAdapter.notifyItemRemoved(position);
        //recyclerContactFriendSelectAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        RealmResults<ItemForFriends> list = realm.where(ItemForFriends.class).findAll();
        for (int i = 0; i < list.size(); i++) {
            //Log.d("리브", list.get(i).getDisplayName() + "/ " + list.get(i).getAmount() + "/ " + list.get(i).getPosition() + "/ " + i);

        }

        //setupRecyclerView(listItem);


    }
    public void selectedList(ItemForFriends item, int position) {
        selected_item.add(item);


        Log.d("라니스터", "test호출");

        selectedAdapter.notifyItemChanged(position);
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


    }

    public void testDelete(int position) {
        /*
        for (int i = 0; i < selected_item.size(); i++) {
            ContactItem ct = selected_item.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_item.remove(idx);
            }
        }

         */

        //RealmResults<ContactItem> item = realm.where(ContactItem.class).findAll();
        //ContactItem item = realm.where(ContactItem.class).equalTo("position",position).findFirst();
        //item.deleteFromRealm();
        for (int i = 0; i < selected_item.size(); i++) {
            if (position == selected_item.get(i).getPosition()) {
               // Log.d("리브", selected_item.get(i).getDisplayName() + "/ " + selected_item.get(i).getAmount() + "/ " + selected_item.get(i).getPosition() + "/ " + i);
                selected_item.remove(i);
                selectedAdapter.notifyItemRemoved(i);
                selectedAdapter.notifyItemRangeChanged(i, selected_item.size());

            }
        }


        Log.d("라니스터", "test호출");
        //recyclerContactFriendSelectAdapter.notifyDataSetChanged();

        //adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


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
                        for(int i=0;i<result.getFriends().size();i++){
                            ItemForFriends item = new ItemForFriends();
                            item.setName(result.getFriends().get(i).getProfileNickname());
                            item.setImage(result.getFriends().get(i).getProfileThumbnailImage());
                            item.setId(String.valueOf(result.getFriends().get(i).getId()));
                            item.setUuid(result.getFriends().get(i).getUUID());
                            item.setFavorite(result.getFriends().get(i).isFavorite().getBoolean());
                            item.setSelected(false);
                            itemForFriendsListList.add(item);

                        }
                        listItem = itemForFriendsListList;


                        realmResults = realm.where(ItemForFriends.class).findAll();

                        selected_item = realm.copyFromRealm(realmResults);
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
    @Override
    public void onFriendsCount(int num) {
        confirm_button.setText("(" + num + ") 선택완료");


    }

    private void contactSorting() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults.deleteAllFromRealm();

                for (int i = 0; i < selected_item.size(); i++) {
                    ItemForFriends item = realm.createObject(ItemForFriends.class);
                    //item.setAmount(selected_item.get(i).getAmount());
                    item.setPosition(selected_item.get(i).getPosition());
                   // item.setPhoneNumbers(selected_item.get(i).getPhoneNumbers());
                    item.setName(selected_item.get(i).getName());
                    item.setId(selected_item.get(i).getId());
                    item.setUuid(selected_item.get(i).getUuid());
                    item.setSelected(selected_item.get(i).isSelected());
                   // Log.d("리브", selected_item.get(i).getAmount() + "/" + selected_item.get(i).getDisplayName() + "/" + selected_item.get(i).getPosition());
                }
                selected_item.clear();


            }
        });

        Intent intent = new Intent();
        setResult(2, intent);
        Log.d("미션카트", realmResults.size() + "");
        finish();
    }



}
