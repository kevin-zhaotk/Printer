package com.industry.printer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.industry.printer.Utils.Debug;

import android.os.Bundle;
//import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.os.SystemProperties;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodInfo;

public class MainActivity extends TabActivity {

	public static final String TAG="MainActivity";
	TabHost mTab;
	
	
	static {
		System.loadLibrary("UsbSerial_jni");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLocale();
		setContentView(R.layout.activity_main);
		boolean isroot=false;
		/*get write permission of ttyACM0*/
		//System.setProperty("ctl.start", "mptty");
		SystemProperties.set("ctl.start","mptty");
		//InputMethodService inputService =
		String pinyinId = "";
		InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputMethodInfos = inputManager.getInputMethodList();

	    for (InputMethodInfo inputMethodInfo : inputMethodInfos) {
	    		Debug.d(TAG, "inputMethod="+inputMethodInfo.getId());
	            if (inputMethodInfo.getId().equals("com.android.inputmethod.pinyin/.PinyinIME")) {
	                    inputManager.setInputMethod(null, inputMethodInfo.getId());
	            }
	    }
	    
		/*
		try {
			isroot = LinuxShell.isRoot(Runtime.getRuntime(), 50);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		Debug.d(TAG, "ControlTab get root "+ isroot);
		mTab = getTabHost();
		
		
		mTab.addTab(mTab.newTabSpec("Control").setIndicator(getResources().getString(R.string.ControlTab)).setContent(new Intent(this, ControlTabActivity.class)));
		mTab.addTab(mTab.newTabSpec("manualCtrl").setIndicator(getResources().getString(R.string.manualCtrlTab)).setContent(new Intent(this, ManualCtrlActivity.class)));
		//mTab.addTab(mTab.newTabSpec("Edit").setIndicator(getResources().getString(R.string.Edit)).setContent(new Intent(this, EditTabActivity.class)));
		mTab.addTab(mTab.newTabSpec("Settings").setIndicator(getResources().getString(R.string.Settings)).setContent(new Intent(this, SettingsTabActivity.class)));
		//mTab.addTab(mTab.newTabSpec("Control_man").setIndicator("Control_man").setContent(new Intent(this, ControlManTabActivity.class)));
		/*adjust control tab title*/
		for(int i=0;i<3; i++)
		{
			TextView v1= (TextView) mTab.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			v1.setTextSize(30);
			v1.setGravity(Gravity.CENTER_VERTICAL);
		}
		
		//set current tab
		mTab.setCurrentTab(0);
		
		IntentFilter filter = new IntentFilter();
		//filter.addDataScheme("file");
		filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(PrinterBroadcastReceiver.BOOT_COMPLETED);
		PrinterBroadcastReceiver mReceiver = new PrinterBroadcastReceiver();
		Context mContext = this.getApplicationContext();
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void drawObjects()
	{
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // TODO Auto-generated method stub
	 
	}

	public void setLocale()
	{
		Configuration config = getResources().getConfiguration(); 
		DisplayMetrics dm = getResources().getDisplayMetrics(); 
		config.locale = Locale.SIMPLIFIED_CHINESE; 
		getResources().updateConfiguration(config, dm); 
		
	}
}
