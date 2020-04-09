package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;

import java.text.DecimalFormat;
import java.text.ParseException;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class RecyclerTransferRespectivelyAdapter extends RecyclerView.Adapter<RecyclerTransferRespectivelyAdapter.ViewHolder> {

    Realm realm;
    RealmResults<ContactItem> realmResults;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    Context context;
    private boolean hasFractionalPart;


    public RecyclerTransferRespectivelyAdapter(Context context, RealmResults<ContactItem> realmResults, Realm realm) {

        this.context = context;
        this.realmResults = realmResults;
        this.realm = realm;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(realmResults.get(position).getContact_or_friend()==1) {

            holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.name.setText("# " + realmResults.get(position).getDisplayName());
        }else{
            holder.name.setText("# " + realmResults.get(position).getDisplayName());

        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler_transfer_respectively, parent, false);


        return new RecyclerTransferRespectivelyAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.transfer_respectively_name);


        }

    }
}
