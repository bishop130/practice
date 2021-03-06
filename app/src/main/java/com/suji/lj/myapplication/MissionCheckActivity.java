package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemPoint;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FirebaseDB;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
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

public class MissionCheckActivity extends AppCompatActivity {

    TextView confirm;
    TextView pay_method;
    TextView total;
    TextView actual_pay;
    TextView point_use;
    TextView date_time;

    static final int SINGLE_MODE = 0;
    static final int MULTI_MODE = 1;
    static final int KAKAO_PAY = 0;
    static final int NAVER_PAY = 1;


    Realm realm;
    //DatabaseReference mRootRef;
    String user_id;
    String mission_id;
    MissionCartItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_check);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        //mFunctions = FirebaseFunctions.getInstance();
        confirm = findViewById(R.id.confirm);
        pay_method = findViewById(R.id.pay_method);
        total = findViewById(R.id.total_pay);
        actual_pay = findViewById(R.id.actual_pay);
        point_use = findViewById(R.id.point);
        date_time = findViewById(R.id.date_time);

        confirm.setClickable(false);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String receipt = bundle.getString("receipt");
            String method = bundle.getString("method");
            deleteAllTemporaryData();
            displayReceipt(receipt);
            activateService();
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


    }


    private void sendPointToServiceAccount(String message) {
        ItemPoint itemPoint = new ItemPoint();
        itemPoint.setUser_id(user_id);
        itemPoint.setDate_time(Utils.getCurrentTime());
        itemPoint.setMission_id(mission_id);
        itemPoint.setReceipt_id(Utils.getValueFromJson(message, "receipt_id"));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("common").child("service_account").push().setValue(itemPoint);


    }

    private void displayReceipt(String result) {


        String method = Utils.getValueFromJson(result, "payment_method");
        pay_method.setText(method);
        String price = Utils.getValueFromJson(result, "cash");
        actual_pay.setText(Utils.makeNumberComma(Integer.valueOf(price)) + " 원");
        String purchased_at = Utils.getValueFromJson(result, "date_time");
        date_time.setText(purchased_at);
        String point = Utils.getValueFromJson(result, "point");
        point_use.setText(Utils.makeNumberComma(Integer.valueOf(point)) + " P");
        int total_pay = Integer.valueOf(price) + Integer.valueOf(point);
        total.setText(Utils.makeNumberComma(total_pay) + " 원");


    }


    private void activateService() {
        Intent service = new Intent(getApplicationContext(), NewLocationService.class);
        boolean is_running = Utils.isServiceRunningInForeground(getApplicationContext(), NewLocationService.class);
        if (is_running) {
            stopService(service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(getApplicationContext(), service);

            } else {
                startService(service);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(getApplicationContext(), service);

            } else {
                startService(service);
            }
        }


    }

    public void deleteAllTemporaryData() {
        Log.d("어댑터", "deletedata");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //RealmResults<ItemForDateTime> itemForDateTimes = realm.where(ItemForDateTime.class).findAll();


                MissionCartItem missionCartItem = realm.where(MissionCartItem.class).findFirst();
                if (missionCartItem != null) {
                    Log.d("어댑터", "널이 아닙니다");
                    SharedPreferences.Editor editor = getSharedPreferences("location_setting", MODE_PRIVATE).edit();
                    editor.putLong("lat", Double.doubleToRawLongBits(missionCartItem.getLat()));
                    editor.putLong("lng", Double.doubleToRawLongBits(missionCartItem.getLng()));
                    editor.apply();
                    editor.commit();
                    missionCartItem.deleteFromRealm();
                    RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
                    RealmResults<ItemForFriends> friendsRealmResults = realm.where(ItemForFriends.class).findAll();
                    if (realmResults.size() != 0) {
                        realmResults.deleteAllFromRealm();
                    }
                    if (friendsRealmResults.size() != 0) {
                        realmResults.deleteAllFromRealm();


                    }

                    /** 포어그라운드 서비스 활성화 **/
                }
                confirm.setClickable(true);


            }

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //


    //싱글
    //현금+포인트

//결제 -> 영수증 검증 -> 포인트 환산 -> 서비스계좌로 +포인트 -> 유저결제입출내역기록 ->
// 센터포인트입출내역기록-> 사용한 포인트 차감 -> 디비저장 -> 끝


    //현금
    //결제 -> 영수증 검증 -> 서비스계좌로 + -> 유저결제입출내역기록 ->센터포인트입출내역기록 -> 디비저장 -> 끝


    // 포인트
    //결제 ->서비스계좌 + -> 유저결제입출내역기록 ->센터포인트입출내역기록 -> 포인트차감 -> 디비저장 -> 끝


    //싱글 멀티
    //3가지 경우의 수 현금100, 현금+포인트, 포인트100

}
