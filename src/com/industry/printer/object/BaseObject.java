package com.industry.printer.object;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.MainActivity;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

public class BaseObject{
	public static final String TAG="BaseObject";
	
	public static final String OBJECT_TYPE_TEXT		="001";
	public static final String OBJECT_TYPE_CNT			="002";
	public static final String OBJECT_TYPE_RT_YEAR	="003";
	public static final String OBJECT_TYPE_RT_MON	="004";
	public static final String OBJECT_TYPE_RT_DATE	="005";
	public static final String OBJECT_TYPE_RT_HOUR="006";
	public static final String OBJECT_TYPE_RT_MIN	="007";
	public static final String OBJECT_TYPE_YEAR			="008";
	public static final String OBJECT_TYPE_SHIFT		="009";
	public static final String OBJECT_TYPE_DL_YEAR	="013";
	public static final String OBJECT_TYPE_DL_MON	="014";
	public static final String OBJECT_TYPE_DL_DATE	="015";
	public static final String OBJECT_TYPE_JULIAN		="025";
	public static final String OBJECT_TYPE_GRAPHIC	="026";
	public static final String OBJECT_TYPE_BARCODE="027";
	public static final String OBJECT_TYPE_LINE			="028";
	public static final String OBJECT_TYPE_RECT		="029";
	public static final String OBJECT_TYPE_ELLIPSE	="030";
	public static final String OBJECT_TYPE_MsgName	="031";
	public static final String OBJECT_TYPE_RT				="032";
	public static final String OBJECT_TYPE_RT_SECOND="033";
	
	
	public Context mContext;
	
	public String mId;
	public int mIndex;
	public String mTypeName;
	public Paint mPaint;
	public Canvas mCan;
	public String mFont;
	public float mSize;
	public float mWidth;
	public float mHeight;
	public float mXcor;
	public float mYcor;
	public float mXcor_end;
	public float mYcor_end;
	public boolean mDragable;
	public int mLineWidth;
	public boolean mIsSelected;
	public String mContent;
	public HashMap<String, byte[]> mVBuffer;
	
	public BaseObject(Context context, String id, float x)
	{
		this(id);
		setX(x);
		mContext = context;
	}
	
	public BaseObject(String id)
	{
		//super(context);
		mId=id;
		mIndex = 0;
		mXcor=0;
		mYcor=0;
		mXcor_end=0;
		mYcor_end=0;
		mDragable = true;
		mFont = "Arial";
		initPaint();
		setSelected(true);		
		setHeight(Configs.gDots);		
		setLineWidth(5);
		setContent("text");
		
		mVBuffer = new HashMap<String, byte[]>();
		//drawCanvas();
		//mPaint.setColor(Color.BLACK);
	}

	public String getId()
	{
		return mId;
	}
	
	public void initPaint()
	{
		mPaint = new Paint();
		mPaint.setTextSize(Configs.gDots);
	}
	public Bitmap getScaledBitmap(Context context)
	{
		Debug.d(TAG,"getScaledBitmap  mWidth="+mWidth+", mHeight="+mHeight);
		Bitmap bmp = getBitmap(context);
		// Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, (int)mWidth, (int)mHeight, true);
		
		return bmp;
	}
	
	protected Bitmap getBitmap(Context context)
	{
		//mPaint.setColor(Color.RED);
		//Debug.d(TAG,"getBitmap mContent="+mContent);
		mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/"+mFont+".ttf"));
		int width = (int)mPaint.measureText(getContent());
		int height = (int)mPaint.getTextSize();
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDotsTotal, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		mCan.drawText(mContent, 0, height-5, mPaint);
		return bmp;
	}
	
