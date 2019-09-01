package com.dpk.pa.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.adapter.RecyclerViewListAdapter;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.OnRecyclerViewItemListener;
import com.dpk.pa.data_models.db.AccountTable;

import java.util.ArrayList;
import java.util.List;

public class SearchPersonActivity extends AppCompatActivity implements OnRecyclerViewItemListener {

    ImageButton arrowBackSearchButton;
    EditText searchText;
    RecyclerView accountRecyclerView;
    RecyclerViewListAdapter accountRecyclerViewListAdapter;
    List<Account> accounts = new ArrayList<Account>();
    PersonalAccountant personalAccountant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_person);

        personalAccountant = new PersonalAccountant(this);
        personalAccountant.setLanguageInApp();
        Log.d("LANG", ApplicationConstants.LANGUAGE_CODE);

        arrowBackSearchButton = (ImageButton) findViewById(R.id.image_search_back);
        searchText = (EditText) findViewById(R.id.edit_text_search);

        checkRegistration(personalAccountant);

        accountRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_account_list);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountRecyclerView.setHasFixedSize(true);

        // Data
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
        accountRecyclerViewListAdapter = new RecyclerViewListAdapter(
                this, R.layout.card_account,accounts.size());
        accountRecyclerView.setAdapter(accountRecyclerViewListAdapter);

        arrowBackSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable string) {
                String text = string.toString();

            }
        });
    }
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
            Intent intent = new Intent(SearchPersonActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void listenItem(View view, final int position) {
        Account account = accounts.get(position);
        TextView phoneText, nameText, givenToText, takenFromText, amountNetText;
        ImageButton rightArrowButton;
        phoneText = (TextView) view.findViewById(R.id.text_view_card_account_phone);
        nameText = (TextView) view.findViewById(R.id.text_view_card_account_name);
        givenToText = (TextView) view.findViewById(R.id.text_view_card_account_given_to);
        takenFromText = (TextView) view.findViewById(R.id.text_view_card_account_taken_from);
        rightArrowButton = (ImageButton) view.findViewById(R.id.button_card_account_right_arrow);
        amountNetText = (TextView) view.findViewById(R.id.text_view_card_account_account_amount_net);

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
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPersonActivity.this, TransactionListActivity.class);
                ApplicationConstants.TARGET_USER_PHONE = accounts.get(position).getPhone();
                startActivity(intent);
            }
        });
    }
}
