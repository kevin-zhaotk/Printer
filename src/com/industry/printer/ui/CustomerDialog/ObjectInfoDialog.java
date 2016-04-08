package com.industry.printer.ui.CustomerDialog;

import java.util.zip.Inflater;

import com.google.zxing.BarcodeFormat;
import com.industry.printer.R;
import com.industry.printer.R.array;
import com.industry.printer.R.id;
import com.industry.printer.R.layout;
import com.industry.printer.R.string;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.EllipseObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LineObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RectObject;
import com.industry.printer.object.TextObject;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.RTSecondObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter.IOnItemClickListener;
import com.industry.printer.widget.PopWindowSpiner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ObjectInfoDialog extends Dialog implements android.view.View.OnClickListener, IOnItemClickListener {
	
	public static final String TAG="ObjectInfoDialog";
	public OnPositiveBtnListener mPListener;
	public OnNagitiveBtnListener mNListener;
	public onDeleteListener mDelListener;
	
	public BaseObject mObject;
	
	public TextView mXCorView;
	public TextView mXCorUnit;
	public TextView mYCorView;
	public TextView mYCorUnit;
	public TextView mWidthView;
	public TextView mWidthUnit;
	public TextView mHighView;
	public TextView mHighUnit;
	public TextView mCntView;
	public TextView mFontView;
	public TextView mRtfmtView;
	public TextView mBitsView;
	public TextView mDirectView;
	public TextView mCodeView;
	public TextView mNumshowView;
	public TextView mLineView;
	public TextView mLinetypeView;
	
	
	public EditText mWidthEdit;
	public EditText mHighEdit;
	public EditText mXcorEdit;
	public EditText mYcorEdit;
	public EditText mContent;
	public TextView mFont;
	public TextView mRtFormat;
	public EditText mDigits;
	public Spinner mDir;
	public Spinner mCode;
	public CheckBox mShow;
	public EditText mLineWidth;
	public EditText mShift1;
	public EditText mShiftVal1;
	public EditText mShift2;
	public EditText mShiftVal2;
	public EditText mShift3;
	public EditText mShiftVal3;
	public EditText mShift4;
	public EditText mShiftVal4;
	public EditText mShift5;
	public EditText mShiftVal5;
	public Button	mBtnOk;
	
	public Spinner mLineType;
	
	
	public EditText mMsg;
	public TextView mPrinter;
	/*
	 * 
	 */
	public Button mOk;
	public Button mCancel;
	public Button mDelete;
	
	public Context mContext;
	public BaseObject mObj;
	
	private PopWindowSpiner  mSpiner;
	private PopWindowAdapter mFontAdapter;
	private PopWindowAdapter mFormatAdapter;
	private PopWindowAdapter mTypeAdapter;
	
	public ObjectInfoDialog(Context context, BaseObject obj) {
		super(context, R.style.Dialog_Fullscreen);
		mContext = context;
		mObj = obj;
		initAdapter();
	}

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 // this.setTitle(R.string.str_title_infodialog);
	     if(mObj==null)
	     {
	    	 Debug.d(TAG, "--->obj: " + mObj.mIndex);
	    	 this.setContentView(R.layout.obj_info_msg); 
	     }
	     else if(mObj instanceof TextObject)
	     {
	    	 this.setContentView(R.layout.obj_info_text); 	 
	     }
	     else if(mObj instanceof BarcodeObject)
	     {
	    	 this.setContentView(R.layout.obj_info_barcode);
	     }
	     else if(mObj instanceof CounterObject)
	     {
	    	 this.setContentView(R.layout.obj_info_counter);
	     }
	     else if(mObj instanceof GraphicObject)
	     {
	    	 this.setContentView(R.layout.obj_info_graphic);
	     }
	     else if(mObj instanceof RealtimeObject)
	     {
	    	 this.setContentView(R.layout.obj_info_realtime);
	     }
	     else if(mObj instanceof JulianDayObject ||
	    		 mObj instanceof RTSecondObject)
	     {
	    	 this.setContentView(R.layout.obj_info_julian);
	     }
	     else if(mObj instanceof LineObject || mObj instanceof RectObject || mObj instanceof EllipseObject )
	     {
	    	 this.setContentView(R.layout.obj_info_shape);
	     }
	     else if(mObj instanceof ShiftObject)
	     {
	    	 Debug.d(TAG, "ShiftObject");
	    	 this.setContentView(R.layout.obj_info_shift);
	     }
	     else if(mObj instanceof MessageObject)
	     {
	    	 this.setContentView(R.layout.msg_info);
	    	 mMsg = (EditText) findViewById(R.id.msgNameEdit);
	    	 mPrinter = (TextView) findViewById(R.id.headTypeSpin);
	    	 mPrinter.setOnClickListener(this);
	     }
	     else 
	     {
	    	 Debug.d(TAG, "--->obj: " + mObj.mIndex);
	    	 this.setContentView(R.layout.obj_info_text);
	     }
	     
//	    mXCorView 	= (TextView) findViewById(R.id.xCorView);
//	 	mXCorUnit 		= (TextView) findViewById(R.id.xCorUnit);
//	 	mYCorView	= (TextView) findViewById(R.id.yCorView);
//	 	mYCorUnit 		= (TextView) findViewById(R.id.yCorUnit);
//	 	mWidthView 	= (TextView) findViewById(R.id.widthView);
//	 	mWidthUnit 	= (TextView) findViewById(R.id.widthUnitView);
//	 	mHighView 		= (TextView) findViewById(R.id.highView);
//	 	mHighUnit 		= (TextView) findViewById(R.id.highUnitView);
//	 	mCntView 		= (TextView) findViewById(R.id.cntView);
//	 	mFontView 		= (TextView) findViewById(R.id.fontView);
//	 	mRtfmtView 	= (TextView) findViewById(R.id.rtFmtView);
//	 	mBitsView 		= (TextView) findViewById(R.id.bitsView);
//	 	mDirectView 	= (TextView) findViewById(R.id.viewDirect);
//	 	mCodeView 	= (TextView) findViewById(R.id.viewCode);
//	 	mNumshowView = (TextView) findViewById(R.id.view_num_show);
//	 	mLineView 		= (TextView) findViewById(R.id.lineView);
//	 	mLinetypeView = (TextView) findViewById(R.id.view_line_type);
//	 	
	 	//Inflater inflater inflater= new Inflater();
	 	//View v1 = inflater.inflate(R.id.)
	 	
	     mWidthEdit = (EditText)findViewById(R.id.widthEdit);
	     mHighEdit = (EditText)findViewById(R.id.highEdit);
	     mXcorEdit = (EditText)findViewById(R.id.xCorEdit);
	     mYcorEdit = (EditText)findViewById(R.id.yCorEdit);
	     mContent = (EditText)findViewById(R.id.cntEdit);
	     mFont = (TextView) findViewById(R.id.fontSpin);
	     mFont.setOnClickListener(this);
	     mRtFormat = (TextView) findViewById(R.id.rtFormat);
	     mRtFormat.setOnClickListener(this);
	     mDigits = (EditText) findViewById(R.id.cntBits);
	     mDir = (Spinner) findViewById(R.id.spinDirect); 
	     mCode = (Spinner) findViewById(R.id.spinCode);
	     mShow = (CheckBox) findViewById(R.id.check_Num_show);
	     mLineWidth = (EditText) findViewById(R.id.lineWidth);
	     mLineType = (Spinner) findViewById(R.id.spin_line_type);
	     mShift1 = (EditText) findViewById(R.id.edit_shift1);
	     mShiftVal1 = (EditText) findViewById(R.id.edit_shiftValue1);
	     mShift2 = (EditText) findViewById(R.id.edit_shift2);
	     mShiftVal2 = (EditText) findViewById(R.id.edit_shiftValue2);
	     mShift3 = (EditText) findViewById(R.id.edit_shift3);
	     mShiftVal3 = (EditText) findViewById(R.id.edit_shiftValue3);
	     mShift4 = (EditText) findViewById(R.id.edit_shift4);
	     mShiftVal4 = (EditText) findViewById(R.id.edit_shiftValue4);
	     mShift5 = (EditText) findViewById(R.id.edit_shift5);
	     mShiftVal5 = (EditText) findViewById(R.id.edit_shiftValue5);
	     mOk = (Button) findViewById(R.id.btn_confirm);
	     mCancel = (Button) findViewById(R.id.btn_objinfo_cnl);
	     fillObjInfo();
	     selfInfoEnable();

	     mOk.setOnClickListener(new View.OnClickListener(){
	    	 
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try{
						
						if(mObject instanceof MessageObject)
						{
							mObject.setContent(mMsg.getText().toString());
							((MessageObject) mObject).setPrinter(mPrinter.getSelectedItemPosition());
							return;
						}
						
						mObject.setWidth(Float.parseFloat(mWidthEdit.getText().toString()));
						mObject.setHeight(Float.parseFloat(mHighEdit.getText().toString()));
						mObject.setX(Float.parseFloat(mXcorEdit.getText().toString()));
						mObject.setY(Float.parseFloat(mYcorEdit.getText().toString()));
						Debug.d(TAG, "content="+mContent.getText().toString());
						mObject.setContent(mContent.getText().toString());
						Resources res = mContext.getResources();
						
						String font = res.getStringArray(R.array.strFontFile)[mFont.getSelectedItemPosition()];
						mObject.setFont(font);
						if(mObject instanceof RealtimeObject)
						{
							((RealtimeObject) mObject).setFormat((String)mRtFormat.getSelectedItem());
							((RealtimeObject)mObject).setWidth(Float.parseFloat(mWidthEdit.getText().toString()));
						}
						else if(mObject instanceof CounterObject)
						{
							((CounterObject) mObject).setBits(Integer.parseInt(mDigits.getText().toString()));
							((CounterObject) mObject).setDirection("increase".equals(mDir.getSelectedItem().toString())?true:false);
						}
						else if(mObject instanceof BarcodeObject)
						{
							((BarcodeObject) mObject).setCode(mCode.getSelectedItem().toString());
							((BarcodeObject) mObject).setShow(mShow.isChecked());
						}
						else if(mObject instanceof RectObject)
						{
							mObject.setLineWidth(Integer.parseInt(mLineWidth.getText().toString()));
							((RectObject) mObject).setLineType(mLineType.getSelectedItemPosition());
						}
						else if(mObject instanceof LineObject)
						{
							mObject.setLineWidth(Integer.parseInt(mLineWidth.getText().toString()));
							((LineObject) mObject).setLineType(mLineType.getSelectedItemPosition());
						}
						else if(mObject instanceof EllipseObject)
						{
							mObject.setLineWidth(Integer.parseInt(mLineWidth.getText().toString()));
							((EllipseObject) mObject).setLineType(mLineType.getSelectedItemPosition());
						}
						else if(mObject instanceof ShiftObject)
						{
							((ShiftObject) mObject).setShift(0, mShift1.getText().toString());
							((ShiftObject) mObject).setValue(0, mShiftVal1.getText().toString());
							((ShiftObject) mObject).setShift(1, mShift2.getText().toString());
							((ShiftObject) mObject).setValue(1, mShiftVal2.getText().toString());
							((ShiftObject) mObject).setShift(2, mShift3.getText().toString());
							((ShiftObject) mObject).setValue(2, mShiftVal3.getText().toString());
							((ShiftObject) mObject).setShift(3, mShift4.getText().toString());
							((ShiftObject) mObject).setValue(3, mShiftVal4.getText().toString());
							((ShiftObject) mObject).setShift(4, mShift5.getText().toString());
							((ShiftObject) mObject).setValue(4, mShiftVal5.getText().toString());
						}
							
						//mObjRefreshHandler.sendEmptyMessage(0);
					}catch(NumberFormatException e)
					{
						System.out.println("NumberFormatException 292");
					}
					dismiss();
					if(mPListener != null)
						mPListener.onClick();
				}
				
			});
	     
	     mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	     
	     mDelete = (Button) findViewById(R.id.btn_delete);
	     mDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
				if(mDelListener != null) {
					mDelListener.onClick();
				}
			}
		});
	 }
	 
	 public void setObject(BaseObject obj)
	 {
		 mObject = obj;
	 }
	 
	 private void fillObjInfo()
	 {
		 int i=0;
		 if(mObject == null)
			 return;
		 if(mObject instanceof MessageObject)
			{	
				mMsg.setText(mObject.getContent());
				mPrinter.setSelection(((MessageObject) mObject).getPrinter());
			}
			else
			{
				mWidthEdit.setText(String.valueOf((int)mObject.getWidth()) );
				mHighEdit.setText(String.valueOf((int)mObject.getHeight()));
				mXcorEdit.setText(String.valueOf((int)mObject.getX()));
				mYcorEdit.setText(String.valueOf((int)mObject.getY()));
				mContent.setText(String.valueOf(mObject.getContent()));
				ArrayAdapter adp = (ArrayAdapter)mFont.getAdapter();
				
				mFont.setSelection(adp.getPosition(mObject.getFont()));
				if(mObject instanceof RealtimeObject)
				{
					String [] fm = mContext.getResources().getStringArray(R.array.strTimeFormat);
					for( i = 0; i<fm.length; i++)
					{
						if(fm[i].equals(((RealtimeObject) mObject).getFormat()))
						{
							mRtFormat.setSelection(i);
							break;
						}
					}
					if(i==fm.length)
						mRtFormat.setSelection(0);
				}
				else if(mObject instanceof CounterObject)
				{
					mDigits.setText(String.valueOf( ((CounterObject) mObject).getBits()));
					mDir.setSelection( ((CounterObject) mObject).getDirection()==true? 1: 0);
				}
				else if(mObject instanceof BarcodeObject)
				{
					if("ENA128".equals(((BarcodeObject) mObject).getCode()))
					{
						mCode.setSelection(0);
					}
					else if("QR".equals(((BarcodeObject) mObject).getCode()))
						mCode.setSelection(1);
					mShow.setChecked(((BarcodeObject) mObject).getShow());
				}
				else if(mObject instanceof ShiftObject)
				{
					mShift1.setText( String.valueOf(((ShiftObject)mObject).getShift(0)));
					mShiftVal1.setText(((ShiftObject)mObject).getValue(0));
					mShift2.setText( String.valueOf(((ShiftObject)mObject).getShift(1)));
					mShiftVal2.setText(((ShiftObject)mObject).getValue(1));
					mShift3.setText(String.valueOf(((ShiftObject)mObject).getShift(2)));
					mShiftVal3.setText(((ShiftObject)mObject).getValue(2));
					mShift4.setText(String.valueOf(((ShiftObject)mObject).getShift(3)));
					mShiftVal4.setText(((ShiftObject)mObject).getValue(3));
					mShift5.setText(String.valueOf(((ShiftObject)mObject).getShift(4)));
					mShiftVal5.setText(((ShiftObject)mObject).getValue(4));
				}
				else if(mObject instanceof RectObject){
					mLineWidth.setText(String.valueOf(((RectObject)mObject).getLineWidth()));
					mLineType.setSelection(((RectObject)mObject).getLineType());
				}
				else if(mObject instanceof LineObject){
					mLineWidth.setText(String.valueOf(((LineObject)mObject).getLineWidth()));
					mLineType.setSelection(((LineObject)mObject).getLineType());
				}
				else if(mObject instanceof EllipseObject){
					mLineWidth.setText(String.valueOf(((EllipseObject)mObject).getLineWidth()));
					mLineType.setSelection(((EllipseObject)mObject).getLineType());
				}
			}
	 }
	 
	 public void selfInfoEnable()
	{
		 if(mObject == null ||(mObject instanceof MessageObject))
			 return ;
		
		 if(mObject instanceof RealtimeObject ||
				 mObject instanceof GraphicObject ||
				 mObject instanceof RTSecondObject ||
				 mObject instanceof ShiftObject ||
				 mObject instanceof EllipseObject ||
				 mObject instanceof RectObject ||
				 mObject instanceof LineObject)
		{
			 Debug.d(TAG, ">>>>>disable content");
			 mContent.setEnabled(false);
			 mCntView.setTextColor(Color.GRAY);
		}
		
		else 
		{
			//this.setClickable(false);
		}
	}
	 
	 public void setOnPositiveBtnListener(OnPositiveBtnListener listener)
	 {
		mPListener = listener; 
	 }
	 
	 public void setOnNagitiveBtnListener(OnNagitiveBtnListener listener)
	 {
		 mNListener = listener;
	 }
	 
	 public void setOnDeleteListener(onDeleteListener listener) {
		 mDelListener = listener;
	 }
	 
	 public interface OnPositiveBtnListener
	 {
		 void onClick();
	 }
	 
	 public interface OnNagitiveBtnListener
	 {
		 void onClick();
	 }
	 
	 public interface onDeleteListener {
		 void onClick();
	 }
	 
	 private void initAdapter() {
		 
		mSpiner = new PopWindowSpiner(mContext);
		mSpiner.setFocusable(true);
		mSpiner.setOnItemClickListener(this);
		
		mFontAdapter = new PopWindowAdapter(mContext, null);
		mFormatAdapter = new PopWindowAdapter(mContext, null);
		mTypeAdapter = new PopWindowAdapter(mContext, null);
		
		String[] fonts = mContext.getResources().getStringArray(R.array.strFontArray);
		for (String font : fonts) {
			mFontAdapter.addItem(font);
		}
		
		String[] formats = mContext.getResources().getStringArray(R.array.strTimeFormat);
		for (String format : formats) {
			mFormatAdapter.addItem(format);
		}
		
		String[] types = mContext.getResources().getStringArray(R.array.strPrinterArray);
		for (String type : types) {
			mTypeAdapter.addItem(type);
		}
	 }
	 
	 private void setHFullScreen() {
		 Window win = this.getWindow();
		 win.getDecorView().setPadding(0, 0, 0, 0);
		 WindowManager.LayoutParams lp = win.getAttributes();
		 // lp.width = WindowManager.LayoutParams.FILL_PARENT;
		 lp.height = WindowManager.LayoutParams.FILL_PARENT;
		 win.setAttributes(lp);
	 }

	@Override
	public void onClick(View v) {
		if (v == null) {
			return;
		}
		mSpiner.setAttachedView(v);
		mSpiner.setWidth(v.getWidth());
		mSpiner.showAsDropDown(v);
		switch (v.getId()) {
		case R.id.headTypeSpin:
			mSpiner.setAdapter(mTypeAdapter);
			break;
		case R.id.fontSpin:
			mSpiner.setAdapter(mFontAdapter);
			break;
		case R.id.rtFormat:
			mSpiner.setAdapter(mFormatAdapter);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(int index) {
		TextView view = mSpiner.getAttachedView();
		if (view == mPrinter) {
			view.setText((String)mTypeAdapter.getItem(index));
		} else if (view == mFont) {
			view.setText((String)mFontAdapter.getItem(index));
		} else if (view == mRtFormat) {
			view.setText((String)mFormatAdapter.getItem(index));
		} else {
			Debug.d(TAG, "--->unknow view");
		}
	}
}
