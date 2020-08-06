package com.suji.lj.myapplication;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.util.helper.Utility;
import com.suji.lj.myapplication.Fragments.GiftFragment;
import com.suji.lj.myapplication.Fragments.NotificationFragment;
import com.suji.lj.myapplication.Fragments.SearchFragment;
import com.suji.lj.myapplication.Fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suji.lj.myapplication.Fragments.MissionFragment;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;
import com.suji.lj.myapplication.Items.ItemForFriendByDay;
import com.suji.lj.myapplication.Items.ItemForMultiKey;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.BackPressHandler;
import com.suji.lj.myapplication.Utils.BadgeManager;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, // 카메라
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS};
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private Fragment fa, fb, fc, fd, fe;
    private FragmentManager fragmentManager;
    private BackPressHandler backPressHandler = new BackPressHandler(this);
    String TAG = "메인";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;
    public BottomNavigationView bottomNavigationView;
    AlertDialog loading_dialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Session.getCurrentSession().checkAndImplicitOpen();


        if (Session.getCurrentSession().isClosed()) {
            Log.d("카카오", Session.getCurrentSession().isOpened() + "세션오픈");
            Intent intent = new Intent(getApplicationContext(), SampleLoginActivity.class);
            startActivity(intent);
            finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        user_id = Account.getUserId(this);
        Realm.init(this);
        // requestProfile();
        checkPermission();


// ...
// Initialize Firebase Auth

        //clearPassedMission();


        fragmentManager = getSupportFragmentManager();
/*
        byte[] sha1 = {
                0x58, 0x64, 0x4B, (byte)0xF6, (byte)0x86, 0x44, (byte)0xE5, 0x27, (byte)0xF0, (byte)0xBB, (byte)0xEF, 0x12, 0x4C, (byte)0x9A,(byte)0xDA, (byte)0xA3,         (byte)0xB5, (byte)0xB5, (byte)0x84, (byte)0xBB

        };

        Log.d("둠피 ","" + Base64.encodeToString(sha1, Base64.NO_WRAP));
*/

        //requestAccessTokenInfo();


        /*
        String mission_id = "20200721231316U1285672933";
        String dateTime = "202007222310";
        String date = "20200722";
        String time_stamp = DateTimeUtils.getCurrentTime();


         */


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
            Log.d("절전", isWhiteListing + "");
        }
        if (!isWhiteListing) {
            AlertDialog.Builder setdialog = new AlertDialog.Builder(MainActivity.this);
            setdialog.setTitle("추가 설정이 필요합니다.")
                    .setMessage("어플을 문제없이 사용하기 위해서는 해당 어플을 \"배터리 사용량 최적화\" 목록에서 \"제외\"해야 합니다. 설정화면으로 이동하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "설정을 취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();

        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        fa = new MissionFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fa).commit();


        displayBadgeCount();


    }

    private void displayBadgeCount() {
        databaseReference.child("user_data").child(user_id).child("notification").orderByChild("read").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int badge_count = (int) dataSnapshot.getChildrenCount();
                    Log.d("뱃지", badge_count + "");
                    BadgeManager.showBottomNavigationViewBadge(MainActivity.this, bottomNavigationView, 2, badge_count);


                } else {


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_favorite:

                    if (fa == null) {
                        fa = new MissionFragment();
                        fragmentManager.beginTransaction().add(R.id.fragment_container, fa).commit();
                    }

                    if (fa != null) fragmentManager.beginTransaction().show(fa).commit();
                    if (fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                    if (fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                    if (fd != null) fragmentManager.beginTransaction().hide(fd).commit();
                    if (fe != null) fragmentManager.beginTransaction().hide(fe).commit();

                    break;
                case R.id.nav_search:

                    if (fb == null) {
                        fb = new SearchFragment();
                        fragmentManager.beginTransaction().add(R.id.fragment_container, fb).commit();
                    }

                    if (fa != null) fragmentManager.beginTransaction().hide(fa).commit();
                    if (fb != null) fragmentManager.beginTransaction().show(fb).commit();
                    if (fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                    if (fd != null) fragmentManager.beginTransaction().hide(fd).commit();
                    if (fe != null) fragmentManager.beginTransaction().hide(fe).commit();
                    //Realm.init(getApplicationContext());
                    //Realm.setDefaultConfiguration(getRealmConfig());


                    break;
                case R.id.nav_setting:

                    if (fc == null) {
                        fc = new SettingFragment();
                        fragmentManager.beginTransaction().add(R.id.fragment_container, fc).commit();
                    }

                    if (fa != null) fragmentManager.beginTransaction().hide(fa).commit();
                    if (fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                    if (fc != null) fragmentManager.beginTransaction().show(fc).commit();
                    if (fd != null) fragmentManager.beginTransaction().hide(fd).commit();
                    if (fe != null) fragmentManager.beginTransaction().hide(fe).commit();
                    break;


                case R.id.nav_notification:
                    if (fd == null) {
                        fd = new NotificationFragment();
                        fragmentManager.beginTransaction().add(R.id.fragment_container, fd).commit();
                    }

                    if (fa != null)
                        fragmentManager.beginTransaction().hide(fa).commit();
                    if (fb != null)
                        fragmentManager.beginTransaction().hide(fb).commit();
                    if (fc != null)
                        fragmentManager.beginTransaction().hide(fc).commit();
                    if (fd != null)
                        fragmentManager.beginTransaction().show(fd).commit();
                    if (fe != null)
                        fragmentManager.beginTransaction().hide(fe).commit();

                    break;
                case R.id.nav_gift:

                    if (fe == null) {
                        fe = new GiftFragment();
                        fragmentManager.beginTransaction().add(R.id.fragment_container, fe).commit();
                    }
                    if (fa != null)
                        fragmentManager.beginTransaction().hide(fa).commit();
                    if (fb != null)
                        fragmentManager.beginTransaction().hide(fb).commit();
                    if (fc != null)
                        fragmentManager.beginTransaction().hide(fc).commit();
                    if (fd != null)
                        fragmentManager.beginTransaction().hide(fd).commit();
                    if (fe != null)
                        fragmentManager.beginTransaction().show(fe).commit();
                    break;


            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_option, menu);
        return true;
    }

    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.notification_icon:


                Realm.init(this);
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

                //FirebaseDatabase.getInstance().getReference().child("common").child("service_account").removeValue();


                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void checkPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        int batteryPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

        if ((locationPermission == PackageManager.PERMISSION_GRANTED) && (batteryPermission == PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(getApplicationContext(), "권한승인", Toast.LENGTH_LONG).show();


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한설정");
                builder.setMessage("서비스를 이용하시려면 위치정보에 대한 접근 권한이 필요합니다.");
                builder.setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getApplicationContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                        PERMISSIONS_REQUEST_CODE);
                            }
                        });
                builder.show();

            } else {

                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                Toast.makeText(this, "권한허가", Toast.LENGTH_LONG).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Toast.makeText(this, "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요. ", Toast.LENGTH_LONG).show();
                } else {
                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Toast.makeText(this, "권한이 거부되었습니다. 설정(앱 정보)에서 권한설정을 해야 합니다. ", Toast.LENGTH_LONG).show();
                }


            }

        }


    }


    private RealmConfiguration getRealmConfig() {

        return new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
    }

    @Override
    public void onBackPressed() {
        Log.d("메인", "onBackPressed");
        //backPressHandler.onBackPressed();
        // Toast 메세지 사용자 지정
        //backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 종료");
        // 뒤로가기 간격 사용자 지정
        //backPressHandler.onBackPressed(3000);
        // Toast, 간격 사용자 지정
        backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 종료", 3000);


    }


    private void clearPassedMission() {

        String user_id = Account.getUserId(this);
        String date_time = DateTimeUtils.getCurrentDateTimeForKey();

        databaseReference.child("user_data").child(user_id).child("passed_mission").orderByChild("date_time").endAt(date_time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    databaseReference.child("user_data").child(user_id).child("notification").setValue(dataSnapshot.getValue());
                    //dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("user_data").child(user_id).child("multi_key").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForMultiKey item = snapshot.getValue(ItemForMultiKey.class);
                        if (item != null) {
                            String multi_key = item.getMulti_mission_id();
                            databaseReference.child("user_data").child("multi_mode").child(multi_key).child("mission_display").orderByChild("date_time").endAt(date_time).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        databaseReference.child("user_data").child(user_id).child("notification").setValue(dataSnapshot.getValue());
                                        dataSnapshot.getRef().removeValue();


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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (loading_dialog.isShowing()) {
            loading_dialog.dismiss();


        }
        // Check if user is signed in (non-null) and update UI accordingly.


    }


}



