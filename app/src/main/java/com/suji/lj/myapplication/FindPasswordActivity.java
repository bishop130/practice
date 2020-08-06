package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.BackPressHandler;
import com.suji.lj.myapplication.Utils.MailSender;
import com.suji.lj.myapplication.Utils.PreciseCountdown;
import com.suji.lj.myapplication.Utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindPasswordActivity extends AppCompatActivity {


    EditText et_send_email_code;
    EditText et_verification_code;
    TextView tv_send_email;
    TextView tv_done;
    TextView tv_rest_time;

    boolean isTimerRunning = false;

    MailSender gMailSender;
    String verification_code;
    CountDownTimer timer;
    Toolbar toolbar;
    LinearLayout ly_rest_time;
    AlertDialog dialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        et_send_email_code = findViewById(R.id.et_send_email_code);
        et_verification_code = findViewById(R.id.et_verification_code);
        tv_send_email = findViewById(R.id.tv_send_email_code);
        tv_done = findViewById(R.id.tv_done);
        tv_rest_time = findViewById(R.id.tv_rest_time);
        toolbar = findViewById(R.id.toolbar);
        ly_rest_time = findViewById(R.id.ly_rest_time);
        user_id = Account.getUserId(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        dialog = builder.create();


        toolbar.setTitle("비밀번호 재설정");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        gMailSender = new MailSender("leejunghwan90@gmail.com", "Maestro130!");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        tv_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                String email_address = et_send_email_code.getText().toString();
               databaseReference.child("account").orderByChild("email").equalTo(email_address).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       ItemRegisterAccount item = dataSnapshot.getValue(ItemRegisterAccount.class);
                       if(item!=null) {
                           if (user_id.equals(item.getUser_id())) {
                               try {


                                   verification_code = gMailSender.getEmailCode();
                                   //GMailSender.sendMail(제목, 본문내용, 받는사람);
                                   String verification = "요청하신 인증코드는 " + verification_code + "입니다.";
                                   gMailSender.sendMail("인증코드", verification, email_address);

                                   dialog.dismiss();
                                   Utils.makeAlertDialog(email_address + "로 인증코드를 전송했습니다.", FindPasswordActivity.this);


                                   timer = new CountDownTimer(1000 * 60 * 3, 1000) {
                                       @Override
                                       public void onTick(long l) {
                                           isTimerRunning = true;
                                           long days = TimeUnit.MILLISECONDS.toDays(l);
                                           long remainingHoursInMillis = l - TimeUnit.DAYS.toMillis(days);
                                           long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                                           long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                                           long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                                           long remainingSecondsInMillis = remainingMinutesInMillis - TimeUnit.MINUTES.toMillis(minutes);
                                           long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingSecondsInMillis);
                                           //Log.d("타이머", "position" + position + "   " + holder.getAdapterPosition() + "time:" + l + " " + timer_switch);


                                           String rest_time = minutes + "분 " + seconds + "초";

                                           tv_rest_time.setText(rest_time);

                                       }

                                       @Override
                                       public void onFinish() {
                                           isTimerRunning = false;
                                           verification_code = "";
                                           tv_rest_time.setText("유효기간이 만료됐습니다.");

                                       }
                                   };

                                   ly_rest_time.setVisibility(View.VISIBLE);
                                   timer.start();


                                   //Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                               } catch (SendFailedException e) {
                                   dialog.dismiss();
                                   Utils.makeAlertDialog("이메일 형식이 잘못되었습니다.", FindPasswordActivity.this);
                               } catch (MessagingException e) {
                                   dialog.dismiss();
                                   StringWriter errors = new StringWriter();
                                   e.printStackTrace(new PrintWriter(errors));

                                   Log.d("인터넷", errors.toString());
                                   Utils.makeAlertDialog("인터넷 연결을 확인해주십시오", FindPasswordActivity.this);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }

                           }
                       }else{
                           dialog.dismiss();
                           Utils.makeAlertDialog(email_address + "로 인증코드를 전송했습니다.", FindPasswordActivity.this);




                       }



                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });



            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMailSender.getEmailCode().equals(et_verification_code.getText().toString())) {

                    //인증코드 인증성공
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this);
                    builder.setMessage("인증이 완료되었습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //kakaoRest.getAddressFromLocation(get_address, lat, lng);

                            Intent intent = new Intent(getApplicationContext(), SecurityActivity.class);
                            intent.putExtra("password", 1);
                            startActivity(intent);
                            finish();


                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            Intent intent = new Intent(getApplicationContext(), SecurityActivity.class);
                            intent.putExtra("password", 1);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    //실패시

                    Utils.makeAlertDialog("인증에 실패했습니다. 다시 시도해주세요.", FindPasswordActivity.this);


                }


            }
        });


        et_send_email_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    et_send_email_code.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }


                return false;
            }
        });

        et_verification_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    et_send_email_code.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    //amountDisplay();


                    return true;
                }


                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTimerRunning) {
            timer.cancel();
        }

    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this);
        builder.setMessage("화면을 벗어나면 인증코드는 초기화됩니다.");
        builder.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();


            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this);
                builder.setMessage("화면을 벗어나면 인증코드는 초기화됩니다.");
                builder.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();


                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
