package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Adapters.RecyclerContactFriendAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsSelectedAdapter;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class FriendSearchFragment extends Fragment implements RecyclerContactFriendAdapter.OnAddSelectedListener, ContactActivity.OnUnCheckFromSelectToFriendListener {


    RecyclerContactFriendAdapter adapter;
    Context context;
    private Realm realm;
    private List<ItemForFriends> listItem;
    private RecyclerView recyclerView;
    private RealmResults<ContactItem> realmResults;

    public FriendSearchFragment(Context context, Realm realm, ContactActivity.OnUnCheckFromSelectToFriendListener onUnCheckFromSelectToFriendListener) {
        this.context = context;
        this.realm = realm;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friend_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);


        //1회 실패시 벌칙금  3,000원
        //분배율에 따라 누적벌칙금은 미션종료일에 분배.
        //최대실패가능횟수만 증거금으로 설정(벌칙금을 제외한 차액은 환급)
        //균등 분배,성공비율별 분배,
        requestFriends();



        return view;

    }

    private void setupRecyclerView(List<ItemForFriends> itemList) {

        adapter = new RecyclerContactFriendAdapter(context, itemList, this);
        //adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);


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


                        realmResults = realm.where(ContactItem.class).findAll();

                        //selected_item = realm.copyFromRealm(realmResults);
                        //selected_item = realm.where(ItemForFriends.class).findAll();
                        for (int j = 0; j < realmResults.size(); j++) {
                            if (realmResults.get(j).getContact_or_friend() == 1) {

                                Log.d("멀티", listItem.get(realmResults.get(j).getPosition()).getPosition() + "전체");
                                Log.d("멀티", realmResults.get(j).getPosition() + "선택");
                                listItem.get(realmResults.get(j).getFriend_position()).setSelected(true);
                                //listItem.get(realmResults.get(j).getPosition()).setAmount(realmResults.get(j).getAmount());
                                //listItem.get(realmResults.get(j).getFriend_position()).setPosition(realmResults.get(j).getPosition());

                            }

                        }


                        setupRecyclerView(listItem);


                        // context의 beforeUrl과 afterUrl이 업데이트 된 상태.
                    }
                });
    }

    @Override
    public void onAddSelected() {

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onUnCheckFromSelectToFriend(int position) {


        listItem.get(position).setSelected(false);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ContactActivity) {
            ContactActivity headlinesFragment = (ContactActivity) getActivity();
            headlinesFragment.setOnUnCheckFromSelectToFriendListener(this);
        }
    }

    /*
1회 실패시 3,000원

    상품가격대 설정 3,000원 ~ 5,000원,  상대가 원하는 상품선물
    내가 원하는 상품 고르기

    모두 성공? 아무도 못받아
    모두 실패? 서로가 서로에게 선물 랜덤 1대1 매칭
    공동 1등 4명 꼴등 한명이 1등중 한명에게 임의로 선물
    공동 1등 3명 2등 1명 3등 2명 꼴등 1명
    실패한 비율대로 등수비율대로

실패비율
6명이서 총 10일이라면

0번 실패 2명 5000원 짜리를 2500원에 선물 내 납입금 전액 반환

1번 실패 1명 10분의 1손해 = 500원
2번 실패 1명 10분의 2손해 = 1000원
2번 실패 1명 10분의 2손해 = 1000원
5번 실패 10분의 5손해 = 2500원

4명이서 총 4일

3400원에 사고 나머지 1666원 반

0번 실패 3명 1666,1666,1666원 할인
1번 실패 1명 100퍼 손해 -5000원
이러면 꼴등은 포기할 확률 높음


승자독식


0번 실패 2명 5000,5000

1번 실패 1명 -1000원
2번 실패 1명 -2000원
2번 실패 1명 -2000원

5번 실패 1명 -5000원
///////////////////
0번 실패 3명
1번 실패 1명 -15,000원

회당 3000원

0번 실패 1명 -0원    +5000원//10,500원퍼//9750원 = 5000원
0번 실패 1명 -0원    +5000원//10,500원//9750원 = 5000원
1번 실패 1명 -3000원 +5000원//6,000원//4500원 = 2000원//+3000원
2번 실패 1명 -6000원 +5000원//1,500원//2250원 = -1000원//-4500원
2번 실패 1명 -6000원 +5000원//1,500원//2250원 = -1000원//-4500원
5번 실패 1명 -15000원 +5000원//0원//1500 = -10000원//-15,000원

30000원 배분

300원


벌칙금분배표

2명
100:0
2+1
원
1등 66.6% 33.3%
2/3, 1/3

3명
1+2+3 = 6
16 33 50

1+1+2+3+5+8 = 20
5 5 10 15 25 40


0.39퍼 1.4




1회 실패




회당 5000원 3일
0번 실패 3명원      = + 750원씩
1번 실패 1명 -3000  = -2250원


750


2500,2500,2500,2500


1/16,2/16

    총 실패비율 3번

3334원 6666원

5000원 5000원

    최선 : + 커피 최악: -15000원


    누적벌칙금 분배
1등이 모두갖기 / 성공한 비율대로 나눠갖기


    기다린사람은 덜 화나고
    지각한사람은 덜 미안하게

     */

}
