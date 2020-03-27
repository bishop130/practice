package com.suji.lj.myapplication;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.suji.lj.myapplication.Fragments.LocationFragment;
import com.suji.lj.myapplication.Fragments.SearchFragment;
import com.suji.lj.myapplication.Fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.suji.lj.myapplication.Fragments.FavoriteFragment;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;
import com.suji.lj.myapplication.Utils.BackPressHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Toolbar myToolbar;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, // 카메라
            Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS};
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private Fragment fa, fb, fc, fd;
    private FragmentManager fragmentManager;
    private BackPressHandler backPressHandler = new BackPressHandler(this);
    String TAG = "메인";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        if (Session.getCurrentSession().isClosed()) {
            Log.d("카카오", Session.getCurrentSession().isClosed() + "");
            Intent intent = new Intent(getApplicationContext(), SampleLoginActivity.class);
            startActivity(intent);
            finish();
        }
        Log.d("카카오", Session.getCurrentSession().isOpened() + "");
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("email", "");
        if(TextUtils.isEmpty(user_id)){
            Intent intent = new Intent(getApplicationContext(), SampleLoginActivity.class);
            startActivity(intent);
            finish();
        }


        checkPermission();



        Toasty.Config.getInstance()
                .setTextSize(14) // optional
                .apply(); // required


        fragmentManager = getSupportFragmentManager();
/*
        byte[] sha1 = {
                0x58, 0x64, 0x4B, (byte)0xF6, (byte)0x86, 0x44, (byte)0xE5, 0x27, (byte)0xF0, (byte)0xBB, (byte)0xEF, 0x12, 0x4C, (byte)0x9A,(byte)0xDA, (byte)0xA3,         (byte)0xB5, (byte)0xB5, (byte)0x84, (byte)0xBB

        };

        Log.d("둠피 ","" + Base64.encodeToString(sha1, Base64.NO_WRAP));
*/

        //requestAccessTokenInfo();

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


        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        fa = new FavoriteFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container,fa).commit();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("뉴토큰", "메인"+token);
                        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Map<String,String> objectMap = new HashMap<>();
                        objectMap.put("token",token);
                        if(user_id!=null)
                            databaseReference.child("user_data").child(user_id).child("fcm_data").setValue(objectMap);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_favorite:

                            if (fa == null) {
                                fa = new FavoriteFragment();
                                fragmentManager.beginTransaction().add(R.id.fragment_container, fa).commit();
                            }

                            if (fa != null) fragmentManager.beginTransaction().show(fa).commit();
                            if (fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                            if (fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                            if (fd != null) fragmentManager.beginTransaction().hide(fd).commit();

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

                            break;
                        case R.id.nav_location:
                            if (fd == null) {
                                fd = new SettingFragment();
                                fragmentManager.beginTransaction().add(R.id.fragment_container, fd).commit();
                            }
                            if (fa != null) fragmentManager.beginTransaction().hide(fa).commit();
                            if (fb != null) fragmentManager.beginTransaction().hide(fb).commit();
                            if (fc != null) fragmentManager.beginTransaction().hide(fc).commit();
                            if (fd != null) fragmentManager.beginTransaction().show(fd).commit();

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
            /*
            case R.id.action_login:

                Intent intent = new Intent(MainActivity.this, SampleLoginActivity.class);
                startActivity(intent);
                //Intent intent = new Intent(MainActivity.this,SampleLoginActivity.class);
                //startActivity(intent);

                // User chose the "Settings" item, show the app settings UI...


                return true;
                */
            case R.id.notification_icon:
                //startActivity(new Intent(MainActivity.this, SingleModeActivity.class));
                //Realm.init(getApplicationContext());
                //Realm.setDefaultConfiguration(getRealmConfig());

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
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);
            }

            @Override
            public void onNotSignedUp() {
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                String user_id = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("카카오세션", user_id);

                SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", user_id);
                editor.apply();


                String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                String refresh_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();


            }
        });
    }

    private void checkPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int contactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int phoneNumPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int batteryPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

        if ((locationPermission == PackageManager.PERMISSION_GRANTED) && (contactsPermission == PackageManager.PERMISSION_GRANTED) && (phoneNumPermission == PackageManager.PERMISSION_GRANTED) && (batteryPermission == PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(getApplicationContext(), "권한승인", Toast.LENGTH_LONG).show();


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[3])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("AlertDialog Title");
                builder.setMessage("이 앱을 실행하려면 위치정보와 연락처에 대한 접근 권한이 필요합니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();
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
        //checkPermission();
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_setting", Context.MODE_PRIVATE);
        Log.d("확인좀9", String.valueOf(sharedPreferences.getBoolean("alarm_setting", true)));
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[2]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[3])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ", Toast.LENGTH_LONG).show();
                } else {
                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }


            }

        }


    }

    public void requestProfile() {
        KakaoTalkService.getInstance().requestProfile(new KakaoTalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onSuccess(KakaoTalkProfile talkProfile) {
                final String nickName = talkProfile.getNickName();
                final String thumbnailURL = talkProfile.getThumbnailUrl();



                SharedPreferences.Editor editor = getSharedPreferences("kakao_profile", MODE_PRIVATE).edit();
                editor.putString("name", nickName);
                editor.putString("profile_image", thumbnailURL);
                editor.apply();

            }
        });
    }


    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("failure : " + errorResult);
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
        public void onSuccess(T result) {

        }
    }

    private RealmConfiguration getRealmConfig() {

        return new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
    }

    @Override
    public void onBackPressed() {
        Log.d("메인","onBackPressed");
        //backPressHandler.onBackPressed();
        // Toast 메세지 사용자 지정
        //backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 종료");
        // 뒤로가기 간격 사용자 지정
        //backPressHandler.onBackPressed(3000);
        // Toast, 간격 사용자 지정
        backPressHandler.onBackPressed("뒤로가기 버튼 한번 더 누르면 종료", 3000);


    }




}



