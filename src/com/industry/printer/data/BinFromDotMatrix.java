package com.industry.printer.data;

import android.content.Context;

public class BinFromDotMatrix extends BinCreater {

	public Context mContext;
	
	public BinFromDotMatrix(Context context) {
		mContext = context;
		mHeight = 32;
	}
	
	///@Override
	public int extract(String text,float height,float width)  {
		InternalCodeCalculater cal = InternalCodeCalculater.getInstance();
		char[] code = cal.getGBKCode(text);
		// cal.toGBCode(code);
		DotMatrixReader reader = DotMatrixReader.getInstance(mContext,height,width);
		mBinBits = reader.getDotMatrix(code,mContext,height,width);		 	 
		return reader.getDotCount(mBinBits);
	}
	
     //  addbylk_1_19/30_begin 
	public int getmatrixlen(String text,float height,float width)  {
		InternalCodeCalculater cal = InternalCodeCalculater.getInstance();
		char[] code = cal.getGBKCode(text);
		// cal.toGBCode(code);
		DotMatrixReader reader = DotMatrixReader.getInstance(mContext,height,width);
		reader.getDotMatrix(code,mContext,height,width);
		return reader.m_matrixlen ;//		 
	}
     //  addbylk_1_23/30_end 
		
}
