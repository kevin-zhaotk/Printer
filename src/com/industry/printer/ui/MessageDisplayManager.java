package com.industry.printer.ui;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.industry.printer.MessageTask;
import com.industry.printer.R;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.MessageObject;
import com.printer.corelib.Debug;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kevin on 2017/7/17.
 */
public class MessageDisplayManager implements View.OnTouchListener {

    private static final String TAG = MessageDisplayManager.class.getSimpleName();

    private Context mContext;
    private ViewGroup mContainer;
    private MessageTask mTask;
    
    /** 
     * The select shadow, show up when object selected 
     */
    private ImageView mShadow;

    private HashMap<BaseObject, ImageView> mImageMap;

    public MessageDisplayManager(Context ctx, ViewGroup container, MessageTask task) {
        mContext = ctx;
        mContainer = container;
        mTask = task;
        mShadow = new ImageView(mContext);
        
        mImageMap = new HashMap<BaseObject, ImageView>();
        reset();
    }


    public void reset() {
        if (mContainer != null) {
            mContainer.removeAllViews();
        }
        mImageMap.clear();
        mShadow.setImageResource(R.drawable.msg_bg_selected);
        mShadow.setVisibility(View.GONE);
        mContainer.addView(mShadow);
    }

    public void fill(MessageTask task) {
        if (task == null) {
            return;
        }
        mTask = task;
        for (BaseObject object : mTask.getObjects()) {
            add(object);
        }
    }
    
    public MessageTask getTask() {
    	return mTask;
    }

    public void add(BaseObject object) {

        if (object == null) {
            return;
        }
        if (object instanceof MessageObject) {
        	// if there is already a messageobject, do nothing
        	if (mTask.getMsgObject() != null) {
        		return;
        	}
        }
        if (mImageMap.containsKey(object)) {
            return;
        }
        draw(object);
//        mTask.addObject(object);
        setSelect(object);
    }

    public void remove(BaseObject object) {
        if (!mImageMap.containsKey(object)) {
            return;
        }
        ImageView view = mImageMap.get(object);
        mImageMap.remove(object);
        mContainer.removeView(view);
        mTask.removeObject(object);
        mShadow.setVisibility(View.GONE);
    }

    public void removeAll() {
        if (mImageMap != null) {
            mImageMap.clear();
        }
        if (mContainer != null) {
            mContainer.removeAllViews();
            mShadow.setVisibility(View.GONE);
            mContainer.addView(mShadow);
        }
        mTask.removeAll();
    }

    public void update(BaseObject object) {
    	
        if (object instanceof MessageObject) {
            return;
        }
        if (!mImageMap.containsKey(object)) {
            return;
        }
        ImageView view = mImageMap.get(object);
        
//        mImageMap.remove(object);
//        mContainer.removeView(view);
//
//        draw(object);
    	
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = (int)object.getWidth();
        params.height = (int)object.getHeight();
        params.topMargin = (int) object.getY();
        params.leftMargin = (int) object.getX();
        view.setLayoutParams(params);
        showSelectRect(params.leftMargin, params.topMargin, params.width, params.height);
        
    }
    
    public void updateDraw(BaseObject object) {
        if (object instanceof MessageObject) {
            return;
        }
        if (!mImageMap.containsKey(object)) {
            return;
        }
        ImageView view = mImageMap.get(object);
        mImageMap.remove(object);
        mContainer.removeView(view);
        draw(object);
    }

    private void draw(BaseObject object) {
    	
    	Debug.e(TAG, "===1--->draw");
    	if (object instanceof MessageObject) {
            return;
        }
    
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int)object.getX();
        lp.topMargin = (int) object.getY();
        ImageView image = new ImageView(mContext);
        
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageBitmap(object.getScaledBitmap(mContext));
        

//        if (object.getSelected()) {
//        	image.setBackgroundResource(R.drawable.msg_bg_selected);
//        	
//        } else {
//            image.setBackgroundResource(R.drawable.msg_bg_unselected);
//        }
        /** width&height must be reseted after object bitmap drawed success */
    	Debug.e(TAG, "===2--->draw W"+ object.getWidth()  +"H"+object.getHeight());
    	
    	
        lp.width = (int)object.getWidth();
        lp.height = (int)object.getHeight();
        mContainer.addView(image, 0,lp);
        mImageMap.put(object, image);
        image.setTag(object);
        image.setOnTouchListener(this);
         	   	
        showSelectRect(lp.leftMargin, lp.topMargin, lp.width, lp.height);
       
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        BaseObject object = (BaseObject)view.getTag();
        Log.d(TAG, "--->onTouch: " + object.mId);
        if (object.getSelected()) {
            return true;
        }

        setSelect(object);
        return true;
    }

    public void setSelect(int i) {
        ArrayList<BaseObject> objects = mTask.getObjects();
        if (i >= objects.size()) {
			return;
		}
        BaseObject object = objects.get(i);
        setSelect(object);
    }

    public void setSelect(BaseObject object) {

        ArrayList<BaseObject> objects = mTask.getObjects();

        for(BaseObject obj : objects)
        {
            if (obj.getSelected()) {
                ImageView view = mImageMap.get(obj);
                obj.setSelected(false);
            }

        }
        if (object == null) {
            return;
        }
        object.setSelected(true);
        showSelectRect((int)object.getX(), (int)object.getY(), (int)object.getWidth(), (int) object.getHeight());
    }

    private void showSelectRect(int x, int y, int w, int h) {
    	RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mShadow.getLayoutParams();
    	lp.width = w;
    	lp.height = h;
    	lp.leftMargin = x;
    	lp.topMargin = y;
    	mShadow.setVisibility(View.VISIBLE);
    	mShadow.setLayoutParams(lp);
    }
    
}
