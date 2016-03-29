package com.industry.printer.hardware;

public class ExtGpio {

	public static native int open(String dev);
	public static native int write(int fd, char[] buffer, int count);
	public static native int ioctl(int fd, int cmd, long arg);
	public static native int close(int fd);
	/**
	 * RFID选择
	 */
	private static final String EXT_GPIO_FILE = "/dev/ext-gpio";
	
	private static final int GPIO_PLAY = 0x01;
	private static final int GPIO_PLAY_ERR = 0x02;
	private static final int GPIO_RFID_CARD1 = 0x03;
	private static final int GPIO_RFID_CARD2 = 0x04;
	
	// RFID卡1對應的3-8譯碼器編碼
	private static final int RFID_CARD1_CODE = 3;
	// RFID卡2對應的3-8譯碼器編碼
	private static final int RFID_CARD2_CODE = 4;
	
	public static final int RFID_CARD1 = 0;
	public static final int RFID_CARD2 = 1;
	
	public static int mFd = 0;
	public static void rfidSwitch(int sw) {
		int fd = open();
		if (sw == RFID_CARD1) {
			ioctl(fd, GPIO_RFID_CARD1, 0);
		} else if (sw == RFID_CARD2) {
			ioctl(fd, GPIO_RFID_CARD2, 0);
		}
	}
	
	public static void playClick() {
		int fd = open();
		ioctl(fd, GPIO_PLAY, 0);
	}
	
	public static void playError() {
		int fd = open();
		ioctl(fd, GPIO_PLAY_ERR, 0);
	}
	
	public static int open() {
		if (mFd <= 0) {
			mFd = open(EXT_GPIO_FILE);
		}
		return mFd;
	}
}
