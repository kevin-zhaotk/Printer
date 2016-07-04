package com.industry.printer.object;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.graphics.Bitmap;

public class ShiftObject extends BaseObject {
	public final String TAG="ShiftObject";
	
	public int mBits;
	public int mShifts[];
	public String mValues[];
	public ShiftObject(Context context, float x) {
		super( context, BaseObject.OBJECT_TYPE_SHIFT, x);
		mBits = 1;
		mShifts = new int[5];
		mShifts[0]=800;
		mShifts[1]=1400;
		mShifts[2]=2200;
		mShifts[3]=1800;
		mShifts[4]=2400;
		mValues = new String[5];
		mValues[0] = "01";
		mValues[1] = "02";
		mValues[2] = "03";
		mValues[3] = "04";
		mValues[4] = "05";
		
		// TODO Auto-generated constructor stub
	}
	
	public void setShift(int shift, String time)
	{
		if(shift >4 || 
				shift<0 || 
				time==null || 
				time.length()>4 || 
				time.length() < 1 || 
				!checkNum(time) )
			return;
		 int i = Integer.parseInt(time);
		 if(i <0 || i > 2400)
			 return;
		mShifts[shift] = Integer.parseInt(time);
	}
	
	public int getShift(int shift)
	{
		if(shift<0 || shift>=5)
			return 0;
		return mShifts[shift];
	}
	
	
	public void setValue(int shift, String val)
	{
		if(shift >4 || shift<0 || val==null || val.length()!=mBits|| !checkNumandLetter(val))
			return;
		
		mValues[shift] = val;
	}
	
	public String getValue(int shift)
	{
		if(shift>4 || shift<0)
			return null;
		return mValues[shift];
	}
	
	@Override
	public String getContent() {
		int i=0;
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY) * 100;
		for (i = 0; i < 4; i++) {
			if (hour >= mShifts[i] && (hour < mShifts[i+1] || mShifts[i+1] == 0)) {
				break;
			}
		}
		return getValue(i);
	}
	
	@Override
	public Bitmap getScaledBitmap(Context context)
	{
		int i=0,index=0;
		Debug.d(TAG,"getScaledBitmap  mWidth="+mWidth+", mHeight="+mHeight);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
		int date = Integer.parseInt(dateFormat.format(new Date()));
		Debug.d(TAG, "date="+date);
		for(i=0; i<5; i++)
		{
			if(!isValidShift(i))
			{
				index = i-1;
				break;
			}
			//Debug.d(TAG, "mShifts["+i+"]="+mShifts[i]+", mShifts[i+1]="+mShifts[i+1]+", isValidShift(i+1)="+ isValidShift(i+1));
			if(date > mShifts[i] && isValidShift(i+1) && date<mShifts[i+1])
			{
				index =i;
				break;
			}
			else
				continue;
		}
		Debug.d(TAG, "index="+index);
		if(index>4 || index<0)
			index=0;
		setContent(mValues[index]);
		return Bitmap.createScaledBitmap(getBitmap(context), (int)mWidth, (int)mHeight, true);
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
		str += mShifts[0]+"^"+mShifts[1]+"^"+mShifts[2]+"^"+mShifts[3]+"^"+mShifts[4]+"^";
		str += mValues[0]+"^"+mValues[1]+"^"+mValues[2]+"^"+mValues[3]+"^"+mValues[4]+"^";
		str += mFont+"^";
		str += mShifts[4];
		Debug.d(TAG,"counter string ["+str+"]");
		return str;
	}
	
	
	public boolean checkNum(String args){
		Pattern p=Pattern.compile("^[0-9]*"); 
		Matcher m=p.matcher(args);
		if(m.matches())
		return true;
		else
		return false;
	}
	
	public boolean checkNumandLetter(String args)
	{
		Pattern p = Pattern.compile("^([0-9]|[a-z])*");
		Matcher m = p.matcher(args);
		if(m.matches())
			return true;
		else
			return false;
	}
	
	public boolean isValidShift(int i)
	{
		if(i<0 || i > 4)
		{
			Debug.d(TAG, "invalide i="+i);
			return false;
		}
		else if(i==0 && mShifts[i]>=0 && mShifts[i] <= 2400)
		{
			Debug.d(TAG, "valide i="+i);
			return true;
		}
		else if(i<=4 && mShifts[i]>=0 && mShifts[i] <= 2400 && (mShifts[i] > mShifts[i-1]))
		{
			Debug.d(TAG, "valide i="+i);
			return true;
		}			
		else
			return false;
	}
}
