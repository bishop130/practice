package com.suji.lj.myapplication.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemFriendRequest;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;

import java.util.List;

public class RecyclerWaitingFriendAdapter extends RecyclerView.Adapter<RecyclerWaitingFriendAdapter.ViewHolder> {


    Context context;
    List<ItemRegisterAccount> list;

    public RecyclerWaitingFriendAdapter(Context context, List<ItemRegisterAccount> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_waiting_friend, parent, false);

        return new RecyclerWaitingFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.friends_name.setText(list.get(i).getUser_name());

        holder.friends_image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.friends_image.setClipToOutline(true);


        if (list.get(i).getThumnail_img() != null) {
            Picasso.with(context)
                    .load(list.get(i).getThumnail_img())
                    .fit()
                    .into(holder.friends_image);


        }

        holder.friend_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_id = Account.getUserId(context);
                String friend_id = list.get(i).getUser_id();
                //상대 친구요청리스트에서 삭제

                if (user_id != null) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("user_data").child(friend_id).child("friend_request").child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    //databaseReference.child("user_data").child(user_id).child("friends_list").

                                }
                            });
                        }
                    });
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView friends_name;
        ImageView friends_image;
        TextView friend_accept;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friends_name = itemView.findViewById(R.id.friends_name);
            friends_image = itemView.findViewById(R.id.friends_image);
            friend_accept = itemView.findViewById(R.id.friend_accept);


        }
    }
}
