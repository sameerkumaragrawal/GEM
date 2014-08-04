package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private Button btnDone;
	private List<String> list = new ArrayList<String>();
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
		setContentView(R.layout.activity_cash_transfer_mod);
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		MemberList();
		addItemsOnSpinner1();
		addItemsOnSpinner2();
		addListenerOnButton();
		getWindow().setSoftInputMode(
				   WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cash_transfer, menu);
		return true;
	}

	public void MemberList(){
		for(int i=0;i<namearray.length;i++){
			list.add(namearray[i]);
		}
	}

	public void addItemsOnSpinner1() {

		spin1 = (Spinner) findViewById(R.id.cashTransferspinner1);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin1.setAdapter(dataAdapter);
	}

	public void addItemsOnSpinner2() {

		spin2 = (Spinner) findViewById(R.id.cashTransferspinner2);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin2.setAdapter(dataAdapter);
	}

	public void addListenerOnButton() {
		btnDone = (Button) findViewById(R.id.cashTransferDoneButton);
		btnDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				transferDone();
			}
		});
	}
	
	public void transferDone() {
		int fM = spin1.getSelectedItemPosition()+1;
		int tM = spin2.getSelectedItemPosition()+1;
		EditText editText;
		editText = (EditText) findViewById(R.id.cashTransferamountText);
		String temp = editText.getText().toString();
		if (temp.equals("")){
			Toast n = Toast.makeText(CashTransferActivity.this,"Error! Cannot leave the amount empty", Toast.LENGTH_SHORT);
			n.setGravity(Gravity.CENTER_VERTICAL,0,0);
			n.show();
			return;
		}
		float a = Float.valueOf(temp);
		gpdb.CashTransfer(fM, tM, a);
		this.finish();	
	}
}
