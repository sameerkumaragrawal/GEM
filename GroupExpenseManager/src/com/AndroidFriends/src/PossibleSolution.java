package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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

public class PossibleSolution extends Activity {

	private String[] namearray;
	private float[] balancearray;
	private int[] idarray;
	private int countmembers;
	private String[][] solutionarray;
	private String groupName="";
	private int ntransactions = 0;
	private GroupSummaryActivity summaryobject = new GroupSummaryActivity();
	private float[] tramountarray;
	private int grpid = 0;
	private GroupDatabase gpdb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		idarray = intent.getIntArrayExtra(GroupSummaryActivity.listofid);
		balancearray = intent.getFloatArrayExtra(GroupSummaryActivity.listofbalance);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		countmembers = intent.getIntExtra(GroupSummaryActivity.stringcount, 0);
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		compute();
		boolean clearflag=intent.getBooleanExtra(GroupSummaryActivity.clearflag, false);
		if(clearflag){
			grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
			clearbalance();
			this.finish();
		}
		setContentView(R.layout.activity_possible_solution);
		filltable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_possible_solution, menu);
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
		TableLayout tl = (TableLayout)findViewById(R.id.PossibleSolutionTable);
		for(int i=0;i<ntransactions;i++){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			TextView v1= new TextView(this);
			v1.setText(solutionarray[i][0]);
			TextView v2= new TextView(this);
			v2.setText(solutionarray[i][2]);
			TextView v3= new TextView(this);
			v3.setText(solutionarray[i][4]);
			
			v1.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
			v2.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
			v3.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
			
			v1.setTextColor(Color.parseColor("#FFFFFF"));
			v2.setTextColor(Color.parseColor("#FFFFFF"));
			v3.setTextColor(Color.parseColor("#FFFFFF"));
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
	public void compute(){
		int minindex, maxindex;
		float net, tramount;
		tramountarray = new float[countmembers];
		solutionarray = new String[countmembers][5];
		ntransactions = 0;
		while(true){
			minindex = 0;
			maxindex = 0;
			net = 0;
			tramount = 0;
			for(int i=1;i<countmembers;i++){
				if(balancearray[i]<balancearray[minindex]){
					minindex=i;
				}
				else if(balancearray[i]>balancearray[maxindex]){
					maxindex=i;
				}
			}
			if(balancearray[minindex]==0 || balancearray[maxindex]==0){
				break;
			}

			net = balancearray[minindex] + balancearray[maxindex];
			if(net>0){
				tramount = - balancearray[minindex];
				balancearray[minindex]=0;
				balancearray[maxindex]=net;
				solutionarray[ntransactions][4] = summaryobject.floatToString(tramount); 
			}
			else{
				tramount = balancearray[maxindex];
				balancearray[minindex]=net;
				balancearray[maxindex]=0;
				solutionarray[ntransactions][4] = summaryobject.floatToString(tramount); 
			}
			tramountarray[ntransactions]=tramount;
			solutionarray[ntransactions][0] = namearray[minindex];
			solutionarray[ntransactions][1] = String.valueOf(idarray[minindex]);
			solutionarray[ntransactions][2] = namearray[maxindex];
			solutionarray[ntransactions][3] = String.valueOf(idarray[maxindex]);
			ntransactions++;
		}
	}

	public void clearbalance(){
		gpdb.clearBalance(ntransactions, solutionarray, tramountarray);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
