package com.industry.printer.ui.CustomerAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.industry.printer.R;
import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdater extends BaseAdapter {
	
	private static final String TAG = MessageListAdater.class.getSimpleName();

	/**
	 * A customerized view holder for widgets 
	 * @author zhaotongkai
	 */
	private class ItemViewHolder{
		TextView	mTitle;		//message title
		TextView	mAbstract;	//message abstract
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
	 * Construct
	 */
	public MessageListAdater(Context context, LinkedList<Map<String, Object>> list, int resource,
			String from[], int to[])
	{
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
			mHolder.mTitle = (TextView) convertView.findViewById(mViewIDs[0]);
			mHolder.mAbstract = (TextView) convertView.findViewById(mViewIDs[1]);
			convertView.setTag(mHolder);
		}
		
		HashMap<String, Object> item = (HashMap<String, Object>) mCntList.get(position);
		if(item!=null)
		{
			String title = (String) item.get(mKeys[0]);
			String abstrace = (String) item.get(mKeys[1]);
			//fill the elements into the empty view created early 
			mHolder.mTitle.setText(title);
			mHolder.mAbstract.setText(abstrace);
		}
		
		
		return convertView;
	}

}
