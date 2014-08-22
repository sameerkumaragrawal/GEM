package com.AndroidFriends.src;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

public class ImportGroupDatabase extends Activity {

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
	private int groupid = 0;
	private int grpCurrency = 0;
	private int numbermembers = 0, numberItems = 0;

	private ArrayList<String> contactNames;
	private GroupDatabase gpdb;
	private CommonDatabase commondb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		commondb = CommonDatabase.get(this);

		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		groupid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		contactNames = intent.getStringArrayListExtra(GroupsActivity.CONTACTS_LIST);
		
		String database="Database_"+groupid;
		gpdb=GroupDatabase.get(this, database);
		String new_title= "Import Group";
		this.setTitle(new_title);
		setContentView(R.layout.activity_new_group);
		
		currencyList();
		addItemsOnCurrencySpinner();
		addMembers();
		
		inflater = LayoutInflater.from(this);
		list = (LinearLayout) findViewById(R.id.newGroupListView);
		listListeners = new ArrayList<CustomRemoveClickListener>();
		listTextViews = new ArrayList<TextView>();
		
		addInitialGroupName(groupName);
		
		for(int j=0; j<numbermembers; j++){
			addInitialMember(namearray[j]);
		}
		View row = (View)list.getChildAt(0);
		AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
		et.requestFocus();
	}
	
	public void addMembers() {
		Cursor memberQuery = gpdb.MemberListWithBalance();
		numbermembers = memberQuery.getCount();
		namearray = new String[numbermembers];
		int i = 0;
		memberQuery.moveToFirst();
		do {
			namearray[i] = memberQuery.getString(1);
			i++;
		} while (memberQuery.moveToNext());
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
		listofcurrency.add("Select Currency");
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
	}


	public void addMember(View v){
		int position = numberItems;
		View convertView = inflater.inflate(R.layout.new_group_item, null);
		ImageButton removeButton = (ImageButton)convertView.findViewById(R.id.new_group_item_ib);
		CustomRemoveClickListener removeListener = new CustomRemoveClickListener(); 
		removeListener.setPosition(position);
		removeButton.setOnClickListener(removeListener);
		
		listListeners.add(removeListener);
		TextView memberText = (TextView)convertView.findViewById(R.id.new_group_item_tv);
		listTextViews.add(memberText);
	
		if(position==0){
			removeButton.setVisibility(View.INVISIBLE);
			MainActivity.setWeight(removeButton, 0);
			memberText.setText("Group Name");
			
			// Add auto complete to the group name
			AutoCompleteTextView groupNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			groupNameEditText.setThreshold(1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, NewGroupActivity.groupNames);
			groupNameEditText.setAdapter(adapter);
		}else{
			if(position <= numbermembers){
				removeButton.setVisibility(View.INVISIBLE);
				MainActivity.setWeight(removeButton, 0);
			}
			else{
				removeButton.setVisibility(View.VISIBLE);
				MainActivity.setWeight(removeButton, MainActivity.imagebuttonweight);
			}
			memberText.setText("Member "+(position));
			
			// Add auto complete to member name
			AutoCompleteTextView memberNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			memberNameEditText.setThreshold(2);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contactNames);
			memberNameEditText.setAdapter(adapter);
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
		Toast n = Toast.makeText(ImportGroupDatabase.this,message, Toast.LENGTH_SHORT);
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
		}
		
		grpCurrency = currencySpinner.getSelectedItemPosition();
		if(grpCurrency == 0){
			createToast("Error! Please select a currency for the group transactions");
			return;
		}
		
		if(!updatedatabase(group_name,members)){
			return;
		}
		else {
			commondb.insertImportedGroup(groupid, groupName, grpCurrency);
			this.finish();
		}
	}
	
	public boolean updatedatabase(String grpname,String[] members){
		if(!updategroupname(grpname)){
			return false;
		}
		
		groupName=grpname;
		gpdb.updateMembers(members,namearray);
		return true;
	}

	public boolean updategroupname(String grpname){
		if(!commondb.updategroupname(grpname,groupid)){
			createToast("Error! Group "+grpname+" already exists");
			return false;
		}
		return true;
	}
	
	@Override
	public void finish(){
		super.finish();
		gpdb.close();
		GroupDatabase.closeAll();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
