package com.suji.lj.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SampleLoginActivity extends AppCompatActivity {

    // view
    private ISessionCallback callback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.d("카카오세션", "세션 오픈됨 sample");
            requestAccessTokenInfo();
            loading_dialog.show();

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Toasty.error(getApplicationContext(), "세션실패", Toasty.LENGTH_LONG, true).show();
            }
        }

    };

    //private


    DatabaseReference databaseReference;
    Handler mhandler;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Runnable runnable;

    AlertDialog loading_dialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Session.getCurrentSession().checkAndImplicitOpen();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Session.getCurrentSession().addCallback(callback);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        Log.d("카카오", "oncreate");
        //FirebaseUser currentUser = mAuth.getCurrentUser();


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
        Log.d("카카오", "remove callback");
        Session.getCurrentSession().removeCallback(callback);
    }


    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);
                Log.d("카카오세션", "세션클로즈");
            }

            @Override
            public void onNotSignedUp() {
                Log.d("카카오세션", "가입이 안됨");
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                String user_id = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("카카오세션", user_id + " sample login");

                SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", user_id);
                editor.apply();


                //String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                //String refresh_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();


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

                UserAccount kakaoAccount = response.getKakaoAccount();
                if (kakaoAccount != null) {
                    String email = kakaoAccount.getEmail();
                    String real_name = kakaoAccount.getLegalName();
                    String user_name = kakaoAccount.getProfile().getNickname();
                    String profile_img = kakaoAccount.getProfile().getProfileImageUrl();
                    String thumbnail_img = kakaoAccount.getProfile().getThumbnailImageUrl();
                    String user_id = String.valueOf(response.getId());

                    Log.d("카카오", email + "이메일");
                    Log.d("카카오", real_name + "실명");
                    if (email != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("thumbnail", thumbnail_img);
                        editor.putString("user_name", user_name);
                        editor.apply();


                        FirebaseUser currentUser = mAuth.getCurrentUser();


                        firebaseConnect(kakaoAccount, user_id);


                    } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                        handleScopeError(kakaoAccount);
                        // 동의 요청 후 이메일 획득 가능
                        // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                    } else {
                        Log.d("카카오", "이메일 획득불가");
                        // 이메일 획득 불가
                    }

                    Profile profile = kakaoAccount.getProfile();
                    if (profile != null) {
                        //Log.d("카카오", profile.getNickname() + "닉네임");
                        //Log.d("카카오", profile.getProfileImageUrl() + "이미지");
                        //Log.d("카카오", profile.getThumbnailImageUrl() + "썸네일");
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
        if (account.emailNeedsAgreement() == OptionalBoolean.TRUE) {
            neededScopes.add("account_email");
        }

        Session.getCurrentSession().updateScopes(this, neededScopes, new AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(AccessToken accessToken) {
                // 유저에게 성공적으로 동의를 받음. 토큰을 재발급 받게 됨.
                Log.d("카카오", "동적동의 성공");
            }

            @Override
            public void onAccessTokenFailure(ErrorResult errorResult) {
                loading_dialog.dismiss();
                // 동의 얻기 실패
            }
        });
    }


    private void firebaseConnect(UserAccount kakaoAccount, String user_id) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/custom_token";

        RequestBody formBody = new FormBody.Builder()
                .add("token", user_id)
                .build();
        okhttp3.Request request = new Request.Builder()
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
                    if (responseBody != null) {
                        String receipt = responseBody.string();
                        String mCustomToken = Utils.getValueFromJson(receipt, "token");
                        Log.d("부트페이", mCustomToken + " 커스텀토큰");
                        mAuth.signInWithCustomToken(mCustomToken)
                                .addOnCompleteListener(SampleLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("카카오", "signInWithCustomToken:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            compareAccount(kakaoAccount, user_id);


                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("부트페이", "signInWithCustomToken:failure", task.getException());

                                        }
                                    }
                                });


                    }
                }

            }
        });


    }

    private void compareAccount(UserAccount kakaoAccount, String user_id) {

        String email = kakaoAccount.getEmail();
        String real_name = kakaoAccount.getLegalName();
        String user_name = kakaoAccount.getProfile().getNickname();
        String profile_img = kakaoAccount.getProfile().getProfileImageUrl();
        String thumbnail_img = kakaoAccount.getProfile().getThumbnailImageUrl();
        String userid = String.valueOf(kakaoAccount.getDisplayId());
        Session.getCurrentSession().removeCallback(callback);


        databaseReference.child("account").orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("카카오", dataSnapshot.getKey() + "키");
                Log.d("카카오", dataSnapshot.getChildrenCount() + "카운트");
                if (dataSnapshot.exists()) {//이미 가입된 유저 (앱연결해제x 로그아웃 후 다시 로그인)

                    Log.d("카카오", "이미가입된 유저" + "아이");
                    Intent intent = new Intent(SampleLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {//신규유저 이메일 획득 후 비밀번호 설정 화면 이동


                    Log.d("카카오", "새로운 유저");
                    //Log.d("이메일 : ",email+"이메일");
                    ItemRegisterAccount item = new ItemRegisterAccount();
                    item.setUser_id(user_id);
                    item.setIs_public(true);
                    item.setUser_name(user_name);
                    item.setThumnail_img(thumbnail_img);
                    item.setEmail(email);


                    databaseReference.child("account").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            Intent intent = new Intent(SampleLoginActivity.this, SecurityActivity.class);
                            intent.putExtra("password", 1);
                            startActivity(intent);
                            finish();
                        }
                    });

                    Log.d("카카오 ", email + "이메일");
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                loading_dialog.dismiss();

            }
        });


    }


}