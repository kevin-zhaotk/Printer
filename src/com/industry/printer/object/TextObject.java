package com.industry.printer.object;

import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;

public class TextObject extends BaseObject {

	public TextObject(Context context, float x) {
		super( context, BaseObject.OBJECT_TYPE_TEXT, x);
	}

	public String toString()
	{
		float prop = getProportion();
		String str="";
		//str += BaseObject.intToFormatString(mIndex, 3)+"^";
		str += mId+"^";
		str += BaseObject.floatToFormatString(getX()*2 * prop, 5)+"^";
		str += BaseObject.floatToFormatString(getY()*2 * prop, 5)+"^";
		str += BaseObject.floatToFormatString(getXEnd()*2 * prop, 5)+"^";
		//str += BaseObject.floatToFormatString(getY() + (getYEnd()-getY())*2, 5)+"^";
		str += BaseObject.floatToFormatString(getYEnd()*2 * prop, 5)+"^";
		str += BaseObject.intToFormatString(0, 1)+"^";
		str += BaseObject.boolToFormatString(mDragable, 3)+"^";
		str += BaseObject.intToFormatString(mContent.length(), 3)+"^";
		str += "000^000^000^000^00000000^00000000^00000000^00000000^0000^0000^" + mFont + "^000^"+mContent;
		System.out.println("file string ["+str+"]");
		return str;
	}
//////addbylk 
	@Override	 
	public Bitmap getpreviewbmp()
	{	Debug.e(TAG, "1===== " + getContent() );
		Bitmap bitmap;
		mPaint.setTextSize(getfeed());
		mPaint.setAntiAlias(true);   
		mPaint.setFilterBitmap(true); 
	
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
		
		int width = (int)mPaint.measureText(getContent());

		Debug.d(TAG, "--->content: " + getContent() + "  width=" + width);
		if (mWidth == 0) {
			setWidth(width);
		}
		Debug.d(TAG, "2===== " + getContent() );
		bitmap = Bitmap.createBitmap(width , (int)mHeight, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"--->getBitmap width="+mWidth+", mHeight="+mHeight);
		mCan = new Canvas(bitmap);
		FontMetrics fm = mPaint.getFontMetrics();
		Debug.d(TAG, "3===== " + getContent() );
		
				
		mCan.drawText(mContent , 0, mHeight-fm.descent, mPaint);
	
		return bitmap;//Bitmap.createScaledBitmap(bitmap, (int)mWidth, (int)mHeight, false);	
	}	
}
