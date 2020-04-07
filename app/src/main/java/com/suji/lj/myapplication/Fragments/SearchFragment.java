package com.suji.lj.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.suji.lj.myapplication.Adapters.FriendPagerAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsListAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
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

public class SearchFragment extends Fragment {
    private RequestQueue requestQueue;
    private Request request;
    private static final String Mission_List_URL = "http://bishop130.cafe24.com/Mission_List.php";
    private List<RecyclerItem> lstRecyclerItem = new ArrayList<>();
    private Context mContext;
    TabLayout tab_ly_friends;
    ViewPager view_pager;

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            Log.d("친구목록아닌거같으",response.toString());

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        tab_ly_friends = view.findViewById(R.id.tab_ly_friends);


        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("약속초대"));
        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("친구목록"));

        view_pager = view.findViewById(R.id.view_pager);

        FriendPagerAdapter adapter = new FriendPagerAdapter
                (getChildFragmentManager(), tab_ly_friends.getTabCount());
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_ly_friends));


        requestFriendsList();

        tab_ly_friends.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        view_pager.setCurrentItem(tab.getPosition());

                        break;
                    case 1:
                        view_pager.setCurrentItem(tab.getPosition());
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

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }



    @Override
    public void onPause(){
        super.onPause();
        Log.d("타이머","onPause_SearchFragment");


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


    public void requestFriends(String is_friend_id) {

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
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onNotSignedUp() {
                        Log.d("카카오친구 ", "onNotSinedup");
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("카카오친구 ", errorResult.toString());
                    }

                    @Override
                    public void onSuccess(AppFriendsResponse result) {
                        // 친구 목록
                        Log.d("친구목록 ", result.getFriends().toString());
                        List<ItemForFriendsList> list = new ArrayList<>();
                        for(int i=0;i<result.getFriends().size();i++){
                            ItemForFriendsList item = new ItemForFriendsList();
                            item.setFriends_name(result.getFriends().get(i).getProfileNickname());
                            item.setFriends_image(result.getFriends().get(i).getProfileThumbnailImage());
                            item.setId(result.getFriends().get(i).getId());
                            item.setUuid(result.getFriends().get(i).getUUID());
                            item.setFavorite(result.getFriends().get(i).isFavorite().getBoolean());
                            list.add(item);

                        }

                        for(int i =0; i<list.size(); i++){
                            String friend_id = String.valueOf(list.get(i).getId());

                            if(is_friend_id.equals(friend_id)){


                            }


                        }

                        //setupRecyclerView(itemForFriendsListList);
                        // context의 beforeUrl과 afterUrl이 업데이트 된 상태.
                    }
                });
    }


}
