package com.suji.lj.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.suji.lj.myapplication.Adapters.BackPressCloseHandler;
import com.suji.lj.myapplication.Adapters.DBHelper;
import com.suji.lj.myapplication.Fragments.SettingFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.fabric.sdk.android.Fabric;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.suji.lj.myapplication.Fragments.FavoriteFragment;
import com.suji.lj.myapplication.Fragments.HomeFragment;
import com.suji.lj.myapplication.Fragments.SearchFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String goURL = "http://bishop130.cafe24.com/Mission_Control.php";
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private static final String refreshDatabaseURL = "http://bishop130.cafe24.com/refreshDB.php";
    private SessionCallback mKakaocallback;
    Toolbar myToolbar;
    private static final String URL = "http://bishop130.cafe24.com/user_control.php";
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, // 카메라
            Manifest.permission.READ_CONTACTS};
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    DrawerLayout mDrawerLayout;
    TextView kakao_name_drawer;
    ImageView profile_image_drawer;
    public static Context mContext;
    private StringRequest request;
    private static final String tokenURL = "http://bishop130.cafe24.com/update_token.php";
    private RequestQueue requestQueue;
    private BackPressCloseHandler backPressCloseHandler;
    DBHelper dbHelper;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        dbHelper = new DBHelper(MainActivity.this, "alarm_manager.db", null, 5);

/*
        byte[] sha1 = {
                0x58, 0x64, 0x4B, (byte)0xF6, (byte)0x86, 0x44, (byte)0xE5, 0x27, (byte)0xF0, (byte)0xBB, (byte)0xEF, 0x12, 0x4C, (byte)0x9A,(byte)0xDA, (byte)0xA3,         (byte)0xB5, (byte)0xB5, (byte)0x84, (byte)0xBB

        };

        Log.d("둠피 ","" + Base64.encodeToString(sha1, Base64.NO_WRAP));
*/

        requestAccessTokenInfo();
        deletePastDB();



        mContext = this;
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


        SharedPreferences preferences = getSharedPreferences("alarm_setting_check", MODE_PRIVATE);
        List<String> date_array = new ArrayList<>();

        Map<String, ?> allEntries = preferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("맵밸류", entry.getKey() + ": " + entry.getValue().toString());
            if (Boolean.valueOf(entry.getValue().toString())) {
                date_array.add(entry.getKey());

            }
        }
        for (int i = 0; i < date_array.size(); i++) {

            Log.d("유유", date_array.get(i));
        }


        backPressCloseHandler = new BackPressCloseHandler(this);

        /*
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.new_mission_make:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, NewMissionActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.check_last_mission:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.setting:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Intent intent_setting = new Intent(MainActivity.this, SampleLoginActivity.class);
                        startActivity(intent_setting);
                        break;


                }

                return true;
            }
        });

        View nav_header_view = navigationView.getHeaderView(0);

        kakao_name_drawer = nav_header_view.findViewById(R.id.kakao_name_drawer);
        profile_image_drawer = nav_header_view.findViewById(R.id.profile_image_drawer);
        profile_image_drawer.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_drawer.setClipToOutline(true);
 */

        SharedPreferences.Editor editor = getSharedPreferences("sFile", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        SharedPreferences.Editor editor1 = getSharedPreferences("Contact", MODE_PRIVATE).edit();
        editor1.clear();
        editor1.apply();
/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.lj.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_favorite:
                            selectedFragment = new FavoriteFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new SettingFragment();

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
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
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);
                Toast.makeText(getApplicationContext(), "세션닫힘", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SampleLoginActivity.class);
                startActivity(intent);
                finish();

 /*
                kakao_name_drawer.setText("로그인 해주세요");

                LinearLayout kakao_login = (LinearLayout) dialog.findViewById(R.id.kakao_layout);
                kakao_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "로그인 확인좀", Toast.LENGTH_LONG).show();


                    }
                });
                */
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getApplicationContext(), "로그인해주세요", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SampleLoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                final String userId = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("토큰확인", String.valueOf(userId));

                SharedPreferences sharedPreferences = getSharedPreferences("Kakao", MODE_PRIVATE);
                SharedPreferences.Editor kakao_token = sharedPreferences.edit();
                kakao_token.putString("token", userId);
                kakao_token.apply();

