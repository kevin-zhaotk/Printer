package com.industry.printer.FileFormat;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.BreakIterator;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.text.StaticLayout;
import android.util.Log;

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
	
	public static int mEncoder = 0;
	public static int mTrigerMode = 0;
	public static int mPHOHighDelay = 0;
	public static int mPHOLowDelay = 0;
	public static int mPHOOutputPeriod = 0;
	public static int mTimedPeriod = 0;
	public static int mTrigerPulse = 0;
	public static int mLenFixedPulse = 0;
	public static int mDelayPulse = 0;
	public static int mHighLen = 0;

	
	public static void parseSystemCofig() {
		File file = new File(Configs.getUsbPath()+"/system/systemconfig.txt");
		if (!file.exists()) {
			return ;
		}
		try {
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				String[] args = line.split(" ");
				if (PH_SETTING_ENCODER.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_ENCODER);
					mEncoder = Integer.parseInt(args[1]);
				} else if (PH_SETTING_TRIGER_MODE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_MODE);
					mTrigerMode = Integer.parseInt(args[1]);
				} else if (PH_SETTING_HIGH_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_DELAY);
					mPHOHighDelay = Integer.parseInt(args[1]);
				} else if (PH_SETTING_LOW_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LOW_DELAY);
					mPHOLowDelay = Integer.parseInt(args[1]);
				} else if (PH_SETTING_PHOOUTPUT_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_PHOOUTPUT_PERIOD);
					mPHOOutputPeriod = Integer.parseInt(args[1]);
				} else if (PH_SETTING_TIMED_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TIMED_PERIOD);
					mTimedPeriod = Integer.parseInt(args[1]);
				} else if (PH_SETTING_TRIGER_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_PULSE);
					mTrigerPulse = Integer.parseInt(args[1]);
				} else if (PH_SETTING_LENFIXED_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LENFIXED_PULSE);
					mLenFixedPulse = Integer.parseInt(args[1]);
				} else if (PH_SETTING_DELAY_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_DELAY_PULSE);
					mDelayPulse = Integer.parseInt(args[1]);
				} else if (PH_SETTING_HIGH_LEN.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_LEN);
					mHighLen = Integer.parseInt(args[1]);
				} else {
					Debug.d(TAG, "===>unknow param: "+args[0]);
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void saveConfig() {
		File file = new File(Configs.getUsbPath()+"/system/systemconfig.txt");
		
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(PH_SETTING_ENCODER+" "+mEncoder);
			writer.append("\n");
			writer.append(PH_SETTING_TRIGER_MODE + " " +mTrigerMode);
			writer.append("\n");
			writer.append(PH_SETTING_HIGH_DELAY + " " +mPHOHighDelay);
			writer.append("\n");
			writer.append(PH_SETTING_LOW_DELAY + " " +mPHOLowDelay);
			writer.append("\n");
			writer.append(PH_SETTING_PHOOUTPUT_PERIOD + " " +mPHOOutputPeriod);
			writer.append("\n");
			writer.append(PH_SETTING_TIMED_PERIOD + " " +mTimedPeriod);
			writer.append("\n");
			writer.append(PH_SETTING_TRIGER_PULSE + " " +mTrigerPulse);
			writer.append("\n");
			writer.append(PH_SETTING_LENFIXED_PULSE + " " +mLenFixedPulse);
			writer.append("\n");
			writer.append(PH_SETTING_DELAY_PULSE + " " +mDelayPulse);
			writer.append("\n");
			writer.append(PH_SETTING_HIGH_LEN + " " +mHighLen);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}