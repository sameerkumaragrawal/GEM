package com.AndroidFriends.src;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;

import com.AndroidFriends.R;

public class MainActivity extends Activity {

	//public final static String GroupName = "com.example.gem.groupname";
	//public final static String MEMBERS = "com.example.gem.members";
	public final static float imagebuttonweight = 0.3f;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase myDB= null;
        try{
        	File dbFile = this.getDatabasePath("GroupNames");
            if (dbFile.exists()){
            	myDB = this.openOrCreateDatabase("GroupNames", MODE_PRIVATE, null);
            	myDB.rawQuery("SELECT Currency FROM " + CommonDatabase.tableName +";",null);
            }
        }catch(Exception e){
        	myDB.setVersion(2);
        }finally{
        	if (myDB != null){
        		myDB.close();
        	}
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
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
    
}