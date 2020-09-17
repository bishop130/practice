package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.NewLocationService;
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
import java.util.Calendar;
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
    boolean pay_method_select = false;
    int pay_method = 0;
    String user_id;

    ItemForMultiModeRequest item;
    TextView text_rest_point;
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

    boolean isTimerRunning = false;
    CountDownTimer timer;
    String missionId;
    List<ItemForFriendResponseForRequest> friendList = new ArrayList<>();
    AlertDialog loading_dialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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


        text_rest_point = findViewById(R.id.tv_rest_point);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(InvitationInfoActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

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


        String missionId = getIntent().getExtras().getString("missionId", "");

        Log.d("리퀘스트", missionId + "  미션");

        databaseReference.child("invitation_data").orderByChild("missionId").equalTo(missionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //item = new ItemForMultiModeRequest();
                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("리퀘스트", snapshot.getKey() + "키");
                        ItemForMultiModeRequest item = snapshot.getValue(ItemForMultiModeRequest.class);
                        displayData(item);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void displayData(ItemForMultiModeRequest item) {
        if (item != null) {


            if (item.getManagerId().equals(user_id)) {
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
            String register_date_time = item.getRegisteredTime();
            displayRestTime(register_date_time, first_date_time);
            friendList = item.getFriendRequestList();
            setupRecyclerView(friendList);
            Log.d("제목", item.getTitle());

            getSupportActionBar().setTitle(item.getTitle());
            tv_address.setText(item.getAddress());
            missionId = item.getMissionId();
            //tv_penalty.setText(Utils.makeNumberComma(item.getPenaltyTotal()) + " 원");
            total = item.getPenaltyTotal() * item.getCalendarDayList().size();
            actual_payment = total - point_input;
            text_actual_payment.setText(Utils.makeNumberComma(total));

            addCalendarFragment(new CalendarShowFragment(getApplicationContext(), item.getCalendarDayList()));
            addFragment(new MapFragment(scrollView, item.getLat(), item.getLng()));

            setUpRecyclerViewPortion(item.getPortionList());


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


    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        switch (v.getId()) {
            case R.id.tv_accept:

                builder.setMessage("약속에 참여하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        joinMission();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
            builder.create().show();


                break;

            case R.id.tv_activate:
                /** 방장이 강제시작**/

                databaseReference.child("invitation_data").orderByChild("missionId").equalTo(missionId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                if (count == 1) {/** 2명이상 수락했으면 약속 시**/
                                    AlertDialog.Builder builder = new AlertDialog.Builder(InvitationInfoActivity.this);
                                    builder.setMessage("약속을 시작하시겠습니까?");
                                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //deleteFriendData();

                                            loading_dialog.show();
                                            activateMission();
                                        }
                                    });
                                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });
                                    builder.create().show();


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

                        cancelMission();

                        //deleteFriendData();
                        //requestPaymentCancel(user_id);
                    }
                });
                cancel_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                cancel_builder.create().show();


                break;
            case R.id.tv_join_cancel:
                /** 승락한이후 다시 취소요청을 할 경우 포인트 환불 **/
                //결제취소
                AlertDialog.Builder join_cancel_builder = new AlertDialog.Builder(this);
                join_cancel_builder.setMessage("약속을 취소하시겠습니까?");
                join_cancel_builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        joinCancelMission();
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

    private void joinMission() {

        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/join_mission";


        RequestBody formBody = new FormBody.Builder()
                .add("missionId", missionId)
                .add("userId",user_id)
                .add("refundPoint",String.valueOf(item.getPenaltyTotal()))
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("초대", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("초대", response.body().string());

                    loading_dialog.dismiss();
                    finish();


                }

            }
        });

    }

    private void joinCancelMission(){
        /** 포인트 환불**/
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/cancel_join";


        RequestBody formBody = new FormBody.Builder()
                .add("missionId", missionId)
                .add("userId",user_id)
                .add("refundPoint",String.valueOf(item.getPenaltyTotal()))
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("초대", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("초대", response.body().string());

                    finish();


                }

            }
        });


    }

    /**
     * 제안한 사람이 미션을 취소할 경우 본인의 데이터 삭제
     **/

    private void cancelMission() {
        loading_dialog.show();

        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/delete_mission";


        RequestBody formBody = new FormBody.Builder()
                .add("missionId", missionId)
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("초대", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("초대", response.body().string());
                    loading_dialog.dismiss();

                    Intent service = new Intent(InvitationInfoActivity.this, NewLocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(InvitationInfoActivity.this, service);
                    } else {
                        startService(service);
                    }

                    finish();


                }

            }
        });


    }

    private void cancelJoin() {

        /** 일반 참여자라면 내것만 삭제**/
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/cancel_join";


        RequestBody formBody = new FormBody.Builder()
                .add("missionId", missionId)
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("초대", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("초대", response.body().string());

                    finish();


                }

            }
        });




    }

    private void activateMission() {

        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/activate_multi";


        RequestBody formBody = new FormBody.Builder()
                .add("missionId", missionId)
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("초대", "방장수락"+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("초대", response.body().string());

                    Intent service = new Intent(InvitationInfoActivity.this, NewLocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(InvitationInfoActivity.this, service);
                    } else {
                        startService(service);
                    }

                    finish();

                }else{
                    Log.d("초대", "실패");
                    finish();
                }
            }
        });
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
