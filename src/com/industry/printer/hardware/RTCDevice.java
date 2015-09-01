package com.industry.printer.hardware;

import android.app.AlarmManager;
import android.content.Context;

import com.industry.printer.Utils.Debug;

public class RTCDevice {

	public static native int open(String dev);
	public static native int close(int fd);
	public static native void syncSystemTimeToRTC(int fd);	//write
	public static native void syncSystemTimeFromRTC(int fd); //read
	
	private static final String TAG = RTCDevice.class.getSimpleName();
	public static final String RTC_DEV = "/dev/rtc1"; 
	
	public static RTCDevice mInstance = null;
	
	public static RTCDevice getInstance() {
		if (mInstance == null) {
			mInstance = new RTCDevice();
		}
		return mInstance;
	}
	
	
	public void initSystemTime(Context context) {
//		Debug.d(TAG, "--->initSystemTime");
//		int fd = open(RTC_DEV);
//		Debug.d(TAG, "--->initSystemTime fd=" + fd);
//		syncSystemTimeFromRTC(fd);
//		Debug.d(TAG, "--->initSystemTime sync success");
//		close(fd);
		AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		aManager.HwToSystemClock();
	}
	
	public void syncSystemTimeToRTC(Context context) {
		AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		aManager.SystemClockToHw();
	}
	
	public void writeCounter(Context context, int count) {
		SystemWriteManager manager = (SystemWriteManager) context.getSystemService(Context.);
	}
}
