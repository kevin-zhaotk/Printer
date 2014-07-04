package com.industry.printer.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint.Style;

public class RectObject extends BaseObject {

	public int mLineType;
	public RectObject(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_RECT,x);
		setWidth(100);
		setHeight(50);
		setLineWidth(5);
		setLineType(0);
		mPaint.setStyle(Style.STROKE);
	}


	public void setLineType(int type)
	{
		mLineType=type;
	}
	
	public int getLineType()
	{
		return mLineType;
	}
	
	@Override
	public Bitmap getScaledBitmap(Context context)
	{
		float adjust = mLineWidth/2;
		Bitmap bmp = Bitmap.createBitmap((int)mWidth , (int)mHeight, Bitmap.Config.ARGB_8888);
		mCan = new Canvas(bmp);
		mCan.drawRect(adjust, adjust, mWidth-adjust, mHeight-adjust, mPaint);
		//can.drawText("text", 0, 4, p);
		//mCan.save();
		return bmp;
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
		str += BaseObject.floatToFormatString(getLineWidth(), 3)+"^";
		str += BaseObject.intToFormatString(getLineType(), 3)+"^";
		str += "000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^000";
		System.out.println("file string ["+str+"]");
		return str;
	}
}
