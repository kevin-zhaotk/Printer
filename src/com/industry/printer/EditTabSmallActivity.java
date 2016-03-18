package com.industry.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import com.industry.printer.ui.CustomerDialog.CustomerDialogBase.OnPositiveListener;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase;
import com.industry.printer.ui.CustomerDialog.MessageBrowserDialog;
import com.industry.printer.ui.CustomerDialog.MessageSaveDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInsertDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog.OnPositiveBtnListener;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.PWMAudio;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.EllipseObject;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LineObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.RTSecondObject;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RectObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TextObject;

import android.app.Activity;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditTabSmallActivity extends Fragment implements OnClickListener, OnTouchListener {
	public static final String TAG="EditTabSmallActivity";
	
	public Context mContext;
	public EditScrollView mObjView;
	public HorizontalScrollView mHScroll;
	
	public String mObjName;
	public MessageTask mMsgTask;
	/*************************
	 * file operation buttons
	 * ***********************/
	public RelativeLayout mBtnNew;
	public RelativeLayout mBtnSave;
	public RelativeLayout mBtnSaveas;
	public RelativeLayout mBtnOpen;
	
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
	
	public ArrayAdapter<String> mNameAdapter;
	
	public EditTabSmallActivity() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.edit_small_frame, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);

		mContext = getActivity();
		
		mMsgTask = new MessageTask(mContext);
		mMsgTask.addObject(new MessageObject(mContext, 0));  //
		
		mBtnNew = (RelativeLayout) getView().findViewById(R.id.btn_new);
		mBtnNew.setOnClickListener(this);
		mBtnNew.setOnTouchListener(this);
		
		mBtnSave = (RelativeLayout) getView().findViewById(R.id.btn_save);
		mBtnSave.setOnClickListener(this);
		mBtnSave.setOnTouchListener(this);
		
		mBtnSaveas = (RelativeLayout) getView().findViewById(R.id.btn_insert);
		mBtnSaveas.setOnClickListener(this);
		mBtnSaveas.setOnTouchListener(this);
		
		mBtnOpen = (RelativeLayout) getView().findViewById(R.id.btn_open);
		mBtnOpen.setOnClickListener(this);
		mBtnOpen.setOnTouchListener(this);
		
		mHScroll = (HorizontalScrollView) getView().findViewById(R.id.scrollView1);
		mObjView = (EditScrollView) getView().findViewById(R.id.editView);
		mObjView.setOnTouchListener(this);
		mObjView.setTask(mMsgTask);
		mObjList = (Spinner) getView().findViewById(R.id.object_list);
		mNameAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);//R.layout.object_list_item);
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

		mShowInfo = (Button) getView().findViewById(R.id.btn_objinfo);
		mShowInfo.setOnClickListener(this);
		mShowInfo.setOnTouchListener(this);
		
		mDel = (ImageButton) getView().findViewById(R.id.btn_delete);
		mDel.setOnClickListener(this);
		mDel.setOnTouchListener(this);
		// mTrans = (ImageButton) getView().findViewById(R.id.btn_trans);
				
		
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
			Debug.d(TAG, "====== 44444");
			ArrayList<BaseObject> objects = mMsgTask.getObjects();
			switch (msg.what) {
			
			case REFRESH_OBJECT_CHANGED:	
				Debug.d(TAG, "======1");
				mNameAdapter.clear();
				for(BaseObject o:objects)
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
				Debug.d(TAG, "======2");
				mObjList.invalidate();
				Debug.d(TAG, "======3");
				OnPropertyChanged(true);
				Bundle bundle = msg.getData();
				int position = bundle.getInt("selection");
				mObjList.setSelection(position);
				break;
			case REFRESH_OBJECT_PROPERTIES:
				OnPropertyChanged(true);
			case REFRESH_OBJECT_JUST:
				mNameAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

			Debug.d(TAG, "=====get curobj");
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
	
	public BaseObject getCurObj()
	{
		Debug.d(TAG, "--->getcurobj");
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		for(BaseObject obj : objects)
		{
			if(obj.getSelected())
				return obj;
		}
		if(objects!=null&& objects.size()>0)
		{
			BaseObject object=objects.get(0);
			object.setSelected(true);
			return object;
		}
		return null;
	}
	
	public void clearCurObj()
	{
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		for(BaseObject obj : objects)
		{
			obj.setSelected(false);
		}
	}
	public void setCurObj(int i)
	{
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		if(i >= objects.size())
			return;
		Debug.d(TAG, "--->setCurObj: " + objects.size() + "   i=" + i);
		BaseObject obj=objects.get(i);
		if (obj instanceof MessageObject) {
			return;
		}
		obj.setSelected(true);
	}
	
	public float getNextXcor()
	{
		float x=0;
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		
		for(BaseObject obj : objects)
		{
			if(obj instanceof MessageObject)
				continue;
			x = obj.getXEnd()>x ? obj.getXEnd() : x;
		}
		return x;
	}

	/**
	 * HANDLER_MESSAGE_OPEN
	 *  Handler message for open tlk file
	 */
	public static final int HANDLER_MESSAGE_OPEN=0;
	/**
	 * HANDLER_MESSAGE_SAVEAS
	 *   Handler message for save event happens
	 */
	public static final int HANDLER_MESSAGE_SAVEAS=1;
	/**
	 * HANDLER_MESSAGE_SAVE
	 *   Handler message for saveas event happens
	 */
	public static final int HANDLER_MESSAGE_SAVE=2;
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
	
	/**
	 * HANDLER_MESSAGE_INSERT_MSG
	 * Handler message for insert a new object
	 */
	public static final int HANDLER_MESSAGE_INSERT_OBJECT = 6;
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			//	String f;
			boolean createfile=false;
            switch (msg.what) {   
            	case HANDLER_MESSAGE_OPEN:		//open
            		Debug.d(TAG, "open file="+ MessageBrowserDialog.getSelected());
            		mObjName = MessageBrowserDialog.getSelected();
            		if (mObjName == null || mObjName.isEmpty()) {
						break;
					}
            		mMsgTask = new MessageTask(mContext, mObjName);
            		clearCurObj();
            		mObjView.setTask(mMsgTask);
	    			mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
            		break;
            	case HANDLER_MESSAGE_SAVEAS:		//saveas
            		Debug.d(TAG, "save as file="+MessageSaveDialog.getTitle());
            		mObjName = MessageSaveDialog.getTitle();
            		createfile=true;
            	case HANDLER_MESSAGE_SAVE:    //save
            		progressDialog();
            		if (mObjName == null || mMsgTask == null) {
						break;
					}
            		// mMsgTask = new MessageTask(mContext, mObjName);
            		mMsgTask.setName(mObjName);
            		mMsgTask.createTaskFolderIfNeed();
            		
            		mMsgTask.save();
           			
            		dismissProgressDialog();
            		OnPropertyChanged(false);
            		break;
            	case HANDLER_MESSAGE_IMAGESELECT:		//select image
            		
            		break;
            	case HANDLER_MESSAGE_DISMISSDIALOG:
            		mProgressDialog.dismiss();
            		break;
            	case HANDLER_MESSAGE_INSERT_OBJECT:
            		Bundle bundle = msg.getData();
            		if (bundle == null) {
						break;
					}
            		String type = bundle.getString(ObjectInsertDialog.OBJECT_TYPE);
            		String format = bundle.getString(ObjectInsertDialog.OBJECT_FORMAT);
            		if (BaseObject.OBJECT_TYPE_TEXT.equals(type)) {
						onInsertObject(new TextObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_CNT.equals(type)) {
						onInsertObject(new CounterObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_RT.equals(type)) {
						onInsertObject(new RealtimeObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_JULIAN.equals(type)) {
						onInsertObject(new JulianDayObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_RECT.equals(type)) {
						onInsertObject(new RectObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_LINE.equals(type)) {
						onInsertObject(new LineObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_ELLIPSE.equals(type)) {
						onInsertObject(new EllipseObject(mContext, getNextXcor()));
					} else if (BaseObject.OBJECT_TYPE_BARCODE.equals(type)) {
						onInsertObject(new BarcodeObject(mContext, getNextXcor()));
					} 
            		break;
            }   
            super.handleMessage(msg);   
       } 
	};
	
	int getTouchedObj(float x, float y)
	{
		int i=1;
		BaseObject o;
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		for(i=1; objects!= null &&i< objects.size(); i++)
		{
			o = objects.get(i);
			if(x>= o.getX() && x<= o.getXEnd() && y >=o.getY() && y <= o.getYEnd())
			{
				Debug.d(TAG, "Touched obj = "+i);
				return i;
			}
		}
		Debug.d(TAG, "no object Touched");
		return -1;
	}
	
	public ProgressDialog mProgressDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		mProgressDialog = ProgressDialog.show(mContext, "", getView().getResources().getString(R.string.strSaving), true,false);
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
	
	public boolean mPropertyChanged=false;

	private void OnPropertyChanged(boolean state)
	{
		mPropertyChanged=state;
	}
	
	public boolean isPropertyChanged()
	{
		return mPropertyChanged;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_new:
				onNew();
				break;
			case R.id.btn_save:
				onSave();
				break;
			case R.id.btn_insert:
				onInsert();
				break;
			case R.id.btn_open:
				onOpen();
				break;
			case R.id.btn_objinfo:
				onShowInfo();
				break;
			case R.id.btn_delete:
				onDelete();
				break;
			default:
				break;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.editView:
			onObjectTouch(event);
			break;
		default:
			break;
		}
		if (v.getId() == R.id.scrollView1) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			PWMAudio.Play();
		}
		return false;
	}
	
	private void onNew() {
		mObjName = null;
		mMsgTask.removeAll();
		mMsgTask.addObject(new MessageObject(mContext, 0));
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
		mHScroll.scrollTo(0, 0);
	}
	
	private void onSave() {
		if(!isPropertyChanged())
			return;
		if(mObjName != null)
		{
			mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVE);
			return;
		}
		onSaveAs();
	}
	
	private void onSaveAs() {
		CustomerDialogBase dialog;
		dialog = new MessageSaveDialog(mContext);
		dialog.setOnPositiveClickedListener(new OnPositiveListener() {
			
			@Override
			public void onClick() {
				mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVEAS);
			}

			@Override
			public void onClick(String content) {
				
			}
		});
		dialog.show();
	}

	
	private void onInsert() {
		ObjectInsertDialog dialog1 = new ObjectInsertDialog(getActivity());
		dialog1.show();
		Message msg = mHandler.obtainMessage(HANDLER_MESSAGE_INSERT_OBJECT);
		dialog1.setDismissMessage(msg);
	}
	
	private void onOpen() {
		MessageBrowserDialog dialog = new MessageBrowserDialog(mContext);
		dialog.setOnPositiveClickedListener(new OnPositiveListener() {
			
			@Override
			public void onClick() {
				mHandler.sendEmptyMessage(HANDLER_MESSAGE_OPEN);
			}

			@Override
			public void onClick(String content) {
			}
		});
		dialog.show();
	}
	
	private void onShowInfo() {
		ObjectInfoDialog objDialog = new ObjectInfoDialog(mContext, getCurObj());
		objDialog.setObject(getCurObj());
		objDialog.setOnPositiveBtnListener(new OnPositiveBtnListener(){
			@Override
			public void onClick() {
				Debug.d(TAG, "===>onShowinfo  clicked");
				Message msg = mObjRefreshHandler.obtainMessage(REFRESH_OBJECT_JUST);
				msg.sendToTarget();
			}
		});
		objDialog.show();
	}
	
	private void onDelete() {
		// TODO Auto-generated method stub
		BaseObject obj = getCurObj();
		if(obj == null || obj instanceof MessageObject)
			return;
		mMsgTask.removeObject(obj);
		setCurObj(0);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
	}
	
	private void onInsertObject(BaseObject object) {
		mMsgTask.addObject(object);
		Message msg = mObjRefreshHandler.obtainMessage(REFRESH_OBJECT_CHANGED);
		Bundle bundle = new Bundle();
		bundle.putInt("selection", mMsgTask.getObjects().size() - 1);
		msg.setData(bundle);
		mObjRefreshHandler.sendMessage(msg);
		clearCurObj();
		
	}
	
	private boolean onObjectTouch(MotionEvent event) {
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
	
}
