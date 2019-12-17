package com.suji.lj.myapplication.StepperForm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.OpenBankingActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.NumberTextWatcher;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ernestoyaquello.com.verticalstepperform.Step;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MakeAccountStep extends Step<String> implements TextWatcher{

    public static Context context;
    String access_token;

    OpenBanking openBanking = OpenBanking.getInstance();
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    //TextView register_account;
    TextView bank_name_tv;
    TextView account_num_tv;
    //TextView nick_name;
    TextView friends_num;
    TextView transfer_amount;
    String user_seq_num;
    String bank_name;
    String account_num;

    NumberTextWatcher numberTextWatcher;
    int contact_num;
    int amount;



    private boolean hasFractionalPart;
    private int friends_amount;

    private TextInputEditText et;
    private TextInputLayout etl;





    // 세자리로 끊어서 쉼표 보여주고, 소숫점 셋째짜리까지 보여준다.
    //DecimalFormat df = new DecimalFormat("###,###.####");
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    // 값 셋팅시, StackOverFlow를 막기 위해서, 바뀐 변수를 저장해준다.
    String result="";



    private final Callback user_account_info_callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.d(TAG, "콜백오류:" + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            //Log.d(TAG, "서버에서 응답한 Body:" + body);

            SharedPreferences.Editor editor = getContext().getSharedPreferences("OpenBanking",MODE_PRIVATE).edit();
            editor.putString("user_me",body);
            editor.apply();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            Log.d("오픈뱅킹","핀테크응답결과 "+ body);

            userAccountItemList = Utils.UserInfoResponseJsonParse(body);
            displayBankAccount(userAccountItemList);

        }
    };


    public MakeAccountStep(String title) {
        super(title," ");
    }

    public MakeAccountStep(String title, String subtitle) {
        super(title, subtitle);
    }

    public MakeAccountStep(String title, String subtitle, String nextButtonText) {
        super(title, subtitle, nextButtonText);
    }

    @Override
    public String getStepData() {


        String result="a";
        return result;
    }

    @Override
    public String getStepDataAsHumanReadableString() {

        return bank_name+"  "+account_num+"\n"+df.format(contact_num*amount)+" 원";
    }

    @Override
    public void restoreStepData(String data) {

    }

    @Override
    public IsDataValid isStepDataValid(String stepData) {
        Log.d("오픈뱅킹","여기에 isDataValid"+etl.isErrorEnabled());

        if(etl.isErrorEnabled()){
            return new IsDataValid(false);
        }
        else{
            return new IsDataValid(true);
        }


       // return new IsDataValid(true);

    }

    @Override
    protected View createStepContentLayout() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_account, null, false);
        //register_account = view.findViewById(R.id.register_account);
        bank_name_tv = view.findViewById(R.id.bank_name);
        account_num_tv = view.findViewById(R.id.account_num);
        //nick_name = view.findViewById(R.id.nick_name);
        friends_num = view.findViewById(R.id.friends_num);
        transfer_amount = view.findViewById(R.id.transfer_amount);

        etl= view.findViewById(R.id.text_input_layout);
        et = view.findViewById(R.id.transfer_input);

        //numberTextWatcher = new NumberTextWatcher(textInputEditText,textInputLayout,contact_num,transfer_amount,context);


        et.addTextChangedListener(this);
        etl.setHintTextColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.mdtp_accent_color_dark)));
        etl.setDefaultHintTextColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.mdtp_accent_color)));


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("OpenBanking", MODE_PRIVATE);
        access_token = sharedPreferences.getString("access_token","");
        user_seq_num = sharedPreferences.getString("user_seq_num","");
        Log.d("오픈뱅킹","사용자토큰"+access_token);

        if(access_token.equals("")){
            //register_account.setText("처음사용자");
            Log.d("오픈뱅킹","처음사용자");
        }else{
            Log.d("오픈뱅킹","기사용자정보요청");
            openBanking.requestUserAccountInfo(user_account_info_callback,access_token,user_seq_num);


        }


        return view;
    }

    @Override
    protected void onStepOpened(boolean animated) {

        Log.d("오픈뱅킹","여기에 onStepOpened");



        contact_num = getContext().getSharedPreferences("sFile",MODE_PRIVATE).getInt("contact_num",0);
        friends_num.setText(contact_num+" 명");

        transfer_amount.setText(df.format(contact_num*Integer.valueOf(et.getText().toString().replaceAll(",", "")))+" 원");
        amount = Integer.valueOf(Integer.valueOf(et.getText().toString().replaceAll(",", "")));
        /*
        register_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), OpenBankingActivity.class);
                getContext().startActivity(intent);





            }
        });

         */
        etl.setHint("금액을 입력하세요.");



    }

    @Override
    protected void onStepClosed(boolean animated) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        SharedPreferences.Editor editor = getContext().getSharedPreferences("OpenBanking",MODE_PRIVATE).edit();
        editor.putString("transfer_amount", String.valueOf(contact_num * amount));
        editor.apply();


    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }
    private void displayBankAccount(List<UserAccountItem> userAccountItemList){

        String user_name = userAccountItemList.get(0).getUser_name();
        String res_cnt = userAccountItemList.get(0).getRes_cnt();
        String fintech_num = userAccountItemList.get(0).getFintech_use_num();
        bank_name = userAccountItemList.get(0).getBank_name();
        account_num = userAccountItemList.get(0).getAccount_num_masked();


        bank_name_tv.setText(bank_name);
        account_num_tv.setText(account_num);
        //nick_name.setText(userAccountItemList.get(0).getAccount_alias());



        Log.d("오픈뱅킹","리스트응답결과 "+ user_name+"\n"+res_cnt+"\n"+fintech_num);

    }

    public List<UserAccountItem> getUserAccountItemList(){
        return userAccountItemList;

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d("오픈뱅킹","before_changed");

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("오픈뱅킹","on_changed");

        if(s.toString().isEmpty()){
            etl.setError("최소 3천원부터 입력됩니다.");
            etl.setErrorEnabled(true);
            Log.d("오픈뱅킹", "트랜스퍼 empty    " + etl.isErrorEnabled());

        }else {
            amount = Integer.valueOf(s.toString().replaceAll(",", ""));


            if (amount > 100000) {
                etl.setError("최대 10만원까지 입력됩니다.");
                etl.setErrorEnabled(true);
                etl.setBoxStrokeColor(getContext().getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "10만원 이상    " + etl.isErrorEnabled() );
            } else if (amount < 3000) {
                etl.setError("최소 3천원부터 입력됩니다.");
                etl.setErrorEnabled(true);
                etl.setBoxStrokeColor(getContext().getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "3천원    " + etl.isErrorEnabled() );
            } else {
                etl.setError(null);
                etl.setErrorEnabled(false);
                etl.setBoxStrokeColor(getContext().getResources().getColor(R.color.blue));


                Log.d("오픈뱅킹", "정상    " + etl.isErrorEnabled() );



            }
        }






        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
        markAsCompletedOrUncompleted(true);
        transfer_amount.setText(df.format(contact_num*amount)+" 원");


    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("오픈뱅킹","after_changed");

        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = et.getSelectionStart();
            if (hasFractionalPart) {
                et.setText(df.format(n));
            } else {
                et.setText(dfnd.format(n));
            }
            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);

    }
}
