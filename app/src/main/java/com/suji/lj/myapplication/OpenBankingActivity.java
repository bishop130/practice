package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Items.ItemForCommonOpenBanking;
import com.suji.lj.myapplication.Items.ItemForOpenBanking;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OpenBankingActivity extends AppCompatActivity {
    private static final String TAG = "오픈뱅킹";

    WebView webView;
    private OpenBanking openBanking = OpenBanking.getInstance();

    private final Callback access_token_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:" + e.getMessage());
            Toast.makeText(getApplicationContext(),"인증오류",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:" + body);
            String access_token = Utils.getValueFromJson(body, "access_token");
            String refresh_token = Utils.getValueFromJson(body,"refresh_token");
            String user_seq_num = Utils.getValueFromJson(body,"user_seq_no");
            String expires_in = Utils.getValueFromJson(body,"expires_in");
            Log.d(TAG, "access token Body:" + access_token);

            saveData(access_token,refresh_token,user_seq_num,expires_in);


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_banking);
        webView = new WebView(this);

        webView = findViewById(R.id.webView_openbanking);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // 로그인을 위해 필요
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("UTF-8");



        webView.loadUrl(openBanking.requestWebServer());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                if(url.startsWith("https://naver.com")) {

                    String code = Utils.getParamValFromUrlString(url, "code");//authorization_code



                    // 요청시 이용기관이 세팅한 state 값을 그대로 전달받는 것으로, 이용기관은 CSRF 보안위협에 대응하기 위해 요청 시의 state 값과 응답 시의 state 값을 비교해야 함

                    openBanking.requestAccessToken(access_token_callback, code);
                    //sendFintech(code);
                    Log.d("오픈뱅킹", "authorization code "+code);


                    return true;
                }

                return true;
            }
        });

    }

    private void saveData(String access_token, String refresh_token, String user_seq_num, String expires_in){
        SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", Context.MODE_PRIVATE);
        SharedPreferences.Editor open_banking_token = sharedPreferences.edit();
        open_banking_token.putString("access_token",access_token);
        open_banking_token.putString("refresh_token",refresh_token);
        open_banking_token.putString("user_seq_num",user_seq_num);
        open_banking_token.putString("expires_in",expires_in);


        SharedPreferences sharedPreferences1 = getSharedPreferences("OpenBanking",MODE_PRIVATE);
        String user_id=getSharedPreferences("Kakao",MODE_PRIVATE).getString("token","");
        Log.d("오픈뱅킹","sharedpreference"+sharedPreferences1.getString("access_token",""));
        ItemForOpenBanking item = new ItemForOpenBanking();
        item.setToken(access_token);
        item.setRefresh_token(refresh_token);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("common").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ItemForCommonOpenBanking item = dataSnapshot.getValue(ItemForCommonOpenBanking.class);
                String center_id = item.getCenter_id();
                open_banking_token.putString("center_id",center_id);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("user_data").child(user_id).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                open_banking_token.apply();
                finish();
            }
        });





    }
}
