package com.industry.printer.ui.CustomerDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.industry.printer.R;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.TextObject;

public class ObjectInsertDialog extends Dialog implements android.view.View.OnClickListener, OnItemClickListener {

	private Context mContext;
	public RadioButton mText;
	public RadioButton mRTime;
	public RadioButton mCounter;
	public ListView 	mTimestyle;
	public ListView 	mCounterSize;
	public RelativeLayout mTimeLayout;
	public ImageView		mPageup;
	public ImageView		mPagedown;
	public Message		mDismissMsg;
	public TextView 		mBack;
	
	public ObjectInsertDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.layout_objectinsert_dialog);
		
		mText = (RadioButton)findViewById(R.id.objinsert_text);
		mText.setOnClickListener(this);
		
		mRTime = (RadioButton)findViewById(R.id.objinsert_time);
		mRTime.setOnClickListener(this);
		mTimeLayout = (RelativeLayout) findViewById(R.id.objinsert_list);
		mTimestyle = (ListView)findViewById(R.id.objinsert_list_time);
		mTimestyle.setOnItemClickListener(this);
		
		mCounter = (RadioButton) findViewById(R.id.objinsert_counter);
		mCounter.setOnClickListener(this);
		mCounterSize = (ListView)findViewById(R.id.objinsert_list_counter);
		mCounterSize.setOnItemClickListener(this);
		
		mPagedown = (ImageView) findViewById(R.id.obj_dialog_btn_pagedown);
		mPagedown.setOnClickListener(this);
		mPageup = (ImageView) findViewById(R.id.obj_dialog_btn_pageup);
		mPageup.setOnClickListener(this);
		
		mBack = (TextView) findViewById(R.id.dialog_back);
		mBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.objinsert_text:
				mTimeLayout.setVisibility(View.GONE);
				Bundle bundle = new Bundle();
				bundle.putString("object", "");
				mDismissMsg.setData(bundle);
				dismiss();
				break;
			case R.id.objinsert_time:
				mTimeLayout.setVisibility(View.VISIBLE);
				mTimestyle.setVisibility(View.VISIBLE);
				mCounterSize.setVisibility(View.GONE);
				break;
			case R.id.objinsert_counter:
				mTimeLayout.setVisibility(View.VISIBLE);
				mTimestyle.setVisibility(View.GONE);
				mCounterSize.setVisibility(View.VISIBLE);
				break;
			case R.id.obj_dialog_btn_pageup:
				if (mRTime.isChecked()) {
					mTimestyle.smoothScrollBy(-200, 1000);
				} else if (mCounter.isChecked()) {
					mCounterSize.smoothScrollBy(-200, 1000);
				}
				
				break;
			case R.id.obj_dialog_btn_pagedown:
				if (mRTime.isChecked()) {
					mTimestyle.smoothScrollBy(200, 1000);
				} else if (mCounter.isChecked()) {
					mCounterSize.smoothScrollBy(200, 1000);
				}
				break;
			case R.id.dialog_back:
				dismiss();
				break;
			default:
				break;
		}
		
	}
	
	@Override
	public void setDismissMessage(Message msg) {
		super.setDismissMessage(msg);
		mDismissMsg = msg;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Debug.d("", "--->arg2=" + arg2);
		Bundle bundle = null;
		if (mRTime.isChecked()) {
			String[] formats = mContext.getResources().getStringArray(R.array.strTimeFormat);
			Debug.d("", "--->arg2=" + formats[arg2]);
			bundle = new Bundle();
			bundle.putString("object", ObjectsFromString.REALTIME_FLAG + formats[arg2]);
		} else if (mCounter.isChecked()) {
			String[] formats = mContext.getResources().getStringArray(R.array.strarrayCounter);
			Debug.d("", "--->arg2=" + formats[arg2]);
			bundle = new Bundle();
			bundle.putString("object", ObjectsFromString.COUNTER_FLAG + BaseObject.intToFormatString(0, arg2 + 3));
		}
		
		mDismissMsg.setData(bundle);
		dismiss();
	}

}
