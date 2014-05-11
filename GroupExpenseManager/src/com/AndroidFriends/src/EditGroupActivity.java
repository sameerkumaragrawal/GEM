package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.AndroidFriends.R;

public class EditGroupActivity extends Activity {
	private String[] namearray;
	private String groupName="";
	private int groupid = 0;
	private float scale = 0;
	private TableLayout editgrouptable = null;
	private int numbermembers = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		groupid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		setContentView(R.layout.activity_edit_group);
		editgrouptable = (TableLayout)findViewById(R.id.EditGrouptableLayout);
		scale = editgrouptable.getContext().getResources().getDisplayMetrics().density;
		numbermembers=namearray.length;
		populatelist();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit_group, menu);
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

	public void populatelist() {
		TableRow tr1 = addmemberhelper("Group Name",groupName);
		editgrouptable.addView(tr1,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		for(int k=0;k<namearray.length;k++){
			TableRow tr = addmemberhelper("Member",namearray[k]);
			editgrouptable.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
	}

	public void addMember(View v){
		numbermembers++;
		TableRow tr = addmemberhelper("Member",null);
		ImageButton ib = new ImageButton(this);
		ib.setBackgroundResource(R.drawable.minus_back);
		int pixels = (int) (55 * scale + 0.5f);
		LayoutParams l3 = new LayoutParams(pixels, pixels, 0.1f);
		ib.setLayoutParams(l3);
		ib.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				removeMember(v);
			}
		});
		tr.addView(ib);
		tr.requestFocus();
		editgrouptable.addView(tr,new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		focusScroll();
	}

	private void focusScroll(){
		new Handler().postDelayed(new Runnable() {            
			public void run() {
				View b = (View)findViewById(R.id.EditGroupLinearLayout);
				ScrollView sv = (ScrollView)findViewById(R.id.EditGroupScroller);
				sv.scrollTo(0, b.getBottom());
			}
		},0);
	}

	public void removeMember(View v) {
		TableRow tr = (TableRow) v.getParent();
		int index = editgrouptable.indexOfChild(tr);
		editgrouptable.removeView(tr);
		TableRow tr2 = (TableRow)editgrouptable.getChildAt(index-1);
		EditText et = (EditText)tr2.getChildAt(1);
		et.requestFocus();
		numbermembers--;
	}

	public TableRow addmemberhelper(String text1, String text2){
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
		if (text2 == null) {
			tv.setPadding(10, 16, 10, 0);
		}
		else {
			tv.setPadding(10, 15, 10, 0);
		}
		tr.addView(tv);

		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		et.setText(text2);
		LayoutParams l2 = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT,
				8f);
		l2.setMargins(0, ten, ten, 0);
		et.setLayoutParams(l2);
		tr.addView(et);
		return tr;
	}

	public boolean isMemberof(String s, String[] sarray){
		for(int j=0;j<numbermembers;j++){
			if(s.equals(sarray[j])){
				return true;
			}
		}
		return false;
	}

	public void doneEditGroup(View v){
		EditText editText = (EditText) (((ViewGroup) editgrouptable.getChildAt(0)).getChildAt(1));
		String group_name = editText.getText().toString();
		if(group_name.equals("")){
			Toast n = Toast.makeText(EditGroupActivity.this,"Error! Cannot leave the group name empty", Toast.LENGTH_SHORT);
			n.setGravity(Gravity.CENTER_VERTICAL,0,0);
			n.show();
			return;
		}
		String[] members = new String[numbermembers];
		for(int j=0;j<numbermembers;j++){
			members[j]="";
		}
		for (int k=0; k<numbermembers; k++) {
			EditText editTextMember;
			editTextMember = (EditText) (((ViewGroup) editgrouptable.getChildAt(k+1)).getChildAt(1));
			String temp = editTextMember.getText().toString();
			if (temp.equals("")){
				Toast n = Toast.makeText(EditGroupActivity.this,"Error! Cannot leave a member name empty", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
			else if(isMemberof(temp,members)){
				Toast n = Toast.makeText(EditGroupActivity.this,"Error! Member "+temp+" already exists", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return;
			}
			else {
				members[k] = temp;
			}
		}
		if(!updatedatabase(group_name,members)){
			return;
		}
		finishedit();
	}
	
	public boolean updatedatabase(String grpname,String[] members){
		if(!grpname.equals(groupName)){
			if(!updategroupname(grpname)){
				return false;
			}
		}
		groupName=grpname;
		updatemembers(members);
		return true;
	}
	
	public boolean updategroupname(String grpname){
		SQLiteDatabase myDB=null;
		String TableName=MainActivity.GroupTable;
		String CommonDatabase=MainActivity.CommonDatabase;
		try {
			myDB = this.openOrCreateDatabase(CommonDatabase, MODE_PRIVATE, null);
			Cursor isPresent=myDB.rawQuery("SELECT ID FROM " + TableName + " WHERE Name = '"+grpname+"';", null);
			if(isPresent.getCount()>0){
				Toast n = Toast.makeText(EditGroupActivity.this,"Error! Group "+grpname+" already exists", Toast.LENGTH_SHORT);
				n.setGravity(Gravity.CENTER_VERTICAL,0,0);
				n.show();
				return false;
			}
			myDB.execSQL("UPDATE "+TableName+" SET Name = '"+grpname+"' WHERE ID = '"+groupid+"';");
		} catch(Exception e) {
			Log.e("Error", "Error", e);
		}
		finally{ 
			if(myDB!=null)
				myDB.close();
		}
		return true;
	}

	public void updatemembers(String[] members){
		SQLiteDatabase groupDb=null;
		String database="Database_"+groupid;
		String TableName=MainActivity.MemberTable;
		try{
			groupDb = this.openOrCreateDatabase(database, MODE_PRIVATE, null);
			for(int i=0;i<namearray.length;i++){
				if(!members[i].equals(namearray[i])){
					groupDb.execSQL("UPDATE "+TableName+" SET Name = '"+members[i]+"' WHERE ID = '"+(i+1)+"';");
				}
			}
			for(int i=namearray.length;i<members.length;i++){
				groupDb.execSQL("INSERT INTO "+TableName + " ( ID, Name, Balance ) VALUES ( '" + (i+1)+"', '"+members[i] + "', '"+0+"' );" );
			}
		}catch(Exception e) {
			Log.e("Error", "Error", e);
		}
		finally{ 
			if(groupDb!=null)
				groupDb.close();
		}
	}

	public void finishedit(){
		Intent intent = new Intent(getApplicationContext(), GroupSummaryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(GroupsActivity.GROUP_NAME, groupName);
		intent.putExtra(GroupsActivity.GROUP_ID, groupid);
		startActivity(intent);
	}
}
