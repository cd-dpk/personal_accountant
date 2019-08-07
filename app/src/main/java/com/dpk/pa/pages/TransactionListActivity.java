package com.dpk.pa.pages;

import android.content.Intent;
import android.os.Bundle;
import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.IRegistration;
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

public class TransactionListActivity extends AppCompatActivity implements OnRecyclerViewItemListener, IRegistration {
    RecyclerView transactionRecyclerView;
    View cardAccountView;

    List<TransactionTable> transactionList = new ArrayList<TransactionTable>();
    List<AccountTable> accountList = new ArrayList<AccountTable>();
    Spinner otherPersonsSpinner;
    List<String> persons = new ArrayList<String>();
    String loggedPerson="", targetPerson="";
    PersonalAccountant personalAccountant;
    AccountTable loggedAccount, targetAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitleText = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleText.setText("Previous Transactions");

        cardAccountView = (View) findViewById(R.id.view_card_transaction_net);
        personalAccountant = new PersonalAccountant(this);

        loggedPerson = getIntent().getStringExtra(ApplicationConstants.LOGGED_USER_PHONE_LABEL).toString();
        targetPerson = getIntent().getStringExtra(ApplicationConstants.TARGET_USER_PHONE_LABEL).toString();

        Log.d("CHECK-0", loggedPerson);
        Log.d("CHECK-1", targetPerson);


        transactionRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_account_list);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setHasFixedSize(true);

        otherPersonsSpinner = (Spinner) findViewById(R.id.transaction_list_spinner_person);

        // Data
        loggedAccount = new AccountTable();
        targetAccount=new AccountTable();
        loggedAccount.setPhone(loggedPerson);
        targetAccount.setPhone(targetPerson);
        transactionList = personalAccountant.getTransactionsBetween(loggedAccount, targetAccount, false);
        accountList = personalAccountant.getAllAccountsExcept(loggedAccount);
        persons = personalAccountant.phoneNumbers(accountList);
        Account targetPersonAccount = new Account(targetAccount);
        targetPersonAccount.setGivenTo(personalAccountant.getTotalAmountGivenTo(new AccountTable(targetPersonAccount),loggedAccount));
        targetPersonAccount.setTakenFrom(personalAccountant.getTotalAmountTakenFrom(new AccountTable(targetPersonAccount),loggedAccount));
        setAccountCardView(targetPersonAccount);
        // Data

        ArrayAdapter<String> personsSpinnerAdapter = new ArrayAdapter<String >(TransactionListActivity.this,
                android.R.layout.simple_list_item_1,persons);
        otherPersonsSpinner.setAdapter(personsSpinnerAdapter);

        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_transaction, transactionList.size());
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

    @Override
    public void listenItem(View view, int position) {
        TransactionTable transactionTable = transactionList.get(position);
        TextView transactionDateText, transactionDescriptionText, givenAmountText, takenAmountText;

        transactionDateText = (TextView) view.findViewById(R.id.text_view_card_transaction_date);
        transactionDescriptionText = (TextView) view.findViewById(R.id.text_view_card_transaction_description);
        givenAmountText = (TextView) view.findViewById(R.id.text_view_card_transaction_given_amount);
        takenAmountText = (TextView) view.findViewById(R.id.text_view_card_transaction_taken_amount);

        transactionDateText.setText(transactionTable.getEntryTime());
        transactionDescriptionText.setText(transactionTable.getDescription());

        if (personalAccountant.isTransactionGivenTo(transactionTable, loggedAccount)){
            takenAmountText.setText(transactionTable.getAmount()+"");
            givenAmountText.setText("0");
        }else {
            takenAmountText.setText("0");
            givenAmountText.setText(transactionTable.getAmount()+"");
        }
    }

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}
