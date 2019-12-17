package com.suji.lj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.suji.lj.myapplication.Adapters.FlexBoxAdapter;
import com.suji.lj.myapplication.Adapters.MissionCartListAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.DateItem;
import com.suji.lj.myapplication.Items.MissionCartItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MissionCartActivity extends AppCompatActivity implements View.OnClickListener {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mission);


        add_mission = findViewById(R.id.add_mission);
        add_contact = findViewById(R.id.add_contact);
        contact_recyclerView = findViewById(R.id.contact_recyclerView);
        add_mission.setOnClickListener(this);
        add_contact.setOnClickListener(this);



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


    }

    public void setRecyclerView(List<MissionCartItem> missionCartItemList){


        missionCartListAdapter = new MissionCartListAdapter(this,missionCartItemList);
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
        flexBoxAdapter = new FlexBoxAdapter(this, contactItemArrayList);
        contact_recyclerView.setAdapter(flexBoxAdapter);


    }

}
