package com.industry.printer.ui.CustomerDialog;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import com.industry.printer.R;
import com.industry.printer.R.id;
import com.industry.printer.R.layout;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.TLKFileParser;
import com.industry.printer.ui.CustomerAdapter.ListViewButtonAdapter;
import com.industry.printer.ui.CustomerAdapter.MessageListAdater;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MessageBrowserDialog extends CustomerDialogBase implements android.view.View.OnClickListener, OnItemClickListener, OnTouchListener, OnScrollListener {

		private final String TAG = MessageBrowserDialog.class.getSimpleName();
		
		public RelativeLayout mConfirm;
		public RelativeLayout mCancel;
		public RelativeLayout mPagePrev;
		public RelativeLayout mPageNext;
		public static String mTitle;
		
		public ListView mMessageList;
		public View mVSelected;
		
		public boolean isTop;
		public boolean isBottom;
		
		public MessageListAdater mFileAdapter;
		public LinkedList<Map<String, Object>> mContent;
		
		
		public MessageBrowserDialog(Context context) {
			super(context);
			
			mVSelected = null;
			mContent = new LinkedList<Map<String, Object>>();
			isTop = false;
			isBottom = false;
			mFileAdapter = new MessageListAdater(context, 
					mContent, 
					R.layout.message_item_layout, 
					new String[]{"title", "abstract"}, 
					new int[]{R.id.tv_message_title, R.id.tv_message_abstract});
		}
		
		@Override
		 protected void onCreate(Bundle savedInstanceState) {
			Debug.d(TAG, "===>oncreate super");
			 super.onCreate(savedInstanceState);
			 Debug.d(TAG, "===>oncreate");
			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 this.setContentView(R.layout.message_list_layout);
			 
			 mConfirm = (RelativeLayout) findViewById(R.id.btn_ok_message_list);
			 mConfirm.setOnClickListener(this);
			 
			 mCancel = (RelativeLayout) findViewById(R.id.btn_cancel_message_list);
			 mCancel.setOnClickListener(this);
			 
			 mPagePrev = (RelativeLayout) findViewById(R.id.btn_page_prev);
			 mPagePrev.setOnClickListener(this);
			 
			 mPageNext = (RelativeLayout) findViewById(R.id.btn_page_next);
			 mPageNext.setOnClickListener(this);
			 
			 
			 mMessageList = (ListView) findViewById(R.id.message_listview);
			 mMessageList.setOnItemClickListener(this);
			 
			 mMessageList.setOnTouchListener(this);
			 mMessageList.setOnScrollListener(this);
			 loadMessages();
			 mFileAdapter.notifyDataSetChanged();
			 
		 }

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
				case R.id.btn_ok_message_list:
					
					dismiss();
					if (pListener != null) {
						pListener.onClick();
					}
					break;
				case R.id.btn_cancel_message_list:
					dismiss();
					if (nListener != null) {
						nListener.onClick();
					}
					break;
				case R.id.btn_page_prev:
					mMessageList.smoothScrollBy(-200, 50);
					break;
				case R.id.btn_page_next:
					mMessageList.smoothScrollBy(200, 50);
					break;
				
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map<String, Object> selected = mContent.get(position);
			mFileAdapter.setSelected(position);
			mFileAdapter.notifyDataSetChanged();
			mTitle = (String) selected.get("title");
			/*
			if(mVSelected == null)
			{
				view.setBackgroundColor(R.color.message_selected_color);
				mVSelected = view;
			}
			else
			{
				mVSelected.setBackgroundColor(Color.WHITE);
				view.setBackgroundColor(R.color.message_selected_color);
				mVSelected = view;
			}*/
			
		}
		
		public void loadMessages()
		{
			TLKFileParser parser = new TLKFileParser(getContext(), null);
			String tlkPath = ConfigPath.getTlkPath();
			if (tlkPath == null) {
				return ;
			}
			
			File rootpath = new File(tlkPath);
			File[] Tlks = rootpath.listFiles();
			if (Tlks == null) {
				return ;
			}
			for (File t:Tlks) {
				
				if (!t.isDirectory()) {
					continue;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				Debug.d(TAG, "--->loadMessage:" + t.getAbsolutePath());
				parser.setTlk(t.getAbsolutePath());
				String content = parser.getContentAbatract();
				map.put("title", t.getName());
				map.put("abstract", content);
				mContent.add(map);
			}
			mMessageList.setAdapter(mFileAdapter);
		}
		
		public static String getSelected() {
			if (mTitle == null) {
				return "";
			} else {
				return mTitle;
			}
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(firstVisibleItem==0){
				Debug.e(TAG, "滑到顶部");
				isTop = true;
            } else {
            	isTop = false;
            }
			
			if(visibleItemCount+firstVisibleItem==totalItemCount){
            	Debug.e(TAG, "滑到底部");
            	isBottom = true;
            } else {
            	isBottom = false;
            }
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int state) {
			switch (state) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				Debug.d(TAG, "===>idle");
				
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				Debug.d(TAG, "===>fling");
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				Debug.d(TAG, "===>touch scroll");
				break;
			default:
				break;
			}
		}
		
}
