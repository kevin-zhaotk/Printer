package com.industry.printer;


import java.util.Calendar;

import com.industry.printer.Utils.Debug;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class CalendarDialog extends Dialog {

	public static final String TAG="CalendarDialog"; 
	private int mLayout;
	public Button mPositive;
	public Button mNegative;
	public DatePicker mDPicker;
	public TimePicker mTPicker;
	
	protected CalendarDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	public  CalendarDialog(Context context, int resLayout)
	{
		super(context);
		mLayout = resLayout;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(mLayout);
		
		mDPicker = (DatePicker) findViewById(R.id.picker_date);
		mTPicker = (TimePicker) findViewById(R.id.picker_time);
		Calendar c = Calendar.getInstance();
		mDPicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
		mTPicker.setIs24HourView(true);
		mPositive = (Button) findViewById(R.id.btn_setTimeOk);
		mPositive.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar c = Calendar.getInstance();
				Debug.d(TAG, "hour="+mTPicker.getCurrentHour().intValue()+", minute="+ mTPicker.getCurrentMinute().intValue());
				c.set(mDPicker.getYear(), mDPicker.getMonth(), mDPicker.getDayOfMonth(), 
						mTPicker.getCurrentHour().intValue(), mTPicker.getCurrentMinute().intValue());
				long when = c.getTimeInMillis();

		        if (when / 1000 < Integer.MAX_VALUE) {
		            SystemClock.setCurrentTimeMillis(when);
		        }
				dismiss();
			}
		});
		
		mNegative =  (Button) findViewById(R.id.btn_setTimeCnl);
		mNegative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}

	
}
