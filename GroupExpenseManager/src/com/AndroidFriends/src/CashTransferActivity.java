package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

@TargetApi(11)
public class CashTransferActivity extends Activity {
	private String grpName = "";
	private int grpid = 0;
	private String[] namearray;
	private ArrayList<String> spin2Array;
	private Spinner spin1, spin2;
	private int spin1Position=0;
	private GroupDatabase gpdb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		setContentView(R.layout.activity_cash_transfer);
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		
		spin1 = (Spinner) findViewById(R.id.cashTransferspinner1);
		spin2 = (Spinner) findViewById(R.id.cashTransferspinner2);
		
		fillSpinner2();
		addItemsOnSpinner1();
		addItemsOnSpinner2();
		
		spin1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				spin1Position = position;
				fillSpinner2();
				addItemsOnSpinner2();
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
				
		getWindow().setSoftInputMode(
				   WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cash_transfer, menu);
		return true;
	}
	
	private void fillSpinner2() {
		spin2Array = new ArrayList<String>();
		spin2Array.add("Select Member");
		for (int i=0; i<namearray.length; i++) {
			if (i!=spin1Position) spin2Array.add(namearray[i]);
		}
	}
	
	public void addItemsOnSpinner1() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, (String[]) namearray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin1.setAdapter(dataAdapter);
	}
	
	public void addItemsOnSpinner2() {
		String[] spin2StringArray = new String[spin2Array.size()];
		spin2StringArray = spin2Array.toArray(spin2StringArray);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, (String[]) spin2StringArray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin2.setAdapter(dataAdapter);
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(CashTransferActivity.this,message, Toast.LENGTH_SHORT);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	public void transferDone(View v) {
		int fM = spin1.getSelectedItemPosition()+1;
		int tM = spin2.getSelectedItemPosition();
		if (tM >= fM) {
			tM += 1;
		}
		EditText editText;
		editText = (EditText) findViewById(R.id.cashTransferamountText);
		String temp = editText.getText().toString();
		if (temp.equals("")){
			createToast("Error! Cannot leave the amount empty");
			return;
		}
		if(tM==0){
			createToast("Error! To person cannot be empty");
			return;
		}
		
		float a = Float.valueOf(temp);
		gpdb.CashTransfer(fM, tM, a, namearray[fM-1], namearray[tM-1]);
		this.finish();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
