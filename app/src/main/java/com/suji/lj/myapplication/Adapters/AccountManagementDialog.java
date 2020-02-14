package com.suji.lj.myapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountManagementDialog extends BottomSheetDialog {
    Context context;
    TextView account_num;
    LinearLayout ly_change_account_name;
    LinearLayout ly_account_cancel;
    TextView dialog_cancel;
    OpenBanking openBanking = OpenBanking.getInstance();
    private Callback account_cancel_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().toString();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {
                dismiss();


            }


        }
    };
    private Callback request_change_account_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().toString();
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {
                dismiss();
            }
        }
    };

    public AccountManagementDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_account_management);
        ly_change_account_name = findViewById(R.id.change_account_name);
        ly_account_cancel = findViewById(R.id.account_cancel);
        dialog_cancel = findViewById(R.id.dialog_cancel);
        ly_change_account_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNewAccountName();
            }
        });
        ly_account_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(getContext());

                alt_bld.setTitle("닉네임 변경")
                        .setMessage("변경할 닉네임을 입력하세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openBanking.requestAccountCancel(account_cancel_callback, context);
                            }
                        });

            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void editNewAccountName() {

        Toast.makeText(getContext(), "준비중입니다.", Toast.LENGTH_LONG).show();
        //openBanking.requestChangeAccountName(this, request_change_account_callback, )
    }
}
