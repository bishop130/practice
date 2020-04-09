package com.suji.lj.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.tabs.TabLayout;
import com.suji.lj.myapplication.Adapters.RecyclerContactFriendSelectAdapter;
import com.suji.lj.myapplication.Fragments.ContactSearchFragment;
import com.suji.lj.myapplication.Fragments.FriendSearchFragment;
import com.suji.lj.myapplication.Items.ContactItem;
import com.github.tamir7.contacts.Contacts;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, RecyclerContactFriendSelectAdapter.OnFriendsCountListener, TabLayout.OnTabSelectedListener, RecyclerContactFriendSelectAdapter.OnRemoveSelectedListener {


    RecyclerContactFriendSelectAdapter recyclerContactFriendSelectAdapter;
    OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener;
    OnUnCheckFromSelectToFriendListener onUnCheckFromSelectToFriendListener;


    RealmResults<ContactItem> tempList;
    RealmResults<ContactItem> tempList2;
    Utils utils = new Utils();

    RecyclerView confirmed_recycler;
    TextView confirm_button;

    List<ContactItem> selected_item = new ArrayList<>();
    Realm realm;
    TabLayout tab_ly;
    RealmResults<ContactItem> realmResults;
    Fragment fa, fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        //Realm.init(this);
        realm = Realm.getDefaultInstance();


        confirmed_recycler = findViewById(R.id.recycler_view_confirmed);

        confirm_button = findViewById(R.id.contact_confirmed);
        tab_ly = findViewById(R.id.tab_ly);
        tab_ly.addTab(tab_ly.newTab().setText("연락처"));
        tab_ly.addTab(tab_ly.newTab().setText("친구"));



        confirm_button.setOnClickListener(this);
        tab_ly.addOnTabSelectedListener(this);


        checkContactPermission();





    }


    public void recyclerViewConfirmed(List<ContactItem> itemList) {
/*
        recyclerContactFriendSelectAdapter = new RecyclerContactFriendSelectAdapter(this,itemList);
        confirmed_recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        confirmed_recycler.setAdapter(recyclerContactFriendSelectAdapter);

 */
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        confirmed_recycler.setLayoutManager(layoutManager);
        recyclerContactFriendSelectAdapter = new RecyclerContactFriendSelectAdapter(this, itemList, this, realm, this);
        confirmed_recycler.setAdapter(recyclerContactFriendSelectAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    public static final int MY_PERMISSIONS_REQUEST_CONTACT = 100;

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.d("퍼미션", "이미 승인받음");
            Contacts.initialize(this);
            initiateTab();
            recyclerViewConfirmed(selected_item);
            fa = new ContactSearchFragment(this,realm, onUnCheckFromSelectToContactListener);
            addFragment(fa);


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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("퍼미션", "여기는 왜 못들어와");
                        Contacts.initialize(this);
                        initiateTab();
                        recyclerViewConfirmed(selected_item);
                        fa = new ContactSearchFragment(this,realm, onUnCheckFromSelectToContactListener);
                        addFragment(fa);

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

        realmResults = realm.where(ContactItem.class).findAll();

        selected_item = realm.copyFromRealm(realmResults);



    }

    public void removeFromSelectToContact(int position) {
        //Log.d("라니스터","포지션 activity"+ position+"   "+listItem.get(position).isSelected());

        onUnCheckFromSelectToContactListener.onUnCheckFromSelectToContact(selected_item.get(position).getPosition());
        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        //listItem.get(selected_item.get(position).getPosition()).setSelected(false);
        selected_item.remove(position);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


        //RealmResults<ContactItem> realmResults = realm.where(ContactItem.class).findAll();
        recyclerContactFriendSelectAdapter.notifyItemRemoved(position);
        recyclerContactFriendSelectAdapter.notifyItemRangeChanged(position, selected_item.size());

        //listItem.remove(position);
        //recyclerContactFriendSelectAdapter.notifyItemRemoved(position);
        //recyclerContactFriendSelectAdapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();


        RealmResults<ContactItem> list = realm.where(ContactItem.class).findAll();
        for (int i = 0; i < list.size(); i++) {
            Log.d("리브", list.get(i).getDisplayName() + "/ " + list.get(i).getAmount() + "/ " + list.get(i).getPosition() + "/ " + i);

        }

        //setupRecyclerView(listItem);


    }

    public void removeFromSelectToFriend(int position) {
        onUnCheckFromSelectToFriendListener.onUnCheckFromSelectToFriend(selected_item.get(position).getPosition());
        selected_item.remove(position);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");

        recyclerContactFriendSelectAdapter.notifyItemRemoved(position);
        recyclerContactFriendSelectAdapter.notifyItemRangeChanged(position, selected_item.size());


    }

    public void addFromContactToSelect(ContactItem item, int position) {
        selected_item.add(item);
        recyclerContactFriendSelectAdapter.notifyItemChanged(position);
        confirm_button.setText("(" + selected_item.size() + ") 선택완료");

    }

    public void removeFromContactToSelect(int position) {
        for (int i = 0; i < selected_item.size(); i++) {
            if (selected_item.get(i).getContact_or_friend() == 0) {
                if (position == selected_item.get(i).getPosition()) {
                    Log.d("리브", selected_item.get(i).getDisplayName() + "/ " + selected_item.get(i).getAmount() + "/ " + selected_item.get(i).getPosition() + "/ " + i);
                    selected_item.remove(i);
                    recyclerContactFriendSelectAdapter.notifyItemRemoved(i);
                    recyclerContactFriendSelectAdapter.notifyItemRangeChanged(i, selected_item.size());
                }
            }
        }

        confirm_button.setText("(" + selected_item.size() + ") 선택완료");


    }

    public void addFromFriendToSelect(ItemForFriends item) {
        ContactItem contactItem = new ContactItem();
        contactItem.setDisplayName(item.getName());
        contactItem.setContact_or_friend(1);
        contactItem.setFriend_selected(true);
        contactItem.setFriend_position(item.getPosition());


        selected_item.add(contactItem);
        recyclerContactFriendSelectAdapter.notifyDataSetChanged();


    }

    public void removeFromFriendToSelect(int position) {
        for (int i = 0; i < selected_item.size(); i++) {
            if (selected_item.get(i).getContact_or_friend() == 1) {
                if (position == selected_item.get(i).getPosition()) {
                    Log.d("리브", selected_item.get(i).getDisplayName() + "/ " + selected_item.get(i).getAmount() + "/ " + selected_item.get(i).getPosition() + "/ " + i);
                    selected_item.remove(i);
                    recyclerContactFriendSelectAdapter.notifyItemRemoved(i);
                    recyclerContactFriendSelectAdapter.notifyItemRangeChanged(i, selected_item.size());

                }
            }
        }

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
                    item.setContact_or_friend(selected_item.get(i).getContact_or_friend());
                    item.setFriend_position(selected_item.get(i).getFriend_position());
                    item.setFriend_selected(selected_item.get(i).isFriend_selected());
                    Log.d("리브", selected_item.get(i).getAmount() + "/" + selected_item.get(i).getDisplayName() + "/" + selected_item.get(i).getPosition());
                }
                selected_item.clear();


            }
        });

        setResult(2, getIntent());
        Log.d("미션카트", realmResults.size() + "");
        finish();
    }


    @Override
    public void onFriendsCount(int num) {
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int p = tab.getPosition();
        tabFragment(p);


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onRemoveSelected(int position) {
        //onRemoveSelectedListener.onRemoveSelected(position);

    }

    public interface OnUnCheckFromSelectToContactListener {
        void onUnCheckFromSelectToContact(int position);


    }

    public void setOnUnCheckFromSelectToContactListener(OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener) {
        this.onUnCheckFromSelectToContactListener = onUnCheckFromSelectToContactListener;
    }

    private void tabFragment(int pos) {
        switch (pos) {
            case 0:
                if (fa == null) {
                    fa = new ContactSearchFragment(this,realm, onUnCheckFromSelectToContactListener);
                    addFragment(fa);

                    Log.d("홈프레그", "fa is null");
                }

                if (fa != null) {
                    showFragment(fa);
                    Log.d("홈프레그", "fa is showing");
                }
                if (fb != null) {
                    hideFragment(fb);
                    Log.d("홈프레그", "fb is hiding");
                }

                break;
            case 1:

                if (fb == null) {
                    fb = new FriendSearchFragment(this, realm, onUnCheckFromSelectToFriendListener);
                    addFragment(fb);
                    Log.d("홈프레그", "fb is null");
                }

                if (fa != null) {
                    hideFragment(fa);
                    Log.d("홈프레그", "fa is hiding");
                }
                if (fb != null) {
                    showFragment(fb);
                    Log.d("홈프레그", "fb is showing");
                }


                break;

        }

    }

    private void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment).commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment).commit();
    }

    public interface OnUnCheckFromSelectToFriendListener {
        void onUnCheckFromSelectToFriend(int position);
    }

    public void setOnUnCheckFromSelectToFriendListener(OnUnCheckFromSelectToFriendListener onUnCheckFromSelectToFriendListener) {
        this.onUnCheckFromSelectToFriendListener = onUnCheckFromSelectToFriendListener;
    }
}
