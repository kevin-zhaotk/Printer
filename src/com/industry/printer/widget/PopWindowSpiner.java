package com.industry.printer.widget;

import java.util.ArrayList;

import com.industry.printer.Utils.Debug;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter.IOnItemClickListener;
import com.industry.printer.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

public class PopWindowSpiner extends PopupWindow implements OnItemClickListener {

	public Context mContext;
	public ListView mListView;
	public PopWindowAdapter mAdapter;
	public IOnItemClickListener mItemClickListener;
	
	public PopWindowSpiner(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_spiner_layout, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		
		mListView = (ListView) view.findViewById(R.id.pop_spiner_listview);
		mListView.setOnItemClickListener(this);
	}

	public void setAdapter(PopWindowAdapter adapter) {
		mAdapter = adapter;
		mListView.setAdapter(mAdapter);
	}
	
	public void refreshData(ArrayList<String> list, int index) {
		if (mAdapter != null) {
			mAdapter.refreshData(list, index);
		}
	}
	
	public void setOnItemClickListener(IOnItemClickListener listener) {
		mItemClickListener = listener;
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dismiss();
		if (mItemClickListener != null) {
			mItemClickListener.onItemClick(position);
		}
	}
}
