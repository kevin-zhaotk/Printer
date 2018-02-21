package com.industry.printer;

import com.industry.printer.Utils.CrashCatcher;

import android.app.Application;

public class PrinterApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashCatcher catcher = CrashCatcher.getInstance();
		catcher.init(getApplicationContext());
	}

}
