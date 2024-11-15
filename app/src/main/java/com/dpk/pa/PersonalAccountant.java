package com.dpk.pa;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.dpk.pa.data.constants.ApplicationConstants;
import com.dpk.pa.data.constants.RegistrationConstants;
import com.dpk.pa.data.db.DataBaseHelper;
import com.dpk.pa.data_models.Account;
import com.dpk.pa.data_models.db.AccountTable;
import com.dpk.pa.data_models.db.ITable;
import com.dpk.pa.data_models.db.TransactionTable;
import com.dpk.pa.data_models.db.Tuple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * created by chandradasdipok @ 2019-Aug-10
 * last modified 2019-Aug-29
 */
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
        return true;
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
    public List<TransactionTable> getTransactionsBetween(AccountTable accountTable1, AccountTable accountTable2, boolean isAscending){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        TransactionTable transactionTable = new TransactionTable();
        String whereClause = TransactionTable.Variable.STRING_GIVER_PHONE +" IN ('"+accountTable1.getPhone()+"','"+accountTable2.getPhone()+"')";
        whereClause += " and "+TransactionTable.Variable.STRING_TAKER_PHONE +" IN ('"+accountTable1.getPhone()+"','"+accountTable2.getPhone()+"')";
        if (isAscending){
            whereClause += "order by "+ TransactionTable.Variable.STRING_ENTRY_TIME+" asc";
        }
        else {
            whereClause += "order by "+ TransactionTable.Variable.STRING_ENTRY_TIME+" desc";
        }
        transactionTable.setWhereClause(whereClause);
        List<ITable> iTables = dataBaseHelper.selectRows(transactionTable);
        return new TransactionTable().toTransactionTables(iTables);
    }
    public boolean isTransactionGivenTo( TransactionTable transactionTable,AccountTable loggedAccount){
        boolean bool = false;
        if (transactionTable.getTakerPhone().equals(loggedAccount.getPhone())){
            bool = true;
        }
        return bool;
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

    public Account getTargetAccountDetails(AccountTable targetAccount, AccountTable loggedAccount){
        Account returnAccount = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        targetAccount.setWhereClause(AccountTable.Variable.STRING_PHONE+"  = '"+targetAccount.getPhone()+"'");
        List<ITable> iTables = dataBaseHelper.selectRows(targetAccount);
        if (iTables.size()==1){
            returnAccount = new Account((AccountTable) iTables.get(0));
            returnAccount.setGivenTo(getTotalAmountGivenTo(targetAccount, loggedAccount));
            returnAccount.setTakenFrom(getTotalAmountTakenFrom(targetAccount, loggedAccount));
            return  returnAccount;
        }
        else {return null;}
    }
    public double getTotalAmountGivenTo(AccountTable targetAccount, AccountTable ownerAccount){
        List<Tuple> tuples = new ArrayList<Tuple>();
        Tuple tuple = new Tuple("select sum("+ TransactionTable.Variable.STRING_AMOUNT+") as given_to from "+new TransactionTable().tableName());
        tuple.setWhereClause(TransactionTable.Variable.STRING_GIVER_PHONE+"='"+ownerAccount.getPhone()+
                "' and "+TransactionTable.Variable.STRING_TAKER_PHONE+"='"+targetAccount.getPhone()+"'");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<ITable> iTables = dataBaseHelper.selectRows(tuple);
        Tuple qTuple = new Tuple();
        if (iTables.size()==1){
            Log.d("VAL", iTables.get(0).toString());
            qTuple = (Tuple) iTables.get(0).toClone();
            double amount = 0.0;
            if (!qTuple.values.get("given_to").equals("")){
                amount = Double.parseDouble(qTuple.values.get("given_to"));
            }
            return amount;
        }
        return -1;
    }
    public double getTotalAmountGivenTo(AccountTable ownerAccount){
        List<Tuple> tuples = new ArrayList<Tuple>();
        Tuple tuple = new Tuple("select sum("+ TransactionTable.Variable.STRING_AMOUNT+") as given_to from "+new TransactionTable().tableName());
        tuple.setWhereClause(TransactionTable.Variable.STRING_GIVER_PHONE+"='"+ownerAccount.getPhone()+"'");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<ITable> iTables = dataBaseHelper.selectRows(tuple);
        Tuple qTuple = new Tuple();
        if (iTables.size()==1){
            Log.d("VAL", iTables.get(0).toString());
            qTuple = (Tuple) iTables.get(0).toClone();
            double amount = 0.0;
            if (!qTuple.values.get("given_to").equals("")){
                amount = Double.parseDouble(qTuple.values.get("given_to"));
            }
            return amount;
        }
        return -1;
    }

    public double getTotalAmountTakenFrom(AccountTable targetAccount, AccountTable ownerAccount){
        List<Tuple> tuples = new ArrayList<Tuple>();
        Tuple tuple = new Tuple("select  sum("+ TransactionTable.Variable.STRING_AMOUNT+") as taken_from from "+new TransactionTable().tableName());
        tuple.setWhereClause(TransactionTable.Variable.STRING_GIVER_PHONE+"='"+targetAccount.getPhone()+
                "' and "+TransactionTable.Variable.STRING_TAKER_PHONE+"='"+ownerAccount.getPhone()+"'");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<ITable> iTables = dataBaseHelper.selectRows(tuple);
        Tuple qTuple = new Tuple();
        if (iTables.size()==1){
            qTuple = (Tuple) iTables.get(0).toClone();
            double amount = 0.0;
            if (!qTuple.values.get("taken_from").equals("")){
                amount = Double.parseDouble(qTuple.values.get("taken_from"));
            }
            return amount;
        }
        return 0.0;
    }
    public double getTotalAmountTakenFrom(AccountTable ownerAccount){
        List<Tuple> tuples = new ArrayList<Tuple>();
        Tuple tuple = new Tuple("select  sum("+ TransactionTable.Variable.STRING_AMOUNT+") as taken_from from "+new TransactionTable().tableName());
        tuple.setWhereClause(TransactionTable.Variable.STRING_TAKER_PHONE+"='"+ownerAccount.getPhone()+"'");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<ITable> iTables = dataBaseHelper.selectRows(tuple);
        Tuple qTuple = new Tuple();
        if (iTables.size()==1){
            qTuple = (Tuple) iTables.get(0).toClone();
            double amount = 0.0;
            if (!qTuple.values.get("taken_from").equals("")){
                amount = Double.parseDouble(qTuple.values.get("taken_from"));
            }
            return amount;
        }
        return 0.0;
    }

    public List<Account> fromTableToObject(List<AccountTable> accountTables){
        List<Account> accounts = new ArrayList<Account>();
        for (AccountTable accountTable: accountTables){
            accounts.add(new Account(accountTable));
        }
        return accounts;
    }

    public boolean isRegistered(){
        ApplicationConstants.LOGGED_PHONE_NUMBER = loadPersonalAccountPhone();
        Log.d(RegistrationConstants.USER_PHONE, ApplicationConstants.LOGGED_PHONE_NUMBER);
        if (!ApplicationConstants.LOGGED_PHONE_NUMBER.equals("")){
            return true;
        }
        return false;
    }

    public String loadLanguage(){
        sharedPreferences = context.getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE,
                Context.MODE_PRIVATE);
        ApplicationConstants.LANGUAGE_CODE = sharedPreferences.getString(ApplicationConstants.LANGUAGE_CODE_LABEL,
                ApplicationConstants.LANGUAGE_CODE_ENGLISH);
        return ApplicationConstants.LANGUAGE_CODE;
    }
    public boolean saveLanguage(String language_code){
        sharedPreferences = context.getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("LANG_PA_S", language_code);
        editor.putString(ApplicationConstants.LANGUAGE_CODE_LABEL, language_code);
        ApplicationConstants.LANGUAGE_CODE = language_code;
        editor.commit();
        return true;
    }

    public void setLanguageInApp() {
        String language_code = loadLanguage();
        Log.d("LANG_PA", language_code);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language_code.toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    public List<Account> searchedAccounts(String searchString, List<Account>toSearchAccounts){
        List<Account> searchedAccounts = new ArrayList<Account>();
        if (searchString.equals("")) return toSearchAccounts;
        String [] searchTokens = searchString.split("\\s+");
        Log.d("TOK",searchString+","+ Arrays.toString(searchTokens));
        for (Account account: toSearchAccounts){
            boolean isSearched = false;
            for (String token :
                    searchTokens) {
                Log.d("TOKENS", token);
                if (account.getName().toLowerCase().matches(".*"+token.toLowerCase()+".*") ||
                        account.getPhone().matches(".*"+token.toLowerCase()+".*")){
                    isSearched =true;
                }
            }
            if (isSearched)
                searchedAccounts.add(account);
        }
        return searchedAccounts;
    }
}
