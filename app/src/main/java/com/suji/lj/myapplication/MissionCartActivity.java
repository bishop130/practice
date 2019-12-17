package com.suji.lj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Adapters.MissionCartListAdapter;
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
    List<MissionCartItem> missionCartItemList = new ArrayList<>();
    String title;
    Realm realm;
    ImageView add_mission;
    ImageView delete_mission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mission);


        add_mission = findViewById(R.id.add_mission);
        delete_mission = findViewById(R.id.delete_mission);
        add_mission.setOnClickListener(this);
        delete_mission.setOnClickListener(this);



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


    }

    public void setRecyclerView(List<MissionCartItem> missionCartItemList){


        MissionCartListAdapter missionCartListAdapter = new MissionCartListAdapter(this,missionCartItemList);
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
        startActivity(intent);

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
            case R.id.delete_mission:
                deleteMission();
                break;
            case R.id.register_send:
                registerSend();

        }

    }
}
