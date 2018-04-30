package com.industry.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.R.color;
import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.industry.printer.MessageTask.MessageType;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Rfid.RfidScheduler;
import com.industry.printer.Rfid.RfidTask;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.FileUtil;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.DataTask;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;

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
	
	public static volatile DataTransferThread mInstance;
	
	private Context mContext;
	
	public boolean mNeedUpdate=false;
	private boolean isBufferReady = false;
	
	private int mcountdown = 0;
	/**打印数据buffer**/
	public List<DataTask> mDataTask;
	/* task index currently printing */
	private int mIndex;
	RfidScheduler	mScheduler;
	private static long mInterval = 0;
	private int mThreshold;
	
	private InkLevelListener mInkListener = null;
	
	public static DataTransferThread getInstance() {
		if(mInstance == null) {
			synchronized (DataTransferThread.class) {
				if (mInstance == null) {
					mInstance = new DataTransferThread();
				}
			}
			Debug.d(TAG, "===>new thread");
		}
		return mInstance;
	}
	
	public DataTransferThread() {
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
		long last = 0;
		/*逻辑要求，必须先发数据*/

		int index = index();
		buffer = mDataTask.get(index).getPrintBuffer();
		int type = mDataTask.get(index).getHeadType();
		
		FileUtil.deleteFolder("/mnt/sdcard/print.bin");
		// save print.bin to /mnt/sdcard/ folder
		BinCreater.saveBin("/mnt/sdcard/print.bin", buffer, mDataTask.get(mIndex).getInfo().mBytesPerHFeed*8*mDataTask.get(mIndex).getHeads());
		
		Debug.e(TAG, "--->write data");
		FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
		last = SystemClock.currentThreadTimeMillis();
		Debug.e(TAG, "--->start print " + mRunning);
		FpgaGpioOperation.init();
		while(mRunning == true) {
			
			// FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
			int writable = FpgaGpioOperation.pollState();
			// writable = 1;
			if (writable == 0) { //timeout
			} else if (writable == -1) {
			} else {
				mInterval = SystemClock.currentThreadTimeMillis() - last;
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				mNeedUpdate = false;
				
				
				if (!mDataTask.get(index()).isReady) {
					mRunning = false;
					if (mCallback != null) {
						mCallback.OnFinished(CODE_BARFILE_END);
					}
					break;
				}
				// Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				
				last = SystemClock.currentThreadTimeMillis();
				countDown();
				mInkListener.onCountChanged();
				mScheduler.schedule();
				if (mCallback != null) {
					mCallback.onComplete();
				}
				next();
				buffer = mDataTask.get(index()).getPrintBuffer();
			}
			
			if(mNeedUpdate == true) {
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				//在此处发生打印数据，同时
				buffer = mDataTask.get(index()).getPrintBuffer();
				Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, MESSAGE_EXCEED_TIMEOUT);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Debug.d(TAG, "===>kernel buffer empty, fill it");
			//TO-DO list 下面需要把打印数据下发
			
		}
		rollback();
		
	}

	private synchronized void next() {
		mIndex++;
		if (mIndex >= mDataTask.size()) {
			mIndex = 0;
		}
	}
	
	public synchronized int index() {
		return mIndex;
	}
	
	public void purge(final Context context) {
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				DataTask task = new DataTask(context, null);
				Debug.e(TAG, "--->task: " + task);
				char[] buffer = task.preparePurgeBuffer();
				Debug.e(TAG, "--->buffer len: " + buffer.length);
				FpgaGpioOperation.updateSettings(context, task, FpgaGpioOperation.SETTING_TYPE_PURGE1);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_PURGE, buffer, buffer.length*2);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FpgaGpioOperation.updateSettings(context, task, FpgaGpioOperation.SETTING_TYPE_PURGE2);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_PURGE, buffer, buffer.length*2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FpgaGpioOperation.clean();
			}
		});
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public boolean launch(Context ctx) {
		mRunning = true;

		DataTransferThread thread = getInstance();
		Debug.d(TAG, "--->thread : " + thread.isRunning());
		if (!isBufferReady || mDataTask == null) {
			return false;
		}
		
		if (mScheduler == null) {
			mScheduler = new RfidScheduler(mContext);
		}
		
		SystemConfigFile configFile = SystemConfigFile.getInstance(ctx);
		mScheduler.init();
		int heads = configFile.getHeads();
		/**如果是4合2的打印头，需要修改为4头*/
		heads = configFile.getParam(SystemConfigFile.INDEX_SPECIFY_HEADS) > 0 ? configFile.getParam(42) : heads;
		for (int i = 0; i < heads; i++) {
			mScheduler.add(new RfidTask(i, mContext));
		}
		mScheduler.load();
		
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
		if (mScheduler == null) {
			return;
		}
		mScheduler.doAfterPrint();
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
	
//	public void initDataBuffer(Context context, MessageTask task) {
//		if (mDataTask == null) {
//			mDataTask = new DataTask(context, task);
//		} else {
//			mDataTask.setTask(task);
//		}
//		Debug.d(TAG, "--->prepare buffer");
//
//		isBufferReady = mDataTask.prepareBackgroudBuffer();
//	}

	public void initDataBuffer(Context context, List<MessageTask> task) {
		if (mDataTask == null) {
			mDataTask = new ArrayList<DataTask>();
		}
		mIndex = 0;
		mDataTask.clear();
		for (MessageTask t : task) {
			DataTask data = new DataTask(mContext, t);
			mDataTask.add(data);
		}
		Debug.d(TAG, "--->prepare buffer: " + mDataTask.size());

		for (DataTask tk : mDataTask) {
			isBufferReady |= tk.prepareBackgroudBuffer();
		}
	}


	public List<DataTask> getData() {
		return mDataTask;
	}

	public DataTask getCurData() {
		return mDataTask.get(index());
	}

	public void setDotCount(List<MessageTask> messages) {
		for (int i = 0; i < mDataTask.size(); i++) {
			DataTask t = mDataTask.get(i);
			if (messages.size() <= i) {
				break;
			}
			t.setDots(messages.get(i).getDots());
		}
		mcountdown = getInkThreshold();
	}
	
	public int getDotCount(DataTask task) {
		if (task == null) {
			return 1;
		}
			
		return task.getDots();
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
			mInkListener.onInkLevelDown();
			return true;
		}
		return false;
	}
	
	public int getCount() {
		
		return mcountdown;
	}
	
	/**
	 * 通过dot count计算RFID减1的阀值
	 * @return
	 */
	public int getInkThreshold() {
		int bold = 1;
		int index = index();
		if (getDotCount(mDataTask.get(index)) <= 0) {
			return 1;
		}
		SystemConfigFile config = SystemConfigFile.getInstance(mContext);
		if (config.getParam(2) <= 0) {
			bold = 1;
		} else {
			bold = config.getParam(2)/150;
		}
		return Configs.DOTS_PER_PRINT*getHeads()/(getDotCount(mDataTask.get(index)) * bold);
	}
	
	public int getHeads() {

		if (mDataTask != null && mDataTask.size() > 0) {
			return mDataTask.get(0).getHeads();
		}
		return 1;
	}
	/**
	 * 打印間隔0~100ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行1步操作
	 * 打印間隔100~200ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行2步操作
	 * 打印間隔200~500ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行4步操作
	 * 打印間隔500~1000ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行8步操作
	 * @return
	 */
	public static long getInterval() {
		if (mInterval >= 1000) {
			return 8;
		} else if (mInterval >= 500) {
			return 4;
		} else if (mInterval >= 200) {
			return 2;
		} else {
			return 1;
		}
	}

	public void refreshCount() {
		mcountdown = getInkThreshold();
	}
	
	private Callback mCallback;
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	
	public interface Callback {
		/**
		 * 整個任務打印完成
		 */
		public void OnFinished(int code);
		/**
		 * 一個任務打印完成
		 */
		public void onComplete();
	}
	
	public static final int CODE_BARFILE_END = 1;
	public static final int CODE_NO_BARFILE = 2;
	
