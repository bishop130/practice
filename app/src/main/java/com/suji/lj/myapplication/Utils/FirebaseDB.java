package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForDateTimeCheck;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForFriendMissionCheck;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForInvitationPreview;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemForMissionCheck;
import com.suji.lj.myapplication.Items.ItemForNotification;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.MissionCheckActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FirebaseDB {

    private final static int SINGLE_MODE = 0;
    private final static int MULTI_MODE = 1;

    public static void registerMissionInfoList(DatabaseReference databaseReference, Context context, MissionCartItem item) {

        String user_id = Account.getUserId(context);

        MissionInfoList missionInfoList = new MissionInfoList();
        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();


        missionInfoList.setMission_title(item.getTitle());
        missionInfoList.setAddress(item.getAddress());
        missionInfoList.setSuccess(false);
        //missionInfoList.setValid(true);
        missionInfoList.setLat(item.getLat());
        missionInfoList.setLng(item.getLng());
        missionInfoList.setPenalty(item.getSingle_penalty());
        missionInfoList.setMission_id(item.getMission_id());
        missionInfoList.setFailed_count(0);
        String min_date = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String max_date = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        missionInfoList.setMin_date(min_date);
        missionInfoList.setMax_date(max_date);
        missionInfoList.setSingle_mode(true);
        missionInfoList.setRadius(item.getRadius());
        List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendByDay object = new ItemForFriendByDay();
            object.setFriend_name(item.getFriendsList().get(i).getName());
            object.setFriend_image(item.getFriendsList().get(i).getImage());
            object.setUser_id(item.getFriendsList().get(i).getId());
            object.setSuccess(false);
            friendByDayList.add(object);
        }
        missionInfoList.setFriendByDayList(friendByDayList);
        missionInfoList.setPenalty_amount(item.getSingle_total());


        missionInfoList.setSingle_mode(true);

        //Log.d("파베", "있기는한가" + missionCartItemList.get(i).getCalendarDayList().size());
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String date = DateTimeUtils.makeDateForServer(year, month, day);
            String time = DateTimeUtils.makeTimeForServer(hour, min);

            ItemForDateTimeByList object = new ItemForDateTimeByList();
            object.setTime_stamp("");
            object.setSuccess(false);
            object.setDate_time(date + time);
            mission_dates.put(date, object);
        }
        missionInfoList.setMission_dates(mission_dates);


        databaseReference.child("user_data").child(user_id).child("mission_info_list").push().setValue(missionInfoList);


    }


    public static void registerCheckForServer(DatabaseReference databaseReference, MissionCartItem item, Context context) {

        String user_id = Account.getUserId(context);
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();

            String date = DateTimeUtils.makeDateForServer(year, month, day);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time_id = DateTimeUtils.makeTimeForServer(hour, min);


            ItemForMissionCheck itemForMissionCheck = new ItemForMissionCheck();

            itemForMissionCheck.setUser_id(user_id);
            itemForMissionCheck.setSuccess(false);
            itemForMissionCheck.setMission_id(item.getMission_id());
            itemForMissionCheck.setServer_check(false);
            itemForMissionCheck.setSingle(true);

            databaseReference.child("check_mission").child(date + time_id).push().setValue(itemForMissionCheck);

        }


    }

    public static void registerMainDisplay(DatabaseReference databaseReference, Context context, MissionCartItem item) {
        //Map<String, Object> mission_main_list = new HashMap<>();


        String user_id = Account.getUserId(context);
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {


            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMission_id(item.getMission_id());
            itemForMissionByDay.setSingle_mode(true);


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


            databaseReference.child("user_data").child(user_id).child("mission_display").push().setValue(itemForMissionByDay);


        }


    }


    private void isCompleteMissionDisplay(int count) {


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
        item.setFail_penalty(missionCartItem.getMulti_penalty());
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
    }


    public static void multiMode(MissionCartItem item, Context context, String method) {

        String user_id = Account.getUserId(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("multi_data").push();
        DatabaseReference multi = FirebaseDatabase.getInstance().getReference().child("invitation_data").push();

        String title = item.getTitle();
        String address = item.getAddress();
        String mission_id = item.getMission_id();
        String manager_id = user_id;
        String user_name = Account.getUserName(context);
        String user_image = Account.getUserThumbnail(context);
        List<ItemPortion> portionList = item.getPortionList();
        String min_date = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String max_date = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        List<ItemForDateTime> dateTimeList = item.getCalendarDayList();
        List<ItemForDateTimeCheck> dateTimeCheckList = new ArrayList<>();
        double lat = Double.longBitsToDouble(context.getSharedPreferences("location_setting", MODE_PRIVATE).getLong("lat", 0));
        double lng = Double.longBitsToDouble(context.getSharedPreferences("location_setting", MODE_PRIVATE).getLong("lng", 0));
        int penalty = item.getMulti_penalty();
        int penaltyTotal = item.getMulti_total();

        /*


         */

        /** mission_info_list 저장**/

        MissionInfoList missionInfoList = new MissionInfoList();


        missionInfoList.setMission_title(title);
        missionInfoList.setAddress(address);
        missionInfoList.setSuccess(false);
        missionInfoList.setLat(lat);
        missionInfoList.setLng(lng);
        missionInfoList.setPenalty(penalty);
        missionInfoList.setMission_id(mission_id);
        missionInfoList.setFailed_count(0);
        missionInfoList.setMin_date(min_date);
        missionInfoList.setMax_date(max_date);
        missionInfoList.setPenalty_amount(penaltyTotal);
        missionInfoList.setSingle_mode(false);
        missionInfoList.setRadius(item.getRadius());
        missionInfoList.setItemPortionList(portionList);

        //List<ContactItemForServer> list = new ArrayList<>();
        List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendByDay object = new ItemForFriendByDay();
            object.setFriend_name(item.getFriendsList().get(i).getName());
            object.setFriend_image(item.getFriendsList().get(i).getImage());
            object.setUser_id(item.getFriendsList().get(i).getId());
            object.setSuccess(false);
            friendByDayList.add(object);
        }
        // add my profile

        ItemForFriendByDay my_profile = new ItemForFriendByDay();
        my_profile.setUser_id(user_id);
        my_profile.setFriend_image(user_image);
        my_profile.setSuccess(false);
        my_profile.setFriend_name(user_name);

        friendByDayList.add(my_profile);


        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();
        //Log.d("파베", "있기는한가" + missionCartItemList.get(i).getCalendarDayList().size());
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();


            String date = DateTimeUtils.makeDateForServer(year, month, day);
            String time = DateTimeUtils.makeTimeForServer(hour, min);


            ItemForDateTimeByList object = new ItemForDateTimeByList();
            object.setTime_stamp("");
            object.setSuccess(false);
            object.setDate_time(date + time);
            object.setFriendByDayList(friendByDayList);
            mission_dates.put(date, object);
        }
        missionInfoList.setMission_dates(mission_dates);


        missionInfoList.setFriendByDayList(friendByDayList);


        //mRootRef.child("user_data").child("multi_mode").child("M" + mission_id).child("mission_info_list").setValue(missionInfoList);


        for (int j = 0; j < item.getCalendarDayList().size(); j++) {


            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMission_id(mission_id);
            itemForMissionByDay.setSingle_mode(false);


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

            itemForMissionByDay.setFriendByDayList(friendByDayList);


            //mission_main_list.put(date + time, itemForMissionByDay);

            /** 공유데이터 하나 전송**/
            databaseReference.child("user_data").child(user_id).child("mission_display").push().setValue(itemForMissionByDay);
            databaseReference1.child("mission_display").push().setValue(itemForMissionByDay);
            // mRootRef.child("user_data").child("multi_mode").child("M" + mission_id).child("mission_display").push().setValue(itemForMissionByDay);


        }

        /** 멀티 디비 테스트**/


        List<ItemForFriendMissionCheck> friendMissionCheckList = new ArrayList<>();
        List<ItemForFriendResponseForRequest> requestList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendResponseForRequest request = new ItemForFriendResponseForRequest();
            request.setFriend_id(item.getFriendsList().get(i).getId());
            request.setThumbnail(item.getFriendsList().get(i).getImage());
            request.setFriend_name(item.getFriendsList().get(i).getName());
            request.setFriend_uuid(item.getFriendsList().get(i).getUuid());
            request.setMission_success(false);
            request.setMission_success_time("");
            request.setAccept(0);

            ItemForFriendMissionCheck itemForFriendMissionCheck = new ItemForFriendMissionCheck();
            itemForFriendMissionCheck.setUser_id(item.getFriendsList().get(i).getId());
            itemForFriendMissionCheck.setUser_image(item.getFriendsList().get(i).getImage());
            itemForFriendMissionCheck.setUser_name(item.getFriendsList().get(i).getName());
            itemForFriendMissionCheck.setUser_uuid(item.getFriendsList().get(i).getUuid());
            itemForFriendMissionCheck.setSuccess(false);
            itemForFriendMissionCheck.setSuccess_time("");


            friendMissionCheckList.add(itemForFriendMissionCheck);
            requestList.add(request);


        }
        ItemForFriendResponseForRequest me = new ItemForFriendResponseForRequest();
        me.setFriend_name(user_name);
        me.setFriend_id(user_id);
        me.setThumbnail(user_image);
        me.setAccept(1);
        me.setMission_success(false);
        me.setMission_success_time("");
        requestList.add(0, me);

        for (int i = 0; i < dateTimeList.size(); i++) {
            ItemForDateTime itemForDateTime = dateTimeList.get(i);
            ItemForDateTimeCheck itemForDateTimeCheck = new ItemForDateTimeCheck();
            itemForDateTimeCheck.setDate(itemForDateTime.getDate());
            itemForDateTimeCheck.setTime(itemForDateTime.getTime());
            itemForDateTimeCheck.setYear(itemForDateTime.getYear());
            itemForDateTimeCheck.setMonth(itemForDateTime.getMonth());
            itemForDateTimeCheck.setDay(itemForDateTime.getDay());
            itemForDateTimeCheck.setHour(itemForDateTime.getHour());
            itemForDateTimeCheck.setMin(itemForDateTime.getMin());
            itemForDateTimeCheck.setFriendMissionCheckList(friendMissionCheckList);


            dateTimeCheckList.add(itemForDateTimeCheck);
        }


        ItemForMultiModeRequest multiModeRequest = new ItemForMultiModeRequest();
        multiModeRequest.setTitle(title);
        multiModeRequest.setAddress(address);
        multiModeRequest.setMission_id(mission_id);
        multiModeRequest.setLat(lat);
        multiModeRequest.setLng(lng);
        multiModeRequest.setManager_id(manager_id);
        multiModeRequest.setManager_thumbnail(user_image);
        multiModeRequest.setManager_name(user_name);
        multiModeRequest.setFriendRequestList(requestList);
        multiModeRequest.setItemPortionList(portionList);
        multiModeRequest.setCalendarDayList(dateTimeList);
        multiModeRequest.setDateTimeCheckList(dateTimeCheckList);
        multiModeRequest.setRegister_time(DateTimeUtils.getCurrentTime());
        multiModeRequest.setFail_penalty(penalty);

        ItemForInvitationPreview preview = new ItemForInvitationPreview();

        preview.setFriend_image(user_image);
        preview.setFriend_name(user_name);
        preview.setMission_id(mission_id);
        preview.setTitle(title);


        ItemForNotification notification = new ItemForNotification();
        String date_time = DateTimeUtils.getCurrentTime();
        notification.setSingle(false);
        notification.setTitle(title);
        notification.setFriend_name(user_name);
        notification.setFriend_image(user_image);
        notification.setNotification_code(300);
        notification.setDate_time(date_time);
        notification.setMission_id(mission_id);
        notification.setForFriendByDayList(friendByDayList);
        notification.setRead(false);



        /** 초대정보 전달 **/
        multi.child("detail").setValue(multiModeRequest);
        multi.child("mission_id").setValue(mission_id);



        for (int i = 0; i < requestList.size(); i++) {
            String friend_id = requestList.get(i).getFriend_id();
            if (!friend_id.equals(user_id)) {
                // mRootRef.child("user_data").child(friend_id).child("invitation").push().setValue(map);
                databaseReference.child("user_data").child(friend_id).child("invitation").child("preview").push().setValue(preview);
                //databaseReference.child("user_data").child(friend_id).child("invitation").child("detail").push().setValue(multiModeRequest);
                databaseReference.child("user_data").child(friend_id).child("mission_info_list").push().setValue(missionInfoList);
                databaseReference.child("user_data").child(friend_id).child("notification").push().setValue(notification);

                String message_content = user_name + " 님이 " + title + "에 초대하셨습니다";

                //FCM.fcmPushAlarm(friend_id, title, message_content);


                for (int j = 0; j < item.getCalendarDayList().size(); j++) {


                    int year = item.getCalendarDayList().get(j).getYear();
                    int month = item.getCalendarDayList().get(j).getMonth();
                    int day = item.getCalendarDayList().get(j).getDay();

                    String date = DateTimeUtils.makeDateForServer(year, month, day);


                    int hour = item.getCalendarDayList().get(j).getHour();
                    int min = item.getCalendarDayList().get(j).getMin();
                    String time = DateTimeUtils.makeTimeForServer(hour, min);


                    ItemForMissionCheck itemForMissionCheck = new ItemForMissionCheck();

                    itemForMissionCheck.setUser_id(user_id);
                    itemForMissionCheck.setSuccess(false);
                    itemForMissionCheck.setMission_id(item.getMission_id());
                    itemForMissionCheck.setServer_check(false);
                    itemForMissionCheck.setSingle(false);


                    databaseReference.child("check_mission").child(date + time).push().setValue(itemForMissionCheck);

                }


            }

        }

        /** 테스트용 나에게 보내기**/
        databaseReference.child("user_data").child(user_id).child("invitation").child("preview").push().setValue(preview);
        databaseReference.child("user_data").child(user_id).child("invitation").child("detail").push().setValue(multiModeRequest);
        databaseReference.child("user_data").child(user_id).child("mission_info_list").push().setValue(missionInfoList);
        databaseReference.child("user_data").child(user_id).child("notification").push().setValue(notification);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("mission_id", mission_id);
        hashMap.put("mission_info_list", missionInfoList);


        databaseReference1.child("mission_id").setValue(mission_id);
        databaseReference1.child("mission_info_list").setValue(missionInfoList);


        String message_content = user_name + " 님이 " + title + "에 초대하셨습니다";
        Log.d("서비스", message_content);
        FCM.fcmPushAlarm(user_id, title, message_content);
        //mRootRef.child("user_data").child("multi_mode").child("M" + mission_id).child("invitation").push().setValue(multiModeRequest);


        Intent intent = new Intent(context, MissionCheckActivity.class);
        //intent.putExtra("receipt", receipt);
        intent.putExtra("method","point");
        context.startActivity(intent);

//            realm.close();


    }

}