	public void drawVarBitmap(String f)
	{
		//mPaint.setTextSize(mHeight);
		int singleW; //the width value of each char
		int height = (int)mPaint.getTextSize();
		int width = (int)mPaint.measureText("8");
		/*draw Bitmap of single digit*/
		Bitmap bmp = Bitmap.createBitmap(width, Configs.gDotsTotal, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		/*draw 0-9 totally 10 digits Bitmap*/
		singleW = (int)mWidth/mContent.length();
		Bitmap gBmp = Bitmap.createBitmap(singleW*10, Configs.gDotsTotal, Bitmap.Config.ARGB_8888);
		Canvas gCan = new Canvas(gBmp);
		gCan.drawColor(Color.WHITE);	/*white background*/
		for(int i =0; i<=9; i++)
		{
			/*draw background to white firstly*/
			can.drawColor(Color.WHITE);
			can.drawText(String.valueOf(i), 0, height-5, mPaint);
			//Bitmap b = Bitmap.createScaledBitmap(bmp, singleW, (int)mHeight, true);
			gCan.drawBitmap(bmp, i*bmp.getWidth(), (int)getY(), mPaint);
			BinCreater.recyleBitmap(bmp);
		}
		BinCreater.recyleBitmap(bmp);
		BinCreater.create(gBmp, singleW);		
		BinCreater.saveBin(f+"/v"+mIndex+".bin", gBmp.getWidth());
		
		BinCreater.recyleBitmap(gBmp);
	}
	/**
	 * generateVarBuffer - generate the variable bin buffer, Contained in the HashMap
	 */
	public void generateVarBuffer()
	{
		//mPaint.setTextSize(mHeight);
		int singleW; //the width value of each char
		int height = (int)mPaint.getTextSize();
		int width = (int)mPaint.measureText("8");
		/*draw Bitmap of single digit*/
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		/*draw 0-9 totally 10 digits Bitmap*/
		singleW = (int)mWidth/mContent.length();

		/*A full-size empty bitmap, width:singleW; height: Configs.gDots*/
		Bitmap bg = Bitmap.createBitmap(singleW, 300, Config.ARGB_8888); 
		mCan = new Canvas(bg);
		
		for(int i =0; i<=9; i++)
		{
			/*draw background to white firstly*/
			can.drawColor(Color.WHITE);
			mCan.drawColor(Color.WHITE);
			can.drawText(String.valueOf(i), 0, height-30, mPaint);
			Bitmap b = Bitmap.createScaledBitmap(bmp, singleW, (int)mHeight, true);
			mCan.drawBitmap(b, 0, getY(), mPaint);
			Bitmap scaledBg = Bitmap.createScaledBitmap(bg, singleW, Configs.gDots, true);
			BinCreater.create(scaledBg, 0);
//			byte[] buffer = new byte[BinCreater.mBmpBits.length];
			byte[] buffer = Arrays.copyOf(BinCreater.mBmpBits, BinCreater.mBmpBits.length);
			BinCreater.recyleBitmap(b);
			mVBuffer.put(String.valueOf(i), buffer);
		}
		BinCreater.recyleBitmap(bmp);
		BinCreater.recyleBitmap(bg);
	}
	
	public Canvas getCanvas()
	{
		return mCan;
	}
	
	public void setHeight(float size)
	{
		if(size<0)
			size=0;
		mHeight = size;
		mYcor_end = mYcor + mHeight;
	}
	
	public float getHeight()
	{
		return mHeight;
	}
	
	public void setWidth(float size)
	{
		if(size<0)
			size=0;
		mWidth = size;
		mXcor_end = mXcor + mWidth; 
	}
	
	public float getWidth()
	{
		return mWidth;
	}
	
	public void setX(float x)
	{
		mXcor=x;
		//float width = mPaint.measureText(mContent);
		mXcor_end = mXcor + mWidth; 
	}
	
	public float getX()
	{
		return mXcor;
	}
	
	public float getXEnd()
	{
		return mXcor_end;
	}
	
	public void setY(float y)
	{
		mYcor = y;
		mYcor_end = mYcor + mHeight;
	}
	
	public float getY()
	{
		return mYcor;
	}
	
	public float getYEnd()
	{
		return mYcor_end;
	}
	
	
	public void setSelected(boolean s)
	{
		mIsSelected = s;
		if(mIsSelected)
			mPaint.setColor(Color.RED);
		else
			mPaint.setColor(Color.BLACK);
	}
	
	public boolean getSelected()
	{
		return mIsSelected;
	}
	
	public void setContent(String content)
	{
		if(mContent!=null && mContent.equals(content))
			return;
		mContent = content;
		mPaint.setTextSize(mHeight);
		mWidth = mPaint.measureText(mContent);
		mPaint.setTextSize(Configs.gDots);
		//Bitmap bmp = Bitmap.createScaledBitmap(getBitmap(), (int)mWidth, (int)mHeight, true);
		mXcor_end = mXcor + mWidth;
		Debug.d(TAG,"content="+mContent+", mXcor = "+mXcor+", mWidth ="+mWidth + ",mHeight="+mHeight);
		mYcor_end = mYcor + mHeight;
	}
	
	public String getContent()
	{
		return mContent;
	}
	
	public void setDragable(boolean drag)
	{
		mDragable = drag;
	}
	
	public boolean getDragable()
	{
		return mDragable;
	}
	
	public void setFont(String font)
	{
		if(font== null)
			return;
		mFont = font;
	}
	
	public String getFont()
	{
		return mFont;
	}
	public void setLineWidth(int width)
	{
		mLineWidth=width;
		if(mPaint != null)
			mPaint.setStrokeWidth(mLineWidth);
	}
	
	public float getLineWidth()
	{
		return mLineWidth;
	}
	
	public static String floatToFormatString(float f, int n)
	{
		String str = "";
		Float ff = new Float(f);
		//Integer i = Integer.valueOf(ff.intValue());
		int cn = String.valueOf(ff.intValue()).length();
		for(int i=0; i < n-cn && n-cn>=0; i++)
		{
			str=str+"0";
		}
		str += ff.intValue();
		Debug.d(TAG,"floatToFormatString str ="+str);
		return str;
	}
	
	public static String intToFormatString(int f, int n)
	{
		String str = "";
		//Float ff = new Float(f);
		//Integer i = Integer.valueOf(ff.intValue());
		int cn = String.valueOf(f).length();
		for(int i=0; i < n-cn && n-cn>=0; i++)
		{
			str=str+"0";
		}
		str += f;
		Debug.d(TAG,"intToFormatString str ="+str);
		return str;
	}
	
	public static String boolToFormatString(boolean b, int n)
	{
		String str = "";
		for(int i=0; i < n-1; i++)
		{
			str=str+"0";
		}
		if(b==true)
			str += 1;
		else
			str += 0;
		Debug.d(TAG,"boolToFormatString str ="+str);
		return str;
	}
	
	public void setIndex(int index)
	{
		mIndex = index;
	}

	public int getIndex()
	{
		return mIndex;
	}
	
	public byte[] getBufferFromContent()
	{
		int n=0;
		Debug.d(TAG, "===>getBufferFromContent id="+mId+", content="+mContent);
		int lenth=mVBuffer.get("0").length;
		ByteArrayBuffer buffer = new ByteArrayBuffer(mContent.length()*lenth);
		Debug.d(TAG, "--->Arraybuffer len="+buffer.length());
		for(int i=0;i<mContent.length(); i++){
			n = Integer.parseInt(mContent.substring(i, i+1));
			byte[] b=mVBuffer.get(String.valueOf(n));
			buffer.append(b, 0, b.length);
		}
		Debug.d(TAG, "--->Arraybuffer len="+buffer.length());
		return buffer.buffer();
	}
	
}
