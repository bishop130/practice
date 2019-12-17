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

import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.ContactResponse;
import com.suji.lj.myapplication.Adapters.RecyclerViewContactAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ContactItemForServer;
import com.github.tamir7.contacts.Contacts;
import com.google.gson.Gson;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_confirmed:
                contactSorting();
                break;
        }


    }

    RecyclerViewContactAdapter adapter;
    ContactResponse contactResponse;
    List<ContactItem> listItem;
    ArrayList<ContactItem> selected_item = new ArrayList<>();
    Utils utils = new Utils();
    RecyclerView recyclerView;
    RecyclerView confirmed_recycler;

    public ContactActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Contacts.initialize(this);


        recyclerView = findViewById(R.id.recycler_view_contact);
        confirmed_recycler= findViewById(R.id.recycler_view_confirmed);
        SearchView searchView = findViewById(R.id.contact_search);
        TextView button = findViewById(R.id.contact_confirmed);

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


        button.setOnClickListener(this);
        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data="";
                String displayInfo="";
                int count = 0;

                List<ContactItem> ctList = selected_item;
                List<ContactItemForServer> ctServer = new ArrayList<>();



                for(int i =0; i<ctList.size(); i++){
                    ContactItem singleContact = ctList.get(i);
                    //if(singleContact.isSelected() == true){
                    data=data+singleContact.getPhoneNumbers();
                    displayInfo=displayInfo+singleContact.getDisplayName()+", ";
                    count++;
                    ctList.get(i);
                    ContactItemForServer contactItemForServer = new ContactItemForServer();
                    contactItemForServer.setName(singleContact.getDisplayName());
                    contactItemForServer.setNum(singleContact.getPhoneNumbers());
                    ctServer.add(contactItemForServer);


                    //}
                }
                String contact_json = new Gson().toJson(ctServer);
                Log.d("List",contact_json);
                int last = displayInfo.length() - 2;
                if (last > 0 && displayInfo.charAt(last) == ',') {
                    displayInfo = displayInfo.substring(0, last);
                }

                Log.d("DisplayInfo",displayInfo);
                Log.d("DisplayInfo","연락처 개수     "+ctList.size());
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ContactInfo",displayInfo);
                editor.putString("contact_json",contact_json);
                editor.putString("contact_count",String.valueOf(ctList.size()));
                editor.putString("count",String.valueOf(count));
                editor.putInt("contact_num",ctList.size());
                editor.putInt("Code",777);
                editor.apply();

                Toast.makeText(getApplicationContext(),
                        "Selected Friends:" +data, Toast.LENGTH_LONG)
                        .show();
                finish();

            }
        });
*/


        listItem = utils.getContacts();

        setupRecyclerView(listItem);
        recyclerViewConfirmed(selected_item);

    }
    private void setupRecyclerView(List<ContactItem> itemList) {

        adapter = new RecyclerViewContactAdapter(this, itemList);
        //adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



    }
    public void recyclerViewConfirmed(List<ContactItem> itemList){

        contactResponse = new ContactResponse(this,itemList);
        confirmed_recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        confirmed_recycler.setAdapter(contactResponse);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    protected void onResume(){
        checkContactPermission();
        super.onResume();
    }

    public static final int MY_PERMISSIONS_REQUEST_CONTACT = 100;

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션","이미 승인받음");
            Contacts.initialize(this);
            initiateTab();


        }
        else{
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
                        Log.d("퍼미션","여기는 왜 못들어와");
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

    private void initiateTab(){



        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        toolbar.setTitle("연락처 선택");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    public void refreshSelection(List<ContactItem> itemList, int position){
        //Log.d("라니스터","포지션 activity"+ position+"   "+listItem.get(position).isSelected());
        listItem.get(position).setSelected(false);

        //listItem.remove(position);
        //contactResponse.notifyItemRemoved(position);
        contactResponse.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        //setupRecyclerView(listItem);


    }
    public void test(ContactItem itemList){
        selected_item.add(itemList);

        Log.d("라니스터","test호출");
        contactResponse.notifyDataSetChanged();
        adapter.notifyDataSetChanged();



    }
    public void testDelete(int position){
        for(int i=0; i<selected_item.size(); i++){
            ContactItem ct = selected_item.get(i);
            if (ct.getPosition() == position) {
                int idx = i;
                selected_item.remove(idx);
            }
        }


        Log.d("라니스터","test호출");
        contactResponse.notifyDataSetChanged();
        adapter.notifyDataSetChanged();



    }
    private void contactSorting(){
        if(selected_item.size()==0){
            Toast.makeText(getApplicationContext(),"연락처를 선택해주세요",Toast.LENGTH_LONG).show();

        }else {

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("contact_list", selected_item);
            intent.putExtra("test","123");
            setResult(2, intent);
            Log.d("미션카트",selected_item.size()+"");
            finish();
        }


    }






}
