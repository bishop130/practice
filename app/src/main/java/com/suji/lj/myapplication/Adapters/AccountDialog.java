package com.suji.lj.myapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AccountDialog extends Dialog {
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private RecyclerView recyclerView_account;
    List<UserAccountItem> userAccountItemList;
    TextView user_name;
    Context context;


    public AccountDialog(@NonNull Context context, List<UserAccountItem> userAccountItemList) {
        super(context);
        this.context = context;
        this.userAccountItemList = userAccountItemList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_account_dialog);



        String fintech_num = context.getSharedPreferences("OpenBanking", MODE_PRIVATE).getString("fintech_num", "");

        //셋팅

        recyclerView_account = findViewById(R.id.recycler_account);
        user_name = findViewById(R.id.user_name);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        if (userAccountItemList.size() > 0) {
            user_name.setText(userAccountItemList.get(0).getUser_name());
        }
        setRecyclerView(fintech_num);
    }
/*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_dialog, container, false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getActivity().getWindow().setAttributes(layoutParams);



        //셋팅
        mPositiveButton=view.findViewById(R.id.pbutton);
        mNegativeButton=view.findViewById(R.id.nbutton);
        recyclerView_account = view.findViewById(R.id.recycler_account);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);
        setRecyclerView();
        return view;
    }


 */


    private void setRecyclerView(String fintech_num) {
        Log.d("계좌", userAccountItemList.size() + "");

        RecyclerAccountAdapter recyclerAccountAdapter = new RecyclerAccountAdapter(context,userAccountItemList,fintech_num);
        recyclerView_account.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_account.setAdapter(recyclerAccountAdapter);


    }
}
