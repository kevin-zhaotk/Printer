package com.industry.printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.TlkObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PreviewScrollView extends View {

	public static final String TAG="PreviewScrollView";
	public Paint mPaint;
	
	public static Bitmap	mPreBitmap;
	public Vector<TlkObject> mList;
	
	public PreviewScrollView(Context context) {
		super(context);
		mPaint = new Paint();
		// TODO Auto-generated constructor stub
	}
	
	public PreviewScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		// TODO Auto-generated constructor stub
	}
	
	public PreviewScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
		// TODO Auto-generated constructor stub
	}
	
	
	public void createBitmap(int w, int h)
	{
		mPreBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	}
	
	public void createBitmap(int[]src, int w, int h)
	{
		mPreBitmap = Bitmap.createBitmap(src, w, h, Config.ARGB_8888);
		//mPreBitmap = Bitmap.createScaledBitmap(mPreBitmap, w, 150, true);
		
	}
	
	public void setObjectList(Vector<TlkObject> list)
	{
		mList = list;
		Debug.d(TAG, "mList size="+mList.size());
	}
	
	
	public void drawBitmap(int x, int y, Bitmap bm)
	{
		 if(mPreBitmap == null)
		 {
			 return;
		 }
		 Bitmap bmp = Bitmap.createBitmap(bm);
		 bmp.eraseColor(Color.WHITE);
		 mPaint.setAlpha(0);
		 
		 mPaint.setAlpha(128);
		 Canvas can = new Canvas(mPreBitmap);
		 can.drawBitmap(bm, x, y, mPaint);
	}

	
	@Override  
	 protected void onDraw(Canvas canvas) {
		Debug.d(TAG, "====>onDraw");
		 //if(mPreBitmap == null)
			 //return;
		Paint p = new Paint();
		int[] bit = null;
		//int bit[]=new int[3*32];
		//DotMatrixFont font = new DotMatrixFont("/mnt/usb/"++".txt");
		int edage=0;
		//TlkObject[] v = (TlkObject[])mList.values().toArray();
		p.setARGB(255, 0, 0, 0);
		if(mList==null)
			return;
		for(int i=0; i<mList.size(); i++)
		{
			TlkObject o = mList.get(i);
			Debug.d(TAG, "&&&&&&&&index="+o.index+", x="+o.x+", y="+o.y+", font="+o.font+",content="+o.mContent);
			if(o.isTextObject() && o.mContent == null)
				continue;
			//DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
			//Debug.d(TAG, "bit lenght="+bit.length);
			if(o.isTextObject())	//each text object take over 16*16/8 * length=32Bytes*length
			{
				//Debug.d(TAG, "=========text object content="+o.mContent);
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
				int end=o.x+font.getColumns()*o.mContent.length();
				edage = edage > end? edage : end; 
				bit = new int[font.getColumns()*2*o.mContent.length()];
				//Debug.d(TAG, "=========bit.length="+bit.length);
				font.getDotbuf(o.mContent, bit);
				mPreBitmap=getTextBitmapFrombuffer(bit, mPaint);
				if(bit != null && bit.length != 0)
				BinCreater.saveBitmap(mPreBitmap, "text.png");
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				//Debug.d(TAG, "=========pic object");
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+o.font+".txt");
				bit = new int[128*8];
				font.getDotbuf(bit);
				mPreBitmap=getPicBitmapFrombuffer(bit, mPaint);
				int end = o.x+128; 
				edage = edage > end? edage : end;
				
			}
			
			//canvas.drawBitmap(Bitmap.createScaledBitmap(mPreBitmap, mPreBitmap.getWidth()*3, 50, false), o.x, o.y, p);
			if(mPreBitmap!= null)
			{
				Debug.d(TAG,"#########");
				canvas.drawBitmap(Bitmap.createScaledBitmap(mPreBitmap, mPreBitmap.getWidth(), mPreBitmap.getHeight()*4, false), o.x, o.y*4, p);
			}
		}
		Debug.d(TAG, "^^^^^^^^^^draw line ("+edage+","+0+")");
		canvas.drawLine(edage, 0, edage, 256, p); 
	 }  

	public static Bitmap getTextBitmapFrombuffer(int[] bit, Paint p)
	{
		if(bit==null || bit.length<=0)
		{
			Debug.d(TAG,"##########bit.length="+bit.length);
			return null;
		}
		Bitmap bmp = Bitmap.createBitmap(bit.length/2, 16, Config.ARGB_8888);
		//columns = columns*2;
		Debug.d(TAG, "***********bmp w="+bmp.getWidth()+", h="+bmp.getHeight());
		Canvas c = new Canvas(bmp);
		c.drawColor(Color.WHITE);
		for(int i=0; i<bit.length/2; i++)
		{
			for(int j=0; j<8; j++)
			{
				if((bit[2*i]>>j&0x01)==1) c.drawPoint(i, j, p);
				//Debug.d(TAG, "x="+i+", y="+j);
			}
			for(int j=0; j<8; j++)
			{
				if((bit[2*i+1]>>j&0x01)==1) c.drawPoint(i, 8+j, p);
				//Debug.d(TAG, "x="+i+", y="+(8+j));
			}
			
			/*
			if(i%columns>=0 && i%columns <8)	//P1
			{
				//Debug.d(TAG, "P1 i="+i);
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*(i/32), j, p);
					//Debug.d(TAG, "x="+(i-16*(i/32))+", y="+j);
				}
			}
			else if(i%columns*2>=8 && i%32 <16)	//P2
			{
				//Debug.d(TAG, "P2 i="+i);
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*(i/32), j, p);
					//Debug.d(TAG, "x="+(i-16*(i/32))+", y="+j);
				}
			} 
			else if(i%32>=16 && i%32 < 24)	//P3
			{
				//Debug.d(TAG, "P3 i="+i);
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*(i/32)-16, j+8, p);
					//Debug.d(TAG, "x="+(i-16*(i/32)-16)+", y="+(j+8));
				}
			}
			else if(i%32>=24 && i%32 <32)	//P4
			{
				//Debug.d(TAG, "P4 i="+i);
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*(i/32)-16, j+8, p);
					//Debug.d(TAG, "x="+(i-16*(i/32)-16)+", y="+(j+8));
				}
			} 
			*/
		}
		return bmp;
	}
	
	public static Bitmap getPicBitmapFrombuffer(int[] bit, Paint p)
	{
		Bitmap bmp = Bitmap.createBitmap(128,64, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		for(int i=0; i<bit.length; i++)
		{
			/*****P1*****/
			if(i>=0 && i<= 255)
			{
				/*the 1st 8*8 area*/
				for(int j=0;j<8; j++)
				{
					if((bit[i]>>j&0x01) ==0x01) 
						c.drawPoint(i%64, j+(i/64)*8, p);
					//Debug.d(TAG, "i="+i+", j="+j+", x="+i%32+" ,y="+(j+(i/32)*8));
				}
			}
			
			/*****P2*****/
			else if(i>=256 && i<= 511)
			{
				/*the 1st 8*8 area*/
				for(int j=0;j<8; j++)
				{
					if((bit[i]>>j&0x01) ==0x01) 
						c.drawPoint(i%64+64, j+(i/64-4)*8, p);
					//Debug.d(TAG, "i="+i+", j="+j+", x="+(i%64+64)+" ,y="+(j+(i/64-4)*8));
				}
			}
			/*****P3*****/
			else if(i>=512 && i<= 767)
			{
				/*the 1st 8*8 area*/
				for(int j=0;j<8; j++)
				{
					if((bit[i]>>j&0x01) ==0x01) 
						c.drawPoint(i%64, 32+j+(i/64-8)*8, p);
					//Debug.d(TAG, "i="+i+", j="+j+", x="+i%64+" ,y="+(32+j+(i/64-8)*8));
				}
			}
			/*****P4*****/
			else if(i>=768 && i<= 1023)
			{
				/*the 1st 8*8 area*/
				for(int j=0;j<8; j++)
				{
					if((bit[i]>>j&0x01) ==0x01) 
						c.drawPoint(i%64+64, 32+j+(i/64-12)*8, p);
					//Debug.d(TAG, "i="+i+", j="+j+", x="+(i%64+64)+" ,y="+(32+j+(i/64-12)*8));
				}
			}
		}
		return bmp;
	}
}
