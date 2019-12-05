package com.suji.lj.myapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.suji.lj.myapplication.StepperForm.MakeAccountStep;

import java.text.DecimalFormat;
import java.text.ParseException;

public class NumberTextWatcher implements TextWatcher {

    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;
    private int friends_num;

    private TextInputEditText et;
    private TextInputLayout etl;
    private String transfer_amount;
    private Context context;
    private TextView tv_amount;

    private boolean isErrorEnable = false;
    private MakeAccountStep makeAccountStep = new MakeAccountStep("계좌확인");


    public NumberTextWatcher(TextInputEditText et,TextInputLayout etl,int friends_num, TextView tv_amount,Context context)
    {
        df = new DecimalFormat("#,###.##");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###");
        this.et = et;
        this.etl = etl;
        this.friends_num = friends_num;
        this.tv_amount = tv_amount;
        hasFractionalPart = false;
        this.context = context;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    public void afterTextChanged(Editable s)
    {
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

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {


        if(s.toString().isEmpty()){
            etl.setError("최소 3천원부터 입력됩니다.");
            etl.setErrorEnabled(true);
            Log.d("오픈뱅킹", "트랜스퍼 empty    " + etl.isErrorEnabled());

        }else {
            int amount = Integer.valueOf(s.toString().replaceAll(",", ""));


            if (amount > 100000) {
                etl.setError("최대 10만원까지 입력됩니다.");
                etl.setErrorEnabled(true);
                isErrorEnable = true;
                Log.d("오픈뱅킹", "10만원 이상    " + etl.isErrorEnabled() );
            } else if (amount < 3000) {
                etl.setError("최소 3천원부터 입력됩니다.");
                etl.setErrorEnabled(true);
                isErrorEnable = true;

                Log.d("오픈뱅킹", "3천원    " + etl.isErrorEnabled() );
            } else {
                etl.setError(null);
                etl.setErrorEnabled(false);
                isErrorEnable = false;

                Log.d("오픈뱅킹", "정상    " + etl.isErrorEnabled() );


                tv_amount.setText(df.format(amount*friends_num));


            }
        }



        makeAccountStep.markAsCompletedOrUncompleted(true);


        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }

    public int getTransferAmount(){

        //SharedPreferences sharedPreferences = context.getSharedPreferences("sFile",Context.MODE_PRIVATE);
        //sharedPreferences.getString("contact_num","");
        //return Double.valueOf(transfer_amount.replaceAll(",", ""));

        return 10;

    }

    public boolean isErrorEnable(){

        return isErrorEnable;
    }
}