package com.industry.printer.object;

import java.util.Vector;

import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.data.DotMatrixReader;
import com.industry.printer.data.InternalCodeCalculater;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;

public class RealtimeObject extends BaseObject {

	public String mFormat; /* yyyy-mm-dd hh:nn for example*/
	public Vector<BaseObject> mSubObjs;
	public Context mContext;
	
	public RealtimeObject(Context context,  float x) {
		super(context, BaseObject.OBJECT_TYPE_RT, x);
		//Time t = new Time();
		//Debug.d(TAG, ">>>RealtimeObject");
		//t.set(System.currentTimeMillis());
		Debug.d(TAG, ">>>RealtimeObject");
		mSubObjs = new Vector<BaseObject>();
		setFormat("YYYY-MM-DD");
		//setContent(BaseObject.intToFormatString(t.year, 4) +"/"+BaseObject.intToFormatString(t.month+1, 2)+"/"+BaseObject.intToFormatString(t.monthDay, 2));
	}

	public void setFormat(String format)
	{
		if(format==null || (mFormat!= null &&mFormat.equals(format)))
			return;
		Debug.d(TAG, ">>>setFormat");
		mFormat = format;
		parseFormat();
		super.setWidth(mXcor_end - mXcor);
	}
	
	public String getFormat()
	{
		return mFormat;
	}
	
	public void parseFormat()
	{
		int i=0;
		float x = getX();
		System.out.println("parseFormat x="+x);
		String str = mFormat.toUpperCase();
		BaseObject o=null;
		mSubObjs.clear();
		for(;str != null && str.length()>0;)
		{
			if(!str.startsWith("YYYY", i) &&
					!str.startsWith("YY", i) &&
					!str.startsWith("MM", i) &&
					!str.startsWith("DD", i) &&
					!str.startsWith("HH", i) &&
					!str.startsWith("NN", i))
			{
				i += 1;
				continue;
			}
			System.out.println("str="+str+", i="+i);
			if(i>0)
			{
				o = new TextObject(mContext, x);
				o.setContent(str.substring(0, i));
				mSubObjs.add(o);
				/*树莓系统通过点阵字库计算坐标，每个字模列宽为16bit*/ 
				if (PlatformInfo.isSmfyProduct()) {
					x = x + o.getContent().length() * 16;
				} 
				/*通过bitmap提取点阵的系统用下面的计算方法*/
				else if (PlatformInfo.isFriendlyProduct()) {
					x = o.getXEnd();
				}
				
				System.out.println("realtime con ="+str.substring(0, i)+", x_end="+x);
			}
			
			if(str.startsWith("YYYY", i))
			{
				System.out.println("YYYY detected");
				o = new RealtimeYear(mContext, x,true);
				mSubObjs.add(o);
				i += 4;
			}
			else if(str.startsWith("YY", i))
			{
				System.out.println("YY detected");
				o = new RealtimeYear(mContext, x,false);
				mSubObjs.add(o);
				i += 2;
			}
			else if(str.startsWith("MM", i))
			{
				System.out.println("MM detected");
				o = new RealtimeMonth(mContext, x);
				mSubObjs.add(o);
				i += 2;
			}
			else if(str.startsWith("DD", i))
			{
				System.out.println("DD detected");
				o = new RealtimeDate(mContext, x);
				mSubObjs.add(o);
				i += 2;
			}
			else if(str.startsWith("HH", i))
			{
				System.out.println("HH detected");
				o = new RealtimeHour(mContext, x);
				mSubObjs.add(o);
				i += 2;
			}
			else if(str.startsWith("NN", i))
			{
				System.out.println("NN detected");
				o = new RealtimeMinute(mContext, x);
				mSubObjs.add(o);
				i += 2;
			} else {
				continue;
			}
			
			/*树莓系统通过点阵字库计算坐标，每个字模列宽为16bit*/ 
			if (PlatformInfo.isSmfyProduct()) {
				x = x + o.getContent().length() * 16;
			} 
			/*通过bitmap提取点阵的系统用下面的计算方法*/
			else if (PlatformInfo.isFriendlyProduct()) {
				x = o.getXEnd();
			}
			str = str.substring(i);
			i=0;
			System.out.println("realtime c x_end="+x);
		}
		mXcor_end = x;
		
	}
	
