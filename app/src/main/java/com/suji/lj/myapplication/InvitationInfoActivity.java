package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationFriendAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerPortionInvitationAdapter;
import com.suji.lj.myapplication.Fragments.CalendarShowFragment;
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForDateTimeByList;
import com.suji.lj.myapplication.Items.ItemForDateTimeCheck;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForFriendMissionCheck;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemForTransaction;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.MissionInfoList;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

public class InvitationInfoActivity extends AppCompatActivity implements View.OnClickListener {


    RecyclerView recyclerView;
    Toolbar toolbar;

    NestedScrollView scrollView;
    RecyclerView recycler_portion;
    LinearLayout ly_accept_or_decline;
    TextView tv_accept;
    TextView tv_decline;

    LinearLayout ly_activate_or_cancel;
    TextView tv_activate;
    TextView tv_cancel;

    LinearLayout ly_join_cancel;
    TextView tv_join_cancel;

    LinearLayout ly_payment;
    LinearLayout ly_agreement;


    TextView tv_address;
    Runnable runnable;
    Handler mHandler;
    ToggleButton kakao_pay;
    ToggleButton naver_pay;
    ToggleButton credit_pay;
    boolean pay_method_select = false;
    int pay_method = 0;
    String user_id;
    String mission_id;
    ItemForMultiModeRequest item;
    TextView text_rest_point;
    EditText edit_point;
    TextView text_total_payment, text_point_total;
    TextView text_actual_payment;
    boolean point_callback_state = false;
    int point_input = 0;
    int point_amount = 0;
    int total = 0;
    int display_point = 0;
    int actual_payment = 0;
    private boolean hasFractionalPart;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    TextView tv_rest_time;
    TextView tv_penalty;
    boolean isTimerRunning = false;
    CountDownTimer timer;
    List<ItemForFriendResponseForRequest> friendList = new ArrayList<>();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_info);
        recyclerView = findViewById(R.id.recycler_friend);
        recycler_portion = findViewById(R.id.recycler_distribution);
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scroll_view);
        ly_accept_or_decline = findViewById(R.id.ly_accept_or_decline);
        tv_accept = findViewById(R.id.tv_accept);
        tv_decline = findViewById(R.id.tv_decline);

        ly_activate_or_cancel = findViewById(R.id.ly_activate_or_cancel);
        tv_activate = findViewById(R.id.tv_activate);
        tv_cancel = findViewById(R.id.tv_cancel);

        ly_join_cancel = findViewById(R.id.ly_join_cancel);
        tv_join_cancel = findViewById(R.id.tv_join_cancel);

        ly_payment = findViewById(R.id.ly_payment);
        ly_agreement = findViewById(R.id.ly_agreement);


        tv_address = findViewById(R.id.tv_address);
        tv_penalty = findViewById(R.id.tv_penalty);
        kakao_pay = findViewById(R.id.kakao_pay);
        naver_pay = findViewById(R.id.naver_pay);
        credit_pay = findViewById(R.id.credit_pay);
        text_rest_point = findViewById(R.id.tv_rest_point);
        edit_point = findViewById(R.id.edit_point);
        text_total_payment = findViewById(R.id.total_payment);
        text_point_total = findViewById(R.id.point_total);
        text_actual_payment = findViewById(R.id.actual_payment);
        tv_rest_time = findViewById(R.id.tv_rest_time);

        tv_accept.setOnClickListener(this);
        tv_decline.setOnClickListener(this);
        tv_activate.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_join_cancel.setOnClickListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user_id = Account.getUserId(this);
        //Bundle extras = getIntent().getExtras();


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


        String mission_id = getIntent().getExtras().getString("mission_id", "");

        Log.d("리퀘스트", mission_id + "  미션");

        databaseReference.child("multi_data").child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        item = snapshot.getValue(ItemForMultiModeRequest.class);
                        displayData(item);


                    }


                } else {
                    //데이터가 없으면 종료

                    finish();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void displayData(ItemForMultiModeRequest item) {
        if (item != null) {

            if (item.getManager_id().equals(user_id)) {
                ly_accept_or_decline.setVisibility(View.GONE);
                ly_activate_or_cancel.setVisibility(View.VISIBLE);
                ly_join_cancel.setVisibility(View.GONE);
                ly_agreement.setVisibility(View.GONE);
                ly_payment.setVisibility(View.GONE);
            } else {

                switch (item.getAccept()) {
                    case 0:
                        ly_join_cancel.setVisibility(View.GONE);
                        ly_activate_or_cancel.setVisibility(View.GONE);
                        ly_accept_or_decline.setVisibility(View.VISIBLE);
                        ly_agreement.setVisibility(View.VISIBLE);
                        ly_payment.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        ly_join_cancel.setVisibility(View.VISIBLE);
                        ly_activate_or_cancel.setVisibility(View.GONE);
                        ly_accept_or_decline.setVisibility(View.GONE);
                        ly_agreement.setVisibility(View.GONE);
                        ly_payment.setVisibility(View.GONE);
                        break;
                    case 2://거절한상태
                        ly_join_cancel.setVisibility(View.GONE);
                        ly_activate_or_cancel.setVisibility(View.GONE);
                        ly_accept_or_decline.setVisibility(View.GONE);
                        ly_agreement.setVisibility(View.GONE);
                        ly_payment.setVisibility(View.GONE);
                        break;


                }

            }


            String first_date_time = item.getCalendarDayList().get(0).getDate() + item.getCalendarDayList().get(0).getTime() + "00";
            String register_date_time = item.getRegister_time();
            displayRestTime(register_date_time, first_date_time);
            friendList = item.getFriendRequestList();
            setupRecyclerView(friendList);
            Log.d("제목", item.getTitle());
            Log.d("제목", item.getFail_penalty() + "");
            getSupportActionBar().setTitle(item.getTitle());
            tv_address.setText(item.getAddress());
            mission_id = item.getMission_id();
            tv_penalty.setText(Utils.makeNumberComma(item.getFail_penalty()) + " 원");
            total = item.getFail_penalty() * item.getCalendarDayList().size();
            actual_payment = total - point_input;
            text_actual_payment.setText(Utils.makeNumberComma(total));
            text_total_payment.setText(Utils.makeNumberComma(total));

            addCalendarFragment(new CalendarShowFragment(getApplicationContext(), item.getCalendarDayList()));
            addFragment(new MapFragment(scrollView, item.getLat(), item.getLng()));

            setUpRecyclerViewPortion(item.getItemPortionList());


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
        }


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


    private void setupRecyclerView(List<ItemForFriendResponseForRequest> list) {
        RecyclerInvitationFriendAdapter adapter = new RecyclerInvitationFriendAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);


    }

    private void setUpRecyclerViewPortion(List<ItemPortion> list) {
        RecyclerPortionInvitationAdapter adapter = new RecyclerPortionInvitationAdapter(list);
        recycler_portion.setLayoutManager(new LinearLayoutManager(this));
        recycler_portion.setAdapter(adapter);


    }


    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_fragment, fragment).commit();
    }

    private void addCalendarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.calendar_layout, fragment).commit();
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


    public void bootPayRequest() {
        // 결제호출
        //int stuck = 10;
        String key = "5d25d429396fa67ca2bd0f45";
        BootpayAnalytics.init(this, key);

        //mission_id = Utils.getCurrentTime() + "U" + user_id;
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
                        function(message);

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
                        finish();
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("부트", message);
                        finish();
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

    private void function(String message) {//영수증 검증
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/bootpay";


        RequestBody formBody = new FormBody.Builder()
                .add("receipt_id", Utils.getValueFromJson(message, "receipt_id"))
                .add("price", Utils.getValueFromJson(message, "price"))
                .add("user_id", user_id)
                .add("message", message)
                .add("point", String.valueOf(point_input))
                .add("total_payment", String.valueOf(actual_payment))
                .add("mission_id", mission_id)
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

                        dataSave();
                    }
                }

            }
        });
    }

    private void dataSave() {//미션수락시 친구들에게 수락여부 알림
        String mission_id = item.getMission_id();


        databaseReference.child("user_data").child(user_id).child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForMultiModeRequest multiModeRequest = snapshot.getValue(ItemForMultiModeRequest.class);
                        if (multiModeRequest != null) {
                            int count = 0;
                            List<ItemForFriendResponseForRequest> list = multiModeRequest.getFriendRequestList();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getAccept() == 1) {
                                    count++;
                                    if (count == list.size() - 1) {/** 내가 마지막으로 수락하는거라면**/


                                        for (int j = 0; j < list.size(); j++) {
                                            String friend_id = list.get(j).getFriend_id();
                                            //나를 제외한 친구들
                                            databaseReference.child("user_data").child(friend_id).child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);

                                                        if (item != null) {

                                                            List<ItemForFriendResponseForRequest> friendsLists = item.getFriendRequestList();
                                                            int count = 0;
                                                            for (int i = 0; i < friendsLists.size(); i++) {
                                                                if (user_id.equals(friendsLists.get(i).getFriend_id())) {
                                                                    ItemForFriendResponseForRequest request = friendsLists.get(i);
                                                                    request.setAccept(1);
                                                                    friendsLists.set(i, request);
                                                                    item.setFriendRequestList(friendsLists);
                                                                    snapshot.getRef().setValue(item);

                                                                }


                                                            }


                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }


                                    }


                                }


                            }

                        }


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_accept:
                bootPayRequest();
                /** 결제 -> 영수증저장 -> 본인포함 친구데이터에 db 1로 설정 **/
                break;

            case R.id.tv_decline:
                int accept = 2;
                /** 본인포함 친구데이터에 db 2로 설정 **/
               // deleteFriendData(false);
                break;

            case R.id.tv_activate:

                databaseReference.child("user_data").child(user_id).child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                int count = 0;
                                List<ItemForFriendResponseForRequest> list = snapshot.getValue(ItemForMultiModeRequest.class).getFriendRequestList();
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getAccept() == 1) {
                                        count++;
                                    }
                                }

                                if (count > 1) {/** 2명이상 수락했으면 약속 시**/
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                    builder.setMessage("약속을 시작하시겠습니까?");
                                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            deleteFriendData(false);
                                            missionActivate();
                                        }
                                    });
                                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();


                                } else {
                                    Utils.makeAlertDialog("2명 이상이 약속을 수락하면 시작할 수 있습니다.", InvitationInfoActivity.this);


                                }


                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                /** 최소인원 2명 확인 -> 초대db 삭 -> missioninfolist 생성 -> display 생성 -> server_list 생성 **/
                break;
            case R.id.tv_cancel:
                /** 친구각각의 mission_id로 찾기 -> 찾은영수증으로 결제취소요청 -> 본인포함 친구데이터 삭제 **/


                AlertDialog.Builder cancel_builder = new AlertDialog.Builder(this);
                cancel_builder.setMessage("약속을 취소하시겠습니까?");
                cancel_builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        requestPaymentCancel(user_id);
                    }
                });
                cancel_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                cancel_builder.create().show();

