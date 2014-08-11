package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

public class EditGroupActivity extends Activity {

	private ListView list;
	private ArrayList<String> items;
	private ListAdaptor adaptor;
	private Spinner currencySpinner;
	private ArrayList<String> listofcurrency = null;
	private String[] currencyArray=null;

	private String[] namearray;
	private String groupName="";
	private int initialGrpCurrency;
	private int groupid = 0;
	private int grpCurrency = 0;
	private int numbermembers = 0, lastItemCount = 0;

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
		
		adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.newGroupListView);

		list.setAdapter(adaptor);
		items.add(groupName);
		

		numbermembers=namearray.length;

		for(int j=0; j<numbermembers; j++){
			items.add(namearray[j]);
		}
		lastItemCount = items.size();
		adaptor.notifyDataSetChanged();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
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
		items.add("");
		adaptor.notifyDataSetChanged();
		list.setSelection(items.size()-1);
	}

	private class ListAdaptor extends BaseAdapter{

		private LayoutInflater inflater;

		public ListAdaptor(Context context){
			inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return items.size();
		}

		public String getItem(int position) {
			return items.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.new_group_item, null);
				holder = new Holder();
				holder.memberText = (TextView)convertView.findViewById(R.id.new_group_item_tv);
				holder.name = (EditText)convertView.findViewById(R.id.new_group_item_et);
				holder.removeButton = (ImageButton)convertView.findViewById(R.id.new_group_item_ib);
				holder.watcher = new CustomTextWatcher();
				holder.name.addTextChangedListener(holder.watcher);
				holder.removeListener= new CustomRemoveClickListener(); 
				holder.removeButton.setOnClickListener(holder.removeListener);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.removeListener.setPosition(position);
			holder.watcher.setPosition(position);
			holder.name.setText(items.get(position));
			
			if((position == getCount()-1) && (lastItemCount < getCount())){
				holder.name.requestFocus();
				lastItemCount++;
			}
			
			if(position==0){
				holder.removeButton.setVisibility(View.INVISIBLE);
				holder.setWeight(0);
				holder.memberText.setText("Group Name");
			}else{
				if(position <= numbermembers){
					holder.removeButton.setVisibility(View.INVISIBLE);
					holder.setWeight(0);
				}else{
					holder.removeButton.setVisibility(View.VISIBLE);
					holder.setWeight(MainActivity.imagebuttonweight);
				}
				holder.memberText.setText("Member "+(position));
			}
			
			return convertView;
		}

	}

	private class Holder{
		TextView memberText;
		EditText name;
		CustomTextWatcher watcher;
		ImageButton removeButton;
		CustomRemoveClickListener removeListener;
		public void setWeight(float d){
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) removeButton.getLayoutParams();
			params.weight = d;
			removeButton.setLayoutParams(params);
		}
	}

	private class CustomRemoveClickListener implements OnClickListener{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}
		public void onClick(View v) {
			items.remove(position);
			adaptor.notifyDataSetChanged();
			lastItemCount--;
			
		}

	}

	private class CustomTextWatcher implements TextWatcher{

		private int position;

		public void setPosition(int pos){
			position = pos;
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		public void afterTextChanged(Editable s) {
			items.set(position, s.toString());
		}

	}

	public void createToast(String message){
		Toast n = Toast.makeText(EditGroupActivity.this,message, Toast.LENGTH_SHORT);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}

	public boolean checkMemberName(String name){
		if (name.equals("")){
			createToast("Error! Cannot leave a member name empty");
			return false;
		}
		int occurrences = Collections.frequency(items, name);
		if(items.indexOf(name) == 0 && occurrences > 2){
			createToast("Error! Member "+name+" already exists");
			return false;
		}else if(items.indexOf(name) > 0 && occurrences > 1){
			createToast("Error! Member "+name+" already exists");
			return false;
		}
		return true;
	}

	public void done(View v){
		int itemsize = items.size() - 1;

		if(itemsize<1){
			createToast("Error! Group cannot be empty");
			return;
		}

		String group_name = items.get(0);

		if(group_name.equals("")){
			createToast("Error! Cannot leave the group name empty");
			return;
		}

		String[] members = new String[itemsize];

		for(int j=0;j<itemsize;j++){
			members[j]="";
		}

		for (int k=0; k<itemsize; k++) {
			String temp = items.get(k+1);

			if (!checkMemberName(temp)){
				return;
			}
			members[k] = temp;
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
