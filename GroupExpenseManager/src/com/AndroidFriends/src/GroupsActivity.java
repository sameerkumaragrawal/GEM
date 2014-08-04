package com.AndroidFriends.src;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.AndroidFriends.src.NewGroupModActivity;
import com.AndroidFriends.R;
import com.AndroidFriends.R.id;

@TargetApi(11)
public class GroupsActivity extends Activity {

	public int position;
	public String GroupName;
	 //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	public ArrayList<String> listItems=null;
	public String[] stringArray = new String[] { "Open", "Delete" };

    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;
    private ListView gl;
    public final static String GROUP_NAME = "GroupSummmary/GroupName";
    public final static String GROUP_ID = "GroupSummmary/GroupID";
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
		gl = (ListView) this.findViewById(R.id.GroupsList);
		listItems= new ArrayList<String>();
		groupList();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        gl.setAdapter(adapter);
        gl.setClickable(true);
        gl.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
        	@SuppressWarnings("rawtypes")
    		public void onItemClick(AdapterView parentView, View childView, int position, long id) {
        		sendName(position);
        	}
            @SuppressWarnings({ "rawtypes", "unused" })
    		public void onNothingClick(AdapterView parentView) {
            }
        });
        gl.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	
        	@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView parentView, View childView, int position, long id) {
        		showDialog1(position);
        		return true;
        	}
        });
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
    	Intent intent = new Intent(this, NewGroupModActivity.class);
    	startActivity(intent);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(String s) {
    	listItems.add(s);
    }
    
    public void sendName(int pos) {
    	Intent intent = new Intent(this, GroupSummaryActivity.class);
    	intent.putExtra(GROUP_NAME, listItems.get(pos));
    	intent.putExtra(GROUP_ID, commondb.GroupNameToDatabaseId(listItems.get(pos)));
    	startActivity(intent);
    }
    
    public void sendOption(int option) {
    	GroupName = listItems.get(position);
    	if(option == 0) {
    		Intent intent = new Intent(this, GroupSummaryActivity.class);
        	intent.putExtra(GROUP_NAME, GroupName);
        	intent.putExtra(GROUP_ID, commondb.GroupNameToDatabaseId(GroupName));
        	startActivity(intent);
    	}
    	if(option == 1) {
    		DeleteAlert();
    	}
    }
    
    public void showDialog1(int pos) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(
    			new ContextThemeWrapper(this, R.style.DialogTheme));
    	//builder.setTitle("Select Color Mode");

    	position = pos;
    	
    	ListView modeList = new ListView(this);
    	
    	ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
    			this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
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
    				addItems(gquery.getString(0));
    			} while(gquery.moveToNext());
    		}
    	}catch(Exception e){}
    }
    
    public void deleteGroup(){
    	int id = commondb.GroupNameToDatabaseId(GroupName);
    	String databasename="Database_"+id;
    	this.deleteDatabase(databasename);
    	commondb.deleteID(id);
    	this.onStart();
    }
    
}
