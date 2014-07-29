package com.industry.printer;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.industry.printer.Utils.Debug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsTabActivity extends Activity{
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
		
	public static final String PREF_DIRECTION="direction";
	public static final String PREF_HORIRES="horires";
	public static final String PREF_VERTRES="vertres";
	
	
	public TextView mTime;
	public TextView mVersion;
	/*
	EditTextPreference mDelay=null;
	ListPreference 		mTrigger=null;
	ListPreference 		mEncoder=null;
	ListPreference 		mDirection=null;
	ListPreference 		mHorires=null;
	ListPreference 		mVertres=null;
	*/
	public EditText		mPrSpeed;
	public EditText		mDelay;
	public Spinner		mTriger;
	public Spinner		mEncoder;
	public EditText		mBold;
	public EditText 	mFixlen;
	public EditText 	mFixtime;
	public EditText 	mHeadtemp;
	public EditText 	mResvtemp;
	public EditText 	mFontwidth;
	public EditText 	mDots;
	
	public Spinner		mDirection;
	
	public EditText		mHRes;
	public EditText		mVRes;
	
	public Button			mSave;
	public Button			mUpgrade;
	public Button			mSetDate;

	Context 			mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLocale();
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.addPreferencesFromResource(R.xml.settings_layout);
		mContext = getApplicationContext();		
		setContentView(R.layout.setting_layout);
		
		
		mTime = (TextView) findViewById(R.id.tv_systemTime);
		mTimeRefreshHandler.sendEmptyMessageDelayed(0, 2000);

		mVersion = (TextView) findViewById(R.id.tv_version);
		InputStream version=null;
		byte[] buffer=null;
		try{
			version = getAssets().open("Version");
			buffer =new byte[version.available()];
			version.read(buffer);
			version.close();
		}catch(Exception e)
		{
			Debug.d(TAG, "version read error: "+e.getMessage());
		}
		mVersion.setText(getResources().getString(R.string.app_version)+new String(buffer));
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
		//delay param 02
		mDelay = (EditText) findViewById(R.id.et_delay);
		//print frequency param 01
		mPrSpeed = (EditText) findViewById(R.id.et_frequency);
		//
		//mHRes = (EditText) findViewById(R.id.et_horires);
		//mVRes = (EditText) findViewById(R.id.et_vertres);
		//triger param 04
		mTriger = (Spinner) findViewById(R.id.sp_triger);
		//sync param 05
		mEncoder = (Spinner) findViewById(R.id.sp_encoder);
				
		//bold
		mBold = (EditText) findViewById(R.id.et_bold);
		
		//et_fixlen_triger
		mFixlen = (EditText) findViewById(R.id.et_fixlen_triger);

		//et_fixtime_triger
		mFixtime = (EditText) findViewById(R.id.et_fixtime_triger);
		//Temperature of print head
		mHeadtemp = (EditText) findViewById(R.id.et_Headtemp);
		//Temperature of reservoir.
		mResvtemp = (EditText) findViewById(R.id.et_reseTemp);
		
		//font width
		mFontwidth = (EditText) findViewById(R.id.et_Fontwidth);
		//dot number
		mDots = (EditText) findViewById(R.id.et_Dotnumber);
		
		//mDirection = (Spinner) findViewById(R.id.sp_direction);
		//param 06 Aggravate
		//param 07  Fix length trigger,unit 0.1mm 
		//param 08  Fix time  trigger,unit:ms  
		//param 09  print head tempreture 00-130
		//param 10  Temperature of reservoir 00-130
		mPreference = getSharedPreferences(PREFERENCE_NAME, 0);
		mPrSpeed.setText(String.valueOf( mPreference.getInt(PREF_PRINTSPEED, 0)));
		mDelay.setText(String.valueOf( mPreference.getInt(PREF_DELAY, 0)));
		mTriger.setSelection((int)mPreference.getLong(PREF_TRIGER, 0));
		mEncoder.setSelection((int)mPreference.getLong(PREF_ENCODER, 0));
		mBold.setText(String.valueOf(mPreference.getInt(PREF_BOLD, 0)));
		mFixlen.setText(String.valueOf(mPreference.getInt(PREF_FIX_LEN, 0)));
		mFixtime.setText(String.valueOf(mPreference.getInt(PREF_FIX_TIME, 0)));
		mHeadtemp.setText(String.valueOf(mPreference.getInt(PREF_HEAD_TEMP, 0)));
		mResvtemp.setText(String.valueOf(mPreference.getInt(PREF_RESV_TEMP, 0)));
		mFontwidth.setText(String.valueOf(mPreference.getInt(PREF_FONT_WIDTH, 0)));
		mDots.setText(String.valueOf(mPreference.getInt(PREF_DOT_NUMBER, 0)));
		//mDirection.setSelection(mPreference.getBoolean(PREF_DIRECTION, true)==true?0:1);
		
		//mHRes.setText(String.valueOf( mPreference.getInt(PREF_HORIRES, 0)));
		//mVRes.setText(String.valueOf( mPreference.getInt(PREF_VERTRES, 0)));
		
		mSave = (Button) findViewById(R.id.btn_set_save);
		mSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				savePreference();
			}
			
		});
		mUpgrade = (Button) findViewById(R.id.btn_upgrade);
		mUpgrade.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File f= new File("/mnt/usb/Printer.apk");
				if(f.exists())
				{
					new AlertDialog.Builder(SettingsTabActivity.this).setMessage("sure to upgrade?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SystemProperties.set("ctl.start", "Upgrade");
						}
						
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();
				}
				else
				{
					new AlertDialog.Builder(SettingsTabActivity.this)
					.setMessage("No Upgrade Image found!!!")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();
				}
			}
			
		});
		
		/*
		 * set system time
		 */
		mSetDate = (Button) findViewById(R.id.btn_setDate);
		mSetDate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new CalendarDialog(SettingsTabActivity.this, R.layout.calendar_setting).show();
			}
			
		});
	}
	
	public void setLocale()
	{
		Configuration config = getResources().getConfiguration(); 
		DisplayMetrics dm = getResources().getDisplayMetrics(); 
		config.locale = Locale.SIMPLIFIED_CHINESE; 
		getResources().updateConfiguration(config, dm); 
		
	}
	
	public void savePreference()
	{
		mPreference.edit().putInt(PREF_PRINTSPEED, Integer.parseInt(mPrSpeed.getText().toString())).commit();
		mPreference.edit().putInt(PREF_DELAY, Integer.parseInt(mDelay.getText().toString())).commit();
		mPreference.edit().putLong(PREF_TRIGER, mTriger.getSelectedItemId()).commit();
		//mPreference.edit().putLong(PREF_DIRECTION, mDirection.getSelectedItemId()).commit();
		mPreference.edit().putLong(PREF_ENCODER, mEncoder.getSelectedItemId()).commit();
		mPreference.edit().putInt(PREF_BOLD, Integer.parseInt(mBold.getText().toString())).commit();
		mPreference.edit().putInt(PREF_FIX_LEN, Integer.parseInt(mFixlen.getText().toString())).commit();
		mPreference.edit().putInt(PREF_FIX_TIME, Integer.parseInt(mFixtime.getText().toString())).commit();
		mPreference.edit().putInt(PREF_HEAD_TEMP, Integer.parseInt(mHeadtemp.getText().toString())).commit();
		mPreference.edit().putInt(PREF_RESV_TEMP, Integer.parseInt(mResvtemp.getText().toString())).commit();
		mPreference.edit().putInt(PREF_FONT_WIDTH, Integer.parseInt(mFontwidth.getText().toString())).commit();
		mPreference.edit().putInt(PREF_DOT_NUMBER, Integer.parseInt(mDots.getText().toString())).commit();
		
	}
	/*
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals(getString(R.string.strDelay_key)))
		{
			System.out.println("delay changed: "+(String)newValue);
			mDelay.setText((String)newValue);
			mDelay.setSummary(mDelay.getText());
		}
		else if(preference.getKey().equals(getString(R.string.strTrigger_key)))
		{
			System.out.println("trigger mode changed:"+(String)newValue);
			mTrigger.setValue((String)newValue);
			mTrigger.setSummary(mTrigger.getEntry());
		}
		else if(preference.getKey().equals(getString(R.string.strDirection_key)))
		{
			System.out.println("direction changed: "+(String)newValue);
			mDirection.setValue((String)newValue);
			mDirection.setSummary(mDirection.getEntry());
		}
		else if(preference.getKey().equals(getString(R.string.strEncoder_key)))
		{
			System.out.println("Encoder changed: "+(String)newValue);
			mEncoder.setValue((String)newValue);
			mEncoder.setSummary(mEncoder.getEntry());
		}
		else if(preference.getKey().equals(getString(R.string.strHoriRes_key)))
		{
			System.out.println("Hori res changed: "+(String)newValue);
			mHorires.setValue((String)newValue);
			mHorires.setSummary(mHorires.getEntry());
		}
		else if(preference.getKey().equals(getString(R.string.strVertRes_key)))
		{
			System.out.println("Vert res changed: "+(String)newValue);
			mVertres.setValue((String)newValue);
			mVertres.setSummary(mVertres.getEntry());
		}
		return true;
	}
	*/
	
	
	public Handler mTimeRefreshHandler = new Handler(){
		public void handleMessage(Message msg) { 
			switch(msg.what)
			{
				case 0:		//
					 long sysTime = System.currentTimeMillis();
					 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					 mTime.setText(dateFormat.format(new Date()));
					break;
			}
			mTimeRefreshHandler.sendEmptyMessageDelayed(0, 500);
		}
	};
}
