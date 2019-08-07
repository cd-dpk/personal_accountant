package com.dpk.pa.pages;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.IRegistration;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.dpk.pa.utils.TimeHandler;

import java.util.ArrayList;
import java.util.List;

public class TransactionAddActivity extends AppCompatActivity implements IRegistration {

    String giverPhone, takerPhone;
    String transactionDescription;
    String amount;
    PersonalAccountant personalAccountant;
    View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_add);


        progressView = (View) findViewById(R.id.transaction_add_progress_view);

        personalAccountant = new PersonalAccountant(TransactionAddActivity.this);
        List<AccountTable> accounts = personalAccountant.getAllAccounts();

        Log.d("ACCOUNTS", accounts.size()+"");
        final List<String> phones  = new ArrayList<String >();
        for (AccountTable accountTable: accounts){
            phones.add(accountTable.getPhone());
        }

        final Spinner giverSpinner, takerSpinner;
        final EditText descriptionText, amountText;
        Button transactionAddButton;
        giverSpinner = (Spinner) findViewById(R.id.spinner_giver);
        ArrayAdapter<String> giverStringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,phones);
        giverSpinner.setAdapter(giverStringArrayAdapter);
        takerSpinner = (Spinner) findViewById(R.id.spinner_taker);
        ArrayAdapter<String> takerStringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,phones);
        takerSpinner.setAdapter(takerStringArrayAdapter);


        descriptionText = (EditText) findViewById(R.id.edit_text_transaction_add_description);
        amountText = (EditText) findViewById(R.id.edit_text_transaction_add_amount);
        transactionAddButton = (Button) findViewById(R.id.button_transaction_add_add);


        giverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                giverPhone = phones.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        takerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                takerPhone = phones.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        transactionAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amountText.getText().equals("") && descriptionText.getText().toString().equals("")) {
                    Toast.makeText(TransactionAddActivity.this, "Please enter Amount and Description Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    amount = amountText.getText().toString();
                    transactionDescription = descriptionText.getText().toString();
                    TransactionTable transactionTable = new TransactionTable();
                    transactionTable.setGiverPhone(giverPhone);
                    transactionTable.setTakerPhone(takerPhone);
                    transactionTable.setAmount(Double.parseDouble(amount));
                    transactionTable.setDescription(transactionDescription);
                    transactionTable.setEntryTime(TimeHandler.now());
                    transactionTable.setTransactionId(transactionTable.generateTransactionID());

                    Log.d("TRANSACTION", transactionTable.toString());
                    new TransactionAddBGTask(transactionTable).execute();
                }
            }
        });

    }

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    private class TransactionAddBGTask extends AsyncTask<String, String, String> {
        TransactionTable transactionTable;

        TransactionAddBGTask(TransactionTable transactionTable) {
            super();
            this.transactionTable = transactionTable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
                if (personalAccountant.insertTransactionIntoDB(transactionTable)) {
                    return Boolean.TRUE.toString();
                }
            return Boolean.FALSE.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressView.setVisibility(View.GONE);
            if (s.equals(Boolean.TRUE.toString())) {
                Intent intent = new Intent(TransactionAddActivity.this, TransactionListActivity.class);
                intent.putExtra(ApplicationConstants.LOGGED_USER_PHONE_LABEL, ApplicationConstants.LOGGED_PHONE_NUMBER);
                intent.putExtra(ApplicationConstants.TARGET_USER_PHONE_LABEL, ApplicationConstants.TARGET_USER_PHONE);
                startActivity(intent);
            }
        }
    }

}
