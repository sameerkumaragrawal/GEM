package com.AndroidFriends.src;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.AndroidFriends.R;

public class EditBillActivity extends Activity {
	private Button doneButton;
	private String[] billNames = new String[] {"Electricity", "Landline", "Mobile", "Rent", "Hospital", "Hotel", "Insurance", "EMI", "Tax" };
	private TextView dateDisplay;
	private Button dueDateButton;
	private AutoCompleteTextView billNameEditText;
	private EditText billAmountEditText;
	private int cyear, cmonth, cday;
	private DatePickerDialog dialog = null;
	private int billId;
	private String obillName;
	private float obillAmount;
	private long obillDueDate;
	private int currencyDecimals = 2;
	private String decimalFlag;
	
	private PersonalDatabase pdb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		obillName = intent.getStringExtra(BillActivity.BILL_NAME);
		billId = intent.getIntExtra(BillActivity.BILL_ID,0);
		
		String new_title= obillName +" - Edit";
		this.setTitle(new_title);
		setContentView(R.layout.activity_add_bill);

		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";
		
		pdb = PersonalDatabase.get(this);
		dateDisplay = (TextView) findViewById(R.id.dateDisplay);
		dueDateButton = (Button) findViewById(R.id.billDueDate);
		
		billAmountEditText = (EditText) findViewById(R.id.billAmount);
		billNameEditText = (AutoCompleteTextView) findViewById(R.id.addBillName);
		billNameEditText.setThreshold(1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, billNames);
		billNameEditText.setAdapter(adapter);
		
		setValues();
		addButtonListener();
		
		//addItemsOnCategorySpinner();
		doneButton = (Button) findViewById(R.id.billDoneButton);
		doneButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	doneEditBill(v);
		    }
		});
	}
	
	public void setValues() {
		Cursor mquery = pdb.getBillDetails(billId);
		mquery.moveToFirst();
		obillAmount = mquery.getFloat(2);
		obillDueDate = mquery.getLong(3);
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(obillDueDate);
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH);
		cday = c.get(Calendar.DAY_OF_MONTH);
		
		billNameEditText.setText(obillName);
		billAmountEditText.setText(String.format(decimalFlag, obillAmount));
		dateDisplay.setText(new StringBuilder().append(cday).append("-").append(cmonth+1).append("-").append(cyear));
	}
	
	public void addButtonListener() {
		dueDateButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar c = null;
				String preExistingDate = (String) dateDisplay.getText().toString();
				String iyear, imonth, iday;
				
				if(preExistingDate != null && !preExistingDate.equals("")) {
					StringTokenizer st = new StringTokenizer(preExistingDate,"-");
					iday = st.nextToken();
					imonth = st.nextToken();
					iyear = st.nextToken();
					if(dialog == null) {
						dialog = new DatePickerDialog(v.getContext(), new PickDate(),
								Integer.parseInt(iyear), Integer.parseInt(imonth)-1, Integer.parseInt(iday));
					}
					dialog.updateDate(Integer.parseInt(iyear), Integer.parseInt(imonth)-1, Integer.parseInt(iday));
				}
				dialog.show();
			}
		});
	}

	public class PickDate implements DatePickerDialog.OnDateSetListener {

		public void onDateSet(DatePicker view, int pyear, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			view.updateDate(pyear, monthOfYear, dayOfMonth);
			int monthOffset = monthOfYear+1;
			dateDisplay.setText(dayOfMonth+"-"+monthOffset+"-"+pyear);
		}
		
	}
	/*protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DATE_DIALOG_ID:
			return DatePickerDialog(this, datePickerListener, year, month, day)
		}
	}*/
	
	public void doneEditBill(View v){
		//AutoCompleteTextView bill = (AutoCompleteTextView) findViewById(R.id.addBillName);
		String billName = billNameEditText.getText().toString();
		if(billName.equals("")){
			createToast("Error! Cannot leave the bill name empty");
			return;
		}
		
		EditText amountText = (EditText) findViewById(R.id.billAmount);
		if (amountText.getText().toString().equals("")) {
			createToast("Error! Cannot leave the amount field empty");
			return;
		}
		
		float amount = Float.valueOf(amountText.getText().toString());
		if (amount == 0) {
			createToast("Error! Cannot have a zero amount bill");
			return;
		}
		
		String billDueDate = dateDisplay.getText().toString();
		StringTokenizer st = new StringTokenizer(billDueDate,"-");
		//day = Integer.parseInt(st.nextToken());
		//month = Integer.parseInt(st.nextToken());
		//year = Integer.parseInt(st.nextToken());
		SimpleDateFormat f = new SimpleDateFormat("d-M-yyyy");
		Date d=null;
		try {
			d = f.parse(billDueDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("nik", "error", e);
		}
		long dueDateMsec = d.getTime();
		if(!checkDueDate(dueDateMsec)) {
			createToast("Error! Due Date must be after today");
			return;
		}

		try {
			pdb.updateBillsTable(billId, billName, amount, dueDateMsec);
		} catch (Exception err) {
			Log.e("adi", "error", err);
		}
		this.finish();
	}
	
	public boolean checkDueDate(long d) {
		final Calendar c = Calendar.getInstance();
		long cd = c.getTimeInMillis();
		if(d<cd) return false;
		else return true;
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(EditBillActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
