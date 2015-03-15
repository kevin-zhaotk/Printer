package com.industry.printer.FileFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.text.StaticLayout;

public class SystemConfigFile{
	private static final String TAG = SystemConfigFile.class.getSimpleName();
	
	
	public static final String PH_SETTING_ENCODER = "encoder";
	public static final String PH_SETTING_TRIGER_MODE = "trigerMode";
	public static final String PH_SETTING_HIGH_DELAY = "PHOHighDelay";
	public static final String PH_SETTING_LOW_DELAY = "PHOLowDelay";
	public static final String PH_SETTING_PHOOUTPUT_PERIOD = "PHOOutputPeriod";
	public static final String PH_SETTING_TIMED_PERIOD = "timedPeriod";
	public static final String PH_SETTING_TRIGER_PULSE = "trigerPulse";
	public static final String PH_SETTING_LENFIXED_PULSE = "lenFixedPulse";
	public static final String PH_SETTING_DELAY_PULSE = "delayPulse";
	public static final String PH_SETTING_HIGH_LEN = "highLen";
	
	public static int mEncoder;
	public static int mTrigerMode;
	public static int mPHOHighDelay;
	public static int mPHOLowDelay;
	public static int mPHOOutputPeriod;
	public static int mTimedPeriod;
	public static int mTrigerPulse;
	public static int mLenFixedPulse;
	public static int mDelayPulse;
	public static int mHighLen;

	
	public static void parseSystemCofig() {
		File file = new File(Configs.getUsbPath()+"/8_1/system/systemconfig.txt");
		
		try {
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			String[] args = line.split(" ");
			if (PH_SETTING_ENCODER.equals(args[0])) {
				Debug.d(TAG, "===>param: "+PH_SETTING_ENCODER);
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}