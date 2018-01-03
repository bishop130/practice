package com.example.lj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;


public class SampleLoginActivity extends Activity {

    private SessionCallback mKakaocallback;

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


        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        iv_user_profile = (ImageView) findViewById(R.id.iv_user_profile);
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Toast.makeText(SampleLoginActivity.this, "로그아웃성공", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mKakaocallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaocallback);
        Session.getCurrentSession().checkAndImplicitOpen();
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

            Intent intent = new Intent(SampleLoginActivity.this,SampleSignupActivity.class);
            startActivity(intent);

            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int ErrorCode = errorResult.getErrorCode();
                    int ClientErrorCode = -777;

                    if (ErrorCode == ClientErrorCode) {
                        Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SampleLoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.d("TAG" , "오류로 카카오로그인 실패 ");
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(SampleLoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    Log.d("TAG" , "오류로 카카오로그인 실패 ");
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    Toast.makeText(SampleLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    profileUrl = userProfile.getProfileImagePath();
                    userId = String.valueOf(userProfile.getId());
                    userName = userProfile.getNickname();

                    setLayoutText();
                }

                @Override
                public void onNotSignedUp() {
                    // 자동가입이 아닐경우 동의창
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Toast.makeText(SampleLoginActivity.this, "세션실패", Toast.LENGTH_SHORT).show();
                Log.d("TAG" , exception.getMessage());
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

}