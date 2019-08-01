package com.dpk.pa.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends AppCompatActivity implements OnRecyclerViewItemListener {

    RecyclerView accountRecyclerView;
    List<Account> accountList = new ArrayList<Account>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_account_list);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountRecyclerView.setHasFixedSize(true);
        // Data
        Account account = new Account("01743972128","Dipok",0,0);
        for (int i=0; i < 10; i++){
            accountList.add(account);
        }
        // Data
        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_account,accountList.size());
        accountRecyclerView.setAdapter(accountRecyclerViewListAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public void listenItem(View view, int position) {
        TextView phoneText, nameText, depositText, dueText;
        phoneText = (TextView) view.findViewById(R.id.text_view_card_account_phone);
        nameText = (TextView) view.findViewById(R.id.text_view_card_account_name);
        depositText = (TextView) view.findViewById(R.id.text_view_card_account_deposit);
        dueText = (TextView) view.findViewById(R.id.text_view_card_account_due);

        phoneText.setText(accountList.get(position).getPhone());
        nameText.setText(accountList.get(position).getName());
        depositText.setText(accountList.get(position).getDeposit()+"");
        dueText.setText(accountList.get(position).getDue()+"");
    }
}
