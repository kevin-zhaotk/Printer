package com.industry.printer.ui.CustomerAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.industry.printer.BinInfo;
import com.industry.printer.MessageTask;
import com.industry.printer.R;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.DimenssionConvertion;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.data.DataTask;

import android.R.integer;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class MessageListAdater extends BaseAdapter {
	
	private static final String TAG = MessageListAdater.class.getSimpleName();

	/**
	 * A customerized view holder for widgets 
	 * @author zhaotongkai
	 */
	private class ItemViewHolder{
		TextView	mTitle;		//message title
		TextView	mAbstract;	//message abstract
		// ImageView	mImage;
		LinearLayout mllPreview;
		ImageView	mMark;
	}
	
	/**
	 * The Content list
	 */
	private LinkedList<Map<String, Object>> mCntList;
	
	/**
	 * An inflater for inflate the view
	 */
	private LayoutInflater mInflater;
	
	/**
	 * context
	 */
	private Context mContext;
	
	/**
	 * The Keys in HashMap
	 */
	private String[] mKeys;
	
	/**
	 * The view id in HashMap 
	 */
	private int[]	mViewIDs;
	
	/**
	 * widget holder
	 */
	private ItemViewHolder mHolder;
	
	
	/**
	 * 
	 */
	private int mSelected;
	
	private Map<String, Bitmap> mPreviews = new HashMap<String, Bitmap>();
	
	/**
	 * Construct
	 */
	public MessageListAdater(Context context, LinkedList<Map<String, Object>> list, int resource,
			String from[], int to[])
	{
		mSelected = -1;
		mCntList = list;
		mContext = context;
		mKeys = new String[from.length];
		Debug.d(TAG, "====key size="+mKeys.length);
		mViewIDs = new int[to.length];
		System.arraycopy(from, 0, mKeys, 0, from.length);
		System.arraycopy(to, 0, mViewIDs, 0, to.length);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	public MessageListAdater getInstance()
	{
		return this;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCntList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mCntList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	public void setSelected(int position) {
		mSelected = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView!=null)
			mHolder = (ItemViewHolder) convertView.getTag();
		else
		{
			//prepare a empty view 
			convertView = mInflater.inflate(R.layout.message_item_layout, null);
			mHolder = new ItemViewHolder();
			// mHolder.mTitle = (TextView) convertView.findViewById(mViewIDs[0]);
			// mHolder.mAbstract = (TextView) convertView.findViewById(mViewIDs[1]);
			mHolder.mTitle = (TextView) convertView.findViewById(mViewIDs[0]);
			// mHolder.mImage = (ImageView) convertView.findViewById(mViewIDs[1]);
			mHolder.mllPreview = (LinearLayout) convertView.findViewById(mViewIDs[1]);
			mHolder.mMark = (ImageView) convertView.findViewById(mViewIDs[2]);
			convertView.setTag(mHolder);
		}
		
		HashMap<String, Object> item = (HashMap<String, Object>) mCntList.get(position);
		
		String title = (String) item.get(mKeys[0]);
		// String abstrace = (String) item.get(mKeys[1]);
		//fill the elements into the empty view created early 
		mHolder.mTitle.setText(title);
		// mHolder.mAbstract.setText(abstrace);
		/*
		 * 最早的方案，使用編輯時生成的1.bmp
		 * 優點：效率高
		 * 缺點：非實時
		 */
		/*
		String path = ConfigPath.getTlkDir(title) + MessageTask.MSG_PREV_IMAGE;
		File img = new File(path);
		if (img.exists()) {
			mHolder.mImage.setImageURI(Uri.parse("file://" + path));
		} else {
			mHolder.mImage.setImageResource(R.drawable.preview_null);
		}*/
		/*
		 * 通過bin生成預覽圖
		 * 優點：實時
		 * 缺點：效率低
		 */
		Bitmap bmp = mPreviews.get(title);
		if (bmp == null) {
			MessageTask task = new MessageTask(mContext, title);
			DataTask dTask = new DataTask(mContext, task);
			dTask.prepareBackgroudBuffer();
			bmp = dTask.getPreview();
			if (bmp.getWidth() > 1500) {
				Bitmap b = Bitmap.createBitmap(bmp, 0, 0, 1500, bmp.getHeight());
				BinFromBitmap.recyleBitmap(bmp);
				bmp = b;
			}
			mPreviews.put(title, bmp);
		}
		// mHolder.mImage.setImageBitmap(bmp);
		dispPreview(bmp);
		Debug.d(TAG, "--->getview position= "+ position + "  -- selected=" + mSelected);
		if(position == mSelected)
		{
			Debug.d(TAG, "---blue");
			mHolder.mMark.setVisibility(View.VISIBLE);
		}
		else {
			Debug.d(TAG, "---transparent");
			mHolder.mMark.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	
	private void dispPreview(Bitmap bmp) {
		int x=0,y=0;
		int cutWidth = 0;
		
		float scale = (float)DimenssionConvertion.dip2px(mContext, 100)/bmp.getHeight();
		mHolder.mllPreview.removeAllViews();
			for (int i = 0;x < bmp.getWidth(); i++) {
				if (x + 1200 > bmp.getWidth()) {
					cutWidth = bmp.getWidth() - x;
				} else {
					cutWidth =1200;
					
				}
				Bitmap child = Bitmap.createBitmap(bmp, x, 0, cutWidth, bmp.getHeight());
				Debug.d(TAG, "-->child: " + child.getWidth() + "  " + child.getHeight() + "   view h: " + mHolder.mllPreview.getHeight());
				Bitmap scaledChild = Bitmap.createScaledBitmap(child, (int) (cutWidth*scale), (int) (bmp.getHeight() * scale), true);
				child.recycle();
				x += cutWidth;
				ImageView imgView = new ImageView(mContext);
				imgView.setScaleType(ScaleType.FIT_START);
//				if (density == 1) {
					imgView.setLayoutParams(new LayoutParams(scaledChild.getWidth(),scaledChild.getHeight()));
//				} else {
//					imgView.setLayoutParams(new LayoutParams(cutWidth,LayoutParams.MATCH_PARENT));
//				}
				
				imgView.setBackgroundColor(Color.WHITE);
				imgView.setImageBitmap(scaledChild);
				mHolder.mllPreview.addView(imgView);
			}
	}
}
