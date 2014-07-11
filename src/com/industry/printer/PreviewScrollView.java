package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;

import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.MessageObject;

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
		int[] bit = {0xff, 0x00, 0xff, 0x00,0xff, 0x00, 0xff, 0x00,
				0xff, 0x00, 0xff, 0x00,0xff, 0x00, 0xff, 0x00,
				0xff, 0x00, 0xff, 0x00,0xff, 0x00, 0xff, 0x00,
				0xff, 0x00, 0xff, 0x00,0xff, 0x00, 0xff, 0x00};
		 //p.setAlpha(128);
		mPreBitmap = Bitmap.createBitmap(bit.length/2, 16, Config.ARGB_8888);
		Canvas c = new Canvas(mPreBitmap);
		for(int i=0; i<bit.length; i++)
		{
			if(i%8%4==0)	//P1
			{
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*i/32, j, p);
				}
			}
			else if(i%8%4==1)	//P2
			{
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*i/32, j, p);
				}
			} 
			else if(i%8%4==2)	//P3
			{
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*i/32-16, j+8, p);
				}
			}
			else if(i%8%4==3)	//P4
			{
				for(int j=0; j<8; j++)
				{
					if((bit[i]>>j&0x01)==1) c.drawPoint(i-16*i/32-16, j+8, p);
				}
			} 
		}
		canvas.drawBitmap(mPreBitmap, 0, 0, p);
		 
	 }  

}
