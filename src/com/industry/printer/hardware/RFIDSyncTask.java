package com.industry.printer.hardware;

import com.industry.printer.Utils.Debug;

public class RFIDSyncTask extends Thread {
	
	public static final String TAG = RFIDSyncTask.class.getSimpleName();

	public static RFIDSyncTask mInstance;
	
	public boolean mRunning = false;
	
	public static RFIDSyncTask getInstance() {
		if(mInstance == null) {
			mInstance = new RFIDSyncTask();
		}
		return mInstance;
	}
	
	
	@Override
	public void run() {
		while(mRunning) {
			RFIDDevice device = RFIDDevice.getInstance();
			device.updateToDevice();
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public void start() {
		
	}
}
