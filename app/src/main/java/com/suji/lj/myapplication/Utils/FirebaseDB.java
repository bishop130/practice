package com.suji.lj.myapplication.Utils;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.suji.lj.myapplication.SingleModeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class FirebaseDB {


    public static void registerMissionInfoRoot(DatabaseReference databaseReference, String user_id, String mission_id, Realm realm) {

        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        RealmResults<ContactItem> contactItemRealmResults = realm.where(ContactItem.class).findAll();
        Map<String, Object> mission_info_root = new HashMap<>();

        ArrayList<ContactItem> friends_selected_array = new ArrayList<>();
        for (int i = 0; i < contactItemRealmResults.size(); i++) {
            ContactItem contactItem = new ContactItem();
            contactItem.setDisplayName(contactItemRealmResults.get(i).getDisplayName());
            contactItem.setPhoneNumbers(contactItemRealmResults.get(i).getPhoneNumbers());
            contactItem.setAmount(contactItemRealmResults.get(i).getAmount());
            friends_selected_array.add(contactItem);
        }

        Map<String, Object> mission_children = new HashMap<>();

        mission_children.put("E" + mission_id, true);


        MissionInfoRoot missionInfoRoot = new MissionInfoRoot();
        missionInfoRoot.setMission_registered_date_time(Utils.getCurrentTime());
        missionInfoRoot.setFriends_selected_list(friends_selected_array);
        missionInfoRoot.setChildren_id(mission_children);


        mission_info_root.put("S" + mission_id, missionInfoRoot);


        databaseReference.child("user_data").child(user_id).child("mission_info_root").updateChildren(mission_info_root);
    }

    public static void registerMissionInfoList(DatabaseReference databaseReference, String user_id, String mission_id, Realm realm) {

        Map<String, Object> mission_info_list = new HashMap<>();

        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        List<ContactItemForServer> list = new ArrayList<>();
        for (int i = 0; i < realmResults.size(); i++) {
            ContactItemForServer object = new ContactItemForServer();

            String phone_num = realmResults.get(i).getPhoneNumbers().replaceAll("[^0-9]", "");
            object.setPhone_num(phone_num);
            object.setAmount(realmResults.get(i).getAmount());
            object.setFriend_name(realmResults.get(i).getDisplayName());
            list.add(object);
        }

        MissionInfoList missionInfoList = new MissionInfoList();
        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();

        missionInfoList.setFriends_selected_list(list);
        missionInfoList.setMission_title(item.getTitle());
        missionInfoList.setAddress(item.getAddress());
        missionInfoList.setSuccess(false);
        missionInfoList.setValid(true);
        missionInfoList.setLat(item.getLat());
        missionInfoList.setLng(item.getLng());
        missionInfoList.setMission_id("E" + mission_id);
        String min_date = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String max_date = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        missionInfoList.setMin_date(min_date);
        missionInfoList.setMax_date(max_date);

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
            mission_dates.put(date, object);
        }
        missionInfoList.setMission_dates(mission_dates);

        mission_info_list.put("E" + mission_id, missionInfoList);


        databaseReference.child("user_data").child(user_id).child("mission_info_list").updateChildren(mission_info_list).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("깃발", task.isComplete() + "iscomplete");
                Log.d("깃발", task.isSuccessful() + "issuccessful");


            }
        });
    }

    public static void registerCheckForServer(DatabaseReference databaseReference, String user_id, String mission_id, MissionCartItem item) {


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
            itemForServer.setRoot_id("S" + mission_id);
            itemForServer.setChildren_id("E" + mission_id);
            itemForServerArrayList.add(itemForServer);


            objectMap.put(time_id, itemForServerArrayList);
            databaseReference.child("check_for_server").child(date + time_id).push().setValue(itemForServer);

        }


    }

    public static void registerMainDisplay(Context context, DatabaseReference databaseReference, String user_id, String mission_id, MissionCartItem item) {
        Map<String, Object> mission_main_list = new HashMap<>();


        for (int j = 0; j < item.getCalendarDayList().size(); j++) {

            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMother_id("E" + mission_id);


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();

            String date = DateTimeUtils.makeDateForServer(year, month, day);
            itemForMissionByDay.setDate(date);

            itemForMissionByDay.setSuccess(false);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time = DateTimeUtils.makeTimeForServer(hour, min);

            mission_main_list.put(date + time, itemForMissionByDay);


        }


        databaseReference.child("user_data").child(user_id).child("mission_display").updateChildren(mission_main_list).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("파베전송", task.isSuccessful() + "");

                }
            }
        });


    }

    public static void registerKakaoToken(DatabaseReference databaseReference, String user_id) {
        String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String request_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();
        Map<String, String> objectMap = new HashMap<>();
        objectMap.put("access_token", access_token);
        objectMap.put("refresh_token", request_token);

        databaseReference.child("user_data").child(user_id).child("kakao_data").setValue(objectMap);

    }

    public static void registerInvitationForFriend(DatabaseReference databaseReference, Context context, Realm realm, MissionCartItem missionCartItem) {
        RealmResults<ItemForFriends> realmResults = realm.where(ItemForFriends.class).findAll();
        String user_name = Account.getUserName(context);
        String user_id = Account.getUserId(context);
        String user_thumbnail = Account.getUserThumbnail(context);

        ItemForMultiModeRequest item = new ItemForMultiModeRequest();
        item.setLat(missionCartItem.getLat());
        item.setLng(missionCartItem.getLng());
        item.setAddress(missionCartItem.getAddress());
        item.setManager_id(user_id);
        item.setManager_name("123");
        item.setTitle(missionCartItem.getTitle());
        item.setManager_thumbnail(user_thumbnail);
        item.setFail_penalty(3000);
        item.setLate_penalty(1000);
        List<ItemForFriendResponseForRequest> requestList = new ArrayList<>();
        for(int j=0; j<realmResults.size(); j++){

            ItemForFriendResponseForRequest item1 = new ItemForFriendResponseForRequest();
            item1.setFriend_id(String.valueOf(realmResults.get(j).getId()));
            item1.setFriend_name(realmResults.get(j).getName());
            item1.setFriend_uuid(realmResults.get(j).getUuid());
            item1.setThumbnail(realmResults.get(j).getImage());
            requestList.add(item1);
        }
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
            boolean select=missionCartItem.getCalendarDayList().get(i).isSelect();
            int position = missionCartItem.getCalendarDayList().get(i).getPosition();

            Log.d("날짜확인",date+"  "+time+"  ");

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

        for (int i = 0; i < realmResults.size(); i++) {

            if (realmResults.get(i).isSelected()) {
                String friend_id = String.valueOf(realmResults.get(i).getId());


                databaseReference.child("user_data").child(friend_id).child("invitation").push().setValue(item);

            }


        }

        //테스트용으로 나에게도 보내기
        databaseReference.child("user_data").child(user_id).child("invitation").push().setValue(item);
    }
}
