package com.industry.printer.hardware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.industry.printer.DataTransferThread;
import com.industry.printer.ThreadPoolManager;
import com.industry.printer.Utils.Debug;

public class RFIDManager {
	
	private static final String TAG = RFIDManager.class.getSimpleName();
	
	private static RFIDManager	mInstance=null;
	private List<RFIDDevice> mRfidDevices = new ArrayList<RFIDDevice>();
	
	public static final int TOTAL_RFID_DEVICES = 4;
	
	public static final int MSG_RFID_INIT_SUCCESS = 101;
	public static final int MSG_RFID_INIT_FAIL = 102;
	public static final int MSG_RFID_WRITE_SUCCESS = 103;
	public static final int MSG_RFID_WRITE_FAIL = 104;
	public static final int MSG_RFID_READ_SUCCESS = 105;
	public static final int MSG_RFID_READ_FAIL = 106;
	
	public static RFIDManager getInstance() {
		if (mInstance == null) {
			Debug.d(TAG, "--->new RfidManager");
			mInstance = new RFIDManager();
		}
		return mInstance;
	}
	
	public void init(final Handler callback) {
		
		mRfidDevices.clear();
		
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				
				for (int i = 0; i < TOTAL_RFID_DEVICES; i++) {
					// 初始化卡
					ExtGpio.rfidSwitch(i);
					// 等待1s，确保RFID建立稳定链接
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					RFIDDevice device = new RFIDDevice();
					device.init();
					mRfidDevices.add(device);
					
				}
				for (int i = 0; i < TOTAL_RFID_DEVICES; i++) {
					Debug.d(TAG, "--->rfid=" + i + "  isReady? " + mRfidDevices.get(i).getLocalInk());
				}
				callback.sendEmptyMessage(MSG_RFID_INIT_SUCCESS);
			}
		});		
		
	}
	
	public void read(final Handler callback) {
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				if (mRfidDevices == null || mRfidDevices.size() <= 0) {
					return;
				}
				Message msg = callback.obtainMessage(MSG_RFID_READ_SUCCESS);
				for (int i=0; i < mRfidDevices.size(); i++) {
					// device.getInkLevel();
					RFIDDevice device = mRfidDevices.get(i);
					if (device.getLocalInk() > 0) {
						continue;
					}
					
					ExtGpio.rfidSwitch(i);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					
					device.cardInit();
					device.getInkLevel();
					Debug.e(TAG, "===>index=" + i + "  level=" + device.getLocalInk());
					
				}
				callback.sendMessage(msg);
				
			}
		});
	}
	
	public float readOne(int id) {
		RFIDDevice device = mRfidDevices.get(id);
		switchRfid(id);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Debug.d(TAG, "--->exception: " + e.getMessage());
		}
		return device.getInkLevel();
	}
	
	/**
	 * 墨水量同步線程，當打印開始後運行這個線程每隔10s自動同步
	 * 
	 * @param callback
	 */
	public void write(final Handler callback) {
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				if (mRfidDevices == null || mRfidDevices.size() <= 0) {
					return;
				}
				DataTransferThread mThread = DataTransferThread.getInstance();
				for(;mThread.isRunning();) {
					
					for (RFIDDevice device : mRfidDevices) {
						if (!device.mReady) {
							continue;
						}
						device.updateToDevice();
					}
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Message msg = callback.obtainMessage(MSG_RFID_WRITE_SUCCESS);
				// callback.sendMessage(msg);
			}
		});
	}
	
	public void switchRfid(final int i) {
		final RFIDDevice device = mRfidDevices.get(i);
		device.setReady(false);
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				Debug.e(TAG, "--->switch");
				ExtGpio.rfidSwitch(i);
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
				Debug.e(TAG, "--->init blind");
				// device.cardInitBlind();
				
				// device.cardInit();
				device.setReady(true);
			}
		});
	}
	
	public float getLocalInk(int dev) {
		if (dev >= mRfidDevices.size()) {
			return 0;
		}
		RFIDDevice device = mRfidDevices.get(dev);
		if (device == null) {
			return 0;
		}
		int max = device.getMax();
		float ink = device.getLocalInk();
		if (max <= 0) {
			return 0;
		} else if (max < ink) {
			return 100;
		}
		return (ink*100/max);
	}
	
	public void downLocal(int dev) {
		if (dev >= mRfidDevices.size()) {
			return ;
		}
		RFIDDevice device = mRfidDevices.get(dev);
		if (device == null) {
			return ;
		}
		device.down();
	}
	
	public boolean isReady(int dev) {
		if (dev >= mRfidDevices.size()) {
			return false;
		}
		RFIDDevice device = mRfidDevices.get(dev);
		return device.isReady();
	}
	
	public boolean isValid(int dev) {
		if (dev >= mRfidDevices.size()) {
			return false;
		}
		RFIDDevice device = mRfidDevices.get(dev);
		return device.isValid();
	}
	
	public RFIDDevice getDevice(int index) {
		if (index >= mRfidDevices.size()) {
			return null;
		}
		return mRfidDevices.get(index);
	}
}
