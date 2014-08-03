package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.AndroidFriends.R;
import com.AndroidFriends.R.id;

public class HistoryActivity extends Activity {

	private String grpName = "";
	private int grpid = 0;
	private String[] namearray=null;
	private Spinner spin1=null;
	private List<String> listofevents = null;
	private int[] idarray = null;
	private int[] flagarray = null;
	private TableLayout historytable = null;
	private GroupSummaryActivity summaryobject = new GroupSummaryActivity();
	private LinearLayout historytablerow1,historytablerow2;
	private GroupDatabase gpdb;

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

		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		historytable = (TableLayout) findViewById(R.id.HistoryTable);
		historytablerow1 = (LinearLayout) findViewById(R.id.historyrow1);
		historytablerow2 = (LinearLayout) findViewById(R.id.historyrow2);
		EventList();
		addItemsOnSpinner();
		if(idarray.length>0){
			filltable(0);
		}
		spin1.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
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

	public void EventList(){
		listofevents = new ArrayList<String>();
		try{
			Cursor mquery = gpdb.eventList();
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

	public void filltable(int position){
		historytable.removeAllViews();
		int tempid = idarray[position];
		int tempflag = flagarray[position];

		String str1,str2,str3;
		float paid,consumed;
		try{
			if(tempflag==1){
				historytablerow2.setVisibility(View.GONE);
				historytablerow1.setVisibility(View.VISIBLE);
				Cursor mquery = gpdb.TransList(tempid);
				mquery.moveToFirst();
				do{
					str1=namearray[mquery.getInt(0)-1];
					paid= mquery.getFloat(1);
					consumed = mquery.getFloat(2);
					str2=(consumed >0) ? summaryobject.floatToString(consumed) : null;
					str3=(paid >0) ? summaryobject.floatToString(paid) : null;
					addEntry(str1,str2,str3);
				}while(mquery.moveToNext());
			}
			else if(tempflag==2){
				historytablerow1.setVisibility(View.GONE);
				historytablerow2.setVisibility(View.VISIBLE);
				Cursor mquery = gpdb.CashList(tempid);
				mquery.moveToFirst();
				do{
					str1=namearray[mquery.getInt(0)-1];
					paid= mquery.getFloat(2);
					str2=namearray[mquery.getInt(1)-1];
					str3=summaryobject.floatToString(paid);
					addEntry(str1,str2,str3);
				}while(mquery.moveToNext());
			}
		}catch(Exception e){}
			
	}

	public void addEntry(String str1,String str2,String str3){
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView v1= new TextView(this);
		TextView v2= new TextView(this);
		TextView v3= new TextView(this);
		v1.setText(str1);
		v2.setText(str2);
		v3.setText(str3);
		v1.setTextColor(Color.parseColor("#FFFFFF"));
		v2.setTextColor(Color.parseColor("#FFFFFF"));
		v3.setTextColor(Color.parseColor("#FFFFFF"));
		v1.setWidth(195);
		v1.setPadding(30, 10, 10, 5);
		v2.setWidth(150);
		v2.setPadding(5, 10, 10, 5);
		v3.setWidth(137);
		v3.setPadding(5, 10, 10, 10);

		tr.addView(v1);
		tr.addView(v2);
		tr.addView(v3);
		historytable.addView(tr);
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

}
