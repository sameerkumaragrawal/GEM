package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.AndroidFriends.R;

public class MainActivity extends Activity {

	public final static String GroupName = "com.example.myfirstapp.groupname";
	public final static String MEMBERS = "com.example.myfirstapp.members";
    public final static String CommonDatabase = "GroupNames";
    public final static String GroupTable = "Groups";
    public final static String MemberTable = "Members";
    public final static String EventTable = "Events";
    public final static String TransTable = "Transactions";
    public final static String CashTable = "CashTransfer";
    SQLiteDatabase myDB= null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
            myDB = this.openOrCreateDatabase(CommonDatabase, MODE_PRIVATE, null);
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
              + GroupTable
              + " ( ID int(11) NOT NULL, Name varchar(255) NOT NULL );");         
           }
           catch(Exception e) {
            Log.e("Error", "Error", e);
           }
           finally{ 
           	if(myDB!=null)
           		myDB.close();
           }
        
        Intent intent = new Intent(this, GroupsActivity.class);
        startActivity(intent);
        this.finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}