package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BuyCarrotActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor bp;
    private static final String merchant_id = "07006820734219471709";
    private static final String url = "http://bishop130.cafe24.com/update_coin.php";
    private static final String display_coin_url = "http://bishop130.cafe24.com/refresh_coin.php";
    private static final String consumeURL = "http://bishop130.cafe24.com/consume_coin.php";
    RequestQueue requestQueue;
    StringRequest request;
    LinearLayout purchase_carrot;
    TextView rest_carrot;
    Button test_consume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_carrot);
        requestQueue = Volley.newRequestQueue(this);
        displayCoin();

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgRyqiV6L" +
                "DIye4rS+ogOYXutxMto8ovzlaj95cAZVNbDTVO3eVg0XfYPZID2nr8fWoJ3qX384PQBGpg7UFeLcXjQ8sxpAffxHtfK8x5" +
                "KD65D/GW+JvkM++hJTI8qwPBlOgIrtOLL73fNNBRSbL4s36uZ8q2vfl2pef81YNRRumBlFcCBpbQg8RAjL8Q2N42ufnBUTcsI" +
                "CXQzdR+pnp0+J4YZXD/ZbVItdRg3OV9fwhpZ0i/O395MjLcr62QPEbdXs9HEZYipzrfyIm6NrNL/qdOsO2ff8ehwMoY8kE" +
                "1bsUPcQIccgdNgWj0LI20dW4vBa8anLiMw8gg1+J8pi1nf+PQIDAQAB", merchant_id, this);
        rest_carrot = findViewById(R.id.rest_carrot);
        test_consume = findViewById(R.id.consume_test);

        purchase_carrot = findViewById(R.id.purchase);
        purchase_carrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseProduct();

            }
        });
        test_consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                consumeCoinVolleyConnect();
            }
        });


    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        boolean isValid = bp.isValidTransactionDetails(details);
        Log.d("구매", bp.isValidTransactionDetails(details) + "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("구매 성공 하였습니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // to do action
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        Toast.makeText(getApplicationContext(), "구매에 성공했습니다." + productId + "   " + details, Toast.LENGTH_LONG).show();
        Log.d("구매", productId);
        Log.d("구매", details.toString());
        if (isValid) {
            updateCoin("100");
        }


    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    public void purchaseProduct() {

        // 지속적인 구매를 위해 구매한 아이템을 소비하는 것이다. 예) 게임 아이템
        // 구매하였으면 소비하여 없앤 후 다시 구매하게 하는 로직.
        // 만약 한번 구매 후 계속 이어지게 할 것이면 아래 if문은 사용하지 않아야 한다.
        if (bp.isPurchased("test_coin")) {
            bp.consumePurchase("test_coin");
        }
        bp.consumePurchase("test_coin");

        // 실질적인 구매 호출 함수
        // productId는 사전 작업시 등록한 인앱 상품 아이디임. 예) a1000
        // 딸랑 한줄.. -_-;; 사전 작업의 구글 콘솔 앱출시 셋팅에 비해 허무함.(라이브러리 정말 편한듯...)
        bp.purchase(this, "test_coin");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // onActivityResult 부분이 없을시 구글 인앱 결제창이 동시에 2개가 나타나는 현상이 발생하였고 (원인은 잘 모름)
        // 해당 부분이 있는 경우에는 그런 현상이 발생되지 않았음.
        if (bp.handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    private void updateCoin(String coin) {
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        Log.d("구매", "코인인풋" + coin);
        Log.d("구매", user_id);
        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                displayCoin();


                Toast.makeText(getApplicationContext(), "전송완료", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", user_id);
                hashMap.put("coin", coin);

                return hashMap;
            }
        };
        requestQueue.add(request);


    }
    private void consumeCoinVolleyConnect(){
        String userId = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");

        request = new StringRequest(Request.Method.POST, consumeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("코인",response);

                if(response.equals("666")){
                    //잔액이 부족합니다.
                    Log.d("코인",response+"들어옴"+5*7);
                    //Toast.makeText(getApplicationContext(),"코인이 부족합니다.",Toast.LENGTH_LONG).show();
                    Toasty.warning(getApplicationContext(),"코인이 부족합니다.",Toasty.LENGTH_LONG,true).show();
                }else{
                    Log.d("코인",response+"들어오지"+5*7);
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toasty.error(getApplicationContext(),"전송실패",Toasty.LENGTH_LONG,true).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", userId);
                hashMap.put("consume_coin",String.valueOf(5*7));


                return hashMap;
            }
        };
        requestQueue.add(request);

    }


    private void displayCoin() {
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        request = new StringRequest(Request.Method.POST, display_coin_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String coin = jsonObject.getString("coin");

                        rest_carrot.setText(coin);
                        Log.d("구매", "코인" + coin);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getApplicationContext(), "전송완료", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", user_id);

                return hashMap;
            }
        };
        requestQueue.add(request);


    }

}
