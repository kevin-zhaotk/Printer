package com.industry.printer.object;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import com.google.zxing.common.BitMatrix;
import com.industry.printer.MainActivity;
import com.industry.printer.MessageTask;
import com.industry.printer.R;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFileMaker;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.data.DotMatrixReader;
import com.industry.printer.data.InternalCodeCalculater;

import android.R.color;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

public class BaseObject{
	public static final String TAG="BaseObject";
	
	public static final String OBJECT_TYPE_TEXT		="001";
	public static final String OBJECT_TYPE_CNT		="002";
	public static final String OBJECT_TYPE_RT_YEAR	="003";
	public static final String OBJECT_TYPE_RT_MON	="004";
	public static final String OBJECT_TYPE_RT_DATE	="005";
	public static final String OBJECT_TYPE_RT_HOUR	="006";
	public static final String OBJECT_TYPE_RT_MIN	="007";
	public static final String OBJECT_TYPE_YEAR		="008";
	public static final String OBJECT_TYPE_SHIFT	="009";
	public static final String OBJECT_TYPE_DL_YEAR	="013";
	public static final String OBJECT_TYPE_DL_MON	="014";
	public static final String OBJECT_TYPE_DL_DATE	="015";
	public static final String OBJECT_TYPE_JULIAN	="025";
	public static final String OBJECT_TYPE_GRAPHIC	="026";
	public static final String OBJECT_TYPE_BARCODE	="027";
	public static final String OBJECT_TYPE_LINE		="028";
	public static final String OBJECT_TYPE_RECT		="029";
	public static final String OBJECT_TYPE_ELLIPSE	="030";
	public static final String OBJECT_TYPE_MsgName	="031";
	public static final String OBJECT_TYPE_RT		="032";
	public static final String OBJECT_TYPE_QR		="033";
	public static final String OBJECT_TYPE_WEEKDAY  ="034";
	public static final String OBJECT_TYPE_WEEKS	="035";
	public static final String OBJECT_TYPE_RT_SECOND="036";
	
	
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
	public String mName;
	/* 
	 * 是否需要重新绘制bitmap 
	 * 需要重新绘制bitmap的几种情况：1、宽高变化；2、字体修改； 3，内容变化
	 */
	protected boolean isNeedRedraw;
	protected Bitmap	mBitmap;
	protected Bitmap	mBitmapSelected;
	
	public HashMap<String, byte[]> mVBuffer;
	public MessageTask mTask;
	
	public BaseObject(Context context, String id, float x)
	{
		this(id);
		setX(x);
		mContext = context;
		initName();
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
		isNeedRedraw = true;
		mFont = "WelldrawH000";
		initPaint();
		setSelected(true);	
		Debug.d(TAG, "--->new baseobject: " + isNeedRedraw);
		setHeight(Configs.gDots);
		setLineWidth(5);
		setContent("text");
		
		mVBuffer = new HashMap<String, byte[]>();
		
	}

	private void initName() {
		Debug.d(TAG, "--->mcontext: " + mContext);
		if(this instanceof MessageObject)
			mName = mContext.getString(R.string.object_msg_name);
		else if(this instanceof TextObject)
			mName = mContext.getString(R.string.object_text);
		else if(this instanceof CounterObject)
			mName = mContext.getString(R.string.object_counter);
		else if(this instanceof BarcodeObject)
			mName = mContext.getString(R.string.object_bar);
		else if(this instanceof GraphicObject)
			mName = mContext.getString(R.string.object_pic);
		else if(this instanceof JulianDayObject)
			mName = mContext.getString(R.string.object_julian);
		else if(this instanceof RealtimeObject)
			mName = mContext.getString(R.string.object_realtime);
		else if(this instanceof LineObject)
			mName = mContext.getString(R.string.object_line);
		else if(this instanceof RectObject)
			mName = mContext.getString(R.string.object_rect);
		else if(this instanceof EllipseObject)
			mName = mContext.getString(R.string.object_ellipse);
		else if(this instanceof ShiftObject)
			mName = mContext.getString(R.string.object_shift);
		else if(this instanceof RTSecondObject)
			mName = mContext.getString(R.string.object_second);
	}
	
	public String getTitle() {
		return mName;
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
		/* 使用
		Debug.d(TAG,"getScaledBitmap  mWidth="+mWidth+", mHeight="+mHeight);
		Bitmap bmp = getBitmap(context);
		if (mWidth <= 0) {
			mWidth = bmp.getWidth();
		}
		Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, (int)mWidth, (int)mHeight, true);
		return scaledBmp;
		*/
		if (isNeedRedraw) {
			// drawSelected();
			drawNormal();
		}
		Debug.d(TAG, "--->redraw?" + isNeedRedraw);
		isNeedRedraw = false;
//		if (mIsSelected) {
//			return mBitmapSelected;
//		}
		return mBitmap;
	}
	
