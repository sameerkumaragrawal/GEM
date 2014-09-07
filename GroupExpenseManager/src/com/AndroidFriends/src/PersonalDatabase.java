package com.AndroidFriends.src;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersonalDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "PersonalExpenses";
	private static final int DATABASE_VERSION = 1;
	public static final String expensesTable = "Expenses";
	public static final String billsTable = "Bills";
	public static final String infoTable = "Information";
	public static final String categoryTable = "Categories";
	
	public final static int expenseFlag = 1;
	public final static int editedExpenseFlag = 2;
	public final static int deletedExpenseFlag = 3;
	public final static int clearedExpenseFlag = 4;
	
	public final static int noIncomeFlag = 0;
	public final static int incomeFlag = 1;
	
	public final static String createInfoTable = "CREATE TABLE IF NOT EXISTS " + infoTable + " (Name varchar(255) NOT NULL, Currency int(2) NOT NULL DEFAULT 1, Income float NOT NULL DEFAULT 0, Flag int(1) NOT NULL)";
	public final static String createExpensesTable = "CREATE TABLE IF NOT EXISTS " + expensesTable + " (ID int(11) NOT NULL, Name varchar(255) NOT NULL, Category int(2) NOT NULL, Amount float NOT NULL, Date BIGINT NOT NULL DEFAULT 0, Flag int(1) NOT NULL)";
	public final static String createBillsTable = "CREATE TABLE IF NOT EXISTS " + billsTable + " (ID int(11) NOT NULL, Name varchar(255) NOT NULL, Amount float NOT NULL, Date BIGINT NOT NULL DEFAULT 0)";
	public final static String createCategoryTable = "CREATE TABLE IF NOT EXISTS " + categoryTable + " (ID int(11) NOT NULL, Name varchar(255) NOT NULL)";

	private SQLiteDatabase db=null;

	private PersonalDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createInfoTable);
		db.execSQL(createExpensesTable);
		db.execSQL(createBillsTable);
		createCategoryTable(db);
	}
	
	public void createCategoryTable(SQLiteDatabase db){
		try{
			db.execSQL(createCategoryTable);
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (1, 'Food')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (2, 'Health')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (3, 'Entertainment')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (4, 'Travel')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (5, 'Accommodation')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (6, 'Shopping')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (7, 'Bills')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (8, 'Group Expenses')");
			db.execSQL("INSERT INTO " + categoryTable + " VALUES (9, 'Miscellaneous')");
		}catch(Exception e){
			Log.e("adi","error",e);
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public boolean updateCurrency(int currency,int id){
		getDB().execSQL("UPDATE "+ infoTable +" SET Currency = ?",new Object[]{currency});
		return true;
	}
	
	public void insertInfoWithoutIncome(String name, int currency) {
		ContentValues values = new ContentValues();
		values.put("Name", name);
		values.put("Currency", currency);
		values.put("Income", 0);
		values.put("Flag", noIncomeFlag);
		getDB().insert(infoTable,null,values);
	}
	
	public void insertInfoWithIncome(String name, int currency, float income) {
		ContentValues values = new ContentValues();
		values.put("Name", name);
		values.put("Currency", currency);
		values.put("Income", income);
		values.put("Flag", incomeFlag);
		getDB().insert(infoTable,null,values);
	}
	
	public void insertExpense(String name, int category, float amount) {
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + expensesTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}
		
		ContentValues values = new ContentValues();
		values.put("ID", ID1);
		values.put("Name", name);
		values.put("Category", category);
		values.put("Amount", amount);
		values.put("Date",System.currentTimeMillis());
		values.put("Flag", expenseFlag);
		getDB().insert(expensesTable,null,values);
	}
	
	public void insertBill(String name, float amount, long date) {
		int ID1 = 1;
		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + billsTable, null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}
		
		ContentValues values = new ContentValues();
		values.put("ID", ID1);
		values.put("Name", name);
		values.put("Amount", amount);
		values.put("Date",date);
		getDB().insert(billsTable,null,values);
	}
	
	public Cursor getExpenseList(int category) {
		Cursor mquery;
		if (category == 0) {
			mquery = getDB().rawQuery("SELECT ID, Name, Amount FROM " + expensesTable + " ORDER BY ID DESC", null);
		}
		else {
			mquery = getDB().rawQuery("SELECT ID, Name, Amount FROM " + expensesTable + " WHERE Category = " + category + " ORDER BY ID DESC", null);
		}
		return mquery;
	}
	
	public Cursor getBillList() {
		Cursor mquery = getDB().rawQuery("SELECT ID, Name FROM " + billsTable + " ORDER BY Date", null);
		return mquery;
	}
	
	public Cursor getExpenseDetails(int id) {
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + expensesTable + " WHERE ID = " + id, null);
		return mquery;
	}
	
	public Cursor getBillDetails(int id) {
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + billsTable + " WHERE ID = " + id, null);
		return mquery;
	}
	
	public Cursor getInformation() {
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + infoTable, null);
		return mquery;
	}
	
	public void updateInfoTable(String name, int currency, float income) {
		int newFlag;
		if (income == 0) {
			newFlag = noIncomeFlag;
		}
		else {
			newFlag = incomeFlag;
		}
		getDB().execSQL("UPDATE " + infoTable + " SET Name = ?, Currency = ?, Income = ?, Flag = ?", new Object[]{name, currency, income, newFlag});
	}
	
	public void updateExpensesTable(int id, String name, int category, float amount) {
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = ?, Category = ?, Amount = ?, Flag = ? WHERE ID = ?", new Object[]{name, category, amount, editedExpenseFlag, id});
	}
	
	public void updateBillsTable(int id, String name, float amount, long date) {
		getDB().execSQL("UPDATE " + billsTable + " SET Name = ?, Amount = ?, Date = ? WHERE ID = ?", new Object[]{name, amount, date, id});
	}
	
	public void deleteExpense(int id) {
		String prefix = "Deleted - ";
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = ? || Name, Flag = ? WHERE ID = ?", new Object[]{prefix, deletedExpenseFlag, id});
	}
	
	public void deleteBill(int id) {
		getDB().execSQL("DELETE FROM " + billsTable + " WHERE ID = ?", new Object[]{id});
	}
	
	public void payBill(int id) {
		Cursor mquery = getDB().rawQuery("SELECT Name, Amount FROM " + billsTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		String name = "Bill Payment - " + mquery.getString(0);
		float amount = mquery.getFloat(1);
		mquery.close();
		
		insertExpense(name, 7, amount);
		deleteBill(id);
	}
	
	public void restoreExpense(int id) {
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = SUBSTR(Name, 11, LENGTH(Name)), Flag = ? WHERE ID = ?",new Object[]{expenseFlag, id});
	}
	
	public void clearExpenses() {
		float total = getTotalExpenses();

		getDB().execSQL("DELETE FROM " + expensesTable + " WHERE Flag != " + clearedExpenseFlag);
		
		if (total != 0) {
			insertClearedExpense(total);
		}
	}
	
	public void insertClearedExpense(float total) {
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + expensesTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues values = new ContentValues();
		values.put("ID", ID1);
		values.put("Name", "Cleared Expenses");
		values.put("Category", 9);
		values.put("Amount", total);
		values.put("Date",System.currentTimeMillis());
		values.put("Flag", clearedExpenseFlag);
		getDB().insert(expensesTable,null,values);
	}
	
	public float getTotalExpenses() {
		float total = 0;
		Cursor mquery = getDB().rawQuery("SELECT Amount FROM " + expensesTable + " WHERE Flag <= " + editedExpenseFlag, null);
		if (mquery.getCount() > 0) {
			mquery.moveToFirst();
			do {
				total += mquery.getFloat(0);
			} while (mquery.moveToNext());
		}
		return total;
	}
	
	public float getTotalCategoryExpenses(int category) {
		float total = 0;
		Cursor mquery = getDB().rawQuery("SELECT Amount FROM " + expensesTable + " WHERE Category = " + category + " AND Flag <= " + editedExpenseFlag, null);
		if (mquery.getCount() > 0) {
			mquery.moveToFirst();
			do {
				total += mquery.getFloat(0);
			} while (mquery.moveToNext());
		}
		return total;
	}
	
	public float getTotalBills() {
		float total = 0;
		Cursor mquery = getDB().rawQuery("SELECT Amount FROM " + billsTable, null);
		if (mquery.getCount() > 0) {
			mquery.moveToFirst();
			do {
				total += mquery.getFloat(0);
			} while (mquery.moveToNext());
		}
		return total;
	}
		
	public String getCategoryName(int id) {
		Cursor mquery = getDB().rawQuery("SELECT Name FROM " + categoryTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		return mquery.getString(0);
	}
	
	public ArrayList<String> getCategoryNames() {
		Cursor mquery = getDB().rawQuery("SELECT Name FROM " + categoryTable, null);
		mquery.moveToFirst();
		ArrayList<String> list = new ArrayList<String>();
		do {
			list.add(mquery.getString(0));
		} while (mquery.moveToNext());
		return list;
	}
	
	public void addOrEditGroupExpense(String name, float total) {
		Cursor expenseQuery = getDB().rawQuery("SELECT * FROM " + expensesTable + " WHERE Name = ? AND Category = ?", new String[]{name, String.valueOf(8)});
		if (expenseQuery.getCount() != 0) {
			getDB().execSQL("UPDATE " + expensesTable + " SET Amount = ?, Date = ? WHERE Name = ? AND Category = ?", new Object[]{total, System.currentTimeMillis(), name, 8});
		}
		else {
			int ID1=1;
			Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + expensesTable , null);
			if(count.getCount()>0){
				count.moveToLast();
				ID1=count.getInt(0)+1;
			}
	
			ContentValues values = new ContentValues();
			values.put("ID", ID1);
			values.put("Name", name);
			values.put("Category", 8);
			values.put("Amount", total);
			values.put("Date",System.currentTimeMillis());
			values.put("Flag", expenseFlag);
			getDB().insert(expensesTable,null,values);
		}
	}
	
	public void deleteGroupExpense(String name) {
		Cursor expenseQuery = getDB().rawQuery("SELECT * FROM " + expensesTable + " WHERE Name = ? AND Category = ?", new String[]{name, String.valueOf(8)});
		if (expenseQuery.getCount() != 0) {
			getDB().execSQL("DELETE FROM " + expensesTable + " WHERE Name = ? AND Category = ?", new Object[]{name, 8});
		}
	}

	private SQLiteDatabase getDB() {
		if (db == null) {
			db = getWritableDatabase();
		}
		return db;
	}

	@Override
	public synchronized void close() {
		super.close();
		if (db != null) {
			try {
				db.close();
			} catch (Exception e) {};
			db = null;
		}
	}

	private static PersonalDatabase DB_;

	public static PersonalDatabase get(Context c) {
		if (DB_ == null) {
			DB_ = new PersonalDatabase(c);
		}
		return DB_;
	}

	public static void closeAll() {
		if (DB_ != null) {
			DB_.close();
			DB_ = null;
			DATABASE_NAME = null;
		}
	}
}
