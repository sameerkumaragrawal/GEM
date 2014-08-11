package com.AndroidFriends.src;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

public class EditGroupActivity extends Activity {

	private LinearLayout list;
	private LayoutInflater inflater;
	String[] members = null;
	ArrayList<CustomRemoveClickListener> listListeners;
	
	private Spinner currencySpinner;
	private ArrayList<String> listofcurrency = null;
	private String[] currencyArray=null;

	private String[] namearray;
	private String groupName="";
	private int initialGrpCurrency;
	private int groupid = 0;
	private int grpCurrency = 0;
	private int numbermembers = 0, numberItems = 0;

	private GroupDatabase gpdb;
	private CommonDatabase commondb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		commondb = CommonDatabase.get(this);

		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		groupid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		grpCurrency = intent.getIntExtra(GroupSummaryActivity.groupCurrencyId,0);
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
		
		numbermembers=namearray.length;
		
		addInitialMember(groupName);
		
		for(int j=0; j<numbermembers; j++){
			addInitialMember(namearray[j]);
		}
		
	}
	
	public void addInitialMember(String txt){
		addMember(null);
		View row = (View)list.getChildAt(numberItems-1);
		EditText et = (EditText) row.findViewById(R.id.new_group_item_et);
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
		Toast n = Toast.makeText(EditGroupActivity.this,message, Toast.LENGTH_SHORT);
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

		if(numberItems<1){
			createToast("Error! Group cannot be empty");
			return;
		}

		View group_row = (View) list.getChildAt(0);
		EditText group_et = (EditText) group_row.findViewById(R.id.new_group_item_et);
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
			EditText et = (EditText) row.findViewById(R.id.new_group_item_et);
			members[k] = et.getText().toString();
			if (!checkMemberName(members[k],k)){
				return;
			}
		}
		
		grpCurrency = currencySpinner.getSelectedItemPosition() + 1;
		if(!updatedatabase(group_name,members,grpCurrency)){
			return;
		}
		finishedit();
	}

	public boolean updatedatabase(String grpname,String[] members,int currencyId){
		if(!grpname.equals(groupName)){
			if(!updategroupname(grpname)){
				return false;
			}
		}
		
		if(currencyId != initialGrpCurrency){
			commondb.updateGroupCurrency(currencyId, groupid);
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
