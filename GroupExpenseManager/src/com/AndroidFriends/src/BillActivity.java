package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
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

public class BillActivity extends Activity {

	public static final String BILL_ID= "billActivity/billid";
	public static final String BILL_NAME= "billActivity/billname";
	
	private int currencyDecimals = 2;
	private int billIdArrayPosition = 0;
	private Spinner billSpin=null;
	private List<String> listofbills = null;
	private int[] idarray = null;
	private LinearLayout billTable = null;
	private LinearLayout editLayout, prevNext;
	private LayoutInflater inflater;
	private PersonalDatabase pdb;
	private String decimalFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.activity_bills);

		pdb=PersonalDatabase.get(this);
		inflater = LayoutInflater.from(this);

		currencyDecimals = intent.getIntExtra(PersonalActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		billTable = (LinearLayout) findViewById(R.id.billTable);
		prevNext = (LinearLayout)findViewById(R.id.billPreviousNextLayout);
		editLayout = (LinearLayout) findViewById(R.id.billEditLayout);
		
		billList();
		addItemsOnBillSpinner();
		filltable(0);
		
		billSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				billIdArrayPosition = position;
				filltable(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
		
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		billList();
		addItemsOnBillSpinner();
		billSpin.setSelection(billIdArrayPosition);
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
//		case id.menu_clear:
//			clearAlert(null);
//			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addItemsOnBillSpinner() {

		billSpin = (Spinner) findViewById(R.id.billDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofbills);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		billSpin.setAdapter(dataAdapter);
		billSpin.setPrompt("Select Bill");
	}
	
	public void billList(){
		listofbills = new ArrayList<String>();
		try{
			Cursor mquery = pdb.getBillList();
			idarray=new int[mquery.getCount()];
			int temp=0;
			mquery.moveToFirst();
			do{
				listofbills.add(mquery.getString(1));
				idarray[temp]=mquery.getInt(0);
				temp++;
			}while(mquery.moveToNext());
		}catch(Exception e){}
	}
	
	public void fillBills() {
		billList();
		addItemsOnBillSpinner();
		
		filltable(0);
		
		billSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				billIdArrayPosition = position;
				filltable(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	public void filltable(int position){
		billTable.removeAllViews();
		
		if(idarray.length == 0){
			prevNext.setVisibility(View.GONE);
			editLayout.setVisibility(View.GONE);
			return;
		}
		
		int tempid = idarray[position];
		Cursor billQuery = pdb.getBillDetails(tempid);
		billQuery.moveToFirst();
		String name = billQuery.getString(1);
		float amount = billQuery.getFloat(2);
		long timeinmillis = billQuery.getLong(3);
		billQuery.close();
		
		String dt = "";
		if(timeinmillis == 0){
			dt += "";
		}else{
			dt += DateUtils.formatDateTime(this, timeinmillis, DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
		}
		
		//Previous Next Button Visibility
		View prev = (View)prevNext.findViewById(R.id.billPreviousButton);
		View next = (View)prevNext.findViewById(R.id.billNextButton);
		
		if(idarray.length == 1){
			prevNext.setVisibility(View.GONE);
		}
		else{
			prevNext.setVisibility(View.VISIBLE);
			
			if(billIdArrayPosition == 0){
				prev.setVisibility(View.INVISIBLE);
			}
			else{
				prev.setVisibility(View.VISIBLE);
			}
			
			int lastEventPosition = idarray.length - 1;
			
			if(billIdArrayPosition == lastEventPosition){
				next.setVisibility(View.INVISIBLE);
			}
			else{
				next.setVisibility(View.VISIBLE);
			}
		}
		
		String amountString = String.format(decimalFlag, amount);
		addEntry(name,amountString,dt);			
	}

	public void addEntry(String name,String amount,String date){
		View convertView = inflater.inflate(R.layout.table_item, null);
		TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
		TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
		TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
		v1.setText(name);
		v2.setText(amount);
		v3.setText(date);
		
		MainActivity.setWeight(v1,1f);
		MainActivity.setWeight(v2,1f);
		MainActivity.setWeight(v3,1.5f);
		
		billTable.addView(convertView);
	}
	
	public void previousBill(View v) {
		billSpin.setSelection(billIdArrayPosition - 1);
	}
	
	public void nextBill(View v) {
		billSpin.setSelection(billIdArrayPosition + 1);
	}
	
	public void payBill(View v) {
		// TODO Go to Pay bill page
	}
	
	public void deleteBill(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Bill");
		alertDialogBuilder
		.setMessage("Deleting an bill will change the overall balance. Are you sure you want to delete this bill?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteBillFromDatabase(billIdArrayPosition);
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
	
	public void deleteBillFromDatabase(int position) {
		int id = idarray[position];

		try {
			pdb.deleteBill(id);
		} catch(Exception e){}
		
		fillBills();
		billSpin.setSelection((billIdArrayPosition>0)?(billIdArrayPosition-1):billIdArrayPosition);
	}
	
//	public void editEvent(View v) {
//		try{
//			int tempid = idarray[eventIdArrayPosition];
//			int tempFlag = flagarray[eventIdArrayPosition];
//			String tempName = listofevents.get(eventIdArrayPosition);
//			
//			if(tempFlag == GroupDatabase.eventFlag){
//				Intent intent = new Intent(this, EditEventActivity.class);
//				intent.putExtra(GroupsActivity.GROUP_ID, grpid);
//				intent.putExtra(GroupSummaryActivity.listofmember, namearray);
//				intent.putExtra(GroupSummaryActivity.stringDecimals, currencyDecimals);
//				intent.putExtra(EVENT_ID, tempid);
//				intent.putExtra(EVENT_NAME, tempName);
//				startActivity(intent);
//			}else if(tempFlag == GroupDatabase.cashTransferFlag){
//				Intent intent = new Intent(this, EditCashTransferActivity.class);
//				intent.putExtra(GroupsActivity.GROUP_ID, grpid);
//				intent.putExtra(GroupSummaryActivity.listofmember, namearray);
//				intent.putExtra(GroupSummaryActivity.stringDecimals, currencyDecimals);
//				intent.putExtra(EVENT_ID, tempid);
//				intent.putExtra(EVENT_NAME, tempName);
//				startActivity(intent);
//			}
//		}catch(Exception e){
//			Log.e("Sameer","Here",e);
//		}
//	}
//	
//	public void deleteEvent(View v) {
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//		alertDialogBuilder.setTitle("Delete Event");
//		alertDialogBuilder
//		.setMessage("Deleting an event will change the balance of the involved members. Are you sure you want to delete this event?")
//		.setCancelable(true)
//		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				deleteEventFromDatabase(eventIdArrayPosition);
//			}
//		})
//		.setNegativeButton("No",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//			}
//		});
//		AlertDialog alertDialog = alertDialogBuilder.create();
//		alertDialog.show();
//	}
//	
//	public void restoreEvent(View v) {
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//		alertDialogBuilder.setTitle("Restore Deleted Event");
//		alertDialogBuilder
//		.setMessage("Restoring a deleted event will change the balance of the involved members. Are you sure you want to restore this event?")
//		.setCancelable(true)
//		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				restoreDeletedEventInDatabase(eventIdArrayPosition);
//			}
//		})
//		.setNegativeButton("No",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//			}
//		});
//		AlertDialog alertDialog = alertDialogBuilder.create();
//		alertDialog.show();
//	}
//
//	public void clearAlert(View v) {
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//		alertDialogBuilder.setTitle("Clear History");
//		alertDialogBuilder
//		.setMessage("Clearing the history will delete all the events from history but will not affect the balance of any member. Are you sure you want to continue?")
//		.setCancelable(true)
//		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				clearlog();
//			}
//		})
//		.setNegativeButton("No",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//			}
//		});
//		AlertDialog alertDialog = alertDialogBuilder.create();
//		alertDialog.show();
//	}
//
//	public void clearlog(){
//		gpdb.clearlog();
//		this.finish();
//	}
//	
//	public void deleteEventFromDatabase(int position) {
//		int tempid = idarray[position];
//		int tempflag = flagarray[position];
//
//		try{
//			if(tempflag==GroupDatabase.eventFlag){
//				gpdb.DeleteFromTransList(tempid);
//			}
//			else if(tempflag==GroupDatabase.cashTransferFlag){
//				gpdb.DeleteFromCashList(tempid);
//			}
//		}catch(Exception e){}
//		
//		fillEvents(memberId);
//		eventSpin.setSelection(eventIdArrayPosition);
//	}
//	
//	public void restoreDeletedEventInDatabase(int position) {
//		int tempid = idarray[position];
//		int tempflag = flagarray[position];
//
//		try{
//			if(tempflag==GroupDatabase.deletedEventFlag){
//				gpdb.restoreInTransList(tempid);
//			}
//			else if(tempflag==GroupDatabase.deletedCashTransferFlag){
//				gpdb.restoreInCashList(tempid);
//			}
//		}catch(Exception e){}
//		
//		fillEvents(memberId);
//		eventSpin.setSelection(eventIdArrayPosition);
//	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}


