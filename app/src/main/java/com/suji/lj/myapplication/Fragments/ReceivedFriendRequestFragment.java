package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.suji.lj.myapplication.Adapters.RecyclerInvitationFriendAdapter;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.List;

public class ReceivedFriendRequestFragment extends Fragment {

    private RecyclerView rv_received_friend;
    Activity activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String user_id;
    RelativeLayout rl_is_empty;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_received_friend_request, container, false);


        rv_received_friend = view.findViewById(R.id.rv_received_friend);
        rl_is_empty = view.findViewById(R.id.rl_is_empty);


        user_id = Account.getUserId(activity);

        databaseReference.child("user_data").child(user_id).child("receiveFriendRequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<ItemForFriendsList> list = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForFriendsList item = snapshot.getValue(ItemForFriendsList.class);
                        list.add(item);


                    }

                    rl_is_empty.setVisibility(View.GONE);

                    setupRecyclerView(list);


                } else {
                    rl_is_empty.setVisibility(View.VISIBLE);

                    setupRecyclerView(list);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    private void setupRecyclerView(List<ItemForFriendsList> list) {


        RecyclerAdapter adapter = new RecyclerAdapter(list);

        rv_received_friend.setLayoutManager(new LinearLayoutManager(activity));
        rv_received_friend.setAdapter(adapter);


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        List<ItemForFriendsList> list;


        private RecyclerAdapter(List<ItemForFriendsList> list) {
            this.list = list;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_receive_friend_request, parent, false);

            return new RecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String friend_name = list.get(position).getFriendName();
            String friend_image = list.get(position).getFriendImage();
            String friend_id = list.get(position).getFriendId();


            holder.tv_friend_name.setText(friend_name);


            holder.iv_friend_image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.iv_friend_image.setClipToOutline(true);


            if (friend_image != null && !friend_image.isEmpty()) {


                Picasso.with(activity).load(friend_image).fit().into(holder.iv_friend_image);


            } else {

                holder.iv_friend_image.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));


            }


            holder.tv_accept_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /** 받은 요청함 비우기**/
                    databaseReference.child("user_data").child(user_id).child("receiveFriendRequest").orderByChild("friendId").equalTo(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    /** 친구의 보낸요청 비우기 **/
                    databaseReference.child("user_data").child(friend_id).child("sendFriendRequest").orderByChild("friendId").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    /** 내 친구창에 등록**/

                    databaseReference.child("user_data").child(user_id).child("friend").push().setValue(list.get(position));

                    /** 친구의 친구창에 내정보 등록**/

                    ItemForFriendsList itemFriend = new ItemForFriendsList();
                    String my_image = Account.getUserThumbnail(activity);
                    String my_name = Account.getUserName(activity);
                    String email = Account.getUserEmail(activity);

                    itemFriend.setFriendId(user_id);
                    itemFriend.setFriendImage(my_image);
                    itemFriend.setFriendName(my_name);
                    itemFriend.setEmail(email);


                    databaseReference.child("user_data").child(friend_id).child("friend").push().setValue(itemFriend);

                }
            });

            holder.tv_reject_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** 받은 요청함 비우기**/
                    databaseReference.child("user_data").child(user_id).child("receiveFriendRequest").orderByChild("friendId").equalTo(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    /** **/
                    /** 친구의 보낸요청 비우기 **/
                    databaseReference.child("user_data").child(friend_id).child("sendFriendRequest").orderByChild("friendId").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_friend_name;
            TextView tv_accept_request;
            TextView tv_reject_request;
            ImageView iv_friend_image;


            private ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_friend_name = itemView.findViewById(R.id.tv_friend_name);
                tv_accept_request = itemView.findViewById(R.id.tv_accept_request);
                tv_reject_request = itemView.findViewById(R.id.tv_reject_request);
                iv_friend_image = itemView.findViewById(R.id.iv_friends_image);

            }

        }
    }
}
