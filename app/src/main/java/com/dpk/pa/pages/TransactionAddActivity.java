package com.dpk.pa.pages;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TransactionAddActivity extends AppCompatActivity implements IRegistration {

    String targetPhone;
    String transactionDescription;
    String amount;
    PersonalAccountant personalAccountant;
    View progressView;
    Spinner personSpinner;
    TextInputEditText descriptionText, amountText;
    RadioGroup transactionTypeRadioGroup;
    RadioButton transactionTypeGivenRadioButton, transactionTypeTakenRadioButton;
    Button transactionAddButton;
    AccountTable loggedAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_add);

        progressView = (View) findViewById(R.id.transaction_add_progress_view);
        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.rg_transaction_add_type);
        transactionTypeGivenRadioButton = (RadioButton) findViewById(R.id.rb_transaction_add_type_given);
        transactionTypeTakenRadioButton = (RadioButton) findViewById(R.id.rb_transaction_add_type_taken);
        personSpinner = (Spinner) findViewById(R.id.spinner_transaction_add_person);
        amountText = (TextInputEditText) findViewById(R.id.edit_text_transaction_add_amount);
        descriptionText  = (TextInputEditText) findViewById(R.id.edit_text_transaction_add_description);
        transactionAddButton = (Button) findViewById(R.id.button_transaction_add_add);
        personalAccountant = new PersonalAccountant(TransactionAddActivity.this);
        loggedAccount = new AccountTable();
        loggedAccount.setPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
        List<AccountTable> accounts = personalAccountant.getAllAccountsExcept(loggedAccount);
        Log.d("ACCOUNTS", accounts.size()+"");
        final List<String> phones  = new ArrayList<String >();
        for (AccountTable accountTable: accounts){
            phones.add(accountTable.getPhone());
        }
        ArrayAdapter<String> giverStringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,phones);
        personSpinner.setAdapter(giverStringArrayAdapter);

        personSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                targetPhone = phones.get(i);
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

                    if (transactionTypeRadioGroup.getCheckedRadioButtonId() == transactionTypeGivenRadioButton.getId()) {
                        transactionTable.setGiverPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setTakerPhone(targetPhone);
                    } else {
                        transactionTable.setTakerPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setGiverPhone(targetPhone);
                    }
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