package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Fragments.CalendarSelectFragment;
import com.suji.lj.myapplication.Fragments.EventModeFragment;
import com.suji.lj.myapplication.Fragments.MapMissionFragment;

import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.Fragments.RuleFragment;
import com.suji.lj.myapplication.Fragments.SingleModeFragment;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForDateTimeCheck;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForFriendMissionCheck;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForInvitationPreview;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemForMissionCheck;
import com.suji.lj.myapplication.Items.ItemForNotification;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.ByteLengthFilter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FCM;
import com.suji.lj.myapplication.Utils.FirebaseDB;
import com.suji.lj.myapplication.Utils.KakaoRest;
import com.suji.lj.myapplication.Utils.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SingleModeActivity extends AppCompatActivity implements TextWatcher,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, CalendarSelectFragment.OnResetAmountFromCalendarListener, RecyclerTransferRespectivelyAdapter.OnResetAmountDisplayListener, RuleFragment.OnResetAmountFromSingleModeListener {


    Callback get_address = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            Toast.makeText(getApplicationContext(), "주소를 불러오던 중 문제가 생겼습니다.", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {

            String body = response.body().string();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Log.d("주소", body);
                        JSONObject obj = new JSONObject(body);
                        JSONArray jsonArray = obj.getJSONArray("documents");
                        JSONObject object = (JSONObject) jsonArray.get(0);

                        String is_road = object.getString("road_address");
                        String old_address = object.getJSONObject("address").getString("address_name");
                        Log.d("주소", old_address + "일반");

                        Log.d("주소", is_road + "is_road");
                        if (!is_road.equals("null")) {
                            String road_address = object.getJSONObject("road_address").getString("address_name");
                            Log.d("주소", road_address);
                            realm.executeTransaction(new Realm.Transaction() {

                                @Override
                                public void execute(Realm realm) {
                                    MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                                    item.setAddress(road_address);
                                    //transferAmount();
                                    MissionCartItem missionCartItem = realm.copyFromRealm(item);
                                    int missionMode = missionCartItem.getMissionMode();

                                    pointPayRequest();
                                    //startActivity(new Intent(getApplicationContext(), MissionCheckActivity.class));
                                }
                            });
                        } else {
                            Log.d("주소", old_address + "일반들어옴");
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                                    item.setAddress(old_address);
                                    // transferAmount();
                                    MissionCartItem missionCartItem = realm.copyFromRealm(item);
                                    int missionMode = missionCartItem.getMissionMode();

                                    pointPayRequest();


                                    //startActivity(new Intent(getApplicationContext(), MissionCheckActivity.class));
                                }
                            });

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            });


            //
        }
    };

    TextView next_btn;
    TextInputLayout textInputLayout;
    EditText textInputEditText;
    Intent intent;
    RecyclerView transfer_recyclerView;
    RealmList<ItemForDateTime> stringList = new RealmList<>();
    NestedScrollView scrollView;
    RecyclerView recyclerView;
    SearchView searchView;
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    OpenBanking openBanking = OpenBanking.getInstance();
    Realm realm;
    TextView detail_setting;
    AppCompatCheckBox checkBox_term_use, checkBox_term_private, checkBox_term_all;
    boolean TERMS_AGREE_1 = false; // No Check = 0, Check = 1
    boolean TERMS_AGREE_2 = false; // No Check = 0, Check = 1

    Toolbar toolbar;
    Runnable runnable;
    Handler mHandler;

    LinearLayout title;
    KakaoRest kakaoRest;
    TextView title_error;
    RelativeLayout text_clear;
    TextView text_length;
    LinearLayout ly_title_error;

    TextView text_rest_point;
    boolean point_callback_state = false;
    int point_input = 0;
    int point_amount = 0;
    int total = 0;
    AlertDialog loading_dialog;
    ProgressBar progressBar;
    int amount = 0;


    static final int SINGLE_MODE = 0;
    static final int MULTI_MODE = 1;
    String user_id;
    DatabaseReference mRootRef;
    TextView tvTotalAmount;
    boolean isTitleValid;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);
        Log.d("목장", "onCreate");


        next_btn = findViewById(R.id.mission_basic_send);
        textInputEditText = findViewById(R.id.mission_basic_editText);
        textInputLayout = findViewById(R.id.mission_basic_inputLayout);
        transfer_recyclerView = findViewById(R.id.transfer_recyclerView);
        scrollView = findViewById(R.id.main_scroll_view);
        recyclerView = findViewById(R.id.place_recycler);
        searchView = findViewById(R.id.daum_map_search);
        detail_setting = findViewById(R.id.detail_setting);
        checkBox_term_use = findViewById(R.id.agree_terms_use);
        checkBox_term_private = findViewById(R.id.agree_terms_private);
        checkBox_term_all = findViewById(R.id.agree_terms_all);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        toolbar = findViewById(R.id.toolbar);
        title_error = findViewById(R.id.title_error);
        text_clear = findViewById(R.id.text_clear);
        text_length = findViewById(R.id.text_length);
        ly_title_error = findViewById(R.id.ly_title_error);

        text_rest_point = findViewById(R.id.rest_point);


        title = findViewById(R.id.title);
        progressBar = findViewById(R.id.pb_map);


        user_id = Account.getUserId(this);
        kakaoRest = KakaoRest.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();


        realm = Realm.getDefaultInstance();
        textInputEditText.addTextChangedListener(this);
        next_btn.setOnClickListener(this);
        checkBox_term_use.setOnCheckedChangeListener(this);
        checkBox_term_private.setOnCheckedChangeListener(this);
        checkBox_term_all.setOnCheckedChangeListener(this);

        next_btn.setEnabled(false);
        next_btn.setClickable(false);

        InputFilter[] filters = new InputFilter[]{new ByteLengthFilter(20, "KSC5601")};
        textInputEditText.setFilters(filters);

        //Utils.fixMapScroll(imageView,scrollView);

        toolbar.setTitle("새 약속");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(SingleModeActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        loading_dialog.show();
        initiate();


        text_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_clear.setVisibility(View.INVISIBLE);
                textInputEditText.setText("");
            }
        });


        textInputEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                textInputEditText.setFocusableInTouchMode(true);
                return false;
            }
        });
        textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                            item.setTitle(v.getText().toString());

                        }
                    });
                    textInputEditText.setFocusable(false);

                    return true;
                }
                return false;
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("point").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double point = dataSnapshot.getValue(Double.class);
                if (point != null) {

                    point_callback_state = true;
                    point_amount = point.intValue();
                    String rest_point_string = Utils.makeNumberComma(point) + " P";
                    text_rest_point.setText(rest_point_string);
                } else {
                    point_callback_state = true;
                    point_amount = 0;
                    String rest_point_string = point_amount + " P";
                    text_rest_point.setText(rest_point_string);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                text_rest_point.setText("error!");
            }
        });

        //openBankingSetting();


        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                addFragment(new MapMissionFragment(scrollView), R.id.flMap);
                addFragment(new CalendarSelectFragment(), R.id.calendar_container);
                addFragment(new RuleFragment(), R.id.flRule);


            }
        };
        mHandler.post(runnable);
    }


    private void initiate() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(MissionCartItem.class);
            }
        });


        //singleAmountDisplay();
        loading_dialog.dismiss();

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d("액션", "before");


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("액션", "on");
        int length = Utils.getStringLength(s.toString());

        if (length == 0) {
            text_clear.setVisibility(View.INVISIBLE);
            text_length.setVisibility(View.INVISIBLE);
        } else {
            //isTitleValid(length);
            text_length.setVisibility(View.VISIBLE);
            text_clear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("액션", "after");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.setTitle(s.toString());

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mission_basic_send:
                dataCheck();

                break;

            case R.id.detail_setting:
                startActivity(new Intent(SingleModeActivity.this, DetailSettingActivity.class));
                break;

            case R.id.account_select:

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 5) { /** 포인트결제 비밀번호 확인**/
            Log.d("포인트", "onresult");
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
            MissionCartItem missionCartItem = realm.copyFromRealm(item);
            int missionMode = item.getMissionMode();
            Log.d("포인트", missionMode + "mission_mode");


            if (missionMode == SINGLE_MODE) {

                FirebaseDB.registerMissionInfoList(mRootRef, this, missionCartItem);//미션목록추가





                /** 파이어베이스 추가후 deleteRealm **/


            }
            if (missionMode == MULTI_MODE) {//multi

                FirebaseDB.multiMode(item, this, "receipt");

            }


        }else if(requestCode == 6){
            Toast.makeText(SingleModeActivity.this, "응답을 받지 못했습니다", Toast.LENGTH_SHORT).show();
            loading_dialog.dismiss();

        }
    }


    private void dataCheck() {


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                RealmResults<ContactItem> result = realm.where(ContactItem.class).findAll();
                RealmList<ContactItem> contactList = new RealmList<>();
                contactList.addAll(result.subList(0, result.size()));
                missionCartItem.setContactList(contactList);
                missionCartItem.setTitle(textInputEditText.getText().toString());

                RealmResults<ItemForFriends> realmResults = realm.where(ItemForFriends.class).findAll();
                RealmList<ItemForFriends> friendList = new RealmList<>();
                friendList.addAll(realmResults.subList(0, realmResults.size()));
                missionCartItem.setFriendsList(friendList);

                String mission_id = Utils.getCurrentTime() + "U" + user_id;
                missionCartItem.setMissionId(mission_id);


            }
        });

        MissionCartItem item = realm.copyFromRealm(realm.where(MissionCartItem.class).findFirst());
        boolean isValid = true;

        RealmResults<ItemForFriends> itemForFriends = realm.where(ItemForFriends.class).findAll();
        RealmResults<ItemPortion> portionRealmResults = realm.where(ItemPortion.class).findAll();


        Log.d("멀티", portionRealmResults.size() + "  size");

        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
            isValid = false;
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_LONG).show();
        }

        if (item.getCalendarDayList().size() == 0) {
            isValid = false;

            Toast.makeText(getApplicationContext(), "날짜를 선택해주세요", Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < item.getCalendarDayList().size(); i++) {
                String date = item.getCalendarDayList().get(i).getDate();
                String time = item.getCalendarDayList().get(i).getTime();
                boolean is30 = DateTimeUtils.compareIsFuture30min(date + time, "yyyyMMddHHmm");
                if (!is30) {
                    isValid = false;
                    Toast.makeText(getApplicationContext(), "시간을 30분 이후 부터 선택해주세요", Toast.LENGTH_LONG).show();
                }
                Log.d("비니", date + "/" + time + "/" + is30);
            }
        }
        if (item.getMissionMode() == 1) { //멀티라면
            if (item.getMultiPenaltyPerDay() < 1000) {
                Toast.makeText(getApplicationContext(), "금액을 입력해주세요.", Toast.LENGTH_LONG).show();
                isValid = false;

            }
            if (itemForFriends.size() == 0) {
                Toast.makeText(getApplicationContext(), "친구를 선택해주세요", Toast.LENGTH_LONG).show();
                isValid = false;
            }
            if (portionRealmResults.size() == 0) {
                Toast.makeText(getApplicationContext(), "분배비율을 설정해주세요", Toast.LENGTH_LONG).show();
                isValid = false;
            }


        } else {
            if (item.getSinglePenaltyPerDay() < 1000) {
                Toast.makeText(getApplicationContext(), "금액을 입력해주세요.", Toast.LENGTH_LONG).show();
                isValid = false;


            }
            if (itemForFriends.size() == 0) {
                Toast.makeText(getApplicationContext(), "친구를 선택해주세요", Toast.LENGTH_LONG).show();
                isValid = false;
            }
        }
        if (point_amount < total) {
            Toast.makeText(getApplicationContext(), "포인트가 부족합니다", Toast.LENGTH_LONG).show();
            isValid = false;

        }

        if (!TERMS_AGREE_1) {

            isValid = false;

        }
        if (!TERMS_AGREE_2) {
            isValid = false;
        }


        if (isValid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("약속을 등록하시겠습니까?");


            double lat = item.getLat();
            double lng = item.getLng();
            Log.d("위치", lat + "  lat");
            Log.d("위치", lng + "  lng");
            builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    loading_dialog.show();

                    kakaoRest.getAddressFromLocation(get_address, lat, lng);

                    //finish();


                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);

        //loading_dialog.dismiss();

        if (!realm.isClosed()) {
            deleteRealmData();
        }

        Log.d("목장", "onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(loading_dialog.isShowing()){
            loading_dialog.dismiss();
        }
        //realm.beginTransaction();
        Log.d("목장", "onResume");

    }

    @Override
    public void onBackPressed() {
        Log.d("번들", "onBackPressed");


        if (!realm.isClosed()) {
            deleteRealmData();
        }

        super.onBackPressed();

    }

    /**
     * 화면을 벗어나면 입력데이터 모두 삭제
     **/
    private void deleteRealmData() {


        if (realm.where(MissionCartItem.class).count() > 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    //RealmResults<ItemForDateTime> itemForDateTimes = realm.where(ItemForDateTime.class).findAll();

                    MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                    if (missionCartItem != null) {
                        missionCartItem.deleteFromRealm();

                    }
                    RealmResults<ItemForFriends> friendsRealmResults = realm.where(ItemForFriends.class).findAll();
                    friendsRealmResults.deleteAllFromRealm();


                    RealmResults<ItemPortion> itemPortionRealmResults = realm.where(ItemPortion.class).findAll();
                    itemPortionRealmResults.deleteAllFromRealm();

                }

            });
        }
        realm.close();
        //finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.agree_terms_use:
                if (buttonView.isChecked()) {
                    TERMS_AGREE_1 = true;
                } else {

                    TERMS_AGREE_1 = false;
                }
                if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                    checkBox_term_all.setChecked(true);

                } else {
                    checkBox_term_all.setChecked(false);
                }

                break;
            case R.id.agree_terms_private:
                if (buttonView.isChecked()) {
                    TERMS_AGREE_2 = true;
                } else {
                    TERMS_AGREE_2 = false;
                }
                if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                    checkBox_term_all.setChecked(true);
                    next_btn.setEnabled(true);
                    next_btn.setClickable(true);

                } else {
                    checkBox_term_all.setChecked(false);
                }
                break;
            case R.id.agree_terms_all:
                if (buttonView.isChecked()) {
                    checkBox_term_use.setChecked(true);
                    checkBox_term_private.setChecked(true);
                    next_btn.setEnabled(true);
                    next_btn.setClickable(true);

                    TERMS_AGREE_1 = true;
                    TERMS_AGREE_2 = true;
                } else {

                    if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                        checkBox_term_use.setChecked(false);
                        checkBox_term_private.setChecked(false);
                        next_btn.setEnabled(false);
                        next_btn.setClickable(false);
                        Log.d("약관", "123");
                        TERMS_AGREE_1 = false;
                        TERMS_AGREE_2 = false;
                        TERMS_AGREE_2 = false;
                    }
                }

                break;


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                deleteRealmData();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);


    }


    private void addFragment(Fragment fragment, int layoutId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(layoutId, fragment).commit();

    }


    public void singleAmountDisplay() {
        Log.d("디스플레이", "display");

        RealmResults<ItemForFriends> realmResults2 = realm.where(ItemForFriends.class).findAll();
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

        int dates = item.getCalendarDayList().size();
        int missionMode = item.getMissionMode();

        if (item.getMissionMode() == 0) {
            amount = item.getSinglePenaltyPerDay();
            total = realmResults2.size() * amount * dates;
        } else {
            if (realmResults2.size() > 0) {
                amount = item.getMultiPenaltyPerDay();
                total = amount * dates;
            } else {
                total = 0;
            }
        }

        String point_total_string = 0 + " P";
        point_input = 0;

        String rest_point_string = Utils.makeNumberComma(point_amount) + " P";
        tvTotalAmount.setText(Utils.makeNumberComma(total));
        text_rest_point.setText(rest_point_string);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                if (missionMode == 0) {
                    item.setSinglePenaltyTotal(total);
                } else {
                    item.setMultiPenaltyTotal(total);
                }
            }
        });
    }


    @Override
    public void onResetAmountDisplay() {
        Log.d("옵티마", "들어와");
        singleAmountDisplay();

    }

    @Override
    public void onResetAmountFromSingleMode() {

        singleAmountDisplay();


    }

    @Override
    public void onResetAmountFromCalendar() {

        singleAmountDisplay();


    }


    private void pointPayRequest() {

        MissionCartItem cartItem = realm.where(MissionCartItem.class).findFirst();
        Intent intent = new Intent(this, SecurityActivity.class);
        int missionMode = cartItem.getMissionMode();
        if(missionMode == 0){
            intent.putExtra("password", 5);
            intent.putExtra("pointTotal", cartItem.getSinglePenaltyTotal());
            intent.putExtra("title", cartItem.getTitle());
            intent.putExtra("missionId", cartItem.getMissionId());
            intent.putExtra("missionMode", cartItem.getMissionMode());


        }else{
            intent.putExtra("password", 5);
            intent.putExtra("pointTotal", cartItem.getMultiPenaltyTotal());
            intent.putExtra("title", cartItem.getTitle());
            intent.putExtra("missionId", cartItem.getMissionId());
            intent.putExtra("missionMode", cartItem.getMissionMode());


        }

        startActivityForResult(intent, 5);


    }

}

/**
 * 확인 -> 주소불러오기(api콜 절약을 위해 확인 뒤에 넣음) -> 결제 -> 영수증검증 -> db저장 -> relam초기화 -> 포어그라운드 서비스 활성화 -> 영수증 액티비
 **/
