package com.dpk.pa.pages;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.IRegistration;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.dpk.pa.utils.TimeHandler;
import com.google.android.material.textfield.TextInputEditText;


public class TransactionAddActivity extends AppCompatActivity implements IRegistration {

    String transactionDescription;
    String amount;
    PersonalAccountant personalAccountant;
    View progressView;
    View cardAccountView;
    TextInputEditText descriptionText, amountText;
    RadioGroup transactionTypeRadioGroup;
    RadioButton transactionTypeGivenRadioButton, transactionTypeTakenRadioButton;
    Button transactionAddButton;
    AccountTable loggedAccount, targetAccount;
    String loggedPerson="", targetPerson="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_add);
        personalAccountant = new PersonalAccountant(TransactionAddActivity.this);
        personalAccountant.setLanguageInApp();

        progressView = (View) findViewById(R.id.transaction_add_progress_view);
        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.rg_transaction_add_type);
        transactionTypeGivenRadioButton = (RadioButton) findViewById(R.id.rb_transaction_add_type_given);
        transactionTypeTakenRadioButton = (RadioButton) findViewById(R.id.rb_transaction_add_type_taken);
        cardAccountView = (View) findViewById(R.id.view_card_account);
        amountText = (TextInputEditText) findViewById(R.id.edit_text_transaction_add_amount);
        descriptionText  = (TextInputEditText) findViewById(R.id.edit_text_transaction_add_description);
        transactionAddButton = (Button) findViewById(R.id.button_transaction_add_add);

        loggedPerson = ApplicationConstants.LOGGED_PHONE_NUMBER;
        targetPerson = ApplicationConstants.TARGET_USER_PHONE;

        Log.d("CHECK-0", loggedPerson);
        Log.d("CHECK-1", targetPerson);

        loggedAccount = new AccountTable();
        loggedAccount.setPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);

        // Data
        loggedAccount = new AccountTable();
        loggedAccount.setPhone(loggedPerson);
        targetAccount=new AccountTable();
        targetAccount.setPhone(targetPerson);
        Account targetPersonAccount = personalAccountant.getTargetAccountDetails(targetAccount, loggedAccount);
        setAccountCardView(targetPersonAccount);
        // Data

        transactionAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amountText.getText().toString().equals("") && descriptionText.getText().toString().equals("")) {
                    Toast.makeText(TransactionAddActivity.this, R.string.warning_transaction_add_entry,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    amount = amountText.getText().toString();
                    transactionDescription = descriptionText.getText().toString();
                    TransactionTable transactionTable = new TransactionTable();

                    if (transactionTypeRadioGroup.getCheckedRadioButtonId() == transactionTypeGivenRadioButton.getId()) {
                        transactionTable.setGiverPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setTakerPhone(targetAccount.getPhone());
                    } else {
                        transactionTable.setTakerPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                        transactionTable.setGiverPhone(targetAccount.getPhone());
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

    private void setAccountCardView(Account account) {
        TextView phoneText, nameText, givenToText, takenFromText, amountNetText;
        ImageButton rightArrowButton;
        phoneText = (TextView) findViewById(R.id.text_view_card_account_phone);
        nameText = (TextView) findViewById(R.id.text_view_card_account_name);
        givenToText = (TextView) findViewById(R.id.text_view_card_account_given_to);
        takenFromText = (TextView) findViewById(R.id.text_view_card_account_taken_from);
        rightArrowButton = (ImageButton) findViewById(R.id.button_card_account_right_arrow);
        amountNetText = (TextView) findViewById(R.id.text_view_card_account_account_amount_net);

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