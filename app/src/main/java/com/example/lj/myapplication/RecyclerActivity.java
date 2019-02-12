package com.example.lj.myapplication;

import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.myapplication.databinding.ActivityRecyclerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private String[] names = {"Charlie", "Andrew", "Han", "Liz", "Thomas", "Sky", "Andy", "Lee", "Park"};
    private static final int LAYOUT = R.layout.activity_recycler;
    private RecyclerView.Adapter adapter;
    private ActivityRecyclerBinding recyclerBinding;
    private ArrayList<RecyclerItem> mItems = new ArrayList<>();
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private JSONArray jsonArray;
    private ArrayList<String> MissionTitleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerBinding = DataBindingUtil.setContentView(this, LAYOUT);
        loadProducts();

        setRecyclerView();
        setRefresh();

    }

    private void setRecyclerView() {

        recyclerBinding.recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(mItems);
        recyclerBinding.recyclerView.setAdapter(adapter);

        recyclerBinding.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(4));


        recyclerBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerBinding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerBinding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), position + "번 째 아이템 클릭", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), position + "번 째 아이템 롱 클릭", Toast.LENGTH_SHORT).show();
                    }
                }));

        setData();
    }

    private void setData() {
        mItems.clear();
        String[] array = MissionTitleList.toArray(new String[MissionTitleList.size()]);


        Log.d("MissionSimpe", MissionTitleList.toString());
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        list.add("Haapy");
        list.add("Hackung");
        for (String test : MissionTitleList) {
            Toast.makeText(getApplicationContext(), test, Toast.LENGTH_LONG).show();
        }
        Log.d("MissionTitleArrayList", list.toString());
        for (String name : array) {
            mItems.add(new RecyclerItem(name));

        }

        adapter.notifyDataSetChanged();
    }

    private void setRefresh() {
        recyclerBinding.swipeRefreshLo.setOnRefreshListener(this);
        recyclerBinding.swipeRefreshLo.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
    }

    @Override
    public void onRefresh() {
        recyclerBinding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(recyclerBinding.recyclerView, "Refresh Success", Snackbar.LENGTH_LONG).show();
                recyclerBinding.swipeRefreshLo.setRefreshing(false);
            }
        }, 500);
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
// 마지막 아이템이 아닌 경우, 공백 추가
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = verticalSpaceHeight;
            }
        }
    }

    private void loadProducts() {
        MissionTitleList = new ArrayList<>();
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, Mission_List_URL, (String) null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {


                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String MissionTitle = jsonObject.getString("MissionTitle");

                                if (MissionTitle != "null") {
                                    MissionTitleList.add(MissionTitle);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("MissionTitleList", MissionTitleList.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(jsonArrayRequest);


    }


}
