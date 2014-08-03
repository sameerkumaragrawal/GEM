package com.AndroidFriends.src;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.AndroidFriends.R;

public class MainActivity extends Activity {

	public final static String GroupName = "com.example.myfirstapp.groupname";
	public final static String MEMBERS = "com.example.myfirstapp.members";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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