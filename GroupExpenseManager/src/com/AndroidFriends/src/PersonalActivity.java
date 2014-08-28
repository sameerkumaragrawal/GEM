package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.AndroidFriends.R;
import com.AndroidFriends.R.id;

@TargetApi(11)
public class PersonalActivity extends Activity {
	private ListAdaptor adaptor;
    private ListView list;
    public int selPosition;
    public ArrayList<String> items;
	public String[] stringArray = new String[] { "Expense", "Bill" };
	
	private PersonalDatabase pdb;
	private CommonDatabase cdb;
	private String name, currencySymbol;
	private float salary, expenses, bills;
	private int currency, salaryFlag, currencyDecimals=2;
	private boolean infoAvailable = true;
	private ImageButton editInfoButton, addButton;
	private Button addInfoButton;
	private TextView noInfoText;
	public final static String stringDecimals = "summaryActivity/stringDecimals";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.PersonalList);
		list.setAdapter(adaptor);
		
		addButton = (ImageButton) findViewById(R.id.personalActivityAddButton);
		editInfoButton = (ImageButton) findViewById(R.id.personalActivityEditButton);
		addInfoButton = (Button) findViewById(R.id.addInfoButton);
		noInfoText = (TextView) findViewById(R.id.noInfoText);
		
		pdb = PersonalDatabase.get(this);
		cdb = CommonDatabase.get(this);

		getInfo();
		makeList();
    }
	
	@Override
	public void onRestart(){
		super.onRestart();
		
		adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.PersonalList);
		list.setAdapter(adaptor);
		
		makeList();
	}
			
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_personal, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case id.menu_about:
            	startActivity(new Intent(this, AboutActivity.class));
            	return true;
            case id.menu_exit:
            	exitAlert();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void makeList(){
		if (!infoAvailable) {
			noInfoText.setVisibility(View.VISIBLE);
			addInfoButton.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			addButton.setVisibility(View.GONE);
			editInfoButton.setVisibility(View.GONE);
		}
		else {
			noInfoText.setVisibility(View.GONE);
			addInfoButton.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			addButton.setVisibility(View.VISIBLE);
			editInfoButton.setVisibility(View.VISIBLE);
			
			String expenseItem = "Expenses = 1500 INR";
			String billItem = "Bils Due = 500 INR";
			// get from database
	    	
			items.add(expenseItem);
	    	items.add(billItem);    	
	    	adaptor.notifyDataSetChanged();
		}
    }
	
	public void getInfo() {
		Cursor infoQuery = pdb.getInformation();
		if (infoQuery.getCount() == 0) {
			infoAvailable = false;
		}
		else {
			infoQuery.moveToFirst();
			name = infoQuery.getString(0);
			currency = infoQuery.getInt(1);
			salary = infoQuery.getFloat(2);
			salaryFlag = infoQuery.getInt(3);
			currencyDecimals = cdb.getCurrencyDecimals(currency);
			currencySymbol = cdb.getCurrencySymbol(currency);
		}
		infoQuery.close();
	}
	
	public void addInfo(View v) {
		
	}
	
	public void editInfo(View v) {
	
	}
	
	public void addNew(int position) {
		if (position == 0) {
			Intent intent = new Intent(this, AddExpenseActivity.class);
			startActivity(intent);
		}
		else if (position == 1) {
			// open add bill activity
		}
	}
	
	public void openActivity(int position) {
		if (position == 0) {
			Intent intent = new Intent(this, ExpenseActivity.class);
			intent.putExtra(stringDecimals, currency);
			startActivity(intent);
		}
		else if (position == 1) {
			// open bills activity
		}
	}
	
    // Define required classes
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
				convertView = inflater.inflate(R.layout.group_item, null);
				holder = new Holder();
				holder.memberText = (TextView)convertView.findViewById(R.id.group_item_tv);
				holder.clickListener= new CustomOnClickListener(); 
				holder.memberText.setOnClickListener(holder.clickListener);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.clickListener.setPosition(position);
			holder.memberText.setText(items.get(position));
			return convertView;
		}

	}
	
	private class Holder{
		TextView memberText;
		CustomOnClickListener clickListener;
	}
	
	private class CustomOnClickListener implements OnClickListener{
		private int position;
		
		public void setPosition(int pos){
			position = pos;
		}
		
		public void onClick(View v) {
			selPosition = position;
			openActivity(position);
		}
	}
	
	private class CustomOnItemClickListener implements OnItemClickListener{
		private AlertDialog dialog;
		
		CustomOnItemClickListener(AlertDialog d){
			this.dialog = d;
		}
		
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		addNew(position);
    		dialog.dismiss();	
		}
    }
	
	public void addTypeDialog(View v) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(
    			new ContextThemeWrapper(this, R.style.DialogTheme));

    	ListView modeList = new ListView(this);
    	
    	ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
    			this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, stringArray);
    	modeList.setAdapter(modeAdapter);

    	builder.setView(modeList);
    	AlertDialog dialog = builder.create();

    	dialog.show();
    	modeList.setClickable(true);
    	CustomOnItemClickListener myitemlistener = new CustomOnItemClickListener(dialog);
    	
        modeList.setOnItemClickListener(myitemlistener);
    }
        
    public void exitAlert() {
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Exit GEM");
		alertDialogBuilder
			.setMessage("Are you sure you want to exit?")
			.setCancelable(true)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					exit();
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
    
    public void exit() {
    	this.finish();
    }
    	
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
    
}