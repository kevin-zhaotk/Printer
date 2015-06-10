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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MessageBrowserDialog extends CustomerDialogBase implements android.view.View.OnClickListener, OnItemClickListener, OnTouchListener {

		private final String TAG = MessageBrowserDialog.class.getSimpleName();
		
		public Button mConfirm;
		public Button mCancel;
		public Button mPagePrev;
		public Button mPageNext;
		public static String mTitle;
		
		public ListView mMessageList;
		public View mVSelected;
		
		public MessageListAdater mFileAdapter;
		public LinkedList<Map<String, Object>> mContent;
		
		
		public MessageBrowserDialog(Context context) {
			super(context);
			
			mVSelected = null;
			mContent = new LinkedList<Map<String, Object>>();
			
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
			 
			 mConfirm = (Button) findViewById(R.id.btn_ok_message_list);
			 mConfirm.setOnClickListener(this);
			 
			 mCancel = (Button) findViewById(R.id.btn_cancel_message_list);
			 mCancel.setOnClickListener(this);
			 
			 mPagePrev = (Button) findViewById(R.id.btn_page_prev);
			 mPagePrev.setOnClickListener(this);
			 
			 mPageNext = (Button) findViewById(R.id.btn_page_next);
			 mPageNext.setOnClickListener(this);
			 
			 
			 mMessageList = (ListView) findViewById(R.id.message_listview);
			 mMessageList.setOnItemClickListener(this);
			 
			 mMessageList.setOnTouchListener(this);
			 loadMessages();
			 mFileAdapter.notifyDataSetChanged();
			 
		 }

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
				case R.id.btn_ok_message_list:
					if (mVSelected == null) {
						break;
					}
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
					mMessageList.scrollBy(0, -300);
					break;
				case R.id.btn_page_next:
					mMessageList.scrollBy(0, 300);
					break;
				
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map<String, Object> selected = mContent.get(position);
			
			mTitle = (String) selected.get("title");

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
			}}
		
		public void loadMessages()
		{
			TLKFileParser parser = new TLKFileParser(null);
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
		
}
