package com.industry.printer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ControlTabActivity extends Activity {
	public static final String TAG="ControlTabActivity";
	
	public static final String ACTION_REOPEN_SERIAL="com.industry.printer.ACTION_REOPEN_SERIAL";
	public static final String ACTION_CLOSE_SERIAL="com.industry.printer.ACTION_CLOSE_SERIAL";
	
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
	
	public Button	mBtnfile;
	public Button	mBtnTlkfile;
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
	public int mFd;
	
	public BinInfo mBg;
	
	public static FileInputStream mFileInputStream;
	Vector<Vector<TlkObject>> mTlkList;
	/*
	 * whether the print-head is doing print work
	 * if no, poll state Thread will read print-header state
	 * 
	 */
	public boolean isPrinting;
	
	public PrintingThread mPrintThread;
	public PollStateThread mPollThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_frame);
		
		isPrinting = false;
		mTlkList = new Vector<Vector<TlkObject>>();
		mObjList = new Vector<BaseObject>();
		mContext = this.getApplicationContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REOPEN_SERIAL);
		filter.addAction(ACTION_CLOSE_SERIAL);
		BroadcastReceiver mReceiver = new SerialEventReceiver(); 
		registerReceiver(mReceiver, filter);
		
		String dev = "/dev/ttyACM0";
		//String dev = "/dev/ttySAC0";
		//mFd = UsbSerial.open(dev);
		openSerial();
		//Debug.d(TAG,"open "+dev+"="+mFd);
		//if(mFd == -1)
		//	return ;
		UsbSerial.setBaudrate(mFd, 115200*8);
		UsbSerial.set_options(mFd, 8, 1, 'n');
		//mFileInputStream = new FileInputStream(mFd);

		
		//mPreview = (PreviewScrollView ) findViewById(R.id.ctl_preview);
		
		mBtnStart = (Button) findViewById(R.id.StartPrint);
		mBtnStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//UsbConnector uc = new UsbConnector(ControlTabActivity.this);
				
				//mPreviewRefreshHandler.sendEmptyMessage(0);
				//UsbSerial.printStart(mFd);
				//
				//UsbSerial.setAllParam(mFd, null);
				//UsbSerial.readAllParam(mFd);
			}
			
		});
		
		mBtnStop = (Button) findViewById(R.id.StopPrint);
		mBtnStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UsbSerial.printStop(mFd);
				mPreviewRefreshHandler.removeMessages(0);
			}
			
		});
		
		/*
		 *clean the print head
		 *this command unsupported now 
		 */
		mBtnClean = (Button) findViewById(R.id.btnClean);
		mBtnClean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//UsbSerial.clean(mFd);
			}
			
		});
		
		mBtnOpen = (Button) findViewById(R.id.btnOpen);
		mBtnOpen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isroot=false;
				/*
				try {
					isroot = LinuxShell.isRoot(Runtime.getRuntime(), 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				Debug.d(TAG, "is root="+isroot);
				FileBrowserDialog fdialog = new FileBrowserDialog(ControlTabActivity.this,DotMatrixFont.USB_SYS_PATH);
				fdialog.setOnPositiveClickedListener(new OnPositiveListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(0);
					}
					
				});
				fdialog.show();
			}
			
		});
		
		/*
		 * Command 4&5
		 */
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnSend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte data[]=new byte[2*1024*1024];
				UsbSerial.sendDataCtrl(mFd, data.length);				
				UsbSerial.printData(mFd, data);
			}
			
		});
		
		mBtnSend.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				byte data[]={0x01, 0x02, 0x03, 0x04,0x05, 0x06, 0x07, 0x08, 0x09};
				
				UsbSerial.sendDataCtrl(mFd, data.length);				
				UsbSerial.printData(mFd, data);
				return false;
			}
			
		});
		/*
		 * Command 6&7
		 * set all param
		 */
		mSetParam = (Button)findViewById(R.id.btnSetparam);
		mSetParam.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UsbSerial.sendSetting(mFd);
				byte[] data = new byte[128];
				makeParams(data);
				UsbSerial.sendSettingData(mFd, data);
			}
			
		});
		
		/*
		 * get Info
		 * Command 8
		 */
		mGetInfo = (Button) findViewById(R.id.btnGetinfo);
		mGetInfo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte[] info = new byte[16];
				UsbSerial.getInfo(mFd, info);
			}
			
		});
		
		
		//mPollThread = new PollStateThread();
		mPrintThread = (PrintingThread) startThread();
		//mPrintThread.start();
		/*
		 * Start Printing Thread
		 */
		mPrint = (Button) findViewById(R.id.btnPrint);
		mPrint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isPrinting)	//printing thread is running, do not create a new print thread again
					return;
				isPrinting = true;
				
			}
			
		});
		
		mFinish = (Button) findViewById(R.id.btnFinish);
		mFinish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isPrinting)
					return;
				isPrinting = false;
				
			}
			
		});
		
		mBtnfile = (Button) findViewById(R.id.btnopenfile);
		mBtnfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog dialog = new FileBrowserDialog(ControlTabActivity.this, DotMatrixFont.USB_PATH, FilenameSuffixFilter.CSV_SUFFIX);
				dialog.setOnPositiveClickedListener(new OnPositiveListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String f = FileBrowserDialog.file();
						if(f==null || !f.toLowerCase().endsWith(".csv"))
						{
							Toast.makeText(mContext, "please select a csv file", Toast.LENGTH_LONG);
							return;
						}
							
						mBtnfile.setText(new File(f).getName());
						readCsv(f);
						mMessageList.setAdapter(mMessageAdapter);
						fileChangedHandler.sendEmptyMessage(FILE_CSV_CHANGED);
					}
					
				});
				dialog.show();
			}
			
		});
		//mMsgFile = (TextView) findViewById(R.id.tvfile);
		
		
		mBtnTlkfile = (Button) findViewById(R.id.btnTlkfile);
		mBtnTlkfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog dialog = new FileBrowserDialog(ControlTabActivity.this, DotMatrixFont.TLK_FILE_PATH, FilenameSuffixFilter.TLK_SUFFIX);
				dialog.setOnPositiveClickedListener(new OnPositiveListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String f = FileBrowserDialog.file();
						if(f==null || !f.toLowerCase().endsWith(".tlk"))
						{
							Toast.makeText(mContext, "please select a csv file", Toast.LENGTH_LONG);
							return;
						}
							
						mBtnTlkfile.setText(new File(f).getName());
						fileChangedHandler.sendEmptyMessage(FILE_TLK_CHANGED);
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
				int pos = mMessageList.getCheckedItemPosition();
				if(pos<0 || pos>=mMessageList.getCount())
					return;
				Debug.d(TAG, "-------selected pos="+pos);
				Vector<TlkObject> list = mTlkList.get(pos);
				PreviewDialog prv = new PreviewDialog(ControlTabActivity.this);
				prv.show(list);
				/*
				Map<String, String> m = (Map<String, String>)mMessageList.getItemAtPosition(pos);
				if(m!= null)
				{
					String index = m.get("index");
					Vector<TlkObject> list = new Vector<TlkObject>();
					//if(mMsgFile!=null)
					{
						//String path = new File(mMsgFile.getText().toString()).getParent();
						if(!Tlk_Parser.parse(DotMatrixFont.TLK_FILE_PATH+mBtnTlkfile.getText().toString(), list))
						{
							Toast.makeText(mContext, getResources().getString(R.string.str_notlkfile), Toast.LENGTH_LONG);
							return;
						}
						setContent(index, list);
						Debug.d(TAG, "list size="+list.size());
					}
					makeBinBuffer(list);
					PreviewDialog prv = new PreviewDialog(ControlTabActivity.this);
					
					prv.show(list);
					
				}
				*/
			}
			
		});
		
		mForward = (Button) findViewById(R.id.btn_mvforward);
		mForward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				int i = mMessageList.getCheckedItemPosition();
				if(i>1)
				{
					Map<String,String> item = mMessageMap.remove(i);
					Log.d(TAG, ""+item.get("index")+" , "+item.get("pic1"));
					mMessageMap.add(i-1, item);
					Vector<TlkObject> vec= mTlkList.remove(i);
					mTlkList.add(i-1, vec);
				}
				mMessageList.setAdapter(mMessageAdapter);
				
			}
			
		});
		
		mBackward = (Button) findViewById(R.id.btn_mvbackward);
		mBackward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int i = mMessageList.getCheckedItemPosition();
				if(i<0 || i>=mMessageList.getCount()-1)
					return;
				
				Log.d(TAG, "********list size="+mMessageList.getCount()+", move down "+i);
				Map<String,String> item = mMessageMap.remove(i);
				mMessageMap.add(i+1, item);
				Vector<TlkObject> vec= mTlkList.remove(i);
				mTlkList.add(i, vec);
				mMessageList.setAdapter(mMessageAdapter);
				
			}
			
		});
		mMessageMap = new LinkedList<Map<String, String>>();
		mMessageAdapter = new PreviewAdapter(mContext, 
											mMessageMap,
											R.layout.pmessagelistviewlayout,
											new String[]{"index","pic1", "pic2", "pic3","pic4",
													"text1","text2","text3","text4","text5","text6",
													"text7","text8","text9","text10","text11","text12",
													"text13","text14","text15","text16"},
											new int[]{R.id.tv_index, R.id.tv_pic1,R.id.tv_pic2,R.id.tv_pic3,R.id.tv_pic4, 
													R.id.tv_text1,R.id.tv_text2,R.id.tv_text3,R.id.tv_text4,
													R.id.tv_text5,R.id.tv_text6,R.id.tv_text7,R.id.tv_text8,
													R.id.tv_text9,R.id.tv_text10,R.id.tv_text11,R.id.tv_text12,
													R.id.tv_text13,R.id.tv_text14,R.id.tv_text15,R.id.tv_text16});
		
		mMessageList = (ListView) findViewById(R.id.lv_messages);
		mMessageList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mMessageList.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "******message list clicked: "+position);
				if(position==0)
				{
					mMessageList.setItemChecked(position, false);
					mMessageAdapter.setChecked(-1);
				}
				else
				{
					mMessageAdapter.setChecked(position);
				}
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});
		initMsglist();
		mMessageList.setAdapter(mMessageAdapter);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		UsbSerial.close(mFd);
	}
		
	public String mObjPath=null;
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) { 
			switch(msg.what)
			{
				case 0:		//
					
					String f = FileBrowserDialog.file();
					if(f !=null && !new File(f).isDirectory())
					{
						Debug.d(TAG, "open object file "+f);
						mObjPath = new File(f).getParent();
						startPreview(f);
					}
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
				if(m.get("index").equals(index))
				{
					Debug.d(TAG, "index "+o.index+" found");
					o.setContent(m.get("text"+o.index));
					break;
				}
			}
		}
		
	}
	
	public Handler mPreviewRefreshHandler = new Handler(){
		public void handleMessage(Message msg) { 
			switch(msg.what)
			{
				case 0:		//
					if(mBg == null)
						return;
					Debug.d(TAG, "mPreviewRefreshHandler  invalidate");
					refreshVariables();
					//BinCreater.saveBitmap(bm, "123.png");
					BinCreater.bin2byte(mPreBitmap, mPreBytes);
					mPreview.createBitmap(mPreBytes, mBg.mColumn, mBg.mBitsperColumn);
					mPreview.invalidate();
					
					break;
			}
			mPreviewRefreshHandler.sendEmptyMessageDelayed(0, 2000);
		}
	};
	
	public byte[] mPreBitmap;
	public int[]	mPreBytes;
	public void startPreview(String f)
	{
		String path=null;
		File fp = new File(f);
		if(fp.isFile())
			path = new File(f).getParent();
		else
			path = f;
		Fileparser.parse(mContext, f, mObjList);
		try{
			/*
			 * parse 1.bin firstly
			 */
			/*
			 File file = new File(path, "1.bin");
			 FileInputStream fs = new FileInputStream(file);
			 mPreBitmap=new byte[fs.available()];
			 fs.read(mPreBitmap);
			 //Bitmap bmp = BinCreater.Bin2Bitmap(mPreBitmap);
			 //mPreview.createBitmap(bmp.getWidth(), bmp.getHeight());
			 //mPreview.drawBitmap(0, 0, bmp);
			  */
			mBg = new BinInfo();
			mBg.getBgBuffer(path+"/1.bin");
			 refreshVariables();
			 mPreBytes = new int[mBg.mBits.length*8];
			 Debug.d(TAG, "startPreview  mBg.mBits.len = "+mBg.mBits.length+", mPreBytes.len= "+mPreBytes.length);
			 //mPreBitmap = new byte[mBg.mBits.length];
			 BinCreater.bin2byte(mPreBitmap, mPreBytes);
			 mPreview.createBitmap(mPreBytes, mBg.mColumn, mBg.mBitsperColumn);
			 mPreview.invalidate();
			 //mPreviewRefreshHandler.sendEmptyMessage(0);
			}catch(Exception e)
			{
				Debug.d(TAG, "startPreview e: "+e.getMessage());
			}
	}
	
	public void refreshVariables()
	{
		Bitmap bm=null;
		String substr=null;
		ByteArrayBuffer bytes=new ByteArrayBuffer(0);
		if(mObjList==null || mObjList.isEmpty())
			return;
		mPreBitmap = Arrays.copyOf(mBg.mBits, mBg.mBits.length);
		for(BaseObject o:mObjList)
		{
			bytes.clear();
			bytes.setLength(0);
			if(o instanceof CounterObject)
			{
				//int value = ((CounterObject) o).getValue();
				String str = ((CounterObject) o).getNext();
				/*
				for(int i=0; i<str.length(); i++)
				{
					byte[] b = BinCreater.getBinBuffer(Integer.parseInt(str.substring(i, i+1)), mObjPath+"/" + o.getIndex() +".bin");
					//Debug.d(TAG, "mPreviewRefreshHandler b.len="+b.length);
					bytes.append(b, 0, b.length);
				}
				//bm = BinCreater.bin2byte(bytes.buffer());
				//BinCreater.saveBitmap(bm, o.getIndex()+".png");
				BinCreater.overlap(mPreBitmap, bytes.buffer(), (int)o.getX(), 0, 110);
				Debug.d(TAG, "obj Y="+o.getY()+", bm.hight="+bm.getHeight());
				//mPreview.drawBitmap((int)o.getX(), 0, bm);
				 */
				BinInfo varbin = new BinInfo();
				try {
					varbin.getVarBuffer(str, mObjPath+"/" + o.getIndex() +".bin");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BinCreater.overlap(mPreBitmap, varbin.mBits, (int)o.getX(), 0, 880);
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
					/*
					Debug.d(TAG, "x="+rtSub.getX()+", y="+rtSub.getY()+", width="+rtSub.getWidth()+", xEnd="+rtSub.getXEnd());
					Debug.d(TAG, "bin file :"+mObjPath+"/" + rtSub.getIndex() +".bin"+", substr="+substr);
					for(int i=0; i<substr.length(); i++)
					{						
						byte[] b = BinCreater.getBinBuffer(Integer.parseInt(substr.substring(i, i+1)), mObjPath+"/" + rtSub.getIndex() +".bin");
						Debug.d(TAG, "mPreviewRefreshHandler b.len="+b.length);
						bytes.append(b, 0, b.length);
					}
					
					//bm = BinCreater.bin2byte(bytes.toByteArray());
					//Debug.d(TAG, "bm width="+bm.getWidth()+", bytes len="+bytes.length());
					//mPreview.drawBitmap((int)rtSub.getX(), (int)rtSub.getY(), bm);
					BinCreater.overlap(mPreBitmap, bytes.buffer(), (int)rtSub.getX(), 0, 110);
					*/
					BinInfo varbin = new BinInfo();
					try {
						varbin.getVarBuffer(substr, mObjPath+"/" + rtSub.getIndex() +".bin");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BinCreater.overlap(mPreBitmap, varbin.mBits, (int)rtSub.getX(), 0, 880);
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
			if(mFd > 0)
			{
				Debug.d(TAG, "open usbserial /dev/"+f.getName()+" success");
				return true;
			}
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
				if(openSerial())
				{
					stopThread(mPrintThread);
					mPrintThread.start();
				}
			}
			else if(ACTION_CLOSE_SERIAL.equals(intent.getAction()))
			{
				stopThread(mPrintThread);
				mFd = 0;
			}
		}
		
	}
	
	
	/*
	 * if in no-print state, poll state of print-head in 100ms interval
	 */
	public class PollStateThread extends Thread{
		
		public boolean isRunning;
		PollStateThread(){
			isRunning = false;
		}
		
		public void run()
		{
			isRunning = true;
			byte[] info = new byte[16];
			if(mFd <= 0)
				return ;
			UsbSerial.printStart(mFd);
			while(!isPrinting)
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
			UsbSerial.printStop(mFd);
			isRunning = false;
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
		
		public boolean isRunning;
		
		public PrintingThread()
		{
			isRunning=false;
		}
		
		public void start()
		{
			isRunning=true;
		}
		
		public void run()
		{
			//isPrinting = true;
			byte[] sdata = new byte[128];
			byte[] pdata;// = new byte[128];
			byte[] info = new byte[16];
			
			if(UsbSerial.printStart(mFd)==0)
			{
				isRunning = false;
				return;
			}
			if(UsbSerial.sendSetting(mFd)==0)
			{
				UsbSerial.printStop(mFd);
				isRunning = false;
				return;
			}	
			makeParams(sdata);
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
						if(info[9] == 0)	//print finished
							break;
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					UsbSerial.sendDataCtrl(mFd, BinCreater.mBmpBits.length-16);
					pdata = new byte[BinCreater.mBmpBits.length-16];
					new ByteArrayInputStream(BinCreater.mBmpBits).read(pdata, 16, BinCreater.mBmpBits.length-16);
					UsbSerial.printData(mFd,  pdata);
				}
			}
			
				
			Log.d(TAG, "PrintingThread exit, mfd="+mFd);
			//isPrinting = false;
		}
	}
	
	public synchronized void stopThread(Thread t)
	{
		if(t != null)
		{
			((PrintingThread) t).isRunning=false;
			Thread tmp = t;
			t = null;
			tmp.interrupt();
		}
	}
	
	public synchronized Thread startThread()
	{
		PrintingThread t = new PrintingThread();
		t.start();
		return t;
	}
	
	public void initMsglist()
	{
		Map<String, String> m = new HashMap<String,String>();
		m.put("index", getResources().getString(R.string.str_column_index));
		m.put("pic1", getResources().getString(R.string.str_column_pic1));
		m.put("pic2", getResources().getString(R.string.str_column_pic2));
		m.put("pic3", getResources().getString(R.string.str_column_pic3));
		m.put("pic4", getResources().getString(R.string.str_column_pic4));
		m.put("text1", getResources().getString(R.string.str_column_text1));
		m.put("text2", getResources().getString(R.string.str_column_text2));
		m.put("text3", getResources().getString(R.string.str_column_text3));
		m.put("text4", getResources().getString(R.string.str_column_text4));
		m.put("text5", getResources().getString(R.string.str_column_text5));
		m.put("text6", getResources().getString(R.string.str_column_text6));
		m.put("text7", getResources().getString(R.string.str_column_text7));
		m.put("text8", getResources().getString(R.string.str_column_text8));
		m.put("text9", getResources().getString(R.string.str_column_text9));
		m.put("text10",getResources().getString(R.string.str_column_text10));
		m.put("text11", getResources().getString(R.string.str_column_text11));
		m.put("text12", getResources().getString(R.string.str_column_text12));
		m.put("text13", getResources().getString(R.string.str_column_text13));
		m.put("text14", getResources().getString(R.string.str_column_text14));
		m.put("text15", getResources().getString(R.string.str_column_text15));
		m.put("text16", getResources().getString(R.string.str_column_text16));
		
		
		mMessageMap.add(m);
	}
	
	
	public void readCsv(String csvfile)//, Vector<TlkObject> list)
	{
		CsvReader reader;
		mMessageMap.clear();
		Map<String, String> m;
		initMsglist();
		try {
			reader = new CsvReader(csvfile);
			reader.readRecord();	/*read the first row*/
			/*TO-DO parse head information*/
			reader.readRecord();	/*read the second row*/
			while(reader.readRecord())
			{
				m = new HashMap<String, String>();
				for(int i=0; i< reader.getColumnCount(); i++)
				{
					if(i == 0)	//index
					{
						m.put("index", reader.get(i));
						Debug.d(TAG, "index="+reader.get(i));
					}
					else if(i> 0 && i< 5)	//pic
					{
						m.put("pic"+i, reader.get(i));
						//list.
						Debug.d(TAG, "pic"+i+" = "+reader.get(i));
					}
					else if(i>=5 &&i<21)	//text
					{
						m.put("text"+(i-4), reader.get(i));
						Debug.d(TAG, "text"+(i-4)+" = "+reader.get(i));
					}
					else
						break;
				}
				
				mMessageMap.add(m);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
		
		
	}
	
	
	
	public int calculateBufsize(Vector<TlkObject> list)
	{
		int length=0;
		for(int i=0; i<list.size(); i++)
		{
			TlkObject o = list.get(i);
			if(o.isTextObject())	//each text object take over 16*16/8 * length=32Bytes*length
			{
				length = (16*o.mContent.length()+o.x) > length?(16*o.mContent.length()+o.x):length;
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				length = (o.x+128) > length?(o.x+128):length;
			}
		}
		return length;
	}
	
	public void makeBinBuffer(Vector<TlkObject>list)
	{
		int len = calculateBufsize(list);
		Debug.d(TAG, "bin length="+len);
		//int[] buffer = new int[len+16];
		int bit[];
		Bitmap bmp=null;
		Bitmap gBmp = Bitmap.createBitmap(len, 64, Config.ARGB_8888);
		
		Canvas can = new Canvas(gBmp);
		can.drawColor(Color.WHITE);
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 0);
		
		for(TlkObject o: list)
		{
			if(o.isTextObject())
			{
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
				bit = new int[32*o.mContent.length()];
				font.getDotbuf(o.mContent, bit);
				bmp=PreviewScrollView.getTextBitmapFrombuffer(bit, p);
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				Debug.d(TAG, "=========pic object");
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+o.font+".txt");
				bit = new int[128*8];
				font.getDotbuf(bit);
				bmp=PreviewScrollView.getPicBitmapFrombuffer(bit, p);
			}
			can.drawBitmap(bmp, o.x, o.y, p);
		}
		BinCreater.saveBitmap(gBmp, "pre.bmp");
		//set contents of text object
		//BinCreater.create(BitmapFactory.decodeFile("/mnt/usb/11.jpg"), "/mnt/usb/1.bin", 0);
		BinCreater.create(gBmp, "/mnt/usb/1.bin", 0);
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
	public void makeParams(byte[] params)
	{
		if(params==null || params.length<128)
		{
			Debug.d(TAG,"params is null or less than 128, realloc it");
			params = new byte[128];
		}
		SharedPreferences preference = getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		int speed = preference.getInt(SettingsTabActivity.PREF_PRINTSPEED, 0);
		params[4] = (byte) ((speed>>16)&0xff);
		params[5] = (byte) ((speed)&0xff);
		int delay = preference.getInt(SettingsTabActivity.PREF_DELAY, 0);
		params[6] = (byte) ((delay>>16)&0xff);
		params[7] = (byte) ((delay)&0xff);
		boolean triger = preference.getBoolean(SettingsTabActivity.PREF_TRIGER, true);
		params[10] = 0x00;
		params[11] = (byte) (triger==true?0x00:0x01);
		boolean encoder = preference.getBoolean(SettingsTabActivity.PREF_ENCODER, true);
		params[12] = 0x00;
		params[13] = (byte) (encoder==true?0x00:0x01);
		int bold = preference.getInt(SettingsTabActivity.PREF_BOLD, 0);
		params[14] = (byte) ((bold>>16)&0xff);
		params[15] = (byte) ((bold)&0xff);
		int fixlen = preference.getInt(SettingsTabActivity.PREF_FIX_LEN, 0);
		params[16] = (byte) ((fixlen>>16)&0xff);
		params[17] = (byte) ((fixlen)&0xff);
		int fixtime= preference.getInt(SettingsTabActivity.PREF_FIX_TIME, 0);
		params[18] = (byte) ((fixtime>>16)&0xff);
		params[19] = (byte) ((fixtime)&0xff);
		int headtemp = preference.getInt(SettingsTabActivity.PREF_HEAD_TEMP, 0);
		params[20] = (byte) ((headtemp>>16)&0xff);
		params[21] = (byte) ((headtemp)&0xff);
		int resvtemp = preference.getInt(SettingsTabActivity.PREF_RESV_TEMP, 0);
		params[22] = (byte) ((resvtemp>>16)&0xff);
		params[23] = (byte) ((resvtemp)&0xff);
		int fontwidth = preference.getInt(SettingsTabActivity.PREF_FONT_WIDTH, 0);
		int dots = preference.getInt(SettingsTabActivity.PREF_DOT_NUMBER, 0);
		
		
	}
	
	public static final int FILE_CSV_CHANGED=1;
	public static final int FILE_TLK_CHANGED=2;
	public Handler fileChangedHandler = new Handler(){
		@Override
		public void  handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case FILE_CSV_CHANGED:
			case FILE_TLK_CHANGED:
				if(mBtnTlkfile.getText().toString()!=null && mBtnTlkfile.getText().toString().toLowerCase().endsWith(FilenameSuffixFilter.TLK_SUFFIX)
					&& mBtnfile.getText().toString()!=null && mBtnfile.getText().toString().toLowerCase().endsWith(FilenameSuffixFilter.CSV_SUFFIX))
				{
					Vector<TlkObject> list = new Vector<TlkObject>();
					//if(mMsgFile!=null)
					{
						mTlkList.clear();
						
						for(int i=0;i<mMessageList.getCount();i++)
						{
							Vector<TlkObject> tmpList = new Vector<TlkObject>();
							//String path = new File(mMsgFile.getText().toString()).getParent();
							if(!Tlk_Parser.parse(DotMatrixFont.TLK_FILE_PATH+mBtnTlkfile.getText().toString(), tmpList))
							{
								Toast.makeText(mContext, getResources().getString(R.string.str_notlkfile), Toast.LENGTH_LONG);
								return;
							}
						
							
							String index = ((Map<String, String>)mMessageList.getItemAtPosition(i)).get("index");
							Debug.d(TAG, "=========index="+index+", i="+i);
							setContent(index, tmpList);
							mTlkList.add(tmpList);
						}
						
						Debug.d(TAG, "list size="+mTlkList.size());
					}
				}
				break;
			default:
				Debug.d(TAG, "unsupported message "+msg.what);
			}
		}
	}; 
	
	
}
