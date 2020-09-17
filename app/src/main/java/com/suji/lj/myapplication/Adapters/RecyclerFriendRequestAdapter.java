package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemFriendRequest;
import com.suji.lj.myapplication.Items.ItemRegisterAccount;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


//친구 요청 어댑터
public class RecyclerFriendRequestAdapter extends RecyclerView.Adapter<RecyclerFriendRequestAdapter.ViewHolder> {

    List<ItemRegisterAccount> list;
    Context context;

    public RecyclerFriendRequestAdapter(Context context, List<ItemRegisterAccount> list) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerFriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_friend_request, parent, false);


        return new RecyclerFriendRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerFriendRequestAdapter.ViewHolder holder, int i) {


        String friend_image = list.get(i).getThumbnail();
        holder.name.setText(list.get(i).getUserName());

        holder.thumbnail.setBackground(new ShapeDrawable(new OvalShape()));
        holder.thumbnail.setClipToOutline(true);

        if (friend_image!=null && !friend_image.isEmpty()) {

            Picasso.with(context)
                    .load(friend_image)
                    .fit()
                    .into(holder.thumbnail);
        }

        String user_id = Account.getUserId(context);
        String friend_id = list.get(i).getUserId();
        String email = list.get(i).getEmail();

        holder.accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemRegisterAccount friend_item = new ItemRegisterAccount();

                friend_item.setUserName(list.get(i).getUserName());
                friend_item.setThumbnail(list.get(i).getThumbnail());
                friend_item.setEmail(list.get(i).getEmail());
                friend_item.setUserId(list.get(i).getUserId());
                friend_item.setProfileImg(list.get(i).getProfileImg());
                friend_item.setOpen(list.get(i).isOpen());


                // String user_id = context.getSharedPreferences("Kakao",Context.MODE_PRIVATE).getString("token","");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("user_data").child(friend_id).child("friendAcceptWaiting").child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        databaseReference.child("user_data").child(user_id).child("friendRequest").child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ItemRegisterAccount item = new ItemRegisterAccount();
                                String user_name = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("user_name", "");
                                String thumbnail = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("thumbnail", "");
                                String email = context.getSharedPreferences("Kakao",MODE_PRIVATE).getString("email","");

                                item.setUserName(user_name);
                                item.setThumbnail(thumbnail);
                                item.setUserId(user_id);
                                item.setEmail(email);

                                databaseReference.child("user_data").child(friend_id).child("friendsList").push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseReference.child("user_data").child(user_id).child("friendsList").push().setValue(friend_item);
                                    }
                                });

                            }
                        });

                        notifyDataSetChanged();
                    }
                });




/*
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
                    String thumbnail = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("thumbnail", "");
                    String user_name = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("user_name", "");


                    ItemFriendRequest request = new ItemFriendRequest();
                    request.setName(user_name);
                    request.setThumbnail(thumbnail);

                    if (user_id != null) {
                        Log.d("토클","on");
                        databaseReference.child("user_data").child(item.getUser_id()).child("friend_request").child(user_id).setValue(request);//친구의 요청리스트 등록
                        databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(item.getUser_id()).setValue(request);//나의 요청리스트 현황등록
                    }




                    String user_id = context.getSharedPreferences("Kakao", MODE_PRIVATE).getString("token", "");
                    if(user_id!=null) {
                        Log.d("토클","off");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();//친구의 요청리스트 제거
                        databaseReference.child("user_data").child(item.getUser_id()).child("friend_request").child(user_id).removeValue();
                        databaseReference.child("user_data").child(user_id).child("friend_accept_waiting").child(item.getUser_id()).removeValue();//나의 요청리스트 현황에서 제거

                }


 */
            }
        });
        holder.decline_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("user_data").child(user_id).child("friend_request").child(friend_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        databaseReference.child("user_data").child(friend_id).child("friend_accept_waiting").child(user_id).removeValue();
                        notifyDataSetChanged();
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView thumbnail;
        TextView decline_request;
        TextView accept_request;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.friends_name);
            thumbnail = itemView.findViewById(R.id.friends_image);
            accept_request = itemView.findViewById(R.id.accept_request);
            decline_request = itemView.findViewById(R.id.decline_request);


        }

    }
}
