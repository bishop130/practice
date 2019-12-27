package com.suji.lj.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.FlexBoxAdapter;
import com.suji.lj.myapplication.Adapters.MissionCartListAdapter;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.ItemForServer;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.MissionInfoRoot;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MissionCartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<DateItem> dateItemArrayList = new ArrayList<>();
    ArrayList<ContactItem> contactItemArrayList = new ArrayList<>();
    List<MissionCartItem> missionCartItemList = new ArrayList<>();
    MissionCartListAdapter missionCartListAdapter;
    String title;
    Realm realm;
    LinearLayout add_mission;
    LinearLayout add_contact;
    RecyclerView contact_recyclerView;
    FlexBoxAdapter flexBoxAdapter;
    //private TextInputEditText et;
    //private TextInputLayout etl;
    private boolean hasFractionalPart;
    int amount;
    TextView bank_name_tv;
    TextView account_num_tv;
    TextView register_send;
    //TextView nick_name;
    TextView friends_num;
    TextView transfer_amount;

    OpenBanking openBanking = OpenBanking.getInstance();
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    LinearLayout ly_add_new_account;

    RealmResults<ContactItem> contactItemRealmResults;
    RealmResults<ContactItem> contactItemRealmResultsOverlap;
    TextView firebase_read;
    TabLayout ly_tab_select_penalty_mode;
    RecyclerView transfer_recyclerView;
    RecyclerTransferRespectivelyAdapter transfer_recycler_adapter;

    Context context;


    // 세자리로 끊어서 쉼표 보여주고, 소숫점 셋째짜리까지 보여준다.
    //DecimalFormat df = new DecimalFormat("###,###.####");
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    // 값 셋팅시, StackOverFlow를 막기 위해서, 바뀐 변수를 저장해준다.
    String result = "";
    private final Callback user_account_info_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.d(TAG, "콜백오류:" + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            //Log.d(TAG, "서버에서 응답한 Body:" + body);

            SharedPreferences.Editor editor = getSharedPreferences("OpenBanking", MODE_PRIVATE).edit();
            editor.putString("user_me", body);
            editor.apply();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            Log.d("유아", "핀테크응답결과 " + body);

            userAccountItemList = Utils.UserInfoResponseJsonParse(body);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("유아이","in");
                    displayBankAccount(userAccountItemList);
                }
            });



        }
    };
    private final Callback transfer_openbanking = new Callback(){
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            final String body = response.body().string();
            Log.d("송금테스트", "핀테크응답결과" + body);
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if(rsp_code.equals("A0000")){
                Log.d("송금테스트", "이체성공");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        registerSend();
                    }
                });

            }else{
                Log.d("송금테스트", Utils.getValueFromJson(body, "rsp_message"));

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mission);


        add_mission = findViewById(R.id.add_mission);
        add_contact = findViewById(R.id.add_contact);
        //contact_recyclerView = findViewById(R.id.contact_recyclerView);

        bank_name_tv = findViewById(R.id.bank_name);
        account_num_tv = findViewById(R.id.account_num);
        //nick_name = view.findViewById(R.id.nick_name);
        friends_num = findViewById(R.id.friends_num);
        transfer_amount = findViewById(R.id.transfer_amount);
        register_send = findViewById(R.id.register_send);

        //etl = findViewById(R.id.text_input_layout);
        //et = findViewById(R.id.transfer_input);
        firebase_read = findViewById(R.id.firebase_read);
        ly_add_new_account = findViewById(R.id.ly_add_new_account);
        transfer_recyclerView = findViewById(R.id.transfer_recyclerView);
        recyclerView = findViewById(R.id.missions_cart_list);

        //numberTextWatcher = new NumberTextWatcher(textInputEditText,textInputLayout,contact_num,transfer_amount,context);

        firebase_read.setOnClickListener(this);
        //et.addTextChangedListener(this);
        add_mission.setOnClickListener(this);
        add_contact.setOnClickListener(this);
        register_send.setOnClickListener(this);
        ly_add_new_account.setOnClickListener(this);
        //etl.setHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color_dark)));
        //etl.setDefaultHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color)));


        //amount = Integer.valueOf(Integer.valueOf(et.getText().toString().replaceAll(",", "")));


        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();


        RealmResults<MissionCartItem> realmResults = realm.where(MissionCartItem.class).findAll();
        contactItemRealmResults = realm.where(ContactItem.class).findAll();
        missionCartItemList = realmResults;


        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).equalTo("isSelected",true).findAll();
        Log.d("교수님", realmResults2.size() + "");


        //dateItemArrayList = getIntent().getParcelableArrayListExtra("date_list");

        openBankingSetting();
