package com.industry.printer;

import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.ui.ExtendMessageTitleFragment;
//import com.android.internal.app.LocalePicker;
//import android.app.TabActivity;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	public static final String TAG="MainActivity";
	public static final String ACTION_USB_PERMISSION="com.industry.printer.USB_PERMISSION";
	
	public Context mContext;
	
	TabHost mTab;
	
	public RadioButton	mRadioCtl;
	public RadioButton	mRadioSet;
	public RadioButton	mRadioEdit;
	public TextView		mExtStatus;
	
	public ControlTabActivity 	mControlTab;
	public EditTabActivity		mEditTab;
	public SettingsTabActivity	mSettingsTab;
	
	public TextView mCtrlTitle;
	public TextView mEditTitle;
	public TextView mSettingTitle;
	
	
	static {
		System.loadLibrary("Hardware_jni");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setLocale();
		setContentView(R.layout.activity_main);
		boolean isroot=false;
		mContext = getApplicationContext();
		/*初始化系统配置*/
		Configs.initConfigs(mContext);
		
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
	    
	    //mExtStatus = (TextView) findViewById(R.id.tv_counter_msg);
	    
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
	
//	public void setLocale()
//	{
//		Configuration config = getResources().getConfiguration(); 
//		DisplayMetrics dm = getResources().getDisplayMetrics(); 
//		config.locale = Locale.SIMPLIFIED_CHINESE; 
//		getResources().updateConfiguration(config, dm); 
//		LocalePicker.updateLocale(Locale.CHINA);
//	}
	
	private void initView() {
		mCtrlTitle = (TextView) findViewById(R.id.ctrl_counter_view);
		mEditTitle = (TextView) findViewById(R.id.edit_message_view);
		mSettingTitle = (TextView) findViewById(R.id.setting_ext_view);
		
		
		mControlTab = new ControlTabActivity();
		mEditTab = new EditTabActivity();
		mSettingsTab = new SettingsTabActivity();
		
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		transaction.replace(R.id.tab_content, mControlTab);
//		transaction.commit();
		transaction.add(R.id.tab_content, mControlTab);
		transaction.add(R.id.tab_content, mEditTab);
		transaction.add(R.id.tab_content, mSettingsTab);
		// transaction.add(R.id.tv_counter_msg, mCtrlTitle);
		// transaction.add(R.id.tv_counter_msg, mEditTitle);
		// transaction.add(R.id.tv_counter_msg, mSettingTitle);
		transaction.commit();
		transaction.hide(mEditTab);
		transaction.hide(mSettingsTab);
		transaction.show(mControlTab);
//		transaction.commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		FragmentTransaction fts = getFragmentManager().beginTransaction();
		switch (arg0.getId()) {
			case R.id.btn_control:
				if(arg1 == true) {
					fts.show(mControlTab);
					mCtrlTitle.setVisibility(View.VISIBLE);
				} else {
					fts.hide(mControlTab);
					mCtrlTitle.setVisibility(View.GONE);
				}
				
				Debug.d(TAG, "====>control checked?"+arg1);
				break;
			case R.id.btn_edit:
				Debug.d(TAG, "====>edit checked?"+arg1);
				if( arg1 == true) {
					fts.show(mEditTab);
					mEditTitle.setVisibility(View.VISIBLE);
				} else {
					fts.hide(mEditTab);
					mEditTitle.setVisibility(View.GONE);
				}
				break;
			case R.id.btn_setting:
				Debug.d(TAG, "====>setting checked?"+arg1);
				
				if (arg1 == true) {
					fts.show(mSettingsTab);
					mSettingTitle.setVisibility(View.VISIBLE);
				} else {
					fts.hide(mSettingsTab);
					mSettingTitle.setVisibility(View.GONE);
				}
				break;
		}
		fts.commit();
	}
}
