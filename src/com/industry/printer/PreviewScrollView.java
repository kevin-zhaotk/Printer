package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;
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
	public Paint p;
	
	public static Bitmap	mPreBitmap;
	public Vector<TlkObject> mList;
	
	public PreviewScrollView(Context context) {
		super(context);
		p = new Paint();
		// TODO Auto-generated constructor stub
	}
	
	public PreviewScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		// TODO Auto-generated constructor stub
	}
	
	public PreviewScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		p = new Paint();
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
		 p.setAlpha(0);
		 
		 p.setAlpha(128);
		 Canvas can = new Canvas(mPreBitmap);
		 can.drawBitmap(bm, x, y, p);
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
		
		//TlkObject[] v = (TlkObject[])mList.values().toArray();
		
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
				Debug.d(TAG, "=========text object");
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
				bit = new int[32*o.mContent.length()];
				font.getDotbuf(o.mContent, bit);
				mPreBitmap=getTextBitmapFrombuffer(bit);
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				Debug.d(TAG, "=========pic object");
				DotMatrixFont font = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+o.font+".txt");
				bit = new int[128*8];
				font.getDotbuf(bit);
				mPreBitmap=getPicBitmapFrombuffer(bit);
			}
			
			//canvas.drawBitmap(Bitmap.createScaledBitmap(mPreBitmap, mPreBitmap.getWidth()*3, 50, false), o.x, o.y, p);
			canvas.drawBitmap(Bitmap.createScaledBitmap(mPreBitmap, mPreBitmap.getWidth()*4, mPreBitmap.getHeight()*4, false), o.x, o.y, p);
		}
		 
	 }  

	public static Bitmap getTextBitmapFrombuffer(int[] bit)
	{
		Bitmap bmp = Bitmap.createBitmap(bit.length/2, 16, Config.ARGB_8888);
		Debug.d(TAG, "***********bmp w="+bmp.getWidth()+", h="+bmp.getHeight());
		Canvas c = new Canvas(bmp);
		for(int i=0; i<bit.length; i++)
		{
			if(i%32>=0 && i%32 <8)	//P1
			{
				//Debug.d(TAG, "P1 i="+i);
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*(i/32), j, p);
					//Debug.d(TAG, "x="+(i-16*(i/32))+", y="+j);
				}
			}
			else if(i%32>=8 && i%32 <16)	//P2
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
		}
		return bmp;
	}
	
	public static Bitmap getPicBitmapFrombuffer(int[] bit)
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
					Debug.d(TAG, "i="+i+", j="+j+", x="+i%64+" ,y="+(32+j+(i/64-8)*8));
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
					Debug.d(TAG, "i="+i+", j="+j+", x="+(i%64+64)+" ,y="+(32+j+(i/64-12)*8));
				}
			}
		}
		return bmp;
	}
}
