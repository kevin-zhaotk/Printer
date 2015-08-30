package com.industry.printer.ui.CustomerDialog;


import java.util.Calendar;
import java.util.logging.Logger;

import com.industry.printer.R;
import com.industry.printer.R.id;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.RTCDevice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
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
		Debug.d(TAG, "--->context="+context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(mLayout);
		
		mDPicker = (DatePicker) findViewById(R.id.picker_date);
		mTPicker = (TimePicker) findViewById(R.id.picker_time);
		Calendar c = Calendar.getInstance();
		mDPicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
		mTPicker.setIs24HourView(true);
		mTPicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		mTPicker.setCurrentMinute(c.get(Calendar.MINUTE));
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
		        RTCDevice rtcDevice = RTCDevice.getInstance();
		        rtcDevice.syncSystemTimeToRTC(getContext());
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
