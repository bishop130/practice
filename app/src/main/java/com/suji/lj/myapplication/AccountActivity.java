package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suji.lj.myapplication.Adapters.AccountDialog;
import com.suji.lj.myapplication.Adapters.AccountManagementDialog;

import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.RecyclerAccountManagementAdapter;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAccountManagementAdapter adapter;
    OpenBanking openBanking = OpenBanking.getInstance();
    List<UserAccountItem> list = new ArrayList<>();

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();

            Log.d("계좌관리",body);
            //Log.d("계좌관리",);

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {

                list = Utils.AccountInfoResponseJsonParse(body);
                for(int i =0; i<list.size(); i++){

                    Log.d("계좌관리",list.get(i).getAccount_num_masked());
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("유아이", "in");
                        setRecyclerView(list);
                    }
                });


            }


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        recyclerView = findViewById(R.id.recycler_account);
        initiateTab();


        openBanking.requestAccountList(callback, this);


    }

    private void setRecyclerView(List<UserAccountItem> list) {

        adapter = new RecyclerAccountManagementAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }
    private void initiateTab() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        toolbar.setTitle("계좌관리");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void accountManagement(String bank_name,String account_num) {

        AccountManagementDialog accountDialog = new AccountManagementDialog(this);
/*
        accountDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //accountDialog.setContentView(R.layout.fragment_account_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(accountDialog.getWindow().getAttributes());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lp.width = (int) (metrics.widthPixels * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.3f;


        accountDialog.getWindow().setAttributes(lp);
        accountDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded));



 */
        accountDialog.show();




        //openBanking.requestAccountCancel(account_cancel_callback, this);


    }

    private void refreshAccountList() {
        openBanking.requestAccountList(callback, this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);



    }
}
