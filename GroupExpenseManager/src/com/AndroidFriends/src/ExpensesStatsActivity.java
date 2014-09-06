package com.AndroidFriends.src;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AndroidFriends.R;

@SuppressLint("NewApi")
public class ExpensesStatsActivity extends Activity {
    private TextView foodText, healthText, entertainmentText, travelText, shoppingText, accommodationText, billsText, groupsText, miscellaneousText;
    private LinearLayout pieChartLayout;
    private int size, chartSize, xmarginSize;
    private int percentageValues[];
    private float angleValues[];
    PersonalDatabase pdb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_stats);
        
        foodText = (TextView) findViewById(R.id.foodTextView);
        healthText = (TextView) findViewById(R.id.healthTextView);
        entertainmentText = (TextView) findViewById(R.id.entertainmentTextView);
        travelText = (TextView) findViewById(R.id.travelTextView);
        shoppingText = (TextView) findViewById(R.id.shoppingTextView);
        accommodationText = (TextView) findViewById(R.id.accommodationTextView);
        billsText = (TextView) findViewById(R.id.billsTextView);
        groupsText = (TextView) findViewById(R.id.groupsTextView);
        miscellaneousText = (TextView) findViewById(R.id.miscellaneousTextView);
        
        pdb = PersonalDatabase.get(this);
        
        pieChartLayout = (LinearLayout) findViewById(R.id.expensePieChartLayout);
        angleValues = new float[9];
        percentageValues = new int[9];
        calculateData();
        
        setColours();
        setValues();
        
        Point screenSize = new Point();
        Display d = getWindowManager().getDefaultDisplay();
        d.getSize(screenSize);
        size = screenSize.x < (screenSize.y/2) ? screenSize.x : (screenSize.y/2);
        chartSize = (int) (size * 0.7);
        xmarginSize = (screenSize.x-chartSize) / 2;
        
        LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        View pieChartView = (new BalanceActivity()).new MyGraphView(this, angleValues, xmarginSize, 0, chartSize+xmarginSize, chartSize);
        pieChartLayout.addView(pieChartView, mLayoutParams);
    }
    
    private void setColours() {
    	foodText.setTextColor(BalanceActivity.COLORS[0]);
    	healthText.setTextColor(BalanceActivity.COLORS[1]);
    	entertainmentText.setTextColor(BalanceActivity.COLORS[2]);
    	travelText.setTextColor(BalanceActivity.COLORS[3]);
    	accommodationText.setTextColor(BalanceActivity.COLORS[4]);
    	shoppingText.setTextColor(BalanceActivity.COLORS[5]);
    	billsText.setTextColor(BalanceActivity.COLORS[6]);
    	groupsText.setTextColor(BalanceActivity.COLORS[7]);
    	miscellaneousText.setTextColor(BalanceActivity.COLORS[8]);
    }
    
    private void setValues() {
    	foodText.setText("Food - " + percentageValues[0] + "%");
    	healthText.setText("Health - " + percentageValues[1] + "%");
    	entertainmentText.setText("Entertainment - " + percentageValues[2] + "%");
    	travelText.setText("Travel - " + percentageValues[3] + "%");
    	accommodationText.setText("Accommodation - " + percentageValues[4] + "%");
    	shoppingText.setText("Shopping - " + percentageValues[5] + "%");
    	billsText.setText("Bills - " + percentageValues[6] + "%");
    	groupsText.setText("Group Expenses - " + percentageValues[7] + "%");
    	miscellaneousText.setText("Miscellaneous - " + percentageValues[8] + "%");
    }
    
    private void calculateData() {
        float total = pdb.getTotalExpenses();
        int lastNonZero = 0;
        for (int i=0; i<9; i++) {
        	float expenses = pdb.getTotalCategoryExpenses(i+1);
        	if (expenses != 0) lastNonZero = i;
        	angleValues[i] = 360 * expenses/total;
        	percentageValues[i] = Math.round(100 * expenses/total);
        }
        int percentageTotal = 0;
        for (int i=0; i<lastNonZero; i++) {
        	percentageTotal += percentageValues[i];
        }
        percentageValues[lastNonZero] = 100 - percentageTotal;
    }
}