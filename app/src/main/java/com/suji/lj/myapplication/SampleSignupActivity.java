package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.common.internal.service.Common;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SampleSignupActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler

{
    private TextView kakao_name_setting;
    private ImageView profile_image_setting;


    private BillingProcessor bp;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_signup);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgRyqiV6L" +
                "DIye4rS+ogOYXutxMto8ovzlaj95cAZVNbDTVO3eVg0XfYPZID2nr8fWoJ3qX384PQBGpg7UFeLcXjQ8sxpAffxHtfK8x5" +
                "KD65D/GW+JvkM++hJTI8qwPBlOgIrtOLL73fNNBRSbL4s36uZ8q2vfl2pef81YNRRumBlFcCBpbQg8RAjL8Q2N42ufnBUTcsI" +
                "CXQzdR+pnp0+J4YZXD/ZbVItdRg3OV9fwhpZ0i/O395MjLcr62QPEbdXs9HEZYipzrfyIm6NrNL/qdOsO2ff8ehwMoY8kE" +
                "1bsUPcQIccgdNgWj0LI20dW4vBa8anLiMw8gg1+J8pi1nf+PQIDAQAB", this);
        bp.initialize();

        kakao_name_setting = findViewById(R.id.kakao_name);
        profile_image_setting = findViewById(R.id.profile_image);
        profile_image_setting.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image_setting.setClipToOutline(true);


        LinearLayout unlink_button = findViewById(R.id.kakao_unlink);
        LinearLayout kakao_logout_button = findViewById(R.id.kakao_logout);
        requestProfile();


        unlink_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUnlink();
            }
        });

        kakao_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_LONG).show();
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        startActivity(new Intent(SampleSignupActivity.this,MainActivity.class));
                        finish();
                    }
                });
            }
        });

    }

    private void requestProfile() {

        SharedPreferences sharedPreferences = getSharedPreferences("kakao_profile", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String profile_image = sharedPreferences.getString("profile_image", "");

        kakao_name_setting.setText(name);
        Picasso.with(this)
                .load(profile_image)
                .fit()
                .into(profile_image_setting);


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
                                        startActivity(new Intent(SampleSignupActivity.this,SampleLoginActivity.class));
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
                                Intent intent = new Intent (SampleSignupActivity.this,MainActivity.class);
                                startActivity(intent);

                            }
                        }).show();

    }



    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {



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
    public void purchaseProduct(final String productId) {

        // 지속적인 구매를 위해 구매한 아이템을 소비하는 것이다. 예) 게임 아이템
        // 구매하였으면 소비하여 없앤 후 다시 구매하게 하는 로직.
        // 만약 한번 구매 후 계속 이어지게 할 것이면 아래 if문은 사용하지 않아야 한다.
      /*if(mBillingProcessor.isPurchased(productId)){
            mBillingProcessor.consumePurchase(productId);
      }*/

        // 실질적인 구매 호출 함수
        // productId는 사전 작업시 등록한 인앱 상품 아이디임. 예) a1000
        // 딸랑 한줄.. -_-;; 사전 작업의 구글 콘솔 앱출시 셋팅에 비해 허무함.(라이브러리 정말 편한듯...)
        bp.purchase(this, "test_coin");
    }
}
