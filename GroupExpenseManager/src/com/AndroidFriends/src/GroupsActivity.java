package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.AndroidFriends.R;
import com.AndroidFriends.R.id;

@TargetApi(11)
public class GroupsActivity extends Activity {

	public int selPosition;
	public String GroupName;
	 //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	public ArrayList<String> items;
	public String[] stringArray = new String[] { "Open", "Delete" };

    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	private ListAdaptor adaptor;
    private ListView list;
    public final static String GROUP_NAME = "GroupSummmary/GroupName";
    public final static String GROUP_ID = "GroupSummmary/GroupID";
    public final static String GROUP_CURR_ID = "GroupSummmary/GroupCurrency";
    private CommonDatabase commondb;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commondb = CommonDatabase.get(this);
        setContentView(R.layout.activity_groups);
    }
	
	@Override
	public void onStart(){
		super.onStart();
		
		adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.GroupsList);
		list.setAdapter(adaptor);
		
		groupList();
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
				convertView = inflater.inflate(R.layout.group_item, null);
				holder = new Holder();
				holder.memberText = (TextView)convertView.findViewById(R.id.group_item_tv);
				holder.clickListener= new CustomOnClickListener(); 
				holder.memberText.setOnClickListener(holder.clickListener);
				holder.longClickListener= new CustomOnLongClickListener();
				holder.memberText.setOnLongClickListener(holder.longClickListener);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			holder.clickListener.setPosition(position);
			holder.longClickListener.setPosition(position);
			holder.memberText.setText(items.get(position));
			return convertView;
		}

	}
	
	private class Holder{
		TextView memberText;
		CustomOnClickListener clickListener;
		CustomOnLongClickListener longClickListener;
	}
	
	private class CustomOnClickListener implements OnClickListener{

		private int position;
		
		public void setPosition(int pos){
			position = pos;
		}
		
		public void onClick(View v) {
			selPosition = position;
			sendOption(0);			
		}
		
	}
	
	private class CustomOnLongClickListener implements OnLongClickListener{

		private int position;
		
		public void setPosition(int pos){
			position = pos;
		}
		
		public boolean onLongClick(View v) {
			showOpenDialog(position);
    		return true;
		}
		
	}
	
	@Override
	public void onRestart() {
    	super.onRestart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_groups, menu);
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
    
    public void newGroup(View v) {
    	Intent intent = new Intent(this, NewGroupActivity.class);
    	startActivity(intent);
    }
    
    public void sendOption(int option) {
    	GroupName = items.get(selPosition);
    	if(option == 0) {
    		Intent intent = new Intent(this, GroupSummaryActivity.class);
        	intent.putExtra(GROUP_NAME, GroupName);
        	intent.putExtra(GROUP_ID, commondb.GroupNameToDatabaseId(GroupName));
        	intent.putExtra(GROUP_CURR_ID, commondb.GroupNameToCurrency(GroupName));
        	startActivity(intent);
    	}
    	if(option == 1) {
    		DeleteAlert();
    	}
    }
    
    public void showOpenDialog(int pos) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(
    			new ContextThemeWrapper(this, R.style.DialogTheme));
    	//builder.setTitle("Select Color Mode");

    	selPosition = pos;
    	
    	ListView modeList = new ListView(this);
    	
    	ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
    			this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, stringArray);
    	modeList.setAdapter(modeAdapter);

    	builder.setView(modeList);
    	final Dialog dialog = builder.create();

    	dialog.show();
    	modeList.setClickable(true);
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
        	@SuppressWarnings("rawtypes")
    		public void onItemClick(AdapterView parentView, View childView, int option, long id) {
        		sendOption(option);
        		dialog.dismiss();
        	}
            @SuppressWarnings({ "rawtypes", "unused" })
    		public void onNothingClick(AdapterView parentView) {
            }
        });
    }
    
    public void DeleteAlert() {
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Group");
		alertDialogBuilder
			.setMessage("Are you sure you want to delete the group?")
			.setCancelable(true)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					deleteGroup();
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
    
    public void groupList(){
    	try{
    		Cursor gquery = commondb.groupList();
    		if(gquery.moveToFirst()){
    			do{
    				items.add(gquery.getString(0));
    			} while(gquery.moveToNext());
    		}
    	}catch(Exception e){}
    	adaptor.notifyDataSetChanged();
    }
    
    public void deleteGroup(){
    	int id = commondb.GroupNameToDatabaseId(GroupName);
    	String databasename="Database_"+id;
    	this.deleteDatabase(databasename);
    	commondb.deleteID(id);
    	items.remove(selPosition);
    	adaptor.notifyDataSetChanged();
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
    
}
