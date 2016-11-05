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
import com.industry.printer.Utils.RFIDAsyncTask.RfidCallback;
import com.industry.printer.data.RFIDData;

public class RFIDManager implements RfidCallback{
	
	private static final String TAG = RFIDManager.class.getSimpleName();
	
	private static RFIDManager	mInstance=null;
	private List<RFIDDevice> mRfidDevices = new ArrayList<RFIDDevice>();
	private int mCurrent=0;
	private RFIDDevice mDevice;
	private Handler mCallback;
	
	public static final int TOTAL_RFID_DEVICES = 8;
	
	public static final int MSG_RFID_INIT_SUCCESS = 101;
	public static final int MSG_RFID_INIT_FAIL = 102;
	public static final int MSG_RFID_WRITE_SUCCESS = 103;
	public static final int MSG_RFID_WRITE_FAIL = 104;
	public static final int MSG_RFID_READ_SUCCESS = 105;
	public static final int MSG_RFID_READ_FAIL = 106;
	public static final int MSG_RFID_INIT = 107;
	
	public static final int MSG_RFID_SWITCH_DEVICE = 1;
	public static final int MSG_RFID_INIT_NEXT = 2;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_RFID_SWITCH_DEVICE:
				mDevice.removeListener(RFIDManager.this);
				Debug.d(TAG, "--->dev: " + mCurrent + "  ink:" + mDevice.getLocalInk());
				mCurrent++;
				if (mCurrent >= TOTAL_RFID_DEVICES) {
					Debug.d(TAG, "--->rfid init success");
					mCallback.sendEmptyMessageDelayed(MSG_RFID_READ_SUCCESS, 100);
					break;
				}
				mDevice = mRfidDevices.get(mCurrent);
				mDevice.addLisetener(RFIDManager.this);
				if (mDevice.getLocalInk() > 0) {
					mHandler.sendEmptyMessageDelayed(MSG_RFID_SWITCH_DEVICE, 1000);
					break;
				}
				ExtGpio.rfidSwitch(mCurrent);
				mHandler.sendEmptyMessageDelayed(MSG_RFID_INIT_NEXT, 1000);	
				break;
			case MSG_RFID_INIT_NEXT:
				if (mDevice.getLocalInk() > 0) {
					mHandler.sendEmptyMessage(MSG_RFID_SWITCH_DEVICE);
					break;
				}
				if (RFIDDevice.isNewModel) {
					mDevice.autoSearch(false);
				} else {
					mDevice.lookForCards(false);
				}
				// mDevice.connect();
				break;
			}
		}
	};
	public static RFIDManager getInstance() {
		if (mInstance == null) {
			Debug.d(TAG, "--->new RfidManager");
			mInstance = new RFIDManager();
		}
		return mInstance;
	}
	
	
	public void init(final Handler callback) {
		mCallback = callback;
		Debug.d(TAG, "--->init");
		mCurrent = 0;
		ExtGpio.rfidSwitch(mCurrent);

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		
		if (mRfidDevices.size() == 0) {
			
			for (int i = 0; i < TOTAL_RFID_DEVICES; i++) {
				RFIDDevice device = new RFIDDevice();
				mRfidDevices.add(device);
			}
		}
		
		mDevice = mRfidDevices.get(mCurrent);
		mDevice.addLisetener(this);
		mHandler.sendEmptyMessage(MSG_RFID_INIT_NEXT);
		// mDevice.connect();
		/*
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				
				for (int i = 0; i < TOTAL_RFID_DEVICES; i++) {
					Debug.d(TAG, "--->RFID init: " + i);
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
					Debug.d(TAG, "--->rfid=" + i + "  isReady? " + mRfidDevices.get(i).isReady());
				}
				
				callback.sendEmptyMessage(MSG_RFID_INIT_SUCCESS);
			}
		});		
		*/
	}
	
	public void checkRfid() {
		
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

	@Override
	public void onFinish(RFIDData data) {
		if (data == null) {
			Debug.d(TAG, "--->rfid response null");
			mHandler.sendEmptyMessageDelayed(MSG_RFID_SWITCH_DEVICE, 2000);
			return;
		}
		switch(data.getCommand()) {
			case RFIDDevice.RFID_CMD_CONNECT:
				mDevice.lookForCards(false);
				break;
			case RFIDDevice.RFID_CMD_SEARCHCARD:
				if (RFIDDevice.isNewModel) {
					mDevice.autoSearch(false);
				} else {
					mDevice.avoidConflict(false);
				}
				break;
			case RFIDDevice.RFID_CMD_MIFARE_CONFLICT_PREVENTION:
				if (!mDevice.selectCard(mDevice.mSN, false)) {
					mHandler.sendEmptyMessage(MSG_RFID_SWITCH_DEVICE);
				}
				break;
			case RFIDDevice.RFID_CMD_MIFARE_CARD_SELECT:
				mDevice.keyVerfication(RFIDDevice.SECTOR_INK_MAX, RFIDDevice.BLOCK_INK_MAX, mDevice.mRFIDKeyA);
				break;
			case RFIDDevice.RFID_CMD_AUTO_SEARCH:
				if (!mDevice.getReady()) {
					mHandler.sendEmptyMessageDelayed(MSG_RFID_SWITCH_DEVICE, 200);
				} else {
					mDevice.readBlock(RFIDDevice.SECTOR_INK_MAX, RFIDDevice.BLOCK_INK_MAX, mDevice.mRFIDKeyA);
				}
				break;
			case RFIDDevice.RFID_CMD_READ_VERIFY:
				if (mDevice.getState() == RFIDDevice.STATE_RFID_MAX_READY) {
					mDevice.readBlock(RFIDDevice.SECTOR_FEATURE, RFIDDevice.BLOCK_FEATURE, mDevice.mRFIDKeyA);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_FEATURE_READY) {
					mDevice.readBlock(RFIDDevice.SECTOR_INKLEVEL, RFIDDevice.BLOCK_INKLEVEL, mDevice.mRFIDKeyA);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_VALUE_READY) {
					// mDevice.readBlock(RFIDDevice.SECTOR_INKLEVEL, RFIDDevice.BLOCK_INKLEVEL, mDevice.mRFIDKeyA);
					mHandler.sendEmptyMessageDelayed(MSG_RFID_SWITCH_DEVICE, 200);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_BACKUP_READY) {
					
				}
				break;
			case RFIDDevice.RFID_CMD_MIFARE_KEY_VERIFICATION:
				byte[] rfid = data.getData();
				// mDevice.isCorrect(rfid);
				if (mDevice.getState() == RFIDDevice.STATE_RFID_MAX_KEY_VERFIED) {
					mDevice.readBlock(RFIDDevice.SECTOR_INK_MAX, RFIDDevice.BLOCK_INK_MAX);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_FEATURE_KEY_VERFYED) {
					mDevice.readBlock(RFIDDevice.SECTOR_FEATURE, RFIDDevice.BLOCK_FEATURE);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_VALUE_KEY_VERFYED) {
					mDevice.readBlock(RFIDDevice.SECTOR_INKLEVEL, RFIDDevice.BLOCK_INKLEVEL);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_BACKUP_KEY_VERFYED) {
				}
				break;
			case RFIDDevice.RFID_CMD_MIFARE_READ_BLOCK:
				if (mDevice.getState() == RFIDDevice.STATE_RFID_MAX_READY) {
					mDevice.keyVerfication(RFIDDevice.SECTOR_FEATURE, RFIDDevice.BLOCK_FEATURE, mDevice.mRFIDKeyA);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_FEATURE_READY) {
					mDevice.keyVerfication(RFIDDevice.SECTOR_INKLEVEL, RFIDDevice.BLOCK_INKLEVEL, mDevice.mRFIDKeyA);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_VALUE_READY) {
					mHandler.sendEmptyMessageDelayed(MSG_RFID_SWITCH_DEVICE, 200);
				} else if (mDevice.getState() == RFIDDevice.STATE_RFID_BACKUP_READY) {
					
				}
				break;
			default:
				break;
		}
	}
}
