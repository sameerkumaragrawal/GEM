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

public class PossibleSolution extends Activity {

	private String[] namearray;
	private float[] balancearray;
	private int[] idarray;
	private int countmembers;
	private int currencyDecimals = 2;
	private String[][] solutionarray;
	private String groupName="";
	private int ntransactions = 0;
	private float[] tramountarray;
	private int grpid = 0;
	private LayoutInflater inflater;
	private GroupDatabase gpdb;
	private String decimalFlag;
	private File imageFile;
	public final static String SOLUTION = "-solution";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		groupName=intent.getStringExtra(GroupsActivity.GROUP_NAME);
		String new_title= groupName+" - "+String.valueOf(this.getTitle());
		this.setTitle(new_title);
		
		inflater = LayoutInflater.from(this);
		
		idarray = intent.getIntArrayExtra(GroupSummaryActivity.listofid);
		balancearray = intent.getFloatArrayExtra(GroupSummaryActivity.listofbalance);
		namearray = intent.getStringArrayExtra(GroupSummaryActivity.listofmember);
		countmembers = intent.getIntExtra(GroupSummaryActivity.stringcount, 0);
		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		String database="Database_"+grpid;
		gpdb=GroupDatabase.get(this, database);
		compute();
		boolean clearflag=intent.getBooleanExtra(GroupSummaryActivity.clearflag, false);
		if(clearflag){
			grpid = intent.getIntExtra(GroupsActivity.GROUP_ID,0);
			clearbalance();
			this.finish();
		}
		setContentView(R.layout.activity_possible_solution);
		filltable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_possible_solution, menu);
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
		LinearLayout tl = (LinearLayout)findViewById(R.id.PossibleSolutionTable);
		for(int i=0;i<ntransactions;i++){
			View convertView = inflater.inflate(R.layout.table_item, null);
			TextView v1 = (TextView)convertView.findViewById(R.id.table_item_tv1);
			TextView v2 = (TextView)convertView.findViewById(R.id.table_item_tv2);
			TextView v3 = (TextView)convertView.findViewById(R.id.table_item_tv3);
			v1.setText(solutionarray[i][0]);
			v2.setText(solutionarray[i][2]);
			v3.setText(solutionarray[i][4]);
			
			tl.addView(convertView);
		}
	}
	public void compute(){
		int minindex, maxindex;
		float net, tramount;
		tramountarray = new float[countmembers];
		solutionarray = new String[countmembers][5];
		ntransactions = 0;
		while(true){
			minindex = 0;
			maxindex = 0;
			net = 0;
			tramount = 0;
			for(int i=1;i<countmembers;i++){
				if(balancearray[i]<balancearray[minindex]){
					minindex=i;
				}
				else if(balancearray[i]>balancearray[maxindex]){
					maxindex=i;
				}
			}
			if(balancearray[minindex]==0 || balancearray[maxindex]==0){
				break;
			}

			net = balancearray[minindex] + balancearray[maxindex];
			if(net>0){
				tramount = - balancearray[minindex];
				balancearray[minindex]=0;
				balancearray[maxindex]=net;
				solutionarray[ntransactions][4] = String.format(decimalFlag, tramount); 
			}
			else{
				tramount = balancearray[maxindex];
				balancearray[minindex]=net;
				balancearray[maxindex]=0;
				solutionarray[ntransactions][4] = String.format(decimalFlag, tramount); 
			}
			tramountarray[ntransactions]=tramount;
			solutionarray[ntransactions][0] = namearray[minindex];
			solutionarray[ntransactions][1] = String.valueOf(idarray[minindex]);
			solutionarray[ntransactions][2] = namearray[maxindex];
			solutionarray[ntransactions][3] = String.valueOf(idarray[maxindex]);
			ntransactions++;
		}
	}

	public void clearbalance(){
		gpdb.clearBalance(ntransactions, solutionarray, tramountarray);
	}
	
	public void shareScreenshot(View v) {
		// Name the screenshot according to group and activity
		String fileName = GroupSummaryActivity.FILENAME + "-" + groupName + SOLUTION;
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
	    String path = folder.getPath();
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
