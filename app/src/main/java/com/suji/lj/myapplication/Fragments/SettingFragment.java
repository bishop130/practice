package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.suji.lj.myapplication.AlarmSettingActivity;
import com.suji.lj.myapplication.AutoLocationSetting;
import com.suji.lj.myapplication.BuyCarrotActivity;
import com.suji.lj.myapplication.FAQActivity;
import com.suji.lj.myapplication.NewMissionActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SampleSignupActivity;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {

    private TextView kakao_name_setting;
    private ImageView profile_image_setting;
    private Context mContext;
    private TextView coin_result;
    private static final String display_coin_url = "http://bishop130.cafe24.com/refresh_coin.php";
    private RequestQueue requestQueue;
    private StringRequest request;
    private LinearLayout buy_carrot_go;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        kakao_name_setting = view.findViewById(R.id.kakao_name_setting);
        profile_image_setting = view.findViewById(R.id.profile_image_setting);
        coin_result = view.findViewById(R.id.coins);
        LinearLayout alarm_setting_go =  view.findViewById(R.id.alarm_setting_go);
        LinearLayout auto_register_location = view.findViewById(R.id.auto_register_location);
        LinearLayout go_to_my_profile = view.findViewById(R.id.go_to_my_profile);
        LinearLayout faq = view.findViewById(R.id.faq);
        LinearLayout new_register = view.findViewById(R.id.new_register_go);
        buy_carrot_go = view.findViewById(R.id.buy_carrot_go);

        requestQueue = Volley.newRequestQueue(mContext);

        displayCoin();
        buy_carrot_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuyCarrotActivity.class));
            }
        });

        go_to_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SampleSignupActivity.class));
            }
        });

        alarm_setting_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), AlarmSettingActivity.class));
                Toast.makeText(getActivity(),"준비중입니다.",Toast.LENGTH_LONG).show();

            }
        });
        auto_register_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AutoLocationSetting.class));
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               startActivity(new Intent(getActivity(), FAQActivity.class));
            }
        });
        new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewMissionActivity.class));
            }
        });
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);
        requestProfile();
        return view;
    }



    private void requestProfile() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("kakao_profile",MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String profile_image = sharedPreferences.getString("profile_image","");

                kakao_name_setting.setText(name);
                Picasso.with(getActivity())
                        .load(profile_image)
                        .fit()
                        .into(profile_image_setting);

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    private void displayCoin() {
        String user_id = mContext.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        request = new StringRequest(Request.Method.POST, display_coin_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String coin = jsonObject.getString("coin");

                        coin_result.setText(coin);
                        Log.d("구매", "코인" + coin);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(mContext, "전송완료", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, "전송실패" + error, Toast.LENGTH_LONG).show();
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
