package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

public class AddEventActivity extends Activity {
	private String grpName = "";
	private int grpid = 0;
	private String[] namearray =null;
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
	private GroupDatabase gpdb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent  intent = getIntent();
		grpName = intent.getStringExtra(GroupsActivity.GROUP_NAME);
		grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		String new_title= grpName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_add_event);
		checkedItems = new ArrayList<boolean[]>();
		popupsize = namearray.length+1;
		tempCheckedItems = new boolean[popupsize];
		AddEventTable1=(TableLayout)findViewById(R.id.AddEventTableLayout1);
		AddEventTable2=(TableLayout)findViewById(R.id.AddEventTableLayout2);
		scale = AddEventTable1.getContext().getResources().getDisplayMetrics().density;
		MemberList();

		addMember1(null);
		addMember2(null);
		EditText event = (EditText) findViewById(R.id.AddEventEventName);
		event.requestFocus();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_event, menu);
		return true;
	}

	public void MemberList(){

		items = new CharSequence [popupsize];
		items[0]="All";
		for (int j=0; j<namearray.length; j++) {
			list.add(namearray[j]);
			items[j+1] = namearray[j];
		}        
	}

	public void addItemsOnSpinner(Spinner spin1) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin1.setAdapter(dataAdapter);
	}

	@TargetApi(11)
	public void addMember1(View v) {
		number1++;
		AddEventTable1.setStretchAllColumns(true);
		TableRow tr = new TableRow(this);

		tr.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		Spinner sp = new Spinner(this);
		LayoutParams l1 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		l1.width = 150;
		sp.setLayoutParams(l1);
		addItemsOnSpinner(sp);
		tr.addView(sp);

		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		LayoutParams l2 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		l2.width = 100;
		l2.setMargins(20, 0, 0, 0);
		et.setLayoutParams(l2);
		tr.addView(et);

		if (number1>1) {
			ImageButton ib = new ImageButton(this);
			ib.setBackgroundResource(R.drawable.minus_back);
			LayoutParams l3 = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			l3.width = 30;
			l3.height = 70;
			ib.setLayoutParams(l3);
			ib.setPadding(0, 10, 0, 0);
			ib.setScaleType(ScaleType.CENTER);
			ib.setScaleX((float) 0.8);
			ib.setScaleY((float) 0.8);
			ib.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					removeMember1(v);
				}
			});
			tr.addView(ib);
			TextView v1 = (TextView) findViewById(R.id.paidView2);
			LayoutParams v1lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v1lp.setMargins(100, 0, 0, 0);
			v1.setLayoutParams(v1lp);
		}
		tr.requestFocus();

		AddEventTable1.addView(tr,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		addScroll1();
	}

	public void removeMember1(View v) {
		TableRow tr = (TableRow) v.getParent();
		int index = AddEventTable1.indexOfChild(tr);
		AddEventTable1.removeView(tr);
		TableRow tr2 = (TableRow)AddEventTable1.getChildAt(index-1);
		EditText et = (EditText)tr2.getChildAt(1);
		et.requestFocus();
		number1--;
		if (number1 == 1) {
			TextView v1 = (TextView) findViewById(R.id.paidView2);
			LayoutParams v1lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v1lp.setMargins(160, 0, 0, 0);
			v1.setLayoutParams(v1lp);
		}
	}

	private void addScroll1(){
		new Handler().postDelayed(new Runnable() {            
			public void run() {
				Button b = (Button)findViewById(R.id.addbuttonAddevent1);
				ScrollView sv = (ScrollView)findViewById(R.id.addEventScroller);
				sv.scrollBy(0, b.getHeight());
			}
		},0);
	}

	@TargetApi(11)
	public void addMember2(View v) {
		boolean[] temparray = new boolean[popupsize];
		for(int i=0;i<popupsize;i++){
			temparray[i]=false;
		}
		number2++;
		checkedItems.add(temparray);
		AddEventTable2.setStretchAllColumns(true);
		TableRow tr = new TableRow(this);

		tr.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		LayoutParams l3 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		l3.width = 90;
		et.setLayoutParams(l3);
		tr.addView(et);

		Button shareButton = new Button(this);
		shareButton.setText("Share Among");
		LayoutParams l4 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		l4.setMargins(20, 0, 0, 0);
		l4.width = 160;
		shareButton.setLayoutParams(l4);
		sharedialogid++;
		shareButton.setId(sharedialogid+500);
		shareButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				TableRow tr = (TableRow) v.getParent();
				int index = AddEventTable2.indexOfChild(tr);
				tempCheckedItems=checkedItems.get(index);
				showDialog(v.getId());
				checkedItems.set(index, tempCheckedItems);
			}
		});
		tr.addView(shareButton);

		if (number2>1) {
			ImageButton ib = new ImageButton(this);
			ib.setBackgroundResource(R.drawable.minus_back);
			//int pixels = (int) (35 * scale + 0.5f);
			LayoutParams l5 = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			l5.width = 30;
			l5.height = 70;
			ib.setLayoutParams(l5);
			ib.setPadding(0, 10, 0, 0);
			ib.setScaleType(ScaleType.CENTER);
			ib.setScaleX((float) 0.8);
			ib.setScaleY((float) 0.8);
			ib.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					removeMember2(v);
				}
			});
			tr.addView(ib);

			TextView v1 = (TextView) findViewById(R.id.ConsumptionView1);
			LayoutParams v1lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v1lp.setMargins(5, 0, 0, 0);
			v1.setLayoutParams(v1lp);

			TextView v2 = (TextView) findViewById(R.id.ConsumptionView2);
			LayoutParams v2lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v2lp.setMargins(65, 0, 0, 0);
			v2.setLayoutParams(v2lp);
		}

		tr.requestFocus();

		AddEventTable2.addView(tr,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));    
		addScroll2();
	}

	public void removeMember2(View v) {
		TableRow tr = (TableRow) v.getParent();
		int index = AddEventTable2.indexOfChild(tr);
		AddEventTable2.removeView(tr);
		checkedItems.remove(index);
		TableRow tr2 = (TableRow)AddEventTable2.getChildAt(index-1);
		tr2.requestFocus();
		number2--;
		if (number2 == 1) {
			TextView v1 = (TextView) findViewById(R.id.ConsumptionView1);
			LayoutParams v1lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v1lp.setMargins(20, 0, 0, 0);
			v1.setLayoutParams(v1lp);

			TextView v2 = (TextView) findViewById(R.id.ConsumptionView2);
			LayoutParams v2lp = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			v2lp.setMargins(120, 0, 0, 0);
			v2.setLayoutParams(v2lp);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
		.setTitle("Share Among")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		})
		/*.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getBaseContext(), "Cancel clicked!", Toast.LENGTH_LONG).show();
                    }
                })*/
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
		})
		.create();

	}

	private void addScroll2(){
		new Handler().postDelayed(new Runnable() {            
			public void run() {
				Button b = (Button)findViewById(R.id.donebuttonAddevent);
				ScrollView sv = (ScrollView)findViewById(R.id.addEventScroller);
				sv.scrollTo(0, b.getBottom());
			}
		},0);
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
			Toast n = Toast.makeText(AddEventActivity.this,"Error! The total amount paid is not equal to the total amount consumed (Paid = "+totalpaid+"; Consumed = "+totalconsumed+"; Difference = "+(totalpaid-totalconsumed)+").", Toast.LENGTH_LONG);
			n.setGravity(Gravity.CENTER_VERTICAL,0,0);
			n.show();
			return false;
		}
		gpdb.addEvent(eventName, amountPaid, paidMembers, amountConsumed, whoConsumed, namearray);
		return true;
	}

	public void doneAddEvent(View v){
		EditText event = (EditText) findViewById(R.id.AddEventEventName);
		String eventname = event.getText().toString();
		if(eventname.equals("")){
			Toast n = Toast.makeText(AddEventActivity.this,"Error! Cannot leave the event name empty", Toast.LENGTH_SHORT);
			n.setGravity(Gravity.CENTER_VERTICAL,0,0);
			n.show();
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
				Toast n = Toast.makeText(AddEventActivity.this,"Error! Cannot leave the amount paid field empty", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
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
				Toast n = Toast.makeText(AddEventActivity.this,"Error! Cannot leave the consumed amount field empty", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
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
				Toast n = Toast.makeText(AddEventActivity.this,"Error! The amount "+amountconsumed[k]+" is not shared among any members", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
		}

		if(!addEvent(eventname, amountpaid, paidmembers, amountconsumed, checkedItems)){
			return;
		}
		this.finish();
	}

}