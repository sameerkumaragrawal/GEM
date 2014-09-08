package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

@SuppressLint("InflateParams")
public class EditGroupActivity extends Activity {

	private LinearLayout list;
	private LayoutInflater inflater;
	String[] members = null;
	ArrayList<CustomRemoveClickListener> listListeners;
	ArrayList<TextView> listTextViews;
	
	private Spinner currencySpinner;
	private ArrayList<String> listofcurrency = null;
	private String[] currencyArray=null;

	private String[] namearray;
	private String groupName="";
	private int initialGrpCurrency;
	private int groupid = 0;
	private int grpCurrency = 0;
	private int numbermembers = 0, numberItems = 0;

	private ArrayList<String> contactNames ;
	private GroupDatabase gpdb;
	private CommonDatabase commondb;
//	private NewGroupActivity newGroup = new NewGroupActivity();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		commondb = CommonDatabase.get(this);

		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		groupid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		grpCurrency = intent.getIntExtra(GroupSummaryActivity.groupCurrencyId,0);
		contactNames = intent.getStringArrayListExtra(GroupsActivity.CONTACTS_LIST);
		initialGrpCurrency = grpCurrency;
		String database="Database_"+groupid;
		gpdb=GroupDatabase.get(this, database);
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_new_group);
		
		currencyList();
		addItemsOnCurrencySpinner();
		
		inflater = LayoutInflater.from(this);
		list = (LinearLayout) findViewById(R.id.newGroupListView);
		listListeners = new ArrayList<CustomRemoveClickListener>();
		listTextViews = new ArrayList<TextView>();
		
		numbermembers=namearray.length;
		
		addInitialGroupName(groupName);
		
		for(int j=0; j<numbermembers; j++){
			addInitialMember(namearray[j]);
		}
		View row = (View)list.getChildAt(0);
		AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
		et.requestFocus();
	}
	
	public void addInitialGroupName(String txt){
		addMember(null);
		View row = (View)list.getChildAt(numberItems-1);
		AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
		et.setText(txt);
	}
	
	public void addInitialMember(String txt){
		addMember(null);
		View row = (View)list.getChildAt(numberItems-1);
		AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
		et.setText(txt);
	}
	
	public void currencyList(){
		currencyArray = commondb.getCurrencies();
		listofcurrency = new ArrayList<String>();
		for (int j=0; j<currencyArray.length; j++) {
			listofcurrency.add(currencyArray[j]);
		}
	}
	
	public void addItemsOnCurrencySpinner() {
		currencySpinner = (Spinner) findViewById(R.id.currencyDropdown);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listofcurrency);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencySpinner.setAdapter(dataAdapter);
		currencySpinner.setPrompt("Select Currency");
		currencySpinner.setSelection(grpCurrency-1);
	}


	public void addMember(View v){
		int position = numberItems;
		View convertView = inflater.inflate(R.layout.new_group_item, null);
		CheckBox meCheckBox = (CheckBox) convertView.findViewById(R.id.new_group_item_checkBox);
		ImageButton removeButton = (ImageButton)convertView.findViewById(R.id.new_group_item_ib);
		CustomRemoveClickListener removeListener = new CustomRemoveClickListener(); 
		removeListener.setPosition(position);
		removeButton.setOnClickListener(removeListener);
		
		listListeners.add(removeListener);
		TextView memberText = (TextView)convertView.findViewById(R.id.new_group_item_tv);
		listTextViews.add(memberText);
	
		if(position==0){
			removeButton.setVisibility(View.INVISIBLE);
			meCheckBox.setVisibility(View.GONE);
			MainActivity.setWeight(removeButton, 0);
			memberText.setText("Group Name");
			
			// Add auto complete to the group name
			AutoCompleteTextView groupNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			groupNameEditText.setThreshold(1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, NewGroupActivity.groupNames);
			groupNameEditText.setAdapter(adapter);
		}
		else{
			if (position==1) {
				removeButton.setVisibility(View.GONE);
				meCheckBox.setVisibility(View.VISIBLE);
				
				boolean userMember = gpdb.getUserMember();
				if (userMember) meCheckBox.setChecked(true);
				else meCheckBox.setChecked(false);
				CheckBoxListener checkBoxListener = new CheckBoxListener();
				meCheckBox.setOnCheckedChangeListener(checkBoxListener);
			}
			else if(position <= numbermembers){
				removeButton.setVisibility(View.INVISIBLE);
				meCheckBox.setVisibility(View.GONE);
				MainActivity.setWeight(removeButton, 0);
			}
			else{
				removeButton.setVisibility(View.VISIBLE);
				meCheckBox.setVisibility(View.GONE);
				MainActivity.setWeight(removeButton, MainActivity.imagebuttonweight);
			}
			memberText.setText("Member "+(position));
			
			// Add auto complete to member name
			AutoCompleteTextView memberNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			memberNameEditText.setThreshold(2);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contactNames);
			memberNameEditText.setAdapter(adapter);
			if (position==1) MainActivity.setWeight(memberNameEditText, 0.9f);
			else if (position <= numbermembers) MainActivity.setWeight(memberNameEditText, 1.3f);
		}
		list.addView(convertView);
		numberItems++;
	}

	private class CustomRemoveClickListener implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}
		
		public void onClick(View v) {
			listListeners.remove(position);
			listTextViews.remove(position);
			list.removeViewAt(position);
			listNotifyDataSetChanged();
			numberItems--;
			
		}
	}
	
	private class CheckBoxListener implements OnCheckedChangeListener{

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				createLongToast("Selecting this will result in your expenses in this group to be added automatically to your personal expenses");
			}
			else {
				createLongToast("Deselecting this will result in your expenses in this group to be automatically removed from your personal expenses");
			}
		}	
	}

	public void listNotifyDataSetChanged(){
		int k=0;
		for (CustomRemoveClickListener lis : listListeners) {
			lis.setPosition(k);
			k++;
		}
		k=0;
		for (TextView tv : listTextViews) {
			if(k!=0)
				tv.setText("Member "+k);
			k++;
		}
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(EditGroupActivity.this,message, Toast.LENGTH_SHORT);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	public void createLongToast(String message){
		Toast n = Toast.makeText(EditGroupActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}

	public boolean checkMemberName(String name, int pos){
		if (name.equals("")){
			createToast("Error! Cannot leave a member name empty");
			return false;
		}
		
		for(int i=0;i<pos;i++){
			if(members[i].equals(name)){
				createToast("Error! Member "+name+" already exists");
				return false;
			}
		}
		return true;
	}

	public void done(View v){

		if(numberItems<2){
			createToast("Error! Group cannot be empty");
			return;
		}

		View group_row = (View) list.getChildAt(0);
		AutoCompleteTextView group_et = (AutoCompleteTextView) group_row.findViewById(R.id.new_group_item_et);
		String group_name = group_et.getText().toString();

		if(group_name.equals("")){
			createToast("Error! Cannot leave the group name empty");
			return;
		}

		int numberOfMembers = numberItems - 1;
		members = new String[numberOfMembers];
		boolean userMember = false;

		for(int j=0;j<numberOfMembers;j++){
			members[j]="";
		}

		for (int k=0; k<numberOfMembers; k++) {
			View row = (View) list.getChildAt(k+1);
			AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
			members[k] = et.getText().toString();
			if (!checkMemberName(members[k],k)){
				return;
			}
			
			if (k==0) {
				CheckBox meCheckBox = (CheckBox) row.findViewById(R.id.new_group_item_checkBox);
				userMember = meCheckBox.isChecked();
			}
		}
		
		grpCurrency = currencySpinner.getSelectedItemPosition() + 1;
		if(!updatedatabase(group_name,members,userMember)){
			return;
		}
		if (initialGrpCurrency != grpCurrency) {
			currencyAlert();
		}
		else {
			finishedit();
		}
	}
	
	public void currencyAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Change Currency");
		alertDialogBuilder
		.setMessage("Changing the currency will only change the unit and not the actual values. Do you wish to continue?")
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				commondb.updateGroupCurrency(grpCurrency, groupid);
				finishedit();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public boolean updatedatabase(String grpname, String[] members, boolean userMember){
		if(!grpname.equals(groupName)){
			if(!updategroupname(grpname)){
				return false;
			}
		}
		
		groupName=grpname;
		gpdb.updateMembers(members,namearray);
		if (userMember) {
			gpdb.editUserMember(1);
			gpdb.addGroupExpensesToPersonalExpenses(groupName);
		}
		else {
			gpdb.editUserMember(0);
			gpdb.removeGroupExpensesFromPersonalExpenses(groupName);
		}
		return true;
	}

	public boolean updategroupname(String grpname){
		if(!commondb.updategroupname(grpname,groupid)){
			createToast("Error! Group "+grpname+" already exists");
			return false;
		}
		return true;
	}

	public void finishedit(){
		Intent intent = new Intent(getApplicationContext(), GroupSummaryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(GroupsActivity.GROUP_NAME, groupName);
		intent.putExtra(GroupsActivity.GROUP_ID, groupid);
		intent.putExtra(GroupsActivity.GROUP_CURR_ID, grpCurrency);
		startActivity(intent);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
