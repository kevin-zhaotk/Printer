package com.industry.printer.Rfid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.industry.printer.ThreadPoolManager;
import com.industry.printer.hardware.ExtGpio;

import android.app.AlarmManager;
import android.os.SystemClock;
import android.provider.AlarmClock;

public class RfidScheduler {
	
	private String TAG = RfidScheduler.class.getSimpleName();
	
	public static RfidScheduler mInstance = null;
	// 5S间隔
	public static final long TASK_SCHEDULE_INTERVAL = 5000;
	public static final long RFID_SWITCH_INTERVAL = 1000;
	
	private List<RfidTask> mRfidTasks = null;
	private int mCurrent = 0;
	private long mSwitchTimeStemp=0;
	private Thread mAfter;
	private boolean running=false;
	
	public static RfidScheduler getInstance() {
		if (mInstance == null) {
			mInstance = new RfidScheduler();
		}
		return mInstance;
	}
	
	public RfidScheduler() {
		mRfidTasks = new ArrayList<RfidTask>();
	}
	
	public void init() {
		running = false;
		if (mAfter != null) {
			mAfter.interrupt();
			mAfter = null;
		}
		removeAll();
		mCurrent = 0;
	}
	
	public void add(RfidTask task) {
		if (mRfidTasks == null) {
			mRfidTasks = new ArrayList<RfidTask>();
		}
		mRfidTasks.add(task);
	}
	
	/**
	 * Rfid調度函數
	 */
	public void schedule() {
		long time = SystemClock.elapsedRealtime();
		RfidTask task = null;
		if (mRfidTasks.size() <= 0 || time - mSwitchTimeStemp < RFID_SWITCH_INTERVAL) {
			return;
		}
		if (mRfidTasks.size() <= mCurrent) {
			 mCurrent = 0;
		}
		task = mRfidTasks.get(mCurrent);
		
		// 
		if (task.isIdle() && (time - task.getLast()) < TASK_SCHEDULE_INTERVAL) {
			return;
		}
		task.execute();
		if (task.getStat() >= RfidTask.STATE_BACKUP_SYNCED) {
			loadNext();
		}
	}
	
	/**
	 * 停止打印後需要把所有的鎖值同步一遍
	 */
	public void doAfterPrint() {
		running = true;
		mAfter = new Thread(){
			@Override
			public void run() {
				mCurrent = 0;
				int last = mCurrent;
				while(running && mCurrent < mRfidTasks.size()) {
					try {
						if (last != mCurrent) {
							Thread.sleep(1000);
						} else {
							Thread.sleep(100);
						}
					} catch (Exception e) {
					}
					schedule();
				}
			}

		};
		mAfter.start();
	}
	
	/**
	 * 装入下一个要处理的任务
	 */
	private void loadNext() {
		RfidTask task = mRfidTasks.get(mCurrent);
		if (task != null) {
			task.clearStat();
		}
		if (mRfidTasks.size() <= 0) {
			return;
		}
		if (mRfidTasks.size() <= mCurrent || mCurrent < 0) {
			 mCurrent = 0;
		} else {
			mCurrent++;
		}
		ExtGpio.rfidSwitch(mCurrent);
		/*切換鎖之後需要等待1s才能進行讀寫操作*/
		mSwitchTimeStemp = SystemClock.elapsedRealtime();
	}
	
	/**
	 * 已经处理完的任务
	 */
	private void unload(RfidTask task) {
		
	}
	
	public void removeAll() {
		if (mRfidTasks == null) {
			return;
		}
		mRfidTasks.clear();
	}
}
