package com.AndroidFriends.src;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommonDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = "GroupNames";
	private static final int DATABASE_VERSION = 2;
	private static final String tableName = "Groups";

	private SQLiteDatabase db=null;

	private CommonDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableName
				+ " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL );");         
	}		


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public Cursor groupList(){
		Cursor gquery = getDB().rawQuery("SELECT Name FROM " + tableName+";",null);
		return gquery;
	}

	public int GroupNameToDatabaseId(String GroupName){
		Cursor idquery = getDB().rawQuery("SELECT ID FROM " + tableName +" WHERE Name = ?",new String[]{GroupName});
		idquery.moveToFirst();
		int databaseId = idquery.getInt(0);;

		return databaseId;
	}

	public int insert(String name){
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
