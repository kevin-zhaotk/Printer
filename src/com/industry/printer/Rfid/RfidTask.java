package com.industry.printer.Rfid;

import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;

import android.R.integer;
import android.os.SystemClock;

public class RfidTask {
	
	private String TAG = RfidTask.class.getSimpleName();
	
	private int mIndex=0;
	private long mTimeStamp = 0;
	private boolean isCertified = false;
	private boolean isSynced = false;
	
	public RfidTask() {
	}
	
	public RfidTask(int index) {
		mIndex = index;
	}
	
	public void setIndex(int index) {
		mIndex = index;
	}
	/**
	 * 清除状态，为下次写入做准备
	 */
	public void clearStat() {
		mTimeStamp = SystemClock.elapsedRealtime();
		isCertified = false;
		isSynced = false;
	}
	
	public boolean certified() {
		return isCertified;
	}
	
	public boolean synced() {
		return isSynced;
	}
	
	public void certify() {
		RFIDManager manager = RFIDManager.getInstance();
		RFIDDevice dev = manager.getDevice(mIndex);
		if (dev == null) {
			return;
		}
		
	}
}
