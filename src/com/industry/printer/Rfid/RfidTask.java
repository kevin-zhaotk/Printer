package com.industry.printer.Rfid;

import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;

import android.R.bool;
import android.R.integer;
import android.os.SystemClock;

public class RfidTask {
	
	private String TAG = RfidTask.class.getSimpleName();
	
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_SEARCH_OK = 1;
	public static final int STATE_AVOID_CONFLICT = 2;
	public static final int STATE_SELECT_OK = 3;
	public static final int STATE_BLOCK_CERTIFIED = 4;
	public static final int STATE_BLOCK_SYNCED = 5;
	public static final int STATE_BACKUP_CERTIFIED = 6;
	public static final int STATE_BACKUP_SYNCED = 7;
	public static final int STATE_CERTIFY_FAIL = 8;
	public static final int STATE_BACKUP_CERTIFY_FAIL = 9;
	
	
	
	private int mIndex=0;
	private long mTimeStamp = 0;
	private int mState = 0;
	
	public RfidTask() {
		mState = STATE_IDLE;
		mTimeStamp = SystemClock.elapsedRealtime();
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
	
	public int getStat() {
		return mState;
	}
	
	public boolean isIdle() {
		return mState == STATE_IDLE;
	}
	
	public long getLast() {
		return mTimeStamp;
	}
	
	public void execute() {
		Debug.d(TAG, "--->execute index=" + mIndex);
		RFIDManager manager = RFIDManager.getInstance();
		RFIDDevice dev = manager.getDevice(mIndex);
		if (!dev.getReady()) {
			return;
		}
		if (dev == null || dev.getReady() != true) {
			return;
		}
		switch (mState) {
			case STATE_IDLE:
				/*
				dev.lookForCards(false);
				mState = STATE_SEARCH_OK;
				break;
			case STATE_SEARCH_OK:
				dev.avoidConflict(false);
				mState = STATE_AVOID_CONFLICT;
				break;
			case STATE_AVOID_CONFLICT:
				dev.selectCard(dev.mSN, false);
				mState = STATE_SELECT_OK;
			case STATE_SELECT_OK:
			*/
				boolean res = dev.keyVerify(false, true);
				mState = STATE_BLOCK_CERTIFIED;
				break;
			case STATE_BLOCK_CERTIFIED:
				dev.writeInk(false);
				mState = STATE_BLOCK_SYNCED;
				break;
			case STATE_BLOCK_SYNCED:
				dev.keyVerify(true, true);
				mState = STATE_BACKUP_CERTIFIED;
				break;
			case STATE_BACKUP_CERTIFIED:
				dev.writeInk(true);
				mTimeStamp = SystemClock.elapsedRealtime();
				mState = STATE_BACKUP_SYNCED;
				break;
			case STATE_BACKUP_SYNCED:
				break;
			case STATE_CERTIFY_FAIL:
			case STATE_BACKUP_CERTIFY_FAIL:
				Debug.e(TAG, "key certify failue");
				break;
			default:
				break;
		}
	}
}
