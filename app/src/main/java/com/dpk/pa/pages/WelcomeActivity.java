package com.dpk.pa.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dpk.pa.R;

public class WelcomeActivity extends AppCompatActivity {

    EditText phoneText, nameText;
    Button okayButton;
    String phone="", name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        phoneText = (EditText) findViewById(R.id.edit_text_phone_number);
        nameText = (EditText) findViewById(R.id.edit_text_name);
        okayButton = findViewById(R.id.button_okay);

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
                }
            }
        });
    }
}
