package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.RecyclerAccountAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Fragments.CalendarSelectFragment;
import com.suji.lj.myapplication.Fragments.EventModeFragment;
import com.suji.lj.myapplication.Fragments.MapMissionFragment;

import com.suji.lj.myapplication.Fragments.MultiModeFragment;
import com.suji.lj.myapplication.Fragments.SingleModeFragment;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeCheck;
import com.suji.lj.myapplication.Items.ItemForFriendMissionCheck;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForMultiInvitationKey;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
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
import java.util.List;

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
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, CalendarSelectFragment.OnResetAmountFromCalendarListener, RecyclerTransferRespectivelyAdapter.OnResetAmountDisplayListener, SingleModeFragment.OnResetAmountFromSingleModeListener, MultiModeFragment.OnResetAmountFromMultiListener {
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
                                // displayBankAccount(userAccountItemList);
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

            //openBankingSetting();


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

                        //dataSave();
                    } else {
                        String message = Utils.getValueFromJson(body, "rsp_message");
                        alertMessage(message);
                    }
                }
            });


        }
    };
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
                                    int multi_amount = missionCartItem.getMulti_amount();
                                    int multi_point = missionCartItem.getMulti_point();
                                    int single_amount = missionCartItem.getSingle_amount();
                                    int single_point = missionCartItem.getSingle_point();
                                    if (mission_mode == 0) {
                                        bootPayRequest(missionCartItem, single_amount - single_point);
                                    } else {
                                        bootPayRequest(missionCartItem, multi_amount - multi_point);

                                    }
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
                                    int multi_amount = missionCartItem.getMulti_amount();
                                    int multi_point = missionCartItem.getMulti_point();
                                    int single_amount = missionCartItem.getSingle_amount();
                                    int single_point = missionCartItem.getSingle_point();
                                    if (mission_mode == 0) {
                                        bootPayRequest(missionCartItem, single_amount - single_point);
                                    } else {
                                        bootPayRequest(missionCartItem, multi_amount - multi_point);

                                    }

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
    TextView total_amount;
    TextInputLayout textInputLayout;
    EditText textInputEditText;
    Intent intent;
    RecyclerView transfer_recyclerView;
    LinearLayout ly_contact_error;
    RealmList<ItemForDateTime> stringList = new RealmList<>();
    NestedScrollView scrollView;
    RecyclerView recyclerView;
    SearchView searchView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    OpenBanking openBanking = OpenBanking.getInstance();
    Realm realm;
    TextView detail_setting;
    TextView bank_name_tv;
    TextView account_num_tv;
    AppCompatCheckBox checkBox_term_use, checkBox_term_private, checkBox_term_all;
    boolean TERMS_AGREE_1 = false; // No Check = 0, Check = 1
    boolean TERMS_AGREE_2 = false; // No Check = 0, Check = 1

    Toolbar toolbar;

    Fragment fa, fb, fc;
    int mission_mode = 0;
    Runnable runnable;
    Handler mHandler;
    BottomSheetDialog bottomSheetDialog;
    private RecyclerView recyclerView_account;
    TextView user_name;
    LinearLayout title;
    TabLayout tab_rule;
    TextView add_new_account;
    KakaoRest kakaoRest;
    TextView title_error;
    RelativeLayout text_clear;
    TextView text_length;
    LinearLayout ly_title_error;
    ToggleButton kakao_pay;
    ToggleButton naver_pay;
    ToggleButton credit_pay;
    TextView text_rest_point;
    EditText edit_point;
    TextView text_total_payment, text_point_total;
    TextView text_actual_payment;
    boolean point_callback_state = false;
    boolean pay_method_select = false;
    int pay_method = 0;
    int point_input = 0;
    int point_amount = 0;
    int total = 0;
    int display_point = 0;
    int actual_payment = 0;


    static final int SINGLE_MODE = 0;
    static final int MULTI_MODE = 1;
    static final int KAKAO_PAY = 0;
    static final int NAVER_PAY = 1;
    String user_id;
    //String mission_id;
    DatabaseReference mRootRef;
    List<ContactItem> contactItemList;
    List<ItemForFriends> itemForFriendsList;

    private boolean hasFractionalPart;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");

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

        tab_rule = findViewById(R.id.rule_tab);
        toolbar = findViewById(R.id.toolbar);
        ly_contact_error = findViewById(R.id.ly_contact_error);
        title_error = findViewById(R.id.title_error);
        text_clear = findViewById(R.id.text_clear);
        text_length = findViewById(R.id.text_length);
        ly_title_error = findViewById(R.id.ly_title_error);
        kakao_pay = findViewById(R.id.kakao_pay);
        naver_pay = findViewById(R.id.naver_pay);
        credit_pay = findViewById(R.id.credit_pay);
        text_rest_point = findViewById(R.id.rest_point);
        edit_point = findViewById(R.id.edit_point);
        text_total_payment = findViewById(R.id.total_payment);
        text_point_total = findViewById(R.id.point_total);
        text_actual_payment = findViewById(R.id.actual_payment);
        title = findViewById(R.id.title);

        user_id = Account.getUserId(this);
        kakaoRest = KakaoRest.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();


        Realm.init(this);


        realm = Realm.getDefaultInstance();
        textInputEditText.addTextChangedListener(this);
        next_btn.setOnClickListener(this);
        checkBox_term_use.setOnCheckedChangeListener(this);
        checkBox_term_private.setOnCheckedChangeListener(this);
        checkBox_term_all.setOnCheckedChangeListener(this);
        next_btn.setEnabled(false);
        next_btn.setClickable(false);

        //Utils.fixMapScroll(imageView,scrollView);

        toolbar.setTitle("새 약속");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        tab_rule.addTab(tab_rule.newTab().setText("혼자서"));
        tab_rule.addTab(tab_rule.newTab().setText("여럿이서"));
        tab_rule.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectTab(tab);

            }
        });
        text_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_clear.setVisibility(View.INVISIBLE);
                textInputEditText.setText("");
            }
        });

        edit_point.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());
                    if (!v.getText().toString().equals("")) {
                        point_input = Integer.valueOf(v.getText().toString().replaceAll(",", ""));
                    } else {
                        point_input = 0;
                    }
                    pointCalculate(point_input);


                    edit_point.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });
        edit_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
                    hasFractionalPart = true;
                } else {
                    hasFractionalPart = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("오픈뱅킹", "after_changed");
                edit_point.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = edit_point.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = edit_point.getSelectionStart();
                    if (hasFractionalPart) {
                        edit_point.setText(df.format(n));
                    } else {
                        edit_point.setText(dfnd.format(n));
                    }
                    endlen = edit_point.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= edit_point.getText().length()) {
                        edit_point.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        edit_point.setSelection(edit_point.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }
                edit_point.addTextChangedListener(this);


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

                    return true;
                }
                return false;
            }
        });
        CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView == kakao_pay) {
                        naver_pay.setChecked(false);
                        credit_pay.setChecked(false);
                        pay_method = 0;
                        pay_method_select = true;
                    }
                    if (buttonView == naver_pay) {
                        kakao_pay.setChecked(false);
                        credit_pay.setChecked(false);
                        pay_method = 1;
                        pay_method_select = true;
                    }
                    if (buttonView == credit_pay) {
                        kakao_pay.setChecked(false);
                        naver_pay.setChecked(false);
                        pay_method = 2;
                        pay_method_select = true;
                    }
                } else {
                    pay_method_select = false;
                }
                payButtonManager(kakao_pay, kakao_pay.isChecked());
                payButtonManager(naver_pay, naver_pay.isChecked());
                payButtonManager(credit_pay, credit_pay.isChecked());

            }
        };
        kakao_pay.setOnCheckedChangeListener(changeChecker);
        naver_pay.setOnCheckedChangeListener(changeChecker);
        credit_pay.setOnCheckedChangeListener(changeChecker);


        String user_id = Account.getUserId(this);
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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                text_rest_point.setText("error!");
            }
        });

        //openBankingSetting();
        initiate();
        TabLayout.Tab tab = tab_rule.getTabAt(mission_mode);
        if (tab != null)
            tab.select();

        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                addMapFragment(new MapMissionFragment(scrollView));

                addCalendarFragment(new CalendarSelectFragment());


            }
        };
        mHandler.post(runnable);
    }

    private void payButtonManager(ToggleButton button, boolean isChecked) {
        if (isChecked) {
            button.setBackgroundDrawable(getDrawable(R.drawable.rounded_rectangle));
            button.setTextColor(getResources().getColor(R.color.White));
        } else {
            button.setBackgroundDrawable(getDrawable(R.drawable.rectangle_stroke));
            button.setTextColor(getResources().getColor(R.color.colorPrimary));


        }

    }


    private void initiate() {
        long count = realm.where(MissionCartItem.class).count();

        if (count == 0) {
            //setDateTimeRecyclerView(stringList);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MissionCartItem item = realm.createObject(MissionCartItem.class);
                    RealmList<ItemForDateTime> realmList = item.getCalendarDayList();
                    stringList.clear();
                    stringList = realmList;
                    //setDateTimeRecyclerView(realmList);
                }
            });

        } else {
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
            stringList = item.getCalendarDayList();

            //setDateTimeRecyclerView(stringList);
            mission_mode = item.getMission_mode();
            textInputEditText.setText(item.getTitle());


            //recyclerDateTimeAdapter.notifyDataSetChanged();

        }
        singleAmountDisplay();

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
            isTitleValid(length);
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


    private void createBottomSheetDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.fragment_account_dialog, null);

        recyclerView_account = view.findViewById(R.id.recycler_account);
        user_name = view.findViewById(R.id.user_name);
        add_new_account = view.findViewById(R.id.add_new_account);
        String fintech_num = getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");

        user_name.setText(userAccountItemList.get(0).getUser_name());
        SpannableString content = new SpannableString("+ 새 계좌등록");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        add_new_account.setText(content);
        add_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OpenBankingActivity.class));
            }
        });
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

    public void changeAccount(UserAccountItem item) {


        String bank_name = item.getBank_name();
        String account_num = item.getAccount_num_masked();
        String account_holder = item.getAccount_holder_name();

        bank_name_tv.setText(bank_name);
        account_num_tv.setText(account_num);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                missionCartItem.setBank_name(bank_name);
                missionCartItem.setAccount_num(account_num);
                missionCartItem.setAccount_holder(account_holder);
            }
        });


        bottomSheetDialog.dismiss();


    }


    private void isTitleValid(int length) {
        text_length.setText(length + "/" + 20);
        if (length < 20) {
            text_length.setTextColor(getResources().getColor(R.color.colorError));
            //제목이 너무 짧습니다. 최소 두 글자부터 입력이 가능합니다.
        } else {
            text_length.setTextColor(getResources().getColor(R.color.colorSuccess));
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
                createBottomSheetDialog();
                // MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                // kakaoRest.getAddressFromLocation(get_address, item.getLat(), item.getLng());


                break;
            case R.id.kakao_pay:


                break;
            case R.id.naver_pay:
                break;
            case R.id.credit_pay:
                break;


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

                RealmResults<ItemForFriends> realmResults = realm.where(ItemForFriends.class).findAll();
                RealmList<ItemForFriends> friendList = new RealmList<>();
                friendList.addAll(realmResults.subList(0, realmResults.size()));
                missionCartItem.setFriendsList(friendList);
                String mission_id = Utils.getCurrentTime() + "U" + user_id;
                missionCartItem.setMission_id(mission_id);

            }
        });

        //MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        MissionCartItem item = realm.copyFromRealm(realm.where(MissionCartItem.class).findFirst());
        boolean isValid = true;

        RealmResults<ContactItem> friend_list = realm.where(ContactItem.class).findAll();
        RealmResults<ItemForFriends> itemForFriends = realm.where(ItemForFriends.class).findAll();

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
                    Toast.makeText(getApplicationContext(), "시간을 30분 이후 부터 선택해주세요", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "연락처를 선택해주세요", Toast.LENGTH_LONG).show();
                ly_contact_error.setVisibility(View.VISIBLE);

                isValid = false;
            }

        }
        if (!pay_method_select) {
            Toast.makeText(getApplicationContext(), "결제수단을 선택해주세요", Toast.LENGTH_LONG).show();
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
            builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (Utils.isNetworkConnected(getApplicationContext())) {


                        kakaoRest.getAddressFromLocation(get_address, lat, lng);

                        //finish();

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
/*
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
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                        item.setBank_name(bank_name);
                        item.setAccount_num(account_num);
                        item.setAccount_holder(account_holder);
                    }
                });
            }
        }


    }


 */

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

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment, tag).commit();
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


    public void singleAmountDisplay() {
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

        int dates = item.getCalendarDayList().size();
        int amount = item.getSingle_amount();
        total = realmResults2.size() * amount * dates;

        edit_point.setText(String.valueOf(0));
        String point_total_string = 0 + " P";
        text_point_total.setText(point_total_string);
        point_input = 0;
        text_actual_payment.setText(Utils.makeNumberComma(total));
        String total_payment_string = Utils.makeNumberComma(total) + " 원";
        text_total_payment.setText(total_payment_string);
        String rest_point_string = point_amount + " P";
        text_rest_point.setText(rest_point_string);
    }

    public void multiAmountDisplay() {
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        RealmResults<ItemForFriends> realmResults = realm.where(ItemForFriends.class).findAll();

        int dates = item.getCalendarDayList().size();
        int amount = item.getMulti_amount();
        int friend_num = realmResults.size();
        total = friend_num * amount * dates;

        edit_point.setText(String.valueOf(0));
        String point_total_string = 0 + " P";
        text_point_total.setText(point_total_string);
        point_input = 0;
        text_actual_payment.setText(Utils.makeNumberComma(total));
        String total_payment_string = Utils.makeNumberComma(total) + " 원";
        text_total_payment.setText(total_payment_string);
        String rest_point_string = point_amount + " P";
        text_rest_point.setText(rest_point_string);


    }

    @Override
    public void onResetAmountDisplay() {
        Log.d("옵티마", "들어와");

    }

    @Override
    public void onResetAmountFromSingleMode() {
        switch (mission_mode) {
            case 0:
                //singleFragmentAmountDisplay();
                singleAmountDisplay();
                break;
            case 1:
                //multiModeFragmentAmountDisplay();
                multiAmountDisplay();
                break;


        }
    }

    @Override
    public void onResetAmountFromMulti() {
        switch (mission_mode) {
            case 0:
                //singleFragmentAmountDisplay();
                singleAmountDisplay();
                break;
            case 1:
                //multiModeFragmentAmountDisplay();
                multiAmountDisplay();
                break;


        }
    }

    @Override
    public void onResetAmountFromCalendar() {
        switch (mission_mode) {
            case 0:
                singleAmountDisplay();
                break;
            case 1:
                multiAmountDisplay();
                break;


        }

    }

    private void pointCalculate(int point_input) {
        if (point_callback_state) {

            if ((point_input <= point_amount) && (point_amount <= total)) {
                display_point = point_input;

            }
            if ((point_input <= point_amount) && (total <= point_amount)) {
                display_point = point_input;

            }
            if ((point_amount <= total) && (total <= point_input)) {
                display_point = point_amount;

            }
            if ((point_amount <= point_input) && (point_input <= total)) {
                display_point = point_amount;

            }
            if ((total <= point_input) && (point_input <= point_amount)) {
                display_point = total;

            }
            if ((total <= point_amount) && (point_amount <= point_input)) {
                display_point = total;


            }

            edit_point.setText(String.valueOf(display_point));
            String point_total_string = Utils.makeNumberComma(display_point) + " P";
            text_point_total.setText(point_total_string);
            String actual_payment_string = String.valueOf(total - display_point);
            actual_payment = total - display_point;
            text_actual_payment.setText(actual_payment_string);
            String rest_point_string = Utils.makeNumberComma(point_amount - display_point) + " P";
            text_rest_point.setText(rest_point_string);


        }


    }


    public void bootPayRequest(MissionCartItem item, int actual_payment) {
        // 결제호출
        //int stuck = 10;
        String key = "5d25d429396fa67ca2bd0f45";
        BootpayAnalytics.init(this, key);

        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});

        Bootpay.init(getFragmentManager())
                .setApplicationId(key) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KAKAO) // 결제할 PG 사
                .setMethod(Method.EASY) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setTaxFree(10000.0)
                .setUX(UX.PG_DIALOG)
