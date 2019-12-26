package com.suji.lj.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.ContactResponse;
import com.suji.lj.myapplication.Adapters.FlexBoxAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewContactAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.github.tamir7.contacts.Contacts;
import com.google.gson.Gson;
import com.suji.lj.myapplication.Items.ContactItemStatus;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;

import java.util.List;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, ContactResponse.OnFriendsCountListener, RecyclerViewContactAdapter.OnFriendsCountFromContactListener {


    RecyclerViewContactAdapter adapter;
    ContactResponse contactResponse;
    List<ContactItem> listItem;
    List<ContactItem> selected_item = new ArrayList<>();
    RealmResults<ContactItem> tempList;
    RealmResults<ContactItem> tempList2;
    Utils utils = new Utils();
    RecyclerView recyclerView;
    RecyclerView confirmed_recycler;
    TextView confirm_button;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Contacts.initialize(this);
        realm = Realm.getDefaultInstance();

        recyclerView = findViewById(R.id.recycler_view_contact);
        confirmed_recycler = findViewById(R.id.recycler_view_confirmed);
        SearchView searchView = findViewById(R.id.contact_search);
        confirm_button = findViewById(R.id.contact_confirmed);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });


        confirm_button.setOnClickListener(this);
        tempList2 = realm.where(ContactItem.class).equalTo("isSelected",true).findAll();

        if (realm.where(ContactItem.class).findAll().size() == 0) {
            Log.d("셀렉트", "처음선택");
            listItem = utils.getContacts();
            setupRecyclerView(listItem);
            recyclerViewConfirmed(selected_item);
        } else {
            Log.d("셀렉트", "재선택");
            tempList = realm.where(ContactItem.class).findAll();
            listItem = realm.copyFromRealm(tempList);

            //selected_item = realm.copyFromRealm(tempList2);
            setupRecyclerView(listItem);
            recyclerViewConfirmed(selected_item);
            for (int i = 0; i < listItem.size(); i++) {
                if (listItem.get(i).isSelected()) {

                    selected_item.add(listItem.get(i));
                    contactResponse.notifyItemChanged(listItem.get(i).getPosition());
                }
            }
            for (int i = 0; i < selected_item.size(); i++) {
                Log.d("뭐가달라", i + "");
                Log.d("뭐가달라", selected_item.get(i).getDisplayName());
                Log.d("뭐가달라", selected_item.get(i).getPhoneNumbers());
                Log.d("뭐가달라", selected_item.get(i).getPosition() + "");
                Log.d("뭐가달라", selected_item.get(i).isSelected() + "");
                Log.d("뭐가달라", " ");
            }


        }


    }

    private void setupRecyclerView(List<ContactItem> itemList) {

        adapter = new RecyclerViewContactAdapter(this, itemList, this);
        //adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public void recyclerViewConfirmed(List<ContactItem> itemList) {
/*
        contactResponse = new ContactResponse(this,itemList);
        confirmed_recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        confirmed_recycler.setAdapter(contactResponse);

 */
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        confirmed_recycler.setLayoutManager(layoutManager);
        contactResponse = new ContactResponse(this, itemList, this);
        confirmed_recycler.setAdapter(contactResponse);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onResume() {
        checkContactPermission();
        super.onResume();
    }

    public static final int MY_PERMISSIONS_REQUEST_CONTACT = 100;

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");
            Contacts.initialize(this);
            initiateTab();


        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

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
                                ActivityCompat.requestPermissions(ContactActivity.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        MY_PERMISSIONS_REQUEST_CONTACT);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CONTACT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d("퍼미션", "여기는 왜 못들어와");
                        Contacts.initialize(this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initiateTab() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        toolbar.setTitle("연락처 선택");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void refreshSelection(List<ContactItem> itemList, int position) {
        //Log.d("라니스터","포지션 activity"+ position+"   "+listItem.get(position).isSelected());
        listItem.get(position).setSelected(false);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");
        //listItem.remove(position);
        //contactResponse.notifyItemRemoved(position);
        //contactResponse.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        //setupRecyclerView(listItem);


    }

    public void selectedList(ContactItem contactItem) {
        selected_item.add(contactItem);
        Log.d("셀렉트", listItem.get(contactItem.getPosition()).isSelected() + "   " + contactItem.getDisplayName());

        Log.d("라니스터", "test호출");
        contactResponse.notifyItemChanged(contactItem.getPosition());
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


    }

    public void testDelete(int position) {
        for (int i = 0; i < selected_item.size(); i++) {
            ContactItem ct = selected_item.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_item.remove(idx);
            }
        }


        Log.d("라니스터", "test호출");
        //contactResponse.notifyDataSetChanged();
        contactResponse.notifyItemRemoved(position);
        //adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


    }

    private void contactSorting() {
        if (selected_item.size() == 0) {
            Toast.makeText(getApplicationContext(), "연락처를 선택해주세요", Toast.LENGTH_LONG).show();

        } else {

            Intent intent = new Intent();
            setResult(2, intent);
/*
            realm.beginTransaction();
            realm.where(ContactItem.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();


 */

            if (tempList2.size() == 0) {
                for (int i = 0; i < listItem.size(); i++) {

                    //Log.d("렐름","선택"+selected_item.get(i).getDisplayName()+"   포지션 : "+selected_item.get(i).getPosition());


                    //Log.d("렐름확인", selected_item.get(i).getPhoneNumbers());

                    realm.beginTransaction();
                    ContactItem contactItem = realm.createObject(ContactItem.class);
                    contactItem.setDisplayName(listItem.get(i).getDisplayName());
                    contactItem.setPhoneNumbers(listItem.get(i).getPhoneNumbers());
                    contactItem.setPosition(listItem.get(i).getPosition());
                    contactItem.setSelected(listItem.get(i).isSelected());
                    contactItem.setAmount(10000);
                    realm.commitTransaction();

                }

            }else{
                tempList2 = realm.where(ContactItem.class).equalTo("isSelected", true).findAll();
                for (int i = 0; i < selected_item.size(); i++) {
                    int count = 0;

                    for (int j = 0; j < tempList2.size(); j++) {

                        boolean test = tempList2.get(j).getPhoneNumbers().equals(selected_item.get(i).getPhoneNumbers());
                        if (test) {
                            Log.d("이게달라", "원래이름" + tempList2.get(j).getPhoneNumbers() + "\n 새이름" + selected_item.get(i).getPhoneNumbers() + "\n 같은지확인" + test + "\n");
                            count++;
                        }
                        if((j==tempList2.size()-1)&&(count==0)){//같은게 하나도 없다면 새로운 목록
                            Log.d("이게달라", "들어와야돼");
                            realm.beginTransaction();
                            ContactItem contactItem = realm.createObject(ContactItem.class);
                            contactItem.setDisplayName(selected_item.get(i).getDisplayName());
                            contactItem.setPhoneNumbers(selected_item.get(i).getPhoneNumbers());
                            contactItem.setPosition(selected_item.get(i).getPosition());
                            contactItem.setSelected(true);
                            contactItem.setAmount(10000);
                            realm.commitTransaction();
                        }


                    }

                }
            }



        }
        Log.d("미션카트", selected_item.size() + "");
        finish();
    }


    @Override
    public void onFriendsCount(int num) {
        confirm_button.setText("(" + num + ") 선택완료");


    }

    @Override
    public void onFriendsCountFromContact(int num) {
        confirm_button.setText("(" + num + ") 선택완료");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_confirmed:
                contactSorting();
                break;
        }


    }
}
