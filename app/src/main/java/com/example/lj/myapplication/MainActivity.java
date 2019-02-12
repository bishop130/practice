package com.example.lj.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String userId;
    String Platform;
    private StringRequest request;
    private StringRequest requestCheck;
    double Latitude;
    double Longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = true;
    private static final String goURL = "http://bishop130.cafe24.com/Mission_Control.php";
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private RequestQueue requestQueue;
    private TextView mTextViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.lj.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        mTextViewResult = (TextView) findViewById(R.id.textView4);

        Button button = (Button) findViewById(R.id.btnGoogleMap);
        Button gpsBtn = (Button) findViewById(R.id.btnGPS);
        Button placeBtn = (Button) findViewById(R.id.goPlace);
        Button calendarBtn = (Button) findViewById(R.id.goCalendar);
        Button goWebView = (Button) findViewById(R.id.webView);
        goWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(intent);
            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> keys = new ArrayList<>();
                keys.add("properties.nickname");
                keys.add("properties.profile_image");
                keys.add("kakao_account.email");

                UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Toast.makeText(getApplicationContext(), "세션닫힘", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Logger.d("UserProfile : " + result);
                        userId = String.valueOf(result.getId());
                        Platform = "Kakao";
                    }
                });


                try {
                    if (mLocationPermissionsGranted) {

                        final Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Location currentLocation = (Location) task.getResult();
                                    Latitude = currentLocation.getLatitude();
                                    Longitude = currentLocation.getLongitude();


                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


                                } else {
                                    Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (SecurityException e) {
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");//현재시간
                Calendar nowdate = Calendar.getInstance();
                final String curTime = sdf.format(nowdate.getTime());


                requestCheck = new StringRequest(Request.Method.POST, goURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), "전송완료", Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("userId", userId);
                        hashMap.put("Latitude", String.valueOf(Latitude));
                        hashMap.put("Longitude", String.valueOf(Longitude));
                        hashMap.put("CurrentTime", curTime);
                        hashMap.put("MissionID", "11");

                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DaumMapActivity.class);
                startActivity(intent);
            }
        });
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RecyclerActivity.class);
                startActivity(intent);

            }
        });
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
                .check();




/*
            request = new StringRequest(Request.Method.POST, Mission_List_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(getApplicationContext(),"전송완료",Toast.LENGTH_LONG).show();

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
                    hashMap.put("UserID",userId);

                    return hashMap;
                }
            };
            requestQueue.add(request);*/


    }

    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, Mission_List_URL, (String) null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        mTextViewResult.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        mTextViewResult.setText("Response: " + error);
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }



}