	@Override
	public Bitmap getScaledBitmap(Context context)
	{
		Debug.d(TAG, "getBitmap width="+(mXcor_end - mXcor)+", mHeight="+mHeight);
		Bitmap bmp = Bitmap.createBitmap((int)(mXcor_end - mXcor) , (int)mHeight, Bitmap.Config.ARGB_8888);
		//System.out.println("getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		
		for(BaseObject o : mSubObjs)
		{
			//System.out.println(""+o.getContent()+",id="+o.mId);
			Bitmap b = o.getScaledBitmap(context);
			mCan.drawBitmap(b, o.getX()-getX(), 0, mPaint);
			BinFromBitmap.recyleBitmap(b);
		}
		return bmp;
	}
	
	
	public Bitmap getBgBitmap(Context context)
	{
		System.out.println("getBitmap width="+(mXcor_end - mXcor)+", mHeight="+mHeight);
		Bitmap bmp = Bitmap.createBitmap((int)(mXcor_end - mXcor) , (int)mHeight, Bitmap.Config.ARGB_8888);
		//System.out.println("getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		mCan.drawColor(Color.WHITE);
		for(BaseObject o : mSubObjs)
		{
			//constant 
			if(o instanceof TextObject)
			{
				Bitmap b = o.getScaledBitmap(context);
				mCan.drawBitmap(b, o.getX()-getX(), 0, mPaint);
				BinFromBitmap.recyleBitmap(b);
			}
			else	//variable
			{
//				o.drawVarBitmap(f);
				o.generateVarBuffer();
			}
		}
		return bmp;
	}
	
	public Bitmap getBgBitmap(Context context, String f)
	{
		System.out.println("getBitmap width="+(mXcor_end - mXcor)+", mHeight="+mHeight);
		Bitmap bmp = Bitmap.createBitmap((int)(mXcor_end - mXcor) , (int)mHeight, Bitmap.Config.ARGB_8888);
		//System.out.println("getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		mCan.drawColor(Color.WHITE);
		for(BaseObject o : mSubObjs)
		{
			//constant 
			if(o instanceof TextObject)
			{
				Bitmap b = o.getScaledBitmap(context);
				mCan.drawBitmap(b, o.getX()-getX(), 0, mPaint);
				BinFromBitmap.recyleBitmap(b);
			}
			else	//variable
			{
				o.drawVarBitmap(f);
			}
		}
		return bmp;
	}
	
	
	@Override
	public void setHeight(float size)
	{
		if(size <0)
			size = 0;
		super.setHeight(size);
		//mHeight = size;
		//mYcor_end = mYcor + mHeight;
		if(mSubObjs == null)
			return;
		for(BaseObject o : mSubObjs)
		{
			o.setHeight(size);
		}
	}
	
	@Override
	public void setX(float x)
	{
		//float i;
		super.setX(x);
		if(mSubObjs == null)
			return;
		for(BaseObject o : mSubObjs)
		{
			o.setX(x);
			x = o.getXEnd();
		}
	}
	@Override
	public void setWidth(float size)
	{
		if(size<0)
			size = 0;
		float x=getX();
		Debug.d(TAG,">>>setWidth size="+size);
		//super.setWidth(size);
		if(size<0)
			size=0;
		mWidth = size;
		mXcor_end = mXcor + mWidth; 
		//mWidth = size;
		//mXcor_end = mXcor + mWidth;
		if(mSubObjs == null)
			return;
		for(BaseObject o : mSubObjs)
		{
			o.setX(x);
			System.out.println("o.content="+o.getContent() +", subWidth="+getSubWidth(o));
			o.setWidth(getSubWidth(o));
			Log.d(TAG, "o.xEnd="+o.getXEnd());
			x = o.getXEnd();
		}
		Debug.d(TAG, "<<<<setWidth");
	}
	
	public float getSubWidth(BaseObject o)
	{
		System.out.println("width="+getWidth()+", content len="+ o.getContent().length()+", len="+mFormat.length());
		return (getWidth() * o.getContent().length())/mFormat.length();
	}
	
	@Override
	public void setSelected(boolean s)
	{
		super.setSelected(s);
		if(mSubObjs == null)
				return;
		for(BaseObject o : mSubObjs)
		{
			o.setSelected(s);
		}
	}
	
	@Override
	public String getContent() {
		mContent = "";
		
		for (BaseObject object : mSubObjs) {
			mContent += object.getContent();
		}
		return mContent;
	}
	
	
	@Override
	public void generateVarbinFromMatrix(String f) {
		for (BaseObject object : getSubObjs()) {
			if (object.mId.equals(BaseObject.OBJECT_TYPE_TEXT)) {
				continue;
			}
			object.generateVarbinFromMatrix(f);
			
		}
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
		str += BaseObject.floatToFormatString(getYEnd(), 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		//str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^0000^000^"+mFormat;
		System.out.println("file string ["+str+"]");
		return str;
	}
	
	public Vector<BaseObject> getSubObjs()
	{
		return mSubObjs;
	}

}
