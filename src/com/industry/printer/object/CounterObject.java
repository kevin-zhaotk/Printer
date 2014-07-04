package com.industry.printer.object;

import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.util.Log;

public class CounterObject extends BaseObject {

	public int mBits;
	public boolean mDirection;
	public int mMax;
	public int mMin;
	public int mValue;
	public int mStepLen;
	//public int mCurVal;
	
	public CounterObject(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_CNT, x);
		mMin=0;
		mBits=5;
		mValue=0;
		mStepLen=1;
		mDirection = true;
		mMax = (int) Math.pow(10, mBits) -1;
		//Debug.d(TAG, "mMax")
		setContent("00000");
	}

	public void setBits(int n)
	{
		mBits = n;
		mValue = 0;
		setContent( BaseObject.intToFormatString(mValue, mBits));
	}
	
	public int getBits()
	{
		return mBits;
	}
	
	public void setMax(int max)
	{
		mMax = max;
	}
	
	public int getMax()
	{
		return mMax;
	}
	
	public void setMin(int min)
	{
		mMin = min;
	}
	
	public int getMin()
	{
		return mMin;
	}
	
	public void setDirection(boolean dir)
	{
		mDirection = dir;
	}
	
	public boolean getDirection()
	{
		return mDirection;
	}
	
	public void setSteplen(int step)
	{
		if(step >= 0)
		mStepLen = step;
	}
	
	public int getSteplen()
	{
		return mStepLen;
	}
	
	public int getValue()
	{
		return mValue;
	}
	
	public void setValue(int value)
	{
		if(mValue >= 0)
		mValue = value;
	}
	
	public String getNext()
	{
		if(mDirection)	//increase
		{
			if(mValue+mStepLen > mMax)
				mValue=mMin;
			else
				mValue++;
		}
		else	//decrease
		{
			if(mValue-mStepLen < mMin)
				mValue=mMax;
			else
				mValue--;
		}
		setContent( BaseObject.intToFormatString(mValue, mBits));
		Debug.d(TAG, "getNext mContent="+mContent+", mValue="+mValue+", mMax="+mMax);
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
		str += BaseObject.intToFormatString(mBits, 3)+"^";
		str += "000^000^000^000^";
		str += BaseObject.intToFormatString(mMax, 8)+"^";
		str += BaseObject.intToFormatString(mMin, 8)+"^";
		str += BaseObject.intToFormatString(Integer.parseInt(mContent) , 8)+"^";
		str += "00000000^0000^0000^0000^000^000";
		System.out.println("counter string ["+str+"]");
		return str;
	}
}
