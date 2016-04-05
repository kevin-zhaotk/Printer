package com.industry.printer.ui.CustomerDialog;

import android.R.menu;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.industry.printer.R;
import com.industry.printer.object.BaseObject;

public class ObjectInsertDialog extends Dialog implements android.view.View.OnClickListener {

	public static final String OBJECT_TYPE = "ObjType";
	public static final String OBJECT_FORMAT = "ObjFormat";
	
	private Context 	mContext;
	public RadioButton 	mText;
	public RadioButton 	mRTime;
	public RadioButton 	mCounter;
	public RadioButton	mBarcode;
	public RadioButton	mJulian;
	public RadioButton	mGraphic;
	public RadioButton	mLine;
	public RadioButton	mRect;
	public RadioButton	mEllipse;
	public Message		mDismissMsg;
	
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
		
		mCounter = (RadioButton) findViewById(R.id.objinsert_counter);
		mCounter.setOnClickListener(this);
		
		mBarcode = (RadioButton) findViewById(R.id.objinsert_barcode);
		mBarcode.setOnClickListener(this);
		
		mGraphic = (RadioButton) findViewById(R.id.objinsert_graphic);
		mGraphic.setOnClickListener(this);
		
		mLine = (RadioButton) findViewById(R.id.objinsert_line);
		mLine.setOnClickListener(this);
		
		mRect = (RadioButton) findViewById(R.id.objinsert_rect);
		mRect.setOnClickListener(this);
		
		mEllipse = (RadioButton) findViewById(R.id.objinsert_ellipse);
		mEllipse.setOnClickListener(this);

		mJulian = (RadioButton) findViewById(R.id.objinsert_julian);
		mJulian.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Bundle bundle = new Bundle();
		switch (arg0.getId()) {
			case R.id.objinsert_text:
				bundle.putString("object", "");
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_TEXT);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_time:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_RT);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_counter:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_CNT);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_barcode:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_BARCODE);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_julian:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_JULIAN);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_graphic:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_GRAPHIC);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_line:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_LINE);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_rect:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_RECT);
				mDismissMsg.setData(bundle);
				break;
			case R.id.objinsert_ellipse:
				bundle.putString(OBJECT_TYPE, BaseObject.OBJECT_TYPE_ELLIPSE);
				mDismissMsg.setData(bundle);
				break;
			default:
				break;
		}
		dismiss();
	}
	
	@Override
	public void setDismissMessage(Message msg) {
		super.setDismissMessage(msg);
		mDismissMsg = msg;
	}
}
