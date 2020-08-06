package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.RecyclerAccountListAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerBankSelectionAdapter;
import com.suji.lj.myapplication.Items.ItemForAccountList;
import com.suji.lj.myapplication.Items.ItemForBank;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SecurityActivity;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WithDrawFragment extends Fragment implements RecyclerBankSelectionAdapter.OnSetupBankListener, RecyclerAccountListAdapter.OnCompleteAccountListener {

    EditText et_account;
    EditText et_withdraw;
    EditText et_account_holder;
    LinearLayout ly_select_bank;
    TextView tv_bank_name;
    Activity activity;
    TextView tv_account_input;
    TextView tv_account_holder;
    TextView tv_withdraw;
    TextView done;
    TextView tv_recent_account;
    String bank_code;
    TextView tv_point;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView recycler_recent_account;


    Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


            Log.d("부트페이", response.body().string());


        }
    };

    InputFilter filterKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    private boolean hasFractionalPart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);


        et_account = view.findViewById(R.id.et_account);
        et_withdraw = view.findViewById(R.id.et_withdraw);
        ly_select_bank = view.findViewById(R.id.ly_select_bank);
        tv_bank_name = view.findViewById(R.id.tv_bank_name);
        et_account_holder = view.findViewById(R.id.et_account_holder);
        tv_account_input = view.findViewById(R.id.tv_account_input);
        tv_account_holder = view.findViewById(R.id.tv_account_holder);
        tv_withdraw = view.findViewById(R.id.tv_withdraw);
        tv_recent_account = view.findViewById(R.id.tv_recent_account);
        tv_point = view.findViewById(R.id.tv_point);
        done = view.findViewById(R.id.tv_done);


        displayPoint();


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_id = Account.getUserId(activity);


                String bank_name = tv_bank_name.getText().toString();
                String account_num = et_account.getText().toString();
                String account_holder_name = et_account_holder.getText().toString();
                String amount = et_withdraw.getText().toString();

                Log.d("계좌", bank_name + "은행이름");
                Log.d("계좌", account_num + "계좌번호");
                Log.d("계좌", account_holder_name + "이름");
                Log.d("계좌", bank_code + "은행코드");


                if (isFilled()) {
                    Toast.makeText(activity, "모두 입력", Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(activity, SecurityActivity.class);
                    intent.putExtra("bank_name", bank_name);
                    intent.putExtra("bank_code", bank_code);
                    intent.putExtra("account_num", account_num);
                    intent.putExtra("account_holder_name", account_holder_name);
                    intent.putExtra("amount", amount);
                    intent.putExtra("password",2);


                    //theTest();


                    startActivity(intent);
                    //functionWithdraw(item);


                    //계좌인출 완료 후 진행


                }
            }
        });


        ly_select_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetBankSelection();
            }
        });
        tv_recent_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetRecentAccount();
            }
        });

        et_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    tv_account_input.setTextColor(activity.getResources().getColor(R.color.colorPrimary));


                } else {
                    tv_account_input.setTextColor(activity.getResources().getColor(R.color.colorDefault));
                }
            }
        });
        et_account.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {


                    et_account_holder.requestFocus();

                    Log.d("에디트", v.getText().toString());


                    //et_account.clearFocus();
                    //InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });


        et_withdraw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
                    hasFractionalPart = true;
                } else {
                    hasFractionalPart = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("오픈뱅킹", "after_changed");
                et_withdraw.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = et_withdraw.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = et_withdraw.getSelectionStart();
                    if (hasFractionalPart) {
                        et_withdraw.setText(df.format(n));
                    } else {
                        et_withdraw.setText(dfnd.format(n));
                    }
                    endlen = et_withdraw.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= et_withdraw.getText().length()) {
                        et_withdraw.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        et_withdraw.setSelection(et_withdraw.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }
                et_withdraw.addTextChangedListener(this);


            }
        });

        et_account_holder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Log.d("에디트", v.getText().toString());


                    et_withdraw.requestFocus();


                    //et_account.clearFocus();
                    //InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });

        et_account_holder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tv_account_holder.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                } else {
                    tv_account_holder.setTextColor(activity.getResources().getColor(R.color.colorDefault));

                }
            }
        });


        et_account_holder.setFilters(new InputFilter[]{filterKor});

        et_withdraw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    et_withdraw.clearFocus();
                    Log.d("에디트", v.getText().toString());


                    //et_account.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });

        et_withdraw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tv_withdraw.setTextColor(activity.getResources().getColor(R.color.colorPrimary));

                } else {
                    tv_withdraw.setTextColor(activity.getResources().getColor(R.color.colorDefault));

                }
            }
        });


        return view;
    }

    private void bottomSheetBankSelection() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_bank_selection, null);
        RecyclerView recycler_bank_selection = view.findViewById(R.id.recycler_bank_selection);
        List<ItemForBank> bankList = new ArrayList<>();

        List<String> bank_name_list = Arrays.asList(getResources().getStringArray(R.array.bank_name));
        List<String> bank_code_list = Arrays.asList(getResources().getStringArray(R.array.bank_code));
        for (int i = 0; i < bank_name_list.size(); i++) {

            ItemForBank itemForBank = new ItemForBank();
            itemForBank.setBank_name(bank_name_list.get(i));
            itemForBank.setBank_id(bank_code_list.get(i));
            bankList.add(itemForBank);


        }
        /*

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int maxHeight = (int) (height * 0.70);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);

        recycler_bank_selection.setLayoutParams(lp);

         */


        Utils.drawRecyclerViewDivider(activity,recycler_bank_selection);


        RecyclerBankSelectionAdapter adapter = new RecyclerBankSelectionAdapter(activity,bankList, bottomSheetDialog, this);
        recycler_bank_selection.setLayoutManager(new LinearLayoutManager(activity));
        recycler_bank_selection.setAdapter(adapter);


        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();


    }

    private void bottomSheetRecentAccount() {
        bottomSheetDialog = new BottomSheetDialog(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_recent_account, null);
        LinearLayout ly_recent_account = view.findViewById(R.id.ly_recent_account);
        recycler_recent_account = view.findViewById(R.id.recycler_recent_account);

        /*

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int maxHeight = (int) (height * 0.70);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);

        recycler_bank_selection.setLayoutParams(lp);

        */

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.d("높이", height + "");

        int maxHeight = (int) (height * 0.60);
        Log.d("높이", maxHeight + "max");
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
        //recycler_date_time.setLayoutParams(lp);
        ly_recent_account.setLayoutParams(lp);


        String user_id = Account.getUserId(activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("recent_account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemForAccountList> accountLists = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemForAccountList item = snapshot.getValue(ItemForAccountList.class);
                    if (item != null) {

                        String bank_name = item.getBank_name();
                        String account_num = item.getAccount_num();
                        String bank_code = item.getBank_code();
                        String bank_account_holder = item.getAccount_holder_name();
                        ItemForAccountList itemForAccountList = new ItemForAccountList();
                        itemForAccountList.setAccount_holder_name(bank_account_holder);
                        itemForAccountList.setAccount_num(account_num);
                        itemForAccountList.setBank_code(bank_code);
                        itemForAccountList.setBank_name(bank_name);
                        accountLists.add(itemForAccountList);
                    }

                    setupRecyclerView(accountLists);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Utils.drawRecyclerViewDivider(activity, recycler_recent_account);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();


    }

    private void setupRecyclerView(List<ItemForAccountList> lists) {

        RecyclerAccountListAdapter adapter = new RecyclerAccountListAdapter(activity,lists, this);
        recycler_recent_account.setLayoutManager(new LinearLayoutManager(activity));
        recycler_recent_account.setAdapter(adapter);
    }

    @Override
    public void onSetupBank(String bank_name, String bank_code) {

        tv_bank_name.setText(bank_name);
        this.bank_code = bank_code;


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


    @Override
    public void onCompleteAccount(ItemForAccountList item) {
        String bank_name = item.getBank_name();
        String account_num = item.getAccount_num();
        String account_holder_name = item.getAccount_holder_name();
        bank_code = item.getBank_code();


        et_account_holder.setText(account_holder_name);
        et_account.setText(account_num);
        tv_bank_name.setText(bank_name);

        bottomSheetDialog.dismiss();


    }

    private boolean isFilled() {

        String bank_name = tv_bank_name.getText().toString();
        String account_num = et_account.getText().toString();
        String account_holder_name = et_account_holder.getText().toString();
        String withdraw = et_withdraw.getText().toString();

        if (bank_name.isEmpty()) {
            Toast.makeText(activity, "은행선택안함", Toast.LENGTH_LONG).show();
            return false;

        }
        if (account_num.isEmpty()) {
            Toast.makeText(activity, "계좌번호입력안함", Toast.LENGTH_LONG).show();
            et_account.requestFocus();

            return false;
        }
        if (account_holder_name.isEmpty()) {
            Toast.makeText(activity, "이름입력안함", Toast.LENGTH_LONG).show();
            et_account_holder.requestFocus();
            return false;
        }

        if (withdraw.isEmpty()) {
            Toast.makeText(activity, "금액입력안함", Toast.LENGTH_LONG).show();
            et_withdraw.requestFocus();
            return false;

        }
        return true;
    }


    private void displayPoint() {
        String user_id = Account.getUserId(activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("point").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Integer point = dataSnapshot.getValue(Integer.class);

                    Log.d("포인트", point + "포인트");
                    if (point != null) {
                        String tv = Utils.makeNumberCommaWon(point) + "P";

                        tv_point.setText(tv);
                    }
                } else {
                    String tv = "0 P";
                    tv_point.setText(tv);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void theTest() {


        OpenBanking openBanking = OpenBanking.getInstance();

        openBanking.requestWithdraw(callback, activity);


    }
/*

edit_point.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("에디트", v.getText().toString());
                    if (!v.getText().toString().equals("")) {
                        point_input = Integer.valueOf(v.getText().toString().replaceAll(",", ""));
                    } else {
                        point_input = 0;
                    }
                    pointCalculate(point_input);


                    edit_point.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }
                return false;
            }
        });


    private void pointCalculate(int point_input) {
        if (point_callback_state) {

            if ((point_input <= point_amount) && (point_amount <= total)) {
                display_point = point_input;

            }
            if ((point_input <= point_amount) && (total <= point_amount)) {
                display_point = point_input;

            }
            if ((point_amount <= total) && (total <= point_input)) {
                display_point = point_amount;

            }
            if ((point_amount <= point_input) && (point_input <= total)) {
                display_point = point_amount;

            }
            if ((total <= point_input) && (point_input <= point_amount)) {
                display_point = total;

            }
            if ((total <= point_amount) && (point_amount <= point_input)) {
                display_point = total;


            }
            if (point_amount == 0) {
                display_point = 0;
            }

            edit_point.setText(String.valueOf(display_point));
            String point_total_string = Utils.makeNumberComma(display_point) + " P";
            text_point_total.setText(point_total_string);
            String actual_payment_string = Utils.makeNumberComma(total - display_point);
            //actual_payment = total - display_point;
            text_actual_payment.setText(actual_payment_string);
            String rest_point_string = Utils.makeNumberComma(point_amount - display_point) + " P";
            text_rest_point.setText(rest_point_string);


        }


    }

 */


}
