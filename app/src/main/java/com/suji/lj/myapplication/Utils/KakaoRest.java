package com.suji.lj.myapplication.Utils;

import android.util.Log;

import com.suji.lj.myapplication.Adapters.OpenBanking;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class KakaoRest {

    private OkHttpClient client;

    private static KakaoRest instance = new KakaoRest();

    public static KakaoRest getInstance() {
        return instance;
    }

    private KakaoRest() {
        this.client = new OkHttpClient();
    }


    public void getAddressFromLocation(Callback callback, double lat, double lng) {


        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://dapi.kakao.com//v2/local/geo/coord2address").newBuilder();
        urlBuilder.addQueryParameter("x", String.valueOf(lng));
        urlBuilder.addQueryParameter("y", String.valueOf(lat));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69")
                .url(url)
                .build();


        client.newCall(request).enqueue(callback);


    }
}
