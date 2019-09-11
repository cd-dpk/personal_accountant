package com.dpk.pa.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data_models.IRegistration;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.dpk.pa.utils.TimeHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AccountOpenActivity extends AppCompatActivity implements IRegistration, DatePickerDialog.OnDateSetListener {
    TextInputEditText phoneText, nameText, amountText, descriptionText;
    String phone = "", name = "", description="";
    double amount;
    RadioGroup transactionTypeRadioGroup, transactionTimeRadioGroup;
    RadioButton transactionTypeGivenRadioButton,
            transactionTypeTakenRadioButton,
            transactionTimeNowRadioButton,
            transactionTimeAnotherRadioButton;
    View progressView, errorMessageView, transactionTypeView;
    TextView errorMessageTextView;
    PersonalAccountant personalAccountant;
    CoordinatorLayout coordinatorLayout;
    TextView transactionTimeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_open);

        personalAccountant = new PersonalAccountant(this);
        personalAccountant.setLanguageInApp();
        checkRegistration(personalAccountant);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinate_layout_account_open);
        phoneText = (TextInputEditText) findViewById(R.id.edit_text_phone_number_open);
        nameText = (TextInputEditText) findViewById(R.id.edit_text_name_open);
        amountText = (TextInputEditText) findViewById(R.id.edit_text_account_open_transaction_amount);
        descriptionText = (TextInputEditText) findViewById(R.id.edit_text_account_open_transaction_description);
        progressView = (View) findViewById(R.id.account_open_progress_view);
        errorMessageView = (View) findViewById(R.id.account_open_error_message_view);
        errorMessageTextView = (TextView) errorMessageView.findViewById(R.id.text_view_error_message);
        transactionTypeView = (View) findViewById(R.id.card_view_account_open_type);

        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.account_open_transaction_type_rb);
        transactionTypeGivenRadioButton = (RadioButton) findViewById(R.id.account_open_transaction_type_given);
        transactionTypeTakenRadioButton = (RadioButton) findViewById(R.id.account_open_transaction_type_taken);

        transactionTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == transactionTypeGivenRadioButton.getId()){
                    transactionTypeView.setBackgroundColor(getResources().getColor(R.color.lime));
                }else if(i == transactionTypeTakenRadioButton.getId()){
                    transactionTypeView.setBackgroundColor(getResources().getColor(R.color.orangered));
                }
            }
        });

        transactionTimeRadioGroup = (RadioGroup) findViewById(R.id.rg_transaction_time);
        transactionTimeNowRadioButton = (RadioButton) findViewById(R.id.rb_transaction_time_now);
        transactionTimeAnotherRadioButton = (RadioButton) findViewById(R.id.rb_transaction_time_another_time);
        transactionTimeTextView = (TextView) findViewById(R.id.transaction_time_text_view_picked_date);


        transactionTimeTextView.setText(TimeHandler.now());
        // Data

        transactionTimeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_transaction_time_now){
                    transactionTimeTextView.setText(TimeHandler.now());
                }
                else  if(i == R.id.rb_transaction_time_another_time){
//                    DialogFragment newFragment = new DatePickerFragment();
//                    newFragment.show(getSupportFragmentManager(), "datePicker");
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AccountOpenActivity.this,
                            R.style.MyDialogTheme ,
                            AccountOpenActivity.this,
                            TimeHandler.year(),
                            TimeHandler.month(),
                            TimeHandler.day());

                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            transactionTimeNowRadioButton.setChecked(true);
                            transactionTimeTextView.setText(TimeHandler.now());
                        }
                    });

                    datePickerDialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_open, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_account_open_close){
            Intent intent = new Intent(AccountOpenActivity.this, TransactionHomeActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_account_open_done) {
            phone = phoneText.getText().toString();
            name = nameText.getText().toString();
            description = descriptionText.getText().toString();
            if (phone.equals("") && name.equals("") && amountText.getText().toString().equals("")) {
                /*Toast.makeText(AccountOpenActivity.this, R.string.warning_account_open_phone_name_entry,
                        Toast.LENGTH_LONG).show();*/
                Snackbar.
                        make(coordinatorLayout,R.string.warning_account_open_phone_name_entry,Snackbar.LENGTH_LONG)
                        .show();
//                    phoneText.setFocusable(true);
            } else if (phone.equals("")) {
                /*Toast.makeText(AccountOpenActivity.this, R.string.warning_account_open_phone_entry,
                        Toast.LENGTH_LONG).show();*/
                Snackbar.
                        make(coordinatorLayout,R.string.warning_account_open_phone_entry,Snackbar.LENGTH_LONG)
                        .show();
            } else if (name.equals("")) {
               /* Toast.makeText(AccountOpenActivity.this, R.string.warning_account_open_name_entry,
                        Toast.LENGTH_LONG).show();*/
                Snackbar.
                        make(coordinatorLayout, R.string.warning_account_open_name_entry,Snackbar.LENGTH_LONG)
                        .show();
            } else if (amountText.getText().toString().equals("")) {
                /*Toast.makeText(AccountOpenActivity.this, R.string.warning_account_open_amount_entry,
                        Toast.LENGTH_LONG).show();*/
                Snackbar.
                        make(coordinatorLayout, R.string.warning_account_open_amount_entry,Snackbar.LENGTH_LONG)
                        .show();
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
                transactionTable.setEntryTime(transactionTimeTextView.getText().toString());
                transactionTable.setTransactionId(transactionTable.generateTransactionID());
                new AccountOpenBackgroundTask(accountTable, transactionTable).execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (!personalAccountant.isRegistered()) {
            Intent intent = new Intent(AccountOpenActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        transactionTimeTextView.setText(Integer.toString(year)+"-"+
                Integer.toString((month+1)%12)+"-"+
                Integer.toString(day));
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
            List<AccountTable> accountTables = personalAccountant.getAllAccountsExcept(
                    new AccountTable(ApplicationConstants.LOGGED_PHONE_NUMBER,""));
            boolean doesExist = false;
            if (accountTable.getPhone().equals(ApplicationConstants.LOGGED_PHONE_NUMBER))
                doesExist = true;
            for (AccountTable accTable: accountTables){
                Log.d("AC", accTable.toString());
                if (accTable.getPhone().equals(accountTable.getPhone())){
                    doesExist = true;
                    break;
                }
            }
            if (doesExist) return ApplicationConstants.ACCOUNT_EXIST_ERROR;
            else if (personalAccountant.insertAccountIntoDB(accountTable)) {
                Log.d("PA", ApplicationConstants.LOGGED_PHONE_NUMBER);
                if (personalAccountant.insertTransactionIntoDB(transactionTable)) {
                    return Boolean.TRUE.toString();
                }
            }
            return Boolean.FALSE.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressView.setVisibility(View.GONE);
            if(s.equals(ApplicationConstants.ACCOUNT_EXIST_ERROR)){
                errorMessageView.setVisibility(View.VISIBLE);
                errorMessageTextView.setText(R.string.account_exist);
            }
            else if (s.equals(Boolean.TRUE.toString())) {
                Intent intent = new Intent(AccountOpenActivity.this, TransactionHomeActivity.class);
                startActivity(intent);
            }
            else {
                errorMessageView.setVisibility(View.VISIBLE);
                errorMessageTextView.setText(R.string.unknown_error);
            }
        }
    }
}
