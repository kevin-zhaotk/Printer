package com.industry.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.DotMatrixReader;
import com.industry.printer.data.InternalCodeCalculater;
import com.industry.printer.data.RFIDData;
import com.industry.printer.hardware.HardwareJni;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.TLKFileParser;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TextObject;
import com.industry.printer.ui.ExtendMessageTitleFragment;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase.OnPositiveListener;
import com.industry.printer.ui.CustomerDialog.FileBrowserDialog;
import com.industry.printer.ui.CustomerDialog.MessageBrowserDialog;
import com.industry.printer.ui.CustomerDialog.MessageSaveDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInsertDialog;
import com.industry.printer.ui.CustomerDialog.TextBrowserDialog;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditTabActivity extends Fragment implements OnClickListener, OnLongClickListener {
	public static final String TAG="EditTabActivity";
	
	public Context mContext;
	public EditScrollView mObjView;
	public HorizontalScrollView mHScroll;
	
	public String mObjName;
	
	public ExtendMessageTitleFragment mMsgTitle;
	/*************************
	 * file operation buttons
	 * ***********************/
	public RelativeLayout mBtnNew;
	public RelativeLayout mBtnSave;
	public RelativeLayout mBtnSaveas;
	public RelativeLayout mBtnOpen;
	public RelativeLayout mPrev;
	public RelativeLayout mNext;
	
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
	 * 鏍戣帗娲�3-缂栬緫鐣岄潰鐩稿叧鎸夐挳
	 ***********************/
	public Button	mInsert;
	public Button	mTest;
	public Button 	mTest5;
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
	
	/***********************
	 * object edit lines for smfy-super3
	 **********************/
	public ScrollView mScrollView1;
	public EditText mObjLine1;
	// public EditText mObjLine2;
	// public EditText mObjLine3;
	// public EditText mObjLine4;
	
	
	
	public static Vector<BaseObject> mObjs;
	public ArrayAdapter<String> mNameAdapter;
	
	public EditTabActivity() {
//		mMsgTitle = (ExtendMessageTitleFragment)fragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.edit_frame, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.edit_frame);
		//this.setVisible(false);
		mContext = getActivity();
		
		mObjs = new Vector<BaseObject>();
		mObjs.add(new MessageObject(mContext, 0));
		mHScroll = (HorizontalScrollView) getView().findViewById(R.id.scrollView1);
		
		mBtnNew = (RelativeLayout) getView().findViewById(R.id.btn_new);
		mBtnNew.setOnClickListener(this);
		
		mBtnOpen = (RelativeLayout) getView().findViewById(R.id.btn_open);
		mBtnOpen.setOnClickListener(this);
		
		mBtnSaveas = (RelativeLayout) getView().findViewById(R.id.btn_saveas);
		mBtnSaveas.setOnClickListener(this);
		
		mBtnSave = (RelativeLayout) getView().findViewById(R.id.btn_save);
		mBtnSave.setOnClickListener(this);
		
		//mTest = (Button) getView().findViewById(R.id.btn_temp_4);
		//mTest.setOnClickListener(this);
		
		//mTest5 = (Button) getView().findViewById(R.id.btn_temp_5);
		//mTest5.setOnClickListener(this);
		
		mPrev = (RelativeLayout) getView().findViewById(R.id.btn_page_up);
		mPrev.setOnClickListener(this);
		
		mNext = (RelativeLayout) getView().findViewById(R.id.btn_page_down);
		mNext.setOnClickListener(this);
		
		mScrollView1 = (ScrollView) getView().findViewById(R.id.edit_scrollview_line1);
		
		mObjLine1 = (EditText) getView().findViewById(R.id.edit_line1);
		mObjLine1.setText("");
		mObjLine1.setOnLongClickListener(this);
		mObjLine1.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg2 == null) {
					return false;
				}
				if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					return true;
				} else {
					return false;
				}
			}
		});
		
	
	}
	
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		Debug.d(TAG, "event:"+event.toString());
//		InputMethodManager manager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
//		Debug.d(TAG, "ime is active? "+manager.isActive());
//		manager.hideSoftInputFromWindow(EditTabActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
////			manager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//		return true;
//	}
	
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
				String content = "";
				String obj = "";
				Debug.d(TAG, "===========refresh object============");
				for (BaseObject object : mObjs) {
					if(object instanceof MessageObject) {
						continue;
					} else if (object instanceof TextObject) {
						obj = object.getContent();
					} else if (object instanceof RealtimeObject) {
						obj = "#T#" + ((RealtimeObject)object).getContent();
					} else {
						continue;
					}
					
					if (content.isEmpty()) {
						content += obj;
					} else {
						content += ObjectsFromString.SPLITOR + obj;
					}
					
				}
				Debug.d(TAG, "===========refresh object============");
				mObjLine1.setText(content);
				break;
			case REFRESH_OBJECT_PROPERTIES:
				OnPropertyChanged(true);
			case REFRESH_OBJECT_JUST:
				//mNameAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};
	
	
	public static BaseObject getCurObj()
	{
	
		for(BaseObject obj : mObjs)
		{
			if(obj.getSelected())
				return obj;
		}
		if(mObjs!=null&& mObjs.size()>0)
		{
			BaseObject object=mObjs.get(0);
			object.setSelected(true);
			return object;
		}
		return null;
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


	/**
	 * 鍝嶅簲save/saveas鎿嶄綔锛屼繚瀛榯lk瀵硅薄
	 * @param path 鏂囦欢璺緞锛堟瘡鏉′俊鎭殑鐩綍锛�
	 * @param create 鏄惁闇�瑕佸垱寤轰竴涓俊鎭洰褰�
	 */
	public void saveObjFile(String path, boolean create)
	{
		int i=1;
		FileWriter fw=null;
		BufferedWriter bw=null;
		File file = new File(path);
		Debug.d(TAG, "=====>saveObjFile path="+path);
		if(create==true)
		{
//			if(file.exists()){
//				Toast.makeText(mContext, R.string.str_tlk_already_exist, Toast.LENGTH_LONG);
//				return;
//			}
//			if(!file.mkdirs())
//			{
//				Debug.d(TAG, "create dir error "+file.getPath());
//				Toast.makeText(mContext, R.string.str_createfile_failure, Toast.LENGTH_LONG);
//				return;
//			}
		}
		if(!file.exists() && !file.mkdirs())
		{
			Debug.d(TAG, "create dir error "+file.getPath());
			return ;
		}
		File tlk = new File(path+"/1.TLK");
		try {
			if(!tlk.exists() && !tlk.createNewFile()){
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
			e.printStackTrace();
		}
		
	}
	
	/**
	 * HANDLER_MESSAGE_NEW
	 *  Handler message for new a tlk file
	 */
	public static final int HANDLER_MESSAGE_NEW=0;
	
	/**
	 * HANDLER_MESSAGE_OPEN
	 *  Handler message for open tlk file
	 */
	public static final int HANDLER_MESSAGE_OPEN=1;
	
	/**
	 * HANDLER_MESSAGE_SAVE
	 *   Handler message for save event happens
	 */
	public static final int HANDLER_MESSAGE_SAVE=2;
	
	/**
	 * HANDLER_MESSAGE_SAVEAS
	 *   Handler message for saveas event happens
	 */
	public static final int HANDLER_MESSAGE_SAVEAS=3;
	
	/**
	 * HANDLER_MESSAGE_IMAGESELECT
	 *   Handler message for image object selected
	 */
	public static final int HANDLER_MESSAGE_IMAGESELECT=4;

	/**
	 * HANDLER_MESSAGE_DISMISSDIALOG
	 *   Handler message for dismiss loading dialog
	 */
	public static final int HANDLER_MESSAGE_DISMISSDIALOG=5;
	
	/**
	 * HANDLER_MESSAGE_INSERT_MSG
	 * Handler message for insert a new object
	 */
	public static final int HANDLER_MESSAGE_INSERT_OBJECT = 6;
	
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			//	String f;
			String title = getResources().getString(R.string.str_file_title);;
			boolean createfile=false;
            switch (msg.what) {

        		case HANDLER_MESSAGE_NEW:
        			mObjName = null;
        			mObjLine1.setText("");
        			((MainActivity) getActivity()).mEditTitle.setText(getResources().getString(R.string.str_filename_no));
        			break;
            	case HANDLER_MESSAGE_OPEN:		//open
            		Debug.d(TAG, "open file="+MessageBrowserDialog.getSelected());
            		mObjName = MessageBrowserDialog.getSelected();
            		File tlk=new File(ConfigPath.getTlkPath()+"/" + mObjName +"/1.TLK");
            		if(tlk.isFile() && tlk.exists())
            		{
            			TLKFileParser parser = new TLKFileParser(mContext, tlk.getAbsolutePath());
	    				parser.parse(mContext, tlk.getAbsolutePath(), mObjs);
	    				setCurObj(0);
	    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
            		}
            		
            		((MainActivity) getActivity()).mEditTitle.setText(title + mObjName);
            		//mMsgTitle.setTitle(mObjName);
            		break;
            		
            	case HANDLER_MESSAGE_SAVEAS:		//saveas
            		Debug.d(TAG, "save as file="+MessageSaveDialog.getTitle());
            		mObjName = MessageSaveDialog.getTitle();
            		createfile=true;
            		
            	case HANDLER_MESSAGE_SAVE:    //save
            		progressDialog();
            		if (mObjName == null) {
						break;
					}
            		createFolderIfNeed(mObjName);
            		if (Configs.gMakeBinFromBitmap == true) {
            			saveObjectBin(ConfigPath.getTlkPath()+"/"+mObjName);
					} else {
						saveBinDotMatrix(ConfigPath.getTlkPath()+"/"+mObjName);
					}
            		
           			saveObjFile(ConfigPath.getTlkPath()+"/"+mObjName, createfile);
            		
           			
            		dismissProgressDialog();
            		// OnPropertyChanged(false);
            		((MainActivity) getActivity()).mEditTitle.setText(title + mObjName);
            		// mMsgTitle.setTitle(mObjName);
            		break;
            		
            	case HANDLER_MESSAGE_IMAGESELECT:		//select image
            		File file = new File(FileBrowserDialog.file());
            		if(!file.exists() || !GraphicObject.isPic(FileBrowserDialog.file()))
            		{
            			Debug.d(TAG, "Image file error");
            			return;
            		}
            		setCurObj(0);
            		GraphicObject o = new GraphicObject(mContext, getNextXcor());
            		o.setImage(FileBrowserDialog.file());
    				mObjs.add(o);    	
    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
            		break;
            		
            	case HANDLER_MESSAGE_DISMISSDIALOG:
            		mProgressDialog.dismiss();
            		break;
            	case HANDLER_MESSAGE_INSERT_OBJECT:
            		Bundle bundle = msg.getData();
            		if (bundle == null) {
						break;
					}
            		String text = bundle.getString("object", null);
            		if (text != null) {
            			String content = mObjLine1.getText().toString();
            			if (content.isEmpty() || content.endsWith(ObjectsFromString.SPLITOR)) {
            				mObjLine1.append(text);
						} else {
							mObjLine1.append(ObjectsFromString.SPLITOR + text);
						}
            			
					}
            		
            		break;
            }   
            super.handleMessage(msg);   
       } 
	};
	
	private boolean createFolderIfNeed(String obj) {
		File dir = new File(ConfigPath.getTlkPath() + "/" + obj);
		if(!dir.exists() && !dir.mkdirs())
		{
			Debug.d(TAG, "create dir error "+dir.getPath());
			return false;
		}
		return true;
	}
	
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
	
	/**
	 * 使用系统字库，生成bitmap，然后通过灰度化和二值化之后提取点阵生成buffer
	 * @param f
	 */
	public void saveObjectBin(String f)
	{
		int width=0;
		Paint p=new Paint();
		if(mObjs==null || mObjs.size() <= 0)
			return ;
		for(BaseObject o:mObjs)
		{
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDotsTotal, Bitmap.Config.ARGB_8888);
		Debug.d(TAG, "drawAllBmp width="+width+", height="+Configs.gDotsTotal);
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
		BinCreater.saveBin(f+"/1.bin", width, bmp.getHeight());
		return ;
	}
	
	/**
	 * 从16*16的点阵字库中提取点阵，生成打印buffer
	 * @param f
	 */
	public void saveBinDotMatrix(String f) {
		if(mObjs==null || mObjs.size() <= 0)
			return ;
		
		String content="";
		for(BaseObject o:mObjs)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				content += o.getContent();
			}
			else if(o instanceof RealtimeObject)
			{
				content += o.getContent();
				o.generateVarBuffer();
				Debug.d(TAG, "--->realtime: " + content);
			}
			else if(o instanceof JulianDayObject)
			{
				content += o.getContent();
			}
			else if(o instanceof ShiftObject)
			{
				content += o.getContent();
			}
			else
			{
				content += o.getContent();
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		InternalCodeCalculater cal = InternalCodeCalculater.getInstance();
		char[] code = cal.getGBKCode(content);
		DotMatrixReader reader = DotMatrixReader.getInstance(mContext);
		byte[] dots = reader.getDotMatrix(code);
		BinCreater.saveBin(f, dots, 32);
		for(BaseObject o:mObjs)
		{
			if((o instanceof MessageObject)	) {
				((MessageObject) o).setDotCount(reader.getDotCount(dots));
				break;
			}
		}
		
		return ;
	}
	
	public ProgressDialog mProgressDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		mProgressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.strSaving), true,false);
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
	public void onClick(View arg0) {
		CustomerDialogBase dialog;
		List<BaseObject> objs = null;
		byte[] d;
		int fd;
		int ret;
		RFIDDevice device = null;
		switch (arg0.getId()) {
			case R.id.btn_new:
				mHandler.sendEmptyMessage(HANDLER_MESSAGE_NEW);
				break;
			case R.id.btn_open:	//test fpga-gpio write
				
				dialog = new MessageBrowserDialog(mContext);
				dialog.setOnPositiveClickedListener(new OnPositiveListener() {
					
					@Override
					public void onClick() {
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_OPEN);
					}

					@Override
					public void onClick(String content) {
						// TODO Auto-generated method stub
						
					}
				});
				dialog.show();
				
				break;
			case R.id.btn_save:
				if (mObjLine1.getText().toString().isEmpty()) {
					break;
				}
				getObjectList();
				if (mObjName != null) {
					mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVE);
				} else {
					dialog = new MessageSaveDialog(mContext);
					dialog.setOnPositiveClickedListener(new OnPositiveListener() {
						
						@Override
						public void onClick() {
							mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVEAS);
						}

						@Override
						public void onClick(String content) {
							// TODO Auto-generated method stub
							
						}
					});
					dialog.show();
				}
				break;
			case R.id.btn_saveas:
				/*
				if (mObjLine1.getText().toString().isEmpty()) {
					break;
				}
				getObjectList();
				dialog = new MessageSaveDialog(mContext);
				dialog.setOnPositiveClickedListener(new OnPositiveListener() {
					
					@Override
					public void onClick() {
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVEAS);
					}

					@Override
					public void onClick(String content) {
						// TODO Auto-generated method stub
						
					}
				});
				dialog.show();
				*/
				ObjectInsertDialog dialog1 = new ObjectInsertDialog(getActivity());
				dialog1.show();
				Message msg = mHandler.obtainMessage();
				msg.what = HANDLER_MESSAGE_INSERT_OBJECT;
				dialog1.setDismissMessage(msg);
				break;
			/*
			case R.id.btn_temp_4:
				//制卡
				device = RFIDDevice.getInstance();
				device.makeCard();
				break;
			case R.id.btn_temp_5:
				
				//writer.connect();
				//writer.setType();
				//寻卡
				device = RFIDDevice.getInstance();
				device.init();
				break;
			*/
			case R.id.btn_page_up:
				float y = mObjLine1.getTranslationY();
				Debug.d(TAG, "===>y="+y);
				//mObjLine1.scrollBy(0, -100);
				mScrollView1.smoothScrollBy(0, -50);
				break;
			case R.id.btn_page_down:

				float ny = mObjLine1.getY();
				Debug.d(TAG, "===>y="+ny);
				ny = mObjLine1.getScaleY();
				Debug.d(TAG, "===>sy="+ny);
				mScrollView1.smoothScrollBy(0, 50);
				//mObjLine1.scrollBy(0, 100);
				break;
			
			default:
				break;
		}
	}
	
	private void getObjectList() {
		List<BaseObject> objs=null;
		objs = ObjectsFromString.makeObjs(mContext, mObjLine1.getText().toString());
		mObjs.clear();
		if(objs == null) {
			Toast.makeText(mContext, R.string.str_notice_no_objects, Toast.LENGTH_LONG);
			return ;
		}
		mObjs.addAll(objs);
		return ;
	}
	@Override
	public boolean onLongClick(View arg0) {
		TextBrowserDialog dialog = new TextBrowserDialog(mContext, "txt");
		dialog.setOnPositiveClickedListener(new OnPositiveListener() {
			
			@Override
			public void onClick() {
				
			}
			@Override
			public void onClick(String content) {
				Debug.d(TAG, "--->content: " +content);
				mObjLine1.setText(content);
			}
		});
		dialog.show();
		return false;
	}
	
}
