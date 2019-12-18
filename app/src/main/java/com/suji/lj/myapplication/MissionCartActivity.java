package com.suji.lj.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.suji.lj.myapplication.Adapters.FlexBoxAdapter;
import com.suji.lj.myapplication.Adapters.MissionCartListAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.MissionCartItem;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MissionCartActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, FlexBoxAdapter.OnFriendsNumListener {

    RecyclerView recyclerView;
    ArrayList<DateItem> dateItemArrayList = new ArrayList<>();
    ArrayList<ContactItem> contactItemArrayList = new ArrayList<>();
    List<MissionCartItem> missionCartItemList = new ArrayList<>();
    MissionCartListAdapter missionCartListAdapter;
    String title;
    Realm realm;
    LinearLayout add_mission;
    LinearLayout add_contact;
    RecyclerView contact_recyclerView;
    FlexBoxAdapter flexBoxAdapter;
    private TextInputEditText et;
    private TextInputLayout etl;
    private boolean hasFractionalPart;
    int amount;
    TextView bank_name_tv;
    TextView account_num_tv;
    //TextView nick_name;
    TextView friends_num;
    TextView transfer_amount;
    String user_seq_num;
    String bank_name;
    String account_num;

    // 세자리로 끊어서 쉼표 보여주고, 소숫점 셋째짜리까지 보여준다.
    //DecimalFormat df = new DecimalFormat("###,###.####");
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    // 값 셋팅시, StackOverFlow를 막기 위해서, 바뀐 변수를 저장해준다.
    String result="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mission);


        add_mission = findViewById(R.id.add_mission);
        add_contact = findViewById(R.id.add_contact);
        contact_recyclerView = findViewById(R.id.contact_recyclerView);
        add_mission.setOnClickListener(this);
        add_contact.setOnClickListener(this);
        bank_name_tv = findViewById(R.id.bank_name);
        account_num_tv = findViewById(R.id.account_num);
        //nick_name = view.findViewById(R.id.nick_name);
        friends_num = findViewById(R.id.friends_num);
        transfer_amount = findViewById(R.id.transfer_amount);

        etl= findViewById(R.id.text_input_layout);
        et = findViewById(R.id.transfer_input);

        //numberTextWatcher = new NumberTextWatcher(textInputEditText,textInputLayout,contact_num,transfer_amount,context);



        et.addTextChangedListener(this);
        etl.setHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color_dark)));
        etl.setDefaultHintTextColor(ColorStateList.valueOf(this.getResources().getColor(R.color.mdtp_accent_color)));



        amount = Integer.valueOf(Integer.valueOf(et.getText().toString().replaceAll(",", "")));










        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();



        RealmResults<MissionCartItem> realmResults = realm.where(MissionCartItem.class).findAll();
        /*
        Log.d("렐름",realmResults.get(0).getAddress());
        Log.d("렐름",realmResults.get(0).getTitle());
        Log.d("렐름",realmResults.get(0).getHour()+"");
        Log.d("렐름",realmResults.get(0).getMin()+"");
        Log.d("렐름",realmResults.get(0).getCalendarDayList().get(0).getDay()+"");

         */





        missionCartItemList = realmResults;
        Log.d("렐름",realmResults.size()+"");
        Log.d("렐름",missionCartItemList.size()+"");
        recyclerView = findViewById(R.id.missions_cart_list);
         //dateItemArrayList = getIntent().getParcelableArrayListExtra("date_list");




//         Log.d("리절트",dateItemArrayList.get(0).getYear()+"");
         setRecyclerView(missionCartItemList);
         setContactRecyclerView(contactItemArrayList);

         //flexBoxAdapter.setOnSampleReceivedEvent(this);


    }

    public void setRecyclerView(List<MissionCartItem> missionCartItemList){


        missionCartListAdapter = new MissionCartListAdapter(this,missionCartItemList,realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(missionCartListAdapter);




    }

    private void deleteMission(){

        realm.beginTransaction();

        RealmResults<MissionCartItem> realmResults = realm.where(MissionCartItem.class).findAll();
        realmResults.get(0).deleteFromRealm();
        realm.commitTransaction();

    }
    private void addMission(){

        Intent intent = new Intent(this,SingleModeActivity.class);
        startActivityForResult(intent,1);

    }
    private void registerSend(){
        //파이어베이스


    }




    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.add_mission:
                addMission();
                break;
            case R.id.add_contact:
                startActivityForResult(new Intent(this,ContactActivity.class),2);
                break;
            case R.id.register_send:
                registerSend();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode==2) {
            Log.d("미션카트", data.getStringExtra("test") + "test");


            contactItemArrayList = data.getParcelableArrayListExtra("contact_list");
            Log.d("미션카트", contactItemArrayList.size() + "result");
            flexBoxAdapter.notifyDataSetChanged();
            setContactRecyclerView(contactItemArrayList);
        }
        if(resultCode==1){

            missionCartListAdapter.notifyDataSetChanged();


        }


    }

    private void setContactRecyclerView(ArrayList<ContactItem> contactItemArrayList){


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        contact_recyclerView.setLayoutManager(layoutManager);
        flexBoxAdapter = new FlexBoxAdapter(this, contactItemArrayList,this);
        contact_recyclerView.setAdapter(flexBoxAdapter);


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "10만원 이상    " + etl.isErrorEnabled() );
            } else if (amount < 3000) {
                etl.setError("최소 3천원부터 입력됩니다.");
                etl.setErrorEnabled(true);
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.red));

                Log.d("오픈뱅킹", "3천원    " + etl.isErrorEnabled() );
            } else {
                etl.setError(null);
                etl.setErrorEnabled(false);
                etl.setBoxStrokeColor(this.getResources().getColor(R.color.blue));


                Log.d("오픈뱅킹", "정상    " + etl.isErrorEnabled() );



            }
        }






        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
        transfer_amount.setText(df.format(contactItemArrayList.size()*amount)+" 원");


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

    @Override
    public void onFriendsNum(int num) {
        friends_num.setText(num+" 명");
    }
}
