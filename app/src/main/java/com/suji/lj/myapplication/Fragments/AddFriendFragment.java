package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

public class AddFriendFragment extends Fragment {


    LinearLayout ly_result;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    EditText et_search_friend;
    TextView tv_friend_name;
    ImageView iv_friend_image;
    TextView tv_request_friend;
    Activity activity;
    ItemForFriendsList itemForFriendsList;
    String user_id;
    AlertDialog loading_dialog;
    String friendId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_friend, container, false);


        ly_result = view.findViewById(R.id.ly_friend_search_result);
        et_search_friend = view.findViewById(R.id.et_search_friend);
        tv_friend_name = view.findViewById(R.id.friend_name);
        iv_friend_image = view.findViewById(R.id.friend_image);
        tv_request_friend = view.findViewById(R.id.tv_request_friend);

        user_id = Account.getUserId(activity);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }


        tv_request_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendRequestFriend();


            }
        });


        et_search_friend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        itemForFriendsList = new ItemForFriendsList();
                        loading_dialog.show();
                        String email = v.getText().toString();
                        Log.d("검색", email);

                        //searchFriend(email);
                        checkIfAlreadyFriend(email);
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


        return view;
    }

    private void checkIfAlreadyFriend(String email) {
        databaseReference.child("user_data").child(user_id).child("friend").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /** 친구창에 검색한 메일이 존재하다면 알림띄우기**/
                    Log.d("친구", "친구창에 검색한 메일이 존재하다면 알림띄우기");

                    loading_dialog.dismiss();
                    Utils.makeAlertDialog("이미 친구로 등록되어있습니다.", activity);

                } else {
                    /** 친구창에 검색한 메일이 없다면 친구요청함 검색하기**/

                    Log.d("친구", "친구창에 검색한 메일이 없다면 친구요청함 검색하기");
                    checkIfSendRequest(email);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkIfSendRequest(String email) {
        databaseReference.child("user_data").child(user_id).child("sendFriendRequest").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /** 보낸요청함에 검색한 메일이 존재하다면 알림띄우기**/
                    Log.d("친구", " 보낸요청함에 검색한 메일이 존재하다면 알림띄우기");
                    //displayResult(itemForFriendsList);
                    ly_result.setVisibility(View.GONE);
                    Utils.makeAlertDialog("이미 친구요청을 보냈습니다.", activity);
                    loading_dialog.dismiss();


                } else {
                    /** 받은친구요청함에 있는지 확인**/

                    checkIfReceiveRequest(email);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkIfReceiveRequest(String email) {

        databaseReference.child("user_data").child(user_id).child("receiveFriendRequest").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ly_result.setVisibility(View.GONE);
                    Utils.makeAlertDialog("이미 받은 친구요청이 있습니다", activity);
                    loading_dialog.dismiss();

                } else {
                    Log.d("친구", " 받은친구함에 검색한 메일이 없다면 친구계정리스트 검색");
                    /** 친구창에 검색한 메일이 없다면 친구계정리스트 검색**/
                    searchFriend(email);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void searchFriend(String email) {
        Log.d("친구 ", "왜못들어와" + email);


        databaseReference.child("account").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("친구 ", "searchFriend");
                if (dataSnapshot.exists()) {
                    /** 검색과 이메일과 일치하는 친구**/
                    Log.d("친구 ", "데이터존재");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemRegisterAccount item = snapshot.getValue(ItemRegisterAccount.class);

                        if (item != null) {

                            String friend_id = item.getUserId();
                            Log.d("친구 ", friend_id);

                            if (!friend_id.equals(user_id)) {
                                displayResult(item);


                            } else {
                                /**본인메일을 검색한 경우 **/
                                ly_result.setVisibility(View.GONE);
                                Utils.makeAlertDialog("검색결과가 없습니다.", activity);
                                loading_dialog.dismiss();


                            }

                        }


                    }


                } else {
                    /** 검색결과에 없는 이메일(오타일 수 있음)**/
                    ly_result.setVisibility(View.GONE);
                    Utils.makeAlertDialog("검색결과가 없습니다.", activity);
                    loading_dialog.dismiss();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * 검색결과 디스플레이
     **/
    private void displayResult(ItemRegisterAccount item) {

        ly_result.setVisibility(View.VISIBLE);
        loading_dialog.dismiss();


        String friendName = item.getUserName();
        String friendImage = item.getThumbnail();
        friendId = item.getUserId();
        itemForFriendsList.setFriendId(item.getUserId());
        itemForFriendsList.setEmail(item.getEmail());
        itemForFriendsList.setFriendImage(item.getThumbnail());
        itemForFriendsList.setFriendName(item.getUserName());


        String me_id = Account.getUserId(activity);
        String me_name = Account.getUserName(activity);
        String me_image = Account.getUserThumbnail(activity);
        String email = Account.getUserEmail(activity);
        Log.d("친구", me_name);



        tv_friend_name.setText(friendName);
        iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        iv_friend_image.setClipToOutline(true);

        if (friendImage != null && !friendImage.isEmpty()) {

            Picasso.with(activity).load(friendImage).fit().into(iv_friend_image);
        } else {

            iv_friend_image.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
        }




    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


    /**
     * 친구요청 보내기
     **/
    private void sendRequestFriend() {


        String me_id = Account.getUserId(activity);
        String me_name = Account.getUserName(activity);
        String me_image = Account.getUserThumbnail(activity);
        String email = Account.getUserEmail(activity);
        Log.d("친구", me_name);

        ItemForFriendsList item = new ItemForFriendsList();
        item.setFriendId(me_id);
        item.setFriendName(me_name);
        item.setFriendImage(me_image);
        item.setEmail(email);

        /** 친구의 받은요청함에 등록**/
        databaseReference.child("user_data").child(friendId).child("receiveFriendRequest").push().setValue(item);



        /** 내 보낸요청함에 등록**/
        if (itemForFriendsList != null) {
            databaseReference.child("user_data").child(me_id).child("sendFriendRequest").push().setValue(itemForFriendsList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Log.d("친구", "데이터전송 성공");
                        ly_result.setVisibility(View.GONE);


                    } else {


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d("친구", "error" + e.getMessage());

                }
            });
        }


    }


}
