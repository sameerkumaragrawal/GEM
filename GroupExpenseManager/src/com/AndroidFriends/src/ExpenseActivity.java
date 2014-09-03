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

public class ExpenseActivity extends Activity {

	public static final String EXPENSE_ID= "expenseActivity/expenseid";
	public static final String EXPENSE_NAME= "expenseActivity/expensename";
	
	private int currencyDecimals = 2;
	private int expenseIdArrayPosition = 0, categoryId = 0;
	private Spinner expenseSpin, categorySpin;
	private List<String> listofexpenses = null, listofcategories = null;
	private int[] idarray = null;
	private LinearLayout expenseTable = null;
	private Button restoreButton, clearButton, expenseStatsButton;
	private LinearLayout editLayout, prevNext;
	private LayoutInflater inflater;
	private PersonalDatabase pdb;
	private String decimalFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.activity_expenses);

		pdb=PersonalDatabase.get(this);
		inflater = LayoutInflater.from(this);

		currencyDecimals = intent.getIntExtra(PersonalActivity.personalCurrencyDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		expenseTable = (LinearLayout) findViewById(R.id.ExpenseTable);
		prevNext = (LinearLayout)findViewById(R.id.expensePreviousNextLayout);
		editLayout = (LinearLayout) findViewById(R.id.expenseEditLayout);
		restoreButton = (Button) findViewById(R.id.expenseRestoreButton);
		clearButton = (Button) findViewById(R.id.clearExpenseHistoryButton);
		expenseStatsButton = (Button) findViewById(R.id.expenseStatsButton);
		
		categoryList();
		addItemsOnCategorySpinner();
		
		categorySpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				categoryId = position;
				fillExpenses(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		fillExpenses(categoryId);
		expenseSpin.setSelection(expenseIdArrayPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_expenses, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case id.expense_stats:
			openExpenseStats(null);
			return true;
		case id.menu_clear_history:
			clearHistoryAlert(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void categoryList() {
		ArrayList<String> categoryList = pdb.getCategoryNames();
		listofcategories = new ArrayList<String>();
		listofcategories.add("All");
		for (int i=0; i<categoryList.size(); i++) {
			listofcategories.add(categoryList.get(i));
		}
	}
	
	public void addItemsOnCategorySpinner() {
		categorySpin = (Spinner) findViewById(R.id.categoryDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofcategories);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpin.setAdapter(dataAdapter);
		categorySpin.setPrompt("Select category");
	}

	public void fillExpenses(int category) {
		expenseList(category);
		addItemsOnExpenseSpinner();
		filltable(0);
		
		expenseSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				expenseIdArrayPosition = position;
				filltable(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void addItemsOnExpenseSpinner() {
		expenseSpin = (Spinner) findViewById(R.id.expenseDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofexpenses);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expenseSpin.setAdapter(dataAdapter);
		expenseSpin.setPrompt("Select expense");
	}
	
	public void expenseList(int category){
		listofexpenses = new ArrayList<String>();
		try{
			Cursor mquery = pdb.getExpenseList(category);
			idarray=new int[mquery.getCount()];
			int temp=0;
			mquery.moveToFirst();
			do{
				listofexpenses.add(mquery.getString(1));
				idarray[temp]=mquery.getInt(0);
				temp++;
			}while(mquery.moveToNext());
		}catch(Exception e){}
	}
	
	public void filltable(int position){
		expenseTable.removeAllViews();
		TextView dtTextView = (TextView) findViewById(R.id.expenseDateTimeText);
		
		if(idarray.length == 0){
			prevNext.setVisibility(View.GONE);
			editLayout.setVisibility(View.GONE);
			restoreButton.setVisibility(View.GONE);
			clearButton.setVisibility(View.GONE);
			expenseStatsButton.setVisibility(View.GONE);
			dtTextView.setVisibility(View.GONE);
			return;
		}
		
		int tempid = idarray[position];
		Cursor expenseQuery = pdb.getExpenseDetails(tempid);
		expenseQuery.moveToFirst();
		String name = expenseQuery.getString(1);
		int category = expenseQuery.getInt(2);
		float amount = expenseQuery.getFloat(3);
		long timeinmillis = expenseQuery.getLong(4);
		int expenseFlag = expenseQuery.getInt(5);
		expenseQuery.close();
		
		dtTextView.setVisibility(View.VISIBLE);
		String dt = "Time of expense : ";
		if(timeinmillis == 0){
			dt += "Not Available";
		}else{
			dt += DateUtils.formatDateTime(this, timeinmillis, DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
		}
		dtTextView.setText(dt);
		
		//Previous Next Button Visibility
		View prev = (View)prevNext.findViewById(R.id.expensePreviousButton);
		View next = (View)prevNext.findViewById(R.id.expenseNextButton);
		
		if(idarray.length == 1){
			prevNext.setVisibility(View.GONE);
		}
		else{
			prevNext.setVisibility(View.VISIBLE);
			
			if(expenseIdArrayPosition == 0){
				prev.setVisibility(View.INVISIBLE);
			}
			else{
				prev.setVisibility(View.VISIBLE);
			}
			
			int lastEventPosition = idarray.length - 1;
			if(expenseIdArrayPosition == lastEventPosition){
				next.setVisibility(View.INVISIBLE);
			}
			else{
				next.setVisibility(View.VISIBLE);
			}
		}
		
		//Other buttons visibility
		if (expenseFlag == PersonalDatabase.clearedExpenseFlag) {
			editLayout.setVisibility(View.GONE);
			restoreButton.setVisibility(View.GONE);
			clearButton.setVisibility(View.GONE);
			expenseStatsButton.setVisibility(View.GONE);
		}
		else if (expenseFlag == PersonalDatabase.deletedExpenseFlag){
			editLayout.setVisibility(View.GONE);
			restoreButton.setVisibility(View.VISIBLE);
			clearButton.setVisibility(View.VISIBLE);
			expenseStatsButton.setVisibility(View.VISIBLE);
		}
		else {
			editLayout.setVisibility(View.VISIBLE);
			restoreButton.setVisibility(View.GONE);
			clearButton.setVisibility(View.VISIBLE);
			expenseStatsButton.setVisibility(View.VISIBLE);
		}
		
		String categoryString = pdb.getCategoryName(category);
		String amountString = String.format(decimalFlag, amount);
		addEntry(categoryString,name,amountString);			
	}

	@SuppressLint("InflateParams")
	public void addEntry(String category, String name, String amount){
		View convertView = inflater.inflate(R.layout.table_item, null);
		TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
		TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
		TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
		v1.setText(name);
		v2.setText(category);
		v3.setText(amount);
		
		MainActivity.setWeight(v1,1f);
		MainActivity.setWeight(v2,1f);
		MainActivity.setWeight(v3,0.8f);
		
		expenseTable.addView(convertView);
	}
	
	public void previousExpense(View v) {
		expenseSpin.setSelection(expenseIdArrayPosition - 1);
	}
	
	public void nextExpense(View v) {
		expenseSpin.setSelection(expenseIdArrayPosition + 1);
	}
	
	public void editExpense(View v) {
		try{
			int tempid = idarray[expenseIdArrayPosition];
			String tempName = listofexpenses.get(expenseIdArrayPosition);
			
			Intent intent = new Intent(this, EditExpenseActivity.class);
			intent.putExtra(GroupSummaryActivity.stringDecimals, currencyDecimals);
			intent.putExtra(EXPENSE_ID, tempid);
			intent.putExtra(EXPENSE_NAME, tempName);
			startActivity(intent);
		}catch(Exception e){}
	}
	
	public void deleteExpense(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Expense");
		alertDialogBuilder
		.setMessage("Deleting an expense will change the overall balance. Are you sure you want to delete this expense?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteExpenseFromDatabase(expenseIdArrayPosition);
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
	
	public void deleteExpenseFromDatabase(int position) {
		int id = idarray[position];

		try {
			pdb.deleteExpense(id);
		} catch(Exception e){}
		
		fillExpenses(categoryId);
		expenseSpin.setSelection(expenseIdArrayPosition);
	}

	public void restoreExpense(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Restore Deleted Expense");
		alertDialogBuilder
		.setMessage("Restoring a deleted expense will change the overall balance. Are you sure you want to restore this expense?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				restoreExpenseInDatabase(expenseIdArrayPosition);
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
	
	public void restoreExpenseInDatabase(int position) {
		int id = idarray[position];

		try {
			pdb.restoreExpense(id);
		} catch(Exception e){}
		
		fillExpenses(categoryId);
		expenseSpin.setSelection(expenseIdArrayPosition);
	}
	
	public void openExpenseStats(View v) {
		Intent intent = new Intent(this, ExpensesStatsActivity.class);
		startActivity(intent);
	}
	
	public void clearHistoryAlert(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Clear Expense History");
		alertDialogBuilder
		.setMessage("Clearing the expense history will delete all the expenses as well as set the total expenses to zero. Are you sure you want to continue?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				clearExpenseHistory();
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

	public void clearExpenseHistory(){
		pdb.clearExpenses();
		this.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}


