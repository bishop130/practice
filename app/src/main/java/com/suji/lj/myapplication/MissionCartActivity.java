package com.suji.lj.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.suji.lj.myapplication.Fragments.TransferEqualModeFragment;
import com.suji.lj.myapplication.Fragments.TransferRespectivelyModeFragment;
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
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MissionCartActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, FlexBoxAdapter.OnFriendsNumListener {

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
    private TextInputEditText et;
    private TextInputLayout etl;
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

            SharedPreferences.Editor editor = getSharedPreferences("OpenBanking",MODE_PRIVATE).edit();
            editor.putString("user_me",body);
            editor.apply();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            Log.d("오픈뱅킹","핀테크응답결과 "+ body);

            userAccountItemList = Utils.UserInfoResponseJsonParse(body);

            //displayBankAccount(userAccountItemList);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mission);


        add_mission = findViewById(R.id.add_mission);
        add_contact = findViewById(R.id.add_contact);
        contact_recyclerView = findViewById(R.id.contact_recyclerView);

        bank_name_tv = findViewById(R.id.bank_name);
        account_num_tv = findViewById(R.id.account_num);
        //nick_name = view.findViewById(R.id.nick_name);
        friends_num = findViewById(R.id.friends_num);
        transfer_amount = findViewById(R.id.transfer_amount);
        register_send = findViewById(R.id.register_send);

        etl = findViewById(R.id.text_input_layout);
        et = findViewById(R.id.transfer_input);
        firebase_read = findViewById(R.id.firebase_read);
        ly_add_new_account = findViewById(R.id.ly_add_new_account);
        ly_tab_select_penalty_mode = findViewById(R.id.ly_tab_select_penalty_mode);

        //numberTextWatcher = new NumberTextWatcher(textInputEditText,textInputLayout,contact_num,transfer_amount,context);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.transfer_container, new TransferEqualModeFragment()).commit();

        ly_tab_select_penalty_mode.addTab(ly_tab_select_penalty_mode.newTab().setText("같은금액으로 송금"));
        ly_tab_select_penalty_mode.addTab(ly_tab_select_penalty_mode.newTab().setText("다른금액으로 송금"));
        ly_tab_select_penalty_mode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.transfer_container, new TransferEqualModeFragment()).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.transfer_container, new TransferRespectivelyModeFragment(getApplicationContext(),realm)).commit();


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        firebase_read.setOnClickListener(this);
        et.addTextChangedListener(this);
        add_mission.setOnClickListener(this);
        add_contact.setOnClickListener(this);
        register_send.setOnClickListener(this);
        ly_add_new_account.setOnClickListener(this);
        etl.setHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color_dark)));
        etl.setDefaultHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color)));


        amount = Integer.valueOf(Integer.valueOf(et.getText().toString().replaceAll(",", "")));


        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();


        RealmResults<MissionCartItem> realmResults = realm.where(MissionCartItem.class).findAll();
        contactItemRealmResults = realm.where(ContactItem.class).findAll();
        missionCartItemList = realmResults;

        List<ContactItem> contactItemList = contactItemRealmResults;
        Log.d("렐름", realmResults.size() + "");
        Log.d("렐름", missionCartItemList.size() + "");
        recyclerView = findViewById(R.id.missions_cart_list);
        //dateItemArrayList = getIntent().getParcelableArrayListExtra("date_list");


