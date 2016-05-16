package com.industry.printer;

import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.MessageObject;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class EditScrollView extends View {
	
	public static final String TAG="EditScrollView";
	
	private int mScreenW;
	private int mScreenH;
	public Paint p;
	public HorizontalScrollView mParent;
	public Context mContext;
	public MessageTask mTask;
	
	public EditScrollView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
		p = new Paint();
		mParent= (HorizontalScrollView) this.getParent();
		getWindowPixels();
		Debug.d(TAG, "==>EditScrollView 1");
	}
	
	public EditScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		//mParent= (HorizontalScrollView) this.getParent().getParent();
		p = new Paint();
		getWindowPixels();
		Debug.d(TAG, "==>EditScrollView 2");
	}
	public EditScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs,defStyle);
		//mParent= (HorizontalScrollView) this.getParent().getParent();
		p = new Paint();
		getWindowPixels();
		Debug.d(TAG, "==>EditScrollView 3");
	}
	@Override  
	protected void onDraw(Canvas canvas) {
		Debug.d(TAG, "====>onDraw");
		int scrollx = 0;
		if (mParent != null) {
			scrollx = mParent.getScrollX();
		}
		Debug.d(TAG, "--->scrollx: " + scrollx + ",  mScreenW: " + mScreenW);
		for(BaseObject obj : mTask.getObjects())
		{
			Debug.d(TAG, "index=" + obj.getIndex() + "  c: " + obj.getContent());
			/* 只有当cursor选中时才显示十字标线  */
			if(obj instanceof MessageObject) {
				if (!obj.getSelected()) {
					continue;
				}
				
				float[] points = {
					/* 画水平线 */
					scrollx, obj.getY(), scrollx + mScreenW, obj.getY(),
					/* 画垂直线 */
					obj.getX(), 0, obj.getX(), mScreenH
				};
				p.setColor(Color.BLACK);
				p.setStrokeWidth(2);
				canvas.drawLines(points, p);
				continue;
			}
			/* 不在显示区域内的对象可以不画，优化效率  */
//			if ((obj.getXEnd() < getScrollX()) || (obj.getX() > getScrollX() + mScreenW)) {
//				Debug.d(TAG, "index=" + obj.getIndex() + "  c: " + obj.getContent() + "  x=" + obj.getX() + " end=" + obj.getXEnd());
//				Debug.d(TAG, "scrollx=" + getScrollX());
//				continue;
//			}
			if(mContext == null)
				Debug.d(TAG, "$$$$$$$$$$context=null");
			
			Bitmap bitmap = obj.getScaledBitmap(mContext);
			if (bitmap == null) {
				Debug.d(TAG, "--->obj: " + obj.getContent());
				continue;
			}
			canvas.drawBitmap(bitmap, obj.getX(), obj.getY(), p);
			 
		}
		canvas.drawLine(scrollx, 153, scrollx + mScreenW, 153, p);
		Debug.d(TAG, "<<<==onDraw");
		 //mParent.fling(100);
	} 
	
	public void setParent(View view) {
		mParent = (HorizontalScrollView) view;
	}
	
	public void setTask(MessageTask task) {
		mTask = task;
	}

	public void getWindowPixels() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float density = metrics.density;
		float w = metrics.widthPixels;
		float h = metrics.heightPixels;
		
		mScreenW = (int) (w * density + 0.5f);
		mScreenH = (int) (h * density + 0.5f);
	}
}
