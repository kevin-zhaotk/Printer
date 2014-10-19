package com.industry.printer.object;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class RealtimeYear extends BaseObject {

	public static final String TAG="RealtimeYear";
	public String mFormat;
	
	public RealtimeYear(Context context, float x, boolean f) {
		super(context, BaseObject.OBJECT_TYPE_DL_YEAR, x);
		// TODO Auto-generated constructor stub
		Time t = new Time();
		t.set(System.currentTimeMillis());
		if(!f)
		{
			mFormat="YY";
			setContent(BaseObject.intToFormatString(t.year%100, 2));
		}
		else if(f)
		{
			mFormat="YYYY";
			setContent(BaseObject.intToFormatString(t.year, 4));
		}
		System.out.println("<<<RealtimeYear");
	}

	@Override
	public String getContent()
	{
		Time t = new Time();
		
		t.set(System.currentTimeMillis());
		if(mFormat.length()==2)
			setContent(BaseObject.intToFormatString(t.year%100, 2));
		else if(mFormat.length()==4)
			setContent(BaseObject.intToFormatString(t.year, 4));
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
