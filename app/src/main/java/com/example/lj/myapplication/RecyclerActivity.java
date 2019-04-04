package com.example.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.myapplication.Adapters.RecyclerAdapter;
import com.example.lj.myapplication.Items.RecyclerItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RecyclerActivity extends AppCompatActivity {
    private ArrayList<RecyclerItem> mItems = new ArrayList<>();
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private List<RecyclerItem> lstRecyclerItem ;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        lstRecyclerItem = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        loadProducts();

    }


    private void loadProducts() {
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Mission_List_URL, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject  = null ;

                        for (int i = 0; i < response.length(); i++) {


                            try {
                                jsonObject = response.getJSONObject(i);
                                RecyclerItem recyclerItem = new RecyclerItem();


                                    recyclerItem.setMissionTitle(jsonObject.getString("MissionTitle"));
                                    recyclerItem.setMissionID(jsonObject.getString("MissionID"));
                                    recyclerItem.setLatitude(jsonObject.getDouble("Latitude"));
                                    recyclerItem.setLongitude(jsonObject.getDouble("Longitude"));
                                    recyclerItem.setMissionTime(jsonObject.getString("MissionTime"));
                                    lstRecyclerItem.add(recyclerItem);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        setupRecyclerView(lstRecyclerItem);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        //adding our stringrequest to queue
        requestQueue = Volley.newRequestQueue(RecyclerActivity.this);
        requestQueue.add(jsonArrayRequest) ;


    }
    private void setupRecyclerView(List<RecyclerItem> lstRecyclerItem) {


        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this,lstRecyclerItem) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

    }



}
