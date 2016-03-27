package com.industry.printer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.hardware.PWMAudio;
import com.industry.printer.ui.ExtendMessageTitleFragment;
//import com.android.internal.app.LocalePicker;
//import android.app.TabActivity;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnTouchListener {

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
	public EditMultiTabActivity mEditFullTab;
	public EditTabSmallActivity mEditSmallTab;
	public SettingsTabActivity	mSettingsTab;
	
	public TextView mCtrlTitle;
	public TextView mEditTitle;
	public LinearLayout mSettings;
	public TextView mSettingTitle;
	public TextView mVersion;
	
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
		
		
		/*get write permission of ttyACM0*/
		//SystemProperties.set("ctl.start","mptty");

		String pinyinId = "";
		InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputMethodInfos = inputManager.getInputMethodList();

//	    for (InputMethodInfo inputMethodInfo : inputMethodInfos) {
//	    		Debug.d(TAG, "inputMethod="+inputMethodInfo.getId());
//	            if (inputMethodInfo.getId().equals("com.android.inputmethod.pinyin/.PinyinIME")) {
//	                    inputManager.setInputMethod(getApplicationContext(), inputMethodInfo.getId());
//	            }
//	    }
	    
	    mRadioCtl = (RadioButton) findViewById(R.id.btn_control);
	    mRadioCtl.setOnCheckedChangeListener(this);
	    mRadioCtl.setOnTouchListener(this);
	    
	    mRadioEdit = (RadioButton) findViewById(R.id.btn_edit);
	    mRadioEdit.setOnCheckedChangeListener(this);
	    mRadioEdit.setOnTouchListener(this);
	    
	    mRadioSet = (RadioButton) findViewById(R.id.btn_setting);
	    mRadioSet.setOnCheckedChangeListener(this);
	    mRadioSet.setOnTouchListener(this);
	    
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
		filter.addDataScheme("file");
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(PrinterBroadcastReceiver.BOOT_COMPLETED);
		
		
		PrinterBroadcastReceiver mReceiver = new PrinterBroadcastReceiver(mHander);
		
		mContext.registerReceiver(mReceiver, filter);
		
		//FpgaGpioOperation.updateSettings(this.getApplicationContext());
		
		initView();
		Configs.initConfigs(mContext);
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
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {
		return ;
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
		mSettings = (LinearLayout) findViewById(R.id.settings_view);
		mSettingTitle = (TextView) findViewById(R.id.setting_ext_view);
		mVersion = (TextView) findViewById(R.id.setting_version);
		try {
			// InputStreamReader sReader = new InputStreamReader(getAssets().open("Version"));
			// BufferedReader reader = new BufferedReader(sReader);
			PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
			mVersion.setText(packageInfo.versionName);
		} catch (Exception e) {
			
		}
		mControlTab = new ControlTabActivity();
		mEditTab = new EditTabActivity();
		mEditSmallTab = new EditTabSmallActivity();
		mEditFullTab = new EditMultiTabActivity();
		mSettingsTab = new SettingsTabActivity();
		Debug.d(TAG, "===>initview");
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		transaction.replace(R.id.tab_content, mControlTab);
//		transaction.commit();
		transaction.add(R.id.tab_content, mControlTab);
		if (PlatformInfo.getEditType() == PlatformInfo.LARGE_SCREEN) {
			transaction.add(R.id.tab_content, mEditFullTab);
		} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_FULL) {
			transaction.add(R.id.tab_content, mEditSmallTab);
		} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_PART) {
			transaction.add(R.id.tab_content, mEditTab);
		}
		transaction.add(R.id.tab_content, mSettingsTab);
		Debug.d(TAG, "===>transaction");
		// transaction.add(R.id.tv_counter_msg, mCtrlTitle);
		// transaction.add(R.id.tv_counter_msg, mEditTitle);
		// transaction.add(R.id.tv_counter_msg, mSettingTitle);
		transaction.commit();
		if (PlatformInfo.getEditType() == PlatformInfo.LARGE_SCREEN) {
			transaction.hide(mEditFullTab);
		} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_FULL) {
			transaction.hide(mEditSmallTab);
		} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_PART) {
			transaction.hide(mEditTab);
		}
		transaction.hide(mSettingsTab);
		transaction.show(mControlTab);
		Debug.d(TAG, "===>show");
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
					if (PlatformInfo.getEditType() == PlatformInfo.LARGE_SCREEN) {
						fts.show(mEditFullTab);
					} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_FULL) {
						fts.show(mEditSmallTab);
					} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_PART) {
						fts.show(mEditTab);
					}
					
					mEditTitle.setVisibility(View.VISIBLE);
				} else {
					if (PlatformInfo.getEditType() == PlatformInfo.LARGE_SCREEN) {
						fts.hide(mEditFullTab);
					} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_FULL) {
						fts.hide(mEditSmallTab);
					} else if (PlatformInfo.getEditType() == PlatformInfo.SMALL_SCREEN_PART) {
						fts.hide(mEditTab);
					}
					mEditTitle.setVisibility(View.GONE);
				}
				break;
			case R.id.btn_setting:
				Debug.d(TAG, "====>setting checked?"+arg1);
				
				if (arg1 == true) {
					Debug.d(TAG, "--->show SettingTab");
					fts.show(mSettingsTab);
					Debug.d(TAG, "--->show SettingTab ok");
					mSettings.setVisibility(View.VISIBLE);
					Debug.d(TAG, "--->show SettingTab visible");
					// mSettingTitle.setVisibility(View.VISIBLE);
					// mVersion.setVisibility(View.VISIBLE);
					mHander.sendEmptyMessage(REFRESH_TIME_DISPLAY);
					
				} else {
					fts.hide(mSettingsTab);
					mSettings.setVisibility(View.GONE);
					//mSettingTitle.setVisibility(View.GONE);
					//mVersion.setVisibility(View.GONE);
					mHander.removeMessages(REFRESH_TIME_DISPLAY);
				}
				break;
		}
		fts.commit();
	}
	
	
	public static final int USB_STORAGE_ATTACHED = 0;
	public static final int REFRESH_TIME_DISPLAY = 1;
	
	public Handler mHander = new Handler(){
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USB_STORAGE_ATTACHED:
				Debug.d(TAG, "--->reload system settings");
				mControlTab.loadMessage();
				mSettingsTab.reloadSettings();
				break;
			case REFRESH_TIME_DISPLAY:
				Calendar calendar = Calendar.getInstance();
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				String time = String.format(getResources().getString(R.string.str_time_format), hour, min, second);
				mSettingTitle.setText(time);
				mHander.sendEmptyMessageDelayed(REFRESH_TIME_DISPLAY, 1000);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onDestroy() {
		FpgaGpioOperation.close();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch(view.getId()) {
		case R.id.btn_control:
		case R.id.btn_edit:
		case R.id.btn_setting:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				PWMAudio.Play();
			}
		default:
			break;
		}
		return false;
	}
}
