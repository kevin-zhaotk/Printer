package com.industry.printer;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.ui.ExtendMessageTitleFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettingsTabActivity extends Fragment implements OnClickListener {
public static final String TAG="SettingsTabActivity";
	
	SharedPreferences mPreference=null;
	public final static String PREFERENCE_NAME="Settings";
/*	
	1.  Byte 2-3,  setting 00,  Reserved. 
	2.  Byte 4-5,  setting 01,  printing frequency.  Unit: Hz, (0-43K Hz)
	3.  Byte 6-7,  setting 02,  Delay.               Unit: 0.1mmm  
	4.  Byte 8-9,  setting 03,  Reserved. 
	5.  Byte 10-11,  setting 04, Photocell.           00 00 : ON.  00 01 OFF. 
	6.  Byte 12-13,  setting 05, Encoder.             00 00 : ON.  00 01 OFF.  
	7.  Byte 14-15,  setting 06,  Bold.  
	8.  Byte 16-17,  setting 07,  Fix length trigger.  Unit: 0.1mmm 
	9.  Byte 18-19,  setting 08,  Fix time  trigger.  Unit: ms
	10. Byte 20-21,  setting 09,  Temperature of print head.  Unit: C 00-130.
	11. Byte 20-21,   setting 09,  Temperature of reservoir.  Unit: C   00-130.
	12. Others ,     Setting 10-63,  Reserved. 
*/
	public static final String PREF_PARAM_0="param0";			//00
	public static final String PREF_PRINTSPEED="printspeed";	//01
	public static final String PREF_DELAY="delay";				//02
	public static final String PREF_TRIGER="triger";			//04
	public static final String PREF_ENCODER="encoder";			//05
	public static final String PREF_BOLD="bold";				//06
	public static final String PREF_FIX_LEN="fix_length_triger";	//07
	public static final String PREF_FIX_TIME="fix_time_triger";	//08
	public static final String PREF_HEAD_TEMP="head_temp";		//09
	public static final String PREF_RESV_TEMP="reservoir_temp";	//10
	public static final String PREF_FONT_WIDTH="fontwidth";		//11
	public static final String PREF_DOT_NUMBER="dot_number";	//12
	public static final String PREF_RESERVED_12="reserved12";	//13
	public static final String PREF_RESERVED_13="reserved13";	//14
	public static final String PREF_RESERVED_14="reserved14";	//15
	public static final String PREF_RESERVED_15="reserved15";	//16
	public static final String PREF_RESERVED_16="reserved16";	//17
	public static final String PREF_RESERVED_17="reserved17";	//18
	public static final String PREF_RESERVED_18="reserved18";	//19
	public static final String PREF_RESERVED_19="reserved19";	//20
	public static final String PREF_RESERVED_20="reserved20";	//21
	public static final String PREF_RESERVED_21="reserved21";	//22
	public static final String PREF_RESERVED_22="reserved22";	//23
	public static final String PREF_RESERVED_23="reserved23";	//24
		
	public static final String PREF_DIRECTION="direction";
	public static final String PREF_HORIRES="horires";
	public static final String PREF_VERTRES="vertres";
	
	
	public TextView mTime;
	public TextView mVersion;

	public Button		mSave;
	
	public Button		mUpgrade;
	public Button		mSetDate;
	public Button		mSettings;
	

	Context 			mContext;
	ProgressDialog 		pDialog;
	
	PHSettingFragment 	mPHSettings;
	
	public ExtendMessageTitleFragment mMsgTitle;
	
	public SettingsTabActivity() {
//		mMsgTitle = (ExtendMessageTitleFragment)fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_layout, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.addPreferencesFromResource(R.xml.settings_layout);
		mContext = getActivity().getApplicationContext();
		
		
//		mTime = (TextView) findViewById(R.id.tv_systemTime);
//		mTimeRefreshHandler.sendEmptyMessageDelayed(0, 2000);

//		mVersion = (TextView) findViewById(R.id.tv_version);
		InputStream version=null;
		byte[] buffer=null;
		try{
			version = mContext.getAssets().open("Version");
			buffer =new byte[version.available()];
			version.read(buffer);
			version.close();
		}catch(Exception e)
		{
			Debug.d(TAG, "version read error: "+e.getMessage());
		}
//		mVersion.setText(getResources().getString(R.string.app_version)+new String(buffer));
		/*
        mPreference=PreferenceManager.getDefaultSharedPreferences(this);
        mDelay = (EditTextPreference) findPreference(getString(R.string.strDelay_key));
        mTrigger = (ListPreference) findPreference(getString(R.string.strTrigger_key));
        mDirection = (ListPreference) findPreference(getString(R.string.strDirection_key));
        mEncoder = (ListPreference) findPreference(getString(R.string.strEncoder_key));
        mHorires = (ListPreference) findPreference(getString(R.string.strHoriRes_key));
        mVertres = (ListPreference) findPreference(getString(R.string.strVertRes_key));
        
        mDelay.setOnPreferenceChangeListener(this);
        mTrigger.setOnPreferenceChangeListener(this);
        mDirection.setOnPreferenceChangeListener(this);
        mEncoder.setOnPreferenceChangeListener(this);
        mHorires.setOnPreferenceChangeListener(this);
        mVertres.setOnPreferenceChangeListener(this);
        
        mDelay.setSummary(mDelay.getText());
        mTrigger.setSummary(mTrigger.getEntry());
        mDirection.setSummary(mDirection.getEntry());
        mEncoder.setSummary(mEncoder.getEntry());
        mHorires.setSummary(mHorires.getEntry());
        mVertres.setSummary(mVertres.getEntry());
        */
		mSave = (Button) getView().findViewById(R.id.btn_setting_ok);
		mSave.setOnClickListener(this);
		
		mUpgrade = (Button) getView().findViewById(R.id.btn_setting_upgrade);
		mUpgrade.setOnClickListener(this);
		
		mSettings = (Button) getView().findViewById(R.id.btn_system_setting);
		mSettings.setOnClickListener(this);
		
		mPHSettings = new PHSettingFragment(mContext);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.phsetting_fragment, mPHSettings);
		transaction.commit();
		
		
	}
	
