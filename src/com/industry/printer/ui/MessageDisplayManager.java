package com.industry.printer.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.industry.printer.MessageTask;
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

    private HashMap<BaseObject, ImageView> mImageMap;

    public MessageDisplayManager(Context ctx, ViewGroup container, MessageTask task) {
        mContext = ctx;
        mContainer = container;
        mTask = task;
        mImageMap = new HashMap<BaseObject, ImageView>();
        reset();
    }


    public void reset() {
        if (mContainer != null) {
            mContainer.removeAllViews();
        }
        mImageMap.clear();
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
    }

    public void removeAll() {
        if (mImageMap != null) {
            mImageMap.clear();
        }
        if (mContainer != null) {
            mContainer.removeAllViews();
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
    	Debug.d(TAG, "--->draw");
    	if (object instanceof MessageObject) {
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int)object.getX();
        lp.topMargin = (int) object.getY();
        ImageView image = new ImageView(mContext);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageBitmap(object.getScaledBitmap(mContext));

        if (object.getSelected()) {
            image.setBackgroundColor(Color.GREEN);
        } else {
            image.setBackgroundColor(Color.WHITE);
        }
        /** width&height must be reseted after object bitmap drawed success */
        lp.width = (int)object.getWidth();
        lp.height = (int)object.getHeight();
        mContainer.addView(image, lp);
        mImageMap.put(object, image);
        image.setTag(object);
        image.setOnTouchListener(this);
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
                if (view != null) {
                    view.setBackgroundColor(Color.WHITE);
                }
                obj.setSelected(false);
            }

        }
        if (object == null) {
            return;
        }
        object.setSelected(true);
        ImageView v = mImageMap.get(object);
        if (v != null) {
            v.setBackgroundColor(Color.GREEN);
        }
    }

}
