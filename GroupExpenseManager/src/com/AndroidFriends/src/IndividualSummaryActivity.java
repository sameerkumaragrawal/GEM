package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.AndroidFriends.R;

public class IndividualSummaryActivity extends Activity {
	private String[] namearray;
	private float[] paidarray;
	private float[] consumedarray;
	private int countmembers;
	private int currencyDecimals = 2;
	private String groupName="";
	private GroupSummaryActivity summaryobject = new GroupSummaryActivity();
	private String decimalFlag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		paidarray = intent.getFloatArrayExtra(GroupSummaryActivity.listofpaid);
		consumedarray = intent.getFloatArrayExtra(GroupSummaryActivity.listofconsumed);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		countmembers = intent.getIntExtra(GroupSummaryActivity.stringcount, 0);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		setContentView(R.layout.activity_individual_summary);
		
		filltable();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_individual_summary, menu);
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

	public void filltable(){
		TableLayout tl = (TableLayout)findViewById(R.id.IndividualSummaryTable);
		for(int i=0;i<countmembers;i++){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			TextView v1= new TextView(this);
			v1.setText(namearray[i]);
			TextView v2= new TextView(this);
			v2.setText(String.format(decimalFlag, consumedarray[i]));
			TextView v3= new TextView(this);
			v3.setText(String.format(decimalFlag, paidarray[i]));
			
			v1.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.1f));
			v2.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.2f));
			v3.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.8f));
			
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
