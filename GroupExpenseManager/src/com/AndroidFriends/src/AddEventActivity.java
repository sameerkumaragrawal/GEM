package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

@SuppressLint("InflateParams")
public class AddEventActivity extends Activity {

	private int nspinners = 0;
	private int ndialogs = 0;
	private String grpName = "";
	private int grpid = 0;
	private int currencyDecimals = 2;
	private String[] namearray =null;
	private LayoutInflater inflater;
	private Spinner eventSpinner, memberSpinner;
	private Button doneButton;
	private EditText highlightedEditText=null;
	private LinearLayout plist;
	ArrayList<CustomRemoveListener1> plistListeners;
	private LinearLayout slist;
	ArrayList<CustomRemoveListener2> slistRListeners;
	ArrayList<CustomClickListener> slistCListeners;
	AlertDialog.Builder alertDialogBuilder;
	private List<boolean[]> checkedItems = null;
	private boolean[] tempCheckedItems = null;
	private CharSequence[] items;
	private int popupsize = 0;
	private GroupDatabase gpdb;
	private String decimalFlag;
	public static final String[] eventNames = new String[] {
		"Food", "Lunch", "Dinner", "Breakfast", "Restaurant", "Hotel", "Canteen", "Drinks",
		"Transport", "Petrol", "Gas", "Car", "Bus", "Train", "Flight",
		"Healthcare", "Medicines", "Hospital", "Dentist",
		"Rent", "Accommodatation", "Electricity", "Phone bill",
		"Movie", "Sports", "Shopping", "Entertainment", "Party"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent  intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		String database="Database_"+grpid;
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_add_event);

		gpdb=GroupDatabase.get(this, database);
		
		AutoCompleteTextView eventNameEditText = (AutoCompleteTextView) findViewById(R.id.AddEventModEventName);
		eventNameEditText.setThreshold(1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, eventNames);
		eventNameEditText.setAdapter(adapter);
		
