package com.industry.printer.object;

import com.industry.printer.R;
import android.content.Context;

public class MessageObject extends BaseObject {

	public int mPrinter;
	public int mDots;
	
	public MessageObject(Context context,  float x) {
		super(context, BaseObject.OBJECT_TYPE_MsgName, x);
		//mIndex = index;
		mPrinter=0;
		String name = (String)context.getResources().getString(R.string.object_msg_name);
		mContent = name;
	}
	
	public void setPrinter(int i)
	{
		String[] printer =	mContext.getResources().getStringArray(R.array.strPrinterArray);
		if(i<0 || i>printer.length)
			return ;
		mPrinter = i;
	}

	public int getPrinter()
	{
		return mPrinter;
	}
	
	public void setDotCount(int count) {
		mDots = count;
	}
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += "00000^00000^00000^00000^0^000^000^000^000^000^000^";
		str += BaseObject.intToFormatString(mDots, 7)+"^00000000^00000000^00000000^0000^0000^0000^000^"+mContent;
		System.out.println("file string ["+str+"]");
		return str;
	}
}
