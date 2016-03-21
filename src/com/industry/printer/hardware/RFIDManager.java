package com.industry.printer.hardware;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.industry.printer.ThreadPoolManager;

public class RFIDManager {
	
	private static final String TAG = RFIDManager.class.getSimpleName();
	
	private static RFIDManager	mInstance=null;
	private List<RFIDDevice> mRfidDevices = new ArrayList<RFIDDevice>();
	
	private static final int TOTAL_RFID_DEVICES = 1;
	
	public static final int MSG_RFID_INIT_SUCCESS = 101;
	public static final int MSG_RFID_INIT_FAIL = 102;
	public static final int MSG_RFID_WRITE_SUCCESS = 103;
	public static final int MSG_RFID_WRITE_FAIL = 104;
	public static final int MSG_RFID_READ_SUCCESS = 105;
	public static final int MSG_RFID_READ_FAIL = 106;
	
	public static RFIDManager getInstance() {
		if (mInstance == null) {
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
					HardwareJni.rfidSwitch(i);
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
				for (RFIDDevice device : mRfidDevices) {
					// device.getInkLevel();
					Bundle bundle = new Bundle();
					bundle.putFloat("level", device.getInkLevel());
					msg.setData(bundle);
				}
				callback.sendMessage(msg);
			}
		});
	}
	
	
	public void write(final Handler callback) {
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				if (mRfidDevices == null || mRfidDevices.size() <= 0) {
					return;
				}
				for (RFIDDevice device : mRfidDevices) {
					device.updateInkLevel();
				}
				Message msg = callback.obtainMessage(MSG_RFID_WRITE_SUCCESS);
				callback.sendMessage(msg);
			}
		});
	}
	
}
