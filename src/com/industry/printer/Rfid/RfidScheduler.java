package com.industry.printer.Rfid;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.os.SystemClock;
import android.provider.AlarmClock;

public class RfidScheduler {
	
	private String TAG = RfidScheduler.class.getSimpleName();
	
	public static RfidScheduler mInstance = null;
	// 5S间隔
	public static final long TASK_SCHEDULE_INTERVAL = 5000;
	
	private List<RfidTask> mRfidTasks = null;
	private int mCurrent = 0;
	
	public static RfidScheduler getInstance() {
		if (mInstance == null) {
			mInstance = new RfidScheduler();
		}
		return mInstance;
	}
	
	public RfidScheduler() {
		mRfidTasks = new ArrayList<RfidTask>();
	}
	
	public void add(RfidTask task) {
		if (mRfidTasks == null) {
			mRfidTasks = new ArrayList<RfidTask>();
		}
		mRfidTasks.add(task);
	}
	
	/**
	 * 
	 */
	public void schedule() {
		long time = SystemClock.elapsedRealtime();
		RfidTask task = null;
		if (mRfidTasks.size() <= 0) {
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
	}
	
	/**
	 * 已经处理完的任务
	 */
	private void unload(RfidTask task) {
		
	}
}
