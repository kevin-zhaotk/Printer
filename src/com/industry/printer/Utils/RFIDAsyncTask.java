package com.industry.printer.Utils;

import com.industry.printer.ControlTabActivity;
import com.industry.printer.data.RFIDData;
import com.industry.printer.hardware.RFIDDevice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RFIDAsyncTask extends AsyncTask<Void, Integer, Void> {

	public Handler mHandler;
	public int mInk;
	public static RFIDAsyncTask mInstance;
	
	public static RFIDAsyncTask getInstance(Handler handler) {
		if (mInstance == null) {
			mInstance = new RFIDAsyncTask(handler);
		}
		return mInstance;
	}
	
	public RFIDAsyncTask(Handler handler) {
		mHandler = handler;
		mInk = 0;
	}
	@Override  
    protected void onPreExecute() {  
    }
	@Override
	protected Void doInBackground(Void...params) {
		RFIDDevice device = RFIDDevice.getInstance();
		while (true) {
			mInk = (int) device.getInkLevel();
			if(mInk >= RFIDDevice.INK_LEVEL_MIN && mInk <= RFIDDevice.INK_LEVEL_MAX) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(true) {
			boolean feature = device.checkFeatureCode();
			if (feature) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void onPostExecute(Integer integer) {
		Message msg = mHandler.obtainMessage(ControlTabActivity.MESSAGE_UPDATE_INKLEVEL);
		Bundle bundle = new Bundle();
		bundle.putInt("ink_level", mInk);
		bundle.putBoolean("feature", true);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
    }
}
