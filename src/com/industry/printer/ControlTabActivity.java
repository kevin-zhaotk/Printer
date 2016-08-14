package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.Utils.PrinterDBHelper;
import com.industry.printer.Utils.RFIDAsyncTask;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.data.DataTask;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.hardware.LRADCBattery;
import com.industry.printer.hardware.PWMAudio;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;
import com.industry.printer.hardware.RTCDevice;
import com.industry.printer.hardware.UsbSerial;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.TlkObject;
import com.industry.printer.ui.ExtendMessageTitleFragment;
import com.industry.printer.ui.CustomerAdapter.PreviewAdapter;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase.OnPositiveListener;
import com.industry.printer.ui.CustomerDialog.MessageBrowserDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ControlTabActivity extends Fragment implements OnClickListener, InkLevelListener, OnTouchListener {
	public static final String TAG="ControlTabActivity";
	
	public static final String ACTION_REOPEN_SERIAL="com.industry.printer.ACTION_REOPEN_SERIAL";
	public static final String ACTION_CLOSE_SERIAL="com.industry.printer.ACTION_CLOSE_SERIAL";
	public static final String ACTION_BOOT_COMPLETE="com.industry.printer.ACTION_BOOT_COMPLETED";
	
	public Context mContext;
	public ExtendMessageTitleFragment mMsgTitle;
	public int mCounter;
	public RelativeLayout mBtnStart;
	public TextView		  mTvStart; 
	public RelativeLayout mBtnStop;
	public TextView		  mTvStop;
	public RelativeLayout mBtnClean;
	public TextView		  mTvClean;
	public Button mBtnOpen;
	public TextView		  mTvOpen;
	//public Button mGoto;
	//public EditText mDstline;
	
	public RelativeLayout	mBtnOpenfile;
	public HorizontalScrollView mScrollView;
	public TextView mMsgFile;
	// public EditText mMsgPreview;
	public TextView mMsgPreview;
	public ImageView mMsgPreImg;
	public Button 	mBtnview;
	public RelativeLayout	mForward;
	public RelativeLayout 	mBackward;
	
	public TextView mRecords;
	
	public LinkedList<Map<String, String>>	mMessageMap;
	public PreviewAdapter mMessageAdapter;
	public ListView mMessageList;
	
	public PreviewScrollView mPreview;
	public ArrayList<BaseObject> mObjList;
	
	public static int mFd;
	
	public BinInfo mBg;
	BroadcastReceiver mReceiver;
	public Handler mCallback;
	
	public static FileInputStream mFileInputStream;
	Vector<Vector<TlkObject>> mTlkList;
	Map<Vector<TlkObject>, byte[]> mBinBuffer;
	/*
	 * whether the print-head is doing print work
	 * if no, poll state Thread will read print-header state
	 * 
	 */
	public boolean isRunning;
	// public PrintingThread mPrintThread;
	public DataTransferThread mDTransThread;
	
	public int mIndex;
	public TextView mPrintStatus;
	public TextView mInkLevel;
	public TextView mInkLevel2;
	public TextView mTVPrinting;
	public TextView mTVStopped;
	public TextView mPhotocellState;
	public TextView mEncoderState;
	public TextView mPrintState;
	public TextView mPower;
	
	public SystemConfigFile mSysconfig;
	/**
	 * UsbSerial device name
	 */
	public String mSerialdev;
	
	private RFIDDevice mRfidDevice;
	private RFIDManager mRfidManager;
	/**
	 * current tlk path opened
	 */
	public String mObjPath=null;
	
	/**
	 * MESSAGE_OPEN_TLKFILE
	 *   message tobe sent when open tlk file
	 */
	public static final int MESSAGE_OPEN_TLKFILE=0;
	/**
	 * MESSAGE_UPDATE_PRINTSTATE
	 *   message tobe sent when update print state
	 */
	public static final int MESSAGE_UPDATE_PRINTSTATE=1;
	/**
	 * MESSAGE_UPDATE_INKLEVEL
	 *   message tobe sent when update ink level
	 */
	public static final int MESSAGE_UPDATE_INKLEVEL=2;
	/**
	 * MESSAGE_DISMISS_DIALOG
	 *   message tobe sent when dismiss loading dialog 
	 */
	public static final int MESSAGE_DISMISS_DIALOG=3;
	
	/**
	 * MESSAGE_PAOMADENG_TEST
	 *   message tobe sent when dismiss loading dialog 
	 */
	public static final int MESSAGE_PAOMADENG_TEST=4;

	/**
	 * MESSAGE_PRINT_START
	 *   message tobe sent when dismiss loading dialog 
	 */
	public static final int MESSAGE_PRINT_START = 5;
	
	/**
	 * MESSAGE_PRINT_STOP
	 *   message tobe sent when dismiss loading dialog 
	 */
	public static final int MESSAGE_PRINT_STOP = 6;
	
	/**
	 * MESSAGE_INKLEVEL_DOWN
	 *   message tobe sent when ink level change 
	 */
	public static final int MESSAGE_INKLEVEL_CHANGE = 7;
	
	/**
	 * MESSAGE_COUNT_CHANGE
	 *   message tobe sent when count change 
	 */
	public static final int MESSAGE_COUNT_CHANGE = 8;
	
	public static final int MESSAGE_REFRESH_POWERSTAT = 9;
	
	/**
	 * the bitmap for preview
	 */
	private Bitmap mPreBitmap;
	/**
	 * 
	 */
	public int[]	mPreBytes;
	
	/**
	 * background buffer
	 *   used for save the background bin buffer
	 *   fill the variable buffer into this background buffer so we get printing buffer
	 */
	public byte[] mBgBuffer;
	/**
	 *printing buffer
	 *	you should use this buffer for print
	 */
	public byte[] mPrintBuffer;
	
	/**
	 * 当前打印任务
	 */
	public MessageTask mMsgTask;
	/**
	 * preview buffer
	 * 	you should use this buffer for preview
	 */
	public byte[] mPreviewBuffer;
	
	private boolean mFeatureCorrect = false;
	
	public ControlTabActivity() {
		//mMsgTitle = (ExtendMessageTitleFragment)fragment;
		mCounter = 0;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		return inflater.inflate(R.layout.control_frame, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {	
		super.onActivityCreated(savedInstanceState);
		mIndex=0;
		mTlkList = new Vector<Vector<TlkObject>>();
		mBinBuffer = new HashMap<Vector<TlkObject>, byte[]>();
		mObjList = new ArrayList<BaseObject>();
		mContext = this.getActivity();
		mSysconfig = SystemConfigFile.getInstance(mContext);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REOPEN_SERIAL);
		filter.addAction(ACTION_CLOSE_SERIAL);
		filter.addAction(ACTION_BOOT_COMPLETE);
		mReceiver = new SerialEventReceiver(); 
		mContext.registerReceiver(mReceiver, filter);
		
		mMsgFile = (TextView) getView().findViewById(R.id.opened_msg_name);
		
		mPreview = (PreviewScrollView ) getView().findViewById(R.id.sv_preview);
		
		mBtnStart = (RelativeLayout) getView().findViewById(R.id.StartPrint);
		mBtnStart.setOnClickListener(this);
		mBtnStart.setOnTouchListener(this);
		mTvStart = (TextView) getView().findViewById(R.id.tv_start);
		
		mBtnStop = (RelativeLayout) getView().findViewById(R.id.StopPrint);
		mBtnStop.setOnClickListener(this);
		mBtnStop.setOnTouchListener(this);
		mTvStop = (TextView) getView().findViewById(R.id.tv_stop);
		//mRecords = (TextView) getView().findViewById(R.id.tv_records);
		/*
		 *clean the print head
		 *this command unsupported now 
		 */
		
		mBtnClean = (RelativeLayout) getView().findViewById(R.id.btnFlush);
		mBtnClean.setOnClickListener(this);
		mBtnClean.setOnTouchListener(this);
		mTvClean = (TextView) getView().findViewById(R.id.tv_flush);
				
		mBtnOpenfile = (RelativeLayout) getView().findViewById(R.id.btnBinfile);
		mBtnOpenfile.setOnClickListener(this);
		mBtnOpenfile.setOnTouchListener(this);
		mTvOpen = (TextView) getView().findViewById(R.id.tv_binfile);
		
		setupViews();
		
		mTVPrinting = (TextView) getView().findViewById(R.id.tv_printState);
		mTVStopped = (TextView) getView().findViewById(R.id.tv_stopState);
		
		
		switchState(STATE_STOPPED);
		mScrollView = (HorizontalScrollView) getView().findViewById(R.id.preview_scroll);
		// mMsgPreview = (TextView) getView().findViewById(R.id.message_preview);
		mMsgPreImg = (ImageView) getView().findViewById(R.id.message_prev_img);
		//
//		mPrintState = (TextView) findViewById(R.id.tvprintState);
		mInkLevel = (TextView) getView().findViewById(R.id.ink_value);
		mInkLevel2 = (TextView) getView().findViewById(R.id.ink_value2);
		
		mPower = (TextView) getView().findViewById(R.id.power_state);
//		mPhotocellState = (TextView) findViewById(R.id.sw_photocell_state);
//		mEncoderState = (TextView) findViewById(R.id.sw_encoder_state);
		
		refreshPower();
		//  加载打印计数
		PrinterDBHelper db = PrinterDBHelper.getInstance(mContext);
		//mCounter = db.getCount(mContext);
		RTCDevice rtcDevice = RTCDevice.getInstance(mContext);

		// 如果是第一次启动，向RTC的NVRAM写入0
		if (PlatformInfo.PRODUCT_SMFY_SUPER3.equalsIgnoreCase(PlatformInfo.getProduct())) {
			rtcDevice.initSystemTime(mContext);
			mCounter = rtcDevice.readCounter(mContext);
			if (mCounter == 0) {
				rtcDevice.writeCounter(mContext, 0);
				db.setFirstBoot(mContext, false);
			}
		}
		
		refreshCount();
		
		/***PG1 PG2输出状态为 0x11，清零模式**/
		FpgaGpioOperation.clean();
		
		/****初始化RFID****/
		/*
		mRfidDevice = RFIDDevice.getInstance();
		if (mRfidDevice.init() != 0) {
			Toast.makeText(mContext, R.string.str_rfid_initfail_notify, Toast.LENGTH_LONG);
			refreshInk(0);
		} else {
//			float ink = mRfidDevice.getInkLevel();
//			mFeatureCorrect = mRfidDevice.checkFeatureCode();
//			refreshInk(ink);
			RFIDAsyncTask.getInstance(mHandler).execute();
		}
		*/
		mRfidManager = RFIDManager.getInstance();
		mRfidManager.init(mHandler);
		//Debug.d(TAG, "===>loadMessage");
		// 通过监听系统广播加载
		loadMessage();
		
	}
	
	private void setupViews() {
		if (PlatformInfo.PRODUCT_FRIENDLY_4412.equalsIgnoreCase(PlatformInfo.getProduct())) {
			mForward.setVisibility(View.GONE);
			mBackward.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroy()
	{
		mContext.unregisterReceiver(mReceiver);
		super.onDestroy();
		//UsbSerial.close(mFd);
	}
	
	public void loadMessage() {
		String f = mSysconfig.getLastMsg();
		Debug.d(TAG, "===>load message: " + f);
		if (f == null || f.isEmpty() || !new File(ConfigPath.getTlkDir(f)).exists()) {
			return;
		}
		Message msg = mHandler.obtainMessage(MESSAGE_OPEN_TLKFILE);
		Bundle bundle = new Bundle();
		bundle.putString("file", f);
		msg.setData(bundle);
		mHandler.sendMessageDelayed(msg, 1000);
	}
	
	private void refreshInk(int dev, float ink) {
		/*
		if (mRfidDevice.mInkMax <= 0) {
			ink = 0;
		} else if (ink > mRfidDevice.mInkMax) {
			ink = 100;
		} else {
			ink = (ink * 100)/mRfidDevice.mInkMax;
		}
		*/
		
		String level = String.valueOf((int)ink);// + "%");
		mInkLevel.setText(level);
		if (!mFeatureCorrect) {
			// level = String.format(getResources().getString(R.string.str_state_inklevel), "--");
			if (dev == 1) {
				mInkLevel.setText(level);
			} else {
				mInkLevel2.setText(level);
			}
			
		} else if (ink <= 0) {
			mInkLevel.setBackgroundColor(Color.RED);
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.ink_alarm_animation);
			if (mInkLevel.getAnimation() == null) {
				mInkLevel.setAnimation(animation);
				//mInkLevel.startAnimation(animation);
			}
		} else {
			mInkLevel.clearAnimation();
		}
		
	}
	
	private void refreshCount() {
		// String cFormat = getResources().getString(R.string.str_print_count);
		// ((MainActivity)getActivity()).mCtrlTitle.setText(String.format(cFormat, mCounter));
		((MainActivity)getActivity()).mCtrlTitle.setText(String.valueOf(mCounter));
	}
	
	private void refreshPower() {
		if (PlatformInfo.PRODUCT_SMFY_SUPER3.equalsIgnoreCase(PlatformInfo.getProduct())) {
			int power = LRADCBattery.getPower();
			// String pwState = String.format(getString(R.string.str_state_battery), power);
			mPower.setText(String.valueOf(power));
			mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_POWERSTAT, 5*60*1000);
		}
	}
	
	public int testdata=0;
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case MESSAGE_OPEN_TLKFILE:		//
					progressDialog();
					mObjPath = msg.getData().getString("file", null);
					Debug.d(TAG, "open tlk :" + mObjPath );
					//startPreview();
					if (mObjPath == null) {
						break;
					}
					
					//方案2：从tlk文件重新绘制图片，然后解析生成buffer
					//parseTlk(f);
					//initBgBuffer();
					/**获取打印缩略图，用于预览展现**/
					mMsgTask = new MessageTask(mContext, mObjPath);
					mObjList = mMsgTask.getObjects();
					//TLKFileParser parser = new TLKFileParser(mContext, mObjPath);
					//String preview = parser.getContentAbatract();
					String preview = mMsgTask.getAbstract();
					if (preview == null) {
						preview = getString(R.string.str_message_no_content);
					}
					// mMsgPreview.setText(new SpanableStringFormator(mObjList));
					// mMsgPreImg.setImageURI(Uri.parse("file://" + mMsgTask.getPreview()));
					if (mPreBitmap != null) {
						BinFromBitmap.recyleBitmap(mPreBitmap);
					}
					//方案1：从bin文件生成buffer
					initDTThread();
					Debug.d(TAG, "--->init thread ok");
					// mPreBitmap = BitmapFactory.decodeFile(mMsgTask.getPreview());
					mPreBitmap = mDTransThread.mDataTask.getPreview();
					mMsgPreImg.setImageBitmap(mPreBitmap);
					mMsgFile.setText(mMsgTask.getName());
					mSysconfig.saveLastMsg(mObjPath);
					dismissProgressDialog();
					
					break;
				case MESSAGE_UPDATE_PRINTSTATE:
					String text = msg.getData().getString("text");
					mPrintStatus.setText("result: "+text);
					break;
				case MESSAGE_UPDATE_INKLEVEL:
					Bundle bundle = msg.getData();
					int level = bundle.getInt("ink_level");
					mFeatureCorrect = bundle.getBoolean("feature", true);
					refreshInk(1, level);
					break;
				case MESSAGE_DISMISS_DIALOG:
					mLoadingDialog.dismiss();
					break;
				case MESSAGE_PAOMADENG_TEST:
					
					char[] data = new char[32];
					for (char i = 0; i < 15; i++) {
						data[2*i] = (char)(0x01<<i);
						data[2*i+1] = 0xffff;
					}
					data[30] = 0xff;
					data[31] = 0xff;
//					char[] data = new char[2];
//					if (testdata < 0 || testdata > 15)
//						testdata = 0;
//					data[0] = (char) (0x0001 << testdata);
//					data[1] = (char) (0x0001 << testdata);
//					testdata++;
					FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT,data, data.length*2);
					mHandler.sendEmptyMessageDelayed(MESSAGE_PAOMADENG_TEST, 1000);
					break;
				case MESSAGE_PRINT_START:
					
					/*if (mRfidManager.getLocalInk(0) <= 0 || !mRfidManager.isReady(0)) {
						Toast.makeText(mContext, R.string.str_toast_no_ink, Toast.LENGTH_LONG).show();
						return;
					}*/
					if (mDTransThread != null && mDTransThread.isRunning()) {
						Toast.makeText(mContext, R.string.str_print_printing, Toast.LENGTH_LONG).show();
						break;
					}
					if (mObjPath == null || mObjPath.isEmpty()) {
						Toast.makeText(mContext, R.string.str_toast_no_message, Toast.LENGTH_LONG).show();
						break;
					}
					//Debug.d(TAG, "--->initDTThread");
					if (mDTransThread == null) {
						initDTThread();
					}
					Debug.d(TAG, "--->prepare buffer");
					DataTask dt = mDTransThread.getData();
					
					if (dt == null || dt.getObjList() == null || dt.getObjList().size() == 0) {
						Toast.makeText(mContext, R.string.str_toast_emptycontent, Toast.LENGTH_LONG).show();
						break;
					}
					/**
					 * 测试buffer生成是否正确，按打印按钮把打印内容保存到u盘
					 */
//					char[] buf = dt.getPrintBuffer();
//					Debug.d(TAG, "--->save print bin");
//					ArrayList<String> usbs = ConfigPath.getMountedUsb();
//					if (usbs != null && usbs.size() > 0) {
//						String path = usbs.get(0);
//						File file = new File(path + "/print.bin");
//						if (file.exists()) {
//							file.delete();
//						}
//						BinCreater.saveBin( path + "/print.bin", buf, dt.mBinInfo.getBytesFeed() * 8);
//					}
					Debug.d(TAG, "--->clean");
					/**
					 * 启动打印后要完成的几个工作：
					 * 1、每次打印，  先清空 （见文件）， 然后 发设置
					 * 2、启动DataTransfer线程，生成打印buffer，并下发数据
					 * 3、调用ioctl启动内核线程，开始轮训FPGA状态
					 */
					/*打印过程中禁止切换打印对象*/
					switchState(STATE_PRINTING);
					FpgaGpioOperation.clean();
					Debug.d(TAG, "--->update settings");
					FpgaGpioOperation.updateSettings(mContext, dt, FpgaGpioOperation.SETTING_TYPE_NORMAL);
					Debug.d(TAG, "--->launch thread");
					/*打印对象在openfile时已经设置，所以这里直接启动打印任务即可*/
					if (!mDTransThread.launch()) {
						Toast.makeText(mContext, R.string.str_toast_no_bin, Toast.LENGTH_LONG);
						break;
					}
					Debug.d(TAG, "--->finish TrheadId=" + Thread.currentThread().getId());
					// FpgaGpioOperation.init();
					Toast.makeText(mContext, R.string.str_print_startok, Toast.LENGTH_LONG).show();
					break;
				case MESSAGE_PRINT_STOP:
					/**
					 * 停止打印后要完成的几个工作：
					 * 1、调用ioctl停止内核线程，停止轮训FPGA状态
					 * 2、停止DataTransfer线程
					 */
					FpgaGpioOperation.uninit();
					if (mDTransThread != null) {
						mDTransThread.finish();
						mDTransThread = null;
					}
					/*打印任务停止后允许切换打印对象*/
					switchState(STATE_STOPPED);
					
					Toast.makeText(mContext, R.string.str_print_stopok, Toast.LENGTH_LONG).show();
					FpgaGpioOperation.clean();
					/* 如果當前打印信息中有計數器，需要記錄當前值到TLK文件中*/
					updateCntIfNeed();
					
					break;
				case MESSAGE_INKLEVEL_CHANGE:
					mRfidManager.downLocal(0);
					refreshInk(1, mRfidManager.getLocalInk(0));
					// mRfidManager.write(mHandler);
					break;
				case MESSAGE_COUNT_CHANGE:
					mCounter++;
					refreshCount();
					//PrinterDBHelper db = PrinterDBHelper.getInstance(mContext);
					//db.updateCount(mContext, (int) mCounter);
					RTCDevice device = RTCDevice.getInstance(mContext);
					device.writeCounter(mContext, mCounter);
					break;
				case MESSAGE_REFRESH_POWERSTAT:
					refreshPower();
					break;
				case RFIDManager.MSG_RFID_INIT_SUCCESS:
					mRfidManager.read(mHandler);
					break;
				case RFIDManager.MSG_RFID_READ_SUCCESS:
					Bundle bd = (Bundle) msg.getData();
					float il = bd.getFloat("level");
					if (il <= 0) {
						mHandler.sendEmptyMessageDelayed(RFIDManager.MSG_RFID_INIT_SUCCESS, 3000);
					}
					refreshInk(bd.getInt("device", 1),il);
					break;
				case RFIDManager.MSG_RFID_WRITE_SUCCESS:
					float ink = mRfidManager.getLocalInk(0);
					refreshInk(1, ink);
					break;
			}
		}
	};
	
	private void updateCntIfNeed() {
		for (BaseObject object : mMsgTask.getObjects()) {
			if (object instanceof CounterObject) {
				Message msg = new Message();
				msg.what = MainActivity.UPDATE_COUNTER;
				msg.arg1 = 17;
				mCallback.sendMessage(msg);
				break;
			}
		}
	}
	
	public void initDTThread() {
		
		if (mDTransThread == null) {
			Debug.d(TAG, "--->Print thread ready");
			mDTransThread = DataTransferThread.getInstance();	
		}
		Debug.d(TAG, "--->init");
		
		// 初始化buffer
		mDTransThread.initDataBuffer(mContext, mMsgTask);
		// TLKFileParser parser = new TLKFileParser(mContext, mObjPath);
		// 设置dot count
		mDTransThread.setDotCount(mMsgTask.getDots());
		// 设置UI回调
		mDTransThread.setOnInkChangeListener(this);
		
	}
	
	private final int STATE_PRINTING = 0;
	private final int STATE_STOPPED = 1;
	
	public void switchState(int state) {
		Debug.d(TAG, "--->switchState=" + state);
		switch(state) {
			case STATE_PRINTING:
				mBtnStart.setClickable(false);
				mTvStart.setTextColor(Color.DKGRAY);
				mBtnStop.setClickable(true);
				mTvStop.setTextColor(Color.BLACK);
				mBtnOpenfile.setClickable(false);
				mTvOpen.setTextColor(Color.DKGRAY);
				mTVPrinting.setVisibility(View.VISIBLE);
				mTVStopped.setVisibility(View.GONE);
				mBtnClean.setEnabled(false);
				mTvClean.setTextColor(Color.DKGRAY);
				break;
			case STATE_STOPPED:
				mBtnStart.setClickable(true);
				mTvStart.setTextColor(Color.BLACK);
				mBtnStop.setClickable(false);
				mTvStop.setTextColor(Color.DKGRAY);
				mBtnOpenfile.setClickable(true);
				mTvOpen.setTextColor(Color.BLACK);
				mTVPrinting.setVisibility(View.GONE);
				mTVStopped.setVisibility(View.VISIBLE);
				mBtnClean.setEnabled(true);
				mTvClean.setTextColor(Color.BLACK);
				break;
			default:
				Debug.d(TAG, "--->unknown state");
		}
	}
	
	
	public void startPreview()
	{
		Debug.d(TAG, "===>startPreview");
		
		try{
			mPreviewBuffer = Arrays.copyOf(mPrintBuffer, mPrintBuffer.length);
			BinInfo.Matrix880Revert(mPreviewBuffer);
			mPreBytes = new int[mPreviewBuffer.length*8];
			// BinCreater.bin2byte(mPreviewBuffer, mPreBytes);
			mPreview.createBitmap(mPreBytes, mBgBuffer.length/110, Configs.gDots);
			mPreview.invalidate();
			
			//mPreviewRefreshHandler.sendEmptyMessage(0);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void onCheckUsbSerial()
	{
		mSerialdev = null;
		File file = new File("/dev");
		if(file.listFiles() == null)
		{
			return ;
		}
		File[] files = file.listFiles(new PrinterFileFilter("ttyACM"));
		for(File f : files)
		{
			if(f == null)
			{
				break;
			}
			Debug.d(TAG, "file = "+f.getName());
			int fd = UsbSerial.open("/dev/"+f.getName());
			Debug.d(TAG, "open /dev/"+f.getName()+" return "+fd);
			if(fd < 0)
			{
				Debug.d(TAG, "open usbserial /dev/"+f.getName()+" fail");
				continue;
			}
			UsbSerial.close(fd);
			mSerialdev = "/dev/"+f.getName();
			break;
		}
	}

	public class SerialEventReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Debug.d(TAG, "******intent="+intent.getAction());
			if(ACTION_REOPEN_SERIAL.equals(intent.getAction()))
			{
				onCheckUsbSerial();
			}
			else if(ACTION_CLOSE_SERIAL.equals(intent.getAction()))
			{
				Debug.d(TAG, "******close");
				mSerialdev = null;
			}
			else if(ACTION_BOOT_COMPLETE.equals(intent.getAction()))
			{
				onCheckUsbSerial();
				byte info[] = new byte[23];
				UsbSerial.printStart(mSerialdev);
				UsbSerial.getInfo(mSerialdev, info);
				//updateInkLevel(info);
				UsbSerial.printStop(mSerialdev);
			}
		}
		
	}
	
	
	public int calculateBufsize(Vector<TlkObject> list)
	{
		int length=0;
		for(int i=0; i<list.size(); i++)
		{
			Debug.d(TAG,"calculateBufsize list i="+i);
			TlkObject o = list.get(i);
			if(o.isTextObject())	//each text object take over 16*16/8 * length=32Bytes*length
			{
				Debug.d(TAG,"content="+o.mContent);
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
				int l = font.getColumns();				
				length = (l*o.mContent.length()+o.x) > length?(l*o.mContent.length()+o.x):length;
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				length = (o.x+128) > length?(o.x+128):length;
			}
		}
		return length;
	}
	
	/*
	 * make set param buffer
	 * 1.  Byte 2-3,param 00,	reserved
	 * 2.  Byte 4-5,param 01,	print speed, unit HZ,43kHZ for highest
	 * 3.  Byte 6-7, param 02,	delay,unit: 0.1mmm
	 * 13. Byte 8-9, param 03,	reserved
	 * 14. Byte 10-11, param 04,triger 00 00  on, 00 01 off
	 * 15. Byte 12-13, param 05,sync  00 00  on, 00 01 off
	 * 16. Byte 14-15, param 06
	 * 17. Byte 16-17, param 07, length, unit 0.1mm
	 * 18. Byte 18-19, param 08, timer, unit ms
	 * 19. Byte 20-21, param 09, print head Temperature
	 * 20. Byte 20-21, param 10,  Ink cartridge Temperature
	 * 21. others reserved  
	 */
	public static void makeParams(Context context, byte[] params)
	{
		if(params==null || params.length<128)
		{
			Debug.d(TAG,"params is null or less than 128, realloc it");
			params = new byte[128];
		}
		SharedPreferences preference = context.getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		int speed = preference.getInt(SettingsTabActivity.PREF_PRINTSPEED, 0);
		params[2] = (byte) ((speed>>8)&0xff);
		params[3] = (byte) ((speed)&0xff);
		int delay = preference.getInt(SettingsTabActivity.PREF_DELAY, 0);
		params[4] = (byte) ((delay>>8)&0xff);
		params[5] = (byte) ((delay)&0xff);
		int triger = (int)preference.getLong(SettingsTabActivity.PREF_TRIGER, 0);
		params[8] = 0x00;
		params[9] = (byte) (triger==0?0x00:0x01);
		int encoder = (int)preference.getLong(SettingsTabActivity.PREF_ENCODER, 0);
		params[10] = 0x00;
		params[11] = (byte) (encoder==0?0x00:0x01);
		int bold = preference.getInt(SettingsTabActivity.PREF_BOLD, 0);
		params[12] = (byte) ((bold>>8)&0xff);
		params[13] = (byte) ((bold)&0xff);
		int fixlen = preference.getInt(SettingsTabActivity.PREF_FIX_LEN, 0);
		params[14] = (byte) ((fixlen>>8)&0xff);
		params[15] = (byte) ((fixlen)&0xff);
		int fixtime= preference.getInt(SettingsTabActivity.PREF_FIX_TIME, 0);
		params[16] = (byte) ((fixtime>>8)&0xff);
		params[17] = (byte) ((fixtime)&0xff);
		int headtemp = preference.getInt(SettingsTabActivity.PREF_HEAD_TEMP, 0);
		params[18] = (byte) ((headtemp>>8)&0xff);
		params[19] = (byte) ((headtemp)&0xff);
		int resvtemp = preference.getInt(SettingsTabActivity.PREF_RESV_TEMP, 0);
		params[20] = (byte) ((resvtemp>>8)&0xff);
		params[21] = (byte) ((resvtemp)&0xff);
		int fontwidth = preference.getInt(SettingsTabActivity.PREF_FONT_WIDTH, 0);
		params[22] = (byte) ((fontwidth>>8)&0xff);
		params[23] = (byte) ((fontwidth)&0xff);
		int dots = preference.getInt(SettingsTabActivity.PREF_DOT_NUMBER, 0);
		params[24] = (byte) ((dots>>8)&0xff);
		params[25] = (byte) ((dots)&0xff);
		int resv12 = preference.getInt(SettingsTabActivity.PREF_RESERVED_12, 0);
		params[26] = (byte) ((resv12>>8)&0xff);
		params[27] = (byte) ((resv12)&0xff);
		int resv13 = preference.getInt(SettingsTabActivity.PREF_RESERVED_13, 0);
		params[28] = (byte) ((resv13>>8)&0xff);
		params[29] = (byte) ((resv13)&0xff);
		int resv14 = preference.getInt(SettingsTabActivity.PREF_RESERVED_14, 0);
		params[30] = (byte) ((resv14>>8)&0xff);
		params[31] = (byte) ((resv14)&0xff);
		int resv15 = preference.getInt(SettingsTabActivity.PREF_RESERVED_15, 0);
		params[32] = (byte) ((resv15>>8)&0xff);
		params[33] = (byte) ((resv15)&0xff);
		int resv16 = preference.getInt(SettingsTabActivity.PREF_RESERVED_16, 0);
		params[34] = (byte) ((resv16>>8)&0xff);
		params[35] = (byte) ((resv16)&0xff);
		int resv17 = preference.getInt(SettingsTabActivity.PREF_RESERVED_17, 0);
		params[36] = (byte) ((resv17>>8)&0xff);
		params[37] = (byte) ((resv17)&0xff);
		int resv18 = preference.getInt(SettingsTabActivity.PREF_RESERVED_18, 0);
		params[38] = (byte) ((resv18>>8)&0xff);
		params[39] = (byte) ((resv18)&0xff);
		int resv19 = preference.getInt(SettingsTabActivity.PREF_RESERVED_19, 0);
		params[40] = (byte) ((resv19>>8)&0xff);
		params[41] = (byte) ((resv19)&0xff);
		int resv20 = preference.getInt(SettingsTabActivity.PREF_RESERVED_20, 0);
		params[42] = (byte) ((resv20>>8)&0xff);
		params[43] = (byte) ((resv20)&0xff);
		int resv21 = preference.getInt(SettingsTabActivity.PREF_RESERVED_21, 0);
		params[44] = (byte) ((resv21>>8)&0xff);
		params[45] = (byte) ((resv21)&0xff);
		int resv22 = preference.getInt(SettingsTabActivity.PREF_RESERVED_22, 0);
		params[46] = (byte) ((resv22>>8)&0xff);
		params[47] = (byte) ((resv22)&0xff);
		int resv23 = preference.getInt(SettingsTabActivity.PREF_RESERVED_23, 0);
		params[48] = (byte) ((resv23>>8)&0xff);
		params[49] = (byte) ((resv23)&0xff);
		
	}
	
	/**
	 * the loading dialog
	 */
	public ProgressDialog mLoadingDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		if (mProgressShowing || (mLoadingDialog != null && mLoadingDialog.isShowing())) {
			return;
		}
		mLoadingDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.strLoading), true,false);
		Debug.d(TAG, "===>show loading");
		mProgressShowing = true;
		mProgressThread = new Thread(){
			
			@Override
			public void run(){
				
				try{
					for(;mProgressShowing==true;)
					{
						Thread.sleep(2000);
					}
					Debug.d(TAG, "===>dismiss loading");
					mHandler.sendEmptyMessage(MESSAGE_DISMISS_DIALOG);
				}catch(Exception e)
				{
					
				}
			}
		};
		mProgressThread.start();
	}
	
	public void dismissProgressDialog()
	{
		mProgressShowing=false;
	}
	
	int mdata=0;
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.StartPrint:
				//mHandler.sendEmptyMessageDelayed(MESSAGE_PAOMADENG_TEST, 1000);
				mHandler.sendEmptyMessage(MESSAGE_PRINT_START);
				break;
			case R.id.StopPrint:
				// mHandler.removeMessages(MESSAGE_PAOMADENG_TEST);
					mHandler.sendEmptyMessage(MESSAGE_PRINT_STOP);
				break;
			/*清洗打印头（一个特殊的打印任务），需要单独的设置：参数2必须为 4，参数4为200， 参数5为20；*/
			case R.id.btnFlush:
				DataTransferThread thread = DataTransferThread.getInstance();
				thread.purge(mContext);
				break;
			case R.id.btnBinfile:
				MessageBrowserDialog dialog = new MessageBrowserDialog(mContext);
				dialog.setOnPositiveClickedListener(new OnPositiveListener() {
					
					@Override
					public void onClick() {
						String f = MessageBrowserDialog.getSelected();
						if (f==null || f.isEmpty()) {
							return;
						}
						Message msg = mHandler.obtainMessage(MESSAGE_OPEN_TLKFILE);

						Bundle bundle = new Bundle();
						bundle.putString("file", f);
						msg.setData(bundle);
						mHandler.sendMessage(msg);
					}

					@Override
					public void onClick(String content) {
						// TODO Auto-generated method stub
						
					}
					
				});
				dialog.show();
				break;
			case R.id.btn_page_forward:
				mScrollView.smoothScrollBy(-400, 0);
				break;
			case R.id.btn_page_backward:
				mScrollView.smoothScrollBy(400, 0);
				break;
			default:
				break;
		}
		
	}

	@Override
	public void onInkLevelDown() {
		mHandler.sendEmptyMessage(MESSAGE_INKLEVEL_CHANGE);
	}

	@Override
	public void onInkEmpty() {
		
	}

	@Override
	public void onCountChanged() {
		mHandler.sendEmptyMessage(MESSAGE_COUNT_CHANGE);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch(view.getId()) {
			case R.id.StartPrint:
			case R.id.StopPrint:
			case R.id.btnFlush:
			case R.id.btnBinfile:
			case R.id.btn_page_forward:
			case R.id.btn_page_backward:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					PWMAudio.Play();
				}
			default:
				break;
		}
		return false;
	}
	
	
	public void setCallback(Handler callback) {
		mCallback = callback;
	}
}
