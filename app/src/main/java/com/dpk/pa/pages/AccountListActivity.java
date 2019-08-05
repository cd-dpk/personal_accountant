package com.dpk.pa.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.Tuple;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends AppCompatActivity implements OnRecyclerViewItemListener {

    RecyclerView accountRecyclerView;
    List<Account> accounts = new ArrayList<Account>();
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
        PersonalAccountant personalAccountant = new PersonalAccountant(this);
        AccountTable exclusiveAccount = personalAccountant.getLoggedAccount();
        accounts = personalAccountant.fromTableToObject(personalAccountant.getAllAccountsExcept(exclusiveAccount));
        for (Account account: accounts){
            account.setGivenTo(personalAccountant.getTotalAmountGivenTo(new AccountTable(account),exclusiveAccount));
            account.setTakenFrom(personalAccountant.getTotalAmountGivenTo(new AccountTable(account),exclusiveAccount));
        }
        // Data
        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_account,accounts.size());
        accountRecyclerView.setAdapter(accountRecyclerViewListAdapter);

        FloatingActionButton fab = findViewById(R.id.ft_account_list_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountListActivity.this, AccountOpenActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void listenItem(View view, final int position) {
        TextView phoneText, nameText, givenToText, takenFromText;
        ImageButton rightArrowButton;
        phoneText = (TextView) view.findViewById(R.id.text_view_card_account_phone);
        nameText = (TextView) view.findViewById(R.id.text_view_card_account_name);
        givenToText = (TextView) view.findViewById(R.id.text_view_card_account_given_to);
        takenFromText = (TextView) view.findViewById(R.id.text_view_card_account_taken_from);
        rightArrowButton = (ImageButton) view.findViewById(R.id.card_account_button_right_arrow);

        phoneText.setText(accounts.get(position).getPhone());
        nameText.setText(accounts.get(position).getName());
        givenToText.setText(accounts.get(position).getGivenTo()+"");
        takenFromText.setText(accounts.get(position).getTakenFrom()+"");
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountListActivity.this, TransactionListActivity.class);
                ApplicationConstants.TARGET_USER_PHONE = accounts.get(position).getPhone();
                intent.putExtra(ApplicationConstants.LOGGED_USER_PHONE_LABEL, ApplicationConstants.LOGGED_PHONE_NUMBER);
                intent.putExtra(ApplicationConstants.TARGET_USER_PHONE_LABEL, ApplicationConstants.TARGET_USER_PHONE);
                startActivity(intent);
            }
        });
    }
}
