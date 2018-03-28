package com.example.lj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    String result;
    String Lat;
    String Lng;
    String year,month,day,hour,min,userId;
    private StringRequest request;
    private static final String URL = "http://bishop130.cafe24.com/test.php";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        requestQueue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");
        hour = intent.getStringExtra("hour");
        min = intent.getStringExtra("min");
        Lat = intent.getStringExtra("Lat");
        Lng = intent.getStringExtra("Lng");
        userId = intent.getStringExtra("userId");

        result = "year:"+year+"\nmonth: "+month+"\nday: "+day+"\nhour: "+hour+"\nmin: "+min+"\nLat: "+Lat+"\nLng: "+Lng+"\nuserId: "+userId;

        Button button = (Button)findViewById(R.id.Btn_Register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(),"전송완료",Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("Lat",Lat);
                        hashMap.put("Lng",Lng);
                        hashMap.put("userId",userId);
                        hashMap.put("password","bishop130");

                        return hashMap;
                    }
                };
                requestQueue.add(request);

            }
        });




        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(result);
    }
}
