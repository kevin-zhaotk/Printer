package com.industry.printer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.industry.printer.ControlTabActivity.SerialEventReceiver;
import com.industry.printer.FileBrowserDialog.OnPositiveListener;
import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.FileFormat.FilenameSuffixFilter;
import com.industry.printer.FileFormat.Tlk_Parser;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.TlkObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ManualCtrlActivity extends Activity {

	public final String TAG="ManualCtrlActivity";
	
	public Button mBtnTlkfile;
	public Button mBtnview;
	public Button mPrint;
	public Button mStop;
	public TextView mPrintState;
	public TextView mPhotocell;
	public TextView mEncoder;
	public TextView mLevel;
	
	public LinkedList<Map<String, String>>	mMessageMap;
	public PreviewAdapter mMessageAdapter;
	public ListView mMessageList;
	public byte[] mBinBuffer;
	public Context mContext;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLocale();
		setContentView(R.layout.manualctrl_frame);
		
		mContext = this.getApplicationContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ControlTabActivity.ACTION_BOOT_COMPLETE);
		BroadcastReceiver mReceiver = new SerialEventReceiver(); 
		registerReceiver(mReceiver, filter);
		
		mPrint = (Button) findViewById(R.id.manual_StartPrint);
		mPrint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPrintState.setBackgroundColor(Color.RED);
				mPrintState.setText(getResources().getString(R.string.strPrinting));
				print();
			}
			
		});
		
		mStop = (Button) findViewById(R.id.manual_StopPrint);
		mStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UsbSerial.printStop(ControlTabActivity.mFd);
			}
			
		});
		
		mMessageMap = new LinkedList<Map<String, String>>();
		mMessageAdapter = new PreviewAdapter(mContext, 
											mMessageMap,
											R.layout.manuallistviewlayout,
											new String[]{"index","text1","text2","text3","text4","text5","text6"},
											new int[]{R.id.manual_index,R.id.manual_text1,R.id.manual_text2,R.id.manual_text3,R.id.manual_text4,
													R.id.manual_text5,R.id.manual_text6});
		mMessageAdapter.setMode(true);
		mMessageList = (ListView) findViewById(R.id.manual_lv_messages);
		mMessageList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		

		mBtnTlkfile = (Button) findViewById(R.id.manual_btnTlkfile);
		mBtnTlkfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog dialog = new FileBrowserDialog(ManualCtrlActivity.this, DotMatrixFont.TLK_FILE_PATH, FilenameSuffixFilter.TLK_SUFFIX);
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
					}
					
				});
				dialog.show();
			}
			
		});
		
		mBtnview = (Button)findViewById(R.id.manual_btn_preview);
		mBtnview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DotMatrixFont dot = new DotMatrixFont("/mnt/usb/font.txt");
				Debug.d(TAG, "====preview button pressed");
				int pos = 0;//mMessageList.getCheckedItemPosition();
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
					int len = calculateBufsize(list);
					Debug.d(TAG, "bmp width="+len);
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
						if(o.isTextObject() && o.mContent!=null)
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
						if(bmp!=null)
							can.drawBitmap(bmp, o.x, o.y, p);
					}
					BinCreater.saveBitmap(gBmp, "pre.bmp");
					//set contents of text object
					//BinCreater.create(BitmapFactory.decodeFile("/mnt/usb/11.jpg"), "/mnt/usb/1.bin", 0);
					BinCreater.create(gBmp, 0);
					ByteArrayInputStream bs = new ByteArrayInputStream(BinCreater.mBmpBits);
					mBinBuffer = new byte[BinCreater.mBmpBits.length];
					try{
						bs.read(mBinBuffer);
					}catch(Exception e)
					{		
					}
					PreviewDialog prv = new PreviewDialog(ManualCtrlActivity.this);
					
					prv.show(list.get(1));
				}
				
			}
			
		});
		mPrintState = (TextView) findViewById(R.id.tvmanprintState);
		mPhotocell= (TextView) findViewById(R.id.sw_manphotocell_state);
		mEncoder = (TextView) findViewById(R.id.sw_manencoder_state);
		mLevel = (TextView) findViewById(R.id.tv_maninkValue);
		byte info[] = new byte[23];
		UsbSerial.printStart(ControlTabActivity.mFd);
		UsbSerial.getInfo(ControlTabActivity.mFd, info);
		updateInkLevel(info);
		UsbSerial.printStop(ControlTabActivity.mFd);
		initMsglist();
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
	
	public void initMsglist()
	{
		for(int i=1;i<=1;i++)
		{
			Map<String,String> map = new HashMap<String,String>();
			map.put("index", String.valueOf(i));
			map.put("text1", "");
			map.put("text2", "");
			map.put("text3", "");
			map.put("text4", "");
			map.put("text5", "");
			mMessageMap.add(map);
		}
		mMessageList.setAdapter(mMessageAdapter);
	}
	
	public void setContent(String index,Vector<TlkObject> list)
	{
		for(TlkObject o:list)
		{
			//for(int i=0;i<mMessageList.getCount(); i++)
			{
				LinearLayout lay = (LinearLayout)mMessageList.getChildAt(0);
				
				if(o.index<=0 || o.index > 5)
					break;
				if(o.index==1)
				{
					EditText t1=(EditText)lay.findViewById(R.id.manual_text1);
					Debug.d(TAG, "===content="+t1.getText().toString());
					o.setContent(t1.getText().toString());
				}
				else if(o.index==2)
				{
					EditText t1=(EditText)lay.findViewById(R.id.manual_text2);
					Debug.d(TAG, "===content="+t1.getText().toString());
					o.setContent(t1.getText().toString());
				}else if(o.index==3)
				{
					EditText t1=(EditText)lay.findViewById(R.id.manual_text3);
					Debug.d(TAG, "===content="+t1.getText().toString());
					o.setContent(t1.getText().toString());
				}else if(o.index==4)
				{
					EditText t1=(EditText)lay.findViewById(R.id.manual_text4);
					Debug.d(TAG, "===content="+t1.getText().toString());
					o.setContent(t1.getText().toString());
				}else if(o.index==5)
				{
					EditText t1=(EditText)lay.findViewById(R.id.manual_text5);
					Debug.d(TAG, "===content="+t1.getText().toString());
					o.setContent(t1.getText().toString());
				}
			}
		}
		
	}
	
	public int calculateBufsize(Vector<TlkObject> list)
	{
		int length=0;
		for(int i=0; i<list.size(); i++)
		{
			TlkObject o = list.get(i);
			if(o == null)
				break;
			else if(o.isTextObject() && o.mContent != null)	//each text object take over 16*16/8 * length=32Bytes*length
			{
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
				length = (font.getColumns()*o.mContent.length()+o.x) > length?(font.getColumns()*o.mContent.length()+o.x):length;
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				length = (o.x+128) > length?(o.x+128):length;
			}
		}
		return length;
	}

	public void setLocale()
	{
		Configuration config = getResources().getConfiguration(); 
		DisplayMetrics dm = getResources().getDisplayMetrics(); 
		config.locale = Locale.SIMPLIFIED_CHINESE; 
		getResources().updateConfiguration(config, dm); 
		
	}
	
	public ProgressDialog mPrintDialog;
	public void print()
	{
		byte[] buffer=null;
		String text="";
		UsbSerial.printStart(ControlTabActivity.mFd);
		UsbSerial.sendSetting(ControlTabActivity.mFd);
		byte[] data = new byte[128];
		ControlTabActivity.makeParams(mContext,data);
		UsbSerial.sendSettingData(ControlTabActivity.mFd, data);
		
		
		if(mBinBuffer==null)
			return;
		UsbSerial.sendDataCtrl(ControlTabActivity.mFd, mBinBuffer.length);
		UsbSerial.printData(ControlTabActivity.mFd,  mBinBuffer);
		//mPrintDialog = ProgressDialog.show(ManualCtrlActivity.this, "", getResources().getString(R.string.strwaitting));
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				int timeout=5;
				byte[] info = new byte[23];
				do
				{
					try{
						Thread.sleep(2000);
					}catch(Exception e)
					{}
					Debug.d(TAG, "##########timeout = "+timeout);
					//if(timeout-- <=1)
					//	break;
					UsbSerial.getInfo(ControlTabActivity.mFd, info);
					Message msg = new Message();
					msg.what=2;
					Bundle b= new Bundle();
					b.putByteArray("info", info);
					msg.setData(b);
					mHandler.sendMessage(msg);
					
				}while(info[9]!=0&&info[9]!=4);
				
				//mHandler.sendEmptyMessage(2);
			}
		}).start();
		
		//UsbSerial.sendDataCtrl(mFd, data.length);
		//UsbSerial.printData(mFd,  data);
	}
	
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) { 
			switch(msg.what)
			{
				case 2:
					updateInkLevel(msg.getData().getByteArray("info"));
					break;
			}
		}
	};
	
	public class SerialEventReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Debug.d(TAG, "******intent="+intent.getAction());
			
			if(ControlTabActivity.ACTION_BOOT_COMPLETE.equals(intent.getAction()))
			{
				/*read out last opened tlk & csv file*/
				
				byte info[] = new byte[23];
				UsbSerial.printStart(ControlTabActivity.mFd);
				UsbSerial.getInfo(ControlTabActivity.mFd, info);
				updateInkLevel(info);
				UsbSerial.printStop(ControlTabActivity.mFd);
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
		mLevel.setText(String.valueOf(level));
		if((info[8]&0x04)==0x00)
			mPhotocell.setText("0");
		else
			mPhotocell.setText("1");
		if((info[8]&0x08)==0x00)
			mEncoder.setText("0");
		else
			mEncoder.setText("1");
		if(info[9]==4 ||info[9]==0)
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
}
