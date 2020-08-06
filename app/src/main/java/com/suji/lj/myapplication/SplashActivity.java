package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
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
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SplashActivity extends AppCompatActivity {

    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    Handler mhandler;
    Runnable runnable;
    boolean isLoaded;
    ProgressBar progressBar;
    boolean isSessionOpen;
    AlertDialog loading_dialog;
    RelativeLayout rlSplash;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);
        rlSplash = findViewById(R.id.rlSplash);

        boolean isNetworkConnected = Utils.isNetworkConnected(this);

        if (isNetworkConnected) {
            Session.getCurrentSession().checkAndImplicitOpen();
            //databaseReference = FirebaseDatabase.getInstance().getReference();
            // Session.getCurrentSession().addCallback(callback);
            isSessionOpen = Session.getCurrentSession().isOpened();


            requestAccessTokenInfo();
        } else {

            Snackbar snackbar = Snackbar.make(rlSplash, "네트워크 상태를 확인해주세요", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();


        }


    }

    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);

                Log.d("카카오세션", "closed");
                mhandler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(SplashActivity.this, SampleLoginActivity.class);
                        startActivity(i);
                        finish();

                    }
                };

                mhandler.postDelayed(runnable, 3000);

            }


            @Override
            public void onNotSignedUp() {
                Log.d("카카오세션", " not signup");
                mhandler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(SplashActivity.this, SampleLoginActivity.class);
                        startActivity(i);
                        finish();

                    }
                };

                mhandler.postDelayed(runnable, 3000);
                // not happened


            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                String user_id = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("카카오세션", Session.getCurrentSession().isOpened() + "   세션상태");

                SharedPreferences sharedPreferences = getSharedPreferences("Kakao", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", user_id);
                editor.apply();

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {

                    Log.d("카카오", "파이어베이스 롱그인");
                    mhandler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();


                        }
                    };

                    mhandler.post(runnable);

                } else {
                    Log.d("카카오", "파이어베이스 아직로그인 안됨");


                    firebaseConnect(user_id);
                }


                //String access_token = Session.getCurrentSession().getTokenInfo().getAccessToken();
                //String refresh_token = Session.getCurrentSession().getTokenInfo().getRefreshToken();


            }
        });


    }


    private void firebaseConnect(String user_id) {

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
                Log.d("카카오", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = "";
                if (!response.isSuccessful()) {
                    Log.d("카카오", "fail response from firebase cloud function");
                } else {
                    if (responseBody != null) {
                        String receipt = responseBody.string();
                        String mCustomToken = Utils.getValueFromJson(receipt, "token");
                        Log.d("카카오", mCustomToken + " 커스텀토큰");
                        mAuth.signInWithCustomToken(mCustomToken).addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("카카오", "signInWithCustomToken:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    mhandler = new Handler();
                                    runnable = new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                            startActivity(i);
                                            finish();


                                        }
                                    };

                                    mhandler.post(runnable);


                                } else {
                                    mhandler = new Handler();
                                    runnable = new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent i = new Intent(SplashActivity.this, SampleLoginActivity.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    };

                                    mhandler.post(runnable);


                                    Log.w("카카오", "signInWithCustomToken:failure", task.getException());

                                }
                            }
                        });


                    }
                }

            }
        });


    }


}
