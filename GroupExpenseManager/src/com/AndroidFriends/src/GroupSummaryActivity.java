package com.AndroidFriends.src;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.AndroidFriends.R;

public class GroupSummaryActivity extends Activity {

	private String grpName = "";
	private int grpId=0;
	private int countmembers=0;
	public final static String listofmember = "summaryActivity/listmember";
	public final static String listofbalance = "summaryActivity/listbalance";
	public final static String listofpaid = "summaryActivity/listpaid";
	public final static String listofconsumed = "summaryActivity/listconsumed";
	public final static String listofid = "summaryActivity/listid";
	public final static String stringcount = "summaryActivity/count";
	public final static String clearflag = "summaryActivity/clearflag";
	private String[] namearray;
	private float[] balancearray;
	private float[] paidarray;
	private float[] consumedarray;
	private int[] idarray;
	private GroupDatabase gpdb;
	private CommonDatabase commondb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		grpId = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		String database="Database_"+grpId;
		
		String gdName="Database_"+grpId;
    	SQLiteDatabase groupDb=null;
    	countmembers=0;
    	
    	try{
    		groupDb = this.openOrCreateDatabase(gdName, MODE_PRIVATE, null);
    		Log.e("Sameer",String.valueOf(groupDb.getVersion()));
    		Cursor mquery = groupDb.rawQuery("SELECT ID, Name, Balance FROM " + GroupDatabase.MemberTable+";",null);
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
			
			
			groupDb.execSQL("DROP TABLE "+GroupDatabase.MemberTable);
			groupDb.execSQL("CREATE TABLE IF NOT EXISTS Members ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Paid float NOT NULL, Consumed float NOT NULL );");
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
				groupDb.insert(GroupDatabase.MemberTable, null, values);
			}
    		
    	}catch(Exception e) {
    		
    		//Log.e("Sameer", "Table doesn't contain column named Balance", e);
        }
        finally{ 
        	if(groupDb!=null)
        		groupDb.setVersion(2);
        		groupDb.close();
        }
    	
		gpdb=GroupDatabase.get(this, database);
		commondb = CommonDatabase.get(this);
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_group_summary);
		TextView header = (TextView) findViewById(R.id.groupNametextView);
		header.setText(grpName);
		MemberListWithBalance();
		fillEntryInTable();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		MemberListWithBalance();
		correctEntryInTable();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case 0 : 
			this.onRestart();
			break;
		} 
	}
	
	@Override
	public void finish(){
		super.finish();
		gpdb.close();
		GroupDatabase.closeAll();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_group_summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_edit:
			editGroup(null);
			return true;
		case R.id.menu_delete:
			DeleteAlert();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void MemberListWithBalance(){
		countmembers = 0;
		try{
			Cursor mquery = gpdb.MemberListWithBalance();			
			namearray = new String[mquery.getCount()];
			idarray = new int[mquery.getCount()];
			balancearray = new float[mquery.getCount()];
			paidarray = new float[mquery.getCount()];
			consumedarray = new float[mquery.getCount()];
			mquery.moveToFirst();
			do{
				idarray[countmembers] = mquery.getInt(0);
				namearray[countmembers] = mquery.getString(1);
				paidarray[countmembers] = mquery.getFloat(2);
				consumedarray[countmembers] = mquery.getFloat(3);
				balancearray[countmembers] = paidarray[countmembers] - consumedarray[countmembers];
				countmembers++;
			}while(mquery.moveToNext());
		}catch(Exception e){}
		
	}

	public void fillEntryInTable(){
		TableLayout tl = (TableLayout)findViewById(R.id.groupSummaryTableLayout);
		for(int j=0;j<countmembers;j++){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			TextView v1= new TextView(this);
			TextView v2= new TextView(this);
			TextView v3= new TextView(this);
			v1.setText(namearray[j]);
			float a = balancearray[j];
			if (a<0) {
				v2.setText(floatToString(-a));
				v3.setText(null);
			}
			else {
				v3.setText(floatToString(a));
				v2.setText(null);
			}	
			v1.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
			v2.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.7f));
			v3.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.7f));

			v1.setGravity(Gravity.CENTER);
			v2.setGravity(Gravity.CENTER);
			v3.setGravity(Gravity.CENTER);
			v1.setPadding(0, 20, 0, 20);
			v2.setPadding(0, 20, 0, 20);
			v3.setPadding(0, 20, 0, 20);

			tr.addView(v1);
			tr.addView(v2);
			tr.addView(v3);
			tl.addView(tr);
		}
	}

	public void correctEntryInTable(){
		TableLayout table = (TableLayout) findViewById(R.id.groupSummaryTableLayout);
		for (int k=0; k<countmembers; k++) {
			TableRow tr2 = (TableRow)table.getChildAt(k);
			TextView v1 = (TextView) (tr2.getChildAt(0));
			TextView v2 = (TextView) (tr2.getChildAt(1));
			TextView v3 = (TextView) (tr2.getChildAt(2));
			v1.setText(namearray[k]);
			float a = balancearray[k];
			if (a<0) {
				v2.setText(floatToString(-a));
				v3.setText(null);
			}
			else {
				v3.setText(floatToString(a));
				v2.setText(null);
			}
		}
	}

	public void cashTransfer(View v) {
		Intent intent = new Intent(this, CashTransferActivity.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
		intent.putExtra(listofmember, namearray);
		startActivity(intent);
	}

	public void showSolution(View v){
		Intent intent = new Intent(this, PossibleSolution.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(listofid, idarray);
		intent.putExtra(listofmember, namearray);
		intent.putExtra(listofbalance, balancearray);
		intent.putExtra(stringcount, countmembers);
		startActivity(intent);
	}
	
	public void showSummary(View v){
		Intent intent = new Intent(this, IndividualSummaryActivity.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(listofid, idarray);
		intent.putExtra(listofmember, namearray);
		intent.putExtra(listofpaid, paidarray);
		intent.putExtra(listofconsumed, consumedarray);
		intent.putExtra(stringcount, countmembers);
		startActivity(intent);
	}

	public void showHistory(View v){
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
		intent.putExtra(listofmember, namearray);
		startActivity(intent);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
		case 0 : return new AlertDialog.Builder(this)
		.setTitle("Clear Balance")
		.setMessage("Are you sure you want to clear the group balance?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				nullify();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.create();
		case 1: return new AlertDialog.Builder(this)
		.setTitle("Delete Group")
		.setMessage("Are you sure you want to delete the group?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteGroup();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.create();
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public void nullifyAlert(View v) {
		showDialog(0);
	}

	public void nullify(){
		Intent intent = new Intent(this, PossibleSolution.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
		intent.putExtra(listofid, idarray);
		intent.putExtra(listofmember, namearray);
		intent.putExtra(listofbalance, balancearray);
		intent.putExtra(stringcount, countmembers);
		intent.putExtra(clearflag, true);
		startActivityForResult(intent,0);
	}

	public void editGroup(View v){
		Intent intent = new Intent(this, EditGroupActivity.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
		intent.putExtra(listofmember, namearray);
		startActivity(intent);
	}

	public void deleteGroup(){
		String databasename="Database_"+grpId;
		this.deleteDatabase(databasename);
		commondb.deleteID(grpId);
		this.finish();
	}

	@SuppressWarnings("deprecation")
	public void DeleteAlert() {
		showDialog(1);
	}

	public void toAddEvent(View v) {
		Intent intent = new Intent(this, AddEventActivity.class);
		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
		intent.putExtra(listofmember, namearray);
		startActivity(intent);
	}
	public String floatToString(float v){
		String result="";
		int r = (int) v;
		result = String.valueOf(r);
		return result;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
