package com.AndroidFriends.src;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = null;
	private static final int DATABASE_VERSION = 2;

	public final static String MemberTable = "Members";
	public final static String EventTable = "Events";
	public final static String TransTable = "Transactions";
	public final static String CashTable = "CashTransfer";

	public final static String createMember = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.MemberTable+" ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Paid float NOT NULL, Consumed float NOT NULL );";
	public final static String createEvent = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.EventTable+" ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Flag int(1) );";
	public final static String createTrans = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.TransTable+" ( MemberId int(11) NOT NULL, Paid float, Consumed float, EventId int(11) NOT NULL );";
	public final static String createCash = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.CashTable+" ( FromMemberId int(11) NOT NULL, ToMemberId int(11) NOT NULL, Amount float NOT NULL, ID int(11) NOT NULL);";

	private SQLiteDatabase db=null;

	private GroupDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createMember);
		db.execSQL(createEvent);
		db.execSQL(createTrans);
		db.execSQL(createCash);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion<2){
    	
		}
	}

	public void onCreateInsert(String[] members){
		int length=members.length;
		for(int j=0;j<length;j++){
			ContentValues values = new ContentValues();
			values.put("ID", (j+1));
			values.put("Name", members[j]);
			values.put("Paid", 0);
			values.put("Consumed", 0);
			getDB().insert(MemberTable,null,values);
		}
	}

	public void updateMembers(String[] members,String[] namearray){
		for(int i=0;i<namearray.length;i++){
			if(!members[i].equals(namearray[i])){
				getDB().execSQL("UPDATE "+MemberTable+" SET Name = ? WHERE ID = ?",new Object[]{members[i],(i+1)});
			}
		}
		for(int i=namearray.length;i<members.length;i++){
			ContentValues values = new ContentValues();
			values.put("ID", (i+1));
			values.put("Name", members[i]);
			values.put("Paid", 0);
			values.put("Consumed", 0);
			getDB().insert(MemberTable,null,values);
		}
	}

	public Cursor MemberListWithBalance(){
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + MemberTable+";",null);
		return mquery;
	}
	
	public Cursor TransList(int id){
		Cursor c = getDB().rawQuery("SELECT * FROM " + TransTable+" WHERE EventId = " + id,null);
		return c;
	}
	
	public Cursor CashList(int id){
		Cursor c = getDB().rawQuery("SELECT * FROM " + CashTable+" WHERE ID = " + id,null);
		return c;
	}
	
	public Cursor eventList(int member){
		if (member==0) {
			Cursor mquery = getDB().rawQuery("SELECT * FROM " + EventTable+";",null);
			return mquery;
		}
		else {
			String transQuery = "SELECT EventId FROM " + TransTable + " WHERE MemberId = " + member + " AND (Paid > 0 OR Consumed > 0)";
			String cashQuery = "SELECT ID as EventId FROM " + CashTable + " WHERE FromMemberId = " + member + " OR ToMemberId = " + member;
			String eventIdQuery = "(" + transQuery + " UNION " + cashQuery + ") as T1";
			Cursor mquery = getDB().rawQuery("SELECT * FROM " + EventTable + " JOIN " + eventIdQuery + " ON Events.ID = T1.EventId",null);
			return mquery;
		}
	}
	
	public void CashTransfer(int fromMember, int toMember, float amount, String from, String to){
		int ID1=1;

		Cursor count = getDB().rawQuery("SELECT count(*) FROM "+EventTable, null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		String eventName = "Cash - " + from + " to " + to; 
		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", 2);
		getDB().insert(EventTable,null,value1);

		ContentValues value2 = new ContentValues();
		value2.put("ID", ID1);
		value2.put("FromMemberId", fromMember);
		value2.put("ToMemberId", toMember);
		value2.put("Amount",amount);
		getDB().insert(CashTable,null,value2);

		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid + ? WHERE ID = ?",new Object[]{amount,fromMember});
		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid - ? WHERE ID = ?",new Object[]{amount,toMember});
	}

	public void addEvent(String eventName, float[] amountPaid, int[] paidMembers, float[] amountConsumed, List<boolean[]> whoConsumed,String[] namearray){
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT count(*) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", 1);
		getDB().insert(EventTable,null,value1);

		float[] memberBalance;
		memberBalance=new float[namearray.length];
		float[] consumedBalance;
		consumedBalance=new float[namearray.length];

		//Paid
		for(int j=0;j<memberBalance.length;j++){
			memberBalance[j]=0;
			consumedBalance[j]=0;
		}
		for(int j=0;j<amountPaid.length;j++){
			memberBalance[paidMembers[j]]+=amountPaid[j];
		}

		//Consumption
		int share;
		float eachshare;
		for(int j=0;j<amountConsumed.length;j++){
			share=0;
			eachshare=0;
			for(int i=1;i<whoConsumed.get(j).length;i++){
				if(whoConsumed.get(j)[i]){
					share++;
				}
			}
			eachshare=amountConsumed[j]/share;
			for(int i=1;i<whoConsumed.get(j).length;i++){
				if(whoConsumed.get(j)[i]){
					consumedBalance[i-1]+=eachshare;
				}
			}
		}
		for(int i=0;i<memberBalance.length;i++){
			if(memberBalance[i]!=0 || consumedBalance[i]!=0){
				ContentValues value2 = new ContentValues();
				value2.put("MemberId", (i+1));
				value2.put("Paid", memberBalance[i]);
				value2.put("Consumed", consumedBalance[i]);
				value2.put("EventId", ID1);
				getDB().insert(TransTable,null,value2);
				
				getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid+?, Consumed = Consumed+? WHERE ID = ?",new Object[]{memberBalance[i],consumedBalance[i],(i+1)});
			}
			
		}            
	}
	
	public void addIndividualEvent(String eventName, float amount, int member){
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT count(*) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", 1);
		getDB().insert(EventTable,null,value1);
		
		ContentValues value2 = new ContentValues();
		value2.put("MemberId", member+1);
		value2.put("Paid", amount);
		value2.put("Consumed", amount);
		value2.put("EventId", ID1);
		getDB().insert(TransTable,null,value2);
		
		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid+?, Consumed = Consumed+? WHERE ID = ?",new Object[]{amount,amount,member+1});
	}

	public void clearlog(){
		getDB().execSQL("DROP TABLE " + EventTable);
		getDB().execSQL("DROP TABLE " + TransTable);
		getDB().execSQL("DROP TABLE " + CashTable);
		getDB().execSQL(createEvent);
		getDB().execSQL(createTrans);
		getDB().execSQL(createCash);
	}

	public void clearBalance(int ntransactions,String [][]solutionarray,float []tramountarray){
		int ID1=1;

		Cursor count = getDB().rawQuery("SELECT count(*) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}
		getDB().execSQL("INSERT INTO " + EventTable + " ( ID, Name, Flag ) VALUES ( '" + ID1+"', 'Balance Settle', '2' );" );
		for(int i=0;i<ntransactions;i++){
			getDB().execSQL("INSERT INTO "+ CashTable + " ( ID, FromMemberId, ToMemberId, Amount ) VALUES ( '"+ID1+"', '"+solutionarray[i][1]+"', '"+solutionarray[i][3]+"', '"+tramountarray[i]+"');" );
		}
		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = 0, Consumed = 0;");

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

	private static GroupDatabase DB_;

	public static GroupDatabase get(Context c,String name) {
		if (DB_ == null) {
			if(DATABASE_NAME == null){
				DATABASE_NAME = name;
			}
			DB_ = new GroupDatabase(c);
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
