package com.example.lj.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.myapplication.Fragments.HomeFragment;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
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

    private String nickName;
    private String profileImageURL;
    private String thumbnailURL;
    private String countryISO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_signup);

        name=findViewById(R.id.kakao_name);
        id = findViewById(R.id.kakao_name_detail);
        profile_image = findViewById(R.id.profile_image);
        LinearLayout unlink_button = findViewById(R.id.kakao_unlink);
        unlink_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUnlink();
            }
        });
        LinearLayout kakao_logout_button = findViewById(R.id.kakao_logout);
        kakao_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Toast.makeText(getApplicationContext(),"로그아웃 완료",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        requestProfile();
        setLayoutText();

    }
    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                // redirectLoginActivity(self);
                Toast.makeText(getApplicationContext(),"세션닫힘",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getApplicationContext(),"로그인해주세요",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                String userId = String.valueOf(accessTokenInfoResponse.getUserId());
                Log.d("토큰확인" ,String.valueOf(userId));
                requestProfile();

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Log.d("토큰 만료 됨 " , expiresInMilis + "이후에");
            }
        });
    }
    public void requestProfile() {
        KakaoTalkService.getInstance().requestProfile(new KakaoTalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onSuccess(KakaoTalkProfile talkProfile) {
                nickName = talkProfile.getNickName();
                profileImageURL = talkProfile.getProfileImageUrl();
                thumbnailURL = talkProfile.getThumbnailUrl();
                countryISO = talkProfile.getCountryISO();

                Log.d("카톡프로필",nickName+"\n"+profileImageURL+"\n"+thumbnailURL+"\n"+countryISO);
            }
        });
    }
    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Toast.makeText(getApplicationContext(),"카카오톡 유저가 아닙니다.",Toast.LENGTH_LONG).show();
            Log.d("카카오톡","not a KakaoTalk user");
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
        id.setText(countryISO);
        name.setText(nickName);

        Picasso.with(this)
                .load(thumbnailURL)
                .fit()
                .into(profile_image);
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        finish();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }
}
