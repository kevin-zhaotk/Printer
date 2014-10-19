package com.industry.printer.object;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class RealtimeMonth extends BaseObject {

	public static final String TAG="RealtimeMonth";
	public RealtimeMonth(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_DL_MON, x);
		Time t = new Time();
		t.set(System.currentTimeMillis());
		setContent(BaseObject.intToFormatString(t.month+1, 2));
	}

	@Override
	public String getContent()
	{
		Time t = new Time();
		t.set(System.currentTimeMillis());
		setContent(BaseObject.intToFormatString(t.month+1, 2));
		Log.d(TAG, ">>getContent, "+mContent);
		return mContent;
	}
	
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX()/2, 5)+"^";
		str += BaseObject.floatToFormatString(getY(), 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd()/2, 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd()-getY(), 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		//str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^000";
		System.out.println("file string ["+str+"]");
		return str;
	}
}
