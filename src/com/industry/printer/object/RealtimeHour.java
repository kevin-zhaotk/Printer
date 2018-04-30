package com.industry.printer.object;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.cache.FontCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.text.format.Time;

public class RealtimeHour extends BaseObject {

	public RealtimeHour(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_RT_HOUR, x);
		Time t = new Time();
		t.set(System.currentTimeMillis());
		setContent(BaseObject.intToFormatString(t.hour, 2));
	}

	@Override
	public String getContent()
	{
		Time t = new Time();
		t.set(System.currentTimeMillis());
		setContent(BaseObject.intToFormatString(t.hour, 2));
		return mContent;
	}
	
////addby kevin
	@Override	 
	public Bitmap getpreviewbmp()
	{		
		Debug.e(TAG, "===========--->content: " + getContent() );	
		Bitmap bitmap;
		
		mPaint.setTextSize(getfeed());
		mPaint.setAntiAlias(true); //  
		mPaint.setFilterBitmap(true); //
	
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
			mPaint.setTypeface(FontCache.getFromExternal(mFont + ".ttf"));
		} catch (Exception e) {}
		
		int width = (int)mPaint.measureText(getContent());//addbylk �����ߴ� 
		Debug.d(TAG, "--->content: " + getContent() + "  width=" + width);
		if (mWidth == 0) {
			setWidth(width);
		}
		bitmap = Bitmap.createBitmap(width , (int)mHeight, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"--->getBitmap width="+mWidth+", mHeight="+mHeight);
		mCan = new Canvas(bitmap);
		FontMetrics fm = mPaint.getFontMetrics();
		mPaint.setColor(Color.BLUE);//���� ���� �� λͼ �� Ϊ ��ɫ 
	
		
		 
		String str_new_content="";
		str_new_content =	mContent;	
		
		str_new_content =	str_new_content.replace('0', 'H');		
		str_new_content =	str_new_content.replace('1', 'H');	
		str_new_content =	str_new_content.replace('2', 'H');	
		str_new_content =	str_new_content.replace('3', 'H');	
		str_new_content =	str_new_content.replace('4', 'H');	
		str_new_content =	str_new_content.replace('5', 'H');	
		str_new_content =	str_new_content.replace('6', 'H');	
		str_new_content =	str_new_content.replace('7', 'H');	
		str_new_content =	str_new_content.replace('8', 'H');	
		str_new_content =	str_new_content.replace('9', 'H');	
		Debug.e(TAG, "--->content: " + getContent() + "  width=" + width);			
		mCan.drawText(str_new_content , 0, mHeight-fm.descent, mPaint);
	
		return Bitmap.createScaledBitmap(bitmap, (int)mWidth, (int)mHeight, false);	
	}	
	
	public String toString()
	{
		float prop = getProportion();
		StringBuilder builder = new StringBuilder(mId);
		
		builder.append("^")
				.append(BaseObject.floatToFormatString(getX() * prop, 5))
				.append("^")
				.append(BaseObject.floatToFormatString(getY()*2 * prop, 5))
				.append("^")
				.append(BaseObject.floatToFormatString(getXEnd() * prop, 5))
				.append("^")
				.append(BaseObject.floatToFormatString(getYEnd()*2 * prop, 5))
				.append("^")
				.append(BaseObject.intToFormatString(0, 1))
				.append("^")
				.append(BaseObject.boolToFormatString(mDragable, 3))
				.append("^")
				.append("000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^")
				.append(mFont)
				.append("^000^000");
		String str = builder.toString();
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
//		str += mId+"^";
//		str += BaseObject.floatToFormatString(getX() * prop, 5)+"^";
//		str += BaseObject.floatToFormatString(getY()*2 * prop, 5)+"^";
//		str += BaseObject.floatToFormatString(getXEnd() * prop, 5)+"^";
//		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
//		str += BaseObject.floatToFormatString(getYEnd()*2 * prop, 5)+"^";
//		str += BaseObject.intToFormatString(0, 1)+"^";
//		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
//		//str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
//		str += "000^000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^" + mFont + "^000^000";
		System.out.println("file string ["+str+"]");
		return str;
	}

}
