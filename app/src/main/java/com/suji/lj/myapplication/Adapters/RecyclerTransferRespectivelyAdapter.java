package com.suji.lj.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;

import java.text.DecimalFormat;

import io.realm.Realm;
import io.realm.RealmResults;

public class RecyclerTransferRespectivelyAdapter extends RecyclerView.Adapter<RecyclerTransferRespectivelyAdapter.ViewHolder> {

    Realm realm;
    RealmResults<ContactItem> realmResults;
    DecimalFormat df = new DecimalFormat("#,###.##");
    private DecimalFormat dfnd = new DecimalFormat("#,###");
    Context context;
    private boolean hasFractionalPart;
    OnResetAmountDisplayListener onResetAmountDisplayListener;


    public RecyclerTransferRespectivelyAdapter(Context context, RealmResults<ContactItem> realmResults, Realm realm,OnResetAmountDisplayListener onResetAmountDisplayListener) {

        this.context = context;
        this.realmResults = realmResults;
        this.realm = realm;
        this.onResetAmountDisplayListener = onResetAmountDisplayListener;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(realmResults.get(position).getContact_or_friend()==1) {

            holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.name.setText("# " + realmResults.get(position).getDisplayName());
        }else{
            holder.name.setText("# " + realmResults.get(position).getDisplayName());

        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmResults.deleteFromRealm(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,realmResults.size());


                    }
                });
                onResetAmountDisplayListener.onResetAmountDisplay();
            }
        });


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

    public interface OnResetAmountDisplayListener{
        void onResetAmountDisplay();

    }
}
