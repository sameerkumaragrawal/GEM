package com.AndroidFriends.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;
import com.AndroidFriends.R.id;


@TargetApi(11)
public class GroupsActivity extends Activity {

//	private class fileItem {
//		public String file;
//		public int icon;
//		
//		public fileItem (String file , Integer icon) {
//	        this.file = file;
//	        this.icon = icon;
//	    }
//		
//		 @Override
//	    public String toString(){
//            return file;
//        }
//	}
	
	public int selPosition;
	public String GroupName;
	 //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	public ArrayList<String> items;
	public String[] stringArray = new String[] { "Open", "Delete" };

    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	private ListAdaptor adaptor;
    private ListView list;
    
    public final static String CONTACTS_LIST = "GroupSummmary/Contacts";
    public final static String GROUP_NAME = "GroupSummmary/GroupName";
    public final static String GROUP_ID = "GroupSummmary/GroupID";
    public final static String GROUP_CURR_ID = "GroupSummmary/GroupCurrency";
    private CommonDatabase commondb;
    private static final int DIALOG_LOAD_FILE = 1000;
    private String[] fileList;
//    private fileItem[] fileItemList;
    private String chosenFile, currentPath;
    private int dirLevel;
    private String rootPath = Environment.getExternalStorageDirectory() + "/";

    public ArrayList<String> contactNames = new ArrayList<String>();
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commondb = CommonDatabase.get(this);
        setContentView(R.layout.activity_groups);
        adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.GroupsList);
		list.setAdapter(adaptor);
		
		groupList();
		getContacts();
    }
	
	@Override
	public void onRestart(){
		super.onRestart();
		
		adaptor = new ListAdaptor(this);
		items = new ArrayList<String>();

		list = (ListView) findViewById(R.id.GroupsList);
		list.setAdapter(adaptor);
		
		groupList();
		getContacts();
	}
	
	// Get the phone contacts in the contactNames array list
	public void getContacts() {
		try {
			ContentResolver cr = getBaseContext().getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			 
			// If data found in contacts 
			if (cur.getCount() > 0) {
				String name = "";
				while (cur.moveToNext()) {			     
				    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				    contactNames.add(name.toString());
				}
			}
		    cur.close();
		} catch (Exception e) {
		     Log.i("getContacts","Exception : "+ e);
		}
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
            case id.new_group:
            	newGroup(null);
            	return true;
            case id.import_group:
            	importStart(null);
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
    	intent.putExtra(CONTACTS_LIST, contactNames);
    	startActivity(intent);
    }
    
    public void sendOption(int option) {
    	GroupName = items.get(selPosition);
    	if(option == 0) {
    		Intent intent = new Intent(this, GroupSummaryActivity.class);
        	intent.putExtra(GROUP_NAME, GroupName);
        	intent.putExtra(GROUP_ID, commondb.GroupNameToDatabaseId(GroupName));
        	intent.putExtra(GROUP_CURR_ID, commondb.GroupNameToCurrency(GroupName));
        	intent.putExtra(CONTACTS_LIST, contactNames);
        	startActivity(intent);
    	}
    	if(option == 1) {
    		DeleteAlert();
    	}
    }
    
    private class CustomOnItemClickListener implements OnItemClickListener{

		private AlertDialog dialog;
		
		CustomOnItemClickListener(AlertDialog d){
			this.dialog = d;
		}
		
    	public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
    		sendOption(position);
    		dialog.dismiss();
			
		}
    	
    }
    public void showOpenDialog(int pos) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(
    			new ContextThemeWrapper(this, R.style.DialogTheme));

    	selPosition = pos;
    	
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
    
    // Functions to import an existing database
    public void importStart(View v) {
    	File mPath = new File(rootPath);
    	currentPath = rootPath;
    	dirLevel = 0;
    	loadFileList(mPath);
    	myShowDialog(DIALOG_LOAD_FILE);
    }
    
    // Load list of files at given path into fileList
	private void loadFileList(File mPath) {
		String[] tempFileList;
	    if(mPath.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                return (filename.endsWith(GroupSummaryActivity.DB_EXTENSION) || sel.isDirectory()) && !sel.isHidden();
	            }
	        };
	        tempFileList = mPath.list(filter);
	        Arrays.sort(tempFileList);
	        if (dirLevel == 0) {
		    	 fileList = tempFileList;
		    }
	        else {
	        	fileList = new String[tempFileList.length+1];
	        	fileList[0] = "..";
	        	for (int i=0; i<tempFileList.length; i++) {
	        		fileList[i+1] = tempFileList[i];
	        	}
	        }
	        
	        for (int i=0; i<fileList.length; i++) {
	        	File tempFile = new File(mPath, fileList[i]);
        		if (tempFile.isDirectory()) {
        			if (!fileList[i].equals("..")) fileList[i] += "/";
        		}
	        }
	        
