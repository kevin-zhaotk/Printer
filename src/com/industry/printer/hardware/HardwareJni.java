package com.industry.printer.hardware;

import com.industry.printer.Utils.SystemFs;

public class HardwareJni {
	
	
	/**
	 * Usb-Serial JNI APIs
	 **/
	static public native int open(String dev);
	
	static public native int setBaudrate(int fd, int speed);
	
	static public native int close(int fd);
	
	static public native int write(int fd, short[] buf, int len);
	
	static public native byte[] read(int fd, int len);
	static public native int set_options(int fd, int databits, int stopbits, int parity);
	
	static public native String get_BuildDate();
	
	
	/**
	 * 蜂鸣器
	 */
	private static final String BUZZLE_FILE = "/sys/devices/platform/fpga_sunxi/play";
	
	/**
	 * RFID选择
	 */
	private static final String RFID_SWITCHER = "/sys/devices/platform/fpga_sunxi/decoder";
	// RFID卡1對應的3-8譯碼器編碼
	private static final int RFID_CARD1_CODE = 3;
	// RFID卡2對應的3-8譯碼器編碼
	private static final int RFID_CARD2_CODE = 4;
	
	public static final int RFID_CARD1 = 0;
	public static final int RFID_CARD2 = 1;
	
	public static void rfidSwitch(int sw) {
		if (sw == RFID_CARD1) {
			SystemFs.writeSysfs(RFID_SWITCHER, String.valueOf(RFID_CARD1_CODE));
		} else if (sw == RFID_CARD2) {
			SystemFs.writeSysfs(RFID_SWITCHER, String.valueOf(RFID_CARD2_CODE));
		}
	}
	
	public static void playClick() {
		
	}
	
	public static void playError() {
		
	}
}
