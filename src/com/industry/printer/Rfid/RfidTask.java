package com.industry.printer.Rfid;

import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;

import android.R.integer;
import android.os.SystemClock;

public class RfidTask {
	
	private String TAG = RfidTask.class.getSimpleName();
	
	
	private static final int STATE_IDLE = 0;
	private static final int STATE_BLOCK_CERTIFIED = 1;
	private static final int STATE_BLOCK_SYNCED = 2;
	private static final int STATE_BACKUP_CERTIFIED = 3;
	private static final int STATE_BACKUP_SYNCED = 4;
	private static final int STATE_CERTIFY_FAIL = 5;
	private static final int STATE_BACKUP_CERTIFY_FAIL = 6;
	
	
	
	private int mIndex=0;
	private long mTimeStamp = 0;
	private int mState = 0;
	
	public RfidTask() {
		mState = STATE_IDLE;
	}
	
	public RfidTask(int index) {
		this();
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
		mState = STATE_IDLE;
	}
	
	public void execute() {
		RFIDManager manager = RFIDManager.getInstance();
		RFIDDevice dev = manager.getDevice(mIndex);
		if (dev == null) {
			return;
		}
		switch (mState) {
		case STATE_IDLE:
			dev.keyVerify(false);
			mState = STATE_BLOCK_CERTIFIED;
			break;
		case STATE_BLOCK_CERTIFIED:
		default:
			break;
		}
	}
}
