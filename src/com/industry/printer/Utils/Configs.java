package com.industry.printer.Utils;


import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract.Directory;

import com.industry.printer.R;
import com.industry.printer.FileFormat.SystemConfigFile;

public class Configs {
	
	/** 每列的有效点阵数 **/
	public static int gDots;
	/** 每列的总字节数 **/
	public static int gDotsTotal;
	
	public static int gBytesPerColumn;
	public static int gCharsPerColumn;
	public static int gFixedRows;
	
	public static int gParams;
	
	public static final boolean gMakeBinFromBitmap = false;
	
	/**
	 * 一个单位的墨水量对应的打点数
	 */
	public static final int DOTS_PER_PRINT = 100;
	
	/**
	 * 墨盒总墨水量
	 */
	public static final int INK_LEVEL_MAX = 1000;

	/**
	 * PROC_MOUNT_FILE
	 * proc/mounts sys file
	 */
	public static final String PROC_MOUNT_FILE = "/proc/mounts";
	/**
	 * USB_ROOT_PATH
	 * 	usb mount root path on this platform
	 */
	public static final String USB_ROOT_PATH = "/mnt/usbhost0";
	
	/**
	 * LOCAL_ROOT_PATH
	 * 	the path local objects store at
	 */
	public static final String LOCAL_ROOT_PATH = "/data/TLK";
	
	/**
	 * LOCAL_ROOT_PATH
	 * 	the path local objects store at
	 */
	public static final String USB_ROOT_PATH2 = "/mnt/usbhost1";
	
	/**
	 * SDCARD_ROOT_PATH
	 * 	sd-card mount root path on this platform
	 */
	public static final String SDCARD_ROOT_PATH = "/storage/sd_external";
	
	
	
	/**
	 * SYSTEM_CONFIG_FILE
	 */
	public static final String SYSTEM_CONFIG_DIR = "/system";
	public static final String SYSTEM_CONFIG_FILE = SYSTEM_CONFIG_DIR+"/system_config.txt";
	public static final String SYSTEM_CONFIG_XML = SYSTEM_CONFIG_DIR+"/system_config.xml";
	public static final String LAST_MESSAGE_XML = SYSTEM_CONFIG_DIR+"/last_message.xml";
	
	/**
	 * 用户pc端编辑的文本存放路径，编辑打印对象的内容时可以从这个目录加载，而不需要手动输入 
	 */
	public static final String TXT_FILES_PATH = SYSTEM_CONFIG_DIR + "/txt";
	
	/**
	 * TLK文件存放路径
	 */
	public static final String TLK_FILE_SUB_PATH = SYSTEM_CONFIG_DIR+"/tlks";
	/**
	 * initConfigs initiallize the system configs,such as dots and fixed rows 
	 * @param context
	 */
	public static void initConfigs(Context context)
	{
		gDots = context.getResources().getInteger(R.integer.dots_per_column);
		gDotsTotal = context.getResources().getInteger(R.integer.dots_per_column_total);
		gBytesPerColumn = context.getResources().getInteger(R.integer.bytes_per_column);
		gCharsPerColumn = context.getResources().getInteger(R.integer.chars_per_column);
		gFixedRows = context.getResources().getInteger(R.integer.fixed_rows);
		gParams = context.getResources().getInteger(R.integer.total_params);
		
		//如果需要，在u盘根目录创建系统所需的目录，当u盘插入是也需要调用
		ConfigPath.makeSysDirsIfNeed();
		/*从U盘中读取系统设置，解析*/
		SystemConfigFile.parseSystemCofig();
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
