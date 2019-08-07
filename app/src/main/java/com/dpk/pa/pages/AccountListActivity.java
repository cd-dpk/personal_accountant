package com.dpk.pa.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.IRegistration;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.dpk.pa.data_models.db.AccountTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.dpk.pa.R.*;

public class AccountListActivity extends AppCompatActivity implements OnRecyclerViewItemListener, IRegistration {

    RecyclerView accountRecyclerView;
    List<Account> accounts = new ArrayList<Account>();
    View cardAccountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_account_list);
        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        cardAccountView = (View) findViewById(id.view_card_accounts_transaction_net);
        accountRecyclerView = (RecyclerView) findViewById(id.recycler_view_account_list);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountRecyclerView.setHasFixedSize(true);

        // Data
        PersonalAccountant personalAccountant = new PersonalAccountant(this);
        AccountTable exclusiveAccount = personalAccountant.getLoggedAccount();
        Account myAccount = new Account(exclusiveAccount);
        myAccount.setGivenTo(personalAccountant.getTotalAmountGivenTo(exclusiveAccount));
        myAccount.setTakenFrom(personalAccountant.getTotalAmountTakenFrom(exclusiveAccount));

        accounts = personalAccountant.fromTableToObject(personalAccountant.getAllAccountsExcept(exclusiveAccount));
        for (Account account: accounts){
            account.setGivenTo(personalAccountant.getTotalAmountGivenTo(new AccountTable(account),exclusiveAccount));
            account.setTakenFrom(personalAccountant.getTotalAmountTakenFrom(new AccountTable(account),exclusiveAccount));
        }
        // Data
        setAccountCardView(myAccount);
        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this, layout.card_account,accounts.size());
        accountRecyclerView.setAdapter(accountRecyclerViewListAdapter);
        FloatingActionButton fab = findViewById(id.ft_account_list_add);
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
        Account account = accounts.get(position);
        TextView phoneText, nameText, givenToText, takenFromText, amountNetText;
        ImageButton rightArrowButton;
        phoneText = (TextView) view.findViewById(id.text_view_card_account_phone);
        nameText = (TextView) view.findViewById(id.text_view_card_account_name);
        givenToText = (TextView) view.findViewById(id.text_view_card_account_given_to);
        takenFromText = (TextView) view.findViewById(id.text_view_card_account_taken_from);
        rightArrowButton = (ImageButton) view.findViewById(id.button_card_account_right_arrow);
        amountNetText = (TextView) view.findViewById(id.text_view_card_account_account_amount_net);

        phoneText.setText(account.getPhone());
        nameText.setText(account.getName());
        givenToText.setText(account.getGivenTo()+"");
        takenFromText.setText(account.getTakenFrom()+"");
        double diff = account.getGivenTo()-account.getTakenFrom();
        amountNetText.setText(diff+"");
        amountNetText.setTextColor(getResources().getColor(color.red));
        if (diff>=0){
            amountNetText.setText("+"+diff);
            amountNetText.setTextColor(getResources().getColor(color.green));
        }
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

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
            Intent intent = new Intent(AccountListActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    private void setAccountCardView(Account account) {
        TextView givenToText, takenFromText, amountNetText;
        givenToText = (TextView) cardAccountView.findViewById(R.id.text_view_card_transaction_net_given_to);
        takenFromText = (TextView) cardAccountView.findViewById(R.id.text_view_card_transaction_net_taken_from);
        amountNetText = (TextView) cardAccountView.findViewById(R.id.text_view_card_transaction_net_account_amount_net);

        givenToText.setText(account.getGivenTo()+"");
        takenFromText.setText(account.getTakenFrom()+"");
        double diff = account.getGivenTo()-account.getTakenFrom();
        amountNetText.setText(diff+"");
        amountNetText.setTextColor(getResources().getColor(R.color.red));
        if (diff>=0){
            amountNetText.setText("+"+diff);
            amountNetText.setTextColor(getResources().getColor(R.color.green));
        }
    }

}
