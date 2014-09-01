package com.AndroidFriends.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

public class EditPersonalInfoActivity extends Activity {

	private String name = "";
	private int currency, currencyDecimals, incomeFlag;
	private float income;
	private Spinner currencySpinner;
	private Button doneButton, addIncomeButton, removeIncomeButton;
	private LinearLayout incomeLayout;
	private PersonalDatabase pdb;
	private CommonDatabase cdb;
	private String decimalFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent  intent = getIntent();
		name = intent.getStringExtra(PersonalActivity.personalName);
		incomeFlag = intent.getIntExtra(PersonalActivity.personalIncomeFlag, PersonalDatabase.noIncomeFlag);
		income = intent.getFloatExtra(PersonalActivity.personalIncome, 0);
		currency = intent.getIntExtra(PersonalActivity.personalCurrency, 1);
		currencyDecimals = intent.getIntExtra(PersonalActivity.personalCurrencyDecimals, 0);
		decimalFlag = "%." + currencyDecimals + "f";

		setContentView(R.layout.activity_add_personal_info);

		pdb = PersonalDatabase.get(this);
		cdb = CommonDatabase.get(this);

		currencySpinner = (Spinner) findViewById(R.id.personalSpinnerCurrency);
		doneButton = (Button) findViewById(R.id.personalDoneButton);
		addIncomeButton = (Button) findViewById(R.id.personalAddIncomeButton);
		removeIncomeButton = (Button) findViewById(R.id.personalRemoveIncomeButton);
		incomeLayout = (LinearLayout) findViewById(R.id.personalIncomeLayout);
		
		doneButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	doneEditInfo(v);
		    }
		});

		addExistingInfo();
	}

	public void addExistingInfo() {
		EditText personalNameEditText = (EditText) findViewById(R.id.personalAddName);
		personalNameEditText.setText(name);

		addItemsOnCurrencySpinner();
		currencySpinner.setSelection(currency-1);
	
		if (incomeFlag == PersonalDatabase.incomeFlag) {
			incomeLayout.setVisibility(View.VISIBLE);
			removeIncomeButton.setVisibility(View.VISIBLE);
			addIncomeButton.setVisibility(View.GONE);
			
			EditText incomeText = (EditText) incomeLayout.findViewById(R.id.personalIncome);
			incomeText.setText(String.format(decimalFlag, income));
		}
		else {
			incomeLayout.setVisibility(View.GONE);
			removeIncomeButton.setVisibility(View.GONE);
			addIncomeButton.setVisibility(View.VISIBLE);
		}
	}

	public void addItemsOnCurrencySpinner() {
		String[] currencyList = cdb.getCurrencies();
		List<String> list = new ArrayList<String>(Arrays.asList(currencyList));
		
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
	
	public void doneEditInfo(View v){
		EditText personalName = (EditText) findViewById(R.id.personalAddName);
		String name = personalName.getText().toString();
		if(name.equals("")){
			createToast("Error! Cannot leave the name field empty");
			return;
		}
		
		int currency = currencySpinner.getSelectedItemPosition() + 1;
		
		if (incomeFlag == PersonalDatabase.incomeFlag) {
			EditText incomeText = (EditText) incomeLayout.findViewById(R.id.personalIncome);
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
				pdb.updateInfoTable(name, currency, incomeAmount);
			} catch (Exception e) {}
		}
		else {
			try {
				pdb.updateInfoTable(name, currency, 0);
			} catch (Exception e) {}
		}
		this.finish();
	}
	
	public void createToast(String message){
		Toast n = Toast.makeText(EditPersonalInfoActivity.this,message, Toast.LENGTH_LONG);
		n.setGravity(Gravity.CENTER_VERTICAL,0,0);
		n.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
