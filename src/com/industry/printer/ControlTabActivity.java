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
import android.app.ProgressDialog;
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
	public PollStateThread mPollThread;
	
	public int mIndex;
	public TextView mPrintStatus;
	public TextView mInkLevel;
	public TextView mPhotocellState;
	public TextView mEncoderState;
	public TextView mPrintState;
	
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

		
		//mPreview = (PreviewScrollView ) findViewById(R.id.ctl_preview);
		
		mBtnStart = (Button) findViewById(R.id.StartPrint);
		mBtnStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				print();
			}
			
		});
		
		mBtnStop = (Button) findViewById(R.id.StopPrint);
		mBtnStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				isPrinting = false;
				UsbSerial.printStop(mFd);
				mPreviewRefreshHandler.removeMessages(0);
				stopThread(mPrintThread);
				*/
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
		
		/*
		mBtnOpen = (Button) findViewById(R.id.btnOpen);
		mBtnOpen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isroot=false;
				
				try {
					isroot = LinuxShell.isRoot(Runtime.getRuntime(), 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		*/
		/*
		 * Command 4&5
		 */
		/*
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnSend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte[] buffer=null;
				byte[] info = new byte[23];
				String text="";
				UsbSerial.getInfo(mFd, info);
				if(info[9] != 0)
				{
					Debug.d(TAG, "printer is printing now, please send buffer later!!!");
					return;
				}
				try{
					int index = mMessageAdapter.getChecked();
					Vector<TlkObject> list = mTlkList.get(index-1);
					Debug.d(TAG,"=======index="+(index-1));
					showListContent(list);
					buffer = mBinBuffer.get(list);
				}catch(Exception e)
				{
					return;
				}
				if(buffer==null)
					return;
				UsbSerial.sendDataCtrl(mFd, buffer.length);
				UsbSerial.printData(mFd,  buffer);
				//UsbSerial.sendDataCtrl(mFd, data.length);
				//UsbSerial.printData(mFd,  data);
				
			}
			
		});
		*/
		/*
		 * Command 6&7
		 * set all param
		 */
		/*
		mSetParam = (Button)findViewById(R.id.btnSetparam);
		mSetParam.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UsbSerial.sendSetting(mFd);
				byte[] data = new byte[128];
				makeParams(mContext, data);
				UsbSerial.sendSettingData(mFd, data);
				
			}
			
		});
		*/
		/*
		 * get Info
		 * Command 8
		 */
		/*
		mGetInfo = (Button) findViewById(R.id.btnGetinfo);
		mGetInfo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				byte[] info = new byte[23];
				String text="";
				UsbSerial.getInfo(mFd, info);
				for(int i=0;i<23;i++)
					text += String.valueOf(Integer.toHexString(info[i] & 0x0ff))+" ";
				mPrintStatus.setText("result: "+text);
			}
			
		});
		*/
		
		//mPollThread = new PollStateThread();
		//mPrintThread = (PrintingThread) startThread();
		//mPrintThread.start();
		/*
		 * Start Printing Thread
		 */
		/*
		mPrint = (Button) findViewById(R.id.btnPrint);
		mPrint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "====isPrinting="+isPrinting);
				if(isPrinting)	//printing thread is running, do not create a new print thread again
					return;
				isPrinting = true;
				mPrintThread = (PrintingThread) startThread();
				
			}
			
		});
		
		mFinish = (Button) findViewById(R.id.btnFinish);
		mFinish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isPrinting = false;
				UsbSerial.printStop(mFd);
				mPreviewRefreshHandler.removeMessages(0);
				stopThread(mPrintThread);
			}
			
		});
		*/
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
						setCsvToPreference(f);
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
						setTlkToPreference(f);
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
				if(pos<1 || pos>=mMessageList.getCount())
					return;
				Debug.d(TAG, "-------selected pos="+pos);
				Vector<TlkObject> list = mTlkList.get(pos-1);
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
		
		mBtnPrev = (Button) findViewById(R.id.btnPreRecord);
		mBtnPrev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int i = mMessageAdapter.getChecked();
				Debug.d(TAG,"curent checked = "+i);
				if(i<=1 || i>=mMessageList.getCount())
					return;
				mMessageAdapter.setChecked(i-1);
				//mMessageList.setAdapter(mMessageAdapter);
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});
		
		mBtnNext = (Button) findViewById(R.id.btnNextRecord);
		mBtnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int i = mMessageAdapter.getChecked();
				Debug.d(TAG,"curent checked = "+i);
				if(i<1 || i>=mMessageList.getCount()-1)
					return;
				
				//Log.d(TAG, "********list size="+mMessageList.getCount()+", move down "+i);
				
				mMessageAdapter.setChecked(i+1);
				//mMessageList.setAdapter(mMessageAdapter);
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});
		/*
		mForward = (Button) findViewById(R.id.btn_mvforward);
		mForward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				int i = mMessageAdapter.getChecked();
				if(i<=1)
					return;
				
				Map<String,String> item = mMessageMap.remove(i);
				Log.d(TAG, ""+item.get("index")+" , "+item.get("pic1"));
				mMessageMap.add(i-1, item);
				try{
					Vector<TlkObject> vec= mTlkList.remove(i);
					mTlkList.add(i-1, vec);
				}catch(Exception e)
				{
					
				}
				mMessageAdapter.setChecked(i-1);
				//mMessageList.setAdapter(mMessageAdapter);
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});
		
		mBackward = (Button) findViewById(R.id.btn_mvbackward);
		mBackward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int i = mMessageAdapter.getChecked();
				if(i<0 || i>=mMessageList.getCount()-1)
					return;
				
				Log.d(TAG, "********list size="+mMessageList.getCount()+", move down "+i);
				Map<String,String> item = mMessageMap.remove(i);
				mMessageMap.add(i+1, item);
				try{
					Vector<TlkObject> vec= mTlkList.remove(i);
					mTlkList.add(i, vec);
				}catch(Exception e)
				{}
				mMessageAdapter.setChecked(i+1);
				//mMessageList.setAdapter(mMessageAdapter);
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});
		*/
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
	
	public ProgressDialog mPrintDialog;
	public void print()
	{
		byte[] buffer=null;
		String text="";
		UsbSerial.printStart(mFd);
		UsbSerial.sendSetting(mFd);
		byte[] data = new byte[128];
		makeParams(mContext,data);
		UsbSerial.sendSettingData(mFd, data);
		
		try{
			int index = mMessageAdapter.getChecked();
			Vector<TlkObject> list = mTlkList.get(index-1);
			Debug.d(TAG,"=======index="+(index-1));
			showListContent(list);
			buffer = mBinBuffer.get(list);
		}catch(Exception e)
		{
			Debug.d(TAG,"######Exception: "+e.getMessage());
			return;
		}
		/*
		buffer = new byte[6400];
		for(int i=0;i<6400;i=i+8)
		{
			buffer[i]=(byte)0xff;
			buffer[i+2]=(byte)0xff;
			buffer[i+4]=(byte)0xff;
			buffer[i+6]=(byte)0xff;
		}
		*/
		if(buffer==null)
			return;
		
		UsbSerial.sendDataCtrl(mFd, buffer.length);
		UsbSerial.printData(mFd,  buffer);
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
					msg.what=2;
					Bundle b= new Bundle();
					b.putByteArray("info", info);
					msg.setData(b);
					mHandler.sendMessage(msg);
					Debug.d(TAG, "##########timeout = "+timeout+",stat="+info[9]);
				}while(info[9]!=4);
				
			}
		}).start();
		
		//UsbSerial.sendDataCtrl(mFd, data.length);
		//UsbSerial.printData(mFd,  data);
	}
	
	public String mObjPath=null;
	public ProgressDialog mLoadingDialog;
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
				case 1:
					String text = msg.getData().getString("text");
					mPrintStatus.setText("result: "+text);
					break;
				case 2:
					//mPrintDialog.dismiss();
					updateInkLevel(msg.getData().getByteArray("info"));
					break;
				case 3:
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
                	 Debug.d(TAG, "index "+o.index+" found"+", pic"+(o.index-20)+"="+m.get("pic"+(o.index-20)));
                	 o.setContent(m.get("pic"+(o.index-20)));
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
	 * if in no-print state, poll state of print-head in 100ms interval
	 */
	public class PollStateThread extends Thread{
		
		//public boolean isRunning;
		PollStateThread(){
			//isRunning = false;
		}
		
		public void run()
		{
			//isRunning = true;
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
			//isRunning = false;
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
						msg.what=1;
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
					
					/*
					byte data[]={0x00, 0x00, 0x50, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x00, 0x00, 
							0x00, 0x00, 0x40, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 
							0x00, 0x00, 0x50, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, (byte) 0x80, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x40, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xA0, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00, 0x50, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 0x20, 0x00, 0x00, 
							0x00, 0x00, 0x00, 0x04, 0x00, 0x10, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 
							0x00, 0x00, 0x40, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x00, 0x00
							};
					*/
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
				bit = new int[font.getColumns()*2*o.mContent.length()];
				Debug.d(TAG, "=========bit.length="+bit.length);
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
		BinCreater.create(gBmp, 0);
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
				//
				if(mBtnTlkfile.getText().toString()!=null && mBtnTlkfile.getText().toString().toLowerCase().endsWith(FilenameSuffixFilter.TLK_SUFFIX)
					&& mBtnfile.getText().toString()!=null && mBtnfile.getText().toString().toLowerCase().endsWith(FilenameSuffixFilter.CSV_SUFFIX))
				{
					//Vector<TlkObject> list = new Vector<TlkObject>();
					//if(mMsgFile!=null)
					/*
					mLoadingDialog = ProgressDialog.show(ControlTabActivity.this, "", "Loading,please wait......"); 
					new Thread(new Runnable(){
						@Override
						public void run() {
					*/{
					
							// TODO Auto-generated method stub
							
						mTlkList.clear();
						
						for(int i=1;i<mMessageList.getCount();i++)
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
					//make bin buffer
					{
						mBinBuffer.clear();
						for(Vector<TlkObject> list:mTlkList)
						{
							Debug.d(TAG,"%%%%%%%%%%%%%%%%%%%%%%%%makeBinbuffer");
							makeBinBuffer(list);
							ByteArrayInputStream stream = new ByteArrayInputStream(BinCreater.mBmpBits);
							byte buffer[] = new byte[BinCreater.mBmpBits.length];
							/*read columns from tlk file*/
							
							try{
								int b=stream.read(buffer);
								Debug.d(TAG,"readout: "+b);
							}
							catch(Exception e)
							{
								
							}
							mBinBuffer.put(list, buffer);
						}
						Debug.d(TAG,"%%%%%%%%%%%%%%%%%%%%%%%%makeBinbuffer finish");
					}/*
					mHandler.sendEmptyMessage(3);
					}
					
					}).start();*/
				}
				break;
			default:
				Debug.d(TAG, "unsupported message "+msg.what);
			}
		}
	}; 
	
	public static final String LAST_TLK_FILE="LAST_TLK_FILE";
	public static final String LAST_CSV_FILE="LAST_CSV_FILE";
	
	private void setTlkToPreference(String f)
	{
		SharedPreferences preference =getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		preference.edit().putString(LAST_TLK_FILE, f).commit();
	}
	
	private String getTlkFromPreference()
	{
		SharedPreferences preference =getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		String f = preference.getString(LAST_TLK_FILE, null);
		return f;
	}
	
	private void setCsvToPreference(String f)
	{
		SharedPreferences preference =getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		preference.edit().putString(LAST_CSV_FILE, f).commit();
	}
	
	private String getCsvFromPreference()
	{
		SharedPreferences preference =getSharedPreferences(SettingsTabActivity.PREFERENCE_NAME, 0);
		String f = preference.getString(LAST_CSV_FILE, null);
		return f;
	}
	
	private void showListContent(Vector<TlkObject> list)
	{
		Debug.d(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$");
		for(TlkObject o:list)
		{
			Debug.d(TAG,"******x="+o.x+", y="+o.y+", content="+o.mContent);
		}
		Debug.d(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$");
	}
}
