package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.kakao.auth.Session;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemForServer;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.MissionInfoRoot;
import com.suji.lj.myapplication.MissionCheckActivity;
import com.suji.lj.myapplication.SingleModeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class FirebaseDB {

    private final static int SINGLE_MODE = 0;
    private final static int MULTI_MODE = 1;

    public static void registerMissionInfoList(DatabaseReference databaseReference, Context context, MissionCartItem item, String receipt) {

        String user_id = Account.getUserId(context);

        //Map<String, Object> mission_info_list = new HashMap<>();


        MissionInfoList missionInfoList = new MissionInfoList();
        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();


        missionInfoList.setMission_title(item.getTitle());
        missionInfoList.setAddress(item.getAddress());
        missionInfoList.setSuccess(false);
        //missionInfoList.setValid(true);
        missionInfoList.setLat(item.getLat());
        missionInfoList.setLng(item.getLng());
        missionInfoList.setMission_id("E" + item.getMission_id());
        String min_date = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String max_date = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        missionInfoList.setMin_date(min_date);
        missionInfoList.setMax_date(max_date);
        if (item.getMission_mode() == SINGLE_MODE) {
            missionInfoList.setMission_mode(SINGLE_MODE);

            // List<ContactItemForServer> list = new ArrayList<>();
            List<ContactItem> contactItemList = item.getContactList();
            /*
            for (int i = 0; i < contactItemList.size(); i++) {
                ContactItemForServer object = new ContactItemForServer();

                String phone_num = contactItemList.get(i).getPhoneNumbers().replaceAll("[^0-9]", "");
                object.setPhone_num(phone_num);
                //object.setAmount(realmResults.get(i).getAmount());
                object.setFriend_image(contactItemList.get(i).get);
                object.setFriend_uuid(contactItemList.get(i).getUuid());
                object.setFriend_id(String.valueOf(friendsList.get(i).getId()));
                object.setFriend_name(contactItemList.get(i).getDisplayName());
                object.setContact_or_friend(contactItemList.get(i).getContact_or_friend());

                list.add(object);
            }

             */
            missionInfoList.setFriends_selected_list(contactItemList);
        } else {
/*
            missionInfoList.setMission_mode(MULTI_MODE);

            List<ContactItemForServer> list = new ArrayList<>();
            List<ItemForFriends> friendsList = item.getFriendsList();
            for (int i = 0; i < friendsList.size(); i++) {
                ContactItemForServer object = new ContactItemForServer();

                object.setFriend_name(friendsList.get(i).getName());
                object.setFriend_image(friendsList.get(i).getImage());
                object.setFriend_uuid(friendsList.get(i).getUuid());
                object.setFriend_id(String.valueOf(friendsList.get(i).getId()));

                list.add(object);
            }
            missionInfoList.setFriends_selected_list(list);

 */
        }
        //missionInfoList.setBank_name(item.getBank_name());
        //missionInfoList.setAccount_num(item.getAccount_num());
        //missionInfoList.setAccount_holder(item.getAccount_holder());

        missionInfoList.setPenalty_amount(item.getSingle_amount());


        missionInfoList.setMission_mode(item.getMission_mode());

        //Log.d("파베", "있기는한가" + missionCartItemList.get(i).getCalendarDayList().size());
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String date = DateTimeUtils.makeDateForServer(year, month, day);
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            //Log.d("파베", "날짜" + date);
            ItemForDateTimeByList object = new ItemForDateTimeByList();
            object.setDate(date);
            object.setTime(time);
            object.setTime_stamp("");
            object.setSuccess(false);
            object.setYear(year);
            object.setMonth(month);
            object.setDay(day);
            object.setHour(hour);
            object.setMin(min);
            mission_dates.put(date, object);
        }
        missionInfoList.setMission_dates(mission_dates);


        //mission_info_list.put("E" + item.getMission_id(), missionInfoList);


        if (item.getMission_mode() == SINGLE_MODE) {

            databaseReference.child("user_data").child(user_id).child("mission_info_list").push().setValue(missionInfoList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("파베전송", task.isComplete() + "register_mission_info_single");


                    registerCheckForServer(databaseReference, user_id, item.getMission_id(), item, context, receipt);

                }
            });
        }
    }


    public static void registerCheckForServer(DatabaseReference databaseReference, String user_id, String mission_id, MissionCartItem item, Context context, String receipt) {


        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            Map<String, Object> objectMap = new HashMap<>();
            ArrayList<ItemForServer> itemForServerArrayList = new ArrayList<>();


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();

            String date = DateTimeUtils.makeDateForServer(year, month, day);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time_id = DateTimeUtils.makeTimeForServer(hour, min);


            ItemForServer itemForServer = new ItemForServer();

            itemForServer.setUser_id(user_id);
            itemForServer.setIs_success(false);
            //itemForServer.setRoot_id("S" + mission_id);
            itemForServer.setChildren_id("E" + mission_id);


            itemForServerArrayList.add(itemForServer);


            objectMap.put(time_id, itemForServerArrayList);
            databaseReference.child("check_for_server").child(date + time_id).push().setValue(itemForServer).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("파베전송", task.isComplete() + "register_mission_info_multi_server");

                    SharedPreferences preferences = context.getSharedPreferences("onComplete", Context.MODE_PRIVATE);
                    if (preferences != null) {
                        int count = preferences.getInt("count", 0);//전송 성공 횟수

                        count++;


                        if (count == item.getCalendarDayList().size()) {
                            Log.d("파베전송", task.isComplete() + "register_mission_info_multi_display_self");

                            registerMainDisplay(databaseReference, user_id, mission_id, item, context, receipt);
                            preferences.edit().clear().apply();


                        } else {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("count", count);
                            editor.apply();

                        }
                    }


                }
            });


        }


    }

    public static void registerMainDisplay(DatabaseReference databaseReference, String user_id, String mission_id, MissionCartItem item, Context context, String receipt) {
        Map<String, Object> mission_main_list = new HashMap<>();


        for (int j = 0; j < item.getCalendarDayList().size(); j++) {


            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMission_id("E" + mission_id);
            itemForMissionByDay.setMission_mode("SINGLE");


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            String date = DateTimeUtils.makeDateForServer(year, month, day);

            itemForMissionByDay.setDate(date);
            itemForMissionByDay.setSuccess(false);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            itemForMissionByDay.setDate_time(date + time);

            //mission_main_list.put(date + time, itemForMissionByDay);

            if (user_id.equals(Account.getUserId(context))) {
                databaseReference.child("user_data").child(user_id).child("mission_display").push().setValue(itemForMissionByDay).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences preferences = context.getSharedPreferences("onComplete", Context.MODE_PRIVATE);
                            if (preferences != null) {
                                int count = preferences.getInt("count", 0);//전송 성공 횟수

                                count++;


                                if (count == item.getCalendarDayList().size()) {
                                    preferences.edit().clear().apply();
                                    Log.d("파베전송", task.isComplete() + "register_mission_info_multi_display_self");
                                    Intent intent = new Intent(context, MissionCheckActivity.class);
                                    intent.putExtra("receipt", receipt);
                                    context.startActivity(intent);



                                } else {
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("count", count);
                                    editor.apply();


                                }
                            }
                        }
                    }
                });
            }


        }


    }

    public static void registerKakaoToken(DatabaseReference databaseReference, String user_id) {
        String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String request_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();
        Map<String, String> objectMap = new HashMap<>();
        objectMap.put("access_token", access_token);
        objectMap.put("refresh_token", request_token);

        databaseReference.child("user_data").child(user_id).child("kakao_data").setValue(objectMap);

    }

    public static void registerInvitationForFriend(DatabaseReference databaseReference, Context context, MissionCartItem missionCartItem, String receipt) {
        //RealmResults<ItemForFriends> realmResults = realm.where(ItemForFriends.class).findAll();
        String user_name = Account.getUserName(context);
        String user_id = Account.getUserId(context);
        String user_thumbnail = Account.getUserThumbnail(context);
        String mission_id = missionCartItem.getMission_id();
        List<ItemForFriends> itemForFriendsList = missionCartItem.getFriendsList();

        ItemForMultiModeRequest item = new ItemForMultiModeRequest();
        item.setLat(missionCartItem.getLat());
        item.setLng(missionCartItem.getLng());
        item.setAddress(missionCartItem.getAddress());
        item.setManager_id(user_id);
        item.setManager_name(user_name);
        item.setMission_id(mission_id);
        item.setTitle(missionCartItem.getTitle());
        item.setManager_thumbnail(user_thumbnail);
        item.setFail_penalty(missionCartItem.getMulti_amount());
        item.setItemPortionList(missionCartItem.getPortionList());
        item.setLate_penalty(1000);
        List<ItemForFriendResponseForRequest> requestList = new ArrayList<>();
        for (int j = 0; j < itemForFriendsList.size(); j++) {/** 친구정보 load **/

            ItemForFriendResponseForRequest item1 = new ItemForFriendResponseForRequest();
            item1.setFriend_id(String.valueOf(itemForFriendsList.get(j).getId()));
            item1.setFriend_name(itemForFriendsList.get(j).getName());
            item1.setFriend_uuid(itemForFriendsList.get(j).getUuid());
            item1.setThumbnail(itemForFriendsList.get(j).getImage());
            item1.setAccept(0);
            requestList.add(item1);
        }
        ItemForFriendResponseForRequest me = new ItemForFriendResponseForRequest();
        me.setFriend_id(user_id);
        me.setFriend_name(user_name);
        me.setThumbnail(user_thumbnail);
        /** uuid 구하기 **/
        me.setAccept(1);
        requestList.add(me);


        item.setFriendRequestList(requestList);


        List<ItemForDateTime> list = new ArrayList<>();
        for (int i = 0; i < missionCartItem.getCalendarDayList().size(); i++) {

            ItemForDateTime dateTime = new ItemForDateTime();


            int year = missionCartItem.getCalendarDayList().get(i).getYear();
            int month = missionCartItem.getCalendarDayList().get(i).getMonth();
            int day = missionCartItem.getCalendarDayList().get(i).getDay();

            String date = DateTimeUtils.makeDateForServer(year, month, day);


            int hour = missionCartItem.getCalendarDayList().get(i).getHour();
            int min = missionCartItem.getCalendarDayList().get(i).getMin();
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            boolean select = missionCartItem.getCalendarDayList().get(i).isSelect();
            int position = missionCartItem.getCalendarDayList().get(i).getPosition();

            Log.d("날짜확인", date + "  " + time + "  ");

            dateTime.setYear(year);
            dateTime.setMonth(month);
            dateTime.setDay(day);
            dateTime.setHour(hour);
            dateTime.setMin(min);
            dateTime.setDate(date);
            dateTime.setTime(time);
            dateTime.setPosition(position);
            dateTime.setSelect(select);
            list.add(dateTime);

        }

        item.setCalendarDayList(list);

        for (int i = 0; i < itemForFriendsList.size(); i++) {

            if (itemForFriendsList.get(i).isSelected()) {
                String friend_id = String.valueOf(itemForFriendsList.get(i).getId());


                databaseReference.child("user_data").child(friend_id).child("invitation").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("파베전송", task.isComplete() + "register_mission_info_multi_invitation_friend");
                    }
                });

            }


        }

        //테스트용으로 나에게도 보내기
        databaseReference.child("user_data").child(user_id).child("invitation").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("파베전송", task.isComplete() + "register_mission_info_multi_invitation_self");
                registerMissionInfoList(databaseReference, context, missionCartItem, receipt);
                //((SingleModeActivity) context).deleteAllTemporaryData(receipt, missionCartItem);


            }
        });
    }


    public static void registerMissionInfoListForMulti(DatabaseReference databaseReference, Context context) {
        String user_id = Account.getUserId(context);


        // databaseReference.child("user_data").child("mission_info_list").push().setValue();


    }

}
