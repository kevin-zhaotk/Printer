package com.industry.printer.data;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import android.content.Context;

import com.industry.printer.BinInfo;
import com.industry.printer.object.BaseObject;
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

	/**
	 * background buffer
	 *   used for save the background bin buffer
	 *   fill the variable buffer into this background buffer so we get printing buffer
	 */
	public byte[] mBgBuffer;
	
	public DataTask(Context context) {
		mContext = context;
		mObjList = null;
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
		TLKFileParser.parse(mContext, path+"/1.TLK", mObjList);
		try{
			mBinInfo = new BinInfo();
			mBinInfo.getBgBuffer(path+"/1.bin");
			//read the background bin bytes to global mBgBuffer
			if (mBinInfo.mBits != null) {
				mBgBuffer = Arrays.copyOf(mBinInfo.mBits, mBinInfo.mBits.length);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}