package com.suji.lj.myapplication.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.suji.lj.myapplication.FindPasswordActivity;
import com.suji.lj.myapplication.Items.ItemForMissionByDay;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.MissionCheckActivity;
import com.suji.lj.myapplication.OpenBankingActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SampleLoginActivity;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.SecurityActivity;
import com.suji.lj.myapplication.SelectKakaoFriendActivity;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.TransactionActivity;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FCM;
import com.suji.lj.myapplication.Utils.Utils;
import com.suji.lj.myapplication.WithDrawActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView tv_user_name;
    private ImageView profile_image_setting;
    Activity activity;
    private LinearLayout alarm_setting_go;
    private Switch auto_location_switch;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView point;
    private LinearLayout ly_withdraw;
    private LinearLayout ly_find_password;
    private LinearLayout unlink_button;
    private LinearLayout kakao_logout_button;
    LinearLayout lyPointPurchase;
    LinearLayout ly_add_new_mission;
    LinearLayout ly_reset_password;
    LinearLayout ly_password_test;
    LinearLayout lyPointTransaction;
    Toolbar toolbar;
    int paymentMethod = 0;
    boolean TERMS_AGREE_1 = false; // No Check = 0, Check = 1
    boolean TERMS_AGREE_2 = false; // No Check = 0, Check = 1

    private static final int POINT_RECHARGE = 0;

    AlertDialog loading_dialog;
    private boolean hasFractionalPart;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    BottomSheetDialog bottomSheetDialog;
    private static final String key = "5d25d429396fa67ca2bd0f45";

    String user_id;
    private final static String TAG = "SettingFragment";
    OpenBanking openBanking = OpenBanking.getInstance();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Callback account_cancel_callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            String body = response.body().toString();
            Log.d("라스트", response.body().toString());
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {

                activity.getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().clear().apply();
                database.child("user_data").child(user_id).removeValue();
                onClickUnlink();
            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        tv_user_name = view.findViewById(R.id.kakao_name_setting);
        profile_image_setting = view.findViewById(R.id.profile_image_setting);
        alarm_setting_go = view.findViewById(R.id.alarm_setting_go);
        auto_location_switch = view.findViewById(R.id.auto_location_switch);
        ly_find_password = view.findViewById(R.id.ly_find_password);
        unlink_button = view.findViewById(R.id.kakao_unlink);
        kakao_logout_button = view.findViewById(R.id.kakao_logout);
        point = view.findViewById(R.id.point);
        ly_withdraw = view.findViewById(R.id.ly_withdraw);
        ly_add_new_mission = view.findViewById(R.id.ly_add_new_mission);
        ly_reset_password = view.findViewById(R.id.ly_reset_password);
        ly_password_test = view.findViewById(R.id.ly_password_test);
        toolbar = view.findViewById(R.id.toolbar);
        lyPointTransaction = view.findViewById(R.id.lyPointTransaction);
        lyPointPurchase = view.findViewById(R.id.lyPointPurchase);


        alarm_setting_go.setOnClickListener(this);
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);
        ly_withdraw.setOnClickListener(this);
        ly_find_password.setOnClickListener(this);
        unlink_button.setOnClickListener(this);
        lyPointPurchase.setOnClickListener(this);
        kakao_logout_button.setOnClickListener(this);
        ly_reset_password.setOnClickListener(this);
        ly_password_test.setOnClickListener(this);
        lyPointTransaction.setOnClickListener(this);
        tv_user_name.setOnClickListener(this);


        toolbar.setTitle("설정");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }


        requestProfile();
        autoLocationSetting();
        return view;
    }


    private void requestProfile() {

        user_id = Account.getUserId(activity);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Kakao", MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", "");
        String profile_image = sharedPreferences.getString("thumbnail", null);
        if (!Utils.isEmpty(profile_image)) {//프로필 사진이 있을 때

            tv_user_name.setText(name);
            Picasso.with(getActivity())
                    .load(profile_image)
                    .fit()
                    .into(profile_image_setting);
        } else {//프로필 사진이 없을 때

            tv_user_name.setText(name);

        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("point").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cash = dataSnapshot.getValue(Integer.class);

                if (cash != null) {
                    String rest_point = Utils.makeNumberComma(cash) + " P";

                    point.setText(rest_point);
                } else {
                    point.setText(0 + " P");

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.d("프로필", profile_image);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kakao_name_setting:
                //nameEditBottom
                break;

            case R.id.ly_find_password:
                ly_find_password.setEnabled(false);
                startActivity(new Intent(getActivity(), FindPasswordActivity.class));
                break;

            case R.id.alarm_setting_go:
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                intent.putExtra("password", 1);
                startActivity(intent);

                break;
            case R.id.kakao_unlink:
                //onClickUnlink();
               // cancelRegister();

                Realm.init(getActivity());
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
                Realm.setDefaultConfiguration(realmConfiguration);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();

// delete all realm objects
                realm.deleteAll();

//commit realm changes
                realm.commitTransaction();



                break;
            case R.id.lyPointPurchase:
                lyPointPurchase.setEnabled(false);
                pointPurchaseDialog();
                break;

            case R.id.kakao_logout:
                kakaoLogout();


                break;

            case R.id.ly_withdraw:
                ly_withdraw.setEnabled(false);
                startActivity(new Intent(getActivity(), WithDrawActivity.class));

                // startActivity(new Intent(getActivity(), SelectKakaoFriendActivity.class));
                break;

            case R.id.ly_add_new_mission:

                startActivity(new Intent(getActivity(), SingleModeActivity.class));
                break;

            case R.id.ly_reset_password:
                Intent intent_reset = new Intent(getActivity(), SecurityActivity.class);
                intent_reset.putExtra("password", 4);
                startActivity(intent_reset);
                break;

            case R.id.ly_password_test:
                Intent test = new Intent(getActivity(), SecurityActivity.class);
                test.putExtra("password", 1);
                startActivity(test);
                break;
            case R.id.lyPointTransaction:
                lyPointTransaction.setEnabled(false);
                startActivity(new Intent(getActivity(), TransactionActivity.class));
                break;


        }
    }

    private void autoLocationSetting() {
        if (!checkLocationPermission()) {
            auto_location_switch.setChecked(false);
            SharedPreferences.Editor editor = activity.getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("auto_register_location", false);
            editor.apply();
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences("settings", MODE_PRIVATE);
        auto_location_switch.setChecked(sharedPreferences.getBoolean("auto_register_location", false));
        if (Utils.isServiceRunningInForeground(activity, NewLocationService.class)) {

            auto_location_switch.setChecked(true);
        } else {
            auto_location_switch.setChecked(false);
        }

        auto_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Utils.isLocationEnabled(activity)) {
                        if (checkLocationPermission()) {
                            Intent service = new Intent(activity, NewLocationService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(activity, service);
                            } else {
                                activity.startService(service);
                            }


                            SharedPreferences.Editor editor = activity.getSharedPreferences("settings", MODE_PRIVATE).edit();
                            editor.putBoolean("auto_register_location", isChecked);
                            editor.apply();
                        } else {
                            requestPermission();
                        }
                    } else {
                        auto_location_switch.setChecked(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("위치설정꺼짐").setMessage("위치설정을 확인해주세요");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    // The toggle is enabled
                } else {
                    Intent serviceIntent = new Intent(activity, NewLocationService.class);
                    activity.stopService(serviceIntent);
                    cancelAlarm();
                    SharedPreferences.Editor editor = activity.getSharedPreferences("settings", MODE_PRIVATE).edit();
                    editor.putBoolean("auto_register_location", isChecked);
                    editor.apply();

                    // The toggle is disabled
                }

            }
        });


    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, NewLocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("서비스", "설정전" + (PendingIntent.getForegroundService(activity, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
            PendingIntent pendingIntent = PendingIntent.getForegroundService(activity, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pendingIntent);
            Log.d("서비스", "설정후" + (PendingIntent.getForegroundService(activity, 123, intent, PendingIntent.FLAG_NO_CREATE) != null));
        }


    }

    private Boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            new AlertDialog.Builder(activity)
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
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent service = new Intent(activity, NewLocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(activity, service);
                        } else {
                            activity.startService(service);
                        }
                        Toast.makeText(activity, "위치권한이 허가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(activity, "위치권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void onClickUnlink() {

        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Log.d("라스트", "fail" + errorResult.getErrorMessage());
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
                Log.d("라스트", "에러" + errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                //redirectSignupActivity();
                Log.d("라스트", "가입안됨");
            }

            @Override
            public void onSuccess(Long userId) {

                Log.d("라스트", "카카탈퇴");
                //파이어베이스 예약삭제
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        activity.getSharedPreferences("Kakao", MODE_PRIVATE).edit().clear().apply();
                                        activity.getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().clear().apply();
                                        Toast.makeText(activity, "탈퇴 완료", Toast.LENGTH_LONG).show();

                                        activity.startActivity(new Intent(activity, SampleLoginActivity.class));
                                        activity.finish();
                                    } else {
                                        Log.d("SettingFragment", "연결끊기 실패");


                                    }
                                }
                            });
                }

            }
        });

    }


    private void cancelRegister() {

        database.child("user_data").child(user_id).child("mission_display").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ItemForMissionByDay item = dataSnapshot.getValue(ItemForMissionByDay.class);
                    String date_time = item.getDate() + item.getTime();
                    Log.d("라스트", date_time);

                    if (!DateTimeUtils.compareIsFuture(date_time, "yyyyMMddHHmm")) {
                        //미션이 현재시간이후로 없으면 바로 탈퇴
                        Log.d("라스트", "바로탈퇴");
                        AlertDialog.Builder setdialog = new AlertDialog.Builder(activity);
                        setdialog.setTitle("서비스 회원 탈퇴")
                                .setMessage("회원을 탈퇴하실 경우 등록된 회원정보와 포인트가 모두 소멸됩니다. 탈퇴하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        database.child("account").orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                                        shot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isComplete()) {
                                                                    onClickUnlink();
                                                                }
                                                            }
                                                        });


                                                    }


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

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
                        AlertDialog.Builder setdialog = new AlertDialog.Builder(activity);
                        setdialog.setTitle("서비스 회원 탈퇴")
                                .setMessage("현재 진행중인 약속이 끝나면 회원탈퇴가 가능합니다.").create().show();

                        //모든 미션이 완료됐을때 탈퇴
                        //openBanking.requestAccountClose(getApplicationContext(), account_cancel_callback);

                    }
                } else {
                    Log.d("라스트", "아무것도 없어도 탈퇴");
                    AlertDialog.Builder setdialog = new AlertDialog.Builder(activity);
                    setdialog.setTitle("서비스 회원 탈퇴")
                            .setMessage("회원을 탈퇴하실 경우 등록된 회원정보와 포인트가 모두 소멸됩니다. 탈퇴하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("라스트", user_id + "");
                                    //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    database.child("account").orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot shot : snapshot.getChildren()) {
                                                    Log.d("라스트", shot.getKey());
                                                    shot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isComplete()) {
                                                                onClickUnlink();
                                                            }
                                                        }
                                                    });


                                                }


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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
        Toast.makeText(activity, "로그아웃 완료", Toast.LENGTH_LONG).show();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {


                Intent serviceIntent = new Intent(activity, NewLocationService.class);
                activity.stopService(serviceIntent);

                startActivity(new Intent(activity, SampleLoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }


        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


    private void pointPurchaseDialog() {


        bottomSheetDialog = new BottomSheetDialog(activity, R.style.DialogStyle);
        View view = View.inflate(activity, R.layout.dialog_point_purchase, null);
        EditText etPoint = view.findViewById(R.id.etPoint);
        RelativeLayout rlPurchase = view.findViewById(R.id.rlPurchase);
        TextView tvKakaoPay = view.findViewById(R.id.tvKakaoPay);
        TextView tvNaverPay = view.findViewById(R.id.tvNaverPay);
        TextView tvCreditPay = view.findViewById(R.id.tvCreditPay);
        TextView tvActualPayment = view.findViewById(R.id.tvActualPayment);
        TextView tvAmountWarning = view.findViewById(R.id.tvAmountWarning);
        AppCompatCheckBox cbAgreeTermUse = view.findViewById(R.id.cbAgreeTermUse);
        AppCompatCheckBox cbAgreeTermPrivate = view.findViewById(R.id.cbAgreeTermPrivate);
        int amount = 0;
        paymentMethod = 0;

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                lyPointPurchase.setEnabled(true);
            }
        });

        cbAgreeTermUse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    TERMS_AGREE_1 = true;
                } else {

                    TERMS_AGREE_1 = false;
                }

            }
        });
        cbAgreeTermPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    TERMS_AGREE_2 = true;
                } else {
                    TERMS_AGREE_2 = false;
                }
            }
        });


        tvKakaoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvKakaoPay.setBackground(activity.getResources().getDrawable(R.drawable.rounded_rectangle));
                tvKakaoPay.setTextColor(activity.getResources().getColor(R.color.White));
                tvNaverPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvNaverPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                tvCreditPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvCreditPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                paymentMethod = 1;
            }
        });
        tvNaverPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNaverPay.setBackground(activity.getResources().getDrawable(R.drawable.rounded_rectangle));
                tvNaverPay.setTextColor(activity.getResources().getColor(R.color.White));
                tvKakaoPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvKakaoPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                tvCreditPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvCreditPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                paymentMethod = 2;
            }
        });

        tvCreditPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCreditPay.setBackground(activity.getResources().getDrawable(R.drawable.rounded_rectangle));
                tvCreditPay.setTextColor(activity.getResources().getColor(R.color.White));
                tvKakaoPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvKakaoPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                tvNaverPay.setBackground(activity.getResources().getDrawable(R.drawable.rectangle_stroke));
                tvNaverPay.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                paymentMethod = 3;

            }
        });

        etPoint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());

                    double point = Integer.valueOf(etPoint.getText().toString().replaceAll(",", ""));
                    etPoint.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (point > 100000) {
                        etPoint.setText("100,000");
                        tvActualPayment.setText(Utils.makeNumberComma(100000));


                    } else if (point < 1000) {
                        etPoint.setText("1,000");
                        tvActualPayment.setText(Utils.makeNumberComma(1000));
                    } else {

                        tvActualPayment.setText(Utils.makeNumberComma(point));

                    }


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });


        rlPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isValid = true;


                if (etPoint.getText().toString().isEmpty()) {


                    isValid = false;
                    Toast.makeText(activity, "충전금액을 입력해주세요", Toast.LENGTH_LONG).show();


                }
                if (paymentMethod == 0) {

                    Toast.makeText(activity, "결제수단을 선택해주세요", Toast.LENGTH_LONG).show();
                    isValid = false;

                }
                if (!TERMS_AGREE_1 || !TERMS_AGREE_2) {

                    Toast.makeText(activity, "약관동의를 확인해주세요", Toast.LENGTH_LONG).show();
                    isValid = false;
                }


                if (isValid) {
                    loading_dialog.show();


                    BootpayAnalytics.init(activity, key);

                    int point = Integer.valueOf(etPoint.getText().toString().replaceAll(",", ""));

                    switch(paymentMethod){
                        case 1:
                            kakaoPayRequest(point);
                            break;

                        case 2:
                            break;
                        case 3:
                            creditCardRequest(point);
                            break;

                    }



                }


            }
        });
        etPoint.addTextChangedListener(new TextWatcher() {
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
                etPoint.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = etPoint.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = etPoint.getSelectionStart();
                    if (hasFractionalPart) {
                        etPoint.setText(df.format(n));
                    } else {
                        etPoint.setText(dfnd.format(n));
                    }
                    endlen = etPoint.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= etPoint.getText().length()) {
                        etPoint.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        etPoint.setSelection(etPoint.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }

                etPoint.addTextChangedListener(this);

                if (s.toString().equals("")) {

                    tvAmountWarning.setText("*최소금액 1,000원");
                    tvAmountWarning.setVisibility(View.VISIBLE);
                    //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                } else {
                    int amount = Integer.valueOf(s.toString().replaceAll(",", ""));
                    if (amount >= 100000) {
                        tvAmountWarning.setText("최대금액 100,000원");
                        tvAmountWarning.setVisibility(View.VISIBLE);
                    } else {

                        if (amount < 1000) {
                            tvAmountWarning.setText("*최소금액 1,000원");
                            tvAmountWarning.setVisibility(View.VISIBLE);
                            //if_fail.setError("최소금액은 100원입니다.",context.getResources().getDrawable(R.drawable.ic_error));
                        } else {
                            tvAmountWarning.setVisibility(View.INVISIBLE);

                        }
                    }
                }


            }
        });


        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();


    }

    public void kakaoPayRequest(int actual_payment) {
        // 결제호출
        //int stuck = 10;
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});


        Bootpay.init(activity)
                .setApplicationId(key) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KAKAO) // 결제할 PG 사
                .setMethod(Method.EASY) // 결제수단
                .setContext(activity)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setTaxFree((double)actual_payment)
                .setUX(UX.PG_DIALOG)
