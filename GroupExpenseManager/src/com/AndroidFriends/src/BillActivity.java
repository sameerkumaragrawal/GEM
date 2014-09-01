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
	private Button payBillButton;
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

		currencyDecimals = intent.getIntExtra(PersonalActivity.personalCurrencyDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		billTable = (LinearLayout) findViewById(R.id.billTable);
		prevNext = (LinearLayout)findViewById(R.id.billPreviousNextLayout);
		editLayout = (LinearLayout) findViewById(R.id.billEditLayout);
		payBillButton = (Button) findViewById(R.id.billPayButton);
		
		fillBills();
		
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
	}
	
	public void filltable(int position){
		billTable.removeAllViews();
		
		if(idarray.length == 0){
			prevNext.setVisibility(View.GONE);
			editLayout.setVisibility(View.GONE);
			payBillButton.setVisibility(View.GONE);
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

	@SuppressLint("InflateParams")
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
		MainActivity.setWeight(v3,1f);
		
		billTable.addView(convertView);
	}
	
	public void previousBill(View v) {
		billSpin.setSelection(billIdArrayPosition - 1);
	}
	
	public void nextBill(View v) {
		billSpin.setSelection(billIdArrayPosition + 1);
	}
	
	public void payBill(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Pay Bill");
		alertDialogBuilder
		.setMessage("Paying a bill will automatically add it to your expenses. Are you sure you want to pay this bill?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				payBillToExpenses(billIdArrayPosition);
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
	
	public void payBillToExpenses(int position) {
		int id = idarray[position];

		try {
			pdb.payBill(id);
		} catch(Exception e){
			Log.e("nik","Here",e);
		}
		
		fillBills();
		billSpin.setSelection((billIdArrayPosition>0)?(billIdArrayPosition-1):billIdArrayPosition);
	}
	
	public void editBill(View v) {
		try{
			int tempid = idarray[billIdArrayPosition];
			String tempName = listofbills.get(billIdArrayPosition);
			
			Intent intent = new Intent(this, EditBillActivity.class);
			intent.putExtra(PersonalActivity.personalCurrencyDecimals, currencyDecimals);
			intent.putExtra(BILL_ID, tempid);
			intent.putExtra(BILL_NAME, tempName);
			startActivity(intent);
		}catch(Exception e){}
	}
	
	public void deleteBill(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Bill");
		alertDialogBuilder
		.setMessage("The bill will be deleted from the list. Are you sure you want to continue?")
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
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}


