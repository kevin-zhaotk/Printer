package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.hardware.UsbSerial;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.TLKFileParser;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ControlTabActivity extends Fragment implements OnClickListener {
	public static final String TAG="ControlTabActivity";
	
	public static final String ACTION_REOPEN_SERIAL="com.industry.printer.ACTION_REOPEN_SERIAL";
	public static final String ACTION_CLOSE_SERIAL="com.industry.printer.ACTION_CLOSE_SERIAL";
	public static final String ACTION_BOOT_COMPLETE="com.industry.printer.ACTION_BOOT_COMPLETED";
	
	public Context mContext;
	public ExtendMessageTitleFragment mMsgTitle;
	public long mCounter;
	public Button mBtnStart;
	public Button mBtnStop;
	public Button mBtnClean;
	public Button mBtnOpen;
	//public Button mGoto;
	//public EditText mDstline;
	
	public Button	mBtnOpenfile;
	public TextView mMsgFile;
	public EditText mMsgPreview;
	public Button 	mBtnview;
	public Button	mForward;
	public Button 	mBackward;
	
	public TextView mRecords;
	
	public LinkedList<Map<String, String>>	mMessageMap;
	public PreviewAdapter mMessageAdapter;
	public ListView mMessageList;
	
	public PreviewScrollView mPreview;
	public Vector<BaseObject> mObjList;
	
	public static int mFd;
	
	public BinInfo mBg;
	
	public static FileInputStream mFileInputStream;
	Vector<Vector<TlkObject>> mTlkList;
	Map<Vector<TlkObject>, byte[]> mBinBuffer;
	/*
	 * whether the print-head is doing print work
	 * if no, poll state Thread will read print-header state
	 * 
	 */
	public boolean isPrinting;
	public boolean isRunning;
	// public PrintingThread mPrintThread;
	public DataTransferThread mDTransThread;
	
	public int mIndex;
	public TextView mPrintStatus;
	public TextView mInkLevel;
	public TextView mPhotocellState;
	public TextView mEncoderState;
	public TextView mPrintState;
	
	/**
	 * UsbSerial device name
	 */
	public String mSerialdev;
	/**
	 * current tlk path opened
	 */
	public String mObjPath=null;
	
	/**
	 * MESSAGE_OPEN_TLKFILE
	 *   message tobe sent when open tlk file
	 */
	public final int MESSAGE_OPEN_TLKFILE=0;
	/**
	 * MESSAGE_UPDATE_PRINTSTATE
	 *   message tobe sent when update print state
	 */
	public final int MESSAGE_UPDATE_PRINTSTATE=1;
	/**
	 * MESSAGE_UPDATE_INKLEVEL
	 *   message tobe sent when update ink level
	 */
	public final int MESSAGE_UPDATE_INKLEVEL=2;
	/**
	 * MESSAGE_DISMISS_DIALOG
	 *   message tobe sent when dismiss loading dialog 
	 */
	public final int MESSAGE_DISMISS_DIALOG=3;
	
	/**
	 * MESSAGE_PAOMADENG_TEST
	 *   message tobe sent when dismiss loading dialog 
	 */
	public final int MESSAGE_PAOMADENG_TEST=4;

	/**
	 * MESSAGE_PRINT_START
	 *   message tobe sent when dismiss loading dialog 
	 */
	public final int MESSAGE_PRINT_START = 5;
	
	/**
	 * MESSAGE_PRINT_STOP
	 *   message tobe sent when dismiss loading dialog 
	 */
	public final int MESSAGE_PRINT_STOP = 6;
		
	
	/**
	 * the bitmap for preview
	 */
	public byte[] mPreBitmap;
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
	 * preview buffer
	 * 	you should use this buffer for preview
	 */
	public byte[] mPreviewBuffer;
	
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
		isPrinting = false;
		mTlkList = new Vector<Vector<TlkObject>>();
		mBinBuffer = new HashMap<Vector<TlkObject>, byte[]>();
		mObjList = new Vector<BaseObject>();
		mContext = this.getActivity();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REOPEN_SERIAL);
		filter.addAction(ACTION_CLOSE_SERIAL);
		filter.addAction(ACTION_BOOT_COMPLETE);
		BroadcastReceiver mReceiver = new SerialEventReceiver(); 
		mContext.registerReceiver(mReceiver, filter);
		
		mMsgFile = (TextView) getView().findViewById(R.id.opened_msg_name);
		
		mPreview = (PreviewScrollView ) getView().findViewById(R.id.sv_preview);
		
		mBtnStart = (Button) getView().findViewById(R.id.StartPrint);
		mBtnStart.setOnClickListener(this);
		
		mBtnStop = (Button) getView().findViewById(R.id.StopPrint);
		mBtnStop.setOnClickListener(this);
		
		mRecords = (TextView) getView().findViewById(R.id.tv_records);
		/*
		 *clean the print head
		 *this command unsupported now 
		 */
		
		mBtnClean = (Button) getView().findViewById(R.id.btnFlush);
		mBtnClean.setOnClickListener(this);
		
				
		mBtnOpenfile = (Button) getView().findViewById(R.id.btnBinfile);
		mBtnOpenfile.setOnClickListener(this);
		
		mMsgPreview = (EditText) getView().findViewById(R.id.message_preview);
		//
