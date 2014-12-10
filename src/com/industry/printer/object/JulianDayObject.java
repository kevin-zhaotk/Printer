package com.industry.printer.object;

import android.content.Context;
import android.text.format.Time;

public class JulianDayObject extends BaseObject {

	public JulianDayObject(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_JULIAN, x);
		setContent();
	}
	
	public void setContent()
	{
		int day = Time.getJulianDay(System.currentTimeMillis(), 0);
		Time t = new Time();
		 t.set(System.currentTimeMillis());
		 day = t.yearDay+1;
		setContent(BaseObject.intToFormatString(day, 3));
		System.out.println("day ="+day+", mContent="+mContent);
	}
	@Override
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX()/2, 5)+"^";
		str += BaseObject.floatToFormatString(getY(), 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd()/2, 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd(), 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		str += "000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^000";
		System.out.println("Julian-day string ["+str+"]");
		return str;
	}
	
	@Override
	public String getContent()
	{
		return mContent;
	}
}
