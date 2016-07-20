package com.industry.printer.object;

import com.industry.printer.MessageTask.MessageType;
import com.industry.printer.R;
import com.industry.printer.Utils.Debug;

import android.content.Context;

public class MessageObject extends BaseObject {

	public int mDots;
	public int mType;
	
	public static final int PIXELS_PER_MM = 12;
	public static final float[] mBaseList = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, (float) 12.7};
	
	public MessageObject(Context context,  float x) {
		super(context, BaseObject.OBJECT_TYPE_MsgName, x);
		//mIndex = index;
		Debug.d(TAG, "--->MessageObject: " + context.getResources());
		String name = (String)context.getResources().getString(R.string.object_msg_name);
		mContent = name;
		mType = 0;
	}
	
	public void setType(int i)
	{
		String[] printer =	mContext.getResources().getStringArray(R.array.strPrinterArray);
		if(i<0 || i>printer.length)
			return ;
		mType = i;
	}
	
	public void setType(String type) {
		String[] printer =	mContext.getResources().getStringArray(R.array.strPrinterArray);
		for (int i=0; i<printer.length; i++) {
			if (printer[i].equals(type)) {
				mType = i;
				break;
			}
		}
	}

	
	public int getType() {
		return mType;
	}
	
	public String getPrinterName() {
		String[] printer =	mContext.getResources().getStringArray(R.array.strPrinterArray);
		return printer[mType];
	}
	
	public void setDotCount(int count) {
		mDots = count;
	}

	
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += "00000^00000^00000^00000^0^000^";
		str += BaseObject.intToFormatString(mType,3) + "^000^000^000^000^";
		str += BaseObject.intToFormatString(mDots, 7)+"^00000000^00000000^00000000^0000^0000^0000^000^"+mContent;
		System.out.println("file string ["+str+"]");
		return str;
	}
	
	public String[] getDisplayFSList() {
		String[] size = new String[mBaseList.length];
		if (mType == 0 || mType == 1) {
			for (int i = 0; i < size.length; i++) {
				size[i] = String.valueOf(mBaseList[i]); 
			}
		} else if (mType == 2) {
			for (int i = 0; i < size.length; i++) {
				size[i] = String.valueOf(mBaseList[i] * 2); 
			}
		}
		return size;
	}
	
	public float getRealFontsize(String size) {
		float h=1;
		Debug.d(TAG, "--->size: " + size);
		try {
			h = Float.parseFloat(size);
			
		} catch(Exception e) {
			Debug.d(TAG, "--->exception: " + e.getMessage());
		}
		Debug.d(TAG, "--->h: " + h);
		if (mType == 0 || mType == 1) {
			return h;
		} else if (mType == 2) {
			return h/2;
		}
		return h;
	}
	
	public int getPixels(String size) {
		float h = getRealFontsize(size);
		return (int)(h * PIXELS_PER_MM);
	}
	
	public String getDisplayFs(float size) {
		float h = 0;
		if (mType == MessageType.MESSAGE_TYPE_12_7 || mType == MessageType.MESSAGE_TYPE_12_7_S) {
			h = size/PIXELS_PER_MM;
		} else if (mType == MessageType.MESSAGE_TYPE_25_4) {
			h = size/(2 * PIXELS_PER_MM);
		} else {
			h = size/PIXELS_PER_MM;
		}
		for (int i = 0; i < mBaseList.length; i++) {
			if ((h > mBaseList[i] - 0.3) && (h < mBaseList[i] + 0.3)) {
				h = mBaseList[i];
				break;
			}
		}
		return String.valueOf(h);
	}
}
