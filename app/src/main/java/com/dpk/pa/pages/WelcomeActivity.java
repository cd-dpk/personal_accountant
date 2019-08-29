package com.dpk.pa.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data_models.IRegistration;
import com.dpk.pa.data_models.db.AccountTable;

public class WelcomeActivity extends AppCompatActivity implements IRegistration {

    EditText phoneText, nameText;
    Button okayButton;
    String phone="", name="";
    View progressView ;
    PersonalAccountant personalAccountant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        personalAccountant = new PersonalAccountant(this);
        checkRegistration(personalAccountant);

        phoneText = (EditText) findViewById(R.id.edit_text_phone_number);
        nameText = (EditText) findViewById(R.id.edit_text_name);
        okayButton = findViewById(R.id.button_okay);
        progressView = (View) findViewById(R.id.welcome_progress_view);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = phoneText.getText().toString();
                name = nameText.getText().toString();
                if (phone.equals("") && name.equals("")){
                    Toast.makeText(WelcomeActivity.this, "Please enter Phone and Name Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (phone.equals("")){
                    Toast.makeText(WelcomeActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else if (name.equals("")){
                    Toast.makeText(WelcomeActivity.this, "Please enter Phone Correctly!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(WelcomeActivity.this, "Correct!",Toast.LENGTH_LONG).show();
                    AccountTable accountTable = new AccountTable(phone, name);
                    new SwitchingActivityAsyncTask(accountTable).execute();
                }
            }
        });
    }

    @Override
    public void checkRegistration(PersonalAccountant personalAccountant) {
        if (personalAccountant.isRegistered()) {
            Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
            Intent intent = new Intent(WelcomeActivity.this, TransactionHomeActivity.class);
            startActivity(intent);
        }
    }


    private class SwitchingActivityAsyncTask extends AsyncTask<String, String ,String>{
        AccountTable accountTable;
        SwitchingActivityAsyncTask(AccountTable accountTable){
            super();
            this.accountTable = accountTable;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            PersonalAccountant personalAccountant = new PersonalAccountant(WelcomeActivity.this);
            if (personalAccountant.insertAccountIntoDB(accountTable)){
                personalAccountant.savePersonalAccountPhone(accountTable.getPhone());
                ApplicationConstants.LOGGED_PHONE_NUMBER = personalAccountant.loadPersonalAccountPhone();
                Log.d("PA", ApplicationConstants.LOGGED_PHONE_NUMBER);
                return Boolean.TRUE.toString();
            }
            return Boolean.FALSE.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressView.setVisibility(View.GONE);
            if (s.equals(Boolean.TRUE.toString())){
                Intent intent = new Intent(WelcomeActivity.this, TransactionHomeActivity.class);
                startActivity(intent);
            }
        }
    }

}
