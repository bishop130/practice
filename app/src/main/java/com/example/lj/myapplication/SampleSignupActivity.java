package com.example.lj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

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
    }
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

    private void setLayoutText(){
        id.setText(userId);
        name.setText(userName);

        Picasso.with(this)
                .load(profileUrl)
                .fit()
                .into(profile_image);
    }
}
