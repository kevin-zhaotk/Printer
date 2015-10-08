package com.industry.printer.ui.CustomerAdapter;

import java.util.LinkedList;
import java.util.Map;

import com.industry.printer.R;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsListAdapter extends BaseAdapter {

	private final static String TAG = SettingsListAdapter.class.getSimpleName();
	
	private Context mContext;
	private ItemViewHolder mHolder;
	
	/**
	 * An inflater for inflate the view
	 */
	private LayoutInflater mInflater;
	
	private String[] mTitles;
	
	/**
	 * A customerized view holder for widgets 
	 * @author zhaotongkai
	 */
	private class ItemViewHolder{
		TextView	mTitle;		//message title
		Spinner 	mSpinner;
		EditText	mValue;
		TextView	mUnit;
	}
	
	public SettingsListAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTitles = mContext.getResources().getStringArray(R.array.str_settings_params);
	}
	
	@Override
	public int getCount() {
		Debug.d(TAG, "--->getCount=" + Configs.gParams);
		return Configs.gParams;
	}

	@Override
	public Object getItem(int arg0) {
		Debug.d(TAG, "--->getItem");
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView != null) {
			mHolder = (ItemViewHolder) convertView.getTag();
		}
		else
		{
			//prepare a empty view 
			convertView = mInflater.inflate(R.layout.settings_frame_item, null);
			mHolder = new ItemViewHolder();
			mHolder.mTitle = (TextView) convertView.findViewById(R.id.ph_set_header);
			mHolder.mSpinner = (Spinner) convertView.findViewById(R.id.ph_set_content_sp);
			mHolder.mValue = (EditText) convertView.findViewById(R.id.ph_set_content_et);
			mHolder.mUnit = (TextView) convertView.findViewById(R.id.ph_set_unit);
			convertView.setTag(mHolder);
		}
		Debug.d(TAG, "--->getView:position=" + position);
		if (position == 0) {
			mHolder.mSpinner.setVisibility(View.VISIBLE);
			mHolder.mValue.setVisibility(View.GONE);
		} else {
			mHolder.mSpinner.setVisibility(View.GONE);
			mHolder.mValue.setVisibility(View.VISIBLE);
			mHolder.mValue.setText("0");
		}
		mHolder.mTitle.setText(mTitles[position]);
		mHolder.mUnit.setText("");
		return convertView;
	}

}
