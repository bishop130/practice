package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

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

    public void requestTransfer(Callback callback, Context context){

        String user_me_body = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me","");
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        int pos = 0;
        List<UserAccountItem> userAccountItemList = Utils.UserInfoResponseJsonParse(user_me_body);

        String url = "https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num";

/*
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num").newBuilder();
        urlBuilder.addQueryParameter("bank_tran_id", Utils.makeBankTranId());
        Log.d("송금",Utils.makeBankTranId());
        urlBuilder.addQueryParameter("cntr_account_type", "N");
        urlBuilder.addQueryParameter("cntr_account_num", "29030204202663");
        urlBuilder.addQueryParameter("dps_print_content", "이불안은위험해");
        urlBuilder.addQueryParameter("fintech_use_num", userAccountItemList.get(pos).getFintech_use_num());

        urlBuilder.addQueryParameter("tran_amt", context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("transfer_amount",""));
        Log.d("송금",context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("transfer_amount",""));
        urlBuilder.addQueryParameter("tran_dtime", Utils.getCurrentTime());
        Log.d("송금",Utils.getCurrentTime());
        urlBuilder.addQueryParameter("req_client_name", userAccountItemList.get(pos).getUser_name());
        Log.d("송금",userAccountItemList.get(pos).getUser_name());
        urlBuilder.addQueryParameter("req_client_num",userAccountItemList.get(pos).getUser_seq_no());
        Log.d("송금",userAccountItemList.get(pos).getUser_seq_no());
        urlBuilder.addQueryParameter("transfer_purpose","TR");
*/
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bank_tran_id", Utils.makeBankTranId());
        params.put("cntr_account_type", "N");
        params.put("cntr_account_num", "85768576");
        params.put("dps_print_content", "이불안은위험해");
        params.put("fintech_use_num", userAccountItemList.get(pos).getFintech_use_num());
        params.put("wd_print_content","오픈");
        params.put("tran_amt", "1000");
        params.put("tran_dtime", Utils.getCurrentTime());
        params.put("req_client_name", userAccountItemList.get(pos).getUser_name());
        params.put("req_client_num","HONG1234");
        params.put("transfer_purpose","TR");
        params.put("req_client_bank_code","004");
        params.put("req_client_account_num","01030331130");
        params.put("req_client_fintech_use _num",userAccountItemList.get(0).getFintech_use_num());
        params.put("sub_frnc_name","이름");
        params.put("sub_frnc_num","12345678");
        params.put("sub_frnc_business_num","12345678");
        params.put("recv_client_name","이불안");
        params.put("recv_client_bank_code","004");
        params.put("recv_client_account_num","85768576");






        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON,parameter.toString());

        Log.d("송금",userAccountItemList.get(pos).getFintech_use_num());


        Log.d("송금",context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("transfer_amount",""));
        Log.d("송금",Utils.getCurrentTime());
        Log.d("송금","핀테크"+userAccountItemList.get(pos).getFintech_use_num());
        Log.d("송금",userAccountItemList.get(pos).getUser_seq_no());

/*
        RequestBody formBody = new FormBody.Builder()
        .add("bank_tran_id", Utils.makeBankTranId())
        .add("cntr_account_type", "N")
        .add("cntr_account_num", "29030204202663")
        .add("dps_print_content", "이불안은 위험해")
        .add("fintech_use_num", userAccountItemList.get(pos).getFintech_use_num())
        .add("tran_amt", context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("transfer_amount",""))
        .add("tran_dtime", Utils.getCurrentTime())
        .add("req_client_name", userAccountItemList.get(pos).getUser_name())
        .add("req_client_num",userAccountItemList.get(pos).getUser_seq_no())
        .add("transfer_purpose","TR")
                .build();


*/
        //String url = urlBuilder.build().toString();




        Request request = new Request.Builder()
                .header("Authorization","Bearer "+access_token)
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);




    }

    public void transferDeposit(Callback callback, Context context){

        String url = "https://openapi.openbanking.or.kr/v2.0/transfer/deposit/acnt_num";

        String user_me_body = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me","");
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        int pos = 0;
        List<UserAccountItem> userAccountItemList = Utils.UserInfoResponseJsonParse(user_me_body);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("cntr_account_type","N");
        params.put("cntr_account_num","85768576");
        params.put("wd_pass_phrase","");
        params.put("wd_print_content","오픈");
        params.put("name_check_option","off");
        params.put("sub_frnc_name","");
        params.put("sub_frnc_num","");
        params.put("sub_frnc_business_num","");
        params.put("tran_dtime",Utils.getCurrentTime());
        params.put("req_cnt","1");


        JSONObject ob = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            ob.put("tran_no", "1");
            ob.put("bank_tran_id", Utils.makeBankTranId());
            ob.put("bank_code_std", "004");
            ob.put("account_num", "29030204202663");
            ob.put("account_holder_name","이정");
            ob.put("print_content","오픈서비스캐시");
            ob.put("tran_amt","500");
            ob.put("req_client_name","이정환");
            ob.put("req_client_bank_code","004");
            ob.put("req_client_account_num","01030331130");
            ob.put("req_client_fintech_use_num","");
            ob.put("req_client_num","HONG1234");
            ob.put("transfer_purpose","TR");

            arr.put(ob);

        }
        catch (JSONException e){
            e.printStackTrace();
        }


        params.put("req_list",arr.toString());


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON,parameter.toString());



        Request request = new Request.Builder()
                .header("Authorization","Bearer "+access_token)
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);




    }


}
