package com.industry.printer;

import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.MessageObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class EditScrollView extends View {
	
	public static final String TAG="EditScrollView";
	
	public Paint p;
	public HorizontalScrollView mParent;
	public Context mContext;
	
	public EditScrollView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
		p = new Paint();
		mParent= (HorizontalScrollView) this.getParent();
		
		Debug.d(TAG, "==>EditScrollView 1");
	}
	
	public EditScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		//mParent= (HorizontalScrollView) this.getParent().getParent();
		p = new Paint();
		Debug.d(TAG, "==>EditScrollView 2");
	}
	public EditScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs,defStyle);
		//mParent= (HorizontalScrollView) this.getParent().getParent();
		p = new Paint();
		Debug.d(TAG, "==>EditScrollView 3");
	}
	@Override  
	 protected void onDraw(Canvas canvas) {
		 Debug.d(TAG, "====>onDraw");
		
		 for(BaseObject obj : EditTabActivity.mObjs)
		 {
			 if(obj instanceof MessageObject)
				 continue;
			 if(mContext == null)
					Debug.d(TAG, "$$$$$$$$$$context=null");
			 Bitmap bitmap = obj.getScaledBitmap(mContext);
			 canvas.drawBitmap(bitmap, obj.getX(), obj.getY(), p);
			 BinFromBitmap.recyleBitmap(bitmap);
		 }
		 Debug.d(TAG, "<<<==onDraw");
		 //mParent.fling(100);
	 }  

}
