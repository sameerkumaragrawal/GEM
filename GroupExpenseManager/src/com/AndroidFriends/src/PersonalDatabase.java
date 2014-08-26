package com.AndroidFriends.src;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonalDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "PersonalExpenses";
	private static final int DATABASE_VERSION = 1;
	public static final String expensesTable = "Expenses";
	public static final String billsTable = "Bills";
	public static final String infoTable = "Information";
	
	public final static int expenseFlag = 1;
	public final static int editedExpenseFlag = 2;
	public final static int deletedExpenseFlag = 3;
	
	public final static int noSalaryFlag = 0;
	public final static int salaryFlag = 1;
	
	public final static String createInfoTable = "CREATE TABLE IF NOT EXISTS " + infoTable + " ( Name varchar(255) NOT NULL, Currency int(2) NOT NULL DEFAULT 1, Salary float NOT NULL DEFAULT 0, Flag int(1) NOT NULL)";
	public final static String createExpensesTable = "CREATE TABLE IF NOT EXISTS " + expensesTable + " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Amount float NOT NULL, Date BIGINT NOT NULL DEFAULT 0, Flag int(1) NOT NULL)";
	public final static String createBillsTable = "CREATE TABLE IF NOT EXISTS " + billsTable + " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Amount float NOT NULL, Date BIGINT NOT NULL DEFAULT 0)";

	private SQLiteDatabase db=null;

	private PersonalDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createInfoTable);
		db.execSQL(createExpensesTable);
		db.execSQL(createBillsTable);
	}		
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public boolean updateCurrency(int currency,int id){
		getDB().execSQL("UPDATE "+ infoTable +" SET Currency = ?",new Object[]{currency});
		return true;
	}
	
	public void insertInfoWithoutSalary(String name, int currency) {
		ContentValues values = new ContentValues();
		values.put("Name", name);
		values.put("Currency", currency);
		values.put("Salary", 0);
		values.put("Flag", noSalaryFlag);
		getDB().insert(infoTable,null,values);
	}
	
	public void insertInfoWithSalary(String name, int currency, float salary) {
		ContentValues values = new ContentValues();
		values.put("Name", name);
		values.put("Currency", currency);
		values.put("Salary", salary);
		values.put("Flag", salaryFlag);
		getDB().insert(infoTable,null,values);
	}
	
	public void insertExpense(String name, float amount) {
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT count(*) FROM " + expensesTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}
		
		ContentValues values = new ContentValues();
		values.put("ID", ID1);
		values.put("Name", name);
		values.put("Amount", amount);
		values.put("Date",System.currentTimeMillis());
		values.put("Flag", expenseFlag);
		getDB().insert(expensesTable,null,values);
	}
	
	public void insertBill(String name, float amount, long date) {
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT count(*) FROM " + billsTable, null);
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
	
	public Cursor getExpenseList() {
		Cursor mquery = getDB().rawQuery("SELECT ID, Name FROM " + expensesTable + " ORDER BY ID DESC", null);
		return mquery;
	}
	
	public Cursor getBillList() {
		Cursor mquery = getDB().rawQuery("SELECT ID, Name FROM " + billsTable + " ORDER BY Date DESC", null);
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
	
	public void updateInfoTable(String name, int currency, float salary) {
		int newFlag;
		if (salary == 0) {
			newFlag = noSalaryFlag;
		}
		else {
			newFlag = salaryFlag;
		}
		getDB().execSQL("UPDATE " + infoTable + " SET Name = ?, Currency = ?, Salary = ?, Flag = ?", new Object[]{name, currency, salary, newFlag});
	}
	
	public void updateExpensesTable(int id, String name, float amount) {
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = ?, Amount = ?, Flag = ? WHERE ID = ?", new Object[]{name, amount, editedExpenseFlag, id});
	}
	
	public void updateBillsTable(int id, String name, float amount, long date) {
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = ?, Amount = ?, Date = ? WHERE ID = ?", new Object[]{name, amount, date, id});
	}
	
	public void deleteExpense(int id) {
		String prefix = "Deleted - ";
		getDB().execSQL("UPDATE " + expensesTable + " SET Name = ? || Name, Flag = ? WHERE ID = ?", new Object[]{prefix, deletedExpenseFlag, id});
	}
	
	public void deleteBill(int id) {
		getDB().execSQL("DELETE FROM " + billsTable + " WHERE ID = ?", new Object[]{id});
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
