package com.suji.lj.myapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suji.lj.myapplication.Adapters.RecyclerFriendAdapter;
import com.suji.lj.myapplication.FriendSearchActivity;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendFragment extends Fragment implements RecyclerFriendAdapter.OnEditFriendListener {


    private RecyclerView rv_friend;
    private Activity activity;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private LinearLayout ly_if_no_friend;
    private TextView tv_find_friend;
    String userId;
    AlertDialog loading_dialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_friend, container, false);



        rv_friend = view.findViewById(R.id.rv_friend);
        ly_if_no_friend = view.findViewById(R.id.lyIfNoFriend);
        tv_find_friend = view.findViewById(R.id.tv_find_friend);
        userId = Account.getUserId(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_progress);
        loading_dialog = builder.create();

        if (loading_dialog.getWindow() != null) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }



        tv_find_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FriendSearchActivity.class);
                activity.startActivity(intent);
            }
        });


        databaseReference.child("user_data").child(userId).child("friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemForFriendsList> list = new ArrayList<>();
                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemForFriendsList item = snapshot.getValue(ItemForFriendsList.class);


                        list.add(item);


                    }
                    ly_if_no_friend.setVisibility(View.GONE);


                    setupRecyclerView(list);


                } else {
                    setupRecyclerView(list);
                    ly_if_no_friend.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    private void setupRecyclerView(List<ItemForFriendsList> list) {

        RecyclerFriendAdapter adapter = new RecyclerFriendAdapter(activity,list,this);
        rv_friend.setLayoutManager(new LinearLayoutManager(activity));
        rv_friend.setAdapter(adapter);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }


    @Override
    public void onEditFriend(String friendId) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View view = View.inflate(activity, R.layout.dialog_edit_friend, null);
        TextView tvDeleteFriend = view.findViewById(R.id.tvDeleteFriend);
        TextView tvEditFriendName = view.findViewById(R.id.tvEditFriendName);
        tvDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("친구를 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading_dialog.show();




                        databaseReference.child("user_data").child(userId).child("friend").orderByChild("friendId").equalTo(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot shot : snapshot.getChildren()){
                                        shot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    databaseReference.child("user_data").child(friendId).child("friend").orderByChild("friendId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()){
                                                                for(DataSnapshot shot : snapshot.getChildren()){
                                                                    shot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            loading_dialog.dismiss();
                                                                            if(task.isSuccessful()){
                                                                                bottomSheetDialog.dismiss();
                                                                                Toast.makeText(activity,"삭제되었습니다", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            loading_dialog.dismiss();

                                                        }
                                                    });
                                                }else{
                                                    loading_dialog.dismiss();
                                                }

                                            }
                                        });
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                loading_dialog.dismiss();
                            }
                        });

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
        tvEditFriendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }
}
