package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerTransactionAdapter;
import com.suji.lj.myapplication.Items.ItemForTransaction;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    /**
     * 포인트 충전
     * 분배금
     * 상품구입
     * 포인트 사용
     **/


    RecyclerView rv_transaction;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;
    Toolbar toolbar;
    TextView tvNoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        user_id = Account.getUserId(this);
        rv_transaction = findViewById(R.id.rv_transaction);
        toolbar = findViewById(R.id.toolbar);
        tvNoList = findViewById(R.id.tvNoList);


        toolbar.setTitle("포인트 사용내역");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        databaseReference.child("user_data").child(user_id).child("transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemForTransaction> list = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    tvNoList.setVisibility(View.GONE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForTransaction item = snapshot.getValue(ItemForTransaction.class);
                        if (item != null) {


                            list.add(item);

                        }


                    }
                    Collections.reverse(list);
                    setupRecyclerView(list);
                } else {

                    tvNoList.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setupRecyclerView(List<ItemForTransaction> list) {
        RecyclerTransactionAdapter adapter = new RecyclerTransactionAdapter(this, list);
        rv_transaction.setLayoutManager(new LinearLayoutManager(this));
        rv_transaction.setAdapter(adapter);

        Utils.drawRecyclerViewDivider(this, rv_transaction);
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
