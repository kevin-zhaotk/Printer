package com.industry.printer.hardware;

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
	private static final String RFID_SWITCHER = "/sys/devices/platform/fpga_sunxi/switcher";
	
	public static void rfidSwitch(int sw) {
		
	}
	
	public static void playClick() {
		
	}
	
	public static void playError() {
		
	}
}
