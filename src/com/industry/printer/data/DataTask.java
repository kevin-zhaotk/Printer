package com.industry.printer.data;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.database.CharArrayBuffer;

import com.industry.printer.BinInfo;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.RealtimeDate;
import com.industry.printer.object.RealtimeHour;
import com.industry.printer.object.RealtimeMinute;
import com.industry.printer.object.RealtimeMonth;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RealtimeYear;
import com.industry.printer.object.TLKFileParser;


/**
 * 用于生成打印数据和预览图
 * @author zhaotongkai
 *
 */
public class DataTask {
	
	public static final String TAG = DataTask.class.getSimpleName();
	
	public Context	mContext;
	public Vector<BaseObject> mObjList;
	public BinInfo mBinInfo;
	public String mMessage;

	/**
	 * background buffer
	 *   used for save the background bin buffer
	 *   fill the variable buffer into this background buffer so we get printing buffer
	 */
	public char[] mBgBuffer;
	public char[] mPrintBuffer;
	
	private int mDots;
	
	public DataTask(Context context) {
		mContext = context;
		mObjList = new Vector<BaseObject>();
		mDots = 0;
	}
	
	/**
	 * prepareBackgroudBuffer
	 * @param f	the tlk object directory path
	 * parse the 1.bin, and then read the file content into mBgBuffer, one bit extends to one byte
	 */
	public boolean prepareBackgroudBuffer(String tlk)
	{
		String path=null;
		File fp = new File(tlk);
		if(fp.isFile())
			path = new File(tlk).getParent();
		else
			path = tlk;
		/**记录当前打印的信息路径**/
		mMessage = path;
		TLKFileParser parser = new TLKFileParser(mContext, mMessage);
		parser.parse(mContext, mMessage+"/1.TLK", mObjList);
		Debug.d(TAG, "-----objlist size="+mObjList.size());
		mBinInfo = new BinInfo(mMessage+"/1.bin");
		if (mBinInfo == null) {
			return false;
		}
		mBgBuffer = mBinInfo.getBgBuffer();
		if (mBgBuffer == null) {
			return false;
		}
		mPrintBuffer = new char[mBgBuffer.length];
		return true;
	}
	
	
	public char[] getPrintBuffer() {
		CharArrayReader cReader = new CharArrayReader(mBgBuffer);
		try {
			cReader.read(mPrintBuffer);
			if (isNeedRefresh()) {
				refreshVariables();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		return mPrintBuffer;
	}
	
	public void refreshVariables()
	{
		String substr=null;
		char[] var;
		if(mObjList==null || mObjList.isEmpty())
			return;
		Debug.d(TAG, "-----objlist size="+mObjList.size());
		//mPreBitmap = Arrays.copyOf(mBg.mBits, mBg.mBits.length);
		for(BaseObject o:mObjList)
		{
			if(o instanceof CounterObject)
			{
				String str = ((CounterObject) o).getNext();
				BinInfo varbin = new BinInfo(mMessage+"/" + "v" + o.getIndex() +".bin");
				var = varbin.getVarBuffer(str);
				BinInfo.overlap(mPrintBuffer, var, (int)o.getX(), varbin.getCharsPerColumn());
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
					BinInfo varbin = new BinInfo(mMessage+"/" + "v"+rtSub.getIndex() +".bin");
					var = varbin.getVarBuffer(substr);
					BinInfo.overlap(mPrintBuffer, var, (int)rtSub.getX()*2, varbin.getCharsPerColumn());
				}				
			}
			else if(o instanceof JulianDayObject)
			{
				String vString = ((JulianDayObject)o).getContent();
				BinInfo varbin= new BinInfo(mMessage + "/v" + o.getIndex() + ".bin");
				var = varbin.getVarBuffer(vString);
				BinInfo.overlap(mPrintBuffer, var, (int)o.getX(), varbin.getCharsPerColumn());
			}
			else
			{
				Debug.d(TAG, "not Variable object");
			}
		}
	}
	
	
	public Vector<BaseObject> getObjList() {
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
					|| (o instanceof JulianDayObject))
			{
				return true;
			}
		}
		return false;
	}
}