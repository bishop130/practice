package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.android.material.navigation.NavigationView;
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
import com.suji.lj.myapplication.Adapters.SearchFriendPagerAdapter;
import com.suji.lj.myapplication.FriendSearchActivity;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemForMultiModeRequest;
import com.suji.lj.myapplication.Items.RecyclerItem;
import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.AES256Cipher;
import com.suji.lj.myapplication.Utils.DateTimeUtils;
import com.suji.lj.myapplication.Utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    TabLayout tab_ly_friends;
    Toolbar toolbar;
    ViewPager view_pager;
    Activity activity;

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            Log.d("친구목록아닌거같으", response.toString());

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        tab_ly_friends = view.findViewById(R.id.tab_ly_friends);
        view_pager = view.findViewById(R.id.view_pager);


        String name = "bishop130@naver.com";
        Log.d("암호", name);


        try {

            byte[] secret_key = AES256Cipher.secretKeyGenerate();
            Log.d("암호", "시크릿  " + secret_key);

            String encoded = AES256Cipher.AES_Encode(name, secret_key);
            Log.d("암호", "인코드   " + encoded);
            String decoded = AES256Cipher.AES_Decode(encoded, secret_key);
            Log.d("암호", "디코드   " + decoded);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));


            Log.d("암호", "에러   " + errors.toString());


        }


        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("친구");
        toolbar.inflateMenu(R.menu.friend_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_friend:
                        Intent intent = new Intent(activity, FriendSearchActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return false;

            }
        });

        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("친구목록"));
        tab_ly_friends.addTab(tab_ly_friends.newTab().setText("약속초대"));



        SearchFriendPagerAdapter adapter = new SearchFriendPagerAdapter
                (getChildFragmentManager(), tab_ly_friends.getTabCount());
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_ly_friends));


        requestFriendsList();

        tab_ly_friends.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
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
    public void onPause() {
        super.onPause();
        Log.d("타이머", "onPause_SearchFragment");


    }

    private void requestFriendsList() {

        OkHttpClient client = new OkHttpClient();

        String url = "https://testapi.openbanking.or.kr/v2.0/inquiry/real_name";


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bank_tran_id", Utils.makeBankTranId());
        params.put("bank_code_std", "004");
        params.put("account_num", "29030204202663");
        params.put("account_holder_info_type", " ");
        params.put("account_holder_info", "901130");
        params.put("tran_dtime", DateTimeUtils.getCurrentTime());


        JSONObject parameter = new JSONObject(params);
        RequestBody formBody = RequestBody.create(JSON, parameter.toString());


        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJUOTkxNTk2OTIwIiwic2NvcGUiOlsib29iIl0sImlzcyI6Imh0dHBzOi8vd3d3Lm9wZW5iYW5raW5nLm9yLmtyIiwiZXhwIjoxNTg2ODU3NDQ0LCJqdGkiOiI3YzdhMDE3OS1iOGU3LTQxYTEtOTBkZi0xOWVhMzlkOTI0MWQifQ.86REUWV7ImJXWr7FtiayZclAmIW_4WO37s5tujR-UXI")
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(callback);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }
}