//	        fileItemList = new fileItem[fileList.length];
//	        for (int i=0; i<fileList.length; i++) {
//	        	fileItemList[i].file = fileList[i];
//	        	fileItemList[i].icon = R.drawable.ic_menu_archive;
//	        }
	    }
	    else {
//	        fileItemList= new fileItem[0];
	    	fileList = new String[0];
	    }
	}

	// Create dialog for choosing files
	public void myShowDialog(int id) {
//		ListAdapter adapter1 = new ArrayAdapter<fileItem>(
//			this, android.R.layout.select_dialog_item, android.R.id.text1, fileItemList){
//			public View getView(int position, View convertView, ViewGroup parent) {
//	            //User super class to create the View
//	            View v = super.getView(position, convertView, parent);
//	            TextView tv = (TextView)v.findViewById(android.R.id.text1);
//
//	            //Put the image on the TextView
//	            tv.setCompoundDrawablesWithIntrinsicBounds(fileItemList[position].icon, 0, 0, 0);
//
//	            //Add margin between image and text (support various screen densities)
//	            int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
//	            tv.setCompoundDrawablePadding(dp5);
//
//	            return v;
//	        }
//	    };
		
		AlertDialog dialog = null;
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    switch(id) {
	        case DIALOG_LOAD_FILE:
	            builder.setTitle("Choose database file to import group from " + currentPath);
	            builder.setIcon(R.drawable.ic_menu_archive);
	            if(fileList == null) {
	                dialog = builder.create();
	                dialog.show();
	                return;
	            }
	            builder.setItems(fileList, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    if (dirLevel != 0 && which == 0) {
	                    	currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
	                    	currentPath = currentPath.substring(0, currentPath.lastIndexOf("/")+1);
	                    	File cFile = new File(currentPath);
	                    	dirLevel--;
	                    	loadFileList(cFile);
	                    	myShowDialog(DIALOG_LOAD_FILE);
	                    }
	                	
	                    else {
		                	chosenFile = fileList[which];
		                    currentPath = currentPath + chosenFile;
		                    File cFile = new File(currentPath);
		                    if (cFile.isDirectory()) {
		                    	dirLevel++;
		                    	loadFileList(cFile);
		                    	myShowDialog(DIALOG_LOAD_FILE);
		                    }
		                    else {
		                    	Toast.makeText(getBaseContext(), "Imported group from " + currentPath + " successfully",
		                    			Toast.LENGTH_LONG).show();
		                    	importGroupDatabase(currentPath);
		                    }
	                    }
	                }
	            });
	            break;
	    }
	    dialog = builder.create();
	    dialog.show();
	}
    
	@SuppressWarnings("resource")
	public void importGroupDatabase(String DBPath) {
		int grpId = commondb.getNumberOfGroups() + 1;
		try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "com.AndroidFriends" + "//databases//" + "Database_" + grpId;
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(DBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            
            String grpName = "";
            Intent intent = new Intent(this, ImportGroupDatabase.class);
    		intent.putExtra(GroupsActivity.GROUP_NAME, grpName);
    		intent.putExtra(GroupsActivity.GROUP_ID, grpId);
    		intent.putExtra(GroupsActivity.CONTACTS_LIST, contactNames);
    		startActivity(intent);
        } catch (Exception e) {
        	Log.e("adi", "error", e);
        }
	}
	
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
    
}
