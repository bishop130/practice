package com.suji.lj.myapplication.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Adapters.RecyclerFriendRequestAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsListAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerInvitationAdapter;
import com.suji.lj.myapplication.Adapters.RecyclerViewDivider;
import com.suji.lj.myapplication.Adapters.RecyclerWaitingFriendAdapter;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemFriendRequest;
import com.suji.lj.myapplication.Items.ItemFriendsList;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FriendsListFragment extends Fragment {

    RecyclerView waiting_friend_recyclerView;
    RecyclerView friend_list_recyclerView;
    RecyclerView friend_request_recyclerView;
    private EditText search_friend;
    LinearLayout ly_add_friend;
    LinearLayout ly_send_request;
    LinearLayout ly_received_request;
    TextView friend_name;
    ImageView friend_image;
    TextView request_friend;
    ImageView text_clear;
    Animation animation;
    Context context;
    LinearLayout ly_error_result;
    TextView tv_error_result;
    List<ItemForFriendsList> list = new ArrayList<>();
    //String user_id = getContext().getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        Log.d("검색", "onCreateView");
        search_friend = view.findViewById(R.id.search_friend);
        search_friend.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        waiting_friend_recyclerView = view.findViewById(R.id.waiting_friend_recyclerView);
        friend_request_recyclerView = view.findViewById(R.id.received_request_recyclerView);
        friend_list_recyclerView = view.findViewById(R.id.friend_recyclerView);
        friend_name = view.findViewById(R.id.friend_name);
        friend_image = view.findViewById(R.id.friend_image);
        request_friend = view.findViewById(R.id.request_friend);
        text_clear = view.findViewById(R.id.text_clear);
        ly_add_friend = view.findViewById(R.id.ly_add_friend);
        ly_send_request = view.findViewById(R.id.ly_send_request);
        ly_received_request = view.findViewById(R.id.ly_received_request);
        ly_error_result = view.findViewById(R.id.ly_error_result);
        tv_error_result = view.findViewById(R.id.tv_error_result);
        friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        friend_image.setClipToOutline(true);


        animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);


        search_friend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String email = v.getText().toString();
                        Log.d("검색", email);
                        searchFriend(email);
                        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        IBinder binder = view.getWindowToken();
                        inputManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);

                        // 검색 동작
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;

            }
        });
        text_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_friend.setText("");
            }
        });
        search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_clear.setVisibility(View.VISIBLE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getKakaoFriend();
        setupFriendListRecyclerView();
        setupWaitingFriendRecyclerView();
        //Log.d("검색","friendList");
        setupFriendRequestRecyclerView();
        //checkIfAlreadyRequest();


        // Inflate the layout for this fragment
        return view;
    }

    private void setupWaitingFriendRecyclerView() {
        String user_id = Account.getUserId(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemRegisterAccount> list = new ArrayList<>();
                Log.d("검색", dataSnapshot.getKey() + "waiting " + dataSnapshot.exists() + "  ");
                if (dataSnapshot.exists()) {
                    ly_send_request.setVisibility(View.VISIBLE);
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemRegisterAccount item = data.getValue(ItemRegisterAccount.class);

                        list.add(item);
                    }

                    RecyclerWaitingFriendAdapter adapter = new RecyclerWaitingFriendAdapter(getContext(), list);
                    waiting_friend_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    waiting_friend_recyclerView.setAdapter(adapter);
                } else {
                    ly_send_request.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //본인제외 검색 && 요청대기 제
    private void searchFriend(String email) {
        String user_id = Account.getUserId(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (email.contains("@")) {
            String str = email.substring(0, email.indexOf("@"));

            databaseReference.child("account").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    String key = dataSnapshot.getKey();

                    ItemRegisterAccount registerAccount = dataSnapshot.getValue(ItemRegisterAccount.class);
                    if (registerAccount != null) {
                        String thumb_img = registerAccount.getThumnail_img();
                        String user_name = registerAccount.getUser_name();
                        String friend_email = registerAccount.getEmail();
                        String friend_id = registerAccount.getUser_id();
                        boolean is_public = registerAccount.isIs_public();
                        if (is_public) {
                            if (!user_id.equals(friend_id)) {//1 본인인지 확

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
                                                ItemForFriendsList item = new ItemForFriendsList();
                                                for (int i = 0; i < result.getFriends().size(); i++) {


                                                    if (friend_id.equals(result.getFriends().get(i).getId() + "")) {


                                                        item.setFriends_name(result.getFriends().get(i).getProfileNickname());
                                                        item.setFriends_image(result.getFriends().get(i).getProfileThumbnailImage());
                                                        item.setId(result.getFriends().get(i).getId());
                                                        item.setUuid(result.getFriends().get(i).getUUID());
                                                        item.setFavorite(result.getFriends().get(i).isFavorite().getBoolean());
                                                        Log.d("친구", item.getFriends_name() + "확인");
                                                    }


                                                }

                                                databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        Log.d("검색", dataSnapshot.getKey() + "getkey" + dataSnapshot.exists() + "  ");

                                                        if (!dataSnapshot.exists()) {//3 친구 요청중인지 확인
                                                            ly_error_result.setVisibility(View.GONE);


                                                            Log.d("검색", "들어왔냐 ");

                                                            databaseReference.child("user_data").child(user_id).child("friend_request").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (!dataSnapshot.exists()) {//4 친구요청이 이미 들어왓는지 확
                                                                        Log.d("친구", "들어왔냐2 ");
                                                                        databaseReference.child("user_data").child(user_id).child("friends_list").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                Log.d("친구", "들어왔냐3 ");
                                                                                ItemRegisterAccount item = dataSnapshot.getValue(ItemRegisterAccount.class);
                                                                                if (item != null) {
                                                                                    String friend_email = item.getEmail();
                                                                                    if (!email.equals(friend_email)) {
                                                                                        Log.d("친구", "진짜완료");

                                                                                        friend_name.setText(user_name);
                                                                                        if (thumb_img != null) {
                                                                                            Picasso.with(getContext())
                                                                                                    .load(thumb_img)
                                                                                                    .fit()
                                                                                                    .into(friend_image);
                                                                                        }
                                                                                        ly_add_friend.setVisibility(View.VISIBLE);


                                                                                        request_friend.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                Log.d("토클", "눌리냐/");

                                                                                                if (key != null) {


                                                                                                    databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(friend_id).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            databaseReference.child("user_data").child(friend_id).child("friend_request").child(user_id).setValue(item);

                                                                                                        }
                                                                                                    });
                                                                                                }


                                                                                                ly_add_friend.setVisibility(View.GONE);
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Log.d("검색", "이미 친구추가함");
                                                                                        ly_add_friend.setVisibility(View.GONE);
                                                                                        ly_error_result.setVisibility(View.VISIBLE);
                                                                                        tv_error_result.setText(email + "와 이미 친구입니다.");

                                                                                    }
                                                                                } else {
                                                                                    friend_name.setText(user_name);
                                                                                    if (thumb_img != null) {
                                                                                        Picasso.with(getContext())
                                                                                                .load(thumb_img)
                                                                                                .fit()
                                                                                                .into(friend_image);
                                                                                    }
                                                                                    ly_add_friend.setVisibility(View.VISIBLE);


                                                                                    request_friend.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            Log.d("토클", "눌리냐/");

                                                                                            if (key != null) {
                                                                                                Log.d("친구", "여기구나11 " + user_id);
                                                                                                Log.d("친구", "여기구나22 " + friend_id);

                                                                                                databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(friend_id).setValue(registerAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        Log.d("친구", "성공");
                                                                                                        // String user_id = context.getSharedPreferences("Kakao",MODE_PRIVATE).getString("user_id","");
                                                                                                        String thumbnail = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("thumbnail", "");
                                                                                                        String user_name = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("user_name", "");

                                                                                                        ItemRegisterAccount profile = new ItemRegisterAccount();
                                                                                                        profile.setUser_id(user_id);
                                                                                                        profile.setThumnail_img(thumbnail);
                                                                                                        profile.setUser_name(user_name);

                                                                                                        databaseReference.child("user_data").child(friend_id).child("friend_request").child(user_id).setValue(profile);
                                                                                                        ly_add_friend.setVisibility(View.GONE);


                                                                                                    }
                                                                                                });
                                                                                            }


                                                                                        }
                                                                                    });


                                                                                }


                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError
                                                                                                            databaseError) {

                                                                            }

                                                                        });


                                                                    } else {


                                                                        //친구요청이 이미 들어옴
                                                                    }


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });


                                                        } else {
                                                            ly_add_friend.setVisibility(View.GONE);
                                                            ly_error_result.setVisibility(View.VISIBLE);
                                                            tv_error_result.setText(email + "의 검색결과가 존재하지 않습니다.");


                                                            Log.d("검색", "존재하지않음");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("검색", "db실패");
                                                    }
                                                });


                                            }

                                            //setupRecyclerView(itemForFriendsListList);
                                            // context의 beforeUrl과 afterUrl이 업데이트 된 상태.

                                        });


                            } else {

                                Log.d("검색", "본인이메일검색함");
                                ly_add_friend.setVisibility(View.GONE);
                                ly_error_result.setVisibility(View.VISIBLE);
                                tv_error_result.setText(email + "의 검색결과가 존재하지 않습니다.");
                            }
                        }else{

                            Toast.makeText(context,"정보가 없거나 친구비공개 설정",Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getContext(), "이메일주소가 정확하지않습니다. ", Toast.LENGTH_LONG).show();
        }


    }

    private void setupFriendRequestRecyclerView() {

        String user_id = Account.getUserId(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("friend_request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemRegisterAccount> list = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    ly_received_request.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemRegisterAccount item = snapshot.getValue(ItemRegisterAccount.class);
                        list.add(item);

                    }
                    RecyclerFriendRequestAdapter adapter = new RecyclerFriendRequestAdapter(getContext(), list);
                    friend_request_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    friend_request_recyclerView.setAdapter(adapter);
                } else {
                    ly_received_request.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setupFriendListRecyclerView() {


        Log.d("검색", "friendList");
        // String user_id = getContext().getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
        String user_id = Account.getUserId(context);
        Log.d("검색", user_id + " user_id");
        List<ItemRegisterAccount> list = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_data").child(user_id).child("friends_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemRegisterAccount item = snapshot.getValue(ItemRegisterAccount.class);

                    list.add(item);
                }
                Log.d("검색", list.size() + "여기까지 와야돼");
                //Log.d("검색",list.get(0).getEmail());
                //Log.d("검색",list.get(0).getUser_name());
                RecyclerFriendsListAdapter adapter = new RecyclerFriendsListAdapter(getContext(), list);
                friend_list_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                friend_list_recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(3000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(3000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("검색", "onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("검색", "onStart");


    }

    public void getKakaoFriend() {

        // offset = 0, limit = 100

    }

    private void checkIsKakaoFriend(String input) {

        Log.d("카카오", list.size() + "몇개야");

        for (int i = 0; i < list.size(); i++) {


        }


    }

}
