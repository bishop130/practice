package com.example.lj.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    String result;
    String Lat;
    String Lng;
    String year,month,day,hour,min,userId,CurrentTime;
    private StringRequest request;
    private static final String goURL = "http://bishop130.cafe24.com/test.php";
    private RequestQueue requestQueue;
    private EditText MissionTitle;
    String mission = "77";
    String mJsonString;
    String setDate;
    String missionTitle;


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
        CurrentTime = intent.getStringExtra("CurrentTime");

        if(Integer.parseInt(month)<10){
            month=String.format("%02d",Integer.parseInt(month));
            month = String.valueOf(month);
        }
        if(Integer.parseInt(day)<10){
            day = String.format("%02d",Integer.parseInt(day));
            day = String.valueOf(day);
        }
        setDate=year+month+day+hour+min;

        result = "year:"+year+"\nmonth: "+month+"\nday: "+day+"\nhour: "+hour+"\nmin: "+min+"\nLat: "+Lat+"\nLng: "+Lng+"\nuserId: "+userId+"\nCurrentTime:"+CurrentTime+"\nsetDate: "+setDate;

        MissionTitle = (EditText)findViewById(R.id.MissionTitle);


        Button button = (Button)findViewById(R.id.Btn_Register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, goURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(),"전송완료"+missionTitle,Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"전송실패"+error,Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("Lat",Lat);
                        hashMap.put("Lng",Lng);
                        hashMap.put("userId",userId);
                        hashMap.put("MissionID",CurrentTime+userId);
                        hashMap.put("MissionTime",setDate);
                        hashMap.put("FriendsNum","0106319610");
                        hashMap.put("MissionTitle",MissionTitle.getText().toString());

                        return hashMap;
                    }
                };
                requestQueue.add(request);

            }
        });






        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(result);
    }/*
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);


            }

        } catch (JSONException e) {

        }

    }
    */



}
