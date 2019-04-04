package com.example.lj.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lj.myapplication.Adapters.RecyclerViewContactAdapter;
import com.example.lj.myapplication.Items.ContactItem;
import com.example.lj.myapplication.Items.ContactItemForServer;
import com.example.lj.myapplication.Items.SendData;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    RecyclerViewContactAdapter adapter;
    ArrayList<ContactItem> listItem;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        listItem = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_contact);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        toolbar.setTitle("연락처 선택");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        SearchView searchView = (SearchView)findViewById(R.id.contact_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });




    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getContacts(){
        Query q = Contacts.getQuery();
        q.include(Contact.Field.ContactId, Contact.Field.DisplayName, Contact.Field.PhoneNumber);
        List<Contact> result = q.find();


        try {
            String con = new GsonBuilder().setPrettyPrinting().create().toJson(result);
            JSONArray jsonArr = new JSONArray(con);
            for (int i = 0; i < jsonArr.length(); i++) {

                JSONObject object = jsonArr.getJSONObject(i);
                ContactItem contactItem = new ContactItem();
                contactItem.setDisplayName(object.getString("displayName"));
                JSONArray jsonArr2 = object.getJSONArray("phoneNumbers");

                for (int j = 0; j < jsonArr2.length(); j++) {
                    JSONObject object2 = jsonArr2.getJSONObject(j);
                    contactItem.setPhoneNumbers(object2.getString("number"));
                    listItem.add(contactItem);
                }
            }
            setupRecyclerView(listItem);
        } catch (JSONException e) {
        }
    }

    private void setupRecyclerView(List<ContactItem> itemList) {

       adapter = new RecyclerViewContactAdapter(this, itemList);
        Button button = (Button)findViewById(R.id.contact_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data="";
                String displayInfo="";
                int count = 0;

                List<ContactItem> ctList = ((RecyclerViewContactAdapter) adapter).getStudentist();
                List<ContactItemForServer> ctServer = new ArrayList<>();


                for(int i =0; i<ctList.size(); i++){
                    ContactItem singleContact = ctList.get(i);
                    if(singleContact.isSelected() == true){
                        data=data+singleContact.getPhoneNumbers();
                        displayInfo=displayInfo+singleContact.getDisplayName()+", ";
                        count++;
                        ctList.get(i);
                        ContactItemForServer contactItemForServer = new ContactItemForServer();
                        contactItemForServer.setName(singleContact.getDisplayName());
                        contactItemForServer.setNum(singleContact.getPhoneNumbers());
                        ctServer.add(contactItemForServer);


                    }
                }
                String contact_json = new Gson().toJson(ctServer);
                Log.d("List",contact_json);
                int last = displayInfo.length() - 2;
                if (last > 0 && displayInfo.charAt(last) == ',') {
                    displayInfo = displayInfo.substring(0, last);
                }

                Log.d("DisplayInfo",displayInfo);
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ContactInfo",displayInfo);
                editor.putString("contact_json",contact_json);
                editor.putString("count",String.valueOf(count));
                editor.putInt("Code",777);
                editor.apply();
                editor.commit();

                Toast.makeText(ContactActivity.this,
                        "Selected Students:" +data, Toast.LENGTH_LONG)
                        .show();
                finish();

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

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
            getContacts();

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
                        getContacts();

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





}
