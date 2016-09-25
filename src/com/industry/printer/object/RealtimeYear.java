package com.industry.printer.object;

import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class RealtimeYear extends BaseObject {

	public static final String TAG="RealtimeYear";
	public String mFormat;
	public int mOffset;
	public RealtimeObject mParent;
	
	public RealtimeYear(Context context, float x, boolean f) {
		super(context, BaseObject.OBJECT_TYPE_DL_YEAR, x);
		mOffset = 0;
		mParent = null;
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
	
	public RealtimeYear(Context context, RealtimeObject parent, float x, boolean f) {
		this(context, x ,f);
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
		if(mFormat.length()==2)
			setContent(BaseObject.intToFormatString(t.year%100, 2));
		else if(mFormat.length()==4)
			setContent(BaseObject.intToFormatString(t.year, 4));
		Debug.d(TAG, ">>getContent, "+mContent);
		return mContent;
	}
	
	public String toString()
	{
		float prop = getProportion();
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX() * prop, 5)+"^";
		str += BaseObject.floatToFormatString(getY()*2 * prop, 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd() * prop, 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd()*2 * prop, 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		//str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^000^";
		str += mParent == null? "00000":BaseObject.intToFormatString(mParent.getOffset(), 5);
		str += "^00000000^00000000^00000000^0000^0000^" + mFont + "^000^000";
		Debug.d(TAG, "file string ["+str+"]");
		return str;
	}
}
