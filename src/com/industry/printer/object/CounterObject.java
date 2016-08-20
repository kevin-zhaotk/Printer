package com.industry.printer.object;

import com.industry.printer.MainActivity;
import com.industry.printer.R;
import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.renderscript.Sampler.Value;
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
		mStepLen=1;
		mDirection = true;
		setBits(5);
	}

	public void setBits(int n)
	{
		mBits = n;
		mValue = 1;
		setContent( BaseObject.intToFormatString(mValue, mBits));
		mMax = (int) Math.pow(10, mBits) -1;
	}
	
//	@Override
//	public void setContent(String content) {
//		super.setContent(content);
//		
//	}
	
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
	
	public void setRange(int start, int end) {
		if (start <= end) {
			mDirection = true;
			mMin = start;
			mMax = end;
		} else {
			mDirection = false;
			mMin = end;
			mMax = start;
		}
		Debug.d(TAG, "setRange mMax="+mMax + ",  mMin=" + mMin);
	}
	
	public void setDirection(boolean dir)
	{
		mDirection = dir;
	}
	
	public String getDirection()
	{
		String[] directions = mContext.getResources().getStringArray(R.array.strDirectArray);
		return mDirection ? directions[0] : directions[1];
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
		if( mMin < mMax) {
			if(value < mMin || value> mMax) {
				mValue = mMin;
			}
			else {
				mValue = value;
			}
		} else {
			if (value > mMin || value < mMax) {
				mValue = mMin;
			} else {
				mValue = value;
			}
		}
		mContent = BaseObject.intToFormatString(mValue, mBits);
	}
	
	@Override
	public void setContent(String content) {
		try{
			Debug.d(TAG, "--->setContent content="+content);
			int value = Integer.parseInt(content);
			Debug.d(TAG, "setContent value="+value);
			if( mMin < mMax) {
				if(value < mMin || value> mMax) {
					mValue = mMin;
				}
				else {
					mValue = value;
				}
			} else {
				if (value > mMin || value < mMax) {
					mValue = mMin;
				} else {
					mValue = value;
				}
			}
			
		} catch (Exception e) {
			mValue = mMin;
			Debug.d(TAG, "--->setContent exception: " + e.getMessage());
		}
		mContent = BaseObject.intToFormatString(mValue, mBits);
		Debug.d(TAG, "setContent content="+content+", value="+mValue+", mMax="+mMax);
	}
	
	
	public String getNext()
	{
		Debug.d(TAG, "--->getNext mContent="+mContent+", mValue="+mValue+", mSteplen=" + mStepLen + " direction=" + mDirection);
		if(mDirection)	//increase
		{
			if(mValue+mStepLen > mMax || mValue < mMin)
				mValue=mMin;
			else
				mValue += mStepLen;
		}
		else	//decrease
		{
			if(mValue-mStepLen < mMax || mValue > mMin)
				mValue=mMax;
			else
				mValue -= mStepLen;
		}
		String value =mContent;
		setContent( BaseObject.intToFormatString(mValue, mBits));
		Debug.d(TAG, "getNext mContent="+mContent+", mValue="+mValue+", mMax="+mMax);
		return value;
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
		str += BaseObject.intToFormatString(mBits, 3)+"^";
		str += "000^000^000^000^";
		str += BaseObject.intToFormatString(mMax, 8)+"^";
		str += BaseObject.intToFormatString(mMin, 8)+"^";
		str += BaseObject.intToFormatString(Integer.parseInt(mContent) , 8)+"^";
		str += "00000000^0000^0000^" + mFont + "^000^000";
		System.out.println("counter string ["+str+"]");
		return str;
	}
}
