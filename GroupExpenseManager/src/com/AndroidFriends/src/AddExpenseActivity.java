package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

public class AddExpenseActivity extends Activity {

	private Spinner categorySpinner;
	private Button doneButton;
	
	private PersonalDatabase pdb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expense);

		pdb = PersonalDatabase.get(this);
		
		AutoCompleteTextView expenseNameEditText = (AutoCompleteTextView) findViewById(R.id.addExpenseName);
		expenseNameEditText.setThreshold(1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, AddEventActivity.eventNames);
		expenseNameEditText.setAdapter(adapter);
		
		addItemsOnCategorySpinner();
		doneButton = (Button) findViewById(R.id.expenseDoneButton);
		doneButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	doneAddExpense(v);
		    }
		});
	}
		
	public void addItemsOnCategorySpinner() {
		categorySpinner = (Spinner) findViewById(R.id.spinnerExpenseCategory);
		List<String> list = new ArrayList<String>();
		list.add("Select category");
		ArrayList<String> categoryList = pdb.getCategoryNames();
		for (int i=0; i<categoryList.size(); i++) {
			list.add(categoryList.get(i));
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(dataAdapter);
		categorySpinner.setPrompt("Select expense category");
	}
	
	public void doneAddExpense(View v){
		AutoCompleteTextView expense = (AutoCompleteTextView) findViewById(R.id.addExpenseName);
		String expenseName = expense.getText().toString();
		if(expenseName.equals("")){
			createToast("Error! Cannot leave the expense name empty");
			return;
		}
		
		int category = categorySpinner.getSelectedItemPosition();
		if (category == 0) {
			createToast("Error! Please select a category for the expense");
			return;
		}
		
		EditText amountText = (EditText) findViewById(R.id.expenseAmount);
		float amount = Float.valueOf(amountText.getText().toString());

		try {
			pdb.insertExpense(expenseName, category, amount);
		} catch (Exception err) {
			Log.e("adi", "error", err);
		}
		this.finish();
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(AddExpenseActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
