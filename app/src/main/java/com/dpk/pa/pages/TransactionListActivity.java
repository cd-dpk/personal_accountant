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
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity implements OnRecyclerViewItemListener, IRegistration {
    RecyclerView transactionRecyclerView;
    View cardAccountView;

    List<TransactionTable> transactionList = new ArrayList<TransactionTable>();
    List<AccountTable> accountList = new ArrayList<AccountTable>();
    List<String> personPhoneNumbers = new ArrayList<String>();
    String loggedPerson="", targetPerson="";
    PersonalAccountant personalAccountant;
    AccountTable loggedAccount, targetAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        personalAccountant = new PersonalAccountant(this);
        personalAccountant.setLanguageInApp();

        cardAccountView = (View) findViewById(R.id.view_card_account);

        loggedPerson = ApplicationConstants.LOGGED_PHONE_NUMBER;
        targetPerson = ApplicationConstants.TARGET_USER_PHONE;

        Log.d("CHECK-0", loggedPerson);
        Log.d("CHECK-1", targetPerson);

        transactionRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_transaction_list);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setHasFixedSize(true);

        // Data
        loggedAccount = new AccountTable();
        loggedAccount.setPhone(loggedPerson);
        accountList = personalAccountant.getAllAccountsExcept(loggedAccount);
        personPhoneNumbers = personalAccountant.phoneNumbers(accountList);

        loadChangeableData(targetPerson);
        // Data

        FloatingActionButton fab = findViewById(R.id.transaction_list_ft_add_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionListActivity.this, TransactionAddActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadChangeableData(String targetPerson) {
        targetAccount=new AccountTable();
        targetAccount.setPhone(targetPerson);
        transactionList = personalAccountant.getTransactionsBetween(loggedAccount, targetAccount, false);
        Account targetPersonAccount = personalAccountant.getTargetAccountDetails(targetAccount, loggedAccount);
        setAccountCardView(targetPersonAccount);
        RecyclerViewListAdapter accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this,R.layout.card_transaction, transactionList.size());
        transactionRecyclerView.setAdapter(accountRecyclerViewListAdapter);
    }

    private void setAccountCardView(Account account) {
        TextView detailedTransactionsText;
        View textHorizontalLineView;

        TextView phoneText, nameText, givenToText, takenFromText, amountNetText;
        ImageButton rightArrowButton;

        textHorizontalLineView = (View) findViewById(R.id.content_transaction_list_text_horizontal_line);
        detailedTransactionsText = (TextView) textHorizontalLineView.findViewById(R.id.text_horizontal_line_text);

        phoneText = (TextView) findViewById(R.id.text_view_card_account_phone);
        nameText = (TextView) findViewById(R.id.text_view_card_account_name);
        givenToText = (TextView) findViewById(R.id.text_view_card_account_given_to);
        takenFromText = (TextView) findViewById(R.id.text_view_card_account_taken_from);
        rightArrowButton = (ImageButton) findViewById(R.id.button_card_account_right_arrow);
        amountNetText = (TextView) findViewById(R.id.text_view_card_account_account_amount_net);

        detailedTransactionsText.setText(R.string.transactions_breakdown);
        phoneText.setText(account.getPhone());
        nameText.setText(account.getName());
        givenToText.setText(account.getGivenTo()+"");
        takenFromText.setText(account.getTakenFrom()+"");
        double diff = account.getGivenTo()-account.getTakenFrom();
        amountNetText.setText(diff+"");
        amountNetText.setTextColor(getResources().getColor(R.color.red));
        if (diff>=0){
            amountNetText.setText("+"+diff);
            amountNetText.setTextColor(getResources().getColor(R.color.green));
        }
        rightArrowButton.setVisibility(View.GONE);
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
