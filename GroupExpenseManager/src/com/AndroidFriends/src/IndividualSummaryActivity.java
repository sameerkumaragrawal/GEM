package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.AndroidFriends.R;

public class IndividualSummaryActivity extends Activity {
	private String[] namearray;
	private float[] paidarray;
	private float[] consumedarray;
	private int countmembers;
	private int currencyDecimals = 2;
	private LayoutInflater inflater;
	private String groupName="";
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
		inflater = LayoutInflater.from(this);
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
			View convertView = inflater.inflate(R.layout.table_item, null);
			TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
			TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
			TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
			v1.setText(namearray[i]);
			v2.setText(String.format(decimalFlag, consumedarray[i]));
			v3.setText(String.format(decimalFlag, paidarray[i]));
			
			MainActivity.setWeight(v1,1.1f);
			MainActivity.setWeight(v2,1.2f);
			MainActivity.setWeight(v3,0.8f);
			
			tl.addView(convertView);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
