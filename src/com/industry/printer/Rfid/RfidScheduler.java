package com.industry.printer.Rfid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.industry.printer.DataTransferThread;
import com.industry.printer.ThreadPoolManager;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.ExtGpio;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;

import android.app.AlarmManager;
import android.os.SystemClock;
import android.provider.AlarmClock;

public class RfidScheduler {
	
	private String TAG = RfidScheduler.class.getSimpleName();
	
	public static RfidScheduler mInstance = null;
	// 5S间隔
	public static final long TASK_SCHEDULE_INTERVAL = 3000;
	public static final long RFID_SWITCH_INTERVAL = 1000;
	
	private List<RfidTask> mRfidTasks = null;
	private int mCurrent = 0;
	private long mSwitchTimeStemp=0;
	private Thread mAfter;
	private boolean running=false;
	private RFIDManager mManager;
	
	public static RfidScheduler getInstance() {
		if (mInstance == null) {
			mInstance = new RfidScheduler();
		}
		return mInstance;
	}
	
	public RfidScheduler() {
		mRfidTasks = new ArrayList<RfidTask>();
		mManager = RFIDManager.getInstance();
	}
	
	public void init() {
		running = false;
		if (mAfter != null) {
			mAfter.interrupt();
			mAfter = null;
		}
		removeAll();
		mCurrent = 0;
		mManager.switchRfid(mCurrent);
	}
	
	public void add(RfidTask task) {
		if (mRfidTasks == null) {
			mRfidTasks = new ArrayList<RfidTask>();
		}
		mRfidTasks.add(task);
	}
	
	/**
	 * Rfid調度函數
	 * 	打印間隔0~100ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行1步操作
	 *  打印間隔100~200ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行2步操作
	 *  打印間隔200~500ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行4步操作
	 *  打印間隔500~1000ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行8步操作
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
		for (int i = 0; i < DataTransferThread.getInterval(); i++) {
			task.execute();
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
			if(task.getStat() >= RfidTask.STATE_BACKUP_SYNCED) {
				break;
			}
			
		}
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
				mManager.switchRfid(mCurrent);
				Debug.e(TAG, "--->sync inklevel after print finish...");
				while(running && mCurrent < mRfidTasks.size()) {
					try {
						if (last != mCurrent) {
							last = mCurrent;
							Thread.sleep(1000);
						} else {
							Thread.sleep(50);
						}
						
					} catch (Exception e) {
					}
					RfidTask task = mRfidTasks.get(mCurrent);
					if ((mCurrent == mRfidTasks.size() -1) && task.getStat() >= RfidTask.STATE_BACKUP_SYNCED) {
						break;
					}
					schedule();
				}
				Debug.e(TAG, "--->sync inklevel after print finish ok");
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
		if (mRfidTasks.size() - 1 <= mCurrent || mCurrent < 0) {
			 mCurrent = 0;
		} else {
			mCurrent++;
		}
		Debug.e(TAG, "--->loadNext");
		// ExtGpio.rfidSwitch(mCurrent);
		if (mRfidTasks.size() > 1) {
			mManager.switchRfid(mCurrent);
		}
		
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
