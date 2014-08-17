package com.AndroidFriends.src;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CommonDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "GroupNames";
	private static final int DATABASE_VERSION = 3;
	public static final String tableName = "Groups";
	public static final String currencyTable = "Currencies";
	
	public final static String createMainTable = "CREATE TABLE IF NOT EXISTS " + tableName + " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Currency int(2) NOT NULL DEFAULT 1)";
	public final static String createCurrencyTable = "CREATE TABLE IF NOT EXISTS " + currencyTable + " ( ID int(2) NOT NULL, Name varchar(255) NOT NULL, Symbol varchar(3) NOT NULL, Decimals int(1) NOT NULL)";
	
	private SQLiteDatabase db=null;

	private CommonDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createMainTable);
		createCurrencyTable(db);
	}		


	public void createCurrencyTable(SQLiteDatabase db){
		try{
			db.execSQL(createCurrencyTable);
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (1, 'Indian Rupee', 'INR', 0)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (2, 'US Dollar', 'USD', 2)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (3, 'Euro', 'EUR', 2)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (4, 'Japanese Yen', 'JPY', 0)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (5, 'South Korean Won', 'KRW', 0)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (6, 'British Pound', 'GBP', 2)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (7, 'Hong Kong Dollar', 'HKD', 1)");
			db.execSQL("INSERT INTO " + currencyTable + " VALUES (8, 'Canadian Dollar', 'CAD', 2)");
		}catch(Exception e){
			Log.e("Sameer","msg",e);
		}
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			if(oldVersion < 3){
				db.execSQL("ALTER TABLE " + tableName +" ADD COLUMN Currency int(2) NOT NULL DEFAULT 1");
				createCurrencyTable(db);
			}
		}catch(Exception e){
			Log.e("Sameer","msg",e);
		}
		
	}

	public Cursor groupList(){
		Cursor gquery = getDB().rawQuery("SELECT Name FROM " + tableName,null);
		return gquery;
	}

	public int GroupNameToDatabaseId(String GroupName){
		Cursor idquery = getDB().rawQuery("SELECT ID FROM " + tableName +" WHERE Name = ?",new String[]{GroupName});
		idquery.moveToFirst();
		int databaseId = idquery.getInt(0);

		return databaseId;
	}

	public int GroupNameToCurrency(String GroupName){
		Cursor idquery = getDB().rawQuery("SELECT Currency FROM " + tableName +" WHERE Name = ?",new String[]{GroupName});
		idquery.moveToFirst();
		int currencyId = idquery.getInt(0);

		return currencyId;
	}
	
	public int getCurrencyDecimals(int grpCurrency){
		Cursor idquery = getDB().rawQuery("SELECT Decimals FROM " + currencyTable +" WHERE ID = ?",new String[]{String.valueOf(grpCurrency)});
		idquery.moveToFirst();
		int currencyId = idquery.getInt(0);

		return currencyId;
	}
	
	public String getCurrencySymbol(int grpCurrency) {
		Cursor idquery = getDB().rawQuery("SELECT Symbol FROM " + currencyTable +" WHERE ID = ?",new String[]{String.valueOf(grpCurrency)});
		idquery.moveToFirst();
		String currencySymbol = idquery.getString(0);
		return currencySymbol;
	}
	
	public String[] getCurrencies() {
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + currencyTable, null);
		mquery.moveToFirst();
		String[] currencyArray = new String[mquery.getCount()];
		int i = 0;
		do{
			currencyArray[i] = mquery.getString(1) + " (" + mquery.getString(2) + ")";
			i++;
		}while(mquery.moveToNext());
		
		return currencyArray;
	}
	
	public int insert(String name, int currencyId){
		int ID=1;
		Cursor isPresent=getDB().rawQuery("SELECT ID FROM " + tableName + " WHERE Name = ?", new String[]{name});
		if(isPresent.getCount()>0){
			return -1;				
		}
		Cursor count = getDB().rawQuery("SELECT max(ID) FROM " + tableName , null);
		if(count.moveToFirst()){
			ID=count.getInt(0)+1;
		}
		ContentValues values = new ContentValues();
		values.put("ID", ID);
		values.put("Name", name);
		values.put("Currency", currencyId);
		getDB().insert(tableName,null,values);

		return ID;
	}

	public boolean updategroupname(String grpname,int id){
		Cursor isPresent=getDB().rawQuery("SELECT ID FROM " + tableName + " WHERE Name = ?",new String[]{grpname});
		if(isPresent.getCount()>0){
			return false;
		}
		getDB().execSQL("UPDATE "+tableName+" SET Name = ? WHERE ID = ?",new Object[]{grpname,id});
		return true;
	}
	
	public boolean updateGroupCurrency(int grpCurrency,int id){
		getDB().execSQL("UPDATE "+tableName+" SET Currency = ? WHERE ID = ?",new Object[]{grpCurrency,id});
		return true;
	}

	public void deleteID(int id){
		getDB().execSQL("DELETE FROM "+ tableName+" WHERE ID = ?",new Object[]{id});
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

	private static CommonDatabase DB_;

	public static CommonDatabase get(Context c) {
		if (DB_ == null) {
			DB_ = new CommonDatabase(c);
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