//              .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName(item.getTitle()) // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(actual_payment) // 결제할 금액
                .addItem(item.getTitle(), 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        Bootpay.confirm(message); // 재고가 있을 경우.
                        //else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우

                        Log.d("부트", message + "confirm");
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("부트", message + "done");
                        //chargePoint(message);
                        function(message, item);

                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("부트", message + "ready");
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("부트", message);
                        //finish();
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("부트", message);
                        //finish();
                    }
                })
                .onClose(new CloseListener() { //결제창이 닫힐때 실행되는 부분
                    @Override
                    public void onClose(String message) {
                        Log.d("부트", "close");
                        // finish();
                    }
                })
                .request();
    }

    private void function(String message, MissionCartItem item) {//영수증 검증
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/bootpay";


        RequestBody formBody = new FormBody.Builder()
                .add("receipt_id", Utils.getValueFromJson(message, "receipt_id"))
                .add("price", Utils.getValueFromJson(message, "price"))
                .add("user_id", user_id)
                .add("message", message)
                .add("point", String.valueOf(item.getSingle_point()))
                .add("total_payment", String.valueOf(item.getSingle_amount()))
                .add("mission_id", item.getMission_id())
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("부트페이", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = "";
                if (!response.isSuccessful()) {
                    Log.d("부트페이", "fail response from firebase cloud function");
                } else {
                    if (responseBody != null) {
                        String receipt = responseBody.string();

                        Log.d("부트페이", "성공" + user_id);
                        //Log.d("부트페이", "성공" + Utils.getValueFromJson(responseBody.string(), "date_time"));
                        //displayReceipt(result);
                        dataSave(item, receipt);
                    }
                }

            }
        });
    }

    private void dataSave(MissionCartItem item, String receipt) {
        Log.d("순서", "dataSave");


        //MissionCartItem item = realm.where(MissionCartItem.class).findFirst();

        if (mission_mode == SINGLE_MODE) {
            //int amount = Integer.valueOf(total_amount.getText().toString().replaceAll(",", ""));


            //FirebaseDB.registerCheckForServer(mRootRef, user_id, mission_id, item);
            //FirebaseDB.registerMainDisplay(mRootRef, user_id, mission_id, item);
            //FirebaseDB.registerKakaoToken(mRootRef, user_id);
            FirebaseDB.registerMissionInfoList(mRootRef, this, item, receipt);//미션목록추가

            /** 파이어베이스 추가후 deleteRealm **/


        }
        if (mission_mode == MULTI_MODE) {//multi


            String title = item.getTitle();
            String address = item.getAddress();
            String mission_id = item.getMission_id();
            String manager_id = user_id;
            String user_name = Account.getUserName(this);
            String image = Account.getUserThumbnail(this);
            List<ItemPortion> portionList = item.getPortionList();
            List<ItemForDateTime> dateTimeList = item.getCalendarDayList();
            List<ItemForDateTimeCheck> dateTimeCheckList = new ArrayList<>();
            double lat = item.getLat();
            double lng = item.getLng();



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
            me.setThumbnail(image);
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
            multiModeRequest.setManager_thumbnail(image);
            multiModeRequest.setManager_name(user_name);
            multiModeRequest.setFriendRequestList(requestList);
            multiModeRequest.setItemPortionList(portionList);
            multiModeRequest.setCalendarDayList(dateTimeList);
            multiModeRequest.setDateTimeCheckList(dateTimeCheckList);



            for(int i=0; i<requestList.size(); i++) {

                String friend_id = requestList.get(i).getFriend_id();
                mRootRef.child("user_data").child(friend_id).child("invitation").push().setValue(multiModeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                            }
                        });

                    }
                });

            }
            Intent intent = new Intent(getApplicationContext(), MissionCheckActivity.class);
            intent.putExtra("receipt", receipt);
            startActivity(intent);
            realm.close();
            finish();


            //FirebaseDB.registerInvitationForFriend(mRootRef, this, item, receipt);
            //FirebaseDB.registerMissionInfoList(mRootRef, this, item, receipt);


        }

        //deleteAllTemporaryData();


    }

    private void selectTab(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                if (fa == null) {
                    fa = new SingleModeFragment();
                    addFragment(fa, "single");

                }
                if (fa != null) showFragment(fa);
                if (fb != null) hideFragment(fb);
                if (fc != null) hideFragment(fc);
                mission_mode = 0;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                        item.setMission_mode(mission_mode);
                    }
                });
                singleAmountDisplay();

                break;
            case 1:
                if (fb == null) {
                    fb = new MultiModeFragment(SingleModeActivity.this);
                    addFragment(fb, "multi");
                }

                if (fa != null) hideFragment(fa);
                if (fb != null) showFragment(fb);
                if (fc != null) hideFragment(fc);
                mission_mode = 1;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                        item.setMission_mode(mission_mode);
                    }
                });

                multiAmountDisplay();

                break;
            case 2:
                if (fc == null) {
                    fc = new EventModeFragment();

                    addFragment(fc, "event");
                }

                if (fa != null) hideFragment(fa);
                if (fb != null) hideFragment(fb);
                if (fc != null) showFragment(fc);
                mission_mode = 2;
                break;


        }


    }




}

/**
 * 확인 -> 주소불러오기(api콜 절약을 위해 확인 뒤에 넣음) -> 결제 -> 영수증검증 -> db저장 -> relam초기화 -> 포어그라운드 서비스 활성화 -> 영수증 액티비
 **/
