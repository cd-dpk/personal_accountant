package com.dpk.pa.pages;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class AccountOpenActivity extends AppCompatActivity implements IRegistration {
    TextInputEditText phoneText, nameText, amountText, descriptionText;
    Button okayButton;
    String phone = "", name = "", description="";
    double amount;
    RadioGroup transactionTypeRadioGroup;
    RadioButton transactionTypeGivenRadioButton, transactionTypeTakenRadioButton;
    View progressView;
    PersonalAccountant personalAccountant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_open);

        personalAccountant = new PersonalAccountant(this);
        personalAccountant.setLanguageInApp();
        checkRegistration(personalAccountant);

        phoneText = (TextInputEditText) findViewById(R.id.edit_text_phone_number_open);
        nameText = (TextInputEditText) findViewById(R.id.edit_text_name_open);
        amountText = (TextInputEditText) findViewById(R.id.edit_text_account_open_transaction_amount);
        descriptionText = (TextInputEditText) findViewById(R.id.edit_text_account_open_transaction_description);
        progressView = (View) findViewById(R.id.account_open_progress_view);

        okayButton = findViewById(R.id.button_open);
        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.account_open_transaction_type_rb);
        transactionTypeGivenRadioButton = (RadioButton) findViewById(R.id.account_open_transaction_type_given);
        transactionTypeTakenRadioButton = (RadioButton) findViewById(R.id.account_open_transaction_type_taken);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = phoneText.getText().toString();
                name = nameText.getText().toString();
                description = descriptionText.getText().toString();
                if (phone.equals("") && name.equals("") && amountText.getText().toString().equals("")) {
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone and Name Correctly!",
                            Toast.LENGTH_LONG).show();
//                    phoneText.setFocusable(true);
                } else if (phone.equals("")) {
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                } else if (name.equals("")) {
                    Toast.makeText(AccountOpenActivity.this, "Please enter Name Correctly!",
                            Toast.LENGTH_LONG).show();
                } else if (amountText.getText().toString().equals("")) {
                    Toast.makeText(AccountOpenActivity.this, "Please enter Amount Correctly!",
                            Toast.LENGTH_LONG).show();
                } else {
                    // TODO create account
                    AccountTable accountTable = new AccountTable(phone, name);
                    TransactionTable transactionTable = new TransactionTable();
                    amount = Double.parseDouble(amountText.getText().toString());
                    transactionTable.setAmount(amount);
                    if (transactionTypeRadioGroup.getCheckedRadioButtonId() == transactionTypeGivenRadioButton.getId()) {
                        transactionTable.setGiverPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setTakerPhone(accountTable.getPhone());
                    } else {
                        transactionTable.setTakerPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setGiverPhone(accountTable.getPhone());
                    }
                    if (description.equals("")){
                        transactionTable.setDescription("Account Open");
                    }
                    else {
                        transactionTable.setDescription(description);
                    }
                    transactionTable.setEntryTime(TimeHandler.now());
                    transactionTable.setTransactionId(transactionTable.generateTransactionID());
                    new AccountOpenBackgroundTask(accountTable, transactionTable).execute();
                }
            }
        });
    }

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Intent intent = new Intent(AccountOpenActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    private class AccountOpenBackgroundTask extends AsyncTask<String, String, String> {
        AccountTable accountTable;
        TransactionTable transactionTable;

        AccountOpenBackgroundTask(AccountTable accountTable, TransactionTable transactionTable) {
            super();
            this.accountTable = accountTable;
            this.transactionTable = transactionTable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            if (personalAccountant.insertAccountIntoDB(accountTable)) {
                Log.d("PA", ApplicationConstants.LOGGED_PHONE_NUMBER);
                // TODO create transaction
                if (personalAccountant.insertTransactionIntoDB(transactionTable)) {
//                    Toast.makeText(AccountOpenActivity.this, "Account Created & Transaction Completed!", Toast.LENGTH_LONG).show();
                    return Boolean.TRUE.toString();
                }
            }
            return Boolean.FALSE.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressView.setVisibility(View.GONE);
            if (s.equals(Boolean.TRUE.toString())) {
                Intent intent = new Intent(AccountOpenActivity.this, TransactionHomeActivity.class);
                startActivity(intent);
            }
        }
    }
}
