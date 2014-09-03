package com.AndroidFriends.src;

import java.io.File;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	//public final static String GroupName = "com.example.gem.groupname";
	//public final static String MEMBERS = "com.example.gem.members";
	public final static float imagebuttonweight = 0.3f;
	private TabHost myTabHost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        
        myTabHost = getTabHost();
        
        // Adding the tabs 
        TabSpec spec1 = myTabHost.newTabSpec("groups_tab"); 
        spec1.setIndicator("Groups"); 
        spec1.setContent(new Intent(this, GroupsActivity.class));
        myTabHost.addTab(spec1);

        TabSpec spec2 = myTabHost.newTabSpec("personal_tab"); 
        spec2.setIndicator("Personal");
        spec2.setContent(new Intent(this, PersonalActivity.class));
        myTabHost.addTab(spec2);
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
    
    public static void setWeight(View v, float w){
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
		params.weight = w;
		v.setLayoutParams(params);
	}   
}