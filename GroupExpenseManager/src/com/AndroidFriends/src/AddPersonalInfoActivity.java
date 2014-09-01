package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.AndroidFriends.R;

public class AddPersonalInfoActivity extends Activity {

	private Spinner currencySpinner;
	private Button doneButton, addIncomeButton, removeIncomeButton;
	private LinearLayout incomeLayout;
	private int incomeFlag = PersonalDatabase.noIncomeFlag;
	
	private PersonalDatabase pdb;
	private CommonDatabase cdb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_personal_info);

		pdb = PersonalDatabase.get(this);
		cdb = CommonDatabase.get(this);
		
		addItemsOnCurrencySpinner();
		addIncomeButton = (Button) findViewById(R.id.personalAddIncomeButton);
		removeIncomeButton = (Button) findViewById(R.id.personalRemoveIncomeButton);
		incomeLayout = (LinearLayout) findViewById(R.id.personalIncomeLayout);
		
		incomeLayout.setVisibility(View.GONE);
		removeIncomeButton.setVisibility(View.GONE);
		addIncomeButton.setVisibility(View.VISIBLE);
		
		doneButton = (Button) findViewById(R.id.personalDoneButton);
		doneButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	doneAddInfo(v);
		    }
		});
	}
		
	public void addItemsOnCurrencySpinner() {
		currencySpinner = (Spinner) findViewById(R.id.personalSpinnerCurrency);
		List<String> list = new ArrayList<String>();
		list.add("Select Currency");
		String[] currencyList = cdb.getCurrencies();
		for (int i=0; i<currencyList.length; i++) {
			list.add(currencyList[i]);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencySpinner.setAdapter(dataAdapter);
		currencySpinner.setPrompt("Select currency");
	}
	
	public void addIncomeLayouts(View v) {
		incomeFlag = PersonalDatabase.incomeFlag;
		incomeLayout.setVisibility(View.VISIBLE);
		removeIncomeButton.setVisibility(View.VISIBLE);
		addIncomeButton.setVisibility(View.GONE);
	}
	
	public void removeIncomeLayouts(View v) {
		incomeFlag = PersonalDatabase.noIncomeFlag;
		incomeLayout.setVisibility(View.GONE);
		removeIncomeButton.setVisibility(View.GONE);
		addIncomeButton.setVisibility(View.VISIBLE);
	}
	
	public void doneAddInfo(View v) {
		EditText personalName = (EditText) findViewById(R.id.personalAddName);
		String name = personalName.getText().toString();
		if(name.equals("")){
			createToast("Error! Cannot leave the name field empty");
			return;
		}
		
		int currency = currencySpinner.getSelectedItemPosition();
		if (currency == 0) {
			createToast("Error! Please select a currency");
			return;
		}
		
		if (incomeFlag == PersonalDatabase.incomeFlag) {
			EditText incomeText = (EditText) findViewById(R.id.personalIncome);
			if (incomeText.getText().toString().equals("")) {
				createToast("Error! Cannot leave the income amount field empty");
				return;
			}
			float incomeAmount = Float.valueOf(incomeText.getText().toString());
			if (incomeAmount == 0) {
				createToast("Error! Cannot have a zero income amount");
				return;
			}
			try {
				pdb.insertInfoWithIncome(name, currency, incomeAmount);
			} catch (Exception e) {}
		}
		else {
			try {
				pdb.insertInfoWithoutIncome(name, currency);
			} catch (Exception e) {}
		}
		this.finish();
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(AddPersonalInfoActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
