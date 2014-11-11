package com.industry.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import com.industry.printer.FileBrowserDialog.OnPositiveListener;
import com.industry.printer.ObjectInfoDialog.OnPositiveBtnListener;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.EllipseObject;
import com.industry.printer.object.Fileparser;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LineObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.RTSecondObject;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RectObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TextObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditTabActivity extends Activity {
	public static final String TAG="EditTabActivity";
	
	public Context mContext;
	public EditScrollView mObjView;
	public HorizontalScrollView mHScroll;
	
	public String mObjName;
	/*************************
	 * file operation buttons
	 * ***********************/
	public ImageButton mBtnNew;
	public ImageButton mBtnSave;
	public ImageButton mBtnSaveas;
	public ImageButton mBtnOpen;
	
	/*************************
	 * object operation buttons
	 ************************/
	public ImageButton mBtnLeft;			//move left
	public ImageButton mBtnRight;			//move right 
	public ImageButton mBtnUp;				//move up
	public ImageButton mBtnDown;		//move down 
	public ImageButton mBtnZoomoutX;//zoom out
	public ImageButton mBtnZoominX;	//zoom in
	public ImageButton mBtnZoomoutY;//zoom out
	public ImageButton mBtnZoominY;	//zoom in
	public ImageButton mDel;
	public ImageButton mTrans;
	/************************
	 * create Object buttons
	 * **********************/
	public ImageButton 	mBtnText;
	public ImageButton 	mBtnCnt;
	public ImageButton 	mBtnBar;
	public ImageButton	mImage;
	public ImageButton 	mBtnDay;
	public ImageButton 	mBtnTime;
	public ImageButton 	mBtnLine;
	public ImageButton 	mBtnRect;
	public ImageButton 	mBtnEllipse;
	public ImageButton	mShift;
	public ImageButton	mScnd;
	/**********************
	 * Object Information Table
	 * **********************/
	public ScrollView mViewInfo;
	public Spinner mObjList;
	Button mShowInfo;
	
	public static Vector<BaseObject> mObjs;
	public ArrayAdapter<String> mNameAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_frame);
		this.setVisible(false);
		mContext = this.getApplicationContext();
		
		mObjs = new Vector<BaseObject>();
		mObjs.add(new MessageObject(mContext, 0));
		
		mBtnNew = (ImageButton) findViewById(R.id.btn_new);
		mBtnNew.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mObjName = null;
				mObjs.clear();
				mObjs.add(new MessageObject(mContext, 0));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
			}		
		});
		
		mBtnSave = (ImageButton) findViewById(R.id.btn_save);
		mBtnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!isPropertyChanged())
					return;
				if(mObjName != null)
				{
					mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVEAS);
					return;
				}
				FileBrowserDialog fdialog = new FileBrowserDialog(EditTabActivity.this,FileBrowserDialog.FLAG_SAVE_FILE);
				//fdialog.setDismissMessage(Message.obtain(mHandler, 1));
				fdialog.setOnPositiveClickedListener(new OnPositiveListener(){
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVE);
					}
				});
				fdialog.show();
			}
		});
		
		mBtnSaveas = (ImageButton) findViewById(R.id.btn_saveas);
		mBtnSaveas.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog fdialog = new FileBrowserDialog(EditTabActivity.this, FileBrowserDialog.FLAG_SAVE_FILE);
				//fdialog.setDismissMessage(Message.obtain(mHandler, 1));
				fdialog.setOnPositiveClickedListener(new OnPositiveListener(){
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVE);
					}
				});
				fdialog.show();
			}
			
		});
		
		mBtnOpen = (ImageButton) findViewById(R.id.btn_open);
		
		mBtnOpen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//File file = new File("/mnt/usb/1.tlk");
				//Debug.d(TAG, ""+file.getPath()+"is "+file.exists());
				FileBrowserDialog fdialog = new FileBrowserDialog(EditTabActivity.this,Configs.LOCAL_ROOT_PATH, ".tlk",FileBrowserDialog.FLAG_OPEN_FILE);
				fdialog.setOnPositiveClickedListener(new OnPositiveListener(){
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_OPEN);
					}
				});
				fdialog.show();
			}
			
		});
		mHScroll = (HorizontalScrollView) findViewById(R.id.scrollView1);
		mObjView = (EditScrollView)findViewById(R.id.editView);
		
		mObjView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "onTouch x="+event.getX()+", y="+event.getY());
				int ret = getTouchedObj(event.getX(), event.getY());
				if(ret != -1)
				{
					clearCurObj();
					setCurObj(ret);
					mObjList.setSelection(ret);
					mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
				}
				return false;
			}
			
		});

		mObjList = (Spinner) findViewById(R.id.object_list);
		mNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);//R.layout.object_list_item);
		mNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mObjList.setAdapter(mNameAdapter);
		
		mObjList.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Debug.d(TAG,"==========objlist item " + position +" clicked"+" of "+mObjList.getCount());
				clearCurObj();
				setCurObj(position);
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "======onNothing selected");
			}
			
		});
		
		mShowInfo = (Button)findViewById(R.id.btn_objinfo);
		mShowInfo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ObjectInfoDialog objDialog = new ObjectInfoDialog(EditTabActivity.this, getCurObj());
				objDialog.setObject(getCurObj());
				objDialog.setOnPositiveBtnListener(new OnPositiveBtnListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
					}
					
				});
				objDialog.show();
			}
			
		});
		
		mBtnLeft = (ImageButton) findViewById(R.id.btn_left);
		mBtnLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				leftKeyPressed();
			}
			
		});
		
		mBtnLeft.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======Down button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(LEFT_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======Down button released!!");
					mKeyRepeatHandler.removeMessages(LEFT_KEY);
				}
				return false;
			}
			
		});
		
		
		
		mBtnRight = (ImageButton) findViewById(R.id.btn_right);
		mBtnRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rightKeyPressed();
			}
			
		});
		
		mBtnRight.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======Down button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(RIGHT_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======Down button released!!");
					mKeyRepeatHandler.removeMessages(RIGHT_KEY);
				}
				return false;         
			}
			
		});
		
		mBtnUp = (ImageButton) findViewById(R.id.btn_up);
		mBtnUp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				upKeyPressed();
			}
			
		});
		
		mBtnUp.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======Down button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(UP_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======up button released!!");
					mKeyRepeatHandler.removeMessages(UP_KEY);
				}
				return false;
			}
			
		});

		mBtnDown = (ImageButton) findViewById(R.id.btn_down);
		mBtnDown.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downKeyPressed();
			}
			
		});
		
		mBtnDown.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======Down button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(DOWN_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======Down button released!!");
					mKeyRepeatHandler.removeMessages(DOWN_KEY);
				}
				return false;
			}
			
		});
		
		mBtnZoomoutX = (ImageButton) findViewById(R.id.btn_zoomoutx);
		mBtnZoomoutX.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomOutXKeyPressed();
			}
			
		});
		
		mBtnZoomoutX.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======zoomout X button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(ZOOMX_OUT_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======zoomout X button released!!");
					mKeyRepeatHandler.removeMessages(ZOOMX_OUT_KEY);
				}
				return false;
			}
			
		});
		
		mBtnZoominX = (ImageButton) findViewById(R.id.btn_zoominx);
		mBtnZoominX.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomInXKeyPressed();
			}
			
		});
		
		mBtnZoominX.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======zoomin X button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(ZOOMX_IN_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======zoomin X button released!!");
					mKeyRepeatHandler.removeMessages(ZOOMX_IN_KEY);
				}
				return false;
			}
			
		});
		
		mBtnZoomoutY = (ImageButton) findViewById(R.id.btn_zoomouty);
		mBtnZoomoutY.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomOutYKeyPressed();
			}
			
		});
		
		mBtnZoomoutY.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======zoomout Y button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(ZOOMY_OUT_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======zoomout Y button released!!");
					mKeyRepeatHandler.removeMessages(ZOOMY_OUT_KEY);
				}
				return false;
			}
			
		});
		
		mBtnZoominY = (ImageButton) findViewById(R.id.btn_zoominy);
		mBtnZoominY.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zoomInYKeyPressed();
			}
			
		});
		
		mBtnZoominY.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.d(TAG, "======zoomin Y button pressed!!");
					mKeyRepeatHandler.sendEmptyMessageDelayed(ZOOMY_IN_KEY, 800);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Debug.d(TAG, "======zoomin Y button released!!");
					mKeyRepeatHandler.removeMessages(ZOOMY_IN_KEY);
				}
				return false;
			}
			
		});
		
		mDel = (ImageButton) findViewById(R.id.btn_delete);
		mDel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BaseObject obj = getCurObj();
				if(obj == null || obj instanceof MessageObject)
					return;
				mObjs.remove(obj);
				setCurObj(0);
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
			}
			
		});
		
		mTrans = (ImageButton) findViewById(R.id.btn_trans);
		mTrans.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DataTransformDialog d = new DataTransformDialog(EditTabActivity.this);
				d.show();
			}
			
		});
		
		mBtnText = (ImageButton) findViewById(R.id.btn_ABC);
		mBtnText.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearCurObj();
				mObjs.add(new TextObject(mContext,  getNextXcor()));
				Debug.d(TAG, "=======objs = "+mObjs.size());
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				mObjList.invalidate();
				Debug.d(TAG, "=======setSelection "+mObjList.getCount());
				//mObjList.setSelection(mObjList.getCount()-1);
				
			}
		});
		
		mBtnCnt = (ImageButton)findViewById(R.id.btn_cnt);
		mBtnCnt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new CounterObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});

		mBtnBar = (ImageButton)findViewById(R.id.btn_barcode);
		mBtnBar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new BarcodeObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});
		
		mImage = (ImageButton) findViewById(R.id.btn_image);
		mImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog fdialog = new FileBrowserDialog(EditTabActivity.this, FileBrowserDialog.FLAG_OPEN_FILE);
				fdialog.setOnPositiveClickedListener(new OnPositiveListener(){
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Debug.d(TAG, "image selected");
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_IMAGESELECT);
						//mObjList.setSelection(mObjList.getCount()-1);
					}
				});
				fdialog.show();
			}
			
		});
		
		mBtnDay = (ImageButton)findViewById(R.id.btn_julian);
		mBtnDay.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new JulianDayObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});
		
		mBtnTime = (ImageButton)findViewById(R.id.btn_time);
		mBtnTime.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new RealtimeObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});
		

		mBtnLine = (ImageButton)findViewById(R.id.btn_line);
		mBtnLine.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new LineObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});

		mBtnRect = (ImageButton)findViewById(R.id.btn_rect);
		mBtnRect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new RectObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});

		mBtnEllipse = (ImageButton)findViewById(R.id.btn_ellipse);
		mBtnEllipse.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearCurObj();
				mObjs.add(new EllipseObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});
		
		mShift = (ImageButton) findViewById(R.id.btn_shift);
		mShift.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearCurObj();
				mObjs.add(new ShiftObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}
			
		});
		
		mScnd = (ImageButton) findViewById(R.id.btn_second);
		mScnd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearCurObj();
				mObjs.add(new RTSecondObject(mContext, getNextXcor()));
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
				
			}			
		});

		/*initialize the object list spinner*/
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
	}
	
	/**
	 * REFRESH_OBJECT_CHANGED
	 *   some object changes, need to resave the tlk&bin files
	 */
	public static final int REFRESH_OBJECT_CHANGED=0;
	/**
	 * REFRESH_OBJECT_PROPERTIES
	 *   the object properties changed
	 */
	public static final int REFRESH_OBJECT_PROPERTIES=1;
	/**
	 * REFRESH_OBJECT_JUST
	 *   just refresh the object list, no need to resave tlk or bin files
	 */
	public static final int REFRESH_OBJECT_JUST=2;
	
	public Handler mObjRefreshHandler = new Handler(){
		@Override
		public void  handleMessage (Message msg)
		{
			
			switch (msg.what) {
			
			case REFRESH_OBJECT_CHANGED:	
			
				mNameAdapter.clear();
				for(BaseObject o:mObjs)
				{
					if(o instanceof MessageObject)
						mNameAdapter.add(o.getContent());
					else if(o instanceof TextObject)
						mNameAdapter.add(mContext.getString(R.string.object_text));
					else if(o instanceof CounterObject)
						mNameAdapter.add(mContext.getString(R.string.object_counter));
					else if(o instanceof BarcodeObject)
						mNameAdapter.add(mContext.getString(R.string.object_bar));
					else if(o instanceof GraphicObject)
						mNameAdapter.add(mContext.getString(R.string.object_pic));
					else if(o instanceof JulianDayObject)
						mNameAdapter.add(mContext.getString(R.string.object_julian));
					else if(o instanceof RealtimeObject)
						mNameAdapter.add(mContext.getString(R.string.object_realtime));
					else if(o instanceof LineObject)
						mNameAdapter.add(mContext.getString(R.string.object_line));
					else if(o instanceof RectObject)
						mNameAdapter.add(mContext.getString(R.string.object_rect));
					else if(o instanceof EllipseObject)
						mNameAdapter.add(mContext.getString(R.string.object_ellipse));
					else if(o instanceof ShiftObject)
						mNameAdapter.add(mContext.getString(R.string.object_shift));
					else if(o instanceof RTSecondObject)
						mNameAdapter.add(mContext.getString(R.string.object_second));
					else
						System.out.println("Unknown Object type");
				}
				//mNameAdapter.notifyDataSetChanged();
				//mObjList.setAdapter(mNameAdapter);
				mObjList.invalidate();
				//selfInfoEnable(obj);
				OnPropertyChanged(true);
				break;
			case REFRESH_OBJECT_PROPERTIES:
				OnPropertyChanged(true);
			case REFRESH_OBJECT_JUST:
				mNameAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
			BaseObject obj = getCurObj();
			Debug.d(TAG, "=====obj:"+obj.mId);
			mObjView.invalidate();
			if(obj != null){
				makeObjToCenter((int)obj.getX());
			}
			Debug.d(TAG, "=========");
		}
	};
	
	private void makeObjToCenter(int x)
	{
		Debug.d(TAG, "current scrollX="+mHScroll.getScrollX());
		if(x - mHScroll.getScrollX() > 500)
		{
			mHScroll.scrollTo(x-300, 0);
		}
	}
	
	public static BaseObject getCurObj()
	{
		for(BaseObject obj : mObjs)
		{
			if(obj.getSelected())
				return obj;
		}
		return null;
	}
	
	public static void clearCurObj()
	{
		for(BaseObject obj : mObjs)
		{
			obj.setSelected(false);
		}
	}
	public static void setCurObj(int i)
	{
		if(i >= mObjs.size())
			return;
		BaseObject obj=mObjs.get(i);
		obj.setSelected(true);
	}
	
	public static float getNextXcor()
	{
		float x=0;
		for(BaseObject obj : mObjs)
		{
			if(obj instanceof MessageObject)
				continue;
			x = obj.getXEnd()>x ? obj.getXEnd() : x;
		}
		return x;
	}
	/*
	
	
	*/
	public void saveObjFile(String path)
	{
		int i=1;
		FileWriter fw=null;
		BufferedWriter bw=null;
		File file = new File(path);
		if(file.exists())
		{
			Toast.makeText(mContext, "file already exist", Toast.LENGTH_LONG);
			return;
		}
		if(!file.mkdirs())
		{
			Debug.d(TAG, "create dir error "+file.getPath());
			return;
		}
		File tlk = new File(path+"/1.TLK");
		try {
			if(!tlk.createNewFile())
			{
				Debug.d(TAG, "create error "+tlk.getPath());
				return;
			}
			fw = new FileWriter(tlk);
			bw = new BufferedWriter(fw);
			for(BaseObject o: mObjs)
			{
				if(o instanceof RealtimeObject)
				{
					System.out.println("******"+BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.write(BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.newLine();
					o.setIndex(i++);
					for(BaseObject so : ((RealtimeObject) o).getSubObjs())
					{
						System.out.println("******"+BaseObject.intToFormatString(i, 3)+"^"+so.toString());
						bw.write(BaseObject.intToFormatString(i, 3)+"^"+so.toString());
						bw.newLine();
						so.setIndex(i++);
					}
				}
				else
				{
					Debug.d(TAG, "filestr="+BaseObject.intToFormatString(i, 3) +"^"+o.toString());
					bw.write(BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.newLine();
					o.setIndex(i++);
				}
				
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * HANDLER_MESSAGE_OPEN
	 *  Handler message for open tlk file
	 */
	public static final int HANDLER_MESSAGE_OPEN=0;
	/**
	 * HANDLER_MESSAGE_SAVE
	 *   Handler message for save event happens
	 */
	public static final int HANDLER_MESSAGE_SAVE=1;
	/**
	 * HANDLER_MESSAGE_SAVEAS
	 *   Handler message for saveas event happens
	 */
	public static final int HANDLER_MESSAGE_SAVEAS=2;
	/**
	 * HANDLER_MESSAGE_IMAGESELECT
	 *   Handler message for image object selected
	 */
	public static final int HANDLER_MESSAGE_IMAGESELECT=3;
	/**
	 * HANDLER_MESSAGE_DISMISSDIALOG
	 *   Handler message for dismiss loading dialog
	 */
	public static final int HANDLER_MESSAGE_DISMISSDIALOG=4;
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			//	String f;
            switch (msg.what) {   
            	case HANDLER_MESSAGE_OPEN:		//open
            		Debug.d(TAG, "open file="+FileBrowserDialog.file());
            		mObjName = FileBrowserDialog.file();
            		if(mObjName != null && new File(mObjName).isFile())
            		{
	    				Fileparser.parse(mContext, FileBrowserDialog.file(), mObjs);
	    				clearCurObj();
	    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
            		}
            		break;
            	case HANDLER_MESSAGE_SAVE:		//saveas
            		Debug.d(TAG, "save as file="+FileBrowserDialog.file()+"/"+FileBrowserDialog.getObjName());
            		mObjName = FileBrowserDialog.file()+"/"+FileBrowserDialog.getObjName();
            	case HANDLER_MESSAGE_SAVEAS:    //save
            		progressDialog();
            		if(mObjName != null)
            		{
            			saveObjFile(mObjName);
            		}
            		drawAllBmp(mObjName);
            		dismissProgressDialog();
            		OnPropertyChanged(false);
            		break;
            	case HANDLER_MESSAGE_IMAGESELECT:		//select image
            		File file = new File(FileBrowserDialog.file());
            		if(!file.exists() || !GraphicObject.isPic(FileBrowserDialog.file()))
            		{
            			Debug.d(TAG, "Image file error");
            			return;
            		}
            		clearCurObj();
            		GraphicObject o = new GraphicObject(mContext, getNextXcor());
            		o.setImage(FileBrowserDialog.file());
    				mObjs.add(o);    	
    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
            		break;
            	case HANDLER_MESSAGE_DISMISSDIALOG:
            		mProgressDialog.dismiss();
            		break;
            }   
            super.handleMessage(msg);   
       } 
	};
	
	
	public final int LEFT_KEY=1;
	public final int RIGHT_KEY=2;
	public final int UP_KEY=3;
	public final int DOWN_KEY=4;
	public final int ZOOMX_IN_KEY=5;
	public final int ZOOMX_OUT_KEY=6;
	public final int ZOOMY_IN_KEY=7;
	public final int ZOOMY_OUT_KEY=8;
	
	Handler mKeyRepeatHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case LEFT_KEY:
					Debug.d(TAG, "left key pressed");
					leftKeyPressed();
					break;
				case RIGHT_KEY:
					Debug.d(TAG, "right key pressed");
					rightKeyPressed();
					break;
				case UP_KEY:
					Debug.d(TAG, "up key pressed");
					upKeyPressed();
					break;
				case	DOWN_KEY:
					Debug.d(TAG, "down key pressed");
					downKeyPressed();
					break;
				case ZOOMX_IN_KEY:
					Debug.d(TAG, "zoom x  in key pressed");
					zoomInXKeyPressed();
					break;
				case ZOOMX_OUT_KEY:
					Debug.d(TAG, "zoom x out key pressed");
					zoomOutXKeyPressed();
					break;
				case ZOOMY_IN_KEY:
					Debug.d(TAG, "zoom y in key pressed");
					zoomInYKeyPressed();
					break;
				case ZOOMY_OUT_KEY:
					Debug.d(TAG, "zoom y out key pressed");
					zoomOutYKeyPressed();
					break;
				default:
					Debug.d(TAG, "unknow key repeat ");
					break;
			}
			mKeyRepeatHandler.sendEmptyMessageDelayed(msg.what, 200);
		}
	};
	int getTouchedObj(float x, float y)
	{
		int i=1;
		BaseObject o;
		for(i=1; mObjs!= null &&i< mObjs.size(); i++)
		{
			o = mObjs.get(i);
			if(x>= o.getX() && x<= o.getXEnd() && y >=o.getY() && y <= o.getYEnd())
			{
				Debug.d(TAG, "Touched obj = "+i);
				return i;
			}
		}
		Debug.d(TAG, "no object Touched");
		return -1;
	}
	
	public void drawAllBmp(String f)
	{
		int width=0;
		Paint p=new Paint();
		if(mObjs==null || mObjs.size() <= 0)
			return ;
		for(BaseObject o:mObjs)
		{
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gFixedRows, Bitmap.Config.ARGB_8888);
		Debug.d(TAG, "drawAllBmp width="+width+", height="+880);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);
		for(BaseObject o:mObjs)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				o.drawVarBitmap(f);
			}
			else if(o instanceof RealtimeObject)
			{
				Bitmap t = ((RealtimeObject)o).getBgBitmap(mContext,f);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinCreater.recyleBitmap(t);
			}
			else if(o instanceof JulianDayObject)
			{
				o.drawVarBitmap(f);
			}
			else if(o instanceof ShiftObject)
			{
				o.drawVarBitmap(f);
			}
			else
			{
				Bitmap t = o.getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinCreater.recyleBitmap(t);
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		//BinCreater.saveBitmap(bmp, "back.png");
		Debug.d(TAG,"******background png width="+bmp.getWidth()+"height="+bmp.getHeight());
		BinCreater.create(bmp, 0);
		BinCreater.saveBin(f+"/1.bin", width, Configs.gDots);
		return ;
	}
	
	public ProgressDialog mProgressDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		mProgressDialog = ProgressDialog.show(EditTabActivity.this, "", getResources().getString(R.string.strSaving), true,false);
		mProgressShowing = true;
		
		mProgressThread = new Thread(){
			
			@Override
			public void run(){
				
				try{
					for(;mProgressShowing==true;)
					{
						Thread.sleep(2000);
					}
					mHandler.sendEmptyMessage(HANDLER_MESSAGE_DISMISSDIALOG);
				}catch(Exception e)
				{
					
				}
			}
		};
		mProgressThread.start();
	}
	
	public void dismissProgressDialog()
	{
		mProgressShowing=false;
		//Thread thread = mProgressThread;
		//thread.interrupt();
		
	}
	
	private void leftKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setX(obj.getX() - 4);
		}
		else
			obj.setX(obj.getX() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void rightKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setX(obj.getX() + 4);
		}
		else
			obj.setX(obj.getX() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void upKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setY(obj.getY() - 4);
		}
		else
			obj.setY(obj.getY() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void downKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setY(obj.getY() + 4);
		}
		else
			obj.setY(obj.getY() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomOutXKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setWidth(obj.getWidth() + 4);
		}
		else
			obj.setWidth(obj.getWidth() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomInXKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setWidth(obj.getWidth() - 4);
		}
		else
			obj.setWidth(obj.getWidth() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
	}
	
	private void zoomOutYKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setHeight(obj.getHeight() + 4);
		}
		else
			obj.setHeight(obj.getHeight() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomInYKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setHeight(obj.getHeight() - 4);
		}
		else
			obj.setHeight(obj.getHeight() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	public boolean mPropertyChanged=false;
	private void OnPropertyChanged(boolean state)
	{
		mPropertyChanged=state;
	}
	
	public boolean isPropertyChanged()
	{
		return mPropertyChanged;
	}
}
