/* Event table -
 * Flag column has:
 * 1 if event
 * 2 if cash transfer
 * 3 if balance settle
 * 8 if deleted event
 * 9 if deleted cash transfer
 */

package com.AndroidFriends.src;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GroupDatabase extends SQLiteOpenHelper{
	private static String DATABASE_NAME = null;
	private static final int DATABASE_VERSION = 5;
	private static Context CONTEXT;

	public final static int eventFlag = 1;
	public final static int cashTransferFlag = 2;
	public final static int balanceSettleFlag = 3;
	public final static int deletedEventFlag = 8;
	public final static int deletedCashTransferFlag = 9;

	public final static String balanceSettleString = "Balance Settle";
	public final static String MemberTable = "Members";
	public final static String EventTable = "Events";
	public final static String TransTable = "Transactions";
	public final static String CashTable = "CashTransfer";

	public final static String createMember = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.MemberTable+" ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Paid float NOT NULL, Consumed float NOT NULL, User int(1) NOT NULL DEFAULT 0 )";
	public final static String createEvent = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.EventTable+" ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Flag int(1), Date BIGINT NOT NULL Default 0)";
	public final static String createTrans = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.TransTable+" ( MemberId int(11) NOT NULL, Paid float, Consumed float, EventId int(11) NOT NULL )";
	public final static String createCash = "CREATE TABLE IF NOT EXISTS "+GroupDatabase.CashTable+" ( FromMemberId int(11) NOT NULL, ToMemberId int(11) NOT NULL, Amount float NOT NULL, ID int(11) NOT NULL)";

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
		switch (oldVersion){
		case 1:
			Cursor mquery = db.rawQuery("SELECT ID, Name, Balance FROM " + MemberTable,null);
			int[] idarray = new int[mquery.getCount()];
			float[] balancearray = new float[mquery.getCount()];
			String[] namearray = new String[mquery.getCount()];
			int countmembers=0;
			mquery.moveToFirst();
			do{
				idarray[countmembers] = mquery.getInt(0);
				namearray[countmembers] = mquery.getString(1);
				balancearray[countmembers] = mquery.getFloat(2);
				countmembers++;
			}while(mquery.moveToNext());


			db.execSQL("DROP TABLE "+MemberTable);
			db.execSQL(createMember);
			for(int i=0;i<balancearray.length;i++){
				ContentValues values = new ContentValues();
				values.put("ID", idarray[i]);
				values.put("Name", namearray[i]);
				if(balancearray[i]>0){
					values.put("Paid", balancearray[i]);
					values.put("Consumed", 0);
				}else if(balancearray[i]<0){
					balancearray[i]*=(-1);
					values.put("Paid", 0);
					values.put("Consumed", balancearray[i]);
				}
				db.insert(MemberTable, null, values);
			}
		case 2:
			db.execSQL("ALTER TABLE " + EventTable +" ADD COLUMN Date BIGINT NOT NULL Default 0");
		case 3:
			db.execSQL("UPDATE " + EventTable +" SET Flag = ? WHERE NAME = ?",new Object[]{balanceSettleFlag,balanceSettleString});
		case 4:
			db.execSQL("ALTER TABLE " + MemberTable +" ADD COLUMN User int(1) NOT NULL Default 0");
		default:
			break;
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
			values.put("User", 0);
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
			values.put("User", 0);
			getDB().insert(MemberTable,null,values);
		}
	}
	
	public Cursor MemberListWithBalance(){
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + MemberTable,null);
		return mquery;
	}

	public Cursor CashList(int id){
		Cursor c = getDB().rawQuery("SELECT * FROM " + CashTable+" WHERE ID = " + id,null);
		return c;
	}

	public void updateCashTransfer(int eventid, int fromMember, int toMember, float amount, String from, String to){

		String eventName = "Edited - Cash - " + from + " to " + to; 
		getDB().execSQL("UPDATE "+EventTable+" SET Name = ? WHERE ID = ?", new Object[]{eventName, eventid});

		UpdateMemberTableAfterCashTransferDelete(eventid);

		getDB().execSQL("UPDATE "+CashTable+" SET FromMemberId = ?, ToMemberId = ?, Amount = ? WHERE ID = ?",new Object[]{fromMember, toMember, amount, eventid});
		UpdateMemberTableAfterCashTransfer(fromMember, toMember, amount);

	}

	public void CashTransfer(int fromMember, int toMember, float amount, String from, String to){
		int ID1=1;

		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM "+EventTable, null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		String eventName = "Cash - " + from + " to " + to; 
		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", cashTransferFlag);
		value1.put("Date", System.currentTimeMillis());
		getDB().insert(EventTable,null,value1);

		ContentValues value2 = new ContentValues();
		value2.put("ID", ID1);
		value2.put("FromMemberId", fromMember);
		value2.put("ToMemberId", toMember);
		value2.put("Amount",amount);
		getDB().insert(CashTable,null,value2);

		UpdateMemberTableAfterCashTransfer(fromMember, toMember, amount);
	}

	public void UpdateMemberTableAfterCashTransfer(int fromMemberId, int toMemberId, float amount){
		getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid + ? WHERE ID = ?", new Object[]{amount,fromMemberId});
		getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid - ? WHERE ID = ?", new Object[]{amount,toMemberId});
	}

	public void UpdateMemberTableAfterCashTransferDelete(int id){
		int fromMemberId, toMemberId;
		float amount;
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + CashTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		do{
			fromMemberId = mquery.getInt(0);
			toMemberId= mquery.getInt(1);
			amount = mquery.getFloat(2);
			getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid - ? WHERE ID = ?", new Object[]{amount,fromMemberId});
			getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid + ? WHERE ID = ?", new Object[]{amount,toMemberId});
		}while(mquery.moveToNext());
	}

	public void DeleteFromCashList(int id){
		String newName = "Deleted - ";
		getDB().execSQL("UPDATE " + EventTable + " SET Name = ? || Name, Flag = ? WHERE ID = " + id,new Object[]{newName, deletedCashTransferFlag});

		UpdateMemberTableAfterCashTransferDelete(id);
	}

	public void restoreInCashList(int id){
		getDB().execSQL("UPDATE " + EventTable + " SET Name = SUBSTR(Name, 11, LENGTH(Name)), Flag = ? WHERE ID = " + id,new Object[]{cashTransferFlag});
		int fromMemberId, toMemberId;
		float amount;
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + CashTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		do{
			fromMemberId = mquery.getInt(0);
			toMemberId= mquery.getInt(1);
			amount = mquery.getFloat(2);
			UpdateMemberTableAfterCashTransfer(fromMemberId, toMemberId, amount);
		}while(mquery.moveToNext());
	}

	public Cursor TransList(int id){
		Cursor c = getDB().rawQuery("SELECT * FROM " + TransTable+" WHERE EventId = " + id,null);
		return c;
	}

	public void UpdateMemberTableAfterEventDelete(int id, String groupName){
		int memberId;
		float paid, consumed;
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + TransTable + " WHERE EventId = " + id, null);
		mquery.moveToFirst();
		do{
			memberId = mquery.getInt(0);
			paid= mquery.getFloat(1);
			consumed = mquery.getFloat(2);
			getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid - ?, Consumed = Consumed - ? WHERE ID = ?", new Object[]{paid, consumed, memberId});
		}while(mquery.moveToNext());
		
		Cursor userQuery = getDB().rawQuery("SELECT User FROM " + MemberTable + " WHERE ID = ?", new String[]{String.valueOf(1)});
		userQuery.moveToFirst();
		if (userQuery.getInt(0) == 1) {
			addGroupExpensesToPersonalExpenses(groupName);
		}
	}

	public void DeleteFromTransList(int id, String groupName){
		String newName = "Deleted - ";
		getDB().execSQL("UPDATE " + EventTable + " SET Name = ? || Name, Flag = ? WHERE ID = ?",new Object[]{newName, deletedEventFlag, id});
		UpdateMemberTableAfterEventDelete(id, groupName);
	}

	public void restoreInTransList(int id, String groupName){
		getDB().execSQL("UPDATE " + EventTable + " SET Name = SUBSTR(Name, 11, LENGTH(Name)), Flag = ? WHERE ID = ?",new Object[]{eventFlag, id});

		int memberId;
		float paid, consumed;
		Cursor mquery = getDB().rawQuery("SELECT * FROM " + TransTable + " WHERE EventId = " + id, null);
		mquery.moveToFirst();
		do{
			memberId = mquery.getInt(0);
			paid= mquery.getFloat(1);
			consumed = mquery.getFloat(2);
			getDB().execSQL("UPDATE " + MemberTable + " SET Paid = Paid + ?, Consumed = Consumed + ? WHERE ID = ?", new Object[]{paid, consumed, memberId});
			
			Cursor userQuery = getDB().rawQuery("SELECT User FROM " + MemberTable + " WHERE ID = ?", new String[]{String.valueOf(memberId)});
			userQuery.moveToFirst();
			if (userQuery.getInt(0) == 1) {
				addGroupExpensesToPersonalExpenses(groupName);
			}
		}while(mquery.moveToNext());
	}

	public int getEventFlag(int id) {
		Cursor mquery = getDB().rawQuery("SELECT Flag FROM " + EventTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		return mquery.getInt(0);
	}

	public Cursor eventList(int member){
		if (member==0) {
			Cursor mquery = getDB().rawQuery("SELECT * FROM " + EventTable+" ORDER BY ID DESC",null);
			return mquery;
		}
		else {
			String transQuery = "SELECT EventId FROM " + TransTable + " WHERE MemberId = " + member + " AND (Paid > 0 OR Consumed > 0)";
			String cashQuery = "SELECT ID as EventId FROM " + CashTable + " WHERE FromMemberId = " + member + " OR ToMemberId = " + member;
			String eventIdQuery = "(" + transQuery + " UNION " + cashQuery + ") as T1";
			Cursor mquery = getDB().rawQuery("SELECT * FROM " + EventTable + " JOIN " + eventIdQuery + " ON Events.ID = T1.EventId ORDER BY ID DESC",null);
			return mquery;
		}
	}

	private void addEntryInTransTable(int eventid, float[] amountPaid, int[] paidMembers, float[] amountConsumed, List<boolean[]> whoConsumed, int arraysize, String groupName) {
		float[] memberBalance;
		memberBalance=new float[arraysize];
		float[] consumedBalance;
		consumedBalance=new float[arraysize];

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
				value2.put("EventId", eventid);
				getDB().insert(TransTable,null,value2);

				getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid+?, Consumed = Consumed+? WHERE ID = ?",new Object[]{memberBalance[i],consumedBalance[i],(i+1)});
			
				// For syncing with personal expenses
				int memberId = i+1;
				Cursor userQuery = getDB().rawQuery("SELECT User FROM " + MemberTable + " WHERE ID = ?", new String[]{String.valueOf(memberId)});
				userQuery.moveToFirst();
				if (userQuery.getInt(0) == 1) {
					addGroupExpensesToPersonalExpenses(groupName);
				}
			}
		}
	}

	private void UpdateEventNameEdited(int eventid, String eventName){
		String editString = "Edited - ";
		boolean flag = false;
		if(eventName.length()<9){
			flag = true;
		}
		else if(!(eventName.substring(0, 9)).equals(editString)){
			flag = true;
		}
		if (flag){
			String newName = editString+eventName;
			getDB().execSQL("UPDATE " + EventTable + " SET Name = ? WHERE ID = ?",new Object[]{newName,eventid});
		}
	}

	public void addEvent(String eventName, float[] amountPaid, int[] paidMembers, float[] amountConsumed, List<boolean[]> whoConsumed,String[] namearray, String groupName){
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", eventFlag);
		value1.put("Date", System.currentTimeMillis());
		getDB().insert(EventTable,null,value1);

		addEntryInTransTable(ID1, amountPaid, paidMembers, amountConsumed, whoConsumed, namearray.length, groupName);
	}

	public void updateEvent(int eventid, String eventName, float[] amountPaid, int[] paidMembers, float[] amountConsumed, List<boolean[]> whoConsumed,int arraysize, String groupName){
		UpdateEventNameEdited(eventid, eventName);

		UpdateMemberTableAfterEventDelete(eventid, groupName);
		getDB().delete(TransTable,"EventId = " + eventid, null);

		addEntryInTransTable(eventid, amountPaid, paidMembers, amountConsumed, whoConsumed, arraysize, groupName);
	}

	private void addEntryInTransTableIndividualExpense(int eventid, int member,	float amount, String groupName) {
		ContentValues value2 = new ContentValues();
		value2.put("MemberId", member+1);
		value2.put("Paid", amount);
		value2.put("Consumed", amount);
		value2.put("EventId", eventid);
		getDB().insert(TransTable,null,value2);

		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = Paid+?, Consumed = Consumed+? WHERE ID = ?",new Object[]{amount,amount,member+1});
		
		int memberId = member+1;
		Cursor userQuery = getDB().rawQuery("SELECT User FROM " + MemberTable + " WHERE ID = ?", new String[]{String.valueOf(memberId)});
		userQuery.moveToFirst();
		if (userQuery.getInt(0) == 1) {
			addGroupExpensesToPersonalExpenses(groupName);
		}
	}

	public void addIndividualEvent(String eventName, float amount, int member,String groupName){
		int ID1=1;
		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", eventName);
		value1.put("Flag", eventFlag);
		value1.put("Date", System.currentTimeMillis());
		getDB().insert(EventTable,null,value1);

		addEntryInTransTableIndividualExpense(ID1, member, amount, groupName);
	}

	public void updateIndividualEvent(int eventid, String eventName, float amount, int member, String groupName){
		UpdateEventNameEdited(eventid, eventName);

		UpdateMemberTableAfterEventDelete(eventid, groupName);
		getDB().delete(TransTable,"EventId = " + eventid, null);

		addEntryInTransTableIndividualExpense(eventid, member, amount, groupName);
	}

	public void clearlog(){
		getDB().execSQL("DROP TABLE " + EventTable);
		getDB().execSQL("DROP TABLE " + TransTable);
		getDB().execSQL("DROP TABLE " + CashTable);
		getDB().execSQL(createEvent);
		getDB().execSQL(createTrans);
		getDB().execSQL(createCash);
	}

	public void clearBalance(int ntransactions,String [][]solutionarray,float []tramountarray, String groupName){
		int ID1=1;

		Cursor count = getDB().rawQuery("SELECT MAX(ID) FROM " + EventTable , null);
		if(count.getCount()>0){
			count.moveToLast();
			ID1=count.getInt(0)+1;
		}

		ContentValues value1 = new ContentValues();
		value1.put("ID", ID1);
		value1.put("Name", balanceSettleString);
		value1.put("Flag", balanceSettleFlag);
		value1.put("Date", System.currentTimeMillis());
		getDB().insert(EventTable,null,value1);

		for(int i=0;i<ntransactions;i++){
			ContentValues value2 = new ContentValues();
			value2.put("ID", ID1);
			value2.put("FromMemberId", solutionarray[i][1]);
			value2.put("ToMemberId", solutionarray[i][3]);
			value2.put("Amount",tramountarray[i]);
			getDB().insert(CashTable,null,value2);
		}
		getDB().execSQL("UPDATE "+MemberTable+" SET Paid = 0, Consumed = 0;");

		removeGroupExpensesFromPersonalExpenses(groupName);
	}

	public long getEventDate(int id){
		Cursor mquery = getDB().rawQuery("SELECT Date FROM " + EventTable + " WHERE ID = " + id, null);
		mquery.moveToFirst();
		return mquery.getLong(0);
	}
	
	public void addUserMember() {
		getDB().execSQL("UPDATE "+MemberTable+" SET User = ? WHERE ID = ?", new Object[]{1, 1});
	}
	
	public void editUserMember(int userMember) {
		getDB().execSQL("UPDATE "+MemberTable+" SET User = ? WHERE ID = ?", new Object[]{userMember, 1});
	}
	
	public boolean getUserMember() {
		Cursor mquery = getDB().rawQuery("SELECT User FROM "+MemberTable+" WHERE ID = 1", null);
		mquery.moveToFirst();
		if (mquery.getInt(0) == 1) return true;
		else return false;
	}

	public void addGroupExpensesToPersonalExpenses(String name) {
		Cursor userQuery = getDB().rawQuery("SELECT Consumed FROM " + MemberTable + " WHERE User = ?", new String[]{String.valueOf(1)});
		float amount = 0;
		if (userQuery.getCount() != 0) {
			userQuery.moveToFirst();
			amount = userQuery.getFloat(0);
			
			PersonalDatabase pdb = PersonalDatabase.get(CONTEXT);
			pdb.addOrEditGroupExpense(name, amount);
		}
	}
	
	public void removeGroupExpensesFromPersonalExpenses(String name) {
		PersonalDatabase pdb = PersonalDatabase.get(CONTEXT);
		pdb.deleteGroupExpense(name);
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
		CONTEXT = c;
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
