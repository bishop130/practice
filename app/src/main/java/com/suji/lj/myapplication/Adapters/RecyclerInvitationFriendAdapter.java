package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Items.ItemForFriendResponseForRequest;
import com.suji.lj.myapplication.R;

import java.util.List;

public class RecyclerInvitationFriendAdapter extends RecyclerView.Adapter<RecyclerInvitationFriendAdapter.ViewHolder>{


    Context context;
    List<ItemForFriendResponseForRequest> list;
    public RecyclerInvitationFriendAdapter(Context context, List<ItemForFriendResponseForRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_multi_info_friend, parent, false);



        return new RecyclerInvitationFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.friend_name.setText(list.get(i).getFriendName());
        holder.friend_image.setBackground(new ShapeDrawable(new OvalShape()));
        holder.friend_image.setClipToOutline(true);
        String thumbnail = list.get(i).getFriendImage();
        int accept = list.get(i).getAccept();


        if (thumbnail!=null && !thumbnail.isEmpty()) {
            Picasso.with(context).load(list.get(i).getFriendImage()).fit().into(holder.friend_image);
        }
        if(accept==0){
            holder.check_state.setImageDrawable(changeDrawableColor(context,R.drawable.faq_icon, context.getResources().getColor(R.color.colorPrimary)));


        }else if(accept ==1){
            holder.check_state.setImageDrawable(changeDrawableColor(context,R.drawable.ic_success, context.getResources().getColor(R.color.colorPrimary)));

        }else{

            holder.check_state.setImageDrawable(changeDrawableColor(context,R.drawable.ic_cancel, context.getResources().getColor(R.color.colorPrimary)));

        }

    }
    public static Drawable changeDrawableColor(Context context, int icon, int newColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon);
        if(mDrawable!=null){
            mDrawable.mutate();
            mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        }

        return mDrawable;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView friend_name;
        ImageView friend_image;
        ImageView check_state;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            friend_name = itemView.findViewById(R.id.friend_name);
            friend_image = itemView.findViewById(R.id.friend_image);
            check_state = itemView.findViewById(R.id.check_state);



        }

    }
}