//         Log.d("리절트",dateItemArrayList.get(0).getYear()+"");
        setRecyclerView(realmResults);
        //setContactRecyclerView(contactItemRealmResults);
        setTransferRecyclerView(realmResults2);

    }

    private void displayBankAccount(List<UserAccountItem> userAccountItemList) {

        String bank_name = userAccountItemList.get(0).getBank_name();
        String account_num = userAccountItemList.get(0).getAccount_num_masked();

        bank_name_tv.setText(bank_name);
        account_num_tv.setText(account_num);


    }

    private void openBankingSetting() {
        SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token", "");
        String user_seq_num = sharedPreferences.getString("user_seq_num", "");
        Log.d("오픈뱅킹", "사용자토큰" + access_token);

        if (TextUtils.isEmpty(access_token)) {
            //register_account.setText("처음사용자");
            ly_add_new_account.setVisibility(View.VISIBLE);
            Log.d("오픈뱅킹", "처음사용자");
        } else {
            Log.d("오픈뱅킹", "기사용자정보요청");
            ly_add_new_account.setVisibility(View.GONE);
            openBanking.requestUserAccountInfo(user_account_info_callback, access_token, user_seq_num);


        }


    }


    public void setRecyclerView(List<MissionCartItem> missionCartItemList) {
        missionCartListAdapter = new MissionCartListAdapter(this, missionCartItemList, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(missionCartListAdapter);
    }

    private void setTransferRecyclerView(RealmResults<ContactItem> realmResults) {
        transfer_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transfer_recycler_adapter = new RecyclerTransferRespectivelyAdapter(realmResults, realm);

        transfer_recyclerView.setAdapter(transfer_recycler_adapter);

    }

    private void addMission() {
        Intent intent = new Intent(this, SingleModeActivity.class);
        startActivityForResult(intent, 1);
    }

    private void transferOpenBanking(){
        openBanking.requestTransfer(transfer_openbanking,this);
    }

    private void registerSend() {
        //파이어베이스
        Log.d("베이스", "전송완료");
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        String mission_id = Utils.getCurrentTime() + "U" + user_id;

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        registerCheckForServer(mRootRef, user_id, mission_id);
        registerMissionInfoRoot(mRootRef,user_id,mission_id);
        registerUserMissionList(mRootRef,user_id,mission_id);
        registerMissionInfoList(mRootRef,user_id,mission_id);
        //testtest(mRootRef);


        mRootRef.child("mission_info_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("실시간", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void registerMissionInfoRoot(DatabaseReference databaseReference, String user_id, String mission_id) {

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
        for (int i = 0; i < missionCartItemList.size(); i++) {
            mission_children.put("E" + mission_id + "U" + i, true);
        }

        MissionInfoRoot missionInfoRoot = new MissionInfoRoot();
        missionInfoRoot.setMission_registered_date_time(Utils.getCurrentTime());
        missionInfoRoot.setFriends_selected_list(friends_selected_array);
        missionInfoRoot.setChildren_id(mission_children);


        mission_info_root.put("S" + mission_id, missionInfoRoot);


        databaseReference.child("mission_info_root").child(user_id).updateChildren(mission_info_root);
    }


    private void registerUserMissionList(DatabaseReference databaseReference, String user_id, String mission_id) {


        Map<String, Object> objectMap = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("E1234");
        arrayList.add("E4567");

        objectMap.put("20191224", arrayList);


        databaseReference.child("users_mission_list").child(user_id).updateChildren(objectMap);


    }

    private void registerMissionInfoList(DatabaseReference databaseReference, String user_id, String mission_id) {

        Map<String, Object> mission_info_list = new HashMap<>();


        for (int i = 0; i < missionCartItemList.size(); i++) {
            MissionInfoList missionInfoList = new MissionInfoList();
            Map<String, Object> mission_dates = new HashMap<>();

            missionInfoList.setMission_title(missionCartItemList.get(i).getTitle());
            missionInfoList.setMission_time(missionCartItemList.get(i).getHour() + ":" + missionCartItemList.get(i).getMin());
            missionInfoList.setAddress(missionCartItemList.get(i).getAddress());
            missionInfoList.setIs_success(false);
            missionInfoList.setLat(missionCartItemList.get(i).getLat());
            missionInfoList.setLng(missionCartItemList.get(i).getLng());
            //Log.d("파베", "있기는한가" + missionCartItemList.get(i).getCalendarDayList().size());
            for (int j = 0; j < missionCartItemList.get(i).getCalendarDayList().size(); j++) {
                int year = missionCartItemList.get(i).getCalendarDayList().get(j).getYear();
                int month = missionCartItemList.get(i).getCalendarDayList().get(j).getMonth();
                int day = missionCartItemList.get(i).getCalendarDayList().get(j).getDay();
                String date = year + "" + month + "" + day;
                //Log.d("파베", "날짜" + date);
                mission_dates.put(date, true);
            }
            missionInfoList.setMission_dates(mission_dates);
            //missionInfoList.setArrayList(gogo);
            missionInfoList.setMission_info_root_id("S" + mission_id);

            mission_info_list.put("E" + mission_id + "N" + i, missionInfoList);

        }


        databaseReference.child("mission_info_list").child(user_id).updateChildren(mission_info_list);
    }

    private void registerCheckForServer(DatabaseReference databaseReference, String user_id, String mission_id) {


        for (int i = 0; i < missionCartItemList.size(); i++) {
            for (int j = 0; j < missionCartItemList.get(i).getCalendarDayList().size(); j++) {
                Map<String, Object> objectMap = new HashMap<>();
                ArrayList<ItemForServer> itemForServerArrayList = new ArrayList<>();


                int year = missionCartItemList.get(i).getCalendarDayList().get(j).getYear();
                int month = missionCartItemList.get(i).getCalendarDayList().get(j).getMonth();
                int day = missionCartItemList.get(i).getCalendarDayList().get(j).getDay();
                String date = year + "" + month + "" + day;
                int hour = missionCartItemList.get(i).getHour();
                int min = missionCartItemList.get(i).getMin();
                String time_id = Utils.makeTimeForServer(hour, min);


                ItemForServer itemForServer = new ItemForServer();

                itemForServer.setUser_id(user_id);
                itemForServer.setIs_success(false);
                itemForServer.setRoot_id("S" + mission_id);
                itemForServer.setChildren_id("E" + mission_id + "N" + i);
                itemForServerArrayList.add(itemForServer);


                objectMap.put(time_id, itemForServerArrayList);
                databaseReference.child("check_for_server").child(date).child(time_id).push().setValue(itemForServer);

            }


        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_mission:
                addMission();
                break;
            case R.id.add_contact:
                startActivityForResult(new Intent(this, ContactActivity.class), 2);
                break;
            case R.id.register_send:
                //registerSend();
                transferOpenBanking();
                break;
            case R.id.firebase_read:
                //checkResult();
                deleteRealm();

                break;

        }

    }

    private void deleteRealm() {
        realm.beginTransaction();
        RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        realmResults.deleteAllFromRealm();

        realm.commitTransaction();
        missionCartListAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 2) {
            contactItemRealmResults = realm.where(ContactItem.class).findAll();
            transfer_recycler_adapter.notifyDataSetChanged();

            //setContactRecyclerView(contactItemRealmResults);

        }
        if (resultCode == 1) {

            missionCartListAdapter.notifyDataSetChanged();
        }
    }

}
