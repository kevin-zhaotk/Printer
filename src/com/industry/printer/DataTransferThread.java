package com.industry.printer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.industry.printer.Utils.Debug;
import com.industry.printer.data.DataTask;
import com.industry.printer.hardware.FpgaGpioOperation;

/**
 * class DataTransferThread
 * 用一个独立的线程读取fpga的buffer状态，
 * 如果kernel已经把打印数据发送给FPGA，那么kernel的Buffer状态为空，可写
 * 此时，需要把下一条打印数据下发给kernel Buffer；
 * 如果kernel的buffer状态不为空，不可写
 * 此时，线程轮训buffer，直到kernel buffer状态为空；
 * @author kevin
 *
 */
public class DataTransferThread extends Thread {
	
	public static final String TAG = DataTransferThread.class.getSimpleName();
	
	public static boolean mRunning;
	
	public static DataTransferThread mInstance;
	
	public boolean mNeedUpdate=false;
	
	/**打印数据buffer**/
	public DataTask mDataTask;
	
	public static DataTransferThread getInstance() {
		if(mInstance == null) {
			Debug.d(TAG, "===>new thread");
			mInstance = new DataTransferThread();
		}
		return mInstance;
	}
	
	/**
	 * 数据更新机制：
	 * 每次发送数据时同时触发一个delay 10s的handler
	 * 如果pollState返回不为0，即数据打印完毕，则remove handlermessage
	 * 否则，处理这个message并置数据更新状态为true
	 * run函数中一旦检测到数据更新状态变为true，就重新生成buffer并下发
	 */
	@Override
	public void run() {
		
		while(mRunning == true) {
			
			int writable = FpgaGpioOperation.pollState();
			if (writable == 0) { //timeout
				Debug.d(TAG, "===>select timeout");
			} else if (writable == -1) {
				
			} else {
				
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				mNeedUpdate = false;
				//在此处发生打印数据，同时
				//Debug.d(TAG, "===>kernel buffer empty, fill it");
				char[] buffer = mDataTask.getPrintBuffer();
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				//mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, 10000);
			}
			
			if(mNeedUpdate == true) {
				//在此处发生打印数据，同时
				//mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, 10000);
			}
			//Debug.d(TAG, "===>kernel buffer empty, fill it");
			//TO-DO list 下面需要把打印数据下发
		}
		
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public void launch() {
		mRunning = true;
		DataTransferThread thread = getInstance();
		thread.start();
	}
	
	public void finish() {
		mRunning = false;
		
		DataTransferThread t = mInstance;
		mInstance = null;
		t.mHandler.removeMessages(MESSAGE_DATA_UPDATE);
		t.interrupt();
	}
	
	
	public static final int MESSAGE_DATA_UPDATE = 1;
	
	public Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_DATA_UPDATE:
					mNeedUpdate = true;
					break;
			}
		}
	};
	
	public void initDataBuffer(Context context, String obj) {
		if (mDataTask == null) {
			mDataTask = new DataTask(context);
		}
		mDataTask.prepareBackgroudBuffer(obj);
	}
	
}
