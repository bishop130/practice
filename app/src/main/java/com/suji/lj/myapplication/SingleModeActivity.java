package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.AccountDialog;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.PlaceRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerAccountAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerDateTimeAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Fragments.CalendarSelectFragment;
import com.suji.lj.myapplication.Fragments.EventModeFragment;
import com.suji.lj.myapplication.Fragments.MapMissionFragment;

import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.Fragments.SingleModeFragment;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FirebaseDB;
import com.suji.lj.myapplication.Utils.Utils;


import net.daum.mf.map.api.MapReverseGeoCoder;


import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public class SingleModeActivity extends AppCompatActivity implements TextWatcher,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final okhttp3.Callback user_account_info_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            //Log.d(TAG, "콜백오류:" + e.getMessage());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            //Log.d(TAG, "서버에서 응답한 Body:" + body);

            SharedPreferences.Editor editor = getSharedPreferences("OpenBanking", MODE_PRIVATE).edit();
            editor.putString("user_me", body);
            editor.apply();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            Log.d("유아", "핀테크응답결과 " + body);
            Log.d("유아", "코드 " + rsp_code);

            if (rsp_code.equals("A0000")) {
                userAccountItemList = Utils.UserInfoResponseJsonParse(body);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("유아이", "in");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayBankAccount(userAccountItemList);
                            }
                        });

                    }
                }).start();


            } else if (rsp_code.equals("O0003")) {
                Log.d("유아", "여기로들어 " + rsp_code);
                SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
                String refresh_token = sharedPreferences.getString("refresh_token", "");

                openBanking.requestAccessTokenFromRefreshToken(refresh_token_callback, refresh_token);

                if (rsp_code.equals("O0012")) {
                    Toast.makeText(getApplicationContext(), "중계처리지연(잠시후 재거래 요망)", Toast.LENGTH_LONG).show();
                }

            } else {
                Log.d("유아", rsp_code + "/" + body);
                Toast.makeText(getApplicationContext(), "에", Toast.LENGTH_LONG).show();
            }


        }
    };
    private final okhttp3.Callback refresh_token_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            String access_token = Utils.getValueFromJson(body, "access_token");
            String refresh_token = Utils.getValueFromJson(body, "refresh_token");
            String user_seq_num = Utils.getValueFromJson(body, "user_seq_no");
            String expires_in = Utils.getValueFromJson(body, "expires_in");
            SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", Context.MODE_PRIVATE);
            SharedPreferences.Editor open_banking_token = sharedPreferences.edit();
            open_banking_token.putString("access_token", access_token);
            open_banking_token.putString("refresh_token", refresh_token);
            open_banking_token.putString("user_seq_num", user_seq_num);
            open_banking_token.putString("expires_in", expires_in);
            open_banking_token.apply();
            open_banking_token.commit();

            openBankingSetting();


        }
    };
    private final okhttp3.Callback transfer_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            Log.d("출금", body);
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rsp_code.equals("A0000")) {
                        Log.d("유아이", "in");
                        dataSave();
                    } else {
                        String message = Utils.getValueFromJson(body, "rsp_message");
                        alertMessage(message);
                    }
                }
            });


        }
    };

    TextView next_btn;
    TextView total_amount;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    Intent intent;


    RecyclerView transfer_recyclerView;
    RecyclerTransferRespectivelyAdapter transfer_recycler_adapter;


    LinearLayout ly_contact_error;


    RealmList<ItemForDateTime> stringList = new RealmList<>();


    NestedScrollView scrollView;
    PlaceRecyclerAdapter placeRecyclerAdapter;
    RecyclerView recyclerView;
    SearchView searchView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    OpenBanking openBanking = OpenBanking.getInstance();


    Realm realm;

    LinearLayout account_select;
    TextView detail_setting;
    TextView bank_name_tv;
    TextView account_num_tv;
    AppCompatCheckBox checkBox_term_use, checkBox_term_private, checkBox_term_all;
    boolean TERMS_AGREE_1 = false; // No Check = 0, Check = 1
    boolean TERMS_AGREE_2 = false; // No Check = 0, Check = 1

    AccountDialog accountDialog;
    Toolbar toolbar;

    Fragment fa, fb, fc;
    int mission_mode = 0;
    Runnable runnable;
    Handler mHandler;
    BottomSheetDialog bottomSheetDialog;
    private RecyclerView recyclerView_account;
    TextView user_name;

    LinearLayout title;

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
        bank_name_tv = findViewById(R.id.bank_name);
        account_num_tv = findViewById(R.id.account_num);
        checkBox_term_use = findViewById(R.id.agree_terms_use);
        checkBox_term_private = findViewById(R.id.agree_terms_private);
        checkBox_term_all = findViewById(R.id.agree_terms_all);
        total_amount = findViewById(R.id.total_amount);

        account_select = findViewById(R.id.account_select);

        toolbar = findViewById(R.id.toolbar);
        ly_contact_error = findViewById(R.id.ly_contact_error);

        Spinner spinner = findViewById(R.id.mission_mode);

        title = findViewById(R.id.title);


        Realm.init(this);

        realm = Realm.getDefaultInstance();

        textInputEditText.addTextChangedListener(this);
        next_btn.setOnClickListener(this);
        checkBox_term_use.setOnCheckedChangeListener(this);
        checkBox_term_private.setOnCheckedChangeListener(this);
        checkBox_term_all.setOnCheckedChangeListener(this);
        account_select.setOnClickListener(this);


        next_btn.setEnabled(false);
        next_btn.setClickable(false);

        //Utils.fixMapScroll(imageView,scrollView);

        toolbar.setTitle("새 약속 정하");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mission_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        fa = new SingleModeFragment();
        addFragment(fa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("스피너", position + "");
                switch (position) {
                    case 0:
                        if (fa == null) {
                            fa = new SingleModeFragment();
                            addFragment(fa);

                        }
                        if (fa != null) showFragment(fa);
                        if (fb != null) hideFragment(fb);
                        if (fc != null) hideFragment(fc);
                        mission_mode = 0;

                        break;
                    case 1:
                        if (fb == null) {
                            fb = new MultiModeFragment(SingleModeActivity.this);
                            addFragment(fb);
                        }

                        if (fa != null) hideFragment(fa);
                        if (fb != null) showFragment(fb);
                        if (fc != null) hideFragment(fc);
                        mission_mode = 1;

                        break;
                    case 2:
                        if (fc == null) {
                            fc = new EventModeFragment();

                            addFragment(fc);
                        }

                        if (fa != null) hideFragment(fa);
                        if (fb != null) hideFragment(fb);
                        if (fc != null) showFragment(fc);
                        mission_mode = 2;
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        openBankingSetting();


        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                initiate();
                addMapFragment(new MapMissionFragment(scrollView));

                addCalendarFragment(new CalendarSelectFragment(stringList, realm));
            }
        };
        mHandler.post(runnable);


        //setDateTimeRecyclerView(stringList);
        //stringList.clear();
    }


    private void initiate() {
        long count = realm.where(MissionCartItem.class).count();
        Log.d("날짜", count + "카운트");
        if (count == 0) {
            //setDateTimeRecyclerView(stringList);
            Log.d("날짜", stringList.size() + "string카운트");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MissionCartItem item = realm.createObject(MissionCartItem.class);
                    RealmList<ItemForDateTime> realmList = item.getCalendarDayList();
                    stringList.clear();
                    stringList = realmList;
                    for (int i = 0; i < realmList.size(); i++) {

                        int year = realmList.get(i).getYear();
                        int month = realmList.get(i).getMonth();
                        int day = realmList.get(i).getDay();
                        Log.d("번들 날", year + " " + month + " " + day);


                    }
                    //setDateTimeRecyclerView(realmList);
                }
            });
            Log.d("날짜", realm.where(MissionCartItem.class).count() + "카운트out");

        } else {
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
            Log.d("날짜", item.getCalendarDayList().size() + "이니시");
            stringList = item.getCalendarDayList();
            Log.d("날짜", stringList.size() + "string카운트");
            Log.d("날짜", stringList.size() + "string카운트");

            //setDateTimeRecyclerView(stringList);
            textInputEditText.setText(item.getTitle());


            //recyclerDateTimeAdapter.notifyDataSetChanged();

        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        isTitleValid(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.setTitle(s.toString());
            }
        });


    }

    private void accountDialog() {


        accountDialog = new AccountDialog(this, userAccountItemList);

        accountDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        accountDialog.setContentView(R.layout.fragment_account_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(accountDialog.getWindow().getAttributes());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lp.width = (int) (metrics.widthPixels * 0.9f);
        lp.height = (int) (metrics.heightPixels * 0.7f);
        //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.8f;


        accountDialog.getWindow().setAttributes(lp);
        //accountDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded));

        accountDialog.show();


    }

    private void createBottomSheetDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_account_dialog, null);

        recyclerView_account = view.findViewById(R.id.recycler_account);
        user_name = view.findViewById(R.id.user_name);
        String fintech_num = getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");

        user_name.setText(userAccountItemList.get(0).getUser_name());
        setRecyclerView(fintech_num);


        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void setRecyclerView(String fintech_num) {
        Log.d("계좌", userAccountItemList.size() + "");

        RecyclerAccountAdapter recyclerAccountAdapter = new RecyclerAccountAdapter(this, userAccountItemList, fintech_num);
        recyclerView_account.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_account.setAdapter(recyclerAccountAdapter);


    }

    public void changeAccount(String fintech_num) {
        for (int i = 0; i < userAccountItemList.size(); i++) {

            if (userAccountItemList.get(i).getFintech_use_num().equals(fintech_num)) {
                String bank_name = userAccountItemList.get(i).getBank_name();
                String account_num = userAccountItemList.get(i).getAccount_num_masked();
                String account_holder = userAccountItemList.get(i).getAccount_holder_name();

                bank_name_tv.setText(bank_name);
                account_num_tv.setText(account_num);


                getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().putString("fintech_num", fintech_num).apply();

            }
        }

        bottomSheetDialog.dismiss();


    }


    private void isTitleValid(String title) {

        if (title.length() > 1) {

        } else {
            //제목이 너무 짧습니다. 최소 두 글자부터 입력이 가능합니다.
        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mission_basic_send:
                dataCheck();
                //dataSave();
                break;

            case R.id.detail_setting:
                startActivity(new Intent(SingleModeActivity.this, DetailSettingActivity.class));
                break;

            case R.id.account_select:
                //accountDialog();
                createBottomSheetDialog();


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
            if (realmResults2.size() > 0) {
                ly_contact_error.setVisibility(View.GONE);
            } else {
                ly_contact_error.setVisibility(View.VISIBLE);
            }
            //refreshFragment(new SingleModeFragment());
            //setTransferRecyclerView(realmResults2);

        }

    }


    private void dataCheck() {
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        boolean isValid = true;
        Log.d("날짜", stringList.size() + "스트링out");
        Log.d("날짜", item.getCalendarDayList().size() + "캘린더out");


        Log.d("날짜", stringList.size() + "스트링");
        Log.d("날짜", item.getCalendarDayList().size() + "캘린더");
        for (int i = 0; i < item.getCalendarDayList().size(); i++) {
            Log.d("증명", item.getCalendarDayList().get(i).getDate());
        }

        RealmResults<ContactItem> friend_list = realm.where(ContactItem.class).findAll();
        RealmResults<ItemForFriends> itemForFriends = realm.where(ItemForFriends.class).findAll();


        //mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), this, this);
        //mapReverseGeoCoder.startFindingAddress();
        if (TextUtils.isEmpty(item.getTitle())) {
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
                boolean is30 = DateTimeUtils.compareIsFuture30min(date + time);
                if (!is30) {
                    isValid = false;
                }
                Log.d("비니", date + "/" + time + "/" + is30);
            }
        }
        if (mission_mode == 1) {
            if (itemForFriends.size() == 0) {
                Toast.makeText(getApplicationContext(), "친구를 선택해주세요", Toast.LENGTH_LONG).show();
                isValid = false;
            }


        }
        if (mission_mode == 0) {
            if (friend_list.size() == 0) {
                Toast.makeText(getApplicationContext(), "연락를 선택해주세요", Toast.LENGTH_LONG).show();
                ly_contact_error.setVisibility(View.VISIBLE);

                isValid = false;
            }

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

            builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (Utils.isNetworkConnected(getApplicationContext())) {
                        transferAmount();
                    } else {
                        Toast.makeText(getApplicationContext(), "인터넷 연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();

                    }

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

    private void transferAmount() {
        openBanking.requestTransfer(transfer_callback, this);

    }


    private void dataSave() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        if (mission_mode == 0) {
            String mission_id = Utils.getCurrentTime() + "U" + user_id;


            FirebaseDB.registerCheckForServer(mRootRef, user_id, mission_id, item);
            FirebaseDB.registerMainDisplay(this, mRootRef, user_id, mission_id, item);
            FirebaseDB.registerKakaoToken(mRootRef, user_id);
            FirebaseDB.registerMissionInfoList(mRootRef, user_id, mission_id, realm);


            Intent service = new Intent(SingleModeActivity.this, NewLocationService.class);
            boolean is_running = Utils.isServiceRunningInForeground(this, NewLocationService.class);
            if (is_running) {
                stopService(service);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, service);

                } else {
                    startService(service);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, service);

                } else {
                    startService(service);
                }
            }

        }
        if (mission_mode == 1) {

            FirebaseDB.registerInvitationForFriend(mRootRef, this, realm, item);


        }
        if (mission_mode == 2) {

        }


        deleteAllTemporaryData();


    }

    private void deleteAllTemporaryData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                //RealmResults<ItemForDateTime> itemForDateTimes = realm.where(ItemForDateTime.class).findAll();
                SharedPreferences.Editor editor = getSharedPreferences("location_setting", MODE_PRIVATE).edit();
                editor.putLong("lat", Double.doubleToRawLongBits(item.getLat()));
                editor.putLong("lng", Double.doubleToRawLongBits(item.getLng()));
                editor.apply();
                editor.commit();
                //itemForDateTimes.deleteAllFromRealm();
                stringList.clear();

                item.deleteFromRealm();
                RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
                realmResults.deleteAllFromRealm();
                //Intent intent = new Intent();
                //setResult(0, intent);
                finish();

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        realm.close();

        Log.d("목장", "onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //realm.beginTransaction();
        Log.d("목장", "onResume");

    }

    @Override
    public void onBackPressed() {
        Log.d("번들", "onBackPressed");

        realm.close();


        super.onBackPressed();

    }


    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";


        @GET("v2/local/search/keyword.json?")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }


    private void openBankingSetting() {
        SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token", "");
        String user_seq_num = sharedPreferences.getString("user_seq_num", "");
        String refresh_token = sharedPreferences.getString("refresh_token", "");
        Log.d("오픈뱅킹", "사용자토큰" + access_token);

        if (TextUtils.isEmpty(access_token)) {
            //register_account.setText("처음사용자");
            //ly_add_new_account.setVisibility(View.VISIBLE);
            Log.d("오픈뱅킹", "처음사용자");
        } else {
            Log.d("오픈뱅킹", "기사용자정보요청");
            //ly_add_new_account.setVisibility(View.GONE);
            openBanking.requestUserAccountInfo(user_account_info_callback, access_token, user_seq_num);


        }


    }

    private void displayBankAccount(List<UserAccountItem> userAccountItemList) {

        String fintech_num = getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");
        for (int i = 0; i < userAccountItemList.size(); i++) {
            if (userAccountItemList.get(i).getFintech_use_num().equals(fintech_num)) {
                String bank_name = userAccountItemList.get(i).getBank_name();
                String account_num = userAccountItemList.get(i).getAccount_num_masked();
                String account_holder = userAccountItemList.get(i).getAccount_holder_name();

                bank_name_tv.setText(bank_name);
                account_num_tv.setText(account_num);
            }
        }


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
                    }
                }

                break;


        }
    }

    private void alertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error").setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);


    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    private void addMapFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_fragment, fragment).commit();
    }

    private void addCalendarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.calendar_container, fragment).commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment).commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment).commit();
    }

    private void refreshFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(fragment).attach(fragment).commit();


    }

}
