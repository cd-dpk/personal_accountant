package com.dpk.pa.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.google.android.material.textfield.TextInputEditText;

public class AccountOpenActivity extends AppCompatActivity {
    TextInputEditText phoneText, nameText, amountText;
    Button okayButton;
    String phone="", name="";
    double amount;
    RadioGroup transactionTypeRadioGroup;
    RadioButton transactionTypeGivenRadioButton, transactionTypeTakenRadioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_open);

        phoneText = (TextInputEditText) findViewById(R.id.edit_text_phone_number_open);
        nameText = (TextInputEditText) findViewById(R.id.edit_text_name_open);
        amountText = (TextInputEditText) findViewById(R.id.edit_text_account_open_transaction_amount);

        okayButton = findViewById(R.id.button_open);
        transactionTypeRadioGroup =  (RadioGroup) findViewById(R.id.account_open_transaction_type_rb);
        transactionTypeGivenRadioButton =  (RadioButton) findViewById(R.id.account_open_transaction_type_given);
        transactionTypeTakenRadioButton =  (RadioButton) findViewById(R.id.account_open_transaction_type_taken);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = phoneText.getText().toString();
                name = nameText.getText().toString();
                if (phone.equals("") && name.equals("") && amountText.getText().toString().equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone and Name Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (phone.equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (name.equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Name Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (amountText.getText().toString().equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Amount Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    // TODO create account
                    Toast.makeText(AccountOpenActivity.this, "Correct!",Toast.LENGTH_LONG).show();
                    AccountTable accountTable = new AccountTable(phone, name);
                    PersonalAccountant personalAccountant = new PersonalAccountant(AccountOpenActivity.this);
                    if (personalAccountant.insertAccountIntoDB(accountTable)){
                        Log.d("PA", ApplicationConstants.LOGGED_PHONE_NUMBER);
                        // TODO create transaction
                        TransactionTable transactionTable = new TransactionTable();
                        amount = Double.parseDouble(amountText.getText().toString());
                        transactionTable.setAmount(amount);
                        if (transactionTypeRadioGroup.getCheckedRadioButtonId() == transactionTypeGivenRadioButton.getId()){
                            transactionTable.setGiverPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                            transactionTable.setTakerPhone(accountTable.getPhone());
                        }
                        else{
                            transactionTable.setTakerPhone(ApplicationConstants.LOGGED_PHONE_NUMBER);
                            transactionTable.setGiverPhone(accountTable.getPhone());
                        }
                        if ( personalAccountant.insertTransactionIntoDB(transactionTable)){
                            Toast.makeText(AccountOpenActivity.this, "Account Created & Transaction Completed!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AccountOpenActivity.this, AccountListActivity.class);
                            startActivity(intent);
                        }
                    }


                }
            }
        });
    }

}
