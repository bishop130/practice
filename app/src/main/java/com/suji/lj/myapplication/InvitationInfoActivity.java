package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationDateTimeAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationFriendAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerPortionInvitationAdapter;
import com.suji.lj.myapplication.Fragments.CalendarSelectFragment;
import com.suji.lj.myapplication.Fragments.CalendarShowFragment;
import com.suji.lj.myapplication.Fragments.MapFragment;
import com.suji.lj.myapplication.Fragments.MapMissionFragment;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.ItemPortion;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

public class InvitationInfoActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Toolbar toolbar;

    NestedScrollView scrollView;
    RecyclerView recycler_portion;
    LinearLayout ly_mission_accept;
    TextView tv_address;
    TextView tv_fail_penalty;
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


    int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_info);
        recyclerView = findViewById(R.id.recycler_friend);
        recycler_portion = findViewById(R.id.recycler_distribution);
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scroll_view);
        ly_mission_accept = findViewById(R.id.accept);
        tv_address = findViewById(R.id.address);
        tv_fail_penalty = findViewById(R.id.penalty);
        kakao_pay = findViewById(R.id.kakao_pay);
        naver_pay = findViewById(R.id.naver_pay);
        credit_pay = findViewById(R.id.credit_pay);
        text_rest_point = findViewById(R.id.rest_point);
        edit_point = findViewById(R.id.edit_point);
        text_total_payment = findViewById(R.id.total_payment);
        text_point_total = findViewById(R.id.point_total);
        text_actual_payment = findViewById(R.id.actual_payment);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user_id = Account.getUserId(this);
        //Bundle extras = getIntent().getExtras();


        item = getIntent().getParcelableExtra("item");
        if (item != null) {
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


            List<ItemForFriendResponseForRequest> list = item.getFriendRequestList();
            setupRecyclerView(list);
            Log.d("제목", item.getTitle());
            Log.d("제목", item.getFail_penalty() + "");
            toolbar.setTitle(item.getTitle());
            tv_address.setText(item.getAddress());
            mission_id = item.getMission_key();
            tv_fail_penalty.setText(Utils.makeNumberComma(item.getFail_penalty()) + " 원");
            total = item.getFail_penalty() * item.getCalendarDayList().size();
            actual_payment = total - point_input;
            text_actual_payment.setText(Utils.makeNumberComma(total));
            text_total_payment.setText(Utils.makeNumberComma(total));
            mHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    addCalendarFragment(new CalendarShowFragment(getApplicationContext(), item.getCalendarDayList()));
                    addFragment(new MapFragment(scrollView, item.getLat(), item.getLng()));


                }
            };
            mHandler.post(runnable);

            setUpRecyclerViewPortion(item.getItemPortionList());


            ly_mission_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //databaseReference.child("")
                    bootPayRequest();


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

                        Log.d("부트페이", "성공" + user_id);
                        //Log.d("부트페이", "성공" + Utils.getValueFromJson(responseBody.string(), "date_time"));
                        //displayReceipt(result);


                        dataSave();
                        Intent intent = new Intent(getApplicationContext(), MissionCheckActivity.class);
                        intent.putExtra("receipt", receipt);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    private void dataSave() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String mission_id = item.getMission_id();


        for (int i = 0; i < item.getFriendRequestList().size(); i++) {
            String friend_id = item.getFriendRequestList().get(i).getFriend_id();
            if (!user_id.equals(friend_id)) {//나를 제외한 친구들
                databaseReference.child("user_data").child(friend_id).child("invitation").orderByChild("mission_id").equalTo(mission_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);

                            String key = snapshot.getKey();

                            Log.d("수락", key + " key");


                            if (item != null && key != null) {
                                Log.d("수락", item.getAddress() + " address");
                                List<ItemForFriendResponseForRequest> friendsLists = item.getFriendRequestList();
                                for (int i = 0; i < friendsLists.size(); i++) {
                                    if (user_id.equals(friendsLists.get(i).getFriend_id())) {
                                        ItemForFriendResponseForRequest request = friendsLists.get(i);
                                        request.setAccept(false);
                                        friendsLists.set(i, request);
                                        item.setFriendRequestList(friendsLists);
                                        databaseReference.child("user_data").child(friend_id).child("invitation").child(key).setValue(item);

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
