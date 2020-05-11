package com.suji.lj.myapplication.Fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.suji.lj.myapplication.AccountActivity;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.FAQActivity;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.MissionCheckActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SampleLoginActivity;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

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
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView kakao_name_setting;
    private ImageView profile_image_setting;
    private Context mContext;
    private LinearLayout alarm_setting_go;
    private LinearLayout go_to_my_profile;
    private LinearLayout faq;
    private LinearLayout manage_account;
    private Switch auto_location_switch;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView point;
    String user_id;
    OpenBanking openBanking = OpenBanking.getInstance();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Callback account_cancel_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().toString();
            Log.d("라스트", response.body().toString());
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {

                mContext.getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().clear().apply();
                database.child("user_data").child(user_id).removeValue();
                onClickUnlink();
            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        kakao_name_setting = view.findViewById(R.id.kakao_name_setting);
        profile_image_setting = view.findViewById(R.id.profile_image_setting);
        alarm_setting_go = view.findViewById(R.id.alarm_setting_go);
        go_to_my_profile = view.findViewById(R.id.go_to_my_profile);
        faq = view.findViewById(R.id.faq);
        manage_account = view.findViewById(R.id.manage_account);
        auto_location_switch = view.findViewById(R.id.auto_location_switch);
        LinearLayout unlink_button = view.findViewById(R.id.kakao_unlink);
        LinearLayout kakao_logout_button = view.findViewById(R.id.kakao_logout);
        point = view.findViewById(R.id.point);


        go_to_my_profile.setOnClickListener(this);
        alarm_setting_go.setOnClickListener(this);
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);
        manage_account.setOnClickListener(this);
        unlink_button.setOnClickListener(this);
        kakao_logout_button.setOnClickListener(this);


        requestProfile();
        autoLocationSetting();
        return view;
    }


    private void requestProfile() {

        user_id = Account.getUserId(mContext);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Kakao", MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", "");
        String profile_image = sharedPreferences.getString("thumbnail", null);
        if (!Utils.isEmpty(profile_image)) {

            kakao_name_setting.setText(name);
            Picasso.with(getActivity())
                    .load(profile_image)
                    .fit()
                    .into(profile_image_setting);
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("point").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long cash =dataSnapshot.getValue(Long.class);

                if (cash != null) {

                    point.setText(cash+"");
                    Log.d("포인트", "12   " + point);
                }else{
                    point.setText(0+"");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.d("프로필", profile_image);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faq:
                startActivity(new Intent(getActivity(), FAQActivity.class));
                break;
            case R.id.manage_account:
                startActivity(new Intent(getActivity(), AccountActivity.class));
                break;
            case R.id.alarm_setting_go:
                //onClick_request();
                startActivity(new Intent(getActivity(), MissionCheckActivity.class));
                break;
            case R.id.kakao_unlink:
                cancelRegister();
                break;
            case R.id.kakao_logout:
                kakaoLogout();
                break;


        }
    }

    private void autoLocationSetting() {
        if (!checkLocationPermission()) {
            auto_location_switch.setChecked(false);
            SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("auto_register_location", false);
            editor.apply();
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("settings", MODE_PRIVATE);
        auto_location_switch.setChecked(sharedPreferences.getBoolean("auto_register_location", false));
        if (Utils.isServiceRunningInForeground(mContext, NewLocationService.class)) {

            auto_location_switch.setChecked(true);
        } else {
            auto_location_switch.setChecked(false);
        }


        auto_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Utils.isLocationEnabled(mContext)) {
                        if (checkLocationPermission()) {
                            Intent service = new Intent(mContext, NewLocationService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(mContext, service);
                            } else {
                                mContext.startService(service);
                            }


                            SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
                            editor.putBoolean("auto_register_location", isChecked);
                            editor.apply();
                        } else {
                            requestPermission();
                        }
                    } else {
                        auto_location_switch.setChecked(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("위치설정꺼짐").setMessage("위치설정을 확인해주세요");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    // The toggle is enabled
                } else {
                    Intent serviceIntent = new Intent(mContext, NewLocationService.class);
                    mContext.stopService(serviceIntent);
                    cancelAlarm();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_register_location", isChecked);
                    editor.apply();

                    // The toggle is disabled
                }

            }
        });


    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, NewLocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("서비스", "설정전" + (PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
            PendingIntent pendingIntent = PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pendingIntent);
            Log.d("서비스", "설정후" + (PendingIntent.getForegroundService(mContext, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
        }


    }

    private Boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");
            return true;
        } else {
            // Should we show an explanation?
            return false;
        }
    }

    private void requestPermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(mContext)
                    .setTitle("위치권한이 필요합니다.")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent service = new Intent(mContext, NewLocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(mContext, service);
                        } else {
                            mContext.startService(service);
                        }
                        Toast.makeText(mContext, "위치권한이 허가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(mContext, "위치권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(mContext)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {


                                        //파이어베이스 예약삭제
                                        mContext.getSharedPreferences("Kakao", MODE_PRIVATE).edit().clear().apply();
                                        mContext.getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().clear().apply();
                                        Toast.makeText(mContext, "탈퇴 완료", Toast.LENGTH_LONG).show();

                                        mContext.startActivity(new Intent(mContext, SampleLoginActivity.class));
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).show();

    }

    private void cancelRegister() {

        database.child("user_data").child(user_id).child("mission_display").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ItemForMissionByDay item = dataSnapshot.getValue(ItemForMissionByDay.class);
                    String date_time = item.getDate() + item.getTime();
                    Log.d("라스트", date_time);

                    if (!DateTimeUtils.compareIsFuture(date_time)) {
                        //미션이 현재시간이후로 없으면 바로 탈퇴
                        Log.d("라스트", "바로탈퇴");
                        AlertDialog.Builder setdialog = new AlertDialog.Builder(mContext);
                        setdialog.setTitle("서비스 회원 탈퇴")
                                .setMessage("회원을 탈퇴하실 경우 등록된 데이터가 삭제되고 계좌연결이 해제됩니다. 탈퇴하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        database.child("user_data").child(user_id).child("open_banking").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    openBanking.requestAccountClose(mContext, account_cancel_callback);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        Log.d("라스트", "탈퇴못해");
                        AlertDialog.Builder setdialog = new AlertDialog.Builder(mContext);
                        setdialog.setTitle("서비스 회원 탈퇴")
                                .setMessage("현 진행중인 약속이 끝나면 회원탈퇴가 가능합니다.").create().show();

                        //모든 미션이 완료됐을때 탈퇴
                        //openBanking.requestAccountClose(getApplicationContext(), account_cancel_callback);

                    }
                } else {
                    Log.d("라스트", "아무것도 없어도 탈퇴");
                    AlertDialog.Builder setdialog = new AlertDialog.Builder(mContext);
                    setdialog.setTitle("서비스 회원 탈퇴")
                            .setMessage("회원을 탈퇴하실 경우 등록된 데이터가 삭제되고 계좌연결이 해제됩니다. 탈퇴하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    database.child("user_data").child(user_id).child("open_banking").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                openBanking.requestAccountClose(mContext, account_cancel_callback);
                                                //database.child("user_data").child(user_id).removeValue();
                                            } else {
                                                onClickUnlink();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void kakaoLogout() {
        Toast.makeText(mContext, "로그아웃 완료", Toast.LENGTH_LONG).show();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent serviceIntent = new Intent(mContext, NewLocationService.class);
                mContext.stopService(serviceIntent);

                startActivity(new Intent(mContext, SampleLoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }


        });
    }

    public void onClick_request() {
        // 결제호출
        int stuck = 10;
        String key = "5d25d429396fa67ca2bd0f45";
        BootpayAnalytics.init(getActivity(), key);
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});

        Bootpay.init(getContext())
                .setApplicationId(key) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KAKAO) // 결제할 PG 사
                .setMethod(Method.EASY) // 결제수단
                .setContext(getActivity())
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//              .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("맥북프로's 임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(10000) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();
    }
}
