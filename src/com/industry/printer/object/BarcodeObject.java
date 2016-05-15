package com.industry.printer.object;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BarcodeObject extends BaseObject {

	public String mFormat;
	public boolean mShow;
	
	public BarcodeObject(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_BARCODE, x);
		// TODO Auto-generated constructor stub
		mShow = true;
		mFormat="ENA128";
		setContent("1234567890");
		mWidth=100;
	}
	
	public void setCode(String code)
	{
		mFormat = code;
		isNeedRedraw = true;
	}
	
	public String getCode()
	{
		return mFormat;
	}
	
	public void setShow(boolean show)
	{
		mShow = show;
	}
	public boolean getShow()
	{
		return mShow;
	}
	
	@Override
	public void setContent(String content)
	{
		mContent=content;
		isNeedRedraw = true;
	}
	
	private static final String CODE = "utf-8"; 
	
	public Bitmap getScaledBitmap(Context context)
	{
		if (!isNeedRedraw) {
			return mBitmap;
		}
		isNeedRedraw = false;
		BitMatrix matrix=null;
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			if(mFormat.equals("ENA128"))
			{
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
	            hints.put(EncodeHintType.CHARACTER_SET, CODE);
	            /* 条形码的宽度设置为高度的3倍  */
				matrix = writer.encode(mContent,
					                BarcodeFormat.CODE_128, (int)mHeight * 3, (int)(mHeight - 30), null);
			}
			else if(mFormat.equals("QR"))
			{
				matrix = writer.encode(mContent,
		                BarcodeFormat.QR_CODE, (int)mHeight, (int)mHeight);
			}
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			Debug.d(TAG, "mWidth="+mWidth+", width="+width + "   height=" + height);
			
			mWidth = width;
			mXcor_end = mXcor + mWidth;
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) 
			{
				for (int x = 0; x < width; x++) 
				{
					if (matrix.get(x, y)) 
					{
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			/* 条码/二维码的四个边缘空出20像素作为白边 */
			mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			
			mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			
			/*if content need to show, draw it*/
			if(mShow && !mFormat.equals("QR"))
			{
				Bitmap bmp = Bitmap.createBitmap(width, height+30, Config.ARGB_8888);
				Bitmap code = creatCodeBitmap(mContent, width, 30);
				Canvas can = new Canvas(bmp);
				can.drawBitmap(mBitmap, 0, 0, mPaint);
				can.drawBitmap(code, 0, height, mPaint);
				BinFromBitmap.recyleBitmap(mBitmap);
				BinFromBitmap.recyleBitmap(code);
				mBitmap = bmp;
			}
			return mBitmap;

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Bitmap creatCodeBitmap(String contents,int width,int height) {
		TextView tv=new TextView(mContext);
	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setTextSize(25);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);  
        tv.setTextColor(Color.BLACK);
        tv.measure(  
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),  
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));  
        tv.layout(0, 0, tv.getMeasuredWidth(),  
        		tv.getMeasuredHeight());
  
        tv.buildDrawingCache();  
        Bitmap bitmapCode=tv.getDrawingCache();
//        BinCreater.saveBitmap(bitmapCode, "barcode.png");
        return bitmapCode;
	}
	
	public int getBestWidth()
	{
		int width=0;
		BitMatrix matrix=null;
		try{
			MultiFormatWriter writer = new MultiFormatWriter();
			if(mFormat.equals("ENA128"))
			{
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
	            hints.put(EncodeHintType.CHARACTER_SET, CODE);
	            
				matrix = writer.encode(mContent,
					                BarcodeFormat.CODE_128, (int)mWidth, (int)mHeight, null);
			}
			else if(mFormat.equals("QR"))
			{
				matrix = writer.encode(mContent,
		                BarcodeFormat.QR_CODE, (int)mWidth, (int)mHeight);
			}
			width = matrix.getWidth();
			int height = matrix.getHeight();
			Debug.d(TAG, "mWidth="+mWidth+", width="+width);
		}
		catch(Exception e)
		{
			Debug.d(TAG, "exception:"+e.getMessage());
		}
		return width;
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
		str += BaseObject.floatToFormatString(mContent.length(), 3)+"^";
		str += getCode()+"^";
		str += "000^";
		str += BaseObject.boolToFormatString(mShow, 3)+"^";
		str += mContent+"^";
		str += "00000000^00000000^00000000^00000000^0000^0000^0000^000^000";
		System.out.println("file string ["+str+"]");
		return str;
	}
}
