package com.example.lj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends Activity implements View.OnClickListener {

    private ArrayList<HashMap<String, String>> dataList;
    private ListView mListview;
    private Button mBtnAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mListview = (ListView) findViewById(R.id.listview);
        mBtnAddress = (Button) findViewById(R.id.btnAddress);
        mBtnAddress.setOnClickListener(this);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = (HashMap<String,String>)adapterView.getItemAtPosition(i);
                String result = map.toString();
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

            }
        });
        mListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView = (ListView) adapterView;
                String name = (String)listView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddress:

                /*Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
                startManagingCursor(cursor);

                String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone._ID};

                int[] to = {android.R.id.text1};

                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2,cursor,from,to);
                lv.setAdapter(simpleCursorAdapter);
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
*/
                dataList = new ArrayList<HashMap<String, String>>();
                Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null,
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");


                while (c.moveToNext()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    // 연락처 id 값
                    String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    // 연락처 대표 이름
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    map.put("name", name);

                    // ID로 전화 정보 조회
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);

                    // 데이터가 있는 경우
                    if (phoneCursor.moveToFirst()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        map.put("phone", number);
                    }

                    phoneCursor.close();
                    dataList.add(map);
                }// end while
                c.close();

                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                        dataList,
                        android.R.layout.simple_list_item_2,
                        new String[]{"name", "phone"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                mListview.setAdapter(adapter);


            }
        }


}
