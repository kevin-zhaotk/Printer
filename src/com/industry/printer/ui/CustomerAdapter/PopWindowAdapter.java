package com.industry.printer.ui.CustomerAdapter;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PopWindowAdapter extends BaseAdapter {

	public Context mContext;
	public ArrayList<String> mItems;
	public LayoutInflater mInflater;
	
	public interface IOnItemClickListener {
		public void onItemClick(int index);
	}
	
	public PopWindowAdapter(Context context, ArrayList<String> items) {
		mContext = context;
		mItems = items;
	}
	
	public void addItem(String item) {
		if (mItems == null) {
			mItems = new ArrayList<>();
		}
		mItems.add(item);
	}
	
	public void refreshData(ArrayList<String> list, int index) {
		mItems = list;
		if (list == null) {
			return;
		}
		if (index < 0) {
			index = 0;
		} else if (index > list.size()) {
			index = list.size() -1;
		}
	}
	
	@Override
	public int getCount() {
		if (mItems != null) {
			return mItems.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mItems == null || position < 0 || position >= mItems.size()) {
			return null;
		}
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = getInflater().inflate(R.layout.popwindow_spiner_item_layout, null);
			holder = new ViewHolder();
			holder.mTextView = convertView.findViewById(R.id.textview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.mTextView.setText(mItems.get(position));
		return convertView;
	}

	private LayoutInflater getInflater() {
		if (mInflater == null) {
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return mInflater;
	}
	
	private class ViewHolder {
		public TextView mTextView;
	}
	
	
}
