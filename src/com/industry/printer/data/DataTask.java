package com.industry.printer.data;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.lang.System;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;

import com.industry.printer.BinInfo;
import com.industry.printer.MessageTask;
import com.industry.printer.MessageTask.MessageType;
import com.industry.printer.FileFormat.QRReader;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LetterHourObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.RealtimeDate;
import com.industry.printer.object.RealtimeHour;
import com.industry.printer.object.RealtimeMinute;
import com.industry.printer.object.RealtimeMonth;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RealtimeYear;
import com.industry.printer.object.TextObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TLKFileParser;
import com.industry.printer.object.WeekDayObject;
import com.industry.printer.object.WeekOfYearObject;
import com.industry.printer.object.data.SegmentBuffer;

import com.industry.printer.Utils.PlatformInfo; //addbylk 
/**
 * 用于生成打印数据和预览图
 * @author zhaotongkai
 *
 */
public class DataTask {
	
	public static final String TAG = DataTask.class.getSimpleName();
	
	public Context	mContext;
	public ArrayList<BaseObject> mObjList;
	public MessageTask mTask;

	/**
	 * background buffer
	 *   used for save the background bin buffer
	 *   fill the variable buffer into this background buffer so we get printing buffer
	 */
	public char[] mBgBuffer;
	public char[] mPrintBuffer;
	public char[] mBuffer;
	
	private int mDots;
	
	public boolean isReady = true;
	
	/**
	 * 背景的binInfo，
	 */
	public BinInfo mBinInfo;
	/**
	 * 保存所有变量bin文件的列表；在prepareBackgroudBuffer时解析所有变量的bin文件
	 * 然后保存到这个列表中，以便在填充打印buffer时直接从这个binInfo的列表中读取相应变量buffer
	 * 无需重新解析bin文件，提高效率
	 */
	public HashMap<BaseObject, BinInfo> mVarBinList;
	
	public DataTask(Context context, MessageTask task) {
		mContext = context;
		init(task);
	}
	
	public void setTask(MessageTask task) {
		if (task == null) {
			return;
		}
		init(task);
	}
	
	private void init(MessageTask task) {
		mTask = task;
		isReady = true;
		if (task != null) {
			mObjList = task.getObjects();
		}
		mDots = 0;
		mVarBinList = new HashMap<BaseObject, BinInfo>();
	}
	/**
	 * prepareBackgroudBuffer
	 * @param f	the tlk object directory path
	 * parse the 1.bin, and then read the file content into mBgBuffer, one bit extends to one byte
	 */
	public boolean prepareBackgroudBuffer()
	{
		if (mTask == null) {
			return false;
		}
		/**记录当前打印的信息路径**/
		mBinInfo = new BinInfo(ConfigPath.getBinAbsolute(mTask.getName()), mTask.getHeads());
		if (mBinInfo == null) {
			Debug.e(TAG, "--->binInfo null");
			return false;
		}
		mBgBuffer = mBinInfo.getBgBuffer();
		if (mBgBuffer == null) {
			return false;
		}
		Debug.d(TAG, "--->bgbuffer = " + mBgBuffer.length);
		mPrintBuffer = new char[mBgBuffer.length];
		return true;
	}
	
	public char[] getPrintBuffer() {
		return getPrintBuffer(false);
	}
	
