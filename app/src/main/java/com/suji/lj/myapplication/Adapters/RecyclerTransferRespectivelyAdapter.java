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

        holder.name.setText("# " + realmResults.get(position).getDisplayName() + "/" + realmResults.get(position).getPosition());
        holder.et.setText(df.format(realmResults.get(position).getAmount()));


        holder.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
                    hasFractionalPart = true;
                } else {
                    hasFractionalPart = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 0) {

                } else {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            Log.d("포지", position+""+s);
                            ContactItem contactItem = realm.where(ContactItem.class).equalTo("position", realmResults.get(position).getPosition()).findFirst();

                            int amount = Integer.valueOf(s.toString().replaceAll(",", ""));
                            //realmResults.get(position).setAmount(amount);
                            contactItem.setAmount(amount);
                            Log.d("포지",contactItem.getDisplayName()+"/"+contactItem.getAmount());
                            ((SingleModeActivity) context).amountDisplay();

                        }
                    });


                }



                /*
                realmResults.get(position).setAmount(amount);



                 */


                Log.d("오픈뱅킹", "after_changed");
                holder.et.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = holder.et.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = holder.et.getSelectionStart();
                    if (hasFractionalPart) {
                        holder.et.setText(df.format(n));
                    } else {
                        holder.et.setText(dfnd.format(n));
                    }
                    endlen = holder.et.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= holder.et.getText().length()) {
                        holder.et.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        holder.et.setSelection(holder.et.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }
                holder.et.addTextChangedListener(this);


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
        TextInputEditText textInputEditText;
        private TextInputEditText et;
        private TextInputLayout etl;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.transfer_respectively_name);
            et = itemView.findViewById(R.id.et_transfer_respectively_amount);


        }

    }
}
