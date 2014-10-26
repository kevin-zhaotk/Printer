package com.industry.printer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.FileBrowserDialog.OnPositiveListener;
import com.industry.printer.FileFormat.CsvReader;
import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.FileFormat.FilenameSuffixFilter;
import com.industry.printer.FileFormat.Tlk_Parser;
import com.industry.printer.Usb.CRC16;
import com.industry.printer.Usb.UsbConnector;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.Fileparser;
import com.industry.printer.object.RealtimeDate;
import com.industry.printer.object.RealtimeHour;
import com.industry.printer.object.RealtimeMinute;
import com.industry.printer.object.RealtimeMonth;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RealtimeYear;

import com.industry.printer.object.TlkObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ControlTabActivity extends Activity{
	public static final String TAG="ControlTabActivity";
	
	public static final String ACTION_REOPEN_SERIAL="com.industry.printer.ACTION_REOPEN_SERIAL";
	public static final String ACTION_CLOSE_SERIAL="com.industry.printer.ACTION_CLOSE_SERIAL";
	public static final String ACTION_BOOT_COMPLETE="com.industry.printer.ACTION_BOOT_COMPLETED";
	
	public Context mContext;
	
	
	public Button mBtnStart;
	public Button mBtnStop;
	public Button mBtnClean;
	public Button mBtnOpen;
	public Button mBtnSend;
	public Button mSetParam;
	public Button mGetInfo;
	public Button mPrint;
	public Button mFinish;
	public Button mBtnNext;
	public Button mBtnPrev;
	//public Button mGoto;
	//public EditText mDstline;
	
	public Button	mBtnfile;
	public Button	mBtnTlkfile;
	public Button	mBtnOpenfile;
	public TextView mMsgFile;
	public Button 	mBtnview;
	public Button	mForward;
	public Button 	mBackward;
	public LinkedList<Map<String, String>>	mMessageMap;
	public PreviewAdapter mMessageAdapter;
	public ListView mMessageList;
	
	public PreviewScrollView mPreview;
	public Vector<BaseObject> mObjList;
	
	public ScrollView mThumb1;
	public ScrollView mThumb2;
	public ScrollView mThumb3;
	public ScrollView mThumb4;
	
	public Button mHead1;
	public Button mOpen1;
	public Button mOpen2;
	public Button mOpen3;
	public Button mOpen4;
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
	public PrintingThread mPrintThread;
	
	
	public int mIndex;
	public TextView mPrintStatus;
	public TextView mInkLevel;
	public TextView mPhotocellState;
	public TextView mEncoderState;
	public TextView mPrintState;
	
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
	 *	you should use this buffer for preview
	 */
	public byte[] mPrintBuffer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_frame);
		mIndex=0;
		isPrinting = false;
		mTlkList = new Vector<Vector<TlkObject>>();
		mBinBuffer = new HashMap<Vector<TlkObject>, byte[]>();
		mObjList = new Vector<BaseObject>();
		mContext = this.getApplicationContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REOPEN_SERIAL);
		filter.addAction(ACTION_CLOSE_SERIAL);
		filter.addAction(ACTION_BOOT_COMPLETE);
		BroadcastReceiver mReceiver = new SerialEventReceiver(); 
		registerReceiver(mReceiver, filter);
		
		String dev = "/dev/ttyACM0";
		//String dev = "/dev/ttySAC0";
		//mFd = UsbSerial.open(dev);
		openSerial();
		//Debug.d(TAG,"open "+dev+"="+mFd);
		//if(mFd == -1)
		//	return ;
		//UsbSerial.setBaudrate(mFd, 115200);
		//UsbSerial.set_options(mFd, 8, 1, 'n');
		//mFileInputStream = new FileInputStream(mFd);

		
		mPreview = (PreviewScrollView ) findViewById(R.id.sv_preview);
		
		mBtnStart = (Button) findViewById(R.id.StartPrint);
		mBtnStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mPrintBuffer==null || mObjList==null || mObjList.isEmpty())
					return;
				preparePrintBuffer();
				print();
			}
			
		});
		
		mBtnStop = (Button) findViewById(R.id.StopPrint);
		mBtnStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				UsbSerial.printStop(mFd);
			}
			
		});
		
		/*
		 *clean the print head
		 *this command unsupported now 
		 */
		
		mBtnClean = (Button) findViewById(R.id.btnFlush);
		mBtnClean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "***********clean head");
				//UsbSerial.printStart(mFd);
				UsbSerial.clean(mFd);
				//UsbSerial.printStop(mFd);
			}
			
		});
		
				
		mBtnOpenfile = (Button) findViewById(R.id.btnBinfile);
		mBtnOpenfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog dialog = new FileBrowserDialog(ControlTabActivity.this, DotMatrixFont.USB_PATH,".tlk");
				dialog.setOnPositiveClickedListener(new OnPositiveListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String f = FileBrowserDialog.file();
						Debug.d(TAG, "-------f="+f);
						if(f==null || !f.toLowerCase().endsWith(".tlk"))
						{
							Toast.makeText(mContext, "please select a csv file", Toast.LENGTH_LONG);
							return;
						}
						// TODO show bin
						mHandler.sendEmptyMessage(MESSAGE_OPEN_TLKFILE);
						
					}
					
				});
				dialog.show();
			}
			
		});
		
		
		
		mBtnview = (Button)findViewById(R.id.btn_preview);
		mBtnview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DotMatrixFont dot = new DotMatrixFont("/mnt/usb/font.txt");
				//preparePrintBuffer();
				startPreview();
			}
			
		});
		
		//
		mPrintState = (TextView) findViewById(R.id.tvprintState);
		mInkLevel = (TextView) findViewById(R.id.tv_inkValue);
		mPhotocellState = (TextView) findViewById(R.id.sw_photocell_state);
		mEncoderState = (TextView) findViewById(R.id.sw_encoder_state);
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		UsbSerial.close(mFd);
	}
	
	@Override
	public boolean  onKeyDown(int keyCode, KeyEvent event)  
	{
		Debug.d(TAG, "keycode="+keyCode);
		
		if(keyCode==KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
		{
			Debug.d(TAG, "back key pressed, ignore it");
			return true;	
		}
		return false;
	}
	
	/**
	 * print
	 * 打印接口，下发打印数据，并定时查询打印状态，自动更新墨水值，打印返回成功后结束Thread
	 */
	public void print()
	{
		byte[] buffer=null;
		String text="";
		UsbSerial.printStart(mFd);
		UsbSerial.sendSetting(mFd);
		byte[] data = new byte[128];
		makeParams(mContext,data);
		UsbSerial.sendSettingData(mFd, data);
		
		if(mPrintBuffer==null)
			return;
		
		UsbSerial.sendDataCtrl(mFd, mPrintBuffer.length);
		UsbSerial.printData(mFd,  mPrintBuffer);
		//mPrintDialog = ProgressDialog.show(ControlTabActivity.this, "", getResources().getString(R.string.strwaitting));
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				int timeout=10;
				byte[] info = new byte[23];
				do
				{
					try{
						Thread.sleep(2000);
					}catch(Exception e)
					{}
					
					UsbSerial.getInfo(mFd, info);
					Message msg = new Message();
					msg.what=MESSAGE_UPDATE_INKLEVEL;
					Bundle b= new Bundle();
					b.putByteArray("info", info);
					msg.setData(b);
					mHandler.sendMessage(msg);
					Debug.d(TAG, "##########timeout = "+timeout+",stat="+info[9]);
				}while(info[9]!=4);
				
			}
		}).start();
		
	}
	
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case MESSAGE_OPEN_TLKFILE:		//
					progressDialog();
					String f = FileBrowserDialog.file();
					if(f !=null && !new File(f).isDirectory())
					{
						Debug.d(TAG, "open object file "+f);
						mObjPath = new File(f).getParent();
						//startPreview();
						prepareBackgroudBuffer(f);
					}
					dismissProgressDialog();
					break;
				case MESSAGE_UPDATE_PRINTSTATE:
					String text = msg.getData().getString("text");
					mPrintStatus.setText("result: "+text);
					break;
				case MESSAGE_UPDATE_INKLEVEL:
					//mPrintDialog.dismiss();
					updateInkLevel(msg.getData().getByteArray("info"));
					break;
				case MESSAGE_DISMISS_DIALOG:
					mLoadingDialog.dismiss();
					break;
			}
		}
	};
	
	public void setContent(String index,Vector<TlkObject> list)
	{
		for(TlkObject o:list)
		{
			for(int i=1;i<mMessageList.getCount(); i++)
			{
				Map<String, String> m = (Map<String, String>)mMessageList.getItemAtPosition(i);
				Debug.d(TAG, "*******index="+m.get("index"));
				 if((o.index>=1 && o.index<=16)&&m.get("index").equals(index))
                 {
                     Debug.d(TAG, "index "+o.index+" found"+", text"+o.index+"="+m.get("text"+o.index));
                     o.setContent(m.get("text"+o.index));
                     break;
                 }
                 else if(o.index>=21 && o.index<=24 && m.get("index").equals(index))
				 {
                	 int logo=Integer.parseInt(o.font);
                	 Debug.d(TAG, "index "+o.index+" found"+", pic"+(o.font)+"="+m.get("pic"+logo));
                	 String pic=m.get("pic"+logo);
                	 o.setContent(BaseObject.intToFormatString(Integer.parseInt(pic), 4));
                	 o.setFont(BaseObject.intToFormatString(Integer.parseInt(pic), 4));
                	 break;
				 }
			}
		}
		
	}
	public void startPreview()
	{
		if(mObjList==null || mObjList.isEmpty() || mPrintBuffer==null)
			return;
		try{
			 mPreBytes = new int[mPrintBuffer.length*8];
			 BinCreater.bin2byte(mPrintBuffer, mPreBytes);
			 mPreview.createBitmap(mPreBytes, mBg.mColumn, mBg.mBitsperColumn);
			 mPreview.invalidate();
			 //mPreviewRefreshHandler.sendEmptyMessage(0);
			}catch(Exception e)
			{
				Debug.d(TAG, "startPreview e: "+e.getMessage());
			}
		
	}
	
	/**
	 * prepareBackgroudBuffer
	 * @param f	the tlk object directory path
	 * parse the 1.bin, and then read the file content into mBgBuffer, one bit extends to one byte
	 */
	public void prepareBackgroudBuffer(String f)
	{
		String path=null;
		File fp = new File(f);
		if(fp.isFile())
			path = new File(f).getParent();
		else
			path = f;
		Fileparser.parse(mContext, f, mObjList);
		try{
			mBg = new BinInfo();
			mBg.getBgBuffer(path+"/1.bin");
			//read the background bin bytes to global mBgBuffer
			mBgBuffer = Arrays.copyOf(mBg.mBits, mBg.mBits.length);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * preparePrintBuffer
	 *   fill the variable buffer into background buffer
	 */
	public void preparePrintBuffer()
	{
		if(mObjList==null || mObjList.isEmpty())
			return;
		mPrintBuffer= Arrays.copyOf(mBgBuffer, mBgBuffer.length);
		refreshVariables(mPrintBuffer);
	}
	
	public void refreshVariables(byte[] buffer)
	{
		Bitmap bm=null;
		String substr=null;
		ByteArrayBuffer bytes=new ByteArrayBuffer(0);
		if(mObjList==null || mObjList.isEmpty())
			return;
		//mPreBitmap = Arrays.copyOf(mBg.mBits, mBg.mBits.length);
		for(BaseObject o:mObjList)
		{
			bytes.clear();
			bytes.setLength(0);
			Debug.d(TAG, "refreshVariables object = "+o.mId);
			if(o instanceof CounterObject)
			{
				String str = ((CounterObject) o).getNext();
				BinInfo varbin = new BinInfo();
				try {
					varbin.getVarBuffer(str, mObjPath+"/" + "v" + o.getIndex() +".bin");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BinCreater.overlap(buffer, varbin.mBits, (int)o.getX(), 0, varbin.mBitsperColumn);
			}
			else if(o instanceof RealtimeObject)
			{
				
				Vector<BaseObject> rt = ((RealtimeObject) o).getSubObjs();
				for(BaseObject rtSub : rt)
				{
					bytes.clear();
					bytes.setLength(0);
					if(rtSub instanceof RealtimeYear)
					{
						substr = ((RealtimeYear)rtSub).getContent();
					}
					else if(rtSub instanceof RealtimeMonth)
					{
						substr = ((RealtimeMonth)rtSub).getContent();
						//continue;
					}
					else if(rtSub instanceof RealtimeDate)
					{
						substr = ((RealtimeDate)rtSub).getContent();
						//continue;
					} 
					else if(rtSub instanceof RealtimeHour)
					{
						substr = ((RealtimeHour)rtSub).getContent();
					} 
					else if(rtSub instanceof RealtimeMinute)
					{
						substr = ((RealtimeMinute)rtSub).getContent();
					}
					else
						continue;
					BinInfo varbin = new BinInfo();
					try {
						varbin.getVarBuffer(substr, mObjPath+"/" + "v"+rtSub.getIndex() +".bin");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BinCreater.overlap(buffer, varbin.mBits, (int)rtSub.getX(), 0, varbin.mBitsperColumn);
				}				
			}
			else
			{
				Debug.d(TAG, "not Variable object");
			}
		}
	}
	
	public boolean openSerial()
	{
		File file = new File("/dev");
		if(file.listFiles() == null)
			return false;
		File[] files = file.listFiles(new PrinterFileFilter("ttyACM"));
		for(File f : files)
		{
			if(f == null)
			{
				mFd = 0;
				return false;
			}
			Debug.d(TAG, "file = "+f.getName());
			mFd = UsbSerial.open("/dev/"+f.getName());
			Debug.d(TAG, "open /dev/"+f.getName()+" return "+mFd);
			if(mFd < 0)
			{
				Debug.d(TAG, "open usbserial /dev/"+f.getName()+" fail");
				continue;
			}
			Debug.d(TAG, "open usbserial /dev/"+f.getName()+" success");
			UsbSerial.setBaudrate(mFd, 115200);
			UsbSerial.set_options(mFd, 8, 1, 'n');
			break;
		}
		return false;
	}

	public class SerialEventReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Debug.d(TAG, "******intent="+intent.getAction());
			if(ACTION_REOPEN_SERIAL.equals(intent.getAction()))
			{
				openSerial();
				/*
				if(openSerial())
				{
					stopThread(mPrintThread);
					mPrintThread.start();
				}*/
			}
			else if(ACTION_CLOSE_SERIAL.equals(intent.getAction()))
			{
				stopThread(mPrintThread);
				mFd = 0;
			}
			else if(ACTION_BOOT_COMPLETE.equals(intent.getAction()))
			{
				/*read out last opened tlk & csv file*/
				Handler mpreHandler = new Handler();
				mpreHandler.postDelayed(new Runnable(){
				
					@Override
					public void run() {
					// TODO Auto-generated method stub
					/*
					String csv = getCsvFromPreference();
					Debug.d(TAG, "preference csv="+csv);
					if(csv!=null && new File(csv).exists())
					{
						mBtnfile.setText(new File(csv).getName());
						readCsv(csv);
						mMessageList.setAdapter(mMessageAdapter);
						fileChangedHandler.sendEmptyMessage(FILE_CSV_CHANGED);
					}
					String tlk = getTlkFromPreference();
					if(tlk !=null && new File(tlk).exists())
					{
						mBtnTlkfile.setText(new File(tlk).getName());
						fileChangedHandler.sendEmptyMessage(FILE_TLK_CHANGED);
					}
					*/
				}
				}, 3000);
				byte info[] = new byte[23];
				UsbSerial.printStart(mFd);
				UsbSerial.getInfo(mFd, info);
				updateInkLevel(info);
				UsbSerial.printStop(mFd);
			}
		}
		
	}
	
	private void updateInkLevel(byte info[])
	{
		if(info == null || info.length<13)
			return;
		int h = info[10]&0x0ff;
		int l = info[11]&0x0ff;
		int level = h<<8 | l;
		mInkLevel.setText(String.valueOf(level));
		if((info[8]&0x04)==0x00)
			mPhotocellState.setText("0");
		else
			mPhotocellState.setText("1");
		if((info[8]&0x08)==0x00)
			mEncoderState.setText("0");
		else
			mEncoderState.setText("1");
		if(info[9]==4 || info[9]==0)
		{
			mPrintState.setBackgroundColor(Color.GREEN);
			mPrintState.setText(getResources().getString(R.string.strPrintok));
		}
		else
		{
			mPrintState.setBackgroundColor(Color.RED);
			mPrintState.setText(getResources().getString(R.string.strPrinting));
		}
	}
	
	/*
	 * printing Thread
	 * 1) send print command(0001)
	 * 2) set all parameter(0006)
	 * 3) send parameter data(0007)
	 * 4) send data control command (0004 )
	 * 5) send data (0005 )
	 * 6) send poll command in 20ms interval, goto 4) if print ok
	 */
	public class PrintingThread extends Thread{
		
		//public boolean isRunning;
		public int curPos=0;
		public PrintingThread()
		{
			//isRunning=false;
		}
		/*
		public void start()
		{
			Debug.d(TAG, "====start");
			isRunning=true;
		}
		*/
		public void run()
		{
			//isPrinting = true;
			byte[] sdata = new byte[128];
			byte[] pdata;// = new byte[128];
			byte[] info = new byte[23];
			Debug.d(TAG, "====run");
			if(UsbSerial.printStart(mFd)==0)
			{
				isRunning = false;
				isPrinting=false;
				return;
			}
			Debug.d(TAG, "====start ok");
			if(UsbSerial.sendSetting(mFd)==0)
			{
				UsbSerial.printStop(mFd);
				isRunning = false;
				isPrinting=false;
				return;
			}
			Debug.d(TAG, "====send setting ok");
			makeParams(mContext, sdata);
			UsbSerial.sendSettingData(mFd, sdata);
			while(isRunning == true)
			{
				//pdata = new byte[128];	//
				if(!isPrinting)
				{
					int ret = UsbSerial.getInfo(mFd, info);
					if(ret != 0)
						Debug.d(TAG, "get Print-header info error");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					
					while(true) //poll state,wait for print finish
					{
						UsbSerial.getInfo(mFd, info);
						String text="";
						for(int i=0;i<23;i++)
							text += String.valueOf(Integer.toHexString(info[i] & 0x0ff))+" ";
						Message msg = new Message();
						Bundle b = new Bundle();
						b.putString("text", text);
						msg.what=MESSAGE_UPDATE_PRINTSTATE;
						msg.setData(b);
						mHandler.sendMessage(msg);
						if(info[9] == 0)	//print finished
							break;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(mTlkList==null || mTlkList.isEmpty())
					{
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					
					if(curPos>=mTlkList.size())
						curPos = 0;
					Vector<TlkObject> list = mTlkList.get(curPos);
					byte[] buffer = mBinBuffer.get(list);
					UsbSerial.sendDataCtrl(mFd, buffer.length);
					UsbSerial.printData(mFd,  buffer);
					curPos++;
				}
			}
			
				
			Log.d(TAG, "PrintingThread exit, mfd="+mFd);
			//isPrinting = false;
		}
	}
	
	public synchronized void stopThread(Thread t)
	{
		isPrinting=false;
		isRunning=false;
		if(t != null)
		{
			Thread tmp = t;
			t = null;
			tmp.interrupt();
		}
	}
	
	public synchronized Thread startThread()
	{
		PrintingThread t = new PrintingThread();
		isRunning=true;
		t.start();
		return t;
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
		mLoadingDialog = ProgressDialog.show(ControlTabActivity.this, "", getResources().getString(R.string.strLoading), true,false);
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
}
