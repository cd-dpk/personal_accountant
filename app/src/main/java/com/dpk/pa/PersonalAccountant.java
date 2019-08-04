package com.dpk.pa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data.db.DataBaseHelper;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.ITable;
import com.dpk.pa.data_models.db.TransactionTable;

import java.util.ArrayList;
import java.util.List;

public class PersonalAccountant {

    private Context context;
    private SharedPreferences sharedPreferences;

    public PersonalAccountant(Context context) {
        this.context = context;
    }

    public String loadPersonalAccountPhone(){
        sharedPreferences = context.getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE,
                Context.MODE_PRIVATE);
        ApplicationConstants.LOGGED_PHONE_NUMBER = sharedPreferences.getString(RegistrationConstants.USER_PHONE, "");
        return ApplicationConstants.LOGGED_PHONE_NUMBER;
    }
    public boolean savePersonalAccountPhone( String phone){
        sharedPreferences = context.getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RegistrationConstants.REGISTRATION_STATUS, RegistrationConstants.REGISTRATION_COMPLETED);
        ApplicationConstants.LOGGED_PHONE_NUMBER = phone;
        editor.putString(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
        editor.commit();
        return false;
    }
    public boolean insertAccountIntoDB(AccountTable accountTable){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        return dataBaseHelper.insertRow(accountTable);
    }
    public List<AccountTable> getAllAccounts(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        AccountTable accountTable = new AccountTable();
        Log.d("CHECK", accountTable.toSelectString());
        List<ITable> iTables = dataBaseHelper.selectRows(accountTable);
        return new AccountTable().toAccountTables(iTables);
    }
    public List<AccountTable> getAllAccountsExcept(AccountTable exclusiveAccountTable){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        AccountTable accountTable = new AccountTable();
        accountTable.setWhereClause(AccountTable.Variable.STRING_PHONE+"  != '"+exclusiveAccountTable.getPhone() +"'");
        List<ITable> iTables = dataBaseHelper.selectRows(accountTable);
        Log.d("SIZE", iTables.size()+"");
        return new AccountTable().toAccountTables(iTables);
    }
    public boolean insertTransactionIntoDB(TransactionTable transactionTable){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        return dataBaseHelper.insertRow(transactionTable);
    }
    public List<TransactionTable> getTransactionsBetween(AccountTable accountTable1, AccountTable accountTable2){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        TransactionTable transactionTable = new TransactionTable();
        String whereClause = TransactionTable.Variable.STRING_GIVER_PHONE +" IN ('"+accountTable1.getPhone()+"','"+accountTable2.getPhone()+"')";
        whereClause += " or "+TransactionTable.Variable.STRING_TAKER_PHONE +" IN ('"+accountTable1.getPhone()+"','"+accountTable2.getPhone()+"')";
        transactionTable.setWhereClause(whereClause);
        List<ITable> iTables = dataBaseHelper.selectRows(transactionTable);
        return new TransactionTable().toTransactionTables(iTables);
    }
    public List<TransactionTable> getTransactions(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        TransactionTable transactionTable = new TransactionTable();
        List<ITable> iTables = dataBaseHelper.selectRows(transactionTable);
        return new TransactionTable().toTransactionTables(iTables);
    }
    public AccountTable getLoggedAccount(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        AccountTable accountTable = new AccountTable();
        accountTable.setWhereClause(AccountTable.Variable.STRING_PHONE+"  = '"+ApplicationConstants.LOGGED_PHONE_NUMBER +"'");
        List<ITable> iTables = dataBaseHelper.selectRows(accountTable);
        if (iTables.size()==1){
            return (AccountTable) iTables.get(0);
        }
        else {return null;}
    }
    public List<String> phoneNumbers(List<AccountTable> accounts){
        List<String> phoneNumbers = new ArrayList<String>();
        for (AccountTable accountTable: accounts){
            phoneNumbers.add(accountTable.getPhone());
        }
        return  phoneNumbers;
    }
}
