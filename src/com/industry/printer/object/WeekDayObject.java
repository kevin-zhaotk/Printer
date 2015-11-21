package com.industry.printer.object;

import java.util.Calendar;
import java.util.Locale;

import com.industry.printer.Utils.Debug;

import android.content.Context;

public class WeekDayObject extends BaseObject {

	public WeekDayObject(Context context, float x) {
		super(context, OBJECT_TYPE_WEEKDAY, x);
		Calendar c = Calendar.getInstance();
		int dat = c.get(Calendar.DAY_OF_WEEK);
		setContent(String.valueOf(dat));
	}

	public WeekDayObject() {
		super(OBJECT_TYPE_WEEKDAY);
		Calendar c = Calendar.getInstance();
		int dat = c.get(Calendar.DAY_OF_WEEK);
		setContent(String.valueOf(dat));
	}
	
	@Override
	public String getContent() {
		Calendar c = Calendar.getInstance();
		int dat = c.get(Calendar.DAY_OF_WEEK);
		mContent = String.valueOf(dat);
		return mContent;
	}
	
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX(), 5)+"^";
		str += BaseObject.floatToFormatString(getY(), 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd(), 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd(), 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		str += "000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^000";
		Debug.d(TAG,"file string ["+str+"]");
		return str;
	}
	
	
}