package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemForAccountList;
import com.suji.lj.myapplication.Items.ItemForPassword;
import com.suji.lj.myapplication.Items.ItemForTransaction;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import io.realm.internal.Collection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SecurityActivity extends AppCompatActivity implements View.OnClickListener {


    TextView key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0, backspace;
    ImageView dot_1, dot_2, dot_3, dot_4, dot_5, dot_6;

    StringBuffer stringBuffer = new StringBuffer();
    TextView find_password;
    TextView tv_info;
    String temp = "";
    int pointAmount = 0;
    String title;
    String missionId;
    int missionMode = 0;


    private static int PASSWORD_SET = 1;
    private static int PASSWORD_MATCH = 2;
    private static int PASSWORD_RESET_WITHOUT_KEY = 3;
    private static int PASSWORD_RESET_WITH_KEY = 4;
    private static int POINT_PAY_REQUEST = 5;
    int code;
    AlertDialog loading_dialog;

    private int[] key_list = {
            R.id.key_1,
            R.id.key_2,
            R.id.key_3,
            R.id.key_4,
            R.id.key_5,
            R.id.key_6,
            R.id.key_7,
            R.id.key_8,
            R.id.key_9,
            R.id.key_0
    };
    int textViewCount = 10;
    //ProgressDialog progressDialog;

    TextView[] textViewArray = new TextView[textViewCount];


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        user_id = Account.getUserId(this);
/*
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("요청을 처리하고 있습니다.");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

 */

        AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();
        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

        loading_dialog.setCancelable(false);


        backspace = findViewById(R.id.backspace);


        shuffleKeyPad();

        dot_1 = findViewById(R.id.dot_1);
        dot_2 = findViewById(R.id.dot_2);
        dot_3 = findViewById(R.id.dot_3);
        dot_4 = findViewById(R.id.dot_4);
        dot_5 = findViewById(R.id.dot_5);
        dot_6 = findViewById(R.id.dot_6);

        find_password = findViewById(R.id.find_password);
        tv_info = findViewById(R.id.tv_info);
        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        backspace.setOnClickListener(this);

        code = getIntent().getIntExtra("password", 0);
        pointAmount = getIntent().getIntExtra("point", 0);
        missionMode = getIntent().getIntExtra("missionMode", 0);
        title = getIntent().getStringExtra("title");
        missionId = getIntent().getStringExtra("missionId");
        if (code == PASSWORD_MATCH) {
            tv_info.setText("비밀번호를 입력해주세요.");
            find_password.setVisibility(View.VISIBLE);
        } else if (code == PASSWORD_SET) {
            tv_info.setText("새 비밀번호를 설정해주세요");
            find_password.setVisibility(View.GONE);

        } else if (code == PASSWORD_RESET_WITHOUT_KEY) {
            tv_info.setText("새 비밀번호를 설정해주세요");
            find_password.setVisibility(View.GONE);


        } else {//비밀번호 재설정
            tv_info.setText("기존 비밀번호를 입력해주세요.");
            find_password.setVisibility(View.VISIBLE);

            //finish();
        }

    }

    @Override
    public void onClick(View v) {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10);

        Log.d("시큐리티", "length   " + stringBuffer.length() + "");

        switch (v.getId()) {

            case R.id.key_1:
                stringBuffer.append(((TextView) findViewById(R.id.key_1)).getText().toString());
                addPassword();
                break;
            case R.id.key_2:
                stringBuffer.append(((TextView) findViewById(R.id.key_2)).getText().toString());
                addPassword();

                break;
            case R.id.key_3:
                stringBuffer.append(((TextView) findViewById(R.id.key_3)).getText().toString());
                addPassword();
                break;
            case R.id.key_4:
                stringBuffer.append(((TextView) findViewById(R.id.key_4)).getText().toString());
                addPassword();
                break;
            case R.id.key_5:
                stringBuffer.append(((TextView) findViewById(R.id.key_5)).getText().toString());
                addPassword();

                break;
            case R.id.key_6:
                stringBuffer.append(((TextView) findViewById(R.id.key_6)).getText().toString());
                addPassword();

                break;
            case R.id.key_7:
                stringBuffer.append(((TextView) findViewById(R.id.key_7)).getText().toString());
                addPassword();

                break;
            case R.id.key_8:
                stringBuffer.append(((TextView) findViewById(R.id.key_8)).getText().toString());
                addPassword();

                break;
            case R.id.key_9:
                stringBuffer.append(((TextView) findViewById(R.id.key_9)).getText().toString());
                addPassword();

                break;
            case R.id.key_0:
                stringBuffer.append(((TextView) findViewById(R.id.key_0)).getText().toString());
                addPassword();

                break;
            case R.id.backspace:
                deletePassword();
                break;
        }

        Log.d("비밀번호", stringBuffer.toString());

    }


    private void addPassword() {

        int size = stringBuffer.length() - 1;
        if (size < 6) {
            switch (size) {
                case 0:
                    dot_1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;
                case 1:
                    dot_2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;
                case 2:
                    dot_3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;
                case 3:
                    dot_4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;
                case 4:
                    dot_5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;
                case 5:
                    dot_6.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.password_dot));
                    break;


            }

            if (size == 5) {//비밀번호 입력 완료
                loading_dialog.show();


                if (code == PASSWORD_SET) {//비밀번호 설정
                    setKey();

                } else if (code == PASSWORD_MATCH) {//비밀번호 입력 확인

                    isKeyMatched();

                } else if (code == PASSWORD_RESET_WITHOUT_KEY) {//비밀번호 재설정 입력-> 새로설정

                    setKey();
                    //resetKey();


                } else {//기좀비번 -> 새 비번


                    isKeyMatched();
                    //setKey();


                }


// 위 비밀번호의 BCrypt 알고리즘 해쉬 생성
// passwordHashed 변수는 실제 데이터베이스에 저장될 60바이트의 문자열이 된다.
                // String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt());

// 위 문장은 아래와 같다. 숫자가 높아질수록 해쉬를 생성하고 검증하는 시간은 느려진다.
// 즉, 보안이 우수해진다. 하지만 그만큼 응답 시간이 느려지기 때문에 적절한 숫자를 선정해야 한다. 기본값은 10이다.


// 생성된 해쉬를 원래 비밀번호로 검증한다. 맞을 경우 true를 반환한다.
// 주로 회원 로그인 로직에서 사용된다.


            }
        }


    }

    private void deletePassword() {

        int size = stringBuffer.length() - 1;
        if (size >= 0) {
            stringBuffer.deleteCharAt(size);
        }
        size = stringBuffer.length();

        switch (size) {
            case 0:
                dot_1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;
            case 1:
                dot_2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;
            case 2:
                dot_3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;
            case 3:
                dot_4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;
            case 4:
                dot_5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;
            case 5:
                dot_6.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
                break;


        }

    }

    private void functionWithdraw() {//영수증 검증


        String bank_name = getIntent().getStringExtra("bank_name");
        String bank_code = getIntent().getStringExtra("bank_code");
        String account_num = getIntent().getStringExtra("account_num");
        String account_holder_name = getIntent().getStringExtra("account_holder_name");
        String amount = getIntent().getStringExtra("amount");

        Log.d("시큐리티", bank_name);


        String user_id = Account.getUserId(this);
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/withdraw";


        RequestBody formBody = new FormBody.Builder()
                .add("bank_name", bank_name)
                .add("bank_code", bank_code)
                .add("account_num", account_num)
                .add("account_holder_name", account_holder_name)
                .add("withdraw", amount)
                .add("user_id", user_id)

                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("부트페이", "실패");

                loading_dialog.dismiss();


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                if (!response.isSuccessful()) {
                    Log.d("부트페이", "fail response from firebase cloud function");
                    loading_dialog.dismiss();


                } else {


                    String receipt = response.body().string();

                    Log.d("부트페이", "성공" + receipt);
                    //Log.d("부트페이", "성공" + Utils.getValueFromJson(responseBody.string(), "date_time"));
                    //displayReceipt(result);
                    //dataSave(item, receipt);
                    //최근계좌 저장
                    databaseReference.child("user_data").child(user_id).child("recent_account").orderByChild("account_num").equalTo(account_num).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (!dataSnapshot.exists()) {
                                Log.d("계좌", "계좌없으므로 추가");
                                ItemForAccountList item = new ItemForAccountList();
                                item.setBank_name(bank_name);
                                item.setAccount_num(account_num);
                                item.setBank_code(bank_code);
                                item.setAccount_holder_name(account_holder_name);
                                databaseReference.child("user_data").child(user_id).child("recent_account").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loading_dialog.dismiss();
                                        Utils.makeAlertDialog("이체를 완료했습니다.", SecurityActivity.this);
                                        finish();


                                    }
                                });


                            } else {
                                Log.d("계좌", "계좌있음");
                                loading_dialog.dismiss();
                                Utils.makeAlertDialog("이체를 완료했습니다.", SecurityActivity.this);


                                finish();


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }


        });
    }


    private void clearPassword() {
        dot_1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
        dot_2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
        dot_3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
        dot_4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
        dot_5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));
        dot_6.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));

        stringBuffer.delete(0, stringBuffer.length());


    }

    private void isKeyMatched() {

        //progressDialog.show();
        String password = stringBuffer.toString();
        //String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt(10));

        Log.d("시큐리티", "iskeymatch");


        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("transfer_key").child("key").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);

                    boolean isValidPassword = BCrypt.checkpw(password, value);

                    if (isValidPassword) {//비밀번호 확인


                        Log.d("시큐리티", "비밀번호 맞음");
                        if (code == PASSWORD_MATCH) {
                            functionWithdraw();
                        } else if (code == PASSWORD_RESET_WITH_KEY) {
                            loading_dialog.dismiss();
                            stringBuffer.delete(0, stringBuffer.length());
                            tv_info.setText("새 비밀번호를 설정해주세요.");
                            shuffleKeyPad();
                            clearPassword();
                            code = PASSWORD_SET;

                        } else if (code == POINT_PAY_REQUEST) {


                            function();

                        }


                    } else {
                        shuffleKeyPad();
                        Log.d("시큐리티", "비밀번호 틀림");
                        //비밀번호 틀림
                        databaseReference.child("user_data").child(user_id).child("transfer_key").child("limit_count").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                                    int count = ((Long) dataSnapshot.getValue()).intValue();
                                    Integer aa = dataSnapshot.getValue(Integer.class);
                                    Log.d("시큐리티", "인트" + count);
                                    Log.d("시큐리티", "인티저" + aa);


                                    if (count > 0) {
                                        count--;
                                        loading_dialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this);
                                        builder.setMessage("비밀번호가 맞지 않습니다. 다시 입력해주세요. 남은 시도횟수는 " + count + "번 입니다.");
                                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //kakaoRest.getAddressFromLocation(get_address, lat, lng);


                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();


                                        clearPassword();
                                        databaseReference.child("user_data").child(user_id).child("transfer_key").child("limit_count").setValue(count).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });


                                    } else {//5번 모두 실패시  비번 삭제후 재설정 페이지 이동

                                        databaseReference.child("user_data").child(user_id).child("transfer_key").child("key").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                } else {
                    loading_dialog.dismiss();
                    //비밀번호를 설정하세요??


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setKey() {


        if (temp.isEmpty()) { //비밀번호 첫번째 입력
            temp = stringBuffer.toString();
            Log.d("시큐리티", "비밀번호 첫번째");
            tv_info.setText("비밀번호를 한 번 더 입력해주세요.");
            loading_dialog.dismiss();
            shuffleKeyPad();
            clearPassword();
        } else {//비밀번호 두번째 입력
            Log.d("시큐리티", "비밀번호 두번째");
            String password = stringBuffer.toString();
            if (temp.equals(password)) {//첫번째 비밀번호와 두번째가 같다면

                String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
                ItemForPassword item = new ItemForPassword();
                item.setKey(passwordHashed);
                item.setLimit_count(5);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("user_data").child(user_id).child("transfer_key").setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //비밀번호 설정 완료

                            AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this);
                            builder.setMessage("비밀번호 설정이 완료되었습니다.");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //kakaoRest.getAddressFromLocation(get_address, lat, lng);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                    finish();


                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                    Toast.makeText(getApplicationContext(), "oncancel", Toast.LENGTH_LONG).show();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


                        } else {
                            loading_dialog.dismiss();
                            //finish();

                        }

                    }
                });


            } else { // 비밀번호가 같지 않다면 경고를 띄우고 처음부터 다시 입력
                temp = "";
                stringBuffer.delete(0, stringBuffer.length());
                loading_dialog.dismiss();
                clearPassword();


                AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this);
                builder.setMessage("처음 입력한 비밀번호와 다릅니다. 다시 입력해주세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tv_info.setText("비밀번호를 입력해주세요.");


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }


        }


    }

    private void shuffleKeyPad() {

        Utils.shuffleArray(key_list);

        for (int i = 0; i < key_list.length; i++) {
            textViewArray[i] = findViewById(key_list[i]);
            textViewArray[i].setOnClickListener(this);
            textViewArray[i].setText(i + "");
        }


    }


    private void pointManage() {
        databaseReference.child("user_data").child(user_id).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer point = snapshot.getValue(Integer.class);
                    if (point != null) {
                        int done = point - pointAmount;
                        Log.d("포인트", point + "point");
                        Log.d("포인트", pointAmount + "pointAmount");
                        Log.d("포인트", done + "done");

                        snapshot.getRef().setValue(done);

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String date_time = format.format(calendar.getTime());

        ItemForTransaction transaction = new ItemForTransaction();
        transaction.setPoint(pointAmount);
        transaction.setTitle(title);
        transaction.setDate_time(date_time);
        transaction.setPayment_method("보증금 납입");
        transaction.setMission_id(missionId);
        transaction.setUser_id(user_id);
        transaction.setMissionMode(missionMode);
        //"2020-08-04 20:58:43"

        /** 영수증 서버에 기록**/

        databaseReference.child("user_data").child(user_id).child("transaction").push().setValue(transaction);

        /** 관리자 열람 서버에 기록**/
        databaseReference.child("common").child("transaction").push().setValue(transaction);

        setResult(5);
        finish();

    }

    private void function() {
        OkHttpClient httpClient = new OkHttpClient();
        // HttpUrl.Builder httpBuider = HttpUrl.parse("").newBuilder();
        String url = "https://us-central1-cloudmessaging-dcdf0.cloudfunctions.net/point_transaction";


        RequestBody formBody = new FormBody.Builder()
                .add("userId", user_id)
                .add("title", title)
                .add("missionId", missionId)
                .add("missionMode", String.valueOf(missionMode))
                .add("point", String.valueOf(pointAmount))
                .add("purchase", String.valueOf(1))
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("부트페이", "실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = "";
                if (!response.isSuccessful()) {
                    Log.d("부트페이", "fail response from firebase cloud function");
                } else {

                    loading_dialog.dismiss();
                    if (responseBody != null) {
                        Log.d("포인트", "전송성공");
                        String receipt = responseBody.string();
                        pointManage();

                    }
                }

            }
        });
    }


}
