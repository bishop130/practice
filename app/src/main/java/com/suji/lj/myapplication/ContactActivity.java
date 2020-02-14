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
import io.realm.RealmChangeListener;
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
    RealmResults<ContactItem> realmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Contacts.initialize(this);
        //Realm.init(this);
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
        //tempList2 = realm.where(ContactItem.class).equalTo("isSelected", true).findAll();


        listItem = utils.getContacts();
        realmResults = realm.where(ContactItem.class).findAll();

        selected_item = realm.copyFromRealm(realmResults);
        for (int j = 0; j < realmResults.size(); j++) {

            Log.d("삼시", listItem.get(realmResults.get(j).getPosition()).getPosition() + "전체");
            Log.d("삼시", realmResults.get(j).getPosition() + "선택");
            listItem.get(realmResults.get(j).getPosition()).setSelected(true);
            listItem.get(realmResults.get(j).getPosition()).setAmount(realmResults.get(j).getAmount());
            listItem.get(realmResults.get(j).getPosition()).setPosition(realmResults.get(j).getPosition());

        }

        setupRecyclerView(listItem);
        recyclerViewConfirmed(selected_item);


    }

    private void setupRecyclerView(List<ContactItem> itemList) {

        adapter = new RecyclerViewContactAdapter(this, itemList, this, realm);
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
        contactResponse = new ContactResponse(this, itemList, this, realm);
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

    public void refreshSelection(int position) {
        //Log.d("라니스터","포지션 activity"+ position+"   "+listItem.get(position).isSelected());


        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        listItem.get(selected_item.get(position).getPosition()).setSelected(false);
        selected_item.remove(position);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        contactResponse.notifyItemRemoved(position);
        contactResponse.notifyItemRangeChanged(position, selected_item.size());

        //listItem.remove(position);
        //contactResponse.notifyItemRemoved(position);
        //contactResponse.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        RealmResults<ContactItem> list = realm.where(ContactItem.class).findAll();
        for (int i = 0; i < list.size(); i++) {
            Log.d("리브", list.get(i).getDisplayName() + "/ " + list.get(i).getAmount() + "/ " + list.get(i).getPosition() + "/ " + i);

        }

        //setupRecyclerView(listItem);


    }

    public void selectedList(ContactItem item, int position) {
        selected_item.add(item);


        Log.d("라니스터", "test호출");

        contactResponse.notifyItemChanged(position);
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");

        RealmResults<ContactItem> list = realm.where(ContactItem.class).findAll();
        for (int i = 0; i < list.size(); i++) {
            Log.d("리브", list.get(i).getDisplayName() + "/ " + list.get(i).getAmount() + "/ " + list.get(i).getPosition() + "/ " + i);

        }


    }

    public void testDelete(int position) {
        /*
        for (int i = 0; i < selected_item.size(); i++) {
            ContactItem ct = selected_item.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_item.remove(idx);
            }
        }

         */

        //RealmResults<ContactItem> item = realm.where(ContactItem.class).findAll();
        //ContactItem item = realm.where(ContactItem.class).equalTo("position",position).findFirst();
        //item.deleteFromRealm();
        for (int i = 0; i < selected_item.size(); i++) {
            if (position == selected_item.get(i).getPosition()) {
                Log.d("리브", selected_item.get(i).getDisplayName() + "/ " + selected_item.get(i).getAmount() + "/ " + selected_item.get(i).getPosition() + "/ " + i);
                selected_item.remove(i);
                contactResponse.notifyItemRemoved(i);
                contactResponse.notifyItemRangeChanged(i, selected_item.size());

            }
        }


        Log.d("라니스터", "test호출");
        //contactResponse.notifyDataSetChanged();

        //adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


    }

    private void contactSorting() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults.deleteAllFromRealm();

                for (int i = 0; i < selected_item.size(); i++) {
                    ContactItem item = realm.createObject(ContactItem.class);
                    item.setAmount(selected_item.get(i).getAmount());
                    item.setPosition(selected_item.get(i).getPosition());
                    item.setPhoneNumbers(selected_item.get(i).getPhoneNumbers());
                    item.setDisplayName(selected_item.get(i).getDisplayName());
                    item.setSelected(selected_item.get(i).isSelected());
                    Log.d("리브", selected_item.get(i).getAmount() + "/" + selected_item.get(i).getDisplayName() + "/" + selected_item.get(i).getPosition());
                }
                selected_item.clear();


            }
        });

        Intent intent = new Intent();
        setResult(2, intent);
        Log.d("미션카트", realmResults.size() + "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
