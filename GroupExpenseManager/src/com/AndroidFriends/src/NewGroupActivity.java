package com.AndroidFriends.src;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
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

public class NewGroupActivity extends Activity {

	private LinearLayout list;
	private LayoutInflater inflater;
	String[] members = null;
	ArrayList<CustomRemoveClickListener> listListeners;
	private int numberItems = 0, currencyId = 0;
	private GroupDatabase gpdb;
	private CommonDatabase commondb;
	private String[] currencyArray=null;
	private Spinner currencySpinner;
	private ArrayList<String> listofcurrency = null;
	public static final String[] groupNames = new String[] {
		"Friends", "Family", "College", "School", "Office", "Colleagues", "Hostel", "Students"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		commondb = CommonDatabase.get(this);

		inflater = LayoutInflater.from(this);
		list = (LinearLayout) findViewById(R.id.newGroupListView);
		listListeners = new ArrayList<CustomRemoveClickListener>();
		addMember(null);

		currencyList();
		addItemsOnCurrencySpinner();
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

	public void addMember(View v) {
		int position = numberItems;
		View convertView = inflater.inflate(R.layout.new_group_item, null);
		ImageButton removeButton = (ImageButton)convertView.findViewById(R.id.new_group_item_ib);
		CustomRemoveClickListener removeListener = new CustomRemoveClickListener(); 
		removeListener.setPosition(position);
		removeButton.setOnClickListener(removeListener);
		
		listListeners.add(removeListener);
		TextView memberText = (TextView)convertView.findViewById(R.id.new_group_item_tv);
	
		if(position==0){
			removeButton.setVisibility(View.INVISIBLE);
			MainActivity.setWeight(removeButton, 0);
			memberText.setText("Group Name");
			
			// Add auto complete to the group name
			AutoCompleteTextView groupNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			groupNameEditText.setThreshold(1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, groupNames);
			groupNameEditText.setAdapter(adapter);
		}else{
			removeButton.setVisibility(View.VISIBLE);
			MainActivity.setWeight(removeButton, MainActivity.imagebuttonweight);
			memberText.setText("Member "+(position));
			
			// Add auto complete to member name
			AutoCompleteTextView memberNameEditText = (AutoCompleteTextView) convertView.findViewById(R.id.new_group_item_et);
			memberNameEditText.setThreshold(2);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, GroupsActivity.contactNamesArray);
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
	}

	public void createToast(String message){
		Toast n = Toast.makeText(NewGroupActivity.this,message, Toast.LENGTH_SHORT);
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

	public void done(View v) {
		
		if(numberItems<1){
			createToast("Error! Group cannot be empty. Please insert a member");
			return;
		}
		
		View group_row = (View) list.getChildAt(0);
		AutoCompleteTextView group_et = (AutoCompleteTextView) group_row.findViewById(R.id.new_group_item_et);
		String group_name = group_et.getText().toString();
		
		if(group_name.equals("")){
			createToast("Error! Cannot leave the group name empty");
			return;
		}
		
		currencyId = currencySpinner.getSelectedItemPosition();
		if(currencyId == 0){
			createToast("Error! Please select a currency for the group transactions");
			return;
		}

		int numberMembers = numberItems - 1;
		members = new String[numberMembers];

		for(int j=0;j<numberMembers;j++){
			members[j]="";
		}

		for (int k=0; k<numberMembers; k++) {
			View row = (View) list.getChildAt(k+1);
			AutoCompleteTextView et = (AutoCompleteTextView) row.findViewById(R.id.new_group_item_et);
			members[k] = et.getText().toString();
			if (!checkMemberName(members[k],k)){
				return;
			}
		}
		
		insertToDatabase(group_name, members, currencyId);
	}

	public void insertToDatabase(String groupName, String[] members, int currencyId) {
		int ID = commondb.insert(groupName, currencyId);
		if(ID<0){
			createToast("Error! Group "+groupName+" already exists");
			return;
		}
		String DatabaseName="Database_"+ID;
		createTables(DatabaseName,members);	        

		this.finish();
	}

	public void createTables(String databaseName,String[] members){
		gpdb = GroupDatabase.get(this, databaseName);
		gpdb.onCreateInsert(members);
		gpdb.close();
		GroupDatabase.closeAll();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
