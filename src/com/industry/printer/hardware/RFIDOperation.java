package com.industry.printer.hardware;

public class RFIDOperation {

	//RFID操作 native接口
	public static native int open(String dev);
	public static native int write(int fd, short[] buf, int len);
	public static native byte[] read(int fd, int len);
	
	
}
