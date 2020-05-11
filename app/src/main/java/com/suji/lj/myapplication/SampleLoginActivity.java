package com.suji.lj.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.Utils.FirebaseDB;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

public class SampleLoginActivity extends AppCompatActivity {

    // view
    private ISessionCallback callback =  new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.d("카카오세", "세션 오픈됨1");
            requestAccessTokenInfo();

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Toasty.error(getApplicationContext(), "세션실패", Toasty.LENGTH_LONG, true).show();
            }
        }

    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        //Session.getCurrentSession().isClosed();

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
        Session.getCurrentSession().removeCallback(callback);
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
                Log.d("카카오세션" , user_id);

                SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token",user_id);
                editor.apply();



                String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                String refresh_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();


                requestMe();

            }
        });
    }


    private void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onSuccess(MeV2Response response) {
                Log.d("이메일 :" ,response.getId()+"아이");

                String user_id = String.valueOf(response.getId());
                UserAccount kakaoAccount = response.getKakaoAccount();
                if (kakaoAccount != null) {
                    String email = kakaoAccount.getEmail();
                    String real_name = kakaoAccount.getLegalName();
                    String user_name = kakaoAccount.getProfile().getNickname();
                    String profile_img = kakaoAccount.getProfile().getProfileImageUrl();
                    String thumbnail_img = kakaoAccount.getProfile().getThumbnailImageUrl();
                    //Log.d("이메일 : ",email+"이메일");
                    if (email != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email",email);
                        editor.putString("thumbnail",thumbnail_img);
                        editor.putString("user_name",user_name);
                        editor.apply();
                        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        ItemRegisterAccount item = new ItemRegisterAccount();
                        item.setUser_id(user_id);
                        item.setIs_public(true);
                        item.setUser_name(user_name);
                        item.setThumnail_img(thumbnail_img);
                        item.setProfile_img(profile_img);
                        item.setEmail(email);
                        String str = email.substring(0,email.indexOf("@"));

                        mRootRef.child("account").child(str).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(SampleLoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        Log.d("이메일 : ",email+"이메일");
                    } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                        handleScopeError(kakaoAccount);
                        // 동의 요청 후 이메일 획득 가능
                        // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                    } else {
                        Log.d("이메일","이메일 획득불가");
                        // 이메일 획득 불가
                    }

                    Profile profile = kakaoAccount.getProfile();
                    if (profile != null) {
                        Log.d("이메일 : " ,profile.getNickname()+"닉네임");
                        Log.d("이메일 : " ,profile.getProfileImageUrl()+"이미지");
                        Log.d("이메일 : " ,profile.getThumbnailImageUrl()+"썸네일");
                    } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 프로필 정보 획득 가능
                    } else {
                        // 프로필 획득 불가
                    }
                }

            }

        });
    }

    private void handleScopeError(UserAccount account) {
        List<String> neededScopes = new ArrayList<>();
        if (account.emailNeedsAgreement()==OptionalBoolean.TRUE) {
            neededScopes.add("account_email");
        }

        Session.getCurrentSession().updateScopes(this, neededScopes, new
                AccessTokenCallback() {
                    @Override
                    public void onAccessTokenReceived(AccessToken accessToken) {
                        // 유저에게 성공적으로 동의를 받음. 토큰을 재발급 받게 됨.
                        Log.d("이메일","동적동의 성공");
                    }

                    @Override
                    public void onAccessTokenFailure(ErrorResult errorResult) {
                        // 동의 얻기 실패
                    }
                });
    }





}