//		mPrintState = (TextView) findViewById(R.id.tvprintState);
//		mInkLevel = (TextView) findViewById(R.id.tv_inkValue);
//		mPhotocellState = (TextView) findViewById(R.id.sw_photocell_state);
//		mEncoderState = (TextView) findViewById(R.id.sw_encoder_state);
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//UsbSerial.close(mFd);
	}
	
	public int testdata=0;
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case MESSAGE_OPEN_TLKFILE:		//
					progressDialog();
					String f = ConfigPath.getTlkPath()+"/"+MessageBrowserDialog.getSelected();
					mObjPath = f;
					//startPreview();
					//方案1：从bin文件生成buffer
					if (mDTransThread == null) {
						mDTransThread = DataTransferThread.getInstance(); 
					}
					/**打开一个打印对象，先处理打印数据，从1.bin文件中取出常量的内容**/
					mDTransThread.initDataBuffer(mContext, mObjPath);
					//方案2：从tlk文件重新绘制图片，然后解析生成buffer
					//parseTlk(f);
					//initBgBuffer();
					/**获取打印缩略图，用于预览展现**/
					TLKFileParser parser = new TLKFileParser(f);
					String preview = parser.getContentAbatract();
					mMsgPreview.setText(preview);
					mMsgFile.setText(MessageBrowserDialog.getSelected());
					dismissProgressDialog();
					break;
				case MESSAGE_UPDATE_PRINTSTATE:
					String text = msg.getData().getString("text");
					mPrintStatus.setText("result: "+text);
					break;
				case MESSAGE_UPDATE_INKLEVEL:
					//mPrintDialog.dismiss();
					//updateInkLevel(msg.getData().getByteArray("info"));
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
				
					if (mObjPath == null || mObjPath.isEmpty() || mObjList.size() == 0) {
						Toast.makeText(mContext, "没有可打印的内容", Toast.LENGTH_LONG);
						return;
					}
					/**
					 * 启动打印后要完成的几个工作：
					 * 1、启动DataTransfer线程，生成打印buffer，并下发数据
					 * 2、调用ioctl启动内核线程，开始轮训FPGA状态
					 */
					if (mDTransThread == null) {
						mDTransThread = DataTransferThread.getInstance(); 
					}
					mDTransThread.launch();
					FpgaGpioOperation.init();
					((MainActivity)getActivity()).mCtrlTitle.setText(String.valueOf(mCounter));
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
					}
					
					break;
			}
		}
	};
	
	
	public void startPreview()
	{
		Debug.d(TAG, "===>startPreview");
		
		try{
			mPreviewBuffer = Arrays.copyOf(mPrintBuffer, mPrintBuffer.length);
			BinInfo.Matrix880Revert(mPreviewBuffer);
			mPreBytes = new int[mPreviewBuffer.length*8];
			BinCreater.bin2byte(mPreviewBuffer, mPreBytes);
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
		mLoadingDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.strLoading), true,false);
		mProgressShowing = true;
		mProgressThread = new Thread(){
			
			@Override
			public void run(){
				
				try{
					for(;mProgressShowing==true;)
					{
						Thread.sleep(2000);
					}
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
	boolean mIsDemo=true;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.StartPrint:
				if(mIsDemo) {
					mHandler.sendEmptyMessageDelayed(MESSAGE_PAOMADENG_TEST, 1000);
				} else {
					mHandler.sendEmptyMessage(MESSAGE_PRINT_START);
				}
				break;
			case R.id.StopPrint:
				if (mIsDemo) {
					mHandler.removeMessages(MESSAGE_PAOMADENG_TEST);
				} else {
					mHandler.sendEmptyMessage(MESSAGE_PRINT_STOP);
				}
				break;
			case R.id.btnFlush:
				break;
			case R.id.btnBinfile:
				MessageBrowserDialog dialog = new MessageBrowserDialog(mContext);
				dialog.setOnPositiveClickedListener(new OnPositiveListener() {
					
					@Override
					public void onClick() {
						mHandler.sendEmptyMessage(MESSAGE_OPEN_TLKFILE);
					}
				});
				dialog.show();
				break;
			default:
				break;
		}
		
	}
	
}
