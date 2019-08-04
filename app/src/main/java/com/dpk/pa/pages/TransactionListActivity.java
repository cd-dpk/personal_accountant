package com.dpk.pa.pages;

import android.content.Intent;
import android.os.Bundle;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity implements OnRecyclerViewItemListener {
    RecyclerView transactionRecyclerView;
    List<TransactionTable> accountList = new ArrayList<TransactionTable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transactionRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_account_list);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setHasFixedSize(true);
        // Data
        PersonalAccountant personalAccountant = new PersonalAccountant(this);
        accountList = personalAccountant.getTransactions();
        // Data
        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_account,accountList.size());
        transactionRecyclerView.setAdapter(accountRecyclerViewListAdapter);
        FloatingActionButton fab = findViewById(R.id.transaction_list_ft_add_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionListActivity.this, TransactionAddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void listenItem(View view, int position) {

    }
}
