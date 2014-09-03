package com.AndroidFriends.src;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AndroidFriends.R;

@SuppressLint("NewApi")
public class BalanceActivity extends Activity {
    private TextView expensePercentageText, billsPercentageText, balancePercentageText;
    private LinearLayout pieChartLayout;
    private int size, chartSize, xmarginSize;
	float expenses, income, bills;
    float values[];
    public final static int[] COLORS = {Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.LTGRAY, Color.MAGENTA, Color.CYAN, Color.WHITE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        
        Intent intent = getIntent();
        expenses = intent.getFloatExtra(PersonalActivity.personalExpenses, 0);
        income = intent.getFloatExtra(PersonalActivity.personalIncome, 0);
        bills = intent.getFloatExtra(PersonalActivity.personalBills, 0);
        
        expensePercentageText = (TextView) findViewById(R.id.expensePercentageText);
        billsPercentageText = (TextView) findViewById(R.id.billsPercentageText);
        balancePercentageText = (TextView) findViewById(R.id.balancePercentageText);
        pieChartLayout = (LinearLayout) findViewById(R.id.balancePieChartLayout);
        values = calculateData();
        
        Point screenSize = new Point();
        Display d = getWindowManager().getDefaultDisplay();
        d.getSize(screenSize);
        size = screenSize.x < (screenSize.y/2) ? screenSize.x : (screenSize.y/2);
        chartSize = (int) (size * 0.7);
        xmarginSize = (screenSize.x - chartSize) / 2;

        LayoutParams mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        View pieChartView = new MyGraphView(this, values, xmarginSize, 0, chartSize+xmarginSize, chartSize);
        pieChartLayout.addView(pieChartView, mLayoutParams);
        
        int expensesPercentage = (int) (100 * expenses/income);
        expensePercentageText.setText("Current expenses are " + String.valueOf(expensesPercentage) + "% of your income");
        expensePercentageText.setTextColor(Color.RED);
        
        int billsPercentage = (int) (100 * bills/income);
        billsPercentageText.setText("Due bills are " + String.valueOf(billsPercentage) + "% of your income");
        billsPercentageText.setTextColor(Color.YELLOW);
        
        int balancePercentage = 100 - expensesPercentage - billsPercentage;
        balancePercentageText.setText("Remaining balance is " + String.valueOf(balancePercentage) + "% of your income");
        balancePercentageText.setTextColor(Color.GREEN);
    }
    
    private float[] calculateData() {
        float[] data = new float[3];
        data[2] = 360 * (expenses)/income;
        data[1] = 360 * bills/income;
        data[0] = 360 - data[1] - data[2];
        return data;
    }
    
    // Class for pie chart
    public class MyGraphView extends View
    {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;
        RectF rectf;
        int temp=0;
        int lastNonZero;
        
		public MyGraphView(Context context, float[] values, int xstart, int ystart, int xend, int yend) {
            super(context);
            rectf = new RectF(xstart, ystart, xend, yend);
            value_degree = new float[values.length];
            for(int i=0;i<values.length;i++) {
                value_degree[i]=values[i];
                if (values[i] != 0) lastNonZero = i;
            }
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (int i = 0; i < value_degree.length; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } 
                else if (i == lastNonZero) {
                    temp += (int) value_degree[i - 1];
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, temp, 360-temp, true, paint);
                }
                else {
                	temp += (int) value_degree[i - 1];
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        }
    }
}