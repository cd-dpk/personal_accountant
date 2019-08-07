package com.dpk.pa.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dpk.pa.PersonalAccountant;
import com.dpk.pa.R;
import com.dpk.pa.data.TransactionsType;
import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.dpk.pa.pages.TransactionAddActivity;
import com.dpk.pa.pages.TransactionListActivity;
import com.dpk.pa.utils.TimeHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionTakenFragment extends Fragment {

    String targetPhone;
    String transactionDescription;
    String amount;
    PersonalAccountant personalAccountant;
    View progressView;
    TransactionsType transactionsType;

    Spinner giverSpinner;
    EditText descriptionText, amountText;
    Button transactionAddButton;

    String loggedPerson="", targetPerson="";

    Context context;
    public TransactionTakenFragment(TransactionsType transactionsType, Context context) {
        this.transactionsType = transactionsType;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personalAccountant = new PersonalAccountant(context);
        List<AccountTable> accounts = personalAccountant.getAllAccounts();
        Log.d("ACCOUNTS", accounts.size()+"");
        final List<String> phones  = new ArrayList<String >();
        for (AccountTable accountTable: accounts){
            phones.add(accountTable.getPhone());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_taken, container, false);
        progressView = view.findViewById(R.id.progress_view_fragment_transaction_taken);
        personalAccountant = new PersonalAccountant(getContext());
        giverSpinner = (Spinner) view.findViewById(R.id.spinner_fragment_transaction_taken);
        descriptionText = (EditText) view.findViewById(R.id.edit_text_fragment_transaction_taken_description);
        amountText = (EditText) view.findViewById(R.id.edit_text_fragment_transaction_taken_amount);
        transactionAddButton = (Button) view.findViewById(R.id.button_fragment_transaction_taken_add);
        return view;
    }


}
