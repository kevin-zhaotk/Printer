package com.industry.printer.object;

import java.util.Calendar;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.os.SystemClock;
import android.text.format.Time;

public class RealtimeDate extends BaseObject {

	public int mOffset;
	public RealtimeObject mParent;
	
	public RealtimeDate(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_DL_DATE, x);
		Time t = new Time();
		t.set(System.currentTimeMillis());
		mOffset = 0;
		mParent = null;
		setContent(BaseObject.intToFormatString(t.monthDay, 2));
	}

	public RealtimeDate(Context context, RealtimeObject parent, float x) {
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
		
		t.set(System.currentTimeMillis() + mOffset * RealtimeObject.MS_DAY - timeDelay());
		Debug.d(TAG, "--->Date: " + t.monthDay);
		setContent(BaseObject.intToFormatString(t.monthDay, 2));
		Debug.d(TAG, "--->Date: " + mContent);
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
		System.out.println("file string ["+str+"]");
		return str;
	}
	
	public void getBgBitmap()
	{ 
	}
//////addbylk 
	@Override	 
	public Bitmap getpreviewbmp()
	{		Debug.e(TAG, "===========--->content: " + getContent() );	
		Bitmap bitmap;
		
		mPaint.setTextSize(getfeed());
		mPaint.setAntiAlias(true); //去除锯齿  
		mPaint.setFilterBitmap(true); //对位图进行滤波处理
	
		boolean isCorrect = false;
		// Debug.d(TAG,"--->getBitmap font = " + mFont);
		for (String font : mFonts) {
			if (font.equals(mFont)) {
				isCorrect = true;
				break;
			}
		}
		if (!isCorrect) {
			mFont = DEFAULT_FONT;
		}
		try {
			mPaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/"+mFont+".ttf"));
		} catch (Exception e) {}
		
		int width = (int)mPaint.measureText(getContent());//addbylk 测量尺寸 
		Debug.d(TAG, "--->content: " + getContent() + "  width=" + width);
		if (mWidth == 0) {
			setWidth(width);
		}
		bitmap = Bitmap.createBitmap(width , (int)mHeight, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"--->getBitmap width="+mWidth+", mHeight="+mHeight);
		mCan = new Canvas(bitmap);
		FontMetrics fm = mPaint.getFontMetrics();
		mPaint.setColor(Color.BLUE);//设置 变量 在 位图 里 为 蓝色 
	
		
		 
		String str_new_content="";
		mContent =	mContent.replace('0', 'D');			
		mContent =	mContent.replace('1', 'D');	
		mContent =	mContent.replace('2', 'D');	
		mContent =	mContent.replace('3', 'D');	
		mContent =	mContent.replace('4', 'D');	
		mContent =	mContent.replace('5', 'D');	
		mContent =	mContent.replace('6', 'D');	
		mContent =	mContent.replace('7', 'D');	
		mContent =	mContent.replace('8', 'D');	
		mContent =	mContent.replace('9', 'D');	
		Debug.e(TAG, "--->content: " + getContent() + "  width=" + width);			
		mCan.drawText(mContent , 0, mHeight-fm.descent, mPaint);
	
		return Bitmap.createScaledBitmap(bitmap, (int)mWidth, (int)mHeight, false);	
	}	
	
}