	public char[] getPrintBuffer(boolean isPreview) {
		if (mBgBuffer == null) {
			return null;
		}
		CharArrayReader cReader = new CharArrayReader(mBgBuffer);
		try {
			cReader.read(mPrintBuffer);
			if (isNeedRefresh()) {
				// addbylk_1_2/30_begin
				if (PlatformInfo.isBufferFromDotMatrix()!=0) 
				{
					refreshVariablesM(isPreview);
				}
				else
				{				
					refreshVariables(isPreview);
				}
				// addbylk_1_2/30_end
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isPreview) {
			return mPrintBuffer;
		}
		if (mBinInfo.mBytesPerColumn == 4) {
			evenBitShift();
		} else {
			/*完成平移/列变换得到真正的打印buffer*/
			rebuildBuffer();
		}
		//BinCreater.Bin2Bitmap(mPrintBuffer);
		/*test bin*/
		/*
		byte[] buffer = new byte[mBgBuffer.length * 2];
		for (int i = 0; i < buffer.length/2; i++) {
			buffer[2*i] = (byte)(mBgBuffer[i] & 0x00ff);
			buffer[2*i+1] = (byte) (((int) mBgBuffer[i])/256 & 0x00ff);
		}
		BinCreater.saveBin("/mnt/usbhost1/print.bin", buffer, 32);
		*/
		/*test bin*/
		Debug.d(TAG, "--->buffer = " + mBuffer.length);
		return mBuffer;
	}


	//addylk forM
	public void refreshVariablesM(boolean prev)	
	{
		MessageObject msg = mTask.getMsgObject();
	if(mObjList==null || mObjList.size() <= 0)
		return ;
	
	String content="";
	float  height=0;
	float  width=0;
	byte [] g_1binbits=null;
	int leftwidth=2;	
	int leftmov=3;
//	float scaleW = 2, scaleH = 1;
//	
	// 生成bin文件
	BinFileMaker maker = new BinFileMaker(mContext);
	

	for(BaseObject o:mObjList)
	{
		if((o instanceof MessageObject)	)
		{
			continue;
		}
		
		if(o instanceof CounterObject)
		{

				    //leftwidth += leftwidth > objlist.getXEnd()  ?  leftwidth : objlist.getXEnd();
													
 				
			char[] var;
			String str = prev? ((CounterObject) o).getContent() : ((CounterObject) o).getNext();
			BinInfo info = mVarBinList.get(o);
			Debug.e(TAG, "====>object index=" + o.getIndex()+"="+ leftwidth );
			if (info == null) {
				info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
				mVarBinList.put(o, info);
			}
			var = info.getVarBuffer(str);
			// BinCreater.saveBin("/mnt/usbhost1/" + o.getIndex() + ".bin", var, info.getCharsPerHFeed()*16);
			// Debug.d(TAG, "--->object x=" + o.getX()/div);(int)(o.getX()*2/18.6)
		////	int objleft =  ((int)(o.mXcor/9.5+0.5));
		//////	objleft= objleft+leftmov;
		////	BinInfo.overlap(mPrintBuffer, var, objleft , info.getCharsFeed());
			
			int objleft =  ((int)(o.mXcor/9.5+0.5));
			objleft= objleft+leftmov;
			int objtop =  ((int)(o.mYcor/9.5+0.5));
			
			BinInfo.overlap(mPrintBuffer, var , objleft,objtop,0, info.getCharsFeed());		
			if( o.mHeight==76)
			{						 
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
			}else if( o.mHeight==152)
			{
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
			}				
		//	content += o.getContent();
		//	o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
		}
		else if(o instanceof RealtimeObject)
		{					
			Debug.e(TAG, " ======000000000000000"   );
			String substr=null;
			char[] var;		
			Vector<BaseObject> rt = ((RealtimeObject) o).getSubObjs();
			
			for(BaseObject rtSub : rt)
			{					
				if(rtSub instanceof RealtimeYear)
				{
					substr = ((RealtimeYear)rtSub).getContent();
					Debug.e(TAG, " ======111" + substr );				
				}
				else if(rtSub instanceof RealtimeMonth)
				{
					substr = ((RealtimeMonth)rtSub).getContent();
					Debug.e(TAG, " ======222" + substr );								
					//continue;
				}
				else if(rtSub instanceof RealtimeDate)
				{
					substr = ((RealtimeDate)rtSub).getContent();
					Debug.e(TAG, " ======3333" + substr );						
					//continue;
				} 
				else if(rtSub instanceof RealtimeHour)
				{
					substr = ((RealtimeHour)rtSub).getContent();
					Debug.e(TAG, " ======4444"   );	
				} 
				else if(rtSub instanceof RealtimeMinute)
				{
					substr = ((RealtimeMinute)rtSub).getContent();
					Debug.e(TAG, " ======55555"   );	
				}
				else if(rtSub instanceof TextObject  )
				{
					if( rtSub.mHeight==76)
					{						 
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.8);
					}else if( o.mHeight==152)
					{
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.1);				
					}	
					continue;
				}
				//else
				//	continue;
				BinInfo info = mVarBinList.get(rtSub);
			//	if (info == null) 
				{
					info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), rtSub.getIndex()), mTask.getHeads());
					mVarBinList.put(rtSub, info);
				}
				var = info.getVarBuffer(substr);
				//BinCreater.saveBin("/mnt/usbhost1/v" + o.getIndex() + ".bin", var, info.mBytesPerHFeed*8);
				int objleft =  ((int)(rtSub.mXcor/9.5+0.5));
				objleft= objleft+leftmov;
				int objtop =  ((int)(rtSub.mYcor/9.5+0.5));
				
