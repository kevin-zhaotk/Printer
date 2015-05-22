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
import java.util.ArrayList;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.text.StaticLayout;
import android.util.Log;
import android.widget.Toast;

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
		FileReader reader=null;
		BufferedReader br = null;
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			return;
		}
		/*
		 * use this first usb as default 
		 */
		File file = new File(paths.get(0)+Configs.SYSTEM_CONFIG_FILE);
		if (!file.exists()) {
			return ;
		}
		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				String[] args = line.split(" ");
				if (PH_SETTING_ENCODER.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_ENCODER);
					if (args.length < 2) {
						mEncoder = 0;
					} else {
						mEncoder = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_MODE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_MODE);
					if (args.length < 2) {
						mTrigerMode = 0;
					} else {
						mTrigerMode = Integer.parseInt(args[1]);
					}
				} else if (PH_SETTING_HIGH_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_DELAY);
					if (args.length < 2) {
						mPHOHighDelay = 0;
					} else {
						mPHOHighDelay = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LOW_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LOW_DELAY);
					if (args.length < 2) {
						mPHOLowDelay = 0;
					} else {
						mPHOLowDelay = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_PHOOUTPUT_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_PHOOUTPUT_PERIOD);
					if (args.length < 2) {
						mPHOOutputPeriod = 0;
					} else {
						mPHOOutputPeriod = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TIMED_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TIMED_PERIOD);
					if (args.length < 2) {
						mTimedPeriod = 0;
					} else {
						mTimedPeriod = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_PULSE);
					if (args.length < 2) {
						mTrigerPulse = 0;
					} else {
						mTrigerPulse = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LENFIXED_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LENFIXED_PULSE);
					if (args.length < 2) {
						mLenFixedPulse = 0;
					} else {
						mLenFixedPulse = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_DELAY_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_DELAY_PULSE);
					if (args.length < 2) {
						mDelayPulse = 0;
					} else {
						mDelayPulse = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_HIGH_LEN.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_LEN);
					if (args.length < 2) {
						mHighLen = 0;
					} else {
						mHighLen = Integer.parseInt(args[1]);
					}
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
		} finally {
		}
		
	}
	
	
	public static void saveConfig() {
		
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			Debug.d(TAG, "===>saveConfig error");
			return ;
		}
		
		/*
		 * use the first usb as the default device
		 */
		String dev = paths.get(0);
		File dir = new File(dev+Configs.SYSTEM_CONFIG_DIR);
		if (!dir.exists()) {
			if(dir.mkdirs() == false)
				return;
		}
		Debug.d(TAG, "===>dir:"+dir.getAbsolutePath());
		try {
			File file = new File(dev+Configs.SYSTEM_CONFIG_FILE);
			if(!file.exists()) {
				file.createNewFile();
			}
			Debug.d(TAG, "===>file:"+file.getAbsolutePath());
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
	
	public void saveSettings() {
		ArrayList<XmlTag> tags = new ArrayList<XmlTag>();
		tags.add(new XmlTag(PH_SETTING_ENCODER, String.valueOf(mEncoder)));
		tags.add(new XmlTag(PH_SETTING_TRIGER_MODE, String.valueOf(mTrigerMode)));
		tags.add(new XmlTag(PH_SETTING_HIGH_DELAY, String.valueOf(mPHOHighDelay)));
		tags.add(new XmlTag(PH_SETTING_LOW_DELAY, String.valueOf(mPHOLowDelay)));
		tags.add(new XmlTag(PH_SETTING_PHOOUTPUT_PERIOD, String.valueOf(mPHOOutputPeriod)));
		tags.add(new XmlTag(PH_SETTING_TIMED_PERIOD, String.valueOf(mTimedPeriod)));
		tags.add(new XmlTag(PH_SETTING_TRIGER_PULSE, String.valueOf(mTrigerPulse)));
		tags.add(new XmlTag(PH_SETTING_LENFIXED_PULSE, String.valueOf(mLenFixedPulse)));
		tags.add(new XmlTag(PH_SETTING_DELAY_PULSE, String.valueOf(mDelayPulse)));
		tags.add(new XmlTag(PH_SETTING_HIGH_LEN, String.valueOf(mHighLen)));
	}
}