package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.AndroidFriends.R;
import com.AndroidFriends.R.id;

public class HistoryActivity extends Activity {

	private String grpName = "";
	private int grpid = 0;
	private int currencyDecimals = 2;
	private int eventId = 0;
	private int memberId = 0;
	private String[] namearray=null;
	private Spinner spin1, memberSpin=null;
	private List<String> listofevents = null;
	private List<String> listofmembers = null;
	private int[] idarray = null;
	private int[] flagarray = null;
	private LinearLayout historytable = null;
	private LinearLayout historytablerow1,historytablerow2;
	private LayoutInflater inflater;
	private GroupDatabase gpdb;
	private String decimalFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_history);

		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		
		inflater = LayoutInflater.from(this);

		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		historytable = (LinearLayout) findViewById(R.id.HistoryTable);
		historytablerow1 = (LinearLayout) findViewById(R.id.historyrow1);
		historytablerow2 = (LinearLayout) findViewById(R.id.historyrow2);

		MemberList();
		addItemsOnMemberSpinner();
		memberSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				memberId = position;
				fillEvents(position);
			}	
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
		
	}
	
	public void fillEvents(int member) {
		EventList(member);
		addItemsOnSpinner();
		if(idarray.length>0){
			filltable(0);
		}
		spin1.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				eventId = position;
				filltable(position);
			}	
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case id.menu_clear:
			clearAlert(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addItemsOnSpinner() {

		spin1 = (Spinner) findViewById(R.id.historyDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofevents);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin1.setAdapter(dataAdapter);
		spin1.setPrompt("Select Event");
	}
	
	public void addItemsOnMemberSpinner() {

		memberSpin = (Spinner) findViewById(R.id.memberDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofmembers);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memberSpin.setAdapter(dataAdapter);
		memberSpin.setPrompt("Select Member");
	}

	public void EventList(int member){
		listofevents = new ArrayList<String>();
		try{
			Cursor mquery = gpdb.eventList(member);
			idarray=new int[mquery.getCount()];
			flagarray=new int[mquery.getCount()];
			int temp=0;
			mquery.moveToFirst();
			do{
				listofevents.add(mquery.getString(1));
				idarray[temp]=mquery.getInt(0);
				flagarray[temp]=mquery.getInt(2);
				temp++;
			}while(mquery.moveToNext());
		}catch(Exception e){}
		
	}
	
	public void MemberList(){
		listofmembers = new ArrayList<String>();
		listofmembers.add("All");
		for (int j=0; j<namearray.length; j++) {
			listofmembers.add(namearray[j]);
		}
	}

	public void filltable(int position){
		historytable.removeAllViews();
		int tempid = idarray[position];
		int tempflag = flagarray[position];
		LinearLayout editLayout = (LinearLayout) findViewById(R.id.editLayout);
		
		if (tempflag == GroupDatabase.deletedEventFlag || tempflag == GroupDatabase.deletedCashTransferFlag) {
			editLayout.setVisibility(View.GONE);
		}
		else {
			editLayout.setVisibility(View.VISIBLE);
		}
		
		String str1,str2,str3;
		float paid,consumed;
		try{
			if(tempflag==GroupDatabase.eventFlag || tempflag==GroupDatabase.deletedEventFlag){
				historytablerow2.setVisibility(View.GONE);
				historytablerow1.setVisibility(View.VISIBLE);
				Cursor mquery = gpdb.TransList(tempid);
				mquery.moveToFirst();
				do{
					str1=namearray[mquery.getInt(0)-1];
					paid= mquery.getFloat(1);
					consumed = mquery.getFloat(2);
					str2=(consumed >0) ? String.format(decimalFlag, consumed) : null;
					str3=(paid >0) ? String.format(decimalFlag, paid) : null;
					addEntry(str1,str2,str3);
				}while(mquery.moveToNext());
			}
			else if(tempflag==GroupDatabase.cashTransferFlag || tempflag==GroupDatabase.deletedCashTransferFlag){
				historytablerow1.setVisibility(View.GONE);
				historytablerow2.setVisibility(View.VISIBLE);
				Cursor mquery = gpdb.CashList(tempid);
				mquery.moveToFirst();
				do{
					str1=namearray[mquery.getInt(0)-1];
					paid= mquery.getFloat(2);
					str2=namearray[mquery.getInt(1)-1];
					str3=String.format(decimalFlag, paid);
					addEntry(str1,str2,str3);
				}while(mquery.moveToNext());
			}
		}catch(Exception e){}
			
	}

	public void addEntry(String str1,String str2,String str3){
		View convertView = inflater.inflate(R.layout.table_item, null);
		TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
		TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
		TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
		v1.setText(str1);
		v2.setText(str2);
		v3.setText(str3);
		
		MainActivity.setWeight(v1,1.1f);
		MainActivity.setWeight(v2,1.2f);
		MainActivity.setWeight(v3,1f);
		
		historytable.addView(convertView);
	}
	
	public void previousEvent(View v) {

	}
	
	public void nextEvent(View v) {

	}
	
	public void editEvent(View v) {

	}
	
	public void deleteEvent(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Event");
		alertDialogBuilder
		.setMessage("Are you sure you want to delete this event?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteEventFromDatabase(eventId);
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void clearAlert(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Clear History");
		alertDialogBuilder
		.setMessage("Are you sure you want to clear the event history?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				clearlog();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void clearlog(){
		gpdb.clearlog();
		this.finish();
	}
	
	public void deleteEventFromDatabase(int position) {
		int tempid = idarray[position];
		int tempflag = flagarray[position];

		try{
			if(tempflag==GroupDatabase.eventFlag){
				gpdb.DeleteFromTransList(tempid);
			}
			else if(tempflag==GroupDatabase.cashTransferFlag){
				gpdb.DeleteFromCashList(tempid);
			}
		}catch(Exception e){}
		
		fillEvents(memberId);
		spin1.setSelection(eventId);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
