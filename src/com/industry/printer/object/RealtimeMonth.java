package com.industry.printer.object;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class RealtimeMonth extends BaseObject {

	public static final String TAG="RealtimeMonth";
	public int mOffset;
	public RealtimeObject mParent;
	
	public RealtimeMonth(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_DL_MON, x);
		Time t = new Time();
		mOffset = 0;
		mParent = null;
		t.set(System.currentTimeMillis());
		setContent(BaseObject.intToFormatString(t.month+1, 2));
	}
	
	public RealtimeMonth(Context context, RealtimeObject parent, float x) {
		this(context, x);
		mParent = parent;
	}

	@Override
	public String getContent()
	{
		if (mParent != null) {
			mOffset = mParent.getOffset();
		}
		Time t = new Time();
		t.set(System.currentTimeMillis() + mOffset * RealtimeObject.MS_DAY);
		setContent(BaseObject.intToFormatString(t.month+1, 2));
		Log.d(TAG, ">>getContent, "+mContent);
		return mContent;
	}
	
	public String toString()
	{
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX(), 5)+"^";
		str += BaseObject.floatToFormatString(getY()*2, 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd(), 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd()*2, 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		//str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^000^";
		str += mParent == null? "00000":BaseObject.intToFormatString(mParent.getOffset(), 5);
		str += "^00000000^00000000^00000000^0000^0000^" + mFont + "^000^000";
		System.out.println("file string ["+str+"]");
		return str;
	}
}
