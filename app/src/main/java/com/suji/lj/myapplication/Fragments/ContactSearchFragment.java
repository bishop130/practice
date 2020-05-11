package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Adapters.GlobalApplication;
import com.suji.lj.myapplication.Adapters.RecyclerViewContactAdapter;
import com.suji.lj.myapplication.ContactActivity;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContactSearchFragment extends Fragment implements ContactActivity.OnUnCheckFromSelectToContactListener {

    RecyclerView recycler_contact;
    SearchView contact_search;
    RecyclerViewContactAdapter adapter;

    List<ContactItem> listItem;
    RealmResults<ContactItem> realmResults;
    Realm realm;
    ContactActivity.OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener;
    Context context;
    GlobalApplication countContact;

    public ContactSearchFragment(Context context, Realm realm, ContactActivity.OnUnCheckFromSelectToContactListener onUnCheckFromSelectToContactListener) {
        this.realm = realm;
        this.onUnCheckFromSelectToContactListener = onUnCheckFromSelectToContactListener;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contact_search, container, false);


        recycler_contact = view.findViewById(R.id.recycler_contact);
        contact_search = view.findViewById(R.id.contact_search);


        countContact = (GlobalApplication) context.getApplicationContext();
        contact_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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


        //((ContactActivity) getActivity()).setOnPassDataListener(this);


        listItem = Utils.getContacts();
        realmResults = realm.where(ContactItem.class).findAll();

        //selected_item = realm.copyFromRealm(realmResults);
        for (int j = 0; j < realmResults.size(); j++) {

            if (realmResults.get(j).getContact_or_friend() == 0) {
                Log.d("삼시", listItem.get(realmResults.get(j).getPosition()).getPosition() + "전체");
                Log.d("삼시", realmResults.get(j).getPosition() + "선택");
                listItem.get(realmResults.get(j).getPosition()).setSelected(true);
                listItem.get(realmResults.get(j).getPosition()).setAmount(realmResults.get(j).getAmount());
                listItem.get(realmResults.get(j).getPosition()).setPosition(realmResults.get(j).getPosition());
            }

        }

        setupRecyclerView(listItem);
        return view;
    }

    private void setupRecyclerView(List<ContactItem> itemList) {



        adapter = new RecyclerViewContactAdapter(context, itemList, realm);
        //adapter.notifyDataSetChanged();
        recycler_contact.setLayoutManager(new LinearLayoutManager(context));
        recycler_contact.setAdapter(adapter);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ContactActivity) {
            ContactActivity headlinesFragment = (ContactActivity) getActivity();
            headlinesFragment.setOnUnCheckFromSelectToContactListener(this);
        }
    }

    @Override
    public void onUnCheckFromSelectToContact(int position) {
        Log.d("여기로", "오면돼");
        listItem.get(position).setSelected(false);
        adapter.notifyDataSetChanged();
    }
}
