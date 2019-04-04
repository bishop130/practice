package com.example.lj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.StringSet;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SampleLoginActivity extends AppCompatActivity {

    private SessionCallback mKakaocallback;
    private static final String URL = "http://bishop130.cafe24.com/user_control.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    // view
    private TextView tv_user_id;
    private TextView tv_user_name;
    private ImageView iv_user_profile;
    private Button logout;
    private String userName;
    private String userId;
    private String profileUrl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        requestQueue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.recycler_result_toolbar);
        toolbar.setTitle("내 정보");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Button fake_button = (Button)findViewById(R.id.fake_button);
        fake_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKakaocallback = new SessionCallback();
                Session.getCurrentSession().addCallback(mKakaocallback);
                Session.getCurrentSession().checkAndImplicitOpen();
            }
        });
        /*
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                    }
                });
                Toast.makeText(SampleLoginActivity.this, "로그아웃성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SampleLoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("TAG" , "세션 오픈됨");

            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴

                List<String> keys = new ArrayList<>();
                keys.add("properties.nickname");
                keys.add("properties.profile_image");
                keys.add("kakao_account.email");

                UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Toast.makeText(SampleLoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.d("TAG" , "오류로 카카오로그인 실패 ");

                    }

                    @Override
                    public void onSuccess(MeV2Response response) {
                        Toast.makeText(SampleLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SampleLoginActivity.this,SampleSignupActivity.class);
                        startActivity(intent);
                        requestUpdateProfile();

                        profileUrl = response.getProfileImagePath();
                        userId = String.valueOf(response.getId());
                        userName = response.getNickname();
                        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.names().get(0).equals("success")){
                                        Toast.makeText(getApplicationContext(),"SUCCESS "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Error" +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> hashMap = new HashMap<String, String>();
                                hashMap.put("email",userId);

                                return hashMap;
                            }
                        };

                        requestQueue.add(request);

                        setLayoutText();
                    }


                    @Override
                    public void onFailure(ErrorResult errorResult){

                    }

                });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Toast.makeText(SampleLoginActivity.this, "세션실패"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG" , exception.getMessage());
            }
        }
    }
    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */

    private void requestUpdateProfile() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("nickname", "Leo");
        properties.put("profile_image", "Leo");

        UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long> () {
            @Override
            public void onSuccess(Long userId) {
                Toast.makeText(SampleLoginActivity.this, "새로받은 userID"+userId, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(SampleLoginActivity.this, "사인업안됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.e(message);
                Toast.makeText(SampleLoginActivity.this, "실패!! "+errorResult, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(SampleLoginActivity.this, "세션닫힘"+userId, Toast.LENGTH_SHORT).show();
            }

        }, properties);

        Toast.makeText(SampleLoginActivity.this, "프로퍼티  "+properties, Toast.LENGTH_SHORT).show();
    }

    private void setLayoutText(){
        tv_user_id.setText(userId);
        tv_user_name.setText(userName);

        Picasso.with(this)
                .load(profileUrl)
                .fit()
                .into(iv_user_profile);
    }


}