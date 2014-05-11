package com.AndroidFriends.src;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.AndroidFriends.R;

@TargetApi(11)
public class NewGroupActivity extends Activity {

	public int numberMembers = 0;
	public float scale = 0;
	public TableLayout newgrouptable = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		newgrouptable = (TableLayout)findViewById(R.id.newGrouptableLayout);
		scale = newgrouptable.getContext().getResources().getDisplayMetrics().density;
		TableRow tr = addmemberhelper("Group Name");
		newgrouptable.addView(tr,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		addMember(null);
		tr.requestFocus();
		getWindow().setSoftInputMode(
				   WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_group, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addMember(View v) {
		numberMembers++;
		TableRow tr = addmemberhelper("Member");
		
		tr.requestFocus();
		newgrouptable.addView(tr,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		focusScroll();
	}
	
	public TableRow addmemberhelper(String text1){
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT)
				);
		int ten = (int) (10 * scale + 0.5f);
		int five = (int) (5 * scale + 0.5f);
		tr.setPadding(0, five, 0, ten);
		
		TextView tv = new TextView(this);
		tv.setText(text1);
		tv.setTextColor(Color.parseColor("#FFFFFF"));
		LayoutParams l1 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT,
				0.01f);
		l1.setMargins(0, ten, ten, 0);
		tv.setLayoutParams(l1);
		tv.setPadding(10, 15, 10, 0);
		tr.addView(tv);

		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		LayoutParams l2 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT,
				8f);
		l2.setMargins(0, ten, ten, 0);
		et.setLayoutParams(l2);
		tr.addView(et);
		
		if(numberMembers>1){
			tv.setPadding(10, 16, 10, 0);
			ImageButton ib = new ImageButton(this);
			ib.setBackgroundResource(R.drawable.minus_back);
			int pixels = (int) (55 * scale + 0.5f);
			LayoutParams l3 = new LayoutParams(pixels, pixels, 0.1f);
			ib.setLayoutParams(l3);
			ib.setPadding(0, 10, 0, 0);
			ib.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					removeMember(v);
				}
			});
			tr.addView(ib);
		}
				
		return tr;
	}

	private void focusScroll(){
		new Handler().postDelayed(new Runnable() {            
            public void run() {
            	View b = (View)findViewById(R.id.newGroupLinearLayout);
            	ScrollView sv = (ScrollView)findViewById(R.id.NewGroupScroller);
                sv.scrollTo(0, b.getBottom());
            }
        },0);
    }

	public void removeMember(View v) {
		TableRow tr = (TableRow) v.getParent();
		int index = newgrouptable.indexOfChild(tr);
		newgrouptable.removeView(tr);
		TableRow tr2 = (TableRow)newgrouptable.getChildAt(index-1);
		EditText et = (EditText)tr2.getChildAt(1);
		et.requestFocus();
		numberMembers--;
	}

	public boolean isMemberof(String s, String[] sarray){
		for(int j=0;j<numberMembers;j++){
			if(s.equals(sarray[j])){
				return true;
			}
		}
		return false;
	}

	public void done(View v) {
		EditText editText = (EditText) (((ViewGroup) newgrouptable.getChildAt(0)).getChildAt(1));
		String group_name = editText.getText().toString();
		if(group_name.equals("")){
			Toast n = Toast.makeText(NewGroupActivity.this,"Error! Cannot leave the group name empty", Toast.LENGTH_SHORT);
			n.setGravity(Gravity.CENTER_VERTICAL,0,0);
			n.show();
			return;
		}
		String[] members = new String[numberMembers];
		for(int j=0;j<numberMembers;j++){
			members[j]="";
		}
		for (int k=0; k<numberMembers; k++) {
			EditText editTextMember;
			editTextMember = (EditText) (((ViewGroup) newgrouptable.getChildAt(k+1)).getChildAt(1));
			String temp = editTextMember.getText().toString();
			if (temp.equals("")){
				Toast n = Toast.makeText(NewGroupActivity.this,"Error! Cannot leave a member name empty", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
			else if(isMemberof(temp,members)){
				Toast n = Toast.makeText(NewGroupActivity.this,"Error! Member "+temp+" already exists", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
			else {
				members[k] = temp;
			}
		}
		insertToDatabase(group_name, members);
	}

	public void insertToDatabase(String groupName, String[] members) {
		SQLiteDatabase myDB=null;
		String TableName=MainActivity.GroupTable;
		String CommonDatabase=MainActivity.CommonDatabase;
		int ID=1;
		try {
			myDB = this.openOrCreateDatabase(CommonDatabase, MODE_PRIVATE, null);
			Cursor isPresent=myDB.rawQuery("SELECT ID FROM " + TableName + " WHERE Name = '"+groupName+"';", null);
			if(isPresent.getCount()>0){
				Toast n = Toast.makeText(NewGroupActivity.this,"Error! Group "+groupName+" already exists", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
			Cursor count = myDB.rawQuery("SELECT max(ID) FROM " + TableName , null);
			if(count.moveToFirst()){
				ID=count.getInt(0)+1;
			}
			myDB.execSQL("INSERT INTO " + TableName + " ( ID, Name ) VALUES ( '" + ID+"', '"+groupName + "' );" );

			String DatabaseName="Database_"+ID;

			createTables(DatabaseName,members);	        


		}
		catch(Exception e) {
			Log.e("Error", "Error", e);
		}
		finally{ 
			if(myDB!=null)
				myDB.close();
		}

		this.finish();
	}

	public void createTables(String databaseName,String[] members){
		SQLiteDatabase groupDatabase=null;
		try{
			groupDatabase=this.openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
			groupDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
					+ MainActivity.MemberTable
					+ " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Balance float NOT NULL );");
			groupDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
     	           + MainActivity.EventTable
     	           + " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL, Flag int(1) );");
        	groupDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
      	           + MainActivity.TransTable
      	           + " ( MemberId int(11) NOT NULL, Paid float, Consumed float, EventId int(11) NOT NULL );");
        	groupDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
       	           + MainActivity.CashTable
       	           + " ( FromMemberId int(11) NOT NULL, ToMemberId int(11) NOT NULL, Amount float NOT NULL, ID int(11) NOT NULL);");
			 
			int length=members.length;
			for(int j=0;j<length;j++){
				groupDatabase.execSQL("INSERT INTO "+MainActivity.MemberTable + " ( ID, Name, Balance ) VALUES ( '" + (j+1)+"', '"+members[j] + "', '"+0+"' );" );
			}
		}catch(Exception e) {
			Log.e("Error", "Error", e);
		}
		finally{ 
			if(groupDatabase!=null)
				groupDatabase.close();
		}
	}
}
