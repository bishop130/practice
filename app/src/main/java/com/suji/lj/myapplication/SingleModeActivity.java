package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.AccountDialog;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Adapters.OpenBanking;
import com.suji.lj.myapplication.Adapters.PlaceRecyclerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerDateTimeAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerTransferRespectivelyAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Items.PlaceItem;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.FirebaseDB;
import com.suji.lj.myapplication.Utils.Utils;


import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public class SingleModeActivity extends AppCompatActivity implements TextWatcher,
        View.OnClickListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, CompoundButton.OnCheckedChangeListener {
    private final okhttp3.Callback user_account_info_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            //Log.d(TAG, "콜백오류:" + e.getMessage());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            //Log.d(TAG, "서버에서 응답한 Body:" + body);

            SharedPreferences.Editor editor = getSharedPreferences("OpenBanking", MODE_PRIVATE).edit();
            editor.putString("user_me", body);
            editor.apply();

            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            Log.d("유아", "핀테크응답결과 " + body);
            Log.d("유아", "코드 " + rsp_code);

            if (rsp_code.equals("A0000")) {
                userAccountItemList = Utils.UserInfoResponseJsonParse(body);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("유아이", "in");
                        displayBankAccount(userAccountItemList);
                    }
                }).start();


            } else if (rsp_code.equals("O0003")) {
                Log.d("유아", "여기로들어 " + rsp_code);
                SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
                String refresh_token = sharedPreferences.getString("refresh_token", "");

                openBanking.requestAccessTokenFromRefreshToken(refresh_token_callback, refresh_token);

                if (rsp_code.equals("O0012")) {
                    Toast.makeText(getApplicationContext(), "중계처리지연(잠시후 재거래 요망)", Toast.LENGTH_LONG).show();
                }

            } else {
                Log.d("유아", rsp_code + "/" + body);
                Toast.makeText(getApplicationContext(), "에", Toast.LENGTH_LONG).show();
            }


        }
    };
    private final okhttp3.Callback refresh_token_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            String access_token = Utils.getValueFromJson(body, "access_token");
            String refresh_token = Utils.getValueFromJson(body, "refresh_token");
            String user_seq_num = Utils.getValueFromJson(body, "user_seq_no");
            String expires_in = Utils.getValueFromJson(body, "expires_in");
            SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", Context.MODE_PRIVATE);
            SharedPreferences.Editor open_banking_token = sharedPreferences.edit();
            open_banking_token.putString("access_token", access_token);
            open_banking_token.putString("refresh_token", refresh_token);
            open_banking_token.putString("user_seq_num", user_seq_num);
            open_banking_token.putString("expires_in", expires_in);
            open_banking_token.apply();
            open_banking_token.commit();

            openBankingSetting();


        }
    };
    private final okhttp3.Callback transfer_callback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            final String body = response.body().string();
            Log.d("출금", body);
            String rsp_code = Utils.getValueFromJson(body, "rsp_code");
            if (rsp_code.equals("A0000")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("유아이", "in");
                        dataSave();
                    }
                });


            } else {
                String message = Utils.getValueFromJson(body, "rsp_message");
                alertMessage(message);


            }


        }
    };

    TextView next_btn;
    TextView total_amount;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    Intent intent;
    //MissionCartItem item = new MissionCartItem();
    Calendar calendar = Calendar.getInstance();

    Switch no_time_switch;
    TextView no_time_limit_tv;
    TextView wrong_time_tv;
    LinearLayout add_contact;
    List<CalendarDay> calendarDayList = new ArrayList<>();
    RecyclerView transfer_recyclerView;
    RecyclerTransferRespectivelyAdapter transfer_recycler_adapter;
    RecyclerDateTimeAdapter recyclerDateTimeAdapter;
    LinearLayout ly_date_error;
    LinearLayout ly_contact_error;


    RecyclerView date_time_recyclerView;
    RealmList<ItemForDateTime> stringList = new RealmList<>();
    MaterialCalendarView materialCalendarView;
    FrameLayout frameLayout;
    NestedScrollView scrollView;
    ImageView imageView;
    PlaceRecyclerAdapter placeRecyclerAdapter;
    RecyclerView recyclerView;
    SearchView searchView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    List<UserAccountItem> userAccountItemList = new ArrayList<>();
    OpenBanking openBanking = OpenBanking.getInstance();
    MapReverseGeoCoder mapReverseGeoCoder;


    int selected_year = calendar.get(Calendar.YEAR);
    int selected_month = calendar.get(Calendar.MONTH) + 1;
    int selected_day = calendar.get(Calendar.DATE);
    int hour;
    int min;
    String min_date = selected_year + "-" + selected_month + "-" + selected_day;

    boolean is_today = true;
    TextView common_time;
    ImageView reset;
    Realm realm;
    MapView mapView;
    TextView location_loading;
    ImageView location_loaded;
    CardView radius_button;
    LinearLayout zoom_in;
    LinearLayout zoom_out;
    LinearLayout account_select;
    private double current_latitude;
    private double current_longitude;
    TextView detail_setting;
    TextView bank_name_tv;
    TextView account_num_tv;
    SeekBar circle_seekBar;
    TextView account_holder_name;
    AppCompatCheckBox checkBox_term_use, checkBox_term_private, checkBox_term_all;
    boolean TERMS_AGREE_1 = false; // No Check = 0, Check = 1
    boolean TERMS_AGREE_2 = false; // No Check = 0, Check = 1
    int radius = 100;
    AccountDialog accountDialog;
    Toolbar toolbar;
    Calendar cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);
        Log.d("목장", "onCreate");


        next_btn = findViewById(R.id.mission_basic_send);
        textInputEditText = findViewById(R.id.mission_basic_editText);
        textInputLayout = findViewById(R.id.mission_basic_inputLayout);
        no_time_switch = findViewById(R.id.no_time_switch);
        add_contact = findViewById(R.id.add_contact);
        transfer_recyclerView = findViewById(R.id.transfer_recyclerView);
        date_time_recyclerView = findViewById(R.id.recycler_date_time);
        materialCalendarView = findViewById(R.id.material_calendarView);
        common_time = findViewById(R.id.common_time);
        reset = findViewById(R.id.reset);
        frameLayout = findViewById(R.id.fragment_container);
        scrollView = findViewById(R.id.main_scroll_view);
        imageView = findViewById(R.id.transparent_image);
        recyclerView = findViewById(R.id.place_recycler);
        location_loading = findViewById(R.id.daum_map_location_loading);
        location_loaded = findViewById(R.id.daum_map_location_loaded);
        radius_button = findViewById(R.id.radius);
        searchView = findViewById(R.id.daum_map_search);
        detail_setting = findViewById(R.id.detail_setting);
        bank_name_tv = findViewById(R.id.bank_name);
        account_num_tv = findViewById(R.id.account_num);
        checkBox_term_use = findViewById(R.id.agree_terms_use);
        checkBox_term_private = findViewById(R.id.agree_terms_private);
        checkBox_term_all = findViewById(R.id.agree_terms_all);
        total_amount = findViewById(R.id.total_amount);
        ly_date_error = findViewById(R.id.ly_date_error);
        zoom_in = findViewById(R.id.zoom_in);
        zoom_out = findViewById(R.id.zoom_out);
        circle_seekBar = findViewById(R.id.circle_seekBar);
        account_select = findViewById(R.id.account_select);
        account_holder_name = findViewById(R.id.account_name);
        toolbar = findViewById(R.id.toolbar);
        ly_contact_error = findViewById(R.id.ly_contact_error);

        Realm.init(this);

        realm = Realm.getDefaultInstance();
        initiate();

        cal = Calendar.getInstance();
        mapView = new MapView(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ViewGroup mapViewContainer = findViewById(R.id.map_view);
                    mapViewContainer.addView(mapView);

                } catch (Exception ignored) {

                }
            }
        }).start();

        textInputEditText.addTextChangedListener(this);
        next_btn.setOnClickListener(this);
        add_contact.setOnClickListener(this);
        common_time.setOnClickListener(this);
        reset.setOnClickListener(this);
        mapView.setMapViewEventListener(this);
        radius_button.setOnClickListener(this);
        checkBox_term_use.setOnCheckedChangeListener(this);
        checkBox_term_private.setOnCheckedChangeListener(this);
        checkBox_term_all.setOnCheckedChangeListener(this);
        zoom_in.setOnClickListener(this);
        zoom_out.setOnClickListener(this);
        account_select.setOnClickListener(this);


        next_btn.setEnabled(false);
        next_btn.setClickable(false);
        circle_seekBar.setProgress(75);
        circle_seekBar.setMax(175);
        circle_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = seekBar.getProgress() + 25;
                realm.where(MissionCartItem.class).findFirst();

                drawCircle(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude, radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radius = seekBar.getProgress() + 25;
                realm.where(MissionCartItem.class).findFirst();

                drawCircle(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude, radius);

            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }

        });

        toolbar.setTitle("새 약속 정하");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();


        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년  ").append(monthOne).append("월");
                return buffer;
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();

                if (selected) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ItemForDateTime item = realm.createObject(ItemForDateTime.class);

                            item.setDate(DateTimeUtils.makeDateForServer(year, month, day));
                            item.setTime(DateTimeUtils.getCurrentHourMin());
                            item.setYear(year);
                            item.setMonth(month);
                            item.setDay(day);
                            item.setHour(cal.get(Calendar.HOUR_OF_DAY));
                            item.setMin(cal.get(Calendar.MINUTE));

                            stringList.add(item);
                            Log.d("번들 담쟁이", stringList.size() + "");
                            Collections.sort(stringList, new Comparator<ItemForDateTime>() {
                                @Override
                                public int compare(ItemForDateTime o1, ItemForDateTime o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });


                            recyclerDateTimeAdapter.notifyDataSetChanged();
                        }
                    });

                    logDateTime();


                } else {
                    for (int i = 0; i < stringList.size(); i++) {

                        if (stringList.get(i).getDate().equals(DateTimeUtils.makeDateForServer(year, month, day))) {

                            Log.d("솔약국", i + "remove");
                            final int position = i;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    stringList.remove(position);
                                }
                            });

                            recyclerDateTimeAdapter.notifyItemRemoved(i);
                            recyclerDateTimeAdapter.notifyDataSetChanged();
                        }


                    }


                    logDateTime();
                }
                if (materialCalendarView.getSelectedDates().size() == 0) {
                    ly_date_error.setVisibility(View.VISIBLE);
                } else {
                    ly_date_error.setVisibility(View.GONE);
                }

            }
        });


        checkLocationPermission();
        onAddressSearch();
        openBankingSetting();


        realmInit();

        //setDateTimeRecyclerView(stringList);
        //stringList.clear();


    }

    private void initiate() {
        long count = realm.where(MissionCartItem.class).count();
        if (count == 0) {
            setDateTimeRecyclerView(stringList);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.createObject(MissionCartItem.class);
                }
            });


        } else {
            MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
            Log.d("번들 리스트사이즈", item.getCalendarDayList().size() + "");
            RealmList<ItemForDateTime> realmList = item.getCalendarDayList();
            stringList.clear();
            stringList = realmList;
            for (int i = 0; i < realmList.size(); i++) {

                int year = realmList.get(i).getYear();
                int month = realmList.get(i).getMonth();
                int day = realmList.get(i).getDay();
                Log.d("번들 날", year + " " + month + " " + day);

                materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);


            }
            logDateTime();
            setDateTimeRecyclerView(stringList);
            textInputEditText.setText(item.getTitle());


            //recyclerDateTimeAdapter.notifyDataSetChanged();

        }

    }

    public void timeSet(int hour, int min, int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                stringList.get(position).setTime(DateTimeUtils.makeTimeForServer(hour, min));
                stringList.get(position).setHour(hour);
                stringList.get(position).setMin(min);
                recyclerDateTimeAdapter.notifyDataSetChanged();

            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        isTitleValid(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.setTitle(s.toString());
            }
        });


    }

    private void logDateTime() {
        for (int i = 0; i < stringList.size(); i++) {

            int year = stringList.get(i).getYear();
            int month = stringList.get(i).getMonth();
            int day = stringList.get(i).getDay();
            int hour = stringList.get(i).getHour();
            int min = stringList.get(i).getMin();

            Log.d("datetime", year + "." + month + "." + day + "/" + hour + "." + min);
        }


    }

    private void commonTime() {
        Log.d("다이소", "여기맞아?");

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        for (int i = 0; i < stringList.size(); i++) {
                            stringList.get(i).setHour(hourOfDay);
                            stringList.get(i).setMin(minute);

                            stringList.get(i).setTime(DateTimeUtils.makeTimeForServer(hourOfDay, minute));
                        }
                        recyclerDateTimeAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();


    }

    private void resetSelection() {
        materialCalendarView.clearSelection();
        stringList.clear();
        recyclerDateTimeAdapter.notifyDataSetChanged();


    }

    private void accountDialog() {
/*
        FragmentManager fm = getSupportFragmentManager();
        AccountDialog dialogFragment = new AccountDialog(this,positiveListener,negativeListener,userAccountItemList);
        dialogFragment.show(fm, "fragment_dialog_test");

 */

        accountDialog = new AccountDialog(this, userAccountItemList);

        accountDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        accountDialog.setContentView(R.layout.fragment_account_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(accountDialog.getWindow().getAttributes());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lp.width = (int) (metrics.widthPixels * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.8f;


        accountDialog.getWindow().setAttributes(lp);
        accountDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded));

        accountDialog.show();


    }

    public void changeAccount(String fintech_num) {
        for (int i = 0; i < userAccountItemList.size(); i++) {

            if (userAccountItemList.get(i).getFintech_use_num().equals(fintech_num)) {
                String bank_name = userAccountItemList.get(i).getBank_name();
                String account_num = userAccountItemList.get(i).getAccount_num_masked();
                String account_holder = userAccountItemList.get(i).getAccount_holder_name();

                bank_name_tv.setText(bank_name);
                account_num_tv.setText(account_num);
                account_holder_name.setText(account_holder);

                getSharedPreferences("OpenBanking", MODE_PRIVATE).edit().putString("fintech_num", fintech_num).apply();

            }
        }

        accountDialog.dismiss();


    }


    private void isTitleValid(String title) {

        if (title.length() > 1) {

        } else {
            //제목이 너무 짧습니다. 최소 두 글자부터 입력이 가능합니다.
        }


    }

    private void setDateTimeRecyclerView(List<ItemForDateTime> stringList) {


        //Log.d("담쟁이2","넘어가"+stringList.size());
        date_time_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerDateTimeAdapter = new RecyclerDateTimeAdapter(this, stringList, realm);
        date_time_recyclerView.setAdapter(recyclerDateTimeAdapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mission_basic_send:
                dataCheck();
                //dataSave();
                break;
            case R.id.add_contact:
                startActivityForResult(new Intent(this, ContactActivity.class), 2);
                break;
            case R.id.common_time:
                commonTime();
                break;
            case R.id.reset:
                materialCalendarView.clearSelection();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        stringList.deleteAllFromRealm();
                        recyclerDateTimeAdapter.notifyDataSetChanged();
                        ly_date_error.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.radius:

                if (mapView.isShowingCurrentLocationMarker()) {
                    //double lat = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
                    //double lng = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
                    mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(current_latitude, current_longitude)));


                } else {
                    Toast.makeText(getApplicationContext(), "위치정보를 수신중입니다.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.detail_setting:
                startActivity(new Intent(SingleModeActivity.this, DetailSettingActivity.class));
                break;
            case R.id.zoom_in:
                mapView.zoomIn(true);

                break;
            case R.id.zoom_out:
                mapView.zoomOut(true);
                break;
            case R.id.account_select:
                accountDialog();


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
            if (realmResults2.size() > 0) {
                ly_contact_error.setVisibility(View.GONE);
            } else {
                ly_contact_error.setVisibility(View.VISIBLE);
            }
            setTransferRecyclerView(realmResults2);
            amountDisplay();

        }

    }

    private void setTransferRecyclerView(RealmResults<ContactItem> realmResults) {
        transfer_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transfer_recycler_adapter = new RecyclerTransferRespectivelyAdapter(this, realmResults, realm);

        transfer_recyclerView.setAdapter(transfer_recycler_adapter);

    }

    private void realmInit() {


        loadData();

    }

    public void amountDisplay() {
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        int amount = 0;
        for (int i = 0; i < realmResults2.size(); i++) {
            amount = amount + realmResults2.get(i).getAmount();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        Log.d("옵티마", decimalFormat.format(amount));
        total_amount.setText(decimalFormat.format(amount));

    }

    private void loadData() {
        RealmResults<ContactItem> realmResults2 = realm.where(ContactItem.class).findAll();
        if (realmResults2.size() > 0) {
            ly_contact_error.setVisibility(View.GONE);
        }

        setTransferRecyclerView(realmResults2);
        amountDisplay();


        int count = 0;

        if (count == 0) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);


        } else {
            MissionCartItem cartItem = realm.where(MissionCartItem.class).findFirst();
            Log.d("미션아이디", "찾기 " + cartItem.getHour());
            Log.d("미션아이디", "찾기 " + cartItem.getMin());
            Log.d("미션아이디", "찾기 " + cartItem.getAddress());
            Log.d("미션아이디", "찾기 " + cartItem.getTitle());
            Log.d("미션아이디", "찾기 " + cartItem.getMax_date());
            Log.d("미션아이디", "찾기 " + cartItem.getMin_date());
            Log.d("미션아이디", "찾기 리스" + cartItem.getCalendarDayList().size());


            recyclerDateTimeAdapter.notifyDataSetChanged();
            hour = cartItem.getHour();
            min = cartItem.getMin();
            min_date = cartItem.getMin_date();
            Log.d("미션아이디", " min " + min);
            textInputEditText.setText(cartItem.getTitle());

        }


    }

    private void dataCheck() {
        boolean isValid = true;

        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        RealmResults<ContactItem> friend_list = realm.where(ContactItem.class).findAll();


        mapReverseGeoCoder = new MapReverseGeoCoder("7ff2c8cb39b23bad249dc2f805898a69", mapView.getMapCenterPoint(), this, this);
        mapReverseGeoCoder.startFindingAddress();
        if (TextUtils.isEmpty(item.getTitle())) {
            isValid = false;
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        if (item.getCalendarDayList().size() == 0) {
            isValid = false;
            Toast.makeText(getApplicationContext(), "날짜를 선택해주세요", Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < item.getCalendarDayList().size(); i++) {
                String date = item.getCalendarDayList().get(i).getDate();
                String time = item.getCalendarDayList().get(i).getTime();
                boolean is30 = DateTimeUtils.compareIsFuture30min(date + time);
                if (!is30) {
                    isValid = false;
                }
                Log.d("비니", date + "/" + time + "/" + is30);
            }
        }
        if (friend_list.size() == 0) {
            //Toast.makeText(getApplicationContext(), "연락를 선택해주세요", Toast.LENGTH_LONG).show();
            ly_contact_error.setVisibility(View.VISIBLE);

            isValid = false;
        }

        if (!TERMS_AGREE_1) {
            isValid = false;
        }
        if (!TERMS_AGREE_2) {
            isValid = false;
        }
        if (isValid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("약속을 등록하시겠습니까?");

            builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (Utils.isNetworkConnected(getApplicationContext())) {
                        transferAmount();
                    } else {
                        Toast.makeText(getApplicationContext(), "인터넷 연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();

                    }

                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void transferAmount() {
        openBanking.requestTransfer(transfer_callback, this);

    }

    private void dataSave() {
        String user_id = getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        String mission_id = Utils.getCurrentTime() + "U" + user_id;

        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();


        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseDB.registerCheckForServer(mRootRef, user_id, mission_id, item);
        FirebaseDB.registerMainDisplay(this, mRootRef, user_id, mission_id, item);
        FirebaseDB.registerKakaoToken(mRootRef, user_id);
        FirebaseDB.registerMissionInfoList(mRootRef, user_id, mission_id, realm);


        Intent service = new Intent(SingleModeActivity.this, NewLocationService.class);
        boolean is_running = Utils.isServiceRunningInForeground(this, NewLocationService.class);
        if (is_running) {
            stopService(service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, service);

            } else {
                startService(service);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, service);

            } else {
                startService(service);
            }
        }


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                SharedPreferences.Editor editor = getSharedPreferences("location_setting", MODE_PRIVATE).edit();
                editor.putLong("lat", Double.doubleToRawLongBits(item.getLat()));
                editor.putLong("lng", Double.doubleToRawLongBits(item.getLng()));
                editor.apply();
                editor.commit();

                item.deleteFromRealm();
                RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
                realmResults.deleteAllFromRealm();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();

            }
        });


    }

    private void checkDateTime() {
        long diff = 0;
        long now = System.currentTimeMillis();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        String input_time = min_date + " " + hour + ":" + min;
        Log.d("싱글", input_time + "  inputdate");
        try {
            Date input_date = sdf.parse(input_time);
            diff = (input_date.getTime() - now) / 60000;
            Log.d("싱글", now + "\n" + input_date.getTime() + "\n" + diff);
        } catch (ParseException e) {
            e.getErrorOffset();
        }

        if (diff >= 30) {

            wrong_time_tv.setVisibility(View.GONE);
        } else {

            if (!no_time_switch.isChecked()) {

                wrong_time_tv.setVisibility(View.GONE);
            } else {

                wrong_time_tv.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();

        Log.d("목장", "onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //realm.beginTransaction();
        Log.d("목장", "onResume");

    }

    @Override
    public void onBackPressed() {
        Log.d("번들", "onBackPressed");

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //RealmResults<ItemForDateTime> realmResults = realm.where(ItemForDateTime.class).findAll();
                //realmResults.deleteAllFromRealm();
                if (stringList.size() != 0) {
                    Log.d("번들", "저장" + stringList.size());
                    //realm.where(MissionCartItem.class).findAll().deleteAllFromRealm();
                    MissionCartItem items = realm.createObject(MissionCartItem.class);
                    items.setCalendarDayList(stringList);
                    items.setMin_date(DateTimeUtils.getCurrentTime());
                }
            }
        });
        //MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        //Log.d("번들", "반환" + item.getCalendarDayList().size());
        //Log.d("번들", "반환" + item.getMin_date());

        realm.close();


        super.onBackPressed();

    }

    private void onAddressSearch() {


        //SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("장소,주소 검색..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mapView.removeAllPOIItems();
                loadAddress(query);
                final float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (300 * scale + 0.5f);
                //map_layout.getLayoutParams().height = pixels;
                searchView.clearFocus();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return true;
            }
        });

    }

    private void loadAddress(String query) {
        final Retrofit retrofit2 = new Retrofit.Builder().
                addConverterFactory(GsonConverterFactory.create()).
                baseUrl(KeywordSearch.base).
                build();
        KeywordSearch keywordSearch = retrofit2.create(KeywordSearch.class);
        Call<JsonObject> call = keywordSearch.keywordSearch(KeywordSearch.key, query);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String con = new GsonBuilder().setPrettyPrinting().create().toJson(response.body());
                List<PlaceItem> placeList = new ArrayList<>();
                try {

                    JSONObject obj = new JSONObject(con);
                    String check_string = obj.getJSONObject("meta").getString("total_count");
                    if (Integer.valueOf(check_string) == 0) {
                        PlaceItem placeItem = new PlaceItem();
                        placeItem.setPlaceName(query + "(와)과 일치하는 검색결과가 없습니다");
                        placeList.add(placeItem);
                    } else {
                        JSONArray jsonArray = obj.getJSONArray("documents");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PlaceItem placeItem = new PlaceItem();
                            JSONObject object = (JSONObject) jsonArray.get(i);
                            if ((object.getString("road_address_name") == null)) {
                                placeItem.setRoadAddress(object.getString("address_name"));
                                placeItem.setOldAddress(" ");

                            } else {
                                placeItem.setRoadAddress(object.getString("road_address_name"));
                                placeItem.setOldAddress(object.getString("address_name"));
                            }
                            placeItem.setPlaceName(object.getString("place_name"));
                            placeItem.setLatitude(object.getDouble("y"));
                            placeItem.setLongitude(object.getDouble("x"));
                            MapPOIItem customMarker = new MapPOIItem();
                            customMarker.setItemName(object.getString("place_name"));
                            customMarker.setTag(1);
                            customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(object.getDouble("y"), object.getDouble("x")));
                            customMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 마커타입을 커스텀 마커로 지정.
                            customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);


                            mapView.addPOIItem(customMarker);
                            Log.d("장소", object.getString("place_name"));


                            placeList.add(placeItem);

                            //  String address = ((JSONObject) tempObj.get("address")).get("address_name").toString();


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setupRecyclerView(placeList);


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "주소를 불러올 수 없습니다.", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void setupRecyclerView(List<PlaceItem> placeList) {

        placeRecyclerAdapter = new PlaceRecyclerAdapter(placeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(placeRecyclerAdapter);

    }

    public interface KeywordSearch {
        String base = "https://dapi.kakao.com/";
        String key = "KakaoAK 7ff2c8cb39b23bad249dc2f805898a69";


        @GET("v2/local/search/keyword.json?")
        Call<JsonObject> keywordSearch(@Header("Authorization") String key, @Query("query") String query);
    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.setAddress(s);
            }
        });


    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        current_latitude = mapPointGeo.latitude;
        current_longitude = mapPointGeo.longitude;


        location_loading.setVisibility(View.GONE);
        location_loaded.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {


        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();

        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        if (item.getLat() == 0.0) {
            Log.d("위치", "초기화됨");
            SharedPreferences preferences = getSharedPreferences("location_setting", MODE_PRIVATE);
            double lat = Double.longBitsToDouble(preferences.getLong("lat", 0));
            double lng = Double.longBitsToDouble(preferences.getLong("lng", 0));
            Log.d("위치", lat+"");
            Log.d("위치", lng+"");
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true);
            drawCircle(geoCoordinate.latitude, geoCoordinate.longitude, radius);
        } else {
            Log.d("위치", "초기화안됨" + item.getLat());
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(item.getLat(), item.getLng()), true);
            drawCircle(geoCoordinate.latitude, geoCoordinate.longitude, radius);
        }


    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        drawCircle(geoCoordinate.latitude, geoCoordinate.longitude, radius);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
                item.setLat(geoCoordinate.latitude);
                item.setLng(geoCoordinate.longitude);
            }
        });

    }

    private void drawCircle(double lat, double lng, int radius) {
        mapView.removeAllCircles();
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(lat, lng), // center
                radius, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle.setTag(5678);//태그는 왜달지
        mapView.addCircle(circle);


    }

    public void moveSelectedPlace(double lat, double lng) {
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng)));

    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");

            mapView.setCurrentLocationEventListener(this);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SingleModeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("퍼미션", "여기는 왜 못들어와");

                        mapView.setCurrentLocationEventListener(this);
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void openBankingSetting() {
        SharedPreferences sharedPreferences = getSharedPreferences("OpenBanking", MODE_PRIVATE);
        String access_token = sharedPreferences.getString("access_token", "");
        String user_seq_num = sharedPreferences.getString("user_seq_num", "");
        String refresh_token = sharedPreferences.getString("refresh_token", "");
        Log.d("오픈뱅킹", "사용자토큰" + access_token);

        if (TextUtils.isEmpty(access_token)) {
            //register_account.setText("처음사용자");
            //ly_add_new_account.setVisibility(View.VISIBLE);
            Log.d("오픈뱅킹", "처음사용자");
        } else {
            Log.d("오픈뱅킹", "기사용자정보요청");
            //ly_add_new_account.setVisibility(View.GONE);
            openBanking.requestUserAccountInfo(user_account_info_callback, access_token, user_seq_num);


        }


    }

    private void displayBankAccount(List<UserAccountItem> userAccountItemList) {

        String fintech_num = getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");
        for (int i = 0; i < userAccountItemList.size(); i++) {
            if (userAccountItemList.get(i).getFintech_use_num().equals(fintech_num)) {
                String bank_name = userAccountItemList.get(i).getBank_name();
                String account_num = userAccountItemList.get(i).getAccount_num_masked();
                String account_holder = userAccountItemList.get(i).getAccount_holder_name();

                bank_name_tv.setText(bank_name);
                account_num_tv.setText(account_num);
                account_holder_name.setText(account_holder);
            }
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.agree_terms_use:
                if (buttonView.isChecked()) {
                    TERMS_AGREE_1 = true;


                } else {

                    TERMS_AGREE_1 = false;
                }
                if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                    checkBox_term_all.setChecked(true);

                } else {
                    checkBox_term_all.setChecked(false);
                }

                break;
            case R.id.agree_terms_private:
                if (buttonView.isChecked()) {
                    TERMS_AGREE_2 = true;
                } else {
                    TERMS_AGREE_2 = false;
                }
                if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                    checkBox_term_all.setChecked(true);
                    next_btn.setEnabled(true);
                    next_btn.setClickable(true);

                } else {
                    checkBox_term_all.setChecked(false);
                }
                break;
            case R.id.agree_terms_all:
                if (buttonView.isChecked()) {
                    checkBox_term_use.setChecked(true);
                    checkBox_term_private.setChecked(true);
                    next_btn.setEnabled(true);
                    next_btn.setClickable(true);

                    TERMS_AGREE_1 = true;
                    TERMS_AGREE_2 = true;
                } else {

                    if (TERMS_AGREE_1 && TERMS_AGREE_2) {
                        checkBox_term_use.setChecked(false);
                        checkBox_term_private.setChecked(false);
                        next_btn.setEnabled(false);
                        next_btn.setClickable(false);
                        Log.d("약관", "123");
                        TERMS_AGREE_1 = false;
                        TERMS_AGREE_2 = false;
                    }
                }

                break;


        }
    }

    private void alertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error").setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);


    }
}
