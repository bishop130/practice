package com.example.lj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleSignupActivity extends AppCompatActivity {
    private TextView id;
    private TextView name;
    private ImageView profile_image;

    private String userName;
    private String userId;
    private String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_signup);

        id = (TextView)findViewById(R.id.id);
        name=(TextView)findViewById(R.id.name);
        profile_image = (ImageView)findViewById(R.id.profile_image);
        requestMe();
        requestUpdateProfile();
    }

    private void requestUpdateProfile() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("nickname", "Leo");
        properties.put("age", "33");

        UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long>() {
            @Override
            public void onSuccess(Long userId) {
                Toast.makeText(SampleSignupActivity.this, "새로받은 userID"+userId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.e(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

        }, properties);
    }

    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onSuccess(MeV2Response result) {
                profileUrl = result.getProfileImagePath();
                userId = String.valueOf(result.getId());
                userName = result.getNickname();
                setLayoutText();

            }

            @Override
            public void onFailure(ErrorResult errorResult){

            }

        });
    }

    /*
    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();

                setLayoutText();

                
            }

            @Override
            public void onNotSignedUp() {
            }
        });
    }
    */

    private void setLayoutText(){
        id.setText(userId);
        name.setText(userName);

        Picasso.with(this)
                .load(profileUrl)
                .fit()
                .into(profile_image);
    }
}