/*
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.d("newToken", newToken);
                        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", newToken);
                        editor.apply();

                        updateToken(newToken, userId);


                    }
                });
                */
                refreshDatabase(userId);
                requestProfile();
                Toast.makeText(getApplicationContext(), "토큰확인 로그인 성공", Toast.LENGTH_LONG).show();

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Log.d("토큰 만료 됨 ", expiresInMilis + "이후에");
            }
        });
    }

    private void updateToken(final String token, final String user_id) {

        request = new StringRequest(Request.Method.POST, tokenURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("토큰", "전송성공");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("token", token);
                hashMap.put("user_id", user_id);

                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("TAG", "세션 오픈됨");


            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Toast.makeText(MainActivity.this, "세션실패" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", exception.getMessage());
            }
        }
    }

    private void checkPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int contactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (locationPermission == PackageManager.PERMISSION_GRANTED && contactsPermission == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "권한승인", Toast.LENGTH_LONG).show();


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

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

    private void deletePastDB() {
        DBHelper dbHelper = new DBHelper(MainActivity.this, "alarm_manager.db", null, 5);
        String sql = "DELETE FROM alarm_table WHERE strftime('%s', date_time) < strftime('%s', datetime('now','localtime'))"; //현재 이전 정보 삭제
        dbHelper.update(sql);
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
                final String profileImageURL = talkProfile.getProfileImageUrl();
                final String thumbnailURL = talkProfile.getThumbnailUrl();
                final String countryISO = talkProfile.getCountryISO();


                SharedPreferences.Editor editor = getSharedPreferences("kakao_profile", MODE_PRIVATE).edit();
                editor.putString("name", nickName);
                editor.putString("profile_image", thumbnailURL);
                editor.apply();
/*
                Log.d("카톡프로필", nickName + "\n" + profileImageURL + "\n" + thumbnailURL + "\n" + countryISO);
                kakao_name_drawer.setText(nickName);
                Picasso.with(getApplicationContext())
                        .load(thumbnailURL)
                        .fit()
                        .into(profile_image_drawer);
                        */


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
    }

    private void refreshDatabase(final String user_id) {
       // SharedPreferences prefs = getSharedPreferences("com.suji.lj", MODE_PRIVATE);
       // if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs


            request = new StringRequest(Request.Method.POST, refreshDatabaseURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("메인", "DB_refresh");
                    String sql = "DELETE FROM alarm_table";
                    dbHelper.selectData(sql);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String mission_id = jsonObject.getString("mission_id");
                            String time = jsonObject.getString("mission_time");
                            String lat = jsonObject.getString("mission_lat");
                            String lng = jsonObject.getString("mission_lng");
                            String title = jsonObject.getString("mission_name");
                            String date = jsonObject.getString("time");
                            String date_time = date+" "+time;

                            String query = "INSERT INTO alarm_table VALUES(null,'" + time + "','" + lat + "','" + lng + "','" +
                                    mission_id + "','" + date + "','" + date_time + "','false','" + title + "','" + user_id + "')";
                            Log.d("메인", "   lat:" + String.valueOf(lat) + "   " +
                                    "lng:" + String.valueOf(lng) + "     time:" + time + "     mission_id:" + mission_id + "   " +
                                    "  date:" + date + "     date+time:" + date_time + "      title:" + title + "       user_id:" + user_id);
                            dbHelper.insert(query);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("user_id", user_id);

                    return hashMap;
                }
            };
            requestQueue.add(request);


         //   prefs.edit().putBoolean("firstrun", false).apply();



    }


}





