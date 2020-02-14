package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.android.material.tabs.TabLayout;
import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Adapters.KakaoFriends;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsListAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private Request request;
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private KakaoFriends kakaoFriends = new KakaoFriends();
    private Context mContext;
    TabLayout tab_ly_friends;
    List<ItemForFriendsList> itemForFriendsListList = new ArrayList<>();
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            Log.d("친구목록",response.toString());

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_total);
        recyclerView.addItemDecoration(new RecyclerViewDivider(36));
        tab_ly_friends = view.findViewById(R.id.tab_ly_friends);

        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("친구목록"));
        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("친구요청"));

        requestFriendsList();
        requestFriends();

        tab_ly_friends.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        break;
                    case 1:
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String user_id = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE).getString("token","");
        //volleyConnect(user_id);
        kakaoFriends.requestFriends();
        setupRecyclerView();

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timer_switch",true);
        editor.apply();
    }

    private void setupRecyclerView() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("kakao_profile",MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String profile_image = sharedPreferences.getString("profile_image","");

        ItemForFriendsList item = new ItemForFriendsList();
        item.setFriends_name(name);
        item.setFriends_image(profile_image);

        itemForFriendsListList.add(item);

        RecyclerFriendsListAdapter recyclerAdapter = new RecyclerFriendsListAdapter(mContext,itemForFriendsListList);
        //recyclerAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("타이머","onPause_SearchFragment");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timer_switch",false);
        editor.apply();

        boolean timer_switch = mContext.getSharedPreferences("timer_control",Context.MODE_PRIVATE).getBoolean("timer_switch",true);


        Log.d("타이머"," "+timer_switch);

    }

    private void requestFriendsList() {

        OkHttpClient client = new OkHttpClient();
        String user_id = mContext.getSharedPreferences("Kakao",Context.MODE_PRIVATE).getString("token","");

        String url="https://testapi.openbanking.or.kr/v2.0/inquiry/real_name";


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bank_tran_id", Utils.makeBankTranId());
        params.put("bank_code_std", "004");
        params.put("account_num", "29030204202663");
        params.put("account_holder_info_type", " ");
        params.put("account_holder_info", "901130");
        params.put("tran_dtime", DateTimeUtils.getCurrentTime());


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON,parameter.toString());


        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJUOTkxNTk2OTIwIiwic2NvcGUiOlsib29iIl0sImlzcyI6Imh0dHBzOi8vd3d3Lm9wZW5iYW5raW5nLm9yLmtyIiwiZXhwIjoxNTg2ODU3NDQ0LCJqdGkiOiI3YzdhMDE3OS1iOGU3LTQxYTEtOTBkZi0xOWVhMzlkOTI0MWQifQ.86REUWV7ImJXWr7FtiayZclAmIW_4WO37s5tujR-UXI")
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);
    }


    public void requestFriends() {

        // offset = 0, limit = 100
        AppFriendContext friendContext = new AppFriendContext(true, 0, 100, "asc");

        KakaoTalkService.getInstance().requestAppFriends(friendContext,
                new TalkResponseCallback<AppFriendsResponse>() {
                    @Override
                    public void onNotKakaoTalkUser() {
                        Log.d("카카오친구 ", "카카오 유저가 아님");
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {

                    }

                    @Override
                    public void onNotSignedUp() {

                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onSuccess(AppFriendsResponse result) {
                        // 친구 목록
                        Log.d("친구목록 ", result.getFriends().toString());
                        result.getFriends().get(0).getUUID();
                        // context의 beforeUrl과 afterUrl이 업데이트 된 상태.
                    }
                });
    }


}
