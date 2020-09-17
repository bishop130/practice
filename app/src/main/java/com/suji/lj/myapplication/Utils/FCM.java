package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FCM {

    public static void fcmPushAlarm(String friend_id, String title, String content) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(friend_id).child("fcm_data").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String fcm_token = dataSnapshot.getValue(String.class);

                    if (fcm_token != null) {

                        OkHttpClient httpClient = new OkHttpClient();
                        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
                        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/fcm_push";


                        FormBody.Builder builder = new FormBody.Builder();

                        builder.add("fcm_token", fcm_token);
                        builder.add("title", title);
                        builder.add("body", content);


                        RequestBody formBody = builder.build();

                        Request request = new Request.Builder()
                                .post(formBody)
                                .url(url)
                                .build();

                        httpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.d("부트페이", "실패");
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                ResponseBody responseBody = response.body();
                                String resp = "";
                                if (!response.isSuccessful()) {
                                    Log.d("푸시알림", "fail response from firebase cloud function");
                                } else {
                                    if (responseBody != null) {
                                        Log.d("푸시알림", responseBody.string() + "fail response from firebase cloud function");

                                    }
                                }

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
