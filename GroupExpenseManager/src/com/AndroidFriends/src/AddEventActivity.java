package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

public class AddEventActivity extends Activity {

	private int nspinners = 0;
	private int ndialogs = 0;
	private String grpName = "";
	private int grpid = 0;
	private String[] namearray =null;
	private LayoutInflater inflater;

	private LinearLayout plist;

	private LinearLayout slist;

	AlertDialog.Builder alertDialogBuilder;

	/*
	private List<String> list = new ArrayList<String>();
	 */
	private List<boolean[]> checkedItems = null;
	private boolean[] tempCheckedItems = null;

	private CharSequence[] items;
	/*
	private TableLayout AddEventTable1 = null;
	private TableLayout AddEventTable2 = null;
	 */

	private int popupsize = 0;
	/*
	public float scale = 0;
	private int number1 = 0;
	private int number2 = 0;
	private int sharedialogid = -1;
	 */
	private GroupDatabase gpdb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent  intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		String database="Database_"+grpid;
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_add_event);

		gpdb=GroupDatabase.get(this, database);

		inflater = LayoutInflater.from(this);
		plist = (LinearLayout) findViewById(R.id.addEventModLinearLayout1);
		slist = (LinearLayout) findViewById(R.id.addEventModLinearLayout2);

		checkedItems = new ArrayList<boolean[]>();
		popupsize = namearray.length+1;
		tempCheckedItems = new boolean[popupsize];

		MemberList();

		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
		.setTitle("Share Among")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		addMember1(null);
		addMember2(null);
		
		EditText event = (EditText) findViewById(R.id.AddEventModEventName);
		event.requestFocus();
	}

	public void MemberList(){
		items = new CharSequence [popupsize];
		items[0]="All";
		for (int j=0; j<namearray.length; j++) {
			items[j+1] = namearray[j];
		}        
	}

	public void addMember1(View v) {
		plist.addView(getPaidView());
		nspinners++;
	}

	public View getPaidView(){
		View convertView = inflater.inflate(R.layout.add_event_paid_item, null);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, namearray); 
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spin = (Spinner)convertView.findViewById(R.id.spinnerAddEventMod);
		spin.setAdapter(dataAdapter);

		ImageButton ib = (ImageButton)convertView.findViewById(R.id.add_event_paid_item_ib);
		CustomRemoveListener1 removeListener = new CustomRemoveListener1();
		removeListener.setPosition(nspinners);
		ib.setOnClickListener(removeListener);

		return convertView;
	}

	private class CustomRemoveListener1 implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}
		public void onClick(View v) {
			plist.removeViewAt(position);
			nspinners--;
		}

	}

	public void addMember2(View v) {
		boolean[] temparray = new boolean[popupsize];
		for(int i=0;i<popupsize;i++){
			temparray[i]=false;
		}
		checkedItems.add(temparray);
		slist.addView(getConsumedView());
		ndialogs++;
	}

	public View getConsumedView(){
		View convertView = inflater.inflate(R.layout.add_event_consumed_item, null);

		Button shareb = (Button)convertView.findViewById(R.id.shareButtonAddEventMod);
		CustomClickListener clickListener = new CustomClickListener();
		clickListener.setPosition(ndialogs);
		shareb.setOnClickListener(clickListener);

		ImageButton ib = (ImageButton)convertView.findViewById(R.id.add_event_consumed_item_ib);
		CustomRemoveListener2 removeListener = new CustomRemoveListener2();
		removeListener.setPosition(ndialogs);
		ib.setOnClickListener(removeListener);

		return convertView;
	}

	private class CustomClickListener implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}

		public void showPopUp(){
			alertDialogBuilder
			.setMultiChoiceItems(items, tempCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(which==0){
						if(isChecked){
							for(int i=1;i<items.length;i++){
								((AlertDialog) dialog).getListView().setItemChecked(i,true);
								tempCheckedItems[i]=true;
							}
						}else{
							for(int i=1;i<items.length;i++){
								((AlertDialog) dialog).getListView().setItemChecked(i,false);
								tempCheckedItems[i]=false;
							}
						}
					}
					else{
						((AlertDialog) dialog).getListView().setItemChecked(0,false);
						tempCheckedItems[0]=false;
					}
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}

		public void onClick(View v) {
			tempCheckedItems=checkedItems.get(position);
			showPopUp();
			checkedItems.set(position, tempCheckedItems);

		}

	}

	private class CustomRemoveListener2 implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}

		public void onClick(View v) {
			slist.removeViewAt(position);	
			checkedItems.remove(position);
			ndialogs--;
		}

	}

	public void createToast(String message){
		Toast n = Toast.makeText(AddEventActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}

	public float sumArray(float[] arr){
		float sum=0;
		for(int j=0;j<arr.length;j++){
			sum+=arr[j];
		}
		return sum;
	}

	public boolean addEvent(String eventName, float[] amountPaid, int[] paidMembers, float[] amountConsumed, List<boolean[]> whoConsumed){
		float totalpaid = sumArray(amountPaid);
		float totalconsumed = sumArray(amountConsumed);
		if(totalpaid!=totalconsumed){
			createToast("Error! The total amount paid is not equal to the total amount consumed (Paid = "+totalpaid+"; Consumed = "+totalconsumed+"; Difference = "+(totalpaid-totalconsumed)+").");
			return false;
		}
		gpdb.addEvent(eventName, amountPaid, paidMembers, amountConsumed, whoConsumed, namearray);
		return true;
	}

	public void doneAddEvent(View v){
		EditText event = (EditText) findViewById(R.id.AddEventModEventName);
		String eventname = event.getText().toString();
		if(eventname.equals("")){
			createToast("Error! Cannot leave the event name empty");
			return;
		}
		
		float[] amountpaid = new float[nspinners];
		int[] paidmembers = new int[nspinners];
		for (int k=0; k<nspinners; k++) {
			View row = (View) plist.getChildAt(k);
			EditText et = (EditText) row.findViewById(R.id.editTextAddEventMod1);
			Spinner sp = (Spinner) row.findViewById(R.id.spinnerAddEventMod); 
			String amt = et.getText().toString();
			if(amt.equals("")){
				createToast("Error! Cannot leave the amount paid field empty");
				return;
			}
			else{
				amountpaid[k]=Float.valueOf(amt);
			}
			paidmembers[k]=sp.getSelectedItemPosition();
		}
		float[] amountconsumed = new float[ndialogs];
		for (int k=0; k<ndialogs; k++) {
			View row = (View) slist.getChildAt(k);
			EditText et = (EditText) row.findViewById(R.id.editTextAddEventMod2);
			String amt = et.getText().toString();
			if(amt.equals("")){
				createToast("Error! Cannot leave the consumed amount field empty");
				return;
			}
			else{
				amountconsumed[k]=Float.valueOf(amt);
			}
			boolean[] tempchk = checkedItems.get(k);
			boolean flagpopup = true;
			for(int i=1;i<tempchk.length;i++){
				if(tempchk[i]){
					flagpopup = false;
				}
			}
			if(flagpopup){
				createToast("Error! The amount "+amountconsumed[k]+" is not shared among any members");
				return;
			}
		}

		if(!addEvent(eventname, amountpaid, paidmembers, amountconsumed, checkedItems)){
			return;
		}
		this.finish();
		 
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