//	private char[] getPrintBuffer() {
//		char[] buffer;
//		int htype = getHeads();
//		// specific process for 9mm header
//		if (htype == MessageType.MESSAGE_TYPE_9MM) {
//			int columns = mDataTask.getBufferColumns();
//			int h = mDataTask.getBufferHeightFeed();
//			char[] b1 = mDataTask.getPrintBuffer();
//			char[] b2 = mDataTask.getPrintBuffer();
//			char[] b3 = mDataTask.getPrintBuffer();
//			char[] b4 = mDataTask.getPrintBuffer();
//			char[] b5 = mDataTask.getPrintBuffer();
//			char[] b6 = mDataTask.getPrintBuffer();
//			buffer = new char[columns * h * 6];
//			for (int i = 0; i < columns; i++) {
//				System.arraycopy(b1, i * h, buffer, i * h *6, h);
//				System.arraycopy(b2, i * h, buffer, i * h * (6 + 1), h);
//				System.arraycopy(b3, i * h, buffer, i * h * (6 + 2), h);
//				System.arraycopy(b4, i * h, buffer, i * h * (6 + 3), h);
//				System.arraycopy(b5, i * h, buffer, i * h * (6 + 4), h);
//				System.arraycopy(b6, i * h, buffer, i * h * (6 + 5), h);
//			}
//		} else {
//			buffer = mDataTask.getPrintBuffer();
//		}
//		return buffer;
//	}
	
	private void rollback() {
		
		if (mDataTask == null) {
			return;
		}
		for (DataTask task : mDataTask) {
			for (BaseObject object : task.getObjList()) {
				if (object instanceof CounterObject) {
					((CounterObject) object).rollback();
				}
			}
		}
	}
}
