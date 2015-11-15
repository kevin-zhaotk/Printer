package com.industry.printer.data;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.database.CharArrayBuffer;

import com.industry.printer.BinInfo;
import com.industry.printer.MessageTask;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
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
	public ArrayList<BaseObject> mObjList;
	public MessageTask mTask;

	/**
	 * background buffer
	 *   used for save the background bin buffer
	 *   fill the variable buffer into this background buffer so we get printing buffer
	 */
	public char[] mBgBuffer;
	public char[] mPrintBuffer;
	
	private int mDots;
	
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
		mTask = task;
		mObjList = task.getObjects();
		mDots = 0;
		mVarBinList = new HashMap<BaseObject, BinInfo>();
	}
	
	public void setTask(MessageTask task) {
		if (task == null) {
			return;
		}
		mTask = task;
		mObjList = task.getObjects();
		mVarBinList = new HashMap<BaseObject, BinInfo>();
	}
	/**
	 * prepareBackgroudBuffer
	 * @param f	the tlk object directory path
	 * parse the 1.bin, and then read the file content into mBgBuffer, one bit extends to one byte
	 */
	public boolean prepareBackgroudBuffer()
	{
		/**记录当前打印的信息路径**/
		mBinInfo = new BinInfo(ConfigPath.getBinAbsolute(mTask.getName()));
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
				BinInfo info = mVarBinList.get(o);
				if (info == null) {
					info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()));
					mVarBinList.put(o, info);
				}
				var = info.getVarBuffer(str);
				BinInfo.overlap(mPrintBuffer, var, (int)o.getX() * 2, info.getCharsPerColumn());
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
						info = new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), rtSub.getIndex()));
						mVarBinList.put(rtSub, info);
					}
					var = info.getVarBuffer(substr);
					BinInfo.overlap(mPrintBuffer, var, (int)rtSub.getX()*2, info.getCharsPerColumn());
				}				
			}
			else if(o instanceof JulianDayObject)
			{
				String vString = ((JulianDayObject)o).getContent();
				BinInfo varbin= new BinInfo(ConfigPath.getVBinAbsolute(mTask.getName(), o.getIndex()));
				var = varbin.getVarBuffer(vString);
				BinInfo.overlap(mPrintBuffer, var, (int)o.getX() *2, varbin.getCharsPerColumn());
			}
			else
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
					|| (o instanceof JulianDayObject))
			{
				return true;
			}
		}
		return false;
	}
}