//         Log.d("리절트",dateItemArrayList.get(0).getYear()+"");
        setRecyclerView(realmResults);
        setContactRecyclerView(contactItemRealmResults);
        openBankingSetting();
    }

    private void openBankingSetting(){
        SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token","");
        String user_seq_num = sharedPreferences.getString("user_seq_num","");
        Log.d("오픈뱅킹","사용자토큰"+access_token);

        if(TextUtils.isEmpty(access_token)){
            //register_account.setText("처음사용자");
            ly_add_new_account.setVisibility(View.VISIBLE);
            Log.d("오픈뱅킹","처음사용자");
        }else{
            Log.d("오픈뱅킹","기사용자정보요청");
            ly_add_new_account.setVisibility(View.GONE);
            openBanking.requestUserAccountInfo(user_account_info_callback,access_token,user_seq_num);


        }



    }


    public void setRecyclerView(List<MissionCartItem> missionCartItemList) {
        missionCartListAdapter = new MissionCartListAdapter(this, missionCartItemList, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(missionCartListAdapter);
    }

    private void addMission() {

        Intent intent = new Intent(this, SingleModeActivity.class);
        startActivityForResult(intent, 1);

    }

    private void registerSend() {
        //파이어베이스

        Log.d("베이스", "전송완료");
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        String mission_id = Utils.getCurrentTime() + "U" + user_id;

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


        //registerUserMissionList(mRootRef, user_id, mission_id);
        //registerMissionInfoList(mRootRef, user_id, mission_id);
        //registerMissionInfoRoot(mRootRef, user_id, mission_id);
        registerCheckForServer(mRootRef, user_id, mission_id);
        //testtest(mRootRef);

        //Log.d("파베", friends_selected_array.size() + "");


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
            friends_selected_array.add(contactItem);
        }

        Map<String, Object> mission_children = new HashMap<>();
        for (int i = 0; i < missionCartItemList.size(); i++) {
            mission_children.put("E" + mission_id + "U" + i, true);
        }

        MissionInfoRoot missionInfoRoot = new MissionInfoRoot();
        missionInfoRoot.setPenalty_amt(5000);
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
                registerSend();
                break;
            case R.id.firebase_read:
                checkResult();
                break;

        }

    }

    private void checkResult(){

        List<ItemForServer> itemForServerList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference = databaseReference.child("check_for_server").child("20191220").child("T231700");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Log.d("파베", dataSnapshot.getChildrenCount()+"");

                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    ItemForServer itemForServer = post.getValue(ItemForServer.class);


                    itemForServerList.add(itemForServer);
                    Log.d("파베", itemForServer.getUser_id() + "");
                    Log.d("파베", itemForServer.getRoot_id());
                    Log.d("파베", itemForServer.getChildren_id());
                    Log.d("파베", itemForServer.isIs_success() + "");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 2) {
            Log.d("미션카트", data.getStringExtra("test") + "test");


            contactItemArrayList = data.getParcelableArrayListExtra("contact_list");
            Log.d("미션카트", contactItemArrayList.size() + "result");
            flexBoxAdapter.notifyDataSetChanged();
            contactItemRealmResults = realm.where(ContactItem.class).findAll();

            setContactRecyclerView(contactItemRealmResults);

        }
        if (resultCode == 1) {

            missionCartListAdapter.notifyDataSetChanged();
        }
    }

    private void setContactRecyclerView(RealmResults<ContactItem> contactItemArrayList) {


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        contact_recyclerView.setLayoutManager(layoutManager);
        flexBoxAdapter = new FlexBoxAdapter(this, contactItemArrayList, this, realm);
        contact_recyclerView.setAdapter(flexBoxAdapter);


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("오픈뱅킹", "on_changed");

        if (s.toString().isEmpty()) {
            etl.setError("최소 3천원부터 입력됩니다.");
            etl.setErrorEnabled(true);
            Log.d("오픈뱅킹", "트랜스퍼 empty    " + etl.isErrorEnabled());

        } else {
            amount = Integer.valueOf(s.toString().replaceAll(",", ""));


            if (amount > 100000) {
                etl.setError("최대 10만원까지 입력됩니다.");
                etl.setErrorEnabled(true);
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "10만원 이상    " + etl.isErrorEnabled());
            } else if (amount < 3000) {
                etl.setError("최소 3천원부터 입력됩니다.");
                etl.setErrorEnabled(true);
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "3천원    " + etl.isErrorEnabled());
            } else {
                etl.setError(null);
                etl.setErrorEnabled(false);
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.blue));


                Log.d("오픈뱅킹", "정상    " + etl.isErrorEnabled());
            }
        }

        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
        transfer_amount.setText(df.format(contactItemArrayList.size() * amount) + " 원");


    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("오픈뱅킹", "after_changed");

        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = et.getSelectionStart();
            if (hasFractionalPart) {
                et.setText(df.format(n));
            } else {
                et.setText(dfnd.format(n));
            }
            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);
    }

    @Override
    public void onFriendsNum(int num) {
        friends_num.setText(num + " 명");
    }



}