//              .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("포인트 충전") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(actual_payment) // 결제할 금액
                .addItem("포인트충전", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
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
                        String msg = Utils.getValueFromJson(message, "message");
                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        loading_dialog.dismiss();
                        Log.d("부트", "결제취소" + message);
                        String msg = Utils.getValueFromJson(message, "message");
                        Toast.makeText(activity, "결제가 취소되었습니다", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("부트", "에러" + message);
                        //finish();
                        loading_dialog.dismiss();
                        String msg = Utils.getValueFromJson(message, "message");
                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .onClose(new CloseListener() { //결제창이 닫힐때 실행되는 부분
                    @Override
                    public void onClose(String message) {
                        loading_dialog.dismiss();
                        Log.d("부트", "닫힘" + message);
                        //Toast.makeText(activity, "결제가 취소되었습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .request();
    }


    public void creditCardRequest(int actual_payment) {
        // 결제호출
        //int stuck = 10;


        String key = "5d25d429396fa67ca2bd0f45";
        BootpayAnalytics.init(activity, key);


        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        Bootpay.init(activity)
                .setApplicationId(key) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.PAYAPP) // 결제할 PG 사
                .setMethod(Method.CARD) // 결제수단
                .setContext(activity)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("포인트 충전") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(actual_payment) // 결제할 금액
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        Bootpay.confirm(message); // 재고가 있을 경우.
                       // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                        function(message);
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


    private void function(String message) {//영수증 검증
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/bootpay";


        RequestBody formBody = new FormBody.Builder()
                .add("receiptId", Utils.getValueFromJson(message, "receipt_id"))
                .add("price", Utils.getValueFromJson(message, "price"))
                .add("userId", user_id)
                .add("message", message)
                .add("code", String.valueOf(POINT_RECHARGE))
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("부트페이", "실패");
                loading_dialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = "";
                if (!response.isSuccessful()) {
                    Log.d("부트페이", "fail response from firebase cloud function");
                } else {

                    loading_dialog.dismiss();
                    if (responseBody != null) {
                        Log.d("부트페이", responseBody.string());
                        //String receipt = responseBody.string();
                        bottomSheetDialog.dismiss();

                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ly_withdraw.setEnabled(true);
        lyPointTransaction.setEnabled(true);
        ly_find_password.setEnabled(true);
        ly_reset_password.setEnabled(true);
        if (loading_dialog.isShowing()) {

            loading_dialog.dismiss();


        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("리줌","onhidden");
        if (hidden) {
            //do when hidden
        } else {
           // auto_location_switch.setChecked(sharedPreferences.getBoolean("auto_register_location", false));
            if (Utils.isServiceRunningInForeground(activity, NewLocationService.class)) {

                auto_location_switch.setChecked(true);
            } else {
                auto_location_switch.setChecked(false);
            }
            //do when show
        }
    }


}
