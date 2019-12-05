package com.suji.lj.myapplication;

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
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
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

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

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
    private SessionCallback callback;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        requestQueue = Volley.newRequestQueue(this);

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
            Log.d("카카오" , "세션 오픈됨");

            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴

                List<String> keys = new ArrayList<>();
                keys.add("properties.nickname");
                keys.add("properties.profile_image");
                keys.add("kakao_account.email");
            Intent intent = new Intent(SampleLoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
/*
                UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Toast.makeText(SampleLoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.d("카카오" , "오류로 카카오로그인 실패 ");

                    }

                    @Override
                    public void onSuccess(MeV2Response response) {
                        Toast.makeText(SampleLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                        requestUpdateProfile();

                        profileUrl = response.getProfileImagePath();
                        userId = String.valueOf(response.getId());
                        userName = response.getNickname();

                    }


                    @Override
                    public void onFailure(ErrorResult errorResult){

                    }

                });
                */

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                //Toast.makeText(SampleLoginActivity.this, "세션실패"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                Toasty.error(getApplicationContext(),"세션실패",Toasty.LENGTH_LONG,true).show();
            }
        }
    }
    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */

    private void setLayoutText(){
        tv_user_id.setText(userId);
        tv_user_name.setText(userName);

        Picasso.with(this)
                .load(profileUrl)
                .fit()
                .into(iv_user_profile);
    }

    private void volleyConnect(){
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


}