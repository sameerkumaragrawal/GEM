package com.AndroidFriends.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AndroidFriends.R;

public class IndividualSummaryActivity extends Activity {
	private String[] namearray;
	private float[] paidarray;
	private float[] consumedarray;
	private int countmembers;
	private int currencyDecimals = 2;
	private LayoutInflater inflater;
	private String groupName="";
	private String decimalFlag;
	private File imageFile;
	public final static String INDIVIDUAL_SUMMARY = "-individual_summary";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		paidarray = intent.getFloatArrayExtra(GroupSummaryActivity.listofpaid);
		consumedarray = intent.getFloatArrayExtra(GroupSummaryActivity.listofconsumed);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		countmembers = intent.getIntExtra(GroupSummaryActivity.stringcount, 0);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		setContentView(R.layout.activity_individual_summary);
		inflater = LayoutInflater.from(this);
		filltable();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_individual_summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.share_screenshot:
			shareScreenshot(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void filltable(){
		LinearLayout tl = (LinearLayout)findViewById(R.id.IndividualSummaryTable);
		for(int i=0;i<countmembers;i++){
			View convertView = inflater.inflate(R.layout.table_item, null);
			TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
			TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
			TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
			v1.setText(namearray[i]);
			v2.setText(String.format(decimalFlag, consumedarray[i]));
			v3.setText(String.format(decimalFlag, paidarray[i]));
			
			MainActivity.setWeight(v1,1.1f);
			MainActivity.setWeight(v2,1.2f);
			MainActivity.setWeight(v3,0.8f);
			
			tl.addView(convertView);
		}
	}
	
	public void shareScreenshot(View v) {
		// Name the screenshot according to group and activity
		String fileName = GroupSummaryActivity.FILENAME + "-" + groupName + INDIVIDUAL_SUMMARY;
		String mPath = Environment.getExternalStorageDirectory().toString() + "/" + GroupSummaryActivity.FOLDER + "/" + fileName + GroupSummaryActivity.EXTENSION;
		saveScreenshot(mPath);
		
		// Share the screenshot by loading from external storage
		Uri screenshotUri = Uri.fromFile(new File(mPath));
		final Intent imageIntent = new Intent(android.content.Intent.ACTION_SEND);
		imageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		imageIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		imageIntent.setType("image/jpeg");

		startActivity(Intent.createChooser(imageIntent, "Share screenshot using"));
	}
	
	public void saveScreenshot(String mPath) {
		// Folder for storing the screenshots
		File folder = new File(Environment.getExternalStorageDirectory() + "/" + GroupSummaryActivity.FOLDER);
	    //String path = folder.getPath();
	    if(!folder.exists()){        
	    	folder.mkdir();
	    }

		// Create bitmap screen capture
		Bitmap bitmap;
		View v1 = getWindow().getDecorView().getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);

		OutputStream fout = null;
		imageFile = new File(mPath);

		try {
		    fout = new FileOutputStream(imageFile);
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
		    fout.flush();
		    fout.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
