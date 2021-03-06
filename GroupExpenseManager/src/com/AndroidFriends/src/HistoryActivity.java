package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.util.Log;
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

@SuppressLint("InflateParams")
public class HistoryActivity extends Activity {

	public static final String EVENT_ID= "historyActivity/eventid";
	public static final String EVENT_NAME= "historyActivity/eventname";
	
	private String grpName = "";
	private int grpid = 0;
	private int currencyDecimals = 2;
	private int eventIdArrayPosition = 0;
	private int memberId = 0;
	private String[] namearray=null;
	private Spinner eventSpin, memberSpin=null;
	private List<String> listofevents = null;
	private List<String> listofmembers = null;
	private int[] idarray = null;
	private int[] flagarray = null;
	private LinearLayout historytable = null;
	private Button restoreButton;
	private LinearLayout historytablerow1,historytablerow2, editLayout, prevNext;
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
		prevNext = (LinearLayout)findViewById(R.id.previousNextLayout);
		editLayout = (LinearLayout) findViewById(R.id.editLayout);
		restoreButton = (Button) findViewById(R.id.restoreButton);
		
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
	
	@Override
	public void onRestart() {
		super.onRestart();
		fillEvents(memberId);
		eventSpin.setSelection(eventIdArrayPosition);
	}
	
	public void fillEvents(int member) {
		EventList(member);
		addItemsOnSpinner();
		
		filltable(0);
		
		eventSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				eventIdArrayPosition = position;
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

		eventSpin = (Spinner) findViewById(R.id.historyDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofevents);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventSpin.setAdapter(dataAdapter);
		eventSpin.setPrompt("Select Event");
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
		TextView dtTextView = (TextView) findViewById(R.id.dateTimeText);
		
		if(idarray.length == 0){
			prevNext.setVisibility(View.GONE);
			editLayout.setVisibility(View.GONE);
			historytablerow1.setVisibility(View.GONE);
			historytablerow2.setVisibility(View.GONE);
			restoreButton.setVisibility(View.GONE);
			dtTextView.setVisibility(View.GONE);
			return;
		}
		
		int tempid = idarray[position];
		int tempflag = flagarray[position];
		
		
		dtTextView.setVisibility(View.VISIBLE);
		String dt = "Time of event : ";
		long timeinmillis = gpdb.getEventDate(tempid);
		if(timeinmillis == 0){
			dt += "Not Available";
		}else{
			dt += DateUtils.formatDateTime(this, timeinmillis, DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
		}
		dtTextView.setText(dt);
		
		//Prev Next Button Visibility
		View prev = (View)prevNext.findViewById(R.id.previousButton);
		View next = (View)prevNext.findViewById(R.id.nextButton);
		
		if(idarray.length == 1){
			prevNext.setVisibility(View.GONE);
		}
		else{
			prevNext.setVisibility(View.VISIBLE);
			
			if(eventIdArrayPosition == 0){
				prev.setVisibility(View.INVISIBLE);
			}
			else{
				prev.setVisibility(View.VISIBLE);
			}
			
			int lastEventPosition = idarray.length - 1;
			
			if(eventIdArrayPosition == lastEventPosition){
				next.setVisibility(View.INVISIBLE);
			}
			else{
				next.setVisibility(View.VISIBLE);
			}
		}
		
		//Edit delete button Visibility
		if (tempflag == GroupDatabase.deletedEventFlag || tempflag == GroupDatabase.deletedCashTransferFlag) {
			editLayout.setVisibility(View.GONE);
			restoreButton.setVisibility(View.VISIBLE);
		}else if(tempflag == GroupDatabase.balanceSettleFlag){
			editLayout.setVisibility(View.GONE);
			restoreButton.setVisibility(View.GONE);
		}else {
			editLayout.setVisibility(View.VISIBLE);
			restoreButton.setVisibility(View.GONE);
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
			else if(tempflag==GroupDatabase.cashTransferFlag || tempflag==GroupDatabase.deletedCashTransferFlag || tempflag==GroupDatabase.balanceSettleFlag){
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
		eventSpin.setSelection(eventIdArrayPosition - 1);
	}
	
	public void nextEvent(View v) {
		eventSpin.setSelection(eventIdArrayPosition + 1);
	}
	
	public void editEvent(View v) {
		try{
			int tempid = idarray[eventIdArrayPosition];
			int tempFlag = flagarray[eventIdArrayPosition];
			String tempName = listofevents.get(eventIdArrayPosition);
			
			if(tempFlag == GroupDatabase.eventFlag){
				Intent intent = new Intent(this, EditEventActivity.class);
				intent.putExtra(GroupsActivity.GROUP_ID, grpid);
				intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
				intent.putExtra(GroupSummaryActivity.listofmember, namearray);
				intent.putExtra(GroupSummaryActivity.stringDecimals, currencyDecimals);
				intent.putExtra(EVENT_ID, tempid);
				intent.putExtra(EVENT_NAME, tempName);
				startActivity(intent);
			}else if(tempFlag == GroupDatabase.cashTransferFlag){
				Intent intent = new Intent(this, EditCashTransferActivity.class);
				intent.putExtra(GroupsActivity.GROUP_ID, grpid);
				intent.putExtra(GroupSummaryActivity.listofmember, namearray);
				intent.putExtra(GroupSummaryActivity.stringDecimals, currencyDecimals);
				intent.putExtra(EVENT_ID, tempid);
				intent.putExtra(EVENT_NAME, tempName);
				startActivity(intent);
			}
		}catch(Exception e){
			Log.e("Sameer","Here",e);
		}
	}
	
	public void deleteEvent(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Event");
		alertDialogBuilder
		.setMessage("Deleting an event will change the balance of the involved members. Are you sure you want to delete this event?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteEventFromDatabase(eventIdArrayPosition);
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
	
	public void restoreEvent(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Restore Deleted Event");
		alertDialogBuilder
		.setMessage("Restoring a deleted event will change the balance of the involved members. Are you sure you want to restore this event?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				restoreDeletedEventInDatabase(eventIdArrayPosition);
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
		.setMessage("Clearing the history will delete all the events from history but will not affect the balance of any member. Are you sure you want to continue?")
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
				gpdb.DeleteFromTransList(tempid, grpName);
			}
			else if(tempflag==GroupDatabase.cashTransferFlag){
				gpdb.DeleteFromCashList(tempid);
			}
		}catch(Exception e){}
		
		fillEvents(memberId);
		eventSpin.setSelection(eventIdArrayPosition);
	}
	
	public void restoreDeletedEventInDatabase(int position) {
		int tempid = idarray[position];
		int tempflag = flagarray[position];

		try{
			if(tempflag==GroupDatabase.deletedEventFlag){
				gpdb.restoreInTransList(tempid, grpName);
			}
			else if(tempflag==GroupDatabase.deletedCashTransferFlag){
				gpdb.restoreInCashList(tempid);
			}
		}catch(Exception e){}
		
		fillEvents(memberId);
		eventSpin.setSelection(eventIdArrayPosition);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