	protected void drawNormal() {
		Debug.d(TAG, "--->drawNormal");
		mPaint.setColor(Color.BLACK);
		mBitmap = draw();
	}
	
	protected void drawSelected() {
		mPaint.setColor(Color.RED);
		Debug.d(TAG, "--->drawSelected");
		mBitmapSelected = draw();
	}
	
	private Bitmap draw() {
		Bitmap bitmap;
		mPaint.setTextSize(mHeight);
		mPaint.setAntiAlias(true); //去除锯齿  
		mPaint.setFilterBitmap(true); //对位图进行滤波处理
		try {
			AssetFileDescriptor fd = mContext.getAssets().openFd("fonts/"+mFont+".ttf");
			if (fd != null) {
				fd.close();
			} else {
				mFont = "WelldrawH000";
			}
		} catch (Exception e) {
			mFont = "WelldrawH000";
		}
		
		mPaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/"+mFont+".ttf"));
		int width = (int)mPaint.measureText(getContent());
		setWidth(width);
		bitmap = Bitmap.createBitmap((int)mWidth , (int)mHeight, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"--->getBitmap width="+mWidth+", mHeight="+mHeight);
		mCan = new Canvas(bitmap);
		FontMetrics fm = mPaint.getFontMetrics();
		Debug.d(TAG, "--->asent: " + fm.ascent + ",  bottom: " + fm.bottom + ", descent: " + fm.descent + ", top: ");
        // float tY = (y - getFontHeight(p))/2+getFontLeading(p); 
		mCan.drawText(mContent, 0, mHeight-fm.descent, mPaint);
		if (mHeight <= 4 * MessageObject.PIXELS_PER_MM) {
			setWidth(width * 1.25f);
		}
		return Bitmap.createScaledBitmap(bitmap, (int)mWidth, (int)mHeight, false);
	}
	protected Bitmap getBitmap(Context context)
	{
		//mPaint.setColor(Color.RED);
		//Debug.d(TAG,"getBitmap mContent="+mContent);
		mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/"+mFont+".ttf"));
		int width = (int)mPaint.measureText(getContent());
		int height = (int)mPaint.getTextSize();
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDots, Bitmap.Config.ARGB_8888);
		Debug.d(TAG,"getBitmap width="+width+", height="+height+ ", mHeight="+mHeight);
		mCan = new Canvas(bmp);
		mCan.drawText(mContent, 0, height-30, mPaint);
		//BinCreater.saveBitmap(bmp, "bg.png");
		return bmp;
	}
	
	public int drawVarBitmap()
	{
		int dots = 0;
		//mPaint.setTextSize(mHeight);
		int singleW; //the width value of each char
		int height = (int)mPaint.getTextSize();
		int width = (int)mPaint.measureText("8");
		FontMetrics fm = mPaint.getFontMetrics();
		float wDiv = (float) (2.0/mTask.getHeads());
		/*draw Bitmap of single digit*/
		Bitmap bmp = Bitmap.createBitmap(width, (int)mHeight, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		Debug.d(TAG, "--->Width=" + mWidth);
		/*draw 0-9 totally 10 digits Bitmap*/
		singleW = (int)mWidth/mContent.length();
		Debug.d(TAG, "--->singleW=" + singleW);
		singleW = (int) (singleW/wDiv);
		Debug.d(TAG, "--->singleW/div=" + singleW);
		Bitmap gBmp = Bitmap.createBitmap(singleW*10, Configs.gDots * mTask.getHeads(), Bitmap.Config.ARGB_8888);
		Canvas gCan = new Canvas(gBmp);
		gCan.drawColor(Color.WHITE);	/*white background*/
		for(int i =0; i<=9; i++)
		{
			/*draw background to white firstly*/
			can.drawColor(Color.WHITE);
			can.drawText(String.valueOf(i), 0, mHeight-fm.descent, mPaint);
			// Bitmap b = Bitmap.createScaledBitmap(bmp, singleW, (int)mHeight, true);
			gCan.drawBitmap(Bitmap.createScaledBitmap(bmp, singleW, (int) (mHeight * mTask.getHeads()), false), i*singleW, (int)getY() * mTask.getHeads(), mPaint);
		}
		BinFromBitmap.recyleBitmap(bmp);
		BinFileMaker maker = new BinFileMaker(mContext);
		dots = maker.extract(gBmp);
		Debug.d(TAG, "--->id: " + mId + " index:  " + mIndex);
		maker.save(ConfigPath.getVBinAbsolute(mTask.getName(), mIndex));
		//
		BinFromBitmap.recyleBitmap(gBmp);
		/*根據變量內容的實際長度計算點數*/
		dots = (dots* getContent().length()/10) + 1;
		return dots;
	}
	/**
	 * generateVarBuffer - generate the variable bin buffer, Contained in the HashMap
	 * 设计： 
	 */
	public void generateVarBuffer()
	{
		//mPaint.setTextSize(mHeight);
		int singleW; //the width value of each char
		int height = (int)mPaint.getTextSize();
		int width = (int)mPaint.measureText("8");
		/*draw Bitmap of single digit*/
		Bitmap bmp = Bitmap.createBitmap(width, Configs.gDots, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		/*draw 0-9 totally 10 digits Bitmap*/
		singleW = (int)mWidth/mContent.length();

		/*A full-size empty bitmap, width:singleW; height: Configs.gDots*/
		Bitmap bg = Bitmap.createBitmap(singleW, Configs.gDots, Config.ARGB_8888); 
		mCan = new Canvas(bg);
		
		for(int i =0; i<=9; i++)
		{
			/*draw background to white firstly*/
			can.drawColor(Color.WHITE);
			mCan.drawColor(Color.WHITE);
			can.drawText(String.valueOf(i), 0, height-30, mPaint);
			Bitmap b = Bitmap.createScaledBitmap(bmp, singleW, (int)mHeight, true);
			mCan.drawBitmap(b, 0, getY(), mPaint);
			// Bitmap scaledBg = Bitmap.createScaledBitmap(bg, singleW, Configs.gDots, true);
			BinFileMaker maker = new BinFileMaker(mContext);
			maker.extract(bmp);
			//byte[] buffer = new byte[BinCreater.mBmpBits.length];
			byte[] buffer = Arrays.copyOf(maker.getBuffer(), maker.getBuffer().length);
			BinFromBitmap.recyleBitmap(b);
			mVBuffer.put(String.valueOf(i), buffer);
		}
		BinFromBitmap.recyleBitmap(bmp);
		BinFromBitmap.recyleBitmap(bg);
	}
	
	public void generateVarbinFromMatrix(String f) {
		BinFileMaker maker = new BinFileMaker(mContext);
		maker.extract("0123456789");
		maker.save(f + getVarBinFileName());
	}
	
	
	public Canvas getCanvas()
	{
		return mCan;
	}
	
	public void setHeight(float size)
	{
		if (this instanceof BarcodeObject && size < 4.0*MessageObject.PIXELS_PER_MM) {
			size = 4.0f * MessageObject.PIXELS_PER_MM;
		} else if (size < MessageObject.mBaseList[0] * MessageObject.PIXELS_PER_MM) {
			size = MessageObject.mBaseList[0] * MessageObject.PIXELS_PER_MM;
		} else if (size > 152) {
			size = 152;
		}
		
		mHeight = size;
		Debug.d(TAG, "--->height=" + mHeight);
		mYcor_end = mYcor + mHeight;
		isNeedRedraw = true;
	}
	
	public void resizeByHeight() {
		mPaint.setTextSize(mHeight);
		String s = getContent();
		if (s == null) {
			return;
		}
		float width = mPaint.measureText(s);
		if (getHeight() <= 4 * MessageObject.PIXELS_PER_MM) {
			width = width * 1.25f;
		}
		setWidth(width);
	}
	
	public void setHeight(String size)
	{
		float height = mTask.getMsgObject().getPixels(size);
		setHeight(height);
	}
	
	public String getDisplayHeight() {
		return mTask.getMsgObject().getDisplayFs(mHeight);
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
		isNeedRedraw = true;
		Debug.d(TAG, "===>x= " + mXcor + ", mWidth=" + mWidth + ", xend=" + mXcor_end);
	}
	
	public float getWidth()
	{
		return mWidth;
	}
	
	public void setX(float x)
	{
		mXcor=x;
		mXcor_end = mXcor + mWidth; 
		Debug.d(TAG, "===>x= " + mXcor + ", mWidth=" + mWidth + ", xend=" + mXcor_end);
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
		// mPaint.setTextSize(Configs.gDots);
		//Bitmap bmp = Bitmap.createScaledBitmap(getBitmap(), (int)mWidth, (int)mHeight, true);
		mXcor_end = mXcor + mWidth;
		Debug.d(TAG,"content="+mContent+", mXcor = "+mXcor+", mWidth ="+mWidth + ",mHeight="+mHeight);
		mYcor_end = mYcor + mHeight;
		isNeedRedraw = true;
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
		isNeedRedraw = true;
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
		isNeedRedraw = true;
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
	
	protected String getBinFileName() {
		return "/1.bin";
	}
	
	protected String getVarBinFileName() {
		return "/v" + mIndex + ".bin";
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
	
	/**
	 * 设置当前object所在的Task
	 * @param task
	 */
	public void setTask(MessageTask task) {
		mTask = task;
	}
	
	/**
	 * 当前object所在的Task
	 * @return MessageTask
	 */
	public MessageTask getTask() {
		return mTask;
	}
	
	public boolean isNeedDraw() {
        return isNeedRedraw;
	}
}
