package com.AndroidFriends.src;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
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
	private Spinner spin1, spin2;
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
		
		addItemsOnSpinner(spin1);
		addItemsOnSpinner(spin2);
		getWindow().setSoftInputMode(
				   WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cash_transfer, menu);
		return true;
	}

	public void addItemsOnSpinner(Spinner spin) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, namearray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(dataAdapter);
	}

	public void createToast(String message){
		Toast n = Toast.makeText(CashTransferActivity.this,message, Toast.LENGTH_SHORT);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	public void transferDone(View v) {
		int fM = spin1.getSelectedItemPosition()+1;
		int tM = spin2.getSelectedItemPosition()+1;
		EditText editText;
		editText = (EditText) findViewById(R.id.cashTransferamountText);
		String temp = editText.getText().toString();
		if (temp.equals("")){
			createToast("Error! Cannot leave the amount empty");
			return;
		}
		if(fM==tM){
			createToast("Error! To and From person cannot be the same");
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
