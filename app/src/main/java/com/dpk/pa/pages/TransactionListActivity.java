package com.dpk.pa.pages;

import android.content.Intent;
import android.os.Bundle;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity implements OnRecyclerViewItemListener {
    RecyclerView transactionRecyclerView;
    List<TransactionTable> transactionList = new ArrayList<TransactionTable>();
    List<AccountTable> accountList = new ArrayList<AccountTable>();
    Spinner otherPersonsSpinner;
    List<String> persons = new ArrayList<String>();
    String loggedPerson="", targetPerson="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loggedPerson = getIntent().getStringExtra(ApplicationConstants.LOGGED_USER_PHONE_LABEL).toString();
        targetPerson = getIntent().getStringExtra(ApplicationConstants.TARGET_USER_PHONE_LABEL).toString();

        Log.d("CHECK-0", loggedPerson);
        Log.d("CHECK-1", targetPerson);

        TextView toolbarTitleText = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleText.setText("Previous Transactions");

        transactionRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_account_list);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setHasFixedSize(true);

        otherPersonsSpinner = (Spinner) findViewById(R.id.transaction_list_spinner_person);


        // Data
        PersonalAccountant personalAccountant = new PersonalAccountant(this);
        AccountTable loggedAccount = new AccountTable(), targetAccount=new AccountTable();
        loggedAccount.setPhone(loggedPerson);
        targetAccount.setPhone(targetPerson);
        transactionList = personalAccountant.getTransactionsBetween(loggedAccount, targetAccount);
        accountList = personalAccountant.getAllAccountsExcept(loggedAccount);
        persons = personalAccountant.phoneNumbers(accountList);
        // Data

        ArrayAdapter<String> personsSpinnerAdapter = new ArrayAdapter<String >(TransactionListActivity.this,
                android.R.layout.simple_list_item_1,persons);
        otherPersonsSpinner.setAdapter(personsSpinnerAdapter);

        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_account, transactionList.size());
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
