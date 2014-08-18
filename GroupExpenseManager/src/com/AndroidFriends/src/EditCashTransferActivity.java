package com.AndroidFriends.src;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

@TargetApi(11)
public class EditCashTransferActivity extends Activity {
	private String eventname ="";
	private int grpid = 0, eventid = 0;
	private String[] namearray;
	private Spinner spin1, spin2;
	private EditText editText;
	private GroupDatabase gpdb;
	private int currencyDecimals = 2;
	private String decimalFlag;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		eventname = intent.getStringExtra(HistoryActivity.EVENT_NAME);

		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		eventid = intent.getIntExtra(HistoryActivity.EVENT_ID,0);

		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";

		String new_title= eventname+" - Edit";
		this.setTitle(new_title);
		setContentView(R.layout.activity_cash_transfer);

		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);

		spin1 = (Spinner) findViewById(R.id.cashTransferspinner1);
		spin2 = (Spinner) findViewById(R.id.cashTransferspinner2);
		addItemsOnSpinner(spin1);
		addItemsOnSpinner(spin2);

		editText = (EditText) findViewById(R.id.cashTransferamountText);

		Cursor mquery = gpdb.CashList(eventid);
		mquery.moveToFirst();
		spin1.setSelection(mquery.getInt(0)-1);
		spin2.setSelection(mquery.getInt(1)-1);
		float paid= mquery.getFloat(2);
		editText.setText(String.format(decimalFlag, paid));

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	public void addItemsOnSpinner(Spinner spin) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, namearray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(dataAdapter);
	}

	public void createToast(String message){
		Toast n = Toast.makeText(EditCashTransferActivity.this,message, Toast.LENGTH_SHORT);
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
			createToast("Error! To and From person cannot be same");
			return;
		}

		float a = Float.valueOf(temp);
		gpdb.updateCashTransfer(eventid, fM, tM, a, namearray[fM-1], namearray[tM-1]);
		this.finish();	
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
