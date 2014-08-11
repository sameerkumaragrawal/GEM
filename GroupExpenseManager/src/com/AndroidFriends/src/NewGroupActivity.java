package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class NewGroupActivity extends Activity {

	private ListView list;
	private ArrayList<String> items;
	private ListAdaptor adaptor;
	private int numberMembers, lastItemCount, currencyId = 0;
	private GroupDatabase gpdb;
	private CommonDatabase commondb;
	private String[] currencyArray=null;
	private Spinner currencySpinner;
	private ArrayList<String> listofcurrency = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		commondb = CommonDatabase.get(this);

		adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.newGroupListView);
		list.setAdapter(adaptor);
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
			if(position==0){
				holder.removeButton.setVisibility(View.INVISIBLE);
				holder.setWeight(0);
				holder.memberText.setText("Group Name");
			}else{
				holder.removeButton.setVisibility(View.VISIBLE);
				holder.setWeight(MainActivity.imagebuttonweight);
				holder.memberText.setText("Member "+(position));
			}
			
			holder.watcher.setPosition(position);
			holder.name.setText(items.get(position));
			if((position == getCount()-1) && (lastItemCount < getCount())){
				holder.name.requestFocus();
				lastItemCount++;
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
		Toast n = Toast.makeText(NewGroupActivity.this,message, Toast.LENGTH_SHORT);
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

	public void done(View v) {
		numberMembers = items.size() - 1;
		
		if(numberMembers<1){
			createToast("Error! Group cannot be empty. Please insert a member");
			return;
		}
		
		String group_name = items.get(0);
		if(group_name.equals("")){
			createToast("Error! Cannot leave the group name empty");
			return;
		}
		
		currencyId = currencySpinner.getSelectedItemPosition();
		if(currencyId == 0){
			createToast("Error! Please select a currency for the group transactions");
			return;
		}

		String[] members = new String[numberMembers];

		for(int j=0;j<numberMembers;j++){
			members[j]="";
		}

		for (int k=0; k<numberMembers; k++) {
			String temp = items.get(k+1);

			if (!checkMemberName(temp)){
				return;
			}
			members[k] = temp;
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
