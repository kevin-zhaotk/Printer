package com.industry.printer.Utils;


import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract.Directory;

import com.industry.printer.R;

public class Configs {
	public static int gDots;
	public static int gFixedRows;
	
	public static int gParams;
	/**
	 * USB_ROOT_PATH
	 * 	usb mount root path on this platform
	 */
	public static final String USB_ROOT_PATH="/mnt/usbhost0";
	
	/**
	 * LOCAL_ROOT_PATH
	 * 	the path local objects store at
	 */
	public static final String LOCAL_ROOT_PATH="/data/TLK";
	
	/**
	 * SDCARD_ROOT_PATH
	 * 	sd-card mount root path on this platform
	 */
	public static final String SDCARD_ROOT_PATH="/storage/sd_external";
	
	/**
	 * SYSTEM_CONFIG_FILE
	 */
	public static final String SYSTEM_CONFIG_FILE="/system/system_config.txt";
	
	/**
	 * initConfigs initiallize the system configs,such as dots and fixed rows 
	 * @param context
	 */
	public static void initConfigs(Context context)
	{
		gDots = context.getResources().getInteger(R.integer.dots_per_column);
		gFixedRows = context.getResources().getInteger(R.integer.fixed_rows);
		gParams = context.getResources().getInteger(R.integer.total_params);
		
		File dir = new File(LOCAL_ROOT_PATH);
				
		if(!dir.exists())
		{
			dir.mkdir();
		}
	}
	
	/**
	 * isRootpath - check whether the path specified by parameter path is root path
	 * @param path the path to check
	 * @return true if path is root path, false otherwise
	 */
	public static boolean isRootpath(String path)
	{
		if(LOCAL_ROOT_PATH.equals(path) || USB_ROOT_PATH.equals(path) || SDCARD_ROOT_PATH.equals(path))
			return true;
		else
			return false;
	}
	
	
	/**
	 * 鑾峰彇u鐩樻寕杞借矾寰�
	 * @return usb root path
	 */
	public static String getUsbPath() {
		return USB_ROOT_PATH;
	}
}
