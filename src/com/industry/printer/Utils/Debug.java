package com.industry.printer.Utils;

import android.util.Log;

public class Debug {
	public static final String TAG="Printer";
	
	public Debug()
	{	
	}
	
	public static int d(String tag, String log)
	{
		Log.d(TAG, tag+":"+log);
		return 0;
	}
	
	public static int d(String tag, String log, Throwable tr)
	{
		Log.d(TAG, tag+":"+log, tr);
		return 0;
	}
	public static int i(String tag, String log)
	{
		Log.i(TAG, tag+":"+log);
		return 0;
	}
	
	public static int i(String tag, String log, Throwable tr)
	{
		Log.i(TAG, tag+":"+log, tr);
		return 0;
	}
	
	public static int v(String tag, String log)
	{
		Log.v(TAG, tag+":"+log);
		return 0;
	}
	public static int v(String tag, String log, Throwable tr)
	{
		Log.v(TAG, tag+":"+log, tr);
		return 0;
	}

	public static int e(String tag, String log)
	{
		Log.e(TAG, tag+":"+log);
		return 0;
	}
	
	public static int e(String tag, String log, Throwable tr)
	{
		Log.e(TAG, tag+":"+log, tr);
		return 0;
	}
	
	public static void print(String tag, byte[] value) {
		if (value == null) {
			return;
		}
		String log = "";
		for (int i = 0; i < value.length; i++) {
			log += "0x"+Integer.toHexString(value[i]&0x0ff)+" " ;
		}
		Debug.d(TAG, tag + " [ " + log + " ]");
	}
	
}
