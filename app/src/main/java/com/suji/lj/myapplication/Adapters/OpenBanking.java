package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
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


    /** 오픈뱅킹 가입창 로드 **/
    public String requestWebServer() {

        String url = "https://testapi.openbanking.or.kr/oauth/2.0/authorize";


        HashMap<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", "X20kit9iRk7IGfFHbGukTXCxGGPPP6wSLie0OVgu");
        params.put("redirect_uri", "https://naver.com");
        params.put("scope", "login inquiry transfer");
        params.put("client_info", "whatever");
        params.put("state", "12345678901234567890123456789012");
        params.put("auth_type", "0");
        params.put("lang", "kor");
        params.put("edit_option", "on");
        params.put("bg_color", "#FFFFFF");
        params.put("text_color", "#000000");
        params.put("btn1_color", "#000000");
        params.put("btn2_color", "#000000");


        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        String LoadUrl = httpBuider.toString();
        Log.d("TestActivity", httpBuider.toString());
        return LoadUrl;
        //Request request = new Request.Builder().url(httpBuider.build()).build();


        //client.newCall(request).enqueue(callback);
    }
    /** 오픈뱅킹 토큰 요청**/
    public void requestAccessToken(Callback callback, String code) {

        String url = "https://testapi.openbanking.or.kr/oauth/2.0/token";

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
    /** 사용자 계좌정보 요청**/
    public void requestUserAccountInfo(Callback callback, String access_token, String user_seq_num) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://testapi.openbanking.or.kr/v2.0/user/me").newBuilder();
        urlBuilder.addQueryParameter("user_seq_no", user_seq_num);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .url(url)
                .build();

        Log.d("오픈뱅킹", "사용자정보요청 get   " + request.toString());

        client.newCall(request).enqueue(callback);


    }
    /** 출금이체 요청**/

    public void requestTransfer(Callback callback, Context context) {

        String user_me_body = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me", "");
        String access_token = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("access_token", "");
        String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");
        List<UserAccountItem> userAccountItemList = Utils.UserInfoResponseJsonParse(user_me_body);
        for(int i =0; i<userAccountItemList.size(); i++){
            if(userAccountItemList.get(i).getFintech_use_num().equals(fintech_num)){
                String url = "https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num";

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> params = new HashMap<String, String>();
                params.put("bank_tran_id", Utils.makeBankTranId());
                params.put("cntr_account_type", "N");
                params.put("cntr_account_num", "290302");
                params.put("dps_print_content", "테스트");
                params.put("fintech_use_num", fintech_num);
                params.put("wd_print_content", "오픈");
                params.put("tran_amt", String.valueOf(1000));
                params.put("tran_dtime", Utils.getCurrentTime());
                params.put("req_client_name", userAccountItemList.get(i).getUser_name());
                params.put("req_client_num", "HONG1234");
                params.put("transfer_purpose", "TR");
                params.put("req_client_bank_code", "004");
                params.put("req_client_account_num", "01030331130");
                params.put("req_client_fintech_use _num", fintech_num);
                params.put("sub_frnc_name", "이름");
                params.put("sub_frnc_num", "12345678");
                params.put("sub_frnc_business_num", "12345678");
                params.put("recv_client_name", "이불");
                params.put("recv_client_bank_code", "004");
                params.put("recv_client_account_num", "290302");

                JSONObject parameter = new JSONObject(params);
                RequestBody formBody = RequestBody.create(JSON, parameter.toString());
                
                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + access_token)
                        .url(url)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(callback);



            }
        }




    }

    /** 계좌조회 **/
    public void transferDeposit(Callback callback, Context context) {

        String url = "https://openapi.openbanking.or.kr/v2.0/transfer/deposit/acnt_num";

        String user_me_body = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("user_me", "");
        String access_token = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("access_token", "");
        int pos = 0;
        List<UserAccountItem> userAccountItemList = Utils.UserInfoResponseJsonParse(user_me_body);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("cntr_account_type", "N");
        params.put("cntr_account_num", "29030204202663");
        params.put("wd_pass_phrase", "");
        params.put("wd_print_content", "오픈");
        params.put("name_check_option", "off");
        params.put("sub_frnc_name", "");
        params.put("sub_frnc_num", "");
        params.put("sub_frnc_business_num", "");
        params.put("tran_dtime", Utils.getCurrentTime());
        params.put("req_cnt", "1");


        JSONObject ob = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            ob.put("tran_no", "1");
            ob.put("bank_tran_id", Utils.makeBankTranId());
            ob.put("bank_code_std", "004");
            ob.put("account_num", "29030204202663");
            ob.put("account_holder_name", "이정");
            ob.put("print_content", "오픈서비스캐시");
            ob.put("tran_amt", "500");
            ob.put("req_client_name", "이정환");
            ob.put("req_client_bank_code", "004");
            ob.put("req_client_account_num", "01030331130");
            ob.put("req_client_fintech_use_num", "");
            ob.put("req_client_num", "HONG1234");
            ob.put("transfer_purpose", "TR");

            arr.put(ob);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        params.put("req_list", arr.toString());


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);


    }

    /** 센터인증토큰 요청 **/
    public void requestCenterToken(Callback callback) {


        String url = "https://testapi.openbanking.or.kr/oauth/2.0/token";


        RequestBody formBody = new FormBody.Builder()
                .add("client_id", "X20kit9iRk7IGfFHbGukTXCxGGPPP6wSLie0OVgu")
                .add("client_secret", "2YWjBZZJGUg5MjBfbP969sXlixByKp7TE704nYI6")
                .add("scope", "oob")
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);

    }

    /** 사용자 실명조회 **/
    public void requestRealName(Callback callback) {


        String url = "https://testapi.openbanking.or.kr/v2.0/inquiry/real_name";


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bank_tran_id", Utils.makeBankTranId());
        params.put("bank_code_std", "004");
        params.put("account_num", "29030204202663");
        params.put("account_holder_info_type", " ");
        params.put("account_holder_info", "901130");
        params.put("tran_dtime", DateTimeUtils.getCurrentTime());


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        Request request = new Request.Builder()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJUOTkxNTk2OTIwIiwic2NvcGUiOlsib29iIl0sImlzcyI6Imh0dHBzOi8vd3d3Lm9wZW5iYW5raW5nLm9yLmtyIiwiZXhwIjoxNTg2ODU3NDQ0LCJqdGkiOiI3YzdhMDE3OS1iOGU3LTQxYTEtOTBkZi0xOWVhMzlkOTI0MWQifQ.86REUWV7ImJXWr7FtiayZclAmIW_4WO37s5tujR-UXI")
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);

    }

    /** 사용자 토큰 갱신 **/
    public void requestAccessTokenFromRefreshToken(Callback callback, String refresh_token) {


        String url = "https://testapi.openbanking.or.kr/oauth/2.0/token";


        RequestBody formBody = new FormBody.Builder()
                .add("client_id", "X20kit9iRk7IGfFHbGukTXCxGGPPP6wSLie0OVgu")
                .add("client_secret", "2YWjBZZJGUg5MjBfbP969sXlixByKp7TE704nYI6")
                .add("refresh_token", refresh_token)
                .add("scope", "login inquiry transfer")
                .add("grant_type", "refresh_token")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);
    }

    /** 오픈뱅킹 등록계좌 삭제 **/
    public void requestAccountCancel(Callback callback,Context context){
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        String fintech_num = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("fintech_num","");


        String url = "https://testapi.openbanking.or.kr/v2.0/account/cancel";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bank_tran_id", Utils.makeBankTranId());
        params.put("scope", "inquiry transfer");
        params.put("fintech_use_num", fintech_num);


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        Request request = new Request.Builder()
                .header("Authorization", "Bearer "+access_token)
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);


    }

    /** 등록계좌 조회 **/

    public void requestAccountList(Callback callback, Context context){
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        String user_seq_num = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("user_seq_num","");

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://testapi.openbanking.or.kr/v2.0/account/list").newBuilder();
        urlBuilder.addQueryParameter("user_seq_no", user_seq_num);
        urlBuilder.addQueryParameter("include_cancel_yn", "N");
        urlBuilder.addQueryParameter("sort_order", "D");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .url(url)
                .build();

        Log.d("오픈뱅킹", "사용자정보요청 get   " + request.toString());

        client.newCall(request).enqueue(callback);



    }
    /** 계좌명 변경 **/

    public void requestChangeAccountName(Context context,Callback callback,String fintech_num,String new_account_name){

        //String fintech_num = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("fintech_num","");
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        String url = "https://testapi.openbanking.or.kr/v2.0/account/update_info";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();

        params.put("fintech_use_num", fintech_num);
        params.put("account_alias",new_account_name );


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        Request request = new Request.Builder()
                .header("Authorization", "Bearer "+access_token)
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);


    }


    /** 계좌삭제 **/
    public void requestAccountClose(Context context,Callback callback){
        String url = "https://testapi.openbanking.or.kr/v2.0/user/close";
        String access_token = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("access_token","");
        String user_seq_num = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("user_seq_num","");
        String client_use_code = context.getSharedPreferences("OpenBanking",MODE_PRIVATE).getString("center_id","");

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_use_code", client_use_code);
        params.put("user_seq_num", user_seq_num);


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        Request request = new Request.Builder()
                .header("Authorization", "Bearer "+access_token)
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);



    }


}
