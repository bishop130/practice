package com.suji.lj.myapplication.Adapters;

import android.util.Log;

import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OpenBanking {


    private OkHttpClient client;
    private static OpenBanking instance = new OpenBanking();

    public static OpenBanking getInstance() {
        return instance;
    }

    private OpenBanking() {
        this.client = new OkHttpClient();
    }



    public String requestWebServer() {

        String url="https://testapi.openbanking.or.kr/oauth/2.0/authorize";


        HashMap<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", "X20kit9iRk7IGfFHbGukTXCxGGPPP6wSLie0OVgu");
        params.put("redirect_uri", "https://naver.com");
        params.put("scope", "login inquiry transfer");
        params.put("client_info", "whatever");
        params.put("state", "12345678901234567890123456789012");
        params.put("auth_type", "0");
        params.put("lang","kor");
        params.put("edit_option","on");
        params.put("bg_color","#FFFFFF");
        params.put("text_color","#000000");
        params.put("btn1_color","#000000");
        params.put("btn2_color","#000000");



        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        String LoadUrl = httpBuider.toString();
        Log.d("TestActivity", httpBuider.toString());
        return LoadUrl;
        //Request request = new Request.Builder().url(httpBuider.build()).build();


        //client.newCall(request).enqueue(callback);
    }

    public void requestAccessToken(Callback callback, String code){

        String url="https://testapi.openbanking.or.kr/oauth/2.0/token";

        RequestBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("client_id", "X20kit9iRk7IGfFHbGukTXCxGGPPP6wSLie0OVgu")
                .add("client_secret", "2YWjBZZJGUg5MjBfbP969sXlixByKp7TE704nYI6")
                .add("redirect_uri", "https://naver.com")
                .add("grant_type", "authorization_code")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);

    }
    public void requestUserAccountInfo(Callback callback, String access_token,String user_seq_num){

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://testapi.openbanking.or.kr/v2.0/user/me").newBuilder();
        urlBuilder.addQueryParameter("user_seq_no", user_seq_num);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization","Bearer "+access_token)
                .url(url)
                .build();

        Log.d("오픈뱅킹","사용자정보요청 get   "+request.toString());

        client.newCall(request).enqueue(callback);


    }

    public void requestTranfer(Callback callback, String access_token, List<UserAccountItem> userAccountItemList,int pos,String transfer_amount){

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num").newBuilder();
        urlBuilder.addQueryParameter("bank_tran_id", Utils.makeBankTranId());
        urlBuilder.addQueryParameter("cntr_account_type", "N");
        urlBuilder.addQueryParameter("cntr_account_num", "000000");
        urlBuilder.addQueryParameter("dps_print_content", "이불안은 위험해");
        urlBuilder.addQueryParameter("fintech_use_num", userAccountItemList.get(pos).getFintech_use_num());
        urlBuilder.addQueryParameter("tran_amt", transfer_amount);
        urlBuilder.addQueryParameter("tran_dtime", Utils.getCurrentTime());
        urlBuilder.addQueryParameter("req_client_name", userAccountItemList.get(pos).getUser_name());
        urlBuilder.addQueryParameter("req_client_num",userAccountItemList.get(pos).getUser_seq_no());
        urlBuilder.addQueryParameter("transfer_purpose","TR");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization","Bearer "+access_token)
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);




    }


}
