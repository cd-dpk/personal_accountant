package com.dpk.pa.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data_models.db.AccountTable;

public class AccountOpenActivity extends AppCompatActivity {
    EditText phoneText, nameText;
    Button okayButton;
    String phone="", name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_open);

        phoneText = (EditText) findViewById(R.id.edit_text_phone_number_open);
        nameText = (EditText) findViewById(R.id.edit_text_name_open);
        okayButton = findViewById(R.id.button_open);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = phoneText.getText().toString();
                name = nameText.getText().toString();
                if (phone.equals("") && name.equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone and Name Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (phone.equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (name.equals("")){
                    Toast.makeText(AccountOpenActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(AccountOpenActivity.this, "Correct!",Toast.LENGTH_LONG).show();
                    AccountTable accountTable = new AccountTable(phone, name);
                    PersonalAccountant personalAccountant = new PersonalAccountant(AccountOpenActivity.this);
                    if (personalAccountant.insertAccountIntoDB(accountTable)){
                        Log.d("PA", ApplicationConstants.LOGGED_PHONE_NUMBER);
                        Intent intent = new Intent(AccountOpenActivity.this, AccountListActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
