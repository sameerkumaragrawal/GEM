package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

public class AddEventModActivity extends Activity {

	private int nspinners = 0;
	private String grpName = "";
	private int grpid = 0;
	private String[] namearray =null;
	private LayoutInflater inflater;
	
	private LinearLayout plist;
	
	private ListView slist;
	private ArrayList<String> sitems;
	//private SListAdaptor sadaptor;
	
	/*
	private List<String> list = new ArrayList<String>();
	private List<boolean[]> checkedItems = null;
	private boolean[] tempCheckedItems = null;
	private CharSequence[] items;
	private TableLayout AddEventTable1 = null;
	private TableLayout AddEventTable2 = null;
	private int popupsize = 0;
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
		setContentView(R.layout.activity_add_event_mod);
		
		gpdb=GroupDatabase.get(this, database);
		
		inflater = LayoutInflater.from(this);
		plist = (LinearLayout) findViewById(R.id.addEventModLinearLayout1);
			
	}
	
	public View getView(){
		View convertView = inflater.inflate(R.layout.add_event_paid_item, null);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, namearray); 
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spin = (Spinner)convertView.findViewById(R.id.spinnerAddEventMod);
		spin.setAdapter(dataAdapter);
		ImageButton ib = (ImageButton)convertView.findViewById(R.id.add_event_paid_item_ib);
		CustomRemoveListener removeListener = new CustomRemoveListener();
		removeListener.setPosition(nspinners);
		ib.setOnClickListener(removeListener);
		
		return convertView;
	}
	
	private class CustomRemoveListener implements OnClickListener{

		private int position;
		
		public void setPosition(int pos){
			position = pos;
		}
		public void onClick(View v) {
			plist.removeViewAt(position);		
			nspinners--;
		}
		
	}
	
	public void addMember1(View v) {
		plist.addView(getView());
		nspinners++;
	}
	
	/*
	private class SListAdaptor extends BaseAdapter{

		private LayoutInflater inflater;

		public SListAdaptor(Context context){
			inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return sitems.size();
		}

		public String getItem(int position) {
			return sitems.get(position);
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
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.memberText.setText("Member "+(position+1));
			holder.name.setText(items.get(position));
			return convertView;
		}

	}
	*/
	public void createToast(String message){
		Toast n = Toast.makeText(AddEventModActivity.this,message, Toast.LENGTH_SHORT);
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
		return;
		/*
		EditText event = (EditText) findViewById(R.id.AddEventEventName);
		String eventname = event.getText().toString();
		if(eventname.equals("")){
			createToast("Error! Cannot leave the event name empty");
			return;
		}
		float[] amountpaid = new float[AddEventTable1.getChildCount()];
		int[] paidmembers = new int[AddEventTable1.getChildCount()];
		for (int k=0; k<AddEventTable1.getChildCount(); k++) {
			TableRow tr = (TableRow) AddEventTable1.getChildAt(k);
			EditText et = (EditText) tr.getChildAt(1);
			Spinner sp = (Spinner) tr.getChildAt(0); 
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
		float[] amountconsumed = new float[AddEventTable2.getChildCount()];
		for (int k=0; k<AddEventTable2.getChildCount(); k++) {
			TableRow tr = (TableRow) AddEventTable2.getChildAt(k);
			EditText et = (EditText) tr.getChildAt(0);
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
		*/
	}
}
