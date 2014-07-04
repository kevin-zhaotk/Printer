package com.industry.printer.object;

import android.content.Context;

public class TextObject extends BaseObject {

	public TextObject(Context context, float x) {
		super( context, BaseObject.OBJECT_TYPE_TEXT, x);
	}

	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX(), 5)+"^";
		str += BaseObject.floatToFormatString(getY(), 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd(), 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd()-getY(), 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^"+mContent;
		System.out.println("file string ["+str+"]");
		return str;
	}
}