/*

                for (int i = 0; i < item.getFriendRequestList().size(); i++) {
                    String friend_id = item.getFriendRequestList().get(i).getFriend_id();
                    if (!user_id.equals(friend_id)) {
                        requestPaymentCancel(friend_id);
                    }

                }
                requestPaymentCancel(user_id);

 */


                break;
            case R.id.tv_join_cancel:
                //결제취소
                AlertDialog.Builder join_cancel_builder = new AlertDialog.Builder(this);
                join_cancel_builder.setMessage("약속을 등록하시겠습니까?");
                join_cancel_builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //requestPaymentJoinCancel(user_id);
                    }
                });
                join_cancel_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                join_cancel_builder.create().show();


                /** mission_id 로 영수증 찾기 -> 찾은 영수증으로 결제취소요청 -> 본인포함 친구데이터에서 db상태 0으로 변경  **/


                break;


        }
    }

    /**
     * 제안한 사람이 미션을 취소할 경우 본인의 데이터 삭제
     **/
    private void deleteFriendData(boolean is_manager) {


        /** 내가 방장인지 먼저 확인**/
        if (is_manager) {
            /** 이미 결제되어있는 친구들 환불시켜주기**/







            /** 공유데이터 detail삭제**/

            databaseReference.child("invitation_data").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot shot : snapshot.getChildren()){
                            shot.getRef().child("detail").removeValue();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            /** 방장이면 내 정보 포함 친구들 정보 모두 삭제 **/
            for (int i = 0; i < friendList.size(); i++) {
                String friend_id = friendList.get(i).getFriend_id();

                /** preview 삭제 **/


                databaseReference.child("user_data").child(friend_id).child("invitation").child("preview").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeValue();


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        } else {/** 일반 참여자라면 내것만 삭제**/


            databaseReference.child("user_data").child(user_id).child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getRef().removeValue();


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("user_data").child(user_id).child("invitation").child("preview").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getRef().removeValue();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /** 본인을 제외하고 친구들데이터에 본인이 미션을 취소했다는 시그널을 남은 친구들의 invitation detail에  전달**/

            for (int i = 0; i < friendList.size(); i++) {
                String friend_id = friendList.get(i).getFriend_id();
                if (!friend_id.equals(user_id)) {
                    databaseReference.child("user_data").child(friend_id).child("invitation").child("detail").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);
                                    if (item != null) {
                                        for (int j = 0; j < item.getFriendRequestList().size(); j++) {
                                            String id = item.getFriendRequestList().get(j).getFriend_id();
                                            if (id.equals(user_id)) {
                                                item.getFriendRequestList().get(j).setAccept(2);
                                            }


                                        }

                                        snapshot.getRef().setValue(item);
                                    }

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }


        }


    }


    private void missionActivate() {

        String title = item.getTitle();
        String address = item.getAddress();
        String mission_id = item.getMission_id();
        String user_name = Account.getUserName(this);
        String user_image = Account.getUserThumbnail(this);
        List<ItemPortion> portionList = item.getItemPortionList();
        String min_date = item.getCalendarDayList().get(0).getDate();//이론상 최소날짜
        String max_date = item.getCalendarDayList().get(item.getCalendarDayList().size() - 1).getDate();//이론상 최대날짜
        List<ItemForDateTime> dateTimeList = item.getCalendarDayList();
        List<ItemForDateTimeCheck> dateTimeCheckList = item.getDateTimeCheckList();
        double lat = item.getLat();
        double lng = item.getLng();
        int penalty = item.getFail_penalty();
        int radius = item.getRadius();


        /** 기본정보 **/
        MissionInfoList missionInfoList = new MissionInfoList();
        Map<String, ItemForDateTimeByList> mission_dates = new HashMap<>();


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
        missionInfoList.setPenalty_amount(penalty);
        missionInfoList.setSingle_mode(false);
        missionInfoList.setRadius(radius);
        missionInfoList.setItemPortionList(portionList);


        /** 친구정**/
        List<ItemForFriendByDay> friendByDayList = new ArrayList<>();
        List<ItemForFriendResponseForRequest> friendRequestList = item.getFriendRequestList();
        for (int i = 0; i < friendRequestList.size(); i++) {
            ItemForFriendByDay object = new ItemForFriendByDay();
            object.setFriend_name(friendRequestList.get(i).getFriend_name());
            object.setFriend_image(friendRequestList.get(i).getThumbnail());
            object.setUser_id(friendRequestList.get(i).getFriend_id());
            object.setSuccess(false);
            friendByDayList.add(object);
        }
        // add my profile

        /** 내정보삽입**/
        ItemForFriendByDay my_profile = new ItemForFriendByDay();
        my_profile.setUser_id(user_id);
        my_profile.setFriend_image(user_image);
        my_profile.setSuccess(false);
        my_profile.setFriend_name(user_name);

        friendByDayList.add(my_profile);

        missionInfoList.setFriendByDayList(friendByDayList);


        /** 날짜정보입력**/
        List<ItemForDateTime> itemForDateTimeList = item.getCalendarDayList();

        for (int j = 0; j < itemForDateTimeList.size(); j++) {
            int year = itemForDateTimeList.get(j).getYear();
            int month = itemForDateTimeList.get(j).getMonth();
            int day = itemForDateTimeList.get(j).getDay();
            int hour = itemForDateTimeList.get(j).getHour();
            int min = itemForDateTimeList.get(j).getMin();


            String date = DateTimeUtils.makeDateForServer(year, month, day);
            String time = DateTimeUtils.makeTimeForServer(hour, min);
            //Log.d("파베", "날짜" + date);
            ItemForDateTimeByList object = new ItemForDateTimeByList();
            object.setTime_stamp(date + time);
            object.setSuccess(false);
            object.setFriendByDayList(friendByDayList);
            mission_dates.put(date, object);
        }
        missionInfoList.setMission_dates(mission_dates);


        //mRootRef.child("user_data").child("multi_mode").child("M" + mission_id).child("mission_info_list").setValue(missionInfoList);

        /** missio display 생성 (친구수 * 날짜)**/

        for (int i = 0; i < friendRequestList.size(); i++) {
            String friend_id = friendRequestList.get(i).getFriend_id();
            for (int j = 0; j < itemForDateTimeList.size(); j++) {


                ItemForMissionByDay itemForMissionByDay = new ItemForMissionByDay();

                itemForMissionByDay.setTitle(title);
                itemForMissionByDay.setAddress(address);
                itemForMissionByDay.setTime(DateTimeUtils.makeTimeForServer(itemForDateTimeList.get(j).getHour(), itemForDateTimeList.get(j).getMin()));
                itemForMissionByDay.setLat(lat);
                itemForMissionByDay.setLng(lng);
                itemForMissionByDay.setMission_id(mission_id);
                itemForMissionByDay.setSingle_mode(false);


                int year = itemForDateTimeList.get(j).getYear();
                int month = itemForDateTimeList.get(j).getMonth();
                int day = itemForDateTimeList.get(j).getDay();
                String date = DateTimeUtils.makeDateForServer(year, month, day);

                itemForMissionByDay.setDate(date);
                itemForMissionByDay.setSuccess(false);


                int hour = itemForDateTimeList.get(j).getHour();
                int min = itemForDateTimeList.get(j).getMin();
                String time = DateTimeUtils.makeTimeForServer(hour, min);
                itemForMissionByDay.setDate_time(date + time);

                itemForMissionByDay.setFriendByDayList(friendByDayList);


                //mission_main_list.put(date + time, itemForMissionByDay);

                /** 공유데이터 하나 전송**/


                databaseReference.child("user_data").child(friend_id).child("mission_display").push().setValue(itemForMissionByDay);
                databaseReference.child("user_data").child(friend_id).child("mission_info_list").push().setValue(missionInfoList);
            }


        }


    }


    /**
     * 본인 결제건 취소
     **/
    private void requestPaymentCancel(String user) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String date_time = simpleDateFormat.format(new Date().getTime());
        String user_name = Account.getUserName(this);
        String manager_id = item.getManager_id();


        /** 내가 방장이라면 나 포함 친구들 환불모두 처리**/
        if (manager_id.equals(user_id)) {
            for (int i = 0; i < friendList.size(); i++) {
                String friend_id = friendList.get(i).getFriend_id();


                databaseReference.child("user_data").child(friend_id).child("transaction").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ItemForTransaction transaction = snapshot.getValue(ItemForTransaction.class);
                                if (transaction != null) {

                                    int point = transaction.getPoint();
                                    int cash = transaction.getCash();

                                    /** cash only 일때**/
                                    if (cash > 0 && point == 0) {

                                        OkHttpClient httpClient = new OkHttpClient();
                                        String receipt_id = transaction.getReceipt_id();
                                        Log.d("취소", receipt_id);
                                        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/bootpay_cancel";

                                        RequestBody formBody = new FormBody.Builder()
                                                .add("receipt_id", receipt_id)
                                                .add("user_id", user_id)
                                                .add("mission_id", mission_id)
                                                .add("title", transaction.getTitle())
                                                .add("date_time", date_time)
                                                .add("payment_method", transaction.getPayment_method())
                                                .add("amount", String.valueOf(transaction.getCash()))
                                                .add("user_name", user_name)
                                                .build();


                                        Request request = new Request.Builder()
                                                .header("Authorization", "5d25d429396fa67ca2bd0f45")
                                                .url(url)
                                                .post(formBody)
                                                .build();


                                        httpClient.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                                Log.d("취소", response.body().string() + "영수증");


                                                boolean is_manager = true;
                                                deleteFriendData(is_manager);

                                                finish();

                                            }
                                        });


                                        /** point only일 때**/
                                    } else if (cash == 0 && point > 0) {


                                        databaseReference.child("user_data").child(friend_id).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    Integer cash = dataSnapshot.getValue(Integer.class);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        /** point + cash 혼합일때 **/


                                    }
                                }


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        } else {
            /** 참가자라면 내것만 처리**/

            databaseReference.child("user_data").child(user_id).child("transaction").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ItemForTransaction transaction = snapshot.getValue(ItemForTransaction.class);
                            if (transaction != null) {


                                int point = transaction.getPoint();
                                int cash = transaction.getCash();

                                /** cash only 일때**/
                                if (cash > 0 && point == 0) {


                                    OkHttpClient httpClient = new OkHttpClient();
                                    String receipt_id = transaction.getReceipt_id();
                                    Log.d("취소", receipt_id);
                                    String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/bootpay_cancel";

                                    RequestBody formBody = new FormBody.Builder()
                                            .add("receipt_id", receipt_id)
                                            .add("user_id", user_id)
                                            .add("mission_id", mission_id)
                                            .add("title", transaction.getTitle())
                                            .add("date_time", date_time)
                                            .add("payment_method", transaction.getPayment_method())
                                            .add("amount", String.valueOf(transaction.getCash()))
                                            .add("user_name", user_name)
                                            .build();


                                    Request request = new Request.Builder()
                                            .header("Authorization", "5d25d429396fa67ca2bd0f45")
                                            .url(url)
                                            .post(formBody)
                                            .build();


                                    httpClient.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                            Log.d("취소", response.body().string() + "영수증");


                                            boolean is_manager = false;
                                            deleteFriendData(is_manager);

                                        }
                                    });


                                }
                            }


                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    /**
     * 약속 수락 가능시간을 표시하고 시간이 만료되면 결제 layout invisible
     **/
    private void displayRestTime(String register_time, String first_time) {

        Date register_date = DateTimeFormatter.dateParser(register_time, "yyyyMMddHHmmss");
        Date first_date = DateTimeFormatter.dateParser(first_time, "yyyyMMddHHmmss");
        long register_diff = System.currentTimeMillis();


        long first_date_diff = first_date.getTime();
        Log.d("타이머", register_diff + "    now");
        Log.d("타이머", first_date_diff + "    first");
        Log.d("타이머", register_time + "    now");
        Log.d("타이머", first_time + "    first");


        timer = new CountDownTimer(first_date_diff - register_diff - (1000 * 60 * 60), 1000) {//1시간 전에 만료
            @Override
            public void onTick(long l) {
                isTimerRunning = true;
                long days = TimeUnit.MILLISECONDS.toDays(l);
                long remainingHoursInMillis = l - TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                long remainingSecondsInMillis = remainingMinutesInMillis - TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingSecondsInMillis);
                //Log.d("타이머", "position" + position + "   " + holder.getAdapterPosition() + "time:" + l + " " + timer_switch);

                if (days == 0) {
                    if (l < 10 * 60 * 1000) { //10분
                        String time2 = minutes + "분 " + seconds + "초 이후 종료";
                        tv_rest_time.setText(time2);
                    } else {
                        String time = hours + "시간 " + minutes + "분 이후 종료";

                        tv_rest_time.setText(time);
                    }
                } else {
                    String time = days + "일 " + hours + "시간 " + minutes + "분 이후 종료";

                    tv_rest_time.setText(time);


                }


            }

            @Override
            public void onFinish() {
                //newQueryData(Utils.getCurrentTime(), new MissionByDayFragment());
                tv_rest_time.setText("수락가능시간이 만료되었습니다.");
                isTimerRunning = false;


            }
        }.start();


    }


}
