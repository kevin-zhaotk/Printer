package com.industry.printer.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.industry.printer.Utils.PlatformInfo;

/**
 * BinFileMaker作为操作BinCreater的统一接口，生成二进制打印buffer文件（即bin文件）的接口类有两个：
 * BinFromBitmap和BinFromDotMatrix；分别表示从bitmap提取和从点阵字库读取。
 * 通过设备model区分使用哪种生成接口类
 * 目前通过点阵字库生成打印buffer的设备为：smfy-super3；
 * @author zhaotongkai
 *
 */
public class BinFileMaker {

	private BinCreater mBinCreater = null;
	private Context mContext;
	public BinFileMaker(Context context) {
		mContext = context;
		init();
	}
	
	public void init() {
		if (PlatformInfo.isBufferFromDotMatrix()!=0) 
		{
			mBinCreater = new BinFromDotMatrix(mContext);
		} else {
			mBinCreater = new BinFromBitmap();
		}
	}
	
	public int  extract(Bitmap bmp) {
		if (mBinCreater == null) {
			init();
		}
		return mBinCreater.extract(bmp);
	}
	
	public int extract(String text,float height,float width) 
	{
		if (mBinCreater == null) {
			init();
		}
		return mBinCreater.extract(text,height,width);
	}
	
	
	public void save(String f) {
		mBinCreater.saveBin(f);
	}
    public  void saveBin(String f, byte[] dots, int single){
		mBinCreater.saveBin(f,dots,single);	
    }
	
	public byte[] getBuffer() {
		return mBinCreater.mBinBits;
	}
	public void setBuffer(byte[] Bits) {
		 mBinCreater.mBinBits=Bits;
	}	
}
