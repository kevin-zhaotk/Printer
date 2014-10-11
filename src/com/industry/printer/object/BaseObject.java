package com.industry.printer.object;

import com.industry.printer.Utils.Debug;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
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
		setHeight(50);		
		setLineWidth(5);
		setContent("text");
		
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
		mPaint.setTextSize(150);
	}
	public Bitmap getScaledBitmap(Context context)
	{
		System.out.println("getScaledBitmap  mWidth="+mWidth+", mHeight="+mHeight);
		return Bitmap.createScaledBitmap(getBitmap(context), (int)mWidth, (int)mHeight, true);
	}
	
	protected Bitmap getBitmap(Context context)
	{
		//mPaint.setColor(Color.RED);
		//System.out.println("getBitmap mContent="+mContent);
		mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/"+mFont+".ttf"));
		int width = (int)mPaint.measureText(getContent());
		int height = (int)mPaint.getTextSize();
		
		Bitmap bmp = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
		System.out.println("getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		mCan.drawText(mContent, 0, height-30, mPaint);
		//can.drawText("text", 0, 4, p);
		//mCan.save();
		return bmp;
		//Debug.d(TAG, "mCan width="+mCan.getWidth());
	}
	
	public void drawVarBitmap(String f)
	{
		//mPaint.setTextSize(mHeight);
		int height = (int)mPaint.getTextSize();
		int width = (int)mPaint.measureText("8");
		/*draw Bitmap of single digit*/
		Bitmap bmp = Bitmap.createBitmap(width, 150, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		/*draw 0-9 totally 10 digits Bitmap*/
		Bitmap gBmp = Bitmap.createBitmap((int)mWidth/mContent.length()*10, 150, Bitmap.Config.ARGB_8888);
		Canvas gCan = new Canvas(gBmp);
		gCan.drawColor(Color.WHITE);	/*white background*/
		for(int i =0; i<=9; i++)
		{
			/*draw background to white firstly*/
			can.drawColor(Color.WHITE);
			can.drawText(String.valueOf(i), 0, height-30, mPaint);
			Bitmap b = bmp.createScaledBitmap(bmp, (int)mWidth/mContent.length(), (int)mHeight, true);
			gCan.drawBitmap(b, i*b.getWidth(), (int)getY(), mPaint);
		}
		Debug.d(TAG, "save var png");
		//BinCreater.saveBitmap(gBmp, "var"+getIndex()+".png");
		
		BinCreater.create(gBmp, (int)mWidth/mContent.length());
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
		mPaint.setTextSize(150);
		//Bitmap bmp = Bitmap.createScaledBitmap(getBitmap(), (int)mWidth, (int)mHeight, true);
		mXcor_end = mXcor + mWidth;
		System.out.println("content="+mContent+", mXcor = "+mXcor+", mWidth ="+mWidth + ",mHeight="+mHeight);
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
		System.out.println("floatToFormatString str ="+str);
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
		System.out.println("intToFormatString str ="+str);
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
		System.out.println("boolToFormatString str ="+str);
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
	
}
