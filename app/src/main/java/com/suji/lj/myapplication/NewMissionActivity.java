package com.suji.lj.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.suji.lj.myapplication.Adapters.DBHelper;
import com.suji.lj.myapplication.Adapters.LocationService;
import com.suji.lj.myapplication.StepperForm.MakeContactStep;
import com.suji.lj.myapplication.StepperForm.MakeDateStep;
import com.suji.lj.myapplication.StepperForm.MakeLocationStep;
import com.suji.lj.myapplication.StepperForm.MakeTimeStep;
import com.suji.lj.myapplication.StepperForm.MakeTitle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewMissionActivity extends AppCompatActivity implements StepperFormListener, DialogInterface.OnClickListener {


    private ProgressDialog progressDialog;
    private MakeTitle makeTitle;
    private MakeTimeStep makeTimeStep;
    private MakeDateStep makeDateStep;
    private MakeContactStep makeContactStep;

    private VerticalStepperFormView verticalStepperForm;
    private MakeLocationStep makeLocationStep;
    public static final String STATE_TITLE = "title";
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";
    public static final String STATE_DAYS = "days";
    public static final String STATE_LOCATION = "location";
    public static final String VALIDE_CODE = "777";
    private StringRequest request;
    private static final String goURL = "http://bishop130.cafe24.com/test.php";
    private RequestQueue requestQueue;
    private SimpleDateFormat date_time_db = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private SimpleDateFormat date_db = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat time_db = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
    String result;
    String lat;
    String lng;
    String date_array;
    String date_array_server;
    String userId, CurrentTime;
    String contact_json;
    String token;
    String initiate_date;
    DBHelper dbHelper;
    String mission_time;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_stepper);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");

        Calendar nowdate = Calendar.getInstance();
        String curTime = sdf.format(nowdate.getTime());

        Log.d("현재시간", "" + curTime);

        CurrentTime = curTime;

        String[] stepTitles = getResources().getStringArray(R.array.steps_titles);
        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Lat", "1111");
        editor.putInt("ValidCode", 666);
        editor.apply();

        SharedPreferences sharedPreferences2 = getSharedPreferences("Contact", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putInt("Code", 666);
        editor2.apply();
        editor2.commit();

        makeTitle = new MakeTitle(stepTitles[0]);
        makeTimeStep = new MakeTimeStep(stepTitles[3]);
        makeDateStep = new MakeDateStep(stepTitles[2]);
        makeLocationStep = new MakeLocationStep(stepTitles[1]);
        makeContactStep = new MakeContactStep(stepTitles[4]);

        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm.setup(this, makeTitle, makeLocationStep, makeDateStep, makeTimeStep, makeContactStep).init();

    }

    @Override
    public void onCompletedForm() {
        final Thread dataSavingThread = saveData();
        requestQueue = Volley.newRequestQueue(this);
        Log.d("완료 데이터", makeTitle.getStepData());
        mission_time = String.valueOf(makeTimeStep.getStepData().hour) + ":" + String.valueOf(makeTimeStep.getStepData().minutes) + ":00";

        String user_id = getSharedPreferences("Kakao",MODE_PRIVATE).getString("token","");

        userId = String.valueOf(user_id);
        Log.d("토큰확인", String.valueOf(userId));
        final SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        lat = sharedPreferences.getString("lat", "");
        lng = sharedPreferences.getString("lng", "");
        date_array = sharedPreferences.getString("date_array", "");
        initiate_date = sharedPreferences.getString("initiate_date", "");
        date_array_server = sharedPreferences.getString("date_array_server", "");
        contact_json = sharedPreferences.getString("contact_json", "");
        token = sharedPreferences.getString("token", "");
        address = sharedPreferences.getString("address", "");


        volleyConnect();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.form_sending_data_message));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                try {
                    dataSavingThread.interrupt();
                } catch (RuntimeException e) {

                    // No need to do anything here
                } finally {

                    verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                }
            }
        });

    }

    private void volleyConnect() {
        request = new StringRequest(Request.Method.POST, goURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                insertDB(mission_time, date_array, CurrentTime + userId, lat, lng, makeTitle.getStepData(), userId);
                setAlarm(mission_time, date_array, CurrentTime + userId, lat, lng, makeTitle.getStepData(), initiate_date);


                Toast.makeText(getApplicationContext(), "전송완료" + makeTitle.getStepData(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewMissionActivity.this, MainActivity.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("Lat", lat);
                hashMap.put("Lng", lng);
                hashMap.put("userId", userId);
                hashMap.put("MissionID", CurrentTime + userId);
                hashMap.put("mission_time", mission_time);
                hashMap.put("MissionTitle", makeTitle.getStepData());
                hashMap.put("date_array", date_array);
                hashMap.put("date_array_server", date_array_server);
                hashMap.put("contact_json", contact_json);
                hashMap.put("token", token);
                hashMap.put("address",address);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onCancelledForm() {
        showCloseConfirmationDialog();

    }


    private Thread saveData() {

        // Fake data saving effect
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(STATE_TITLE, makeTitle.getStepData());
                    intent.putExtra(STATE_TIME_HOUR, makeTimeStep.getStepData().hour);
                    intent.putExtra(STATE_TIME_MINUTES, makeTimeStep.getStepData().minutes);
                    intent.putExtra(STATE_DAYS, makeDateStep.getStepData());
                    intent.putExtra(STATE_LOCATION, makeLocationStep.getStepData());

                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return thread;
    }


    private void finishIfPossible() {
        if (verticalStepperForm.isAnyStepCompleted()) {
            showCloseConfirmationDialog();
        } else {
            finish();
        }
    }

    private void showCloseConfirmationDialog() {
        new DiscardAlarmConfirmationFragment().show(getSupportFragmentManager(), null);
    }


    private void dismissDialogIfNecessary() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishIfPossible();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1:
                Toast.makeText(getApplicationContext(), "DISCARD", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();


                SharedPreferences sharedPreferences1 = getSharedPreferences("Contact", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear();
                editor1.apply();

                finish();
                break;

            case -2:
                verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishIfPossible();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("생명주기", "onPause");
        // dismissDialogIfNecessary();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("생명주기", "onStop");

        //dismissDialogIfNecessary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("생명주기", "onResume");

        makeLocationStep.restoreStepData(" ");
        makeLocationStep.isStepDataValid(" ");
        makeContactStep.restoreStepData(" ");
        makeContactStep.isStepDataValid(" ");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("생명주기", "onDestroy");
        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(STATE_TITLE, makeTitle.getStepData());
        savedInstanceState.putInt(STATE_TIME_HOUR, makeTimeStep.getStepData().hour);
        savedInstanceState.putInt(STATE_TIME_MINUTES, makeTimeStep.getStepData().minutes);
        savedInstanceState.putString(STATE_DAYS, makeDateStep.getStepData());
        savedInstanceState.putString(STATE_LOCATION, makeLocationStep.getStepData());
        // IMPORTANT: The call to super method must be here at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey(STATE_TITLE)) {
            String title = savedInstanceState.getString(STATE_TITLE);
            makeTitle.restoreStepData(title);
        }

        if (savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTES)) {
            int hour = savedInstanceState.getInt(STATE_TIME_HOUR);
            int minutes = savedInstanceState.getInt(STATE_TIME_MINUTES);
            MakeTimeStep.TimeHolder time = new MakeTimeStep.TimeHolder(hour, minutes);
            makeTimeStep.restoreStepData(time);
        }

        if (savedInstanceState.containsKey(STATE_DAYS)) {
            String day = savedInstanceState.getString(STATE_DAYS);
            makeDateStep.restoreStepData(day);
        }


        if (savedInstanceState.containsKey(STATE_LOCATION)) {
            String location = savedInstanceState.getString(STATE_LOCATION);
            makeLocationStep.restoreStepData(location);
        }


        // IMPORTANT: The call to super method must be here at the end
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static class DiscardAlarmConfirmationFragment extends DialogFragment {

        private DialogInterface.OnClickListener listener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            listener = (DialogInterface.OnClickListener) context;

        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.form_discard_question)
                    .setMessage(R.string.form_info_will_be_lost)
                    .setPositiveButton(R.string.form_discard, listener)
                    .setNegativeButton(R.string.form_discard_cancel, listener)
                    .setCancelable(false);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            return dialog;
        }
    }

    private void insertDB(String mission_time, String date_array, String mission_id, String lat, String lng, String title, String user_id) {
        List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));
        dbHelper = new DBHelper(NewMissionActivity.this, "alarm_manager.db", null, 5);

        for (int i = 0; i < items.size(); i++) {
            try {
                String date = date_db.format(date_db.parse(items.get(i)));
                String date_time = date_time_db.format(date_time_db.parse(items.get(i) + " " + mission_time));
                String time = time_db.format(time_db.parse(mission_time));
                String query = "INSERT INTO alarm_table VALUES(null,'" + time + "','" + lat + "','" + lng + "','" +
                        mission_id + "','" + date + "','" + date_time + "','false','" + title + "','" + user_id + "')";
                dbHelper.insert(query);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    private void setAlarm(String mission_time, String date_array, String mission_id, String lat, String lng, String title, String initiate_date) {

        Intent service = new Intent(NewMissionActivity.this, LocationService.class);
        service.putExtra("service_time", mission_time);
        service.putExtra("date_array", date_array);
        service.putExtra("service_date", initiate_date);
        service.putExtra("mission_id", mission_id);
        service.putExtra("lat", lat);
        service.putExtra("lng", lng);
        service.putExtra("service_title", title);
        service.putExtra("service_flag", false);
        stopService(service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, service);
        } else {
            startService(service);
        }

/*
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long[] interval = {1000 * 60 * 5, 1000 * 60 * 4, 1000 * 60 * 3};



        List<String> items = Arrays.asList(date_array.split("\\s*,\\s*"));

        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < interval.length; j++) {
                int requestID = String.valueOf(mission_id + items.get(i) + interval[j]).hashCode();
                try {
                    Log.d("확인좀",String.valueOf(requestID));

                    Date cur_time = date_time.parse(date_time.format(new Date(System.currentTimeMillis())));
                    Date mission_datetime = date_time.parse(items.get(i) + " " + mission_time);
                    Intent intent = new Intent(this, BroadCastService.class);
                    intent.putExtra("mission_name", makeTitle.getStepData());
                    long days = TimeUnit.MILLISECONDS.toDays(interval[i]);
                    long remainingHoursInMillis = interval[i] - TimeUnit.DAYS.toMillis(days);
                    long hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
                    long remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
                    intent.putExtra("hour", hours);
                    intent.putExtra("min", minutes);


                    PendingIntent sender = PendingIntent.getBroadcast(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (cur_time.getTime() < mission_datetime.getTime() - interval[j]) {
                        Log.d("확인좀2",String.valueOf(mission_datetime.getTime()));
                        Log.d("확인좀3",String.valueOf(cur_time.getTime()));
                        Log.d("확인좀4",date_time.format(mission_datetime.getTime() - interval[j]));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - interval[j], sender);
                        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - interval[j], sender);
                        else
                            alarmManager.set(AlarmManager.RTC_WAKEUP, mission_datetime.getTime() - interval[j], sender);
                    }
                } catch (ParseException e) {

                }
            }
        }

*/
    }


}
