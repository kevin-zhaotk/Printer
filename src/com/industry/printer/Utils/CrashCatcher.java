package com.industry.printer.Utils;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.industry.printer.DataTransferThread;
import com.industry.printer.SettingsTabActivity;

public class CrashCatcher implements UncaughtExceptionHandler {

	private static final String TAG = CrashCatcher.class.getSimpleName();
	
	private static CrashCatcher mInstance;
	
	private Context mContext;
	//系统默认的UncaughtException处理类       
    private Thread.UncaughtExceptionHandler mDefaultHandler;
	
	public static CrashCatcher getInstance() {
		if (mInstance == null) {
			mInstance = new CrashCatcher();
		}
		return mInstance;
	}
	
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable arg1) {
		Debug.d(TAG, "--->uncaughtException: " + arg1.getMessage());
		DataTransferThread dtt = DataTransferThread.getInstance();
		if (dtt.isRunning()) {
			SharedPreferences p = mContext.getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
			p.edit().putBoolean(PreferenceConstants.PRINTING_BEFORE_CRASH, true).commit();
		}
		mDefaultHandler.uncaughtException(thread, arg1);
	}

}
