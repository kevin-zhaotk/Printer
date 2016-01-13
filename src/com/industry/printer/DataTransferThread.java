package com.industry.printer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.io.WriteAbortedException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PrinterDBHelper;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.DataTask;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.object.BaseObject;

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
	private static final int MESSAGE_EXCEED_TIMEOUT = 60 * 1000;
	
	public static boolean mRunning;
	
	public static DataTransferThread mInstance;
	
	public boolean mNeedUpdate=false;
	private boolean isBufferReady = false;
	
	private int mcountdown = 0;
	/**打印数据buffer**/
	public DataTask mDataTask;
	
	private InkLevelListener mInkListener = null;
	
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
		
		char[] buffer;
		/*逻辑要求，必须先发数据*/
		Debug.d(TAG, "--->printbuffer: TreadId=" + Thread.currentThread().getId());
		buffer = mDataTask.getPrintBuffer();
		Debug.d(TAG, "--->write data");
		FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
		Debug.d(TAG, "--->start print");
		FpgaGpioOperation.init();
		while(mRunning == true) {
			
			// Debug.d(TAG, "===>buffer size="+buffer.length);
			// FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
			
			int writable = FpgaGpioOperation.pollState();
			Debug.d(TAG, "--->writeable=" + writable);
			// writable = 1;
			if (writable == 0) { //timeout
				Debug.d(TAG, "===>select timeout");
			} else if (writable == -1) {
				
			} else {
				
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				mNeedUpdate = false;
				//在此处发生打印数据，同时
				//Debug.d(TAG, "===>kernel buffer empty, fill it");
				// 只有打印数据中有变量时才重新下发数据，否则不需要重新下发
				//if (mDataTask.isNeedRefresh()) {
				buffer = mDataTask.getPrintBuffer();
				Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				//}
				
				//mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, MESSAGE_EXCEED_TIMEOUT);
				// 保存打印计数
				// 墨水量count down计算
				//if (countDown()) {
				//	mInkListener.onInkLevelDown();
				//}
				mInkListener.onInkLevelDown();
				mInkListener.onCountChanged();
			}
			
			if(mNeedUpdate == true) {
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				//在此处发生打印数据，同时
				buffer = mDataTask.getPrintBuffer();
				Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, MESSAGE_EXCEED_TIMEOUT);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Debug.d(TAG, "===>kernel buffer empty, fill it");
			//TO-DO list 下面需要把打印数据下发
		}
		
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public boolean launch() {
		mRunning = true;
		DataTransferThread thread = getInstance();
		if (!isBufferReady) {
			return false;
		}
		thread.start();
		return true;
	}
	
	public void finish() {
		mRunning = false;
		
		DataTransferThread t = mInstance;
		
		mInstance = null;
		mHandler.removeMessages(MESSAGE_DATA_UPDATE);
		if (t != null) {
			t.interrupt();
		}
	}
	
	public void setOnInkChangeListener(InkLevelListener listener) {
		mInkListener = listener;
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
	
	public void initDataBuffer(Context context, MessageTask task) {
		if (mDataTask == null) {
			mDataTask = new DataTask(context, task);
		} else {
			mDataTask.setTask(task);
		}
		isBufferReady = mDataTask.prepareBackgroudBuffer();
		
	}
	
	public DataTask getData() {
		return mDataTask;
	}
																																																																																																																
	public void setDotCount(int count) {
		if (mDataTask == null) {
			return;
		}
		mDataTask.setDots(count);
	}
	
	public int getDotCount() {
		if (mDataTask == null) {
			return 0;
		}
			
		return mDataTask.getDots();
	}
	
	/**
	 * 倒计数，当计数倒零时表示墨水量需要减1，同时倒计数回归
	 * @return true 墨水量需要减1； false 墨水量不变
	 */
	private boolean countDown() {
		mcountdown--;
		if (mcountdown <= 0) {
			// 赋初值
			mcountdown = getInkThreshold();
			return true;
		}
		return false;
	}
	
	/**
	 * 通过dot count计算RFID减1的阀值
	 * @return
	 */
	private int getInkThreshold() {
		return Configs.DOTS_PER_PRINT;
	}

}
