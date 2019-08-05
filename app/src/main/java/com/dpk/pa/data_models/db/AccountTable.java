package com.dpk.pa.data_models.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.dpk.pa.data_models.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 3/23/2016.
 */

public class AccountTable implements ITable{


    public AccountTable(Account account){
        setPhone(account.getPhone());
        setName(account.getName());
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    private String whereClause ="";
    private String name;
    private String phone;

    public AccountTable() {}


    public AccountTable(String phone, String name) {
        this();
        setName(name);
        setPhone(phone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        AccountTable user = new AccountTable();
        if (cursor.getColumnIndex(Variable.STRING_PHONE)!=-1){
            user.phone = cursor.getString(cursor.getColumnIndex(Variable.STRING_PHONE));
        }
        if (cursor.getColumnIndex(Variable.STRING_NAME)!=-1){
            user.name = cursor.getString(cursor.getColumnIndex(Variable.STRING_NAME));
        }
        return user;
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
        return "accounts";
    }

    @Override
    public String toCreateTableString() {
        return "create table if not exists "+tableName()+
                "( phone text," +
                " name text," +
                " primary key (phone)" +
                ")";
    }

    @Override
    public String toDeleteSingleRowString() {
        return null;
    }

    @Override
    public String toDeleteRows() {
        return "delete from "+tableName();
    }

    @Override
    public String toSelectSingleRowString() {
        return "select * from "+ tableName() +" where phone = '"+phone+"'";
    }
    @Override
    public ITable toClone(){
        return  new AccountTable(phone,name);
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(Variable.STRING_PHONE,phone);
        contentValues.put(Variable.STRING_NAME,name);
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
    public String toDropTableString() {
        return "drop table "+tableName()+" if exists";
    }



    @Override
    public String toString() {
        return "("+
                phone+","+
                name+")";
    }
    public static class Variable {
        public final static String STRING_NAME = "name";
        public final static String STRING_PHONE = "phone";
    }

    public List<AccountTable> toAccountTables(List<ITable> iTables){
        List<AccountTable> accountTables = new ArrayList<AccountTable>();
        for (ITable iTable: iTables) {
            AccountTable accountTable = (AccountTable) iTable.toClone();
            accountTables.add(accountTable);
        }
        return accountTables;
    }

}