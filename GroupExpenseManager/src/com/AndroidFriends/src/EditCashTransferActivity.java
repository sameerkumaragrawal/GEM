package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
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
public class EditCashTransferActivity extends Activity {
	private String eventname ="";
	private int grpid = 0, eventid = 0;
	private String[] namearray;
	private Spinner spin1, spin2;
	private ArrayList<String> spin2Array;
	private int spin1Position, spin2Position;
	private boolean initial = true;
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
		editText = (EditText) findViewById(R.id.cashTransferamountText);

		Cursor mquery = gpdb.CashList(eventid);
		mquery.moveToFirst();
		spin1Position = mquery.getInt(0)-1;
		fillSpinner2();
		addItemsOnSpinner1();
		addItemsOnSpinner2();

		spin1.setSelection(spin1Position);
		
		spin2Position = mquery.getInt(1)-1;
		if (spin2Position > spin1Position) {
			spin2Position -= 1;
		}
		spin2.setSelection(spin2Position);
		float paid= mquery.getFloat(2);
		editText.setText(String.format(decimalFlag, paid));

		spin1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				if (initial) initial = false;
				else {
					spin1Position = position;
					fillSpinner2();
					addItemsOnSpinner2();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private void fillSpinner2() {
		spin2Array = new ArrayList<String>();
		if (!initial) spin2Array.add("Select Member");
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
		Toast n = Toast.makeText(EditCashTransferActivity.this,message, Toast.LENGTH_SHORT);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}

	public void transferDone(View v) {
		boolean selectPresent = spin2Array.contains("Select Member");
		
		int fM = spin1.getSelectedItemPosition()+1;
		int tM = spin2.getSelectedItemPosition();
		if (!selectPresent) tM += 1;
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
		if(selectPresent && tM==0){
			createToast("Error! To person cannot be empty");
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
