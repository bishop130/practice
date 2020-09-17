package com.suji.lj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.friends.AppFriendContext;
import com.kakao.friends.response.AppFriendsResponse;
import com.kakao.friends.response.model.AppFriendInfo;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.squareup.picasso.Picasso;
import com.suji.lj.myapplication.Adapters.RecyclerFriendsAdapter;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.ItemForFriends;
import com.suji.lj.myapplication.Items.ItemForFriendsList;
import com.suji.lj.myapplication.Items.ItemFriendsList;
import com.suji.lj.myapplication.Utils.Account;
import com.suji.lj.myapplication.Utils.HangulUtils;
import com.suji.lj.myapplication.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * SingleModeActivity 에서 카카오친구 선택
 **/
public class SelectKakaoFriendActivity extends AppCompatActivity {

    RecyclerView rvKakaoFriend;
    RecyclerView rvSelectedFriend;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    AppFriendContext friendContext = new AppFriendContext(true, 0, 100, "asc");

    List<ItemForFriends> friendsList = new ArrayList<>();
    List<ItemForFriends> selectedList = new ArrayList<>();
    Realm realm;
    String userId;
    Toolbar toolbar;
    TextView tvSelectConfirm;
    RealmResults<ItemForFriends> realmResults;

    FriendAdapter friendAdapter;
    SelectedAdapter selectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_kakao_friend);
        rvKakaoFriend = findViewById(R.id.rvKakaoFriend);
        rvSelectedFriend = findViewById(R.id.rvSelectedFriend);
        toolbar = findViewById(R.id.toolbar);
        tvSelectConfirm = findViewById(R.id.tvSelectConfirm);

        realm = Realm.getDefaultInstance();
        userId = Account.getUserId(this);
        toolbar.setTitle("친구선택");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //databaseReference.child("user_data").child(userId).child("friend_list").


        selectedAdapter = new SelectedAdapter();
        rvSelectedFriend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedFriend.setAdapter(selectedAdapter);
        friendAdapter = new FriendAdapter();
        rvKakaoFriend.setLayoutManager(new LinearLayoutManager(SelectKakaoFriendActivity.this));
        rvKakaoFriend.setAdapter(friendAdapter);

        Utils.drawRecyclerViewDivider(this, rvKakaoFriend);

        tvSelectConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("몇명", selectedList.size() + "");
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmResults = realm.where(ItemForFriends.class).findAll();
                        realmResults.deleteAllFromRealm();
                        for (int i = 0; i < selectedList.size(); i++) {


                            ItemForFriends item = realm.createObject(ItemForFriends.class);
                            item.setPosition(selectedList.get(i).getPosition());
                            item.setName(selectedList.get(i).getName());
                            item.setSelected(selectedList.get(i).isSelected());
                            item.setImage(selectedList.get(i).getImage());
                            item.setUuid(selectedList.get(i).getUuid());
                            item.setId(selectedList.get(i).getId());

                            //Log.d("리브", selected_item.get(i).getAmount() + "/" + selected_item.get(i).getDisplayName() + "/" + selected_item.get(i).getPosition());
                        }
                    }
                });

                //selectedList.clear();

                setResult(2, getIntent());
                finish();

            }
        });


        databaseReference.child("user_data").child(userId).child("friend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot shot : snapshot.getChildren()){
                        ItemForFriendsList item = shot.getValue(ItemForFriendsList.class);

                        if(item!=null) {
                            ItemForFriends itemForFriends = new ItemForFriends();
                            String uuid = item.getUuid();
                            String userName = item.getFriendName();
                            String userImage = item.getFriendImage();
                            String userId = item.getFriendId();
                            // 메시지 전송 시 사용

                            itemForFriends.setUuid(uuid);
                            itemForFriends.setName(userName);
                            itemForFriends.setImage(userImage);
                            itemForFriends.setId(userId);
                            itemForFriends.setSelected(false);
                            friendsList.add(itemForFriends);
                        }

                    }

                    long count = realm.where(ItemForFriends.class).count();
                    if (count != 0) {
                        realmResults = realm.where(ItemForFriends.class).findAll();

                        selectedList = realm.copyFromRealm(realmResults);
                        tvSelectConfirm.setText("(" + selectedList.size() + ") 선택완료");


                        for (int i = 0; i < realmResults.size(); i++) {
                            String realmId = realmResults.get(i).getId();
                            for (int j = 0; j < friendsList.size(); j++) {
                                String listId = friendsList.get(j).getId();
                                if (realmId.equals(listId)) {

                                    int idx = realmResults.get(i).getPosition();
                                    friendsList.get(idx).setSelected(true);

                                }


                            }


                        }

                    }


                    friendAdapter.notifyDataSetChanged();
                    selectedAdapter.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ItemViewHolder> implements Filterable {

        //private List<ItemForFriends> itemList;
        private List<ItemForFriends> exampleListFull;
        // private List<ItemForFriends> selected_contact = new ArrayList<>();

        private int count = 0;
        private static final int MAX_CONTACTS = 10;

        private Realm realm;

        public FriendAdapter() {

        }

        public FriendAdapter(Context context, List<ItemForFriends> itemList, Realm realm) {


            this.exampleListFull = new ArrayList<>(itemList);
            this.realm = realm;

        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater inflater = LayoutInflater.from(SelectKakaoFriendActivity.this);
            view = inflater.inflate(R.layout.item_friend_select, parent, false);

            return new FriendAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FriendAdapter.ItemViewHolder holder, final int position) {

            ItemForFriends itemForFriends = friendsList.get(position);
            String friendName = friendsList.get(position).getName();
            String friendImage = friendsList.get(position).getImage();
            String friendId = friendsList.get(position).getId();
            String friendUuid = friendsList.get(position).getUuid();
            boolean isSelect = friendsList.get(position).isSelected();


            holder.tvFriendName.setText(friendName);
            //holder.first_name.setText(Strin
            // g.valueOf(itemList.get(position).getName().charAt(0)));
            //holder.contact_container.setBackgroundColor(isSelect ? Color.WHITE : Color.WHITE);
            holder.ivSelect.setImageDrawable(isSelect ? getResources().getDrawable(R.drawable.checked_icon) : getResources().getDrawable(R.drawable.ring));

            holder.ivFriendImage.setBackground(new ShapeDrawable(new OvalShape()));
            holder.ivFriendImage.setClipToOutline(true);


            if (friendImage != null && !friendImage.isEmpty()) {
                Picasso.with(SelectKakaoFriendActivity.this).load(friendImage).fit().into(holder.ivFriendImage);
                Log.d("이미지", "이미지있음");
            } else {
                holder.ivFriendImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));

            }

            //holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_icon : R.drawable.contact_circle);
            //holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getName().charAt(0)));


            holder.lyContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //friendsList.get(position).setSelected(!isSelect); //스위치

                    if (!isSelect) {

                        Log.d("이미지", "selct");

                        itemForFriends.setSelected(true);
                        itemForFriends.setPosition(position);

                        selectedList.add(itemForFriends);
                        friendsList.get(position).setSelected(true);
                        selectedAdapter.notifyItemInserted(selectedList.size());
                        friendAdapter.notifyDataSetChanged();
                        tvSelectConfirm.setText("(" + selectedList.size() + ") 선택완료");

                    } else {


                        Log.d("이미지", "unselct");
                        itemForFriends.setSelected(false);

                        friendsList.get(position).setSelected(false);

                        for (int i = 0; i < selectedList.size(); i++) {
                            String listId = friendsList.get(position).getId();
                            String selId = selectedList.get(i).getId();
                            if (listId.equals(selId)) {

                                selectedList.remove(i);
                                selectedAdapter.notifyItemRemoved(i);
                                selectedAdapter.notifyItemRangeChanged(i, selectedList.size());
                            }


                        }
                        //selectedAdapter.notifyDataSetChanged();
                        friendAdapter.notifyDataSetChanged();
                        tvSelectConfirm.setText("(" + selectedList.size() + ") 선택완료");


                    }


                }
            });


        }

        @Override
        public void onViewRecycled(@NonNull FriendAdapter.ItemViewHolder holder) {
            super.onViewRecycled(holder);

        }

        @Override
        public int getItemCount() {
            return friendsList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ItemForFriends> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(exampleListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ItemForFriends item : exampleListFull) {
                        String miniName = HangulUtils.getHangulInitialSound(item.getName(), constraint.toString());
                        if (miniName.indexOf(constraint.toString()) >= 0) {
                            filteredList.add(item);
                        } else if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                friendsList.clear();
                friendsList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };


        private void iterator(int position) {
            for (int i = 0; i < selectedList.size(); i++) {
                ItemForFriends ct = selectedList.get(i);
                if (ct.getPosition() == position) {
                    int idx = i;
                    selectedList.remove(idx);


                }


            }
            //((ContactActivity) context).recyclerViewConfirmed(selected_contact);
        }


        private class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView tvFriendName;
            LinearLayout lyContainer;
            ImageView ivFriendImage;
            ImageView ivSelect;

            private ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                lyContainer = itemView.findViewById(R.id.ly_friend);
                tvFriendName = itemView.findViewById(R.id.friend_name);
                ivFriendImage = itemView.findViewById(R.id.thumbnail);
                ivSelect = itemView.findViewById(R.id.friend_select_box);


            }

        }

    }

    private class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.ItemViewHolder> {

        //private List<ItemForFriends> itemList;
        private List<ItemForFriends> exampleListFull;

        private int count = 0;
        private static final int MAX_CONTACTS = 10;

        private Realm realm;

        public SelectedAdapter() {

        }

        public SelectedAdapter(Context context, List<ItemForFriends> itemList, Realm realm) {

            this.exampleListFull = new ArrayList<>(itemList);
            this.realm = realm;

        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;
            LayoutInflater inflater = LayoutInflater.from(SelectKakaoFriendActivity.this);
            view = inflater.inflate(R.layout.item_friend_image, parent, false);

            return new SelectedAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SelectedAdapter.ItemViewHolder holder, final int position) {

            //  ItemForFriends itemForFriends = selectedList.get(position);
            String friendName = selectedList.get(position).getName();
            String friendImage = selectedList.get(position).getImage();
            String friendId = selectedList.get(position).getId();
            String friendUuid = selectedList.get(position).getUuid();
            boolean isSelect = selectedList.get(position).isSelected();


            holder.tvFriendName.setText(friendName);
            //holder.first_name.setText(Strin
            // g.valueOf(itemList.get(position).getName().charAt(0)));
            //holder.contact_container.setBackgroundColor(isSelect ? Color.WHITE : Color.WHITE);
            holder.ivSelect.setImageDrawable(isSelect ? getResources().getDrawable(R.drawable.checked_icon) : getResources().getDrawable(R.drawable.ring));

            holder.ivFriendImage.setBackground(new ShapeDrawable(new OvalShape()));
            holder.ivFriendImage.setClipToOutline(true);


            if (friendImage != null && !friendImage.isEmpty()) {
                Picasso.with(SelectKakaoFriendActivity.this).load(friendImage).fit().into(holder.ivFriendImage);
                Log.d("이미지", "이미지있음");
            } else {
                holder.ivFriendImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));

            }

            //holder.first_name.setBackgroundResource(itemList.get(position).isSelected() ? R.drawable.checked_icon : R.drawable.contact_circle);
            //holder.first_name.setText(itemList.get(position).isSelected() ? "" : String.valueOf(itemList.get(position).getName().charAt(0)));


            holder.lyFriendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int idx = selectedList.get(position).getPosition();
                    friendsList.get(idx).setSelected(false);

                    friendAdapter.notifyDataSetChanged();
                    selectedList.remove(position);
                    selectedAdapter.notifyItemRemoved(position);
                    selectedAdapter.notifyItemRangeChanged(position, selectedList.size());

                    tvSelectConfirm.setText("(" + selectedList.size() + ") 선택완료");


                }
            });


        }


        @Override
        public int getItemCount() {
            return selectedList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView tvFriendName;
            LinearLayout lyFriendContainer;
            ImageView ivFriendImage;
            ImageView ivSelect;

            private ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                lyFriendContainer = itemView.findViewById(R.id.lyFriendContainer);
                tvFriendName = itemView.findViewById(R.id.tv_friend_name);
                ivFriendImage = itemView.findViewById(R.id.iv_friend_image);
                ivSelect = itemView.findViewById(R.id.iv_check_state);


            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
