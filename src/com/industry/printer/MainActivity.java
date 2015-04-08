package com.industry.printer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.FpgaGpioOperation;

import com.android.internal.app.LocalePicker;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.PendingIntent;
//import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.os.SystemProperties;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodInfo;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	public static final String TAG="MainActivity";
	public static final String ACTION_USB_PERMISSION="com.industry.printer.USB_PERMISSION";
	
	TabHost mTab;
	
	public RadioButton	mRadioCtl;
	public RadioButton	mRadioSet;
	public RadioButton	mRadioEdit;
	
	public ControlTabActivity 	mControlTab;
	public EditTabActivity		mEditTab;
	public SettingsTabActivity	mSettingsTab;
	
	static {
		System.loadLibrary("Hardware_jni");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLocale();
		setContentView(R.layout.activity_main);
		boolean isroot=false;
		Context context = getApplicationContext();
		/*初始化系统配置*/
		Configs.initConfigs(context);
		
		/*get write permission of ttyACM0*/
		//SystemProperties.set("ctl.start","mptty");

		String pinyinId = "";
		InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputMethodInfos = inputManager.getInputMethodList();

	    for (InputMethodInfo inputMethodInfo : inputMethodInfos) {
	    		Debug.d(TAG, "inputMethod="+inputMethodInfo.getId());
	            if (inputMethodInfo.getId().equals("com.android.inputmethod.latin/.LatinIME")) {
	                    inputManager.setInputMethod(null, inputMethodInfo.getId());
	            }
	    }
	    
	    mRadioCtl = (RadioButton) findViewById(R.id.btn_control);
	    mRadioCtl.setOnCheckedChangeListener(this);
	    
	    mRadioEdit = (RadioButton) findViewById(R.id.btn_edit);
	    mRadioEdit.setOnCheckedChangeListener(this);
	    
	    mRadioSet = (RadioButton) findViewById(R.id.btn_setting);
	    mRadioSet.setOnCheckedChangeListener(this);
	    
	    
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

		//mTab = getTabHost();
		
		//mTab.addTab(mTab.newTabSpec("Control").setIndicator(getResources().getString(R.string.ControlTab)).setContent(new Intent(this, ControlTabActivity.class)));
		//mTab.addTab(mTab.newTabSpec("Edit").setIndicator(getResources().getString(R.string.Edit)).setContent(new Intent(this, EditTabActivity.class)));
		//mTab.addTab(mTab.newTabSpec("Settings").setIndicator(getResources().getString(R.string.Settings)).setContent(new Intent(this, SettingsTabActivity.class)));
		/*adjust control tab title*/
		//for(int i=0;i<3; i++)
		//{
		//	TextView v1= (TextView) mTab.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
		//	v1.setTextSize(40);
		//	v1.setGravity(Gravity.CENTER);
		//}
		
		//set current tab
		//mTab.setCurrentTab(0);
		
		IntentFilter filter = new IntentFilter();
		//filter.addDataScheme("file");
		//filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(PrinterBroadcastReceiver.BOOT_COMPLETED);
		
		
		PrinterBroadcastReceiver mReceiver = new PrinterBroadcastReceiver();
		Context mContext = this.getApplicationContext();
		mContext.registerReceiver(mReceiver, filter);
		
		FpgaGpioOperation.updateSettings(this.getApplicationContext());
		
		initView();
		
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
		LocalePicker.updateLocale(Locale.CHINA);
	}
	
	private void initView() {
		mControlTab = new ControlTabActivity();
		mEditTab = new EditTabActivity();
		mSettingsTab = new SettingsTabActivity();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		transaction.replace(R.id.tab_content, mControlTab);
//		transaction.commit();
		transaction.add(R.id.tab_content, mControlTab);
		transaction.add(R.id.tab_content, mEditTab);
		transaction.add(R.id.tab_content, mSettingsTab);
		transaction.commit();
		transaction.hide(mEditTab);
		transaction.hide(mSettingsTab);
		transaction.show(mControlTab);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		FragmentTransaction fts = getFragmentManager().beginTransaction();
		switch (arg0.getId()) {
			case R.id.btn_control:
				if(arg1 == true) {
					fts.show(mControlTab);
				} else {
					fts.hide(mControlTab);
				}
				
				Debug.d(TAG, "====>control checked?"+arg1);
				break;
			case R.id.btn_edit:
				Debug.d(TAG, "====>edit checked?"+arg1);
				if( arg1 == true) {
					fts.show(mEditTab);
				} else {
					fts.hide(mEditTab);
				}
				break;
			case R.id.btn_setting:
				Debug.d(TAG, "====>setting checked?"+arg1);
				
				if (arg1 == true) {
					fts.show(mSettingsTab);
				} else {
					fts.hide(mSettingsTab);
				}
				break;
		}
		fts.commit();
	}
}
