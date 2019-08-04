package com.dpk.pa.data_models.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.dpk.pa.security.AppSecurityManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 3/23/2016.
 */

public class TransactionTable implements ITable{

    private String transactionId="";
    private String giverPhone="";
    private String takerPhone="";
    private double amount=0.0;
    private String description="";
    private String entryTime="";

    private String whereClause="";

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public TransactionTable() {}


    public TransactionTable(String transactionId, String giverPhone, String takerPhone, double amount, String description, String entryTime) {
        setTransactionId(transactionId);
        setGiverPhone(giverPhone);
        setTakerPhone(takerPhone);
        setAmount(amount);
        setDescription(description);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGiverPhone() {
        return giverPhone;
    }

    public void setGiverPhone(String giverPhone) {
        this.giverPhone = giverPhone;
    }

    public String getTakerPhone() {
        return takerPhone;
    }

    public void setTakerPhone(String takerPhone) {
        this.takerPhone = takerPhone;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    @Override
    public String toSelectString() {
        if (getWhereClause().equals("")){
            return "select * from "+ tableName();
        }else {
            return "select * from "+ tableName()+" where "+ getWhereClause();
        }
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        TransactionTable transaction = new TransactionTable();
        if (cursor.getColumnIndex(Variable.STRING_TRANSACTION_ID)!=-1){
            transaction.transactionId = cursor.getString(
                    cursor.getColumnIndex(Variable.STRING_TRANSACTION_ID));
        }
        if (cursor.getColumnIndex(Variable.STRING_GIVER_PHONE)!=-1){
            transaction.giverPhone = cursor.getString(cursor.getColumnIndex(Variable.STRING_GIVER_PHONE));
        }
        if (cursor.getColumnIndex(Variable.STRING_TAKER_PHONE)!=-1){
            transaction.takerPhone = cursor.getString(
                    cursor.getColumnIndex(Variable.STRING_TAKER_PHONE));
        }
        if (cursor.getColumnIndex(Variable.STRING_AMOUNT)!=-1){
            transaction.amount = cursor.getInt(
                    cursor.getColumnIndex(Variable.STRING_AMOUNT));
        }
        if (cursor.getColumnIndex(Variable.STRING_DESCRIPTION)!=-1){
            transaction.description = cursor.getString(
                    cursor.getColumnIndex(Variable.STRING_DESCRIPTION));
        }
        if (cursor.getColumnIndex(Variable.STRING_ENTRY_TIME)!=-1){
            transaction.entryTime = cursor.getString(
                    cursor.getColumnIndex(Variable.STRING_ENTRY_TIME));
        }
        return transaction;
    }

    @Override
    public boolean isCloned(ITable iTable) {
        if (iTable.toString().equals(this.toString())) return true;
        else return false;
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public String tableName() {
        return "transactions";
    }

    @Override
    public String toCreateTableString() {
        return "create table if not exists "+tableName()+" ("+
                Variable.STRING_TRANSACTION_ID+" text," +
                Variable.STRING_GIVER_PHONE+" text," +
                Variable.STRING_TAKER_PHONE+" text," +
                Variable.STRING_AMOUNT+" double,"+
                Variable.STRING_DESCRIPTION+" text,"+
                Variable.STRING_ENTRY_TIME+" text,"+
                "primary key ("+ Variable.STRING_TRANSACTION_ID+"),"+
                "foreign key (" + Variable.STRING_GIVER_PHONE +" ) references  accounts(phone),"+
                "foreign key (" + Variable.STRING_TAKER_PHONE +" ) references  accounts(phone)"+
                ")";
    }

    @Override
    public String toDeleteSingleRowString() {
        return null;
    }

    @Override
    public String toDeleteRows() {
        return null;
    }

    @Override
    public String toSelectSingleRowString() {
        return "select * from "+ tableName() ;
    }
    @Override
    public ITable toClone(){
        return  new TransactionTable();
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(Variable.STRING_GIVER_PHONE,giverPhone);
        contentValues.put(Variable.STRING_TAKER_PHONE,takerPhone);
        contentValues.put(Variable.STRING_AMOUNT,amount);
        contentValues.put(Variable.STRING_DESCRIPTION,description);
        contentValues.put(Variable.STRING_ENTRY_TIME,entryTime);
        return contentValues;
    }
    @Override
    public void setUpdateContentValues(ContentValues updateContentValues) {

    }

    @Override
    public ContentValues getUpdateContentValues() {
        return null;
    }

    @Override
    public String getWhereClauseString() {
        return null;
    }

    @Override
    public String toString() {
        return "("+
                transactionId+","+
                giverPhone+","+
                takerPhone+","+
                amount+","+
                description+","+
                entryTime+")";
    }

    public List<TransactionTable> toTransactionTables(List<ITable> iTables) {
        List<TransactionTable> transactionTables = new ArrayList<TransactionTable>();
        for (ITable iTable: iTables) {
            TransactionTable transactionTable = (TransactionTable) iTable.toClone();
            transactionTables.add(transactionTable);
        }
        return transactionTables;
    }

    public static class Variable {

        public final static String STRING_TRANSACTION_ID = "t_id",
                STRING_GIVER_PHONE = "giver_phone",
                STRING_TAKER_PHONE="taker_phone",
                STRING_AMOUNT="amount",
                STRING_DESCRIPTION="description",
                STRING_ENTRY_TIME= "entry_time";
    }
    @Override
    public String toDropTableString() {
        return "DROP TABLE "+" "+tableName();
    }



    // generate an encrypted transaction id
    public String generateTransactionID(){
        String inputText = giverPhone+""+takerPhone+amount+description+entryTime;
        return AppSecurityManager.getEncryptedText(inputText);
    }
}