				BinInfo.overlap(mPrintBuffer, var , objleft,objtop,0, info.getCharsFeed());
				
			///	BinInfo.overlap(mPrintBuffer, var, objleft, info.getCharsFeed());	
				Debug.e(TAG, "=====X " + rtSub.getX()+ ", Y=" + rtSub.mYcor );
				if( rtSub.mHeight==76)
				{						 
					 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.8);
				}else if( o.mHeight==152)
				{
					 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.1);				
				}
				////- 特殊 处理 坐标 坐标 增加 
			    if(rtSub instanceof TextObject  )
				{
					if( rtSub.mHeight==76)
					{						 
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.8);
					}else if( o.mHeight==152)
					{
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.1);				
					}				 
				}
				
			}			
		//	Bitmap t = ((RealtimeObject)o).getBgBitmapN(mContext,N);
		//	can.drawBitmap(t, o.getX(), o.getY(), p);
		//	BinFromBitmap.recyleBitmap(t);
			       //  9999-99-99
		//	content +="    -  -  ";// o.getContent();
		//	o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
			Debug.e(TAG, "--->realtime: " + content);
		}
		else if(o instanceof JulianDayObject)
		{	
			char[] var;			
			String vString = ((JulianDayObject)o).getContent();
			BinInfo varbin= mVarBinList.get(o);
			if (varbin == null) {
				varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
				mVarBinList.put(o, varbin);
			}
			Debug.d(TAG, "--->real x=" + o.getX()+ ", div-x=" + o.getX() );
			var = varbin.getVarBuffer(vString);
			
	//		int objleft =  ((int)(o.mXcor/9.5+0.5));	
	//		objleft= objleft+leftmov;
	//		BinInfo.overlap(mPrintBuffer, var, objleft, varbin.getCharsFeed());	
			
			int objleft =  ((int)(o.mXcor/9.5+0.5));
			objleft= objleft+leftmov;
			int objtop =  ((int)(o.mYcor/9.5+0.5));			
			BinInfo.overlap(mPrintBuffer, var , objleft,objtop,0, varbin.getCharsFeed());			
			
			
			if( o.mHeight==76)
			{						 
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
			}else if( o.mHeight==152)
			{
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
			}	
		//	content += o.getContent();
	//		o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
		}
		else if(o instanceof ShiftObject)
		{
	 			
			
			char[]  var;
			/*班次變量特殊處理，生成v.bin時固定爲兩位有效位，如果shift的bit爲1，那前面補0，
			 *所以，shift變量的v.bin固定爲8位，如果bit=1，需要跳過前面的0*/
			int shift = ((ShiftObject)o).getShiftIndex();
			Debug.d(TAG, "--->shift ******: " + shift);
			BinInfo varbin= mVarBinList.get(o);
			if (varbin == null) {
				varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
				mVarBinList.put(o, varbin);
			}
			// Debug.d(TAG, "--->real x=" + o.getX()+ ", div-x=" + o.getX()/div );
			var = varbin.getVarBuffer(shift, ((ShiftObject)o).getBits());
		/////	int objleft =  ((int)(o.mXcor/9.5+0.5));
		/////		objleft += leftmov;
		/////	BinInfo.overlap(mPrintBuffer, var, objleft, varbin.getCharsFeed());	
			
			int objleft =  ((int)(o.mXcor/9.5+0.5));
			objleft= objleft+leftmov;
			int objtop =  ((int)(o.mYcor/9.5+0.5));			
			BinInfo.overlap(mPrintBuffer, var , objleft,objtop,0, varbin.getCharsFeed());			
			
			if( o.mHeight==76)
			{						 
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
			}else if( o.mHeight==152)
			{
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
			}	
			// content += o.getContent();
	//		o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
		}
		else if(o instanceof TextObject )
		{
			if( o.mHeight==76)
			{						 
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
			}else if( o.mHeight==152)
			{
				 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
			}			
		}
		/*else if(o instanceof TextObject )
		{
			char[] var;			
			String vString = o.getContent();//((JulianDayObject)o).getContent();
			BinInfo varbin= mVarBinList.get(o);
			if (varbin == null) {
				varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
				mVarBinList.put(o, varbin);
			}
			Debug.d(TAG, "--->real x=" + o.getX()+ ", div-x=" + o.getX()/7 );
			var = varbin.getVarBuffer(vString);
			BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/7), varbin.getCharsFeed());	
			*/
			/*
			
			content = o.getContent();
			height = o.mHeight;
			width = o.mWidth; 
			
			 Debug.e(TAG, "====DZ_buffer 000="+height+ width);
		mDots = maker.extract(content,height,width);
		
							 
		byte[] bit_32 = new byte[ mDots ];//字模高  × 字模宽 
		 Debug.e(TAG, "====DZ_buffer 111=maker.getBuffer().length "+maker.getBuffer().length );
	 bit_32 = maker.getBuffer() ;	
	 Debug.e(TAG, "====DZ_buffer 222=");
	
		 if( height ==76)
		 {
			for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
			{
					Debug.e(TAG, "====DZ_buffer.length-76=");
					int objcew=  ((int)(o.mXcor /7 )*4 );
					mPrintBuffer[(i +   objcew ) ] = (char) bit_32[i];
					mPrintBuffer[(i+1 + objcew  ) ] = (char) bit_32[i+1];
					mPrintBuffer[(i+2 + objcew ) ] = (char) bit_32[i+2];
					mPrintBuffer[(i+3 + objcew  ) ] =(char)  bit_32[i+3];						
			}
		 }
		 if( height ==120)
		 {
			for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
			{
					Debug.e(TAG, "====DZ_buffer.length-120=");
					int objcew=  ((int)(o.mXcor /7 )*4 );
					mPrintBuffer[(i +   objcew ) ] = (char) bit_32[i];
					mPrintBuffer[(i+1 + objcew  ) ] =(char)  bit_32[i+1];
					mPrintBuffer[(i+2 + objcew ) ] =(char)  bit_32[i+2];
					mPrintBuffer[(i+3 + objcew  ) ] =(char)  bit_32[i+3];
					
			}
		 }	*/
		 
		 
		 
			 				
	//	}
	
	//can.drawText(mContent, 0, height-30, mPaint);
	}
		
	//	maker.setBuffer(g_1binbits);
		// 保存bin文件
	//	maker.saveBin(ConfigPath.getTlkDir(mName) + "/1.bin",g_1binbits,32);	
	/*	
	for(BaseObject o:mObjList)
	{
		if((o instanceof MessageObject)	) {
			((MessageObject) o).setDotCount(mDots);
			break;
		}
	}
	*/
	
	return ;
}
	
	
	public void refreshVariables(boolean prev)
	{
		float scaleW = 2, scaleH = 1;
		String substr=null;
		char[] var;
		if(mObjList==null || mObjList.isEmpty())
			return;
		int heads = mTask.getHeads() == 0 ? 1 : mTask.getHeads();
		SystemConfigFile config = SystemConfigFile.getInstance(mContext);
		float div = (float) (2.0/heads);
		MessageObject msg = mTask.getMsgObject();
		// Debug.d(TAG, "+++++type:" + msg.getType());
		if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH || msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_FAST)) {
			div = 1;
			scaleW = 1;
			scaleH = 0.5f;
		} else if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL || msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST)) {
			div = 0.5f;
			scaleW = 0.5f;
			scaleH = 0.25f;
		}
		/**if high resolution message, do not divide width by 2 */
		if (msg.getResolution()) {
			Debug.d(TAG, "--->High Resolution");
			scaleW = scaleW/2;
			div = div/2;
		}
		Debug.d(TAG, "-----scaleW = " + scaleW + " div = " + div);
		//mPreBitmap = Arrays.copyOf(mBg.mBits, mBg.mBits.length);
		for(BaseObject o:mObjList)
		{
			if (o instanceof BarcodeObject) {
				Debug.d(TAG, "+++++++++++++>source: " + o.getSource());
				/* 如果二維碼從QR文件中讀 */
				if (!o.getSource()) {
					continue;
				}
				String content = "123456789";
				if (!prev) {
					QRReader reader = QRReader.getInstance(mContext);
					content = reader.read();
				}
				if (TextUtils.isEmpty(content)) {
					isReady = false;
					continue;
				}
				o.setContent(content);
				// Bitmap bmp = o.getScaledBitmap(mContext);
				Bitmap bmp = ((BarcodeObject)o).getPrintBitmap((int)(o.getWidth()/scaleW), mBinInfo.getBytesFeed()*8, (int)(o.getWidth()/scaleW), (int)(o.getHeight()/scaleH), (int)o.getY());
				Debug.d(TAG,"--->cover barcode w = " + o.getWidth() + "  h = " + o.getHeight() + " total=" + (mBinInfo.getBytesFeed()*8) + " " + (o.getWidth()/scaleW) + " " + (o.getHeight()/scaleH));
				// BinCreater.saveBitmap(bmp, "bar.png");
				BinInfo info = new BinInfo(mContext, bmp);
				
				BinInfo.overlap(mPrintBuffer, info.getBgBuffer(), (int)(o.getX()/div), info.getCharsFeed());
			} else if(o instanceof CounterObject)
			{
				String str = prev? ((CounterObject) o).getContent() : ((CounterObject) o).getNext();
				BinInfo info = mVarBinList.get(o);
				Debug.d(TAG, "--->object index=" + o.getIndex());
				if (info == null) {
					info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
					mVarBinList.put(o, info);
				}
				var = info.getVarBuffer(str);
				// BinCreater.saveBin("/mnt/usbhost1/" + o.getIndex() + ".bin", var, info.getCharsPerHFeed()*16);
				// Debug.d(TAG, "--->object x=" + o.getX()/div);
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), info.getCharsFeed());
			}
			else if(o instanceof RealtimeObject)
			{
				
				Vector<BaseObject> rt = ((RealtimeObject) o).getSubObjs();
				
				for(BaseObject rtSub : rt)
				{
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
					BinInfo info = mVarBinList.get(rtSub);
					if (info == null) {
						info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), rtSub.getIndex()), mTask.getHeads());
						mVarBinList.put(rtSub, info);
					}
					var = info.getVarBuffer(substr);
					//BinCreater.saveBin("/mnt/usbhost1/v" + o.getIndex() + ".bin", var, info.mBytesPerHFeed*8);
					BinInfo.overlap(mPrintBuffer, var, (int)(rtSub.getX()/div), info.getCharsFeed());
					Debug.d(TAG, "--->real x=" + rtSub.getX()/div );
				}
				
			}
			else if(o instanceof JulianDayObject)
			{
				String vString = ((JulianDayObject)o).getContent();
				BinInfo varbin= mVarBinList.get(o);
				if (varbin == null) {
					varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
					mVarBinList.put(o, varbin);
				}
				Debug.d(TAG, "--->real x=" + o.getX()+ ", div-x=" + o.getX()/div );
				var = varbin.getVarBuffer(vString);
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), varbin.getCharsFeed());
				
			} else if (o instanceof ShiftObject) {
				/*班次變量特殊處理，生成v.bin時固定爲兩位有效位，如果shift的bit爲1，那前面補0，
				 *所以，shift變量的v.bin固定爲8位，如果bit=1，需要跳過前面的0*/
				int shift = ((ShiftObject)o).getShiftIndex();
				Debug.d(TAG, "--->shift ******: " + shift);
				BinInfo varbin= mVarBinList.get(o);
				if (varbin == null) {
					varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
					mVarBinList.put(o, varbin);
				}
				// Debug.d(TAG, "--->real x=" + o.getX()+ ", div-x=" + o.getX()/div );
				var = varbin.getVarBuffer(shift, ((ShiftObject)o).getBits());
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), varbin.getCharsFeed());
			} else if (o instanceof LetterHourObject) {
				BinInfo varbin= mVarBinList.get(o);
				if (varbin == null) {
					varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads(), 24);
					mVarBinList.put(o, varbin);
				}
				String t = ((LetterHourObject) o).getContent();
				var = varbin.getVarBuffer(t);
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), varbin.getCharsFeed());
			} else if (o instanceof WeekOfYearObject) {
				BinInfo varbin= mVarBinList.get(o);
				if (varbin == null) {
					varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
					mVarBinList.put(o, varbin);
				}
				String t = ((WeekOfYearObject) o).getContent();
				var = varbin.getVarBuffer(t);
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), varbin.getCharsFeed());
			}  else if (o instanceof WeekDayObject) {
				BinInfo varbin= mVarBinList.get(o);
				if (varbin == null) {
					varbin = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()), mTask.getHeads());
					mVarBinList.put(o, varbin);
				}
				String t = ((WeekDayObject) o).getContent();
				var = varbin.getVarBuffer(t);
				BinInfo.overlap(mPrintBuffer, var, (int)(o.getX()/div), varbin.getCharsFeed());
			} else
			{
				Debug.d(TAG, "not Variable object");
			}
		}
	}
	
	
	public ArrayList<BaseObject> getObjList() {
		return mObjList;
	}
	
	
	public void setDots(int dots) {
		mDots = dots;
	}
	
	public int getDots() {
		return mDots;
	}
	
	public boolean isNeedRefresh() {
		
		if (mObjList == null || mObjList.isEmpty()) {
			return false;
		}
		
		for(BaseObject o:mObjList)
		{
			if((o instanceof CounterObject)
					|| (o instanceof RealtimeObject)
					|| (o instanceof JulianDayObject)
					|| (o instanceof ShiftObject)
					|| (o instanceof LetterHourObject)
					|| (o instanceof WeekOfYearObject)
					|| (o instanceof WeekDayObject)
					|| o.getSource())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 对buffer进行左右移动变换，生成真正的打印数据
	 */
	public void rebuildBuffer() {
		BaseObject object = null;
		ArrayList<SegmentBuffer> buffers = new ArrayList<SegmentBuffer>();
		for (BaseObject msg : mTask.getObjects()) {
			if (msg instanceof MessageObject) {
				object = msg;
				break;
			}
		}
		/*分头处理*/
		int type = 1;
		if (object != null) {
			type = mTask.getHeads();
		}
		Debug.d(TAG, "--->type=" + type);
		for (int i = 0; i < type; i++) {
			/**
			 * for 'Nova' header, shift & mirror is forbiden; 
			 */
			if (((MessageObject)object).getType() == MessageType.MESSAGE_TYPE_NOVA) {
				buffers.add(new SegmentBuffer(mContext, mPrintBuffer, i, type, mBinInfo.getCharsFeed(), SegmentBuffer.DIRECTION_NORMAL, 0));
			} else {
				buffers.add(new SegmentBuffer(mContext, mPrintBuffer, i, type, mBinInfo.getCharsFeed(), Configs.getMessageDir(i), Configs.getMessageShift(i)));
			}
		}
		
		/*计算转换后的buffer总列数*/
		int columns=0;
		int hight = 0;
		for (SegmentBuffer segmentBuffer : buffers) {
			columns = segmentBuffer.getColumns() > columns?segmentBuffer.getColumns():columns;
			hight = segmentBuffer.mHight * buffers.size();
		}
		
		mBuffer = new char[columns * hight];
		/*处理完之后重新合并为一个buffer, 因为涉及到坐标平移，所以不能对齐的段要补0*/
		for (int j=0; j < columns; j++) {
			for (SegmentBuffer buffer : buffers) {
				buffer.readColumn(mBuffer, j, j*hight + buffer.mHight*buffer.mType);
			}
		}
		
	}
	/**
	 * 对齐设定, 只针对32Bit × N的buffer，其他buffer不处理
	 * 1.  原来在buffer的总长度， 增加N 列。 
	 * 2.原buffer列中第X列，所有偶数bit，   0,2,4。。。 34 bit，  后移到第n+X列。
	 * 例如·， 
	 * a. 如果设为 0，  就是现在的buffer , 完全没变化。 
	 * b. 如果设为4，  则 buffer 增加 4 列，  16 B。 
     * 例如：  没4B 为一列， 
	 * 第0 列的 的偶数bit （在0-3  字节中，）会 移到 新buffer的第（0+4）= 列的偶数bit， （在16-19Ｂ）。　
	 */
	public void evenBitShift() {
		int shift = Configs.getEvenShift();
		mBuffer = new char[mPrintBuffer.length + shift  * 2];
		CharArrayReader cReader = new CharArrayReader(mPrintBuffer);
		try {
			cReader.read(mBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**/
		char next=0;
		char cur = 0;
		int columns = mBinInfo.mColumn + shift;
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < 2; j++) {
				cur = mBuffer[(columns-1-i)*2 + j];
				int col = shift + i + 1; 
				if (col >= columns) {
					next = 0;
				} else {
					next = mBuffer[2*(columns - col) + j];
				}
				cur = (char) (cur & 0x0AAAA);
				next = (char) (next & 0x05555);
				// System.out.println("--->cur&a0a0=" + String.valueOf((int)cur) + ",  next&a0a0=" + String.valueOf((int)next));
				mBuffer[(columns-1-i)*2 + j] = (char) (cur | next);
				// System.out.println("--->buffer[" + ((columns-1-i)*2 + j) + "]=" + String.valueOf((int)mBuffer[(columns-1-i)*2 + j]));
			}
			
		}
	}
	
	public BinInfo getInfo() {
		return mBinInfo;
	}
	
	/**
	 * 用於清洗的buffer
	 * @return
	 */
	public char[] preparePurgeBuffer() {
		InputStream stream;
		try {
			stream = mContext.getAssets().open("purge/single.bin");
			mBinInfo = new BinInfo(stream, 1);
			char[] buffer = mBinInfo.getBgBuffer();
			stream.close();
			char[] rb = new char[buffer.length * 12];
			for(int i = 0; i < 12; i++) {
				System.arraycopy(buffer, 0, rb, i * buffer.length, buffer.length -1);
			}
			return rb;
//			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getHeads() {
		return mTask.getHeads();
	}
	
	public int getHeadType() {
		return mTask.getHeadType();
	}
	
	public int getBufferHeightFeed() {
		if (mBinInfo == null) {
			return 0;
		}
		return mBinInfo.mCharsPerHFeed;
	}
	
	public int getBufferColumns() {
		if (mBinInfo == null) {
			return 0;
		}
		return mBinInfo.mColumn;
	}
	
	public Bitmap getPreview() {
		char[] preview = getPrintBuffer(true);
		if (preview == null) {
			return null;
		}
		// String path = "/mnt/usbhost1/prev.bin";
		// BinCreater.saveBin(path, preview, getInfo().mBytesPerHFeed*8*getHeads());
		Debug.d(TAG, "--->column=" + mBinInfo.mColumn + ", charperh=" + mBinInfo.mCharsPerHFeed);
		return BinFromBitmap.Bin2Bitmap(preview, mBinInfo.mColumn, mBinInfo.mCharsFeed*16);
	}
}