		addItemsOnTypeSpinner();
		doneButton = (Button) findViewById(R.id.doneButton);
		eventSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				if (position == 0) {
					doneButton.setOnClickListener(new Button.OnClickListener() {
					    public void onClick(View v) {
					    	doneAddEvent(v);
					    }
					});
					groupEvent();
				}
				else {
					doneButton.setOnClickListener(new Button.OnClickListener() {
					    public void onClick(View v) {
					    	doneAddIndividualEvent(v);
					    }
					});
					individualEvent();
				}
			}			
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	public void groupEvent() {
		LinearLayout individualExpense1 = (LinearLayout) findViewById(R.id.addIndividualExpense1);
		individualExpense1.setVisibility(View.GONE);
		LinearLayout individualExpense2 = (LinearLayout) findViewById(R.id.addIndividualExpense2);
		individualExpense2.setVisibility(View.GONE);
		
		TextView groupExpense1 = (TextView) findViewById(R.id.addGroupExpense1);
		groupExpense1.setVisibility(View.VISIBLE);
		LinearLayout groupExpense2 = (LinearLayout) findViewById(R.id.addGroupExpense2);
		groupExpense2.setVisibility(View.VISIBLE);
		LinearLayout groupExpense3 = (LinearLayout) findViewById(R.id.addGroupExpense3);
		groupExpense3.setVisibility(View.VISIBLE);
		Button groupExpense4 = (Button) findViewById(R.id.addGroupExpense4);
		groupExpense4.setVisibility(View.VISIBLE);
		TextView groupExpense5 = (TextView) findViewById(R.id.addGroupExpense5);
		groupExpense5.setVisibility(View.VISIBLE);
		LinearLayout groupExpense6 = (LinearLayout) findViewById(R.id.addGroupExpense6);
		groupExpense6.setVisibility(View.VISIBLE);
		LinearLayout groupExpense7 = (LinearLayout) findViewById(R.id.addGroupExpense7);
		groupExpense7.setVisibility(View.VISIBLE);
		Button groupExpense8 = (Button) findViewById(R.id.addGroupExpense8);
		groupExpense8.setVisibility(View.VISIBLE);
		
		plistListeners = new ArrayList<CustomRemoveListener1>();
		slistRListeners = new ArrayList<CustomRemoveListener2>();
		slistCListeners = new ArrayList<CustomClickListener>();
		checkedItems = new ArrayList<boolean[]>();
		
		inflater = LayoutInflater.from(this);
		plist = groupExpense3;
		slist = groupExpense7;

		
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
	
	public void individualEvent() {
		TextView groupExpense1 = (TextView) findViewById(R.id.addGroupExpense1);
		groupExpense1.setVisibility(View.GONE);
		LinearLayout groupExpense2 = (LinearLayout) findViewById(R.id.addGroupExpense2);
		groupExpense2.setVisibility(View.GONE);
		LinearLayout groupExpense3 = (LinearLayout) findViewById(R.id.addGroupExpense3);
		groupExpense3.setVisibility(View.GONE);
		Button groupExpense4 = (Button) findViewById(R.id.addGroupExpense4);
		groupExpense4.setVisibility(View.GONE);
		TextView groupExpense5 = (TextView) findViewById(R.id.addGroupExpense5);
		groupExpense5.setVisibility(View.GONE);
		LinearLayout groupExpense6 = (LinearLayout) findViewById(R.id.addGroupExpense6);
		groupExpense6.setVisibility(View.GONE);
		LinearLayout groupExpense7 = (LinearLayout) findViewById(R.id.addGroupExpense7);
		groupExpense7.setVisibility(View.GONE);
		Button groupExpense8 = (Button) findViewById(R.id.addGroupExpense8);
		groupExpense8.setVisibility(View.GONE);
		
		LinearLayout individualExpense1 = (LinearLayout) findViewById(R.id.addIndividualExpense1);
		individualExpense1.setVisibility(View.VISIBLE);
		LinearLayout individualExpense2 = (LinearLayout) findViewById(R.id.addIndividualExpense2);
		individualExpense2.setVisibility(View.VISIBLE);
		
		removeAllMember1(null);
		removeAllMember2(null);
		
		addItemsOnMemberSpinner();
	}
	
	public void addItemsOnTypeSpinner() {
		eventSpinner = (Spinner) findViewById(R.id.spinnerEventType);
		List<String> list = new ArrayList<String>();
		list.add("Group expense");
		list.add("Individual expense");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventSpinner.setAdapter(dataAdapter);
		eventSpinner.setPrompt("Select event type");
	}
	
	public void addItemsOnMemberSpinner() {
		memberSpinner = (Spinner) findViewById(R.id.spinnerIndividualExpense);
		List<String> list = new ArrayList<String>(Arrays.asList(namearray));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memberSpinner.setAdapter(dataAdapter);
		memberSpinner.setPrompt("Select member");
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
	
	public void removeAllMember1(View v) {
		plist.removeAllViews();
		nspinners = 0;
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
		
		plistListeners.add(removeListener);
		ib.setOnClickListener(removeListener);

		return convertView;
	}

	public void plistNotifyChanged(){
		int k=0;
		for (CustomRemoveListener1 lis : plistListeners) {
			lis.setPosition(k);
			k++;
		}
	}
	
	private class CustomRemoveListener1 implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}
		public void onClick(View v) {
			plistListeners.remove(position);
			plist.removeViewAt(position);
			nspinners--;
			plistNotifyChanged();
			if(highlightedEditText != null) highlightedEditText.clearFocus();
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
	
	public void removeAllMember2(View v) {
		slist.removeAllViews();
		ndialogs = 0;
	}

	public String getRemainingAmount() {
		String s="0";
		float amountpaid = 0;
		float amountspent = 0;
		for (int k=0; k<nspinners; k++) {
			View row = (View) plist.getChildAt(k);
			EditText et = (EditText) row.findViewById(R.id.editTextAddEventMod1);
			String amt = et.getText().toString();
			if(!amt.equals("")){
				amountpaid += Float.valueOf(amt);
			}
		}
		for (int k=0; k<ndialogs; k++) {
			View row = (View) slist.getChildAt(k);
			EditText et = (EditText) row.findViewById(R.id.editTextAddEventMod2);
			String amt = et.getText().toString();
			if(!amt.equals("")){
				amountspent += Float.valueOf(amt);
			}
		}
		float hintAmount = amountpaid-amountspent;
		s = String.format(decimalFlag, hintAmount);
		return s;
	}
	
	private class GenericTextWatcher implements TextWatcher{

	    private EditText et;
	    private GenericTextWatcher(EditText e) {
	        this.et = e;
	    }

	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	    public void afterTextChanged(Editable editable) {
	    	if (et.getText().toString().equals("")) {
            	et.setHint(getRemainingAmount());
            }
	    }
	}
	
	public View getConsumedView(){
		View convertView = inflater.inflate(R.layout.add_event_consumed_item, null);
		
		EditText et = (EditText) convertView.findViewById(R.id.editTextAddEventMod2);
		customFocusListener focusListener = new customFocusListener(); 
		et.setHint(getRemainingAmount());
		et.setOnFocusChangeListener(focusListener);
		
		GenericTextWatcher watcher= new GenericTextWatcher(et);
	    et.addTextChangedListener(watcher);
		
		Button shareb = (Button)convertView.findViewById(R.id.shareButtonAddEventMod);
		CustomClickListener clickListener = new CustomClickListener();
		clickListener.setPosition(ndialogs);
		slistCListeners.add(clickListener);
		shareb.setOnClickListener(clickListener);

		ImageButton ib = (ImageButton)convertView.findViewById(R.id.add_event_consumed_item_ib);
		CustomRemoveListener2 removeListener = new CustomRemoveListener2();
		removeListener.setPosition(ndialogs);
		slistRListeners.add(removeListener);
		ib.setOnClickListener(removeListener);

		return convertView;
	}

	private class customFocusListener implements OnFocusChangeListener {
		
		public void onFocusChange(View v, boolean hasFocus) {
			EditText et = ((EditText) v);
			if(hasFocus) {
				et.setHint(getRemainingAmount());
			}
			else {
				et.setHint("");
			}
			if(hasFocus) {
				highlightedEditText = et;
			}
			else {
				highlightedEditText = null;
			}
		}
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
			if(highlightedEditText != null) highlightedEditText.clearFocus();
			slistCListeners.remove(position);
			slistRListeners.remove(position);
			ndialogs--;
			slist.removeViewAt(position);
			checkedItems.remove(position);
			slistNotifyChanged();
		}

	}
	
	public void slistNotifyChanged(){
		int k=0;
		for (CustomRemoveListener2 lis : slistRListeners) {
			lis.setPosition(k);
			k++;
		}
		k=0;
		for (CustomClickListener lis : slistCListeners) {
			lis.setPosition(k);
			k++;
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
		gpdb.addEvent(eventName, amountPaid, paidMembers, amountConsumed, whoConsumed, namearray, grpName);
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
	
	public boolean addIndividualEvent(String eventName, float amount, int member){
		gpdb.addIndividualEvent(eventName, amount, member, grpName);
		return true;
	}
	
	public void doneAddIndividualEvent(View v){
		EditText event = (EditText) findViewById(R.id.AddEventModEventName);
		String eventname = event.getText().toString();
		if(eventname.equals("")){
			createToast("Error! Cannot leave the event name empty");
			return;
		}
		
		float eventAmount;
		int memberId;
		EditText et = (EditText) findViewById(R.id.editTextIndividualExpense);
		Spinner sp = (Spinner) findViewById(R.id.spinnerIndividualExpense); 
		String amt = et.getText().toString();
		if(amt.equals("")){
			createToast("Error! Cannot leave the amount field empty");
			return;
		}
		else{
			eventAmount = Float.valueOf(amt);
			memberId = sp.getSelectedItemPosition();
		}

		if(!addIndividualEvent(eventname, eventAmount, memberId)){
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
