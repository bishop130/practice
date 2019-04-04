package com.example.lj.myapplication.Fragments;

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

import com.example.lj.myapplication.AlarmSettingActivity;
import com.example.lj.myapplication.AutoLocationSetting;
import com.example.lj.myapplication.Items.RecyclerItem;
import com.example.lj.myapplication.MainActivity;
import com.example.lj.myapplication.R;
import com.example.lj.myapplication.SampleLoginActivity;
import com.example.lj.myapplication.SampleSignupActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {

    private TextView kakao_name_setting;
    private ImageView profile_image_setting;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        kakao_name_setting = view.findViewById(R.id.kakao_name_setting);
        profile_image_setting = view.findViewById(R.id.profile_image_setting);
        LinearLayout alarm_setting_go =  view.findViewById(R.id.alarm_setting_go);
        LinearLayout auto_register_location = view.findViewById(R.id.auto_register_location);
        LinearLayout go_to_my_profile = view.findViewById(R.id.go_to_my_profile);

        go_to_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SampleSignupActivity.class));
            }
        });


        alarm_setting_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AlarmSettingActivity.class));

            }
        });
        auto_register_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AutoLocationSetting.class));
            }
        });
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);
        requestAccessTokenInfo();
        requestProfile();
        return view;
    }

    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);
                Toast.makeText(getActivity(), "세션닫힘", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), SampleLoginActivity.class);
                startActivity(intent);


                kakao_name_setting.setText("로그인 해주세요");
                /*
                LinearLayout kakao_login = (LinearLayout) dialog.findViewById(R.id.kakao_layout);
                kakao_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "로그인 확인좀", Toast.LENGTH_LONG).show();


                    }
                });
                */
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getActivity(), "로그인해주세요", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), SampleLoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                final String userId = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("토큰확인", String.valueOf(userId));


            }
        });


    }


    private void requestProfile() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("kakao_profile",MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String profile_image = sharedPreferences.getString("profile_image","");



                kakao_name_setting.setText(name);
                Picasso.with(getActivity())
                        .load(profile_image)
                        .fit()
                        .into(profile_image_setting);



    }

    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }
    }


}
