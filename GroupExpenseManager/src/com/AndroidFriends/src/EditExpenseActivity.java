package com.AndroidFriends.src;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

public class EditExpenseActivity extends Activity {

	private String expenseName = "";
	private int expenseId = 0, expenseTypeFlag;
	private int currencyDecimals = 2;
	private Spinner categorySpinner;
	private Button doneButton;
	private PersonalDatabase pdb;
	private String decimalFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent  intent = getIntent();
		expenseName = intent.getStringExtra(ExpenseActivity.EXPENSE_NAME);
		expenseId = intent.getIntExtra(ExpenseActivity.EXPENSE_ID,0);

		currencyDecimals = intent.getIntExtra(GroupSummaryActivity.stringDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";

		String new_title= expenseName +" - Edit";
		this.setTitle(new_title);
		setContentView(R.layout.activity_add_expense);

		pdb = PersonalDatabase.get(this);

		AutoCompleteTextView expenseNameEditText = (AutoCompleteTextView) findViewById(R.id.addExpenseName);
		expenseNameEditText.setThreshold(1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, AddEventActivity.eventNames);
		expenseNameEditText.setAdapter(adapter);
		expenseNameEditText.setText(expenseName);

		categorySpinner = (Spinner) findViewById(R.id.spinnerExpenseCategory);
		doneButton = (Button) findViewById(R.id.expenseDoneButton);

		editExpense();
	}

	public void editExpense() {
		doneButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doneEditExpense(v);
			}
		});
		
		addItemsOnCategorySpinner();

		Cursor mquery = pdb.getExpenseDetails(expenseId);
		mquery.moveToFirst();
		categorySpinner.setSelection(mquery.getInt(2)-1);
		
		float amount = mquery.getFloat(3);
		EditText et = (EditText) findViewById(R.id.expenseAmount);
		et.setText(String.format(decimalFlag, amount));
		
		expenseTypeFlag = mquery.getInt(5);
	}

	public void addItemsOnCategorySpinner() {
		List<String> list = pdb.getCategoryNames();
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(dataAdapter);
		categorySpinner.setPrompt("Select expense category");
	}
	
	public void doneEditExpense(View v){
		AutoCompleteTextView expense = (AutoCompleteTextView) findViewById(R.id.addExpenseName);
		String expenseName = expense.getText().toString();
		if(expenseName.equals("")){
			createToast("Error! Cannot leave the expense name empty");
			return;
		}
		
		String editString = "Edited - ";
		
		if (expenseTypeFlag == PersonalDatabase.expenseFlag){
			expenseName = editString + expenseName;
		}
		
		int category = categorySpinner.getSelectedItemPosition() + 1;
		
		EditText amountText = (EditText) findViewById(R.id.expenseAmount);
		if (amountText.getText().toString().equals("")) {
			createToast("Error! Cannot leave the amount field empty");
			return;
		}
		float amount = Float.valueOf(amountText.getText().toString());
		if (amount == 0) {
			createToast("Error! Cannot have a zero amount expense");
			return;
		}

		try {
			pdb.updateExpensesTable(expenseId, expenseName, category, amount);
		} catch (Exception err) {
			Log.e("adi", "error", err);
		}
		this.finish();
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(EditExpenseActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
