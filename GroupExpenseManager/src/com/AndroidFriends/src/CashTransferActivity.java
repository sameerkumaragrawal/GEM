package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.AndroidFriends.R;

@TargetApi(11)
public class CashTransferActivity extends Activity {
	private String grpName = "";
	private int grpid = 0;
	private String[] namearray;
	private Spinner spin1, spin2;
	private Button btnDone;
	private List<String> list = new ArrayList<String>();

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

	public void CashTransfer(int fromMember, int toMember, float amount){

		SQLiteDatabase groupDb=null;
		String database="Database_"+grpid;
		int ID1=1;
		try{
			groupDb = this.openOrCreateDatabase(database, MODE_PRIVATE, null);
			Cursor count = groupDb.rawQuery("SELECT count(*) FROM " + MainActivity.EventTable , null);
	        if(count.getCount()>0){
	        	count.moveToLast();
	        	ID1=count.getInt(0)+1;
	        }
	        groupDb.execSQL("INSERT INTO " + MainActivity.EventTable + " ( ID, Name, Flag ) VALUES ( '" + ID1+"', 'Cash Transfer', '2' );" );
	        groupDb.execSQL("INSERT INTO "+ MainActivity.CashTable + " ( ID, FromMemberId, ToMemberId, Amount ) VALUES ( '"+ID1+"', '"+fromMember+"', '"+toMember+"', '"+amount+"');" );
	        //groupDb.execSQL("INSERT INTO "+ MainActivity.TransTable + " ( MemberId, Paid, Consumed, EventId ) VALUES ( '"+toMember+"', '0', '"+amount+"', '"+ID1+"' );" );
	        
	        groupDb.execSQL("UPDATE "+MainActivity.MemberTable+" SET Balance = Balance+'"+amount+"' WHERE ID = '"+fromMember+"';");
			groupDb.execSQL("UPDATE "+MainActivity.MemberTable+" SET Balance = Balance-'"+amount+"' WHERE ID = '"+toMember+"';");
		}catch(Exception e) {
			Log.e("Error", "Error", e);
		}
		finally{ 
			if(groupDb!=null)
				groupDb.close();
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
		float a = Float.valueOf(editText.getText().toString());
		CashTransfer(fM, tM, a);
		this.finish();	
	}
}