//	@Override
//	public boolean  onKeyDown(int keyCode, KeyEvent event)  
//	{
//		Debug.d(TAG, "keycode="+keyCode);
//		
//		if(keyCode==KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
//		{
//			Debug.d(TAG, "back key pressed, ignore it");
//			return true;	
//		}
//		return false;
//	}
//	
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		Debug.d(TAG, "event:"+event.toString());
//		InputMethodManager manager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
//		Debug.d(TAG, "ime is active? "+manager.isActive());
//		manager.hideSoftInputFromWindow(SettingsTabActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
////			manager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//		return true;
//	}
	
	public void setLocale()
	{
		Configuration config = getResources().getConfiguration(); 
		DisplayMetrics dm = getResources().getDisplayMetrics(); 
		config.locale = Locale.SIMPLIFIED_CHINESE; 
		getResources().updateConfiguration(config, dm); 
		
	}
	
	public void savePreference()
	{
		
	}
	
	
	public Handler mTimeRefreshHandler = new Handler(){
		public void handleMessage(Message msg) { 
			switch(msg.what)
			{
				case 0:		//
					 long sysTime = System.currentTimeMillis();
					 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//					 mTime.setText(dateFormat.format(new Date()));
					break;
			}
			mTimeRefreshHandler.sendEmptyMessageDelayed(0, 500);
		}
	};
	@Override
	public void onClick(View arg0) {
		if (arg0 == null) {
			return;
		}
		switch (arg0.getId()) {
			case R.id.btn_setting_ok:
				Debug.d(TAG, "===>onclick");
				SystemConfigFile.saveConfig();
				FpgaGpioOperation.updateSettings(mContext);
				break;
			case R.id.btn_system_setting:	//进入系统设置
				Intent intent = new Intent();
				intent.setClassName("com.android.settings","com.android.settings.Settings");
				startActivity(intent);
				break;
			case R.id.btn_setting_upgrade:
				
				ArrayList<String> paths = ConfigPath.getMountedUsb();
				for (String str : paths) {
					File file = new File(str+"/Printer.apk");
					if (!file.exists()) {
						continue;
					}
//					ProgressDialog pDialog = new ProgressDialog(mContext);
//					pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//					pDialog.setMessage(getResources().getString(R.string.str_upgrade_progress));
//					pDialog.show();
					System.setProperty("ctrl.start", "upgrade");
					break;
				}
				break;
			default :
				Debug.d(TAG, "===>unknown view clicked");
				break;
		}
	}
}
