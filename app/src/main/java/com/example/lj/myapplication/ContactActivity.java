package com.example.lj.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactActivity extends AppCompatActivity {


    ListView lv;
    Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        lv = (ListView)findViewById(R.id.ListView);
        button1 = (Button)findViewById(R.id.get);
        button2 = (Button)findViewById(R.id.next);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
                startManagingCursor(cursor);

                String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone._ID};

                int[] to = {android.R.id.text1,android.R.id.text2};

                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2,cursor,from,to);
                lv.setAdapter(simpleCursorAdapter);
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this,ResultActivity.class);
                startActivity(intent);
            }
        });
    }


}
