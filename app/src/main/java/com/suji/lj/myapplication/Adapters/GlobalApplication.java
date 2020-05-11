package com.suji.lj.myapplication.Adapters;


import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import android.app.Application;
import android.content.Context;

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return GlobalApplication.this;
                    }
                };
            }
            @Override
            public ISessionConfig getSessionConfig() {
                return new ISessionConfig() {
                    @Override
                    public AuthType[] getAuthTypes() {
                        return new AuthType[]{AuthType.KAKAO_TALK};
                    }

                    @Override
                    public boolean isUsingWebviewTimer() {
                        return false;
                    }

                    @Override
                    public boolean isSecureMode() {
                        return false;
                    }


                    @Override
                    public ApprovalType getApprovalType() {
                        return ApprovalType.INDIVIDUAL;
                    }

                    @Override
                    public boolean isSaveFormData() {
                        return true;
                    }
                };
            }


        });
    }


}



