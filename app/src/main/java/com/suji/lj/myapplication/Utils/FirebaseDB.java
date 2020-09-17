package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;
import com.suji.lj.myapplication.Adapters.NewLocationService;
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
import com.suji.lj.myapplication.SingleModeActivity;

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

        String title = item.getTitle();
        String address = item.getAddress();
        String missionId = item.getMissionId();
        String managerId = Account.getUserId(context);
        String userId = Account.getUserId(context);
        String myName = Account.getUserName(context);
        String myImage = Account.getUserThumbnail(context);
        List<ItemPortion> portionList = item.getPortionList();
        String minDate = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String maxDate = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        String minTime = item.getCalendarDayList().get(0).getTime();
        String maxTime = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getTime();
        List<ItemForDateTime> dateTimeList = item.getCalendarDayList();
        List<ItemForDateTimeCheck> dateTimeCheckList = new ArrayList<>();
        String dateTime = DateTimeUtils.getCurrentTime();


        MissionInfoList missionInfoList = new MissionInfoList();
        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();


        missionInfoList.setTitle(item.getTitle());
        missionInfoList.setAddress(item.getAddress());
        missionInfoList.setSuccess(false);
        //missionInfoList.setValid(true);
        missionInfoList.setLat(item.getLat());
        missionInfoList.setLng(item.getLng());
        missionInfoList.setPenaltyPerDay(item.getSinglePenaltyPerDay());
        missionInfoList.setMissionId(item.getMissionId());
        missionInfoList.setFailedCount(0);

        missionInfoList.setMinDate(minDate);
        missionInfoList.setMaxDate(maxDate);
        missionInfoList.setMinTime(minTime);
        missionInfoList.setMaxTime(maxTime);
        missionInfoList.setSingleMode(true);
        missionInfoList.setActivation(true);

        List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendByDay object = new ItemForFriendByDay();
            object.setFriendName(item.getFriendsList().get(i).getName());
            object.setFriendImage(item.getFriendsList().get(i).getImage());
            object.setFriendId(item.getFriendsList().get(i).getId());
            object.setSuccess(false);
            friendByDayList.add(object);
        }
        missionInfoList.setFriendByDayList(friendByDayList);
        missionInfoList.setPenaltyTotal(item.getSinglePenaltyTotal());


        missionInfoList.setSingleMode(true);

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
            object.setTimeStamp("");
            object.setSuccess(false);
            object.setDateTime(date + time);
            mission_dates.put(date, object);
        }
        missionInfoList.setMissionDates(mission_dates);

        ItemForNotification notification = new ItemForNotification();
        notification.setTitle(title);
        notification.setContent(title + " 약속이 시작됐습니다");
        notification.setCode(Code.MISSION_START);
        notification.setSingle(true);
        notification.setDateTime(dateTime);
        notification.setMissionId(missionId);


        ItemForMissionCheck itemForMissionCheck = new ItemForMissionCheck();

        itemForMissionCheck.setUserId(user_id);
        itemForMissionCheck.setSuccess(false);
        itemForMissionCheck.setMissionId(item.getMissionId());
        itemForMissionCheck.setServerCheck(false);
        itemForMissionCheck.setSingle(true);

        String checkKey = maxDate+maxTime;

        String pushKey = databaseReference.child("user_data").child(userId).child("missionInfoList").push().getKey();
        String pushKey2 = databaseReference.child("user_data").child(userId).child("notification").push().getKey();
        String checkMissionKey = databaseReference.child("check_mission").child(checkKey).push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("user_data/" + userId + "/missionInfoList/" + pushKey, missionInfoList);
        hashMap.put("user_data/" + userId + "/notification/" + pushKey2, notification);
        hashMap.put("check_mission/" + checkKey + "/" + checkMissionKey, itemForMissionCheck);

        for (int j = 0; j < item.getCalendarDayList().size(); j++) {


            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMissionId(item.getMissionId());
            itemForMissionByDay.setSingleMode(true);
            itemForMissionByDay.setActivation(true);


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            String date = DateTimeUtils.makeDateForServer(year, month, day);

            itemForMissionByDay.setDate(date);
            itemForMissionByDay.setSuccess(false);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            itemForMissionByDay.setDateTime(date + time);

            //mission_main_list.put(date + time, itemForMissionByDay);


            String displayKey = databaseReference.child("user_data").child(user_id).child("missionDisplay").push().getKey();

            hashMap.put("user_data/" + userId + "/missionDisplay/" + displayKey, itemForMissionByDay);


        }


        databaseReference.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {


                Log.d("파이어베이스", "update완료");
                FCM.fcmPushAlarm(user_id, item.getTitle(), "약속이 생성되었습니다");

                Intent service = new Intent(context, NewLocationService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, service);
                } else {
                    context.startService(service);
                }


                ((SingleModeActivity) context).finish();

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

    public static void multiMode(MissionCartItem item, Context context, String method) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("multi_data").push();

        String title = item.getTitle();
        String address = item.getAddress();
        String missionId = item.getMissionId();
        String managerId = Account.getUserId(context);
        String userId = Account.getUserId(context);
        String myName = Account.getUserName(context);
        String myImage = Account.getUserThumbnail(context);
        List<ItemPortion> portionList = item.getPortionList();
        int totalDates = item.getCalendarDayList().size();
        String minDate = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String maxDate = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        String minTime = item.getCalendarDayList().get(0).getTime();
        String maxTime = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getTime();
        List<ItemForDateTime> dateTimeList = item.getCalendarDayList();
        List<ItemForDateTimeCheck> dateTimeCheckList = new ArrayList<>();
        double lat = Double.longBitsToDouble(context.getSharedPreferences("location_setting", MODE_PRIVATE).getLong("lat", 0));
        double lng = Double.longBitsToDouble(context.getSharedPreferences("location_setting", MODE_PRIVATE).getLong("lng", 0));
        int multiPenaltyPerDay = item.getMultiPenaltyPerDay();
        int multiPenaltyTotal = item.getMultiPenaltyTotal();

        HashMap<String, Object> hashMapMulti = new HashMap<>();

        /** mission_info_list 저장**/

        List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendByDay object = new ItemForFriendByDay();
            object.setFriendName(item.getFriendsList().get(i).getName());
            object.setFriendImage(item.getFriendsList().get(i).getImage());
            object.setFriendId(item.getFriendsList().get(i).getId());
            object.setSuccess(false);
            friendByDayList.add(object);
        }

        ItemForFriendByDay my_profile = new ItemForFriendByDay();
        my_profile.setFriendId(managerId);
        my_profile.setFriendImage(myImage);
        my_profile.setSuccess(false);
        my_profile.setFriendName(myName);

        friendByDayList.add(my_profile);


        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();
        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();


            String date = DateTimeUtils.makeDateForServer(year, month, day);
            String time = DateTimeUtils.makeTimeForServer(hour, min);


            ItemForDateTimeByList object = new ItemForDateTimeByList();
            object.setTimeStamp("");
            object.setSuccess(false);
            object.setDateTime(date + time);
            object.setFriendByDayList(friendByDayList);
            mission_dates.put(date, object);
        }

        for (int j = 0; j < item.getCalendarDayList().size(); j++) {
            ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

            itemForMissionByDay.setTitle(item.getTitle());
            itemForMissionByDay.setAddress(item.getAddress());
            itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(item.getCalendarDayList().get(j).getHour(), item.getCalendarDayList().get(j).getMin()));
            itemForMissionByDay.setLat(item.getLat());
            itemForMissionByDay.setLng(item.getLng());
            itemForMissionByDay.setMissionId(missionId);
            itemForMissionByDay.setSingleMode(false);
            itemForMissionByDay.setActivation(false);


            int year = item.getCalendarDayList().get(j).getYear();
            int month = item.getCalendarDayList().get(j).getMonth();
            int day = item.getCalendarDayList().get(j).getDay();
            String date = DateTimeUtils.makeDateForServer(year, month, day);

            itemForMissionByDay.setDate(date);
            itemForMissionByDay.setSuccess(false);


            int hour = item.getCalendarDayList().get(j).getHour();
            int min = item.getCalendarDayList().get(j).getMin();
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            itemForMissionByDay.setDateTime(date + time);

            itemForMissionByDay.setFriendByDayList(friendByDayList);
        }

        /** 멀티 디비 테스트**/


        List<ItemForFriendMissionCheck> friendMissionCheckList = new ArrayList<>();
        List<ItemForFriendResponseForRequest> requestList = new ArrayList<>();
        for (int i = 0; i < item.getFriendsList().size(); i++) {
            ItemForFriendResponseForRequest request = new ItemForFriendResponseForRequest();
            request.setFriendId(item.getFriendsList().get(i).getId());
            request.setFriendImage(item.getFriendsList().get(i).getImage());
            request.setFriendName(item.getFriendsList().get(i).getName());
            request.setFriendUuid(item.getFriendsList().get(i).getUuid());
            request.setMissionSuccess(false);
            request.setMissionSuccessTime("");
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
        me.setFriendName(myName);
        me.setFriendId(managerId);
        me.setFriendImage(myImage);
        me.setAccept(1);
        me.setMissionSuccess(false);
        me.setMissionSuccessTime("");
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
        multiModeRequest.setMissionId(missionId);
        multiModeRequest.setLat(lat);
        multiModeRequest.setLng(lng);
        multiModeRequest.setManagerId(managerId);
        multiModeRequest.setManagerImage(myImage);
        multiModeRequest.setManagerName(myName);
        multiModeRequest.setFriendRequestList(requestList);
        multiModeRequest.setPortionList(portionList);
        multiModeRequest.setCalendarDayList(dateTimeList);
        multiModeRequest.setDateTimeCheckList(dateTimeCheckList);
        multiModeRequest.setRegisteredTime(DateTimeUtils.getCurrentTime());
        multiModeRequest.setPenaltyTotal(multiPenaltyTotal);
        multiModeRequest.setPenaltyPerDay(multiPenaltyPerDay);
        multiModeRequest.setMissionDates(mission_dates);

        ItemForInvitationPreview preview = new ItemForInvitationPreview();

        preview.setFriendImage(myImage);
        preview.setFriendName(myName);
        preview.setMissionId(missionId);
        preview.setTitle(title);


        ItemForNotification notification = new ItemForNotification();
        String date_time = DateTimeUtils.getCurrentTime();
        notification.setSingle(false);
        notification.setTitle(title);
        notification.setFriendName(myName);
        notification.setFriendImage(myImage);
        notification.setCode(201);
        notification.setDateTime(date_time);
        notification.setMissionId(missionId);
        notification.setForFriendByDayList(friendByDayList);
        notification.setRead(false);
        notification.setContent("약속에 친구를 초대했습니다");

        /** 초대정보 전달 **/

        for (int i = 0; i < requestList.size(); i++) {
            String friend_id = requestList.get(i).getFriendId();
            String invitationKey = databaseReference.child("user_data").child(friend_id).child("invitation").push().getKey();
            String notificationKey = databaseReference.child("user_data").child(friend_id).child("notification").push().getKey();
            databaseReference.child("user_data").child(friend_id).child("notification").push().setValue(notification);
            hashMapMulti.put("user_data/" + friend_id + "/invitation/" + invitationKey, preview);
            hashMapMulti.put("user_data/" + friend_id + "/notification/" + notificationKey, notification);


        }

        /** 테스트용 나에게 보내기**/
        String multiRequestKey = databaseReference.child("invitation_data").push().getKey();

        hashMapMulti.put("invitation_data/" + multiRequestKey, multiModeRequest);

        databaseReference.updateChildren(hashMapMulti, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String message_content = "친구를 초대했습니다";
                Log.d("서비스", message_content);
                FCM.fcmPushAlarm(userId, title, message_content);
                ((SingleModeActivity) context).finish();
            }
        });

    